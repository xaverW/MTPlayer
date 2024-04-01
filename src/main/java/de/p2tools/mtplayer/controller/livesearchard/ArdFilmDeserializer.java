package de.p2tools.mtplayer.controller.livesearchard;


import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.livesearch.JsonInfoDto;
import de.p2tools.mtplayer.controller.livesearch.tools.JsonFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveConst;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.UrlUtils;
import de.p2tools.mtplayer.controller.livesearchzdf.ZdfDatenFilm;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.tools.log.P2Log;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class ArdFilmDeserializer {

    private static final String GERMAN_TIME_ZONE = "Europe/Berlin";

    private static final String ELEMENT_EMBEDDED = "embedded";
    private static final String ELEMENT_MEDIA_COLLECTION = "mediaCollection";
    private static final String ELEMENT_PUBLICATION_SERVICE = "publicationService";
    private static final String ELEMENT_SHOW = "show";
    private static final String ELEMENT_TEASERS = "teasers";
    private static final String ELEMENT_WIDGETS = "widgets";

    private static final String ATTRIBUTE_BROADCAST = "broadcastedOn";
    private static final String ATTRIBUTE_DURATION = "_duration";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_PARTNER = "partner";
    private static final String ATTRIBUTE_SYNOPSIS = "synopsis";
    private static final String ATTRIBUTE_TITLE = "title";

    private static final DateTimeFormatter DATE_FORMAT
            = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT
            = DateTimeFormatter.ofPattern("HH:mm:ss");

    // the key of the map is the value of publicationService.channelType in film.json
    private static final Map<String, String> ADDITIONAL_SENDER = new HashMap<>();

    static {
        ADDITIONAL_SENDER.put("rbb", Const.RBB);
        ADDITIONAL_SENDER.put("swr", Const.SWR);
        ADDITIONAL_SENDER.put("mdr", Const.MDR);
        ADDITIONAL_SENDER.put("ndr", Const.NDR);
        ADDITIONAL_SENDER.put("wdr", Const.WDR);
        ADDITIONAL_SENDER.put("hr", Const.HR);
        ADDITIONAL_SENDER.put("br", Const.BR);
        ADDITIONAL_SENDER.put("radio_bremen", "rbtv");
    }

    private final ArdVideoInfoJsonDeserializer videoDeserializer;

    public ArdFilmDeserializer() {
        videoDeserializer = new ArdVideoInfoJsonDeserializer();
    }

    public void deserialize(JsonInfoDto jsonInfoDto, JsonNode jsonElement) {
        if (!jsonElement.has(ELEMENT_WIDGETS)
                || !jsonElement.get(ELEMENT_WIDGETS).isArray()) {
            return;
        }

        Iterator<JsonNode> widgets = jsonElement.get(ELEMENT_WIDGETS).elements();
        if (!widgets.hasNext()) {
            return;
        }

        final JsonNode itemObject = widgets.next();

        final Optional<String> topic = parseTopic(itemObject);
        final Optional<String> title = parseTitle(itemObject);
        final Optional<String> description;
        if (itemObject.has(ATTRIBUTE_SYNOPSIS)) {
            description = Optional.of(itemObject.get(ATTRIBUTE_SYNOPSIS).asText());
        } else {
            description = Optional.empty();
        }
        final Optional<LocalDateTime> date = parseDate(itemObject);
        final Optional<Duration> duration = parseDuration(itemObject);
        final Optional<ArdVideoInfoDto> videoInfo = parseVideoUrls(itemObject);
        final Optional<String> partner = parsePartner(itemObject);

        if (topic.isPresent()
                && title.isPresent()
                && videoInfo.isPresent()
                && videoInfo.get().getVideoUrls().size() > 0) {
            // add film to ARD
            final ArdFilmDto filmDto
                    = new ArdFilmDto(
                    createFilm(
                            Const.ARD,
                            topic.get(),
                            title.get(),
                            description,
                            date,
                            duration,
                            videoInfo.get()));

            if (widgets.hasNext()) {
                parseRelatedFilms(filmDto, widgets.next());
            }

            addFilmToList(jsonInfoDto, filmDto);


            if (partner.isPresent() && ADDITIONAL_SENDER.containsKey(partner.get())) {
                // add film to other sender (like RBB)
                FilmDataMTP additionalFilm
                        = createFilm(
                        ADDITIONAL_SENDER.get(partner.get()),
                        topic.get(),
                        title.get(),
                        description,
                        date,
                        duration,
                        videoInfo.get());
                addFilmToList(jsonInfoDto, new ArdFilmDto(additionalFilm));
            }
        }
    }

    private void addFilmToList(JsonInfoDto jsonInfoDto, ArdFilmDto filmDataMTP) {
        filmDataMTP.getFilm().arr[FilmDataXml.FILM_WEBSITE] = String.format(ArdConstants.WEBSITE_URL, jsonInfoDto.getArdFilmId());
        LiveFactory.setFilmSize(filmDataMTP.getFilm());
        filmDataMTP.getFilm().init();
        jsonInfoDto.getList().add(filmDataMTP.getFilm());
    }

    private static Optional<JsonNode> getMediaCollectionObject(final JsonNode itemObject) {
        if (itemObject.has(ELEMENT_MEDIA_COLLECTION)
                && !itemObject.get(ELEMENT_MEDIA_COLLECTION).isEmpty()
                && itemObject.get(ELEMENT_MEDIA_COLLECTION).has(ELEMENT_EMBEDDED)
                && !itemObject.get(ELEMENT_MEDIA_COLLECTION).get(ELEMENT_EMBEDDED).isEmpty()) {

            return Optional.of(itemObject.get(ELEMENT_MEDIA_COLLECTION)
                    .get(ELEMENT_EMBEDDED));
        }

        return Optional.empty();
    }

    private static Optional<String> parseTopic(final JsonNode playerPageObject) {
        Optional<String> topic;
        if (playerPageObject.has(ELEMENT_SHOW) && !playerPageObject.get(ELEMENT_SHOW).isEmpty()) {
            final JsonNode showObject = playerPageObject.get(ELEMENT_SHOW);
            if (showObject.has(ATTRIBUTE_TITLE)) {
                topic = Optional.of(showObject.get(ATTRIBUTE_TITLE).asText());
            } else {
                topic = Optional.empty();
            }
        } else {
            // no show element found -> use title as topic
            if (playerPageObject.has(ATTRIBUTE_TITLE)) {
                topic = Optional.of(playerPageObject.get(ATTRIBUTE_TITLE).asText());
            } else {
                topic = Optional.empty();
            }
        }

        if (topic.isPresent()) {
            // remove time in topic
            if (topic.get().contains("MDR aktuell")) {
                return Optional.of(topic.get().replaceAll("[0-9][0-9]:[0-9][0-9] Uhr$", "").trim());
            }
        }

        return topic;
    }

    private Optional<String> parseTitle(final JsonNode playerPageObject) {
        if (playerPageObject.has(ATTRIBUTE_TITLE)) {
            Optional<String> title = Optional.of(playerPageObject.get(ATTRIBUTE_TITLE).asText());
            if (title.isPresent()) {
                return Optional.of(title.get().replace("Hörfassung", "Audiodeskription"));
            }

            return title;
        }

        return Optional.empty();
    }

    private static Optional<LocalDateTime> parseDate(final JsonNode playerPageObject) {
        if (!playerPageObject.has(ATTRIBUTE_BROADCAST)) {
            return Optional.empty();
        }

        final Optional<String> dateValue = Optional.of(playerPageObject.get(ATTRIBUTE_BROADCAST).asText());
        if (dateValue.isPresent()) {
            try {
                final ZonedDateTime inputDateTime = ZonedDateTime.parse(dateValue.get());
                final LocalDateTime localDateTime
                        = inputDateTime.withZoneSameInstant(ZoneId.of(GERMAN_TIME_ZONE)).toLocalDateTime();
                return Optional.of(localDateTime);
            } catch (final DateTimeParseException ex) {
                P2Log.errorLog(201214587, ex, "Error parsing date time value " + dateValue.get());
            }
        }

        return Optional.empty();
    }

    private static Optional<Duration> parseDuration(final JsonNode playerPageObject) {
        final Optional<JsonNode> mediaCollectionObject = getMediaCollectionObject(playerPageObject);
        if (mediaCollectionObject.isPresent() && mediaCollectionObject.get().has(ATTRIBUTE_DURATION)) {
            final long durationValue = mediaCollectionObject.get().get(ATTRIBUTE_DURATION).asLong();
            return Optional.of(Duration.ofSeconds(durationValue));
        }

        return Optional.empty();
    }

    private Optional<String> parsePartner(JsonNode playerPageObject) {
        if (playerPageObject.has(ELEMENT_PUBLICATION_SERVICE)) {
            JsonNode publicationServiceObject
                    = playerPageObject.get(ELEMENT_PUBLICATION_SERVICE);

            Optional<String> partnerAttribute = JsonFactory.getOptStringElement(publicationServiceObject, ATTRIBUTE_PARTNER);
            if (partnerAttribute.isPresent()) {
                return partnerAttribute;
            }

            Optional<String> nameAttribute = JsonFactory.getOptStringElement(publicationServiceObject, ATTRIBUTE_NAME);
            if (nameAttribute.isPresent()) {
                return Optional.of(nameAttribute.get());
            }
        }

        return Optional.empty();
    }

    private static String prepareSubtitleUrl(final String url) {
        return UrlUtils.addDomainIfMissing(url, ArdConstants.BASE_URL_SUBTITLES);
    }

    private void parseRelatedFilms(final ArdFilmDto filmDto, final JsonNode playerPageObject) {
        if (playerPageObject.has(ELEMENT_TEASERS)) {
            final JsonNode teasersElement = playerPageObject.get(ELEMENT_TEASERS);

            Iterator<JsonNode> it = teasersElement.elements();
            while (it.hasNext()) {
                JsonNode teasersItemElement = it.next();
                final Optional<String> id = JsonFactory.getOptStringElement(teasersItemElement, ATTRIBUTE_ID);
                if (id.isPresent()) {
                    final String url = ArdConstants.ITEM_URL + id.get();
                    filmDto.addRelatedFilm(new ArdFilmInfoDto(id.get(), url, 0));
                }
            }
        }
    }

    private FilmDataMTP createFilm(
            final String sender,
            final String topic,
            final String title,
            final Optional<String> description,
            final Optional<LocalDateTime> date,
            final Optional<Duration> duration,
            final ArdVideoInfoDto videoInfo) {

        LocalDateTime time = date.orElse(LocalDateTime.now());

        String dateValue = time.format(DATE_FORMAT);
        String timeValue = time.format(TIME_FORMAT);

        Map<LiveConst.Qualities, String> videoUrls = videoInfo.getVideoUrls();

        FilmDataMTP film = new ZdfDatenFilm(sender, topic, "", title, videoInfo.getDefaultVideoUrl(),
                dateValue, timeValue, duration.orElse(Duration.ZERO).getSeconds(), description.orElse(""));


        if (videoUrls.containsKey(LiveConst.Qualities.SMALL)) {
            LiveFactory.addUrlKlein(film, videoUrls.get(LiveConst.Qualities.SMALL));
        }
        if (videoUrls.containsKey(LiveConst.Qualities.HD)) {
            LiveFactory.addUrlHd(film, videoUrls.get(LiveConst.Qualities.HD));
        }
        if (videoInfo.getSubtitleUrlOptional().isPresent()) {
            LiveFactory.addUrlSubtitle(film, videoInfo.getSubtitleUrl());
        }

        return film;
    }

    private Optional<ArdVideoInfoDto> parseVideoUrls(final JsonNode playerPageObject) {
        final Optional<JsonNode> mediaCollectionObject = getMediaCollectionObject(playerPageObject);
        if (mediaCollectionObject.isPresent()) {
            final ArdVideoInfoDto videoDto
                    = videoDeserializer.deserialize(mediaCollectionObject.get()/*, null, null*/);
            return Optional.of(videoDto);
        }

        return Optional.empty();
    }
}
