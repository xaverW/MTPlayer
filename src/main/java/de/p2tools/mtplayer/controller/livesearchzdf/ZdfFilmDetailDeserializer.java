package de.p2tools.mtplayer.controller.livesearchzdf;

import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.livesearch.JsonInfoDto;
import de.p2tools.mtplayer.controller.livesearch.tools.JsonFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.JsonUtils;
import de.p2tools.mtplayer.controller.livesearch.tools.UrlUtils;
import de.p2tools.p2lib.tools.log.P2Log;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class ZdfFilmDetailDeserializer {

    private static final String GERMAN_TIME_ZONE = "Europe/Berlin";

    private static final String JSON_ELEMENT_BEGIN = "airtimeBegin";
    private static final String JSON_ELEMENT_BRAND = "http://zdf.de/rels/brand";
    private static final String JSON_ELEMENT_CATEGORY = "http://zdf.de/rels/category";
    private static final String JSON_ELEMENT_BROADCAST = "http://zdf.de/rels/cmdm/broadcasts";
    private static final String JSON_ELEMENT_DURATION = "duration";
    private static final String JSON_ELEMENT_EDITORIAL_DATE = "editorialDate";
    private static final String JSON_ELEMENT_LEAD_PARAGRAPH = "leadParagraph";
    private static final String JSON_ELEMENT_MAIN_VIDEO = "mainVideoContent";
    private static final String JSON_ELEMENT_PROGRAM_ITEM = "programmeItem";
    private static final String JSON_ELEMENT_SHARING_URL = "http://zdf.de/rels/sharing-url";
    private static final String JSON_ELEMENT_STREAMS = "streams";
    private static final String JSON_ELEMENT_SUBTITLE = "subtitle";
    private static final String JSON_ELEMENT_TARGET = "http://zdf.de/rels/target";
    private static final String JSON_ELEMENT_TITLE = "title";
    private static final String JSON_ELEMENT_TEASER_TEXT = "teasertext";
    private static final String JSON_ATTRIBUTE_TEMPLATE = "http://zdf.de/rels/streams/ptmd-template";
    private static final String EPISODENUMBER = "episodeNumber";
    private static final String[] SEASONNUMBER = {"http://zdf.de/rels/cmdm/season", "seasonNumber"};

    private static final String PLACEHOLDER_PLAYER_ID = "{playerId}";
    private static final String PLAYER_ID = "android_native_5";

    private static final String DOWNLOAD_URL_DEFAULT = "default";
    private static final String DOWNLOAD_URL_DGS = "dgs";

    private static final String[] KNOWN_STREAMS =
            new String[]{DOWNLOAD_URL_DEFAULT, DOWNLOAD_URL_DGS};

    private static final DateTimeFormatter DATE_FORMATTER_EDITORIAL
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"); // 2016-10-29T16:15:00.000+02:00
    private static final DateTimeFormatter DATE_FORMATTER_AIRTIME
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"); // 2016-10-29T16:15:00+02:00

    private final String apiUrlBase = "https://api.zdf.de";
    private final JsonInfoDto jsonInfoDto;

    public ZdfFilmDetailDeserializer(JsonInfoDto jsonInfoDto) {
        this.jsonInfoDto = jsonInfoDto;
    }

    public void deserialize(String getUrl) {
        try {
            Optional<JsonNode> optRootNode = JsonFactory.getRootNode(getUrl, "Bearer " + jsonInfoDto.getApi());
            if (optRootNode.isEmpty()) {
                return;
            }

            JsonNode rootNode = optRootNode.get();
            JsonNode programItemTarget = null;
            JsonNode mainVideoTarget = null;

            if (rootNode.get(JSON_ELEMENT_PROGRAM_ITEM) != null) {

                Iterator<JsonNode> children = rootNode.get(JSON_ELEMENT_PROGRAM_ITEM).elements();
                if (!children.hasNext()) {
                    P2Log.debugLog("getVideoUrl: error");
                    return;
                }
                JsonNode jn = children.next();
                if (rootNode.get(JSON_ELEMENT_TARGET) != null) {
                    programItemTarget = jn.get(JSON_ELEMENT_TARGET);
                }
            }


            if (rootNode.get(JSON_ELEMENT_MAIN_VIDEO) != null) {
                JsonNode mainVideoElement = rootNode.get(JSON_ELEMENT_MAIN_VIDEO);
                if (mainVideoElement.get(JSON_ELEMENT_TARGET) != null) {
                    mainVideoTarget = mainVideoElement.get(JSON_ELEMENT_TARGET);
                }
            }

            Optional<String> title = parseTitle(rootNode, programItemTarget);
            Optional<String> topic = parseTopic(rootNode);
            Optional<String> description = parseDescription(rootNode);

            Optional<String> website = parseWebsiteUrl(rootNode);
            Optional<LocalDateTime> time = parseAirtime(rootNode, programItemTarget);
            Optional<Duration> duration = parseDuration(mainVideoTarget);

            final Map<String, String> downloadUrl = parseDownloadUrls(mainVideoTarget);

            if (title.isPresent() && downloadUrl.containsKey(DOWNLOAD_URL_DEFAULT)) {
                ZdfFilmDto zdfFilmDto = new ZdfFilmDto(downloadUrl.get(DOWNLOAD_URL_DEFAULT), topic,
                        title.get(), description, website, time, duration, downloadUrl.get(DOWNLOAD_URL_DGS));

                Optional<DownloadDto> downloadDtoOptional = new ZdfDownloadDtoDeserializer().deserialize(jsonInfoDto, downloadUrl.get(DOWNLOAD_URL_DEFAULT));
                downloadDtoOptional.ifPresent(downloadDto -> new ZdfFilmDetailTask().processRestTarget(jsonInfoDto, zdfFilmDto, downloadDto));

            } else {
                P2Log.errorLog(652365478, "ZdfFilmDetailDeserializer: no title or url found");
            }

        } catch (final Exception ex) {
            P2Log.errorLog(959454120, ex, "Url: " + getUrl);
        }
    }

    private Map<String, String> parseDownloadUrls(final JsonNode mainVideoContent) {
        // key: type of download url, value: the download url
        final Map<String, String> result = new HashMap<>();

        if (mainVideoContent != null) {
            for (String knownStream : KNOWN_STREAMS) {
                if (JsonUtils.checkTreePath(
                        mainVideoContent, JSON_ELEMENT_STREAMS, knownStream, JSON_ATTRIBUTE_TEMPLATE)) {

                    final Optional<String> url =
                            JsonUtils.getAttributeAsString(
                                    mainVideoContent
                                            .get(JSON_ELEMENT_STREAMS)
                                            .get(knownStream),
                                    JSON_ATTRIBUTE_TEMPLATE);
                    if (url.isPresent()) {
                        result.put(knownStream, finalizeDownloadUrl(url.get()));
                    }
                }
            }

            if (!result.containsKey(DOWNLOAD_URL_DEFAULT)) {
                Optional<String> urlOptional =
                        JsonUtils.getAttributeAsString(mainVideoContent, JSON_ATTRIBUTE_TEMPLATE);
                if (urlOptional.isPresent()) {
                    result.put(DOWNLOAD_URL_DEFAULT, finalizeDownloadUrl(urlOptional.get()));
                }
            }
        }
        return result;
    }

    private String finalizeDownloadUrl(final String url) {
        return UrlUtils.addDomainIfMissing(url, apiUrlBase).replace(PLACEHOLDER_PLAYER_ID, PLAYER_ID);
    }

    private Optional<LocalDateTime> parseAirtime(
            JsonNode aRootNode, JsonNode aProgramItemTarget) {
        Optional<String> date;
        DateTimeFormatter formatter;

        // use broadcast airtime if found
        if (aProgramItemTarget != null) {
            Iterator<JsonNode> it = aProgramItemTarget.iterator();

            if (!it.hasNext()) {
                date = getEditorialDate(aRootNode);
                formatter = DATE_FORMATTER_EDITORIAL;
            } else {
                // array is ordered ascending though the oldest broadcast is the first entry
                date = Optional.of(
                        it.next().get(JSON_ELEMENT_BEGIN).asText());
                formatter = DATE_FORMATTER_AIRTIME;
            }
            return date.map(s -> LocalDateTime.parse(s, formatter));
        } else {
            // use editorialdate
            date = getEditorialDate(aRootNode);
            if (date.isPresent()) {
                final ZonedDateTime inputDateTime = ZonedDateTime.parse(date.get());
                final LocalDateTime localDateTime
                        = inputDateTime.withZoneSameInstant(ZoneId.of(GERMAN_TIME_ZONE)).toLocalDateTime();
                return Optional.of(localDateTime);
            }
        }

        return Optional.empty();
    }

    private Optional<String> getEditorialDate(JsonNode aRootNode) {
        if (aRootNode.has(JSON_ELEMENT_EDITORIAL_DATE)) {
            return Optional.of(aRootNode.get(JSON_ELEMENT_EDITORIAL_DATE).asText());
        }

        return Optional.empty();
    }

    private Optional<String> parseWebsiteUrl(JsonNode aRootNode) {
        if (aRootNode.has(JSON_ELEMENT_SHARING_URL)) {
            return Optional.of(aRootNode.get(JSON_ELEMENT_SHARING_URL).asText());
        }

        return Optional.empty();
    }

    private Optional<Duration> parseDuration(JsonNode mainVideoTarget) {
        if (mainVideoTarget != null) {
            JsonNode duration = mainVideoTarget.get(JSON_ELEMENT_DURATION);
            if (duration != null) {
                return Optional.of(Duration.ofSeconds(duration.asInt()));
            }
        }

        return Optional.empty();
    }

    private Optional<String> parseDescription(JsonNode aRootNode) {
        JsonNode leadParagraph = aRootNode.get(JSON_ELEMENT_LEAD_PARAGRAPH);
        if (leadParagraph != null) {
            return Optional.of(leadParagraph.asText());
        } else {
            JsonNode teaserText = aRootNode.get(JSON_ELEMENT_TEASER_TEXT);
            if (teaserText != null) {
                return Optional.of(teaserText.asText());
            }
        }

        return Optional.empty();
    }

    private Optional<String> parseTitle(final JsonNode aRootNode, final JsonNode aTarget) {
        final Optional<String> programmTitle = JsonUtils.getElementValueAsString(aRootNode, JSON_ELEMENT_TITLE);
        final Optional<String> programmSubtitle = JsonUtils.getElementValueAsString(aRootNode, JSON_ELEMENT_SUBTITLE);
        Optional<String> resultingTitle = formatTitle(programmTitle, programmSubtitle);
        if (resultingTitle.isEmpty()) {
            final Optional<String> targetTitle = JsonUtils.getElementValueAsString(aTarget, JSON_ELEMENT_TITLE);
            final Optional<String> targetSubtitle = JsonUtils.getElementValueAsString(aTarget, JSON_ELEMENT_SUBTITLE);
            resultingTitle = formatTitle(targetTitle, targetSubtitle);
        }
        if (resultingTitle.isPresent()) {
            final Optional<Integer> season = JsonUtils.getElementValueAsInteger(aTarget, SEASONNUMBER);
            final Optional<Integer> episode = JsonUtils.getElementValueAsInteger(aTarget, EPISODENUMBER);
            final Optional<String> seasonEpisodeTitle = formatEpisodeTitle(season, episode);
            final Optional<String> title = cleanupTitle((resultingTitle.get() + " " + seasonEpisodeTitle.orElse("")).trim());
            return title;
        }
        return Optional.empty();
    }

    private Optional<String> cleanupTitle(String title) {
        return Optional.of(title.replaceAll("\\(CC.*\\) - .* Creative Commons.*", ""));
    }

    private Optional<String> formatTitle(Optional<String> title, Optional<String> sub) {
        if (title.isEmpty()) {
            return Optional.empty();
        }
        if (sub.isPresent() && !sub.get().isBlank()) {
            return Optional.of(title.get().trim() + " - " + sub.get().trim());
        } else {
            return Optional.of(title.get().trim());
        }
    }

    private Optional<String> formatEpisodeTitle(Optional<Integer> season, Optional<Integer> episode) {
        if (season.isEmpty() && episode.isEmpty()) {
            return Optional.empty();
        }
        String result = "";
        if (season.isPresent()) {
            result += String.format("S%02d", season.get());
        }
        if (season.isPresent() && episode.isPresent()) {
            result += "/";
        }
        if (episode.isPresent()) {
            result += String.format("E%02d", episode.get());
        }
        return Optional.of("(" + result + ")");
    }

    private Optional<String> parseTopic(JsonNode aRootNode) {
        JsonNode brand = aRootNode.get(JSON_ELEMENT_BRAND);
        JsonNode category = aRootNode.get(JSON_ELEMENT_CATEGORY);

        if (brand != null) {
            // first use brand
            JsonNode topic = brand.get(JSON_ELEMENT_TITLE);
            if (topic != null) {
                return Optional.of(topic.asText());
            }
        }

        if (category != null) {
            // second use category
            JsonNode topic = category.get(JSON_ELEMENT_TITLE);
            if (topic != null) {
                return Optional.of(topic.asText());
            }
        }

        return Optional.empty();
    }
}
