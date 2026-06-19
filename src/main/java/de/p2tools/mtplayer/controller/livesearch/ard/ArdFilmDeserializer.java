/*
angepasste Version aus:
https://github.com/mediathekview/MLib
*/

package de.p2tools.mtplayer.controller.livesearch.ard;


import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.livesearch.tools.*;
import de.p2tools.mtplayer.controller.livesearch.zdf.ZdfDatenFilm;
import de.p2tools.p2lib.mediathek.filmdata.FilmDataXml;
import de.p2tools.p2lib.tools.log.P2Log;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ArdFilmDeserializer {

    private static final String GERMAN_TIME_ZONE = "Europe/Berlin";

    private static final String ELEMENT_EMBEDDED = "embedded";
    private static final String ELEMENT_MEDIA_COLLECTION = "mediaCollection";
    private static final String ELEMENT_PUBLICATION_SERVICE = "publicationService";
    private static final String ELEMENT_SHOW = "show";
    private static final String ELEMENT_TEASERS = "teasers";
    private static final String ELEMENT_WIDGETS = "widgets";
    private static final String[] ELEMENT_SUBTITLES = {ELEMENT_MEDIA_COLLECTION, ELEMENT_EMBEDDED, "subtitles"};
    private static final String ELEMENT_SOURCES = "sources";
    private static final String ELEMENT_STREAMS = "streams";
    private static final String ELEMENT_MEDIA = "media";
    private static final String ELEMENT_AUDIO = "audios";


    private static final String ATTRIBUTE_BROADCAST = "broadcastedOn";
    private static final String[] ATTRIBUTE_DURATION = {"meta", "duration"};
    private static final String[] ATTRIBUTE_DURATION_SEC = {"meta", "durationSeconds"};
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_PARTNER = "partner";
    private static final String ATTRIBUTE_SYNOPSIS = "synopsis";
    private static final String ATTRIBUTE_TITLE = "title";
    private static final String ATTRIBUTE_URL = "url";
    private static final String ATTRIBUTE_RESOLUTION_H = "maxHResolutionPx";
    private static final String ATTRIBUTE_MIME = "mimeType";
    private static final String ATTRIBUTE_KIND = "kind";
    private static final String ATTRIBUTE_ADUIO_LANG = "languageCode";
    private static final String ATTRIBUTE_GEO_BLOCKED = "isGeoBlocked";

    private static final String MARKER_VIDEO_MP4 = "video/mp4";
    private static final String MARKER_VIDEO_STANDARD = "standard";
    private static final String MARKER_VIDEO_CATEGORY_MAIN = "main";
    private static final String MARKER_VIDEO_CATEGORY_MPEG = "application/vnd.apple.mpegurl";
    private static final String MARKER_VIDEO_AD = "audio-description";
    private static final String MARKER_VIDEO_DGS = "sign-language";
    private static final String MARKER_VIDEO_OV = "OV";
    private static final String MARKER_VIDEO_DE = "deu";

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
        ADDITIONAL_SENDER.put("radio_bremen", Const.RBTV);
        ADDITIONAL_SENDER.put("tagesschau24", Const.TAGESSCHAU24);
        ADDITIONAL_SENDER.put("das_erste", Const.ARD);
        ADDITIONAL_SENDER.put("one", Const.ONE); // ONE
        ADDITIONAL_SENDER.put("ard-alpha", Const.ARD_ALPHA); // ARD-alpha
        ADDITIONAL_SENDER.put("funk", "Funk.net"); // Funk.net
        ADDITIONAL_SENDER.put("sr", Const.SR);
        ADDITIONAL_SENDER.put("phoenix", Const.PHOENIX);
        ADDITIONAL_SENDER.put("ard", Const.ARD);
        //IGNORED_SENDER "zdf", "kika", "3sat", "arte"
    }

    private final ArdVideoInfoJsonDeserializer videoDeserializer;

    public ArdFilmDeserializer() {
        videoDeserializer = new ArdVideoInfoJsonDeserializer();
    }

    public void deserialize(ArdDto ardDto, JsonNode jsonElement) {
        List<ArdFilmDto> films = new ArrayList<>();
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
        Optional<String> subtitles = prepareSubtitleUrl(itemObject);
        final Optional<Boolean> geoBlocked = parseGeoBlocked(itemObject);

        final Optional<LocalDateTime> date = parseDate(itemObject);
        final Optional<Duration> duration = parseDuration(itemObject);
        Optional<Map<LiveConst.Qualities, String>> videoInfoStandard =
                parseVideoUrls(itemObject, MARKER_VIDEO_CATEGORY_MAIN, MARKER_VIDEO_STANDARD, MARKER_VIDEO_MP4, MARKER_VIDEO_DE);

        final Optional<String> partner = parsePartner(itemObject);

        if (topic.isPresent()
                && title.isPresent()
                && videoInfoStandard.isPresent()
                && !videoInfoStandard.get().isEmpty()) {
            // add film standard
            final ArdFilmDto filmDto
                    = new ArdFilmDto(
                    createFilm(
                            partner.isPresent() && ADDITIONAL_SENDER.get(partner.get()) != null ?
                                    ADDITIONAL_SENDER.get(partner.get()) : "",
                            topic.get(),
                            title.get(),
                            description,
                            date,
                            duration,
                            videoInfoStandard.get(),
                            subtitles,
                            geoBlocked.orElse(false)));
            films.add(filmDto);
            addFilmToList(ardDto, filmDto);
        }
    }

    private void addFilmToList(ArdDto ardDto, ArdFilmDto filmDataMTP) {
        filmDataMTP.getFilm().arr[FilmDataXml.FILM_WEBSITE] = String.format(ArdConstants.WEBSITE_URL, ardDto.getPageId());
        LiveFactory.setFilmSize(filmDataMTP.getFilm());
        filmDataMTP.getFilm().init();
        ardDto.getFilmList().add(filmDataMTP.getFilm());
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

        if (mediaCollectionObject.isPresent()) {
            Optional<JsonNode> durNode = JsonUtils.getElement(mediaCollectionObject.get(), "meta", "durationSeconds");
            if (durNode.isPresent()) {
                final long durationValue = durNode.get().asLong();
                return Optional.of(Duration.ofSeconds(durationValue));
            }
        }
//
//         mediaCollectionObject.get().has(ATTRIBUTE_DURATION[0])) {
//            final long durationValue = mediaCollectionObject.get().get(ATTRIBUTE_DURATION[0]).asLong();
//            return Optional.of(Duration.ofSeconds(durationValue));
//
//        } else if (mediaCollectionObject.isPresent() && mediaCollectionObject.get().has(ATTRIBUTE_DURATION[1])) {
//            final long durationValue = mediaCollectionObject.get().get(ATTRIBUTE_DURATION[1]).asLong();
//            return Optional.of(Duration.ofSeconds(durationValue));
//        }

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
                return nameAttribute;
            }
        }

        return Optional.empty();
    }

    private Optional<String> prepareSubtitleUrl(final JsonNode embeddedElement) {
        Optional<JsonNode> subtitle = JsonUtils.getElement(embeddedElement, ELEMENT_SUBTITLES);
        if (subtitle.isEmpty() || !subtitle.get().isArray() || (subtitle.get().isEmpty()))
            return Optional.empty();

        Optional<JsonNode> sources = JsonUtils.getElement(subtitle.get().get(0), ELEMENT_SOURCES);
        if (sources.isEmpty() || !sources.get().isArray())
            return Optional.empty();
        Set<String> urls = new HashSet<>();
        Iterator<JsonNode> it = sources.get().elements();
        while (it.hasNext()) {
            JsonNode url = it.next();
            JsonUtils.getElementValueAsString(url, ATTRIBUTE_URL).ifPresent(urls::add);
        }
        return urls.stream()
                .filter(s -> !s.endsWith(".vtt"))
                .findFirst();
    }

    private Optional<Boolean> parseGeoBlocked(final JsonNode playerPageObject) {
        // todo
//        final Optional<JsonNode> mediaCollectionObject = getMediaCollectionObject(playerPageObject);
//        if (mediaCollectionObject.isEmpty()) {
//            return Optional.empty();
//        }
//        final Optional<JsonNode> geoBlockedElement =
//                JsonUtils.getElement(mediaCollectionObject.get(), ATTRIBUTE_GEO_BLOCKED);
//        if (geoBlockedElement.isPresent() && geoBlockedElement.get().isJsonPrimitive()) {
//            return Optional.of(geoBlockedElement.get().getAsBoolean());
//        }
        return Optional.empty();
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
            final Map<LiveConst.Qualities, String> videoUrls,
            final Optional<String> sub,
            final boolean geoBlocking) {

        LocalDateTime time = date.orElse(LocalDateTime.now());

        String dateValue = time.format(DATE_FORMAT);
        String timeValue = time.format(TIME_FORMAT);
        String baseUrl = videoUrls.get(LiveConst.Qualities.NORMAL);
        FilmDataMTP film = new ZdfDatenFilm(sender, topic, "", title, baseUrl,
                dateValue, timeValue, duration.orElse(Duration.ZERO).getSeconds(), description.orElse(""));

        if (videoUrls.containsKey(LiveConst.Qualities.SMALL)) {
            addUrlKlein(film, videoUrls.get(LiveConst.Qualities.SMALL));
        }
        if (videoUrls.containsKey(LiveConst.Qualities.HD)) {
            addUrlHd(film, videoUrls.get(LiveConst.Qualities.HD));
        }
        if (sub.isPresent()) {
            addUrlSubtitle(film, sub.get());
        }
        if (geoBlocking) {
            film.arr[FilmDataMTP.FILM_GEO] = FilmDataMTP.GEO_DE;
        }

        return film;
    }


    private Optional<Map<LiveConst.Qualities, String>>
    parseVideoUrls(final JsonNode playerPageObject, String streamType, String aduioType, String mimeType, String language) {
        Optional<Map<Integer, String>> urls = parseVideoUrlMap(playerPageObject, streamType, aduioType, mimeType, language);
        if (urls.isEmpty()) {
            return Optional.empty();
        }
        Map<LiveConst.Qualities, String> videoInfo = new EnumMap<>(LiveConst.Qualities.class);
        for (Map.Entry<Integer, String> entry : urls.get().entrySet()) {
            LiveConst.Qualities resolution = LiveConst.Qualities.getResolutionFromWidth(entry.getKey());
            if (!videoInfo.containsKey(resolution)) {
                videoInfo.put(resolution, entry.getValue());
            }
        }
        // issue if we do not have normal res
        if (!videoInfo.containsKey(LiveConst.Qualities.NORMAL)) {
            if (videoInfo.containsKey(LiveConst.Qualities.HD)) {
                videoInfo.put(LiveConst.Qualities.NORMAL, videoInfo.get(LiveConst.Qualities.HD));
                videoInfo.remove(LiveConst.Qualities.HD);
            } else {
                videoInfo.put(LiveConst.Qualities.NORMAL, videoInfo.get(LiveConst.Qualities.SMALL));
                videoInfo.remove(LiveConst.Qualities.SMALL);
            }
        }
        return Optional.of(videoInfo);
    }

    private Optional<Map<Integer, String>> parseVideoUrlMap(final JsonNode playerPageObject, String streamType, String aduioType, String mimeType, String language) {
        final Optional<JsonNode> mediaCollectionObject = getMediaCollectionObject(playerPageObject);
        if (mediaCollectionObject.isEmpty())
            return Optional.empty();
        final Optional<JsonNode> streams = JsonUtils.getElement(mediaCollectionObject.get(), ELEMENT_STREAMS);
        if (streams.isEmpty() || !streams.get().isArray() || (streams.get().isEmpty()))
            return Optional.empty();
        //
        Map<Integer, String> videoInfo = new TreeMap<>(Comparator.reverseOrder());
        Iterator<JsonNode> it = streams.stream().iterator();
        while (it.hasNext()) {
            JsonNode streamsCategory = it.next();
            if (!streamsCategory.elements().hasNext()) {
                return Optional.empty();
            }

            final Optional<String> streamKind = JsonUtils.getElementValueAsString(streamsCategory.get(0), ATTRIBUTE_KIND);

//            // todo
//            Optional<JsonNode> media = Optional.empty();
//            Iterator<JsonNode> itS = streamsCategory.elements();
//            while (itS.hasNext()) {
//                JsonNode js = itS.next();
//                if (JsonUtils.hasElements(js, ELEMENT_MEDIA)) {
//                    media = JsonUtils.getElement(js, ELEMENT_MEDIA);
//                }
//            }

            final Optional<JsonNode> media = JsonUtils.getElement(streamsCategory.get(0), ELEMENT_MEDIA);
            if (media.isEmpty() || !media.get().isArray() || (media.get().isEmpty()))
                return Optional.empty();
            if (streamKind.orElse("").equalsIgnoreCase(streamType)) {
                Iterator<JsonNode> itVideo = media.get().iterator();
                while (itVideo.hasNext()) {
                    JsonNode video = itVideo.next();
                    Optional<String> mime = JsonUtils.getElementValueAsString(video, ATTRIBUTE_MIME);
                    if (mime.isPresent() && mime.get().equalsIgnoreCase(mimeType)) {
                        Optional<JsonNode> audios = JsonUtils.getElement(video, ELEMENT_AUDIO);
                        if (audios.isPresent() && audios.get().isArray() && !audios.get().isEmpty()) {
                            Optional<String> kind = JsonUtils.getElementValueAsString(audios.get().get(0), ATTRIBUTE_KIND);
                            Optional<String> resh = JsonUtils.getElementValueAsString(video, ATTRIBUTE_RESOLUTION_H);
                            Optional<String> url = JsonUtils.getElementValueAsString(video, ATTRIBUTE_URL);
                            Optional<String> languageCode = JsonUtils.getElementValueAsString(audios.get().get(0), ATTRIBUTE_ADUIO_LANG);
                            if (url.isPresent() && resh.isPresent() && kind.isPresent() && kind.get().equalsIgnoreCase(aduioType) &&
                                    (languageCode.orElse("").equalsIgnoreCase(language) || (language.equalsIgnoreCase("*") && !languageCode.orElse("").equalsIgnoreCase("deu")))) {
                                videoInfo.put(Integer.parseInt(resh.get()), UrlUtils.removeParameters(url.get()));
                            }
                        }
                    }
                }
            }
        }
        if (videoInfo.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(videoInfo);
    }

    private Optional<ArdVideoInfoDto> parseVideoUrls_(final JsonNode playerPageObject) {
        final Optional<JsonNode> mediaCollectionObject = getMediaCollectionObject(playerPageObject);
        if (mediaCollectionObject.isPresent()) {
            final ArdVideoInfoDto videoDto
                    = videoDeserializer.deserialize(mediaCollectionObject.get()/*, null, null*/);
            return Optional.of(videoDto);
        }

        return Optional.empty();
    }


    public static void addUrlHd(FilmDataMTP film, String url) {
        film.arr[FilmDataMTP.FILM_URL_HD] = url.isEmpty() ? "" : getKlein(film.arr[FilmDataMTP.FILM_URL], url);
    }

    public static void addUrlSubtitle(FilmDataMTP film, String url) {
        film.arr[FilmDataMTP.FILM_URL_SUBTITLE] = url;
    }

    public static void addUrlKlein(FilmDataMTP film, String url) {
        film.arr[FilmDataMTP.FILM_URL_SMALL] = url.isEmpty() ? "" : getKlein(film.arr[FilmDataMTP.FILM_URL], url);
    }

    private static String getKlein(String url1, String url2) {
        String ret = "";
        boolean diff = false;
        for (int i = 0; i < url2.length(); ++i) {
            if (url1.length() > i) {
                if (url1.charAt(i) != url2.charAt(i)) {
                    if (!diff) {
                        ret = i + "|";
                    }
                    diff = true;
                }
            } else {
                diff = true;
            }
            if (diff) {
                ret += url2.charAt(i);
            }
        }
        return ret;
    }


}
