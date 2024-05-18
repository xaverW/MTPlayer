package de.p2tools.mtplayer.controller.livesearchzdf;

import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.livesearch.JsonInfoDto;
import de.p2tools.mtplayer.controller.livesearch.tools.JsonFactory;
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

    public static final String URL_API_BASE = "https://api.zdf.de";
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
    private static final DateTimeFormatter DATE_FORMATTER_AIRTIME
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"); // 2016-10-29T16:15:00+02:00
    private static final DateTimeFormatter DATE_FORMATTER_EDITORIAL
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"); // 2016-10-29T16:15:00.000+02:00
    private static final String GERMAN_TIME_ZONE = "Europe/Berlin";

    private static final String DOWNLOAD_URL_DEFAULT = "default";
    private static final String DOWNLOAD_URL_DGS = "dgs";
    private static final String[] KNOWN_STREAMS = new String[]{DOWNLOAD_URL_DEFAULT, DOWNLOAD_URL_DGS};
    private static final String PLACEHOLDER_PLAYER_ID = "{playerId}";
    private static final String PLAYER_ID = "android_native_5";


    public ZdfFilmDetailDeserializer() {
    }

    public void deserialize(JsonInfoDto jsonInfoDto, String getUrl) {
        try {
            String api = "Bearer " + jsonInfoDto.getApi();
            Optional<JsonNode> optRootNode = JsonFactory.getRootNode(getUrl, api);

            if (optRootNode.isPresent()) {
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

                String title = parseTitle(rootNode, programItemTarget).orElse("");
                Optional<String> topic = parseTopic(rootNode);
                Optional<String> description = parseDescription(rootNode);

                Optional<String> website = parseWebsiteUrl(rootNode);
                Optional<LocalDateTime> time = parseAirtime(rootNode, programItemTarget);
                Optional<Duration> duration = parseDuration(mainVideoTarget);

                Map<String, String> downloadUrl = parseDownloadUrls(mainVideoTarget);

                if (downloadUrl.containsKey(DOWNLOAD_URL_DEFAULT)) {
                    ZdfFilmDto zdfFilmDto = new ZdfFilmDto(downloadUrl.get(DOWNLOAD_URL_DEFAULT),
                            topic, title, description, website, time, duration, downloadUrl.get(DOWNLOAD_URL_DGS));

                    Optional<DownloadDto> downloadDtoOptional = new ZdfDownloadDtoDeserializer().deserialize(jsonInfoDto, downloadUrl.get(DOWNLOAD_URL_DEFAULT));
                    downloadDtoOptional.ifPresent(downloadDto -> new ZdfFilmDetailTask().processRestTarget(jsonInfoDto, zdfFilmDto, downloadDto));
                }
            }
        } catch (final Exception ex) {
            P2Log.errorLog(959454120, ex, "Url: " + getUrl);
        }
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

    private Optional<String> parseTitle(JsonNode aRootNode, JsonNode aTarget) {
        Optional<String> title = parseTitleValue(aRootNode, aTarget);
        return title.map(s -> s.replaceAll("\\(CC.*\\) - .* Creative Commons.*", ""));
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

    private Optional<String> parseWebsiteUrl(JsonNode aRootNode) {
        if (aRootNode.has(JSON_ELEMENT_SHARING_URL)) {
            return Optional.of(aRootNode.get(JSON_ELEMENT_SHARING_URL).asText());
        }

        return Optional.empty();
    }

    private Optional<LocalDateTime> parseAirtime(JsonNode aRootNode, JsonNode aProgramItemTarget) {
        Optional<String> date = Optional.of("");
        DateTimeFormatter formatter;

        // use broadcast airtime if found
        if (aProgramItemTarget != null) {
            JsonNode jn = aProgramItemTarget.get(JSON_ELEMENT_BROADCAST);
            if (jn != null) {
                Iterator<JsonNode> broadcastArray = jn.elements();
                if (broadcastArray == null || (!broadcastArray.hasNext())) {
                    date = getEditorialDate(aRootNode);
                    formatter = DATE_FORMATTER_EDITORIAL;
                } else {
                    // array is ordered ascending though the oldest broadcast is the first entry
                    JsonNode jnS = broadcastArray.next().get(JSON_ELEMENT_BEGIN);
                    if (jnS != null) {
                        date = Optional.of(jnS.asText());
                    }
                    formatter = DATE_FORMATTER_AIRTIME;
                }
                return date.map(s -> LocalDateTime.parse(s, formatter));
            }
            return Optional.empty();

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

    private Optional<Duration> parseDuration(JsonNode mainVideoTarget) {
        if (mainVideoTarget != null) {
            JsonNode duration = mainVideoTarget.get(JSON_ELEMENT_DURATION);
            if (duration != null) {
                return Optional.of(Duration.ofSeconds(duration.asInt()));
            }
        }

        return Optional.empty();
    }

    private Optional<String> getEditorialDate(JsonNode aRootNode) {
        if (aRootNode.get(JSON_ELEMENT_EDITORIAL_DATE) != null) {
            return Optional.of(aRootNode.get(JSON_ELEMENT_EDITORIAL_DATE).asText());
        }

        return Optional.empty();
    }

    private Optional<String> parseTitleValue(JsonNode aRootNode, JsonNode aTarget) {
        // use property "title" if found
        JsonNode titleElement = aRootNode.get(JSON_ELEMENT_TITLE);
        if (titleElement != null) {
            JsonNode subTitleElement = aRootNode.get(JSON_ELEMENT_SUBTITLE);
            if (subTitleElement != null) {
                return Optional.of(titleElement.asText().trim() + " - " + subTitleElement.asText());
            } else {
                return Optional.of(titleElement.asText());
            }
        } else {
            // programmItem target required to determine title
            if (aTarget != null && aTarget.has(JSON_ELEMENT_TITLE)) {
                String title = aTarget.get(JSON_ELEMENT_TITLE).asText();
                String subTitle = aTarget.get(JSON_ELEMENT_SUBTITLE).asText();

                if (subTitle.isEmpty()) {
                    return Optional.of(title);
                } else {
                    return Optional.of(title.trim() + " - " + subTitle);
                }
            }
        }

        return Optional.empty();
    }

    private Map<String, String> parseDownloadUrls(final JsonNode mainVideoContent) {
        // key: type of download url, value: the download url
        final Map<String, String> result = new HashMap<>();

        if (mainVideoContent != null) {
            for (String knownStream : KNOWN_STREAMS) {

                if (mainVideoContent.get(JSON_ELEMENT_STREAMS) != null) {
                    if (mainVideoContent.get(JSON_ELEMENT_STREAMS).get(knownStream) != null) {
                        if (mainVideoContent.get(JSON_ELEMENT_STREAMS).get(knownStream).get(JSON_ATTRIBUTE_TEMPLATE) != null) {
                            final String url = mainVideoContent.get(JSON_ELEMENT_STREAMS).get(knownStream).get(JSON_ATTRIBUTE_TEMPLATE).asText();
                            if (!url.isEmpty()) {
                                result.put(knownStream, finalizeDownloadUrl(url));
                            }
                        }
                    }
                }
            }

            if (!result.containsKey(DOWNLOAD_URL_DEFAULT)) {
                if (mainVideoContent.get(JSON_ATTRIBUTE_TEMPLATE) != null) {
                    String urlOptional = mainVideoContent.get(JSON_ATTRIBUTE_TEMPLATE).asText();
                    if (!urlOptional.isEmpty()) {
                        result.put(DOWNLOAD_URL_DEFAULT, finalizeDownloadUrl(urlOptional));
                    }
                }
            }
        }
        return result;
    }

    private String finalizeDownloadUrl(final String url) {
        return addDomainIfMissing(url, ZdfFilmDetailDeserializer.URL_API_BASE).replace(PLACEHOLDER_PLAYER_ID, PLAYER_ID);
    }

    public String addDomainIfMissing(final String aUrl, final String aDomain) {
        if (aUrl != null && !aUrl.isEmpty() && aUrl.startsWith("/")) {
            return aDomain + aUrl;
        }

        return aUrl;
    }
}
