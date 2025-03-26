package de.p2tools.mtplayer.controller.livesearchzdf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.p2tools.mtplayer.controller.livesearch.JsonInfoDto;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveConst;
import de.p2tools.p2lib.mtdownload.MLHttpClient;
import de.p2tools.p2lib.tools.log.P2Log;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.InputStream;
import java.time.Duration;
import java.util.*;

public class ZdfDownloadDtoDeserializer {

    private static final String ZDF_QUALITY_UHD = "uhd";
    private static final String ZDF_QUALITY_FHD = "fhd";
    private static final String ZDF_QUALITY_HD = "hd";
    private static final String ZDF_QUALITY_VERYHIGH = "veryhigh";
    private static final String ZDF_QUALITY_HIGH = "high";
    private static final String ZDF_QUALITY_MED = "med";
    private static final String ZDF_QUALITY_MEDIUM = "medium";
    private static final String ZDF_QUALITY_LOW = "low";
    private static final String JSON_ELEMENT_ATTRIBUTES = "attributes";
    private static final String JSON_ELEMENT_AUDIO = "audio";
    private static final String JSON_ELEMENT_CAPTIONS = "captions";
    private static final String JSON_ELEMENT_CLASS = "class";
    private static final String JSON_ELEMENT_DURATION = "duration";
    private static final String JSON_ELEMENT_FORMITAET = "formitaeten";
    private static final String JSON_ELEMENT_GEOLOCATION = "geoLocation";
    private static final String JSON_ELEMENT_HIGHEST_VERTIVAL_RESOLUTION = "highestVerticalResolution";
    private static final String JSON_ELEMENT_LANGUAGE = "language";
    private static final String JSON_ELEMENT_MIMETYPE = "mimeType";
    private static final String JSON_ELEMENT_PRIORITYLIST = "priorityList";
    private static final String JSON_ELEMENT_QUALITY = "quality";
    private static final String JSON_ELEMENT_TRACKS = "tracks";
    private static final String JSON_ELEMENT_URI = "uri";

    private static final String JSON_PROPERTY_VALUE = "value";

    private static final String CLASS_AD = "ad";

    private static final String RELEVANT_MIME_TYPE = "video/mp4";
    private static final String RELEVANT_SUBTITLE_TYPE = ".xml";
    private static final String JSON_ELEMENT_QUALITIES = "qualities";

    public ZdfDownloadDtoDeserializer() {
    }

    public Optional<DownloadDto> deserialize(final JsonInfoDto jsonInfoDto, String videoUrl) {
        try {
            final Request.Builder builder = new Request.Builder().url(videoUrl);
            String api = "Bearer " + jsonInfoDto.getApi();
            builder.addHeader("Api-Auth", api);
            Response response = MLHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
            ResponseBody body = response.body();

            final DownloadDto downloadDto = new DownloadDto();
            if (body != null && response.isSuccessful()) {
                InputStream input = body.byteStream();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(input);
                parseDuration(downloadDto, rootNode);
                parseVideoUrls(downloadDto, rootNode);
                parseSubtitle(downloadDto, rootNode);
                parseGeoLocation(downloadDto, rootNode);
            }
            return Optional.of(downloadDto);
        } catch (final Exception ex) {
            P2Log.errorLog(959562301, ex, "Url: " + videoUrl);
        }
        return Optional.empty();
    }

    private void parseDuration(final DownloadDto dto, final JsonNode rootNode) {
        final JsonNode attributes = rootNode.get(JSON_ELEMENT_ATTRIBUTES);
        if (attributes != null) {
            final JsonNode durationElement = attributes.get(JSON_ELEMENT_DURATION);
            if (durationElement != null) {
                final JsonNode durationValue = durationElement.get(JSON_PROPERTY_VALUE);
                if (durationValue != null) {
                    dto.setDuration(Duration.ofMillis(durationValue.asLong()));
                }
            }
        }
    }

    private void parseVideoUrls(final DownloadDto dto, final JsonNode rootNode) {
        // array priorityList
        JsonNode jn = rootNode.get(JSON_ELEMENT_PRIORITYLIST);
        if (jn != null) {
            Iterator<JsonNode> children = jn.elements();
            while (children.hasNext()) {
                JsonNode jnC = children.next();
                parsePriority(dto, jnC);
            }
        }
    }

    private void parsePriority(final DownloadDto dto, final JsonNode priority) {
        if (priority != null) {
            JsonNode jn = priority.get(JSON_ELEMENT_FORMITAET);
            if (jn != null) {
                Iterator<JsonNode> children = jn.elements();
                while (children.hasNext()) {
                    JsonNode jnC = children.next();
                    parseFormitaet(dto, jnC);
                }
            }
        }
    }

    private void parseFormitaet(final DownloadDto dto, final JsonNode formitaet) {
        // only mp4-videos are relevant
        final JsonNode mimeType = formitaet.get(JSON_ELEMENT_MIMETYPE);
        if (mimeType != null && mimeType.asText().equalsIgnoreCase(RELEVANT_MIME_TYPE)) {
            List<DownloadInfo> downloads = new ArrayList<>();

            // array Resolution
            JsonNode jn = formitaet.get(JSON_ELEMENT_QUALITIES);
            if (jn != null) {
                Iterator<JsonNode> qualityList = jn.elements();
                while (qualityList.hasNext()) {
                    JsonNode quality = qualityList.next();
                    // todo
                    final LiveConst.Qualities qualityValue = parseVideoQuality(quality);
//                    final LiveConst.Qualities qualityValue = LiveConst.Qualities.NORMAL;
                    JsonNode jnQ = quality.get(JSON_ELEMENT_HIGHEST_VERTIVAL_RESOLUTION);
                    int verticalResolution = 0;
                    if (jnQ != null) {
                        verticalResolution = jnQ.asInt();
                    }

                    // subelement audio
                    final JsonNode audio = quality.get(JSON_ELEMENT_AUDIO);
                    if (audio != null) {
                        // array tracks
                        JsonNode jnTr = audio.get(JSON_ELEMENT_TRACKS);
                        if (jnTr != null) {
                            Iterator<JsonNode> tracks = jnTr.elements();
                            while (tracks.hasNext()) {
                                JsonNode track = tracks.next();

                                final JsonNode size = track.get("filesize");
                                String fileSize = "";
                                if (size != null) {
                                    fileSize = size.asText();
                                }

                                final AbstractMap.SimpleEntry<String, String> languageUri = extractTrack(track);
                                downloads.add(new DownloadInfo(languageUri.getKey(), languageUri.getValue(),
                                        verticalResolution, qualityValue, fileSize));
                                if (downloads.size() == 1) {
                                    // dann ists der erste
                                    if (!qualityValue.equals(LiveConst.Qualities.NORMAL)) {
                                        // dann vorsichtshalber
                                        downloads.add(new DownloadInfo(languageUri.getKey(), languageUri.getValue(),
                                                verticalResolution, LiveConst.Qualities.NORMAL, fileSize));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            downloads.sort(Comparator.comparingInt(DownloadInfo::getVerticalResolution));
            downloads.forEach(info -> dto.addUrl(info.getLanguage(), info.getQuality(), info.getUri()));
        }
    }

    private AbstractMap.SimpleEntry<String, String> extractTrack(JsonNode trackObject) {
        String classValue = trackObject.get(JSON_ELEMENT_CLASS).asText();
        String language = trackObject.get(JSON_ELEMENT_LANGUAGE).asText();
        String uri = trackObject.get(JSON_ELEMENT_URI).asText();

        // films with audiodescription are handled as a language
        if (CLASS_AD.equalsIgnoreCase(classValue)) {
            language += "-ad";
        }
        if (uri != null) {
            return new AbstractMap.SimpleEntry<>(language, uri);
        } else {
            throw new RuntimeException("uri is null");
        }
    }

    private void parseSubtitle(final DownloadDto dto, final JsonNode rootNode) {
        JsonNode jn = rootNode.get(JSON_ELEMENT_CAPTIONS);
        if (jn != null) {
            Iterator<JsonNode> captionList = rootNode.get(JSON_ELEMENT_CAPTIONS).elements();
            while (captionList.hasNext()) {
                final JsonNode caption = captionList.next();
                final JsonNode uri = caption.get(JSON_ELEMENT_URI);
                if (uri != null) {
                    final String uriValue = uri.asText();
                    JsonNode jnL = caption.get(JSON_ELEMENT_LANGUAGE);
                    final String language;
                    if (jnL != null) {
                        language = caption.get(JSON_ELEMENT_LANGUAGE).asText();
                    } else {
                        language = "";
                    }

                    // prefer xml subtitles
                    if (uriValue.endsWith(RELEVANT_SUBTITLE_TYPE)
                            || !dto.getSubTitleUrl(language).isPresent()) {
                        dto.addSubTitleUrl(language, uriValue);
                    }
                }
            }
        }
    }

    private void parseGeoLocation(final DownloadDto dto, final JsonNode rootNode) {
        final JsonNode attributes = rootNode.get(JSON_ELEMENT_ATTRIBUTES);
        if (attributes != null) {
            final JsonNode geoLocation = attributes.get(JSON_ELEMENT_GEOLOCATION);
            if (geoLocation != null) {
                final JsonNode geoValue = geoLocation.get(JSON_PROPERTY_VALUE);
                if (geoValue != null) {
                    final Optional<GeoLocations> foundGeoLocation = GeoLocations.find(geoValue.asText());
                    if (foundGeoLocation.isPresent()) {
                        dto.setGeoLocation(foundGeoLocation.get());
                    } else {
                        P2Log.errorLog(951542145, "Can't find a GeoLocation for \"{}\" " + geoValue.asText());
                    }
                }
            }
        }
    }

    private LiveConst.Qualities parseVideoQuality(final JsonNode quality) {
        LiveConst.Qualities qualityValue;
        final String zdfQuality = quality.get(JSON_ELEMENT_QUALITY).asText();
        switch (zdfQuality) {
            case ZDF_QUALITY_LOW:
            case ZDF_QUALITY_MED:
            case ZDF_QUALITY_MEDIUM:
            case ZDF_QUALITY_HIGH:
                qualityValue = LiveConst.Qualities.SMALL;
                break;
            case ZDF_QUALITY_VERYHIGH:
                qualityValue = LiveConst.Qualities.NORMAL;
                break;
            case ZDF_QUALITY_HD:
            case ZDF_QUALITY_FHD:
                qualityValue = LiveConst.Qualities.HD;
                break;
            case ZDF_QUALITY_UHD:
                qualityValue = LiveConst.Qualities.UHD;
                break;
            default:
                P2Log.errorLog(959562541, "unknown quality: {} " + zdfQuality);
                P2Log.errorLog(852141452, "ZDF: unknown quality: " + zdfQuality);
                qualityValue = LiveConst.Qualities.SMALL;
        }
        return qualityValue;
    }

    private class DownloadInfo {
        private String language;
        private String uri;
        private int verticalResolution;
        private LiveConst.Qualities quality;
        private String fileSize;

        DownloadInfo(String language, String uri, int verticalResolution, LiveConst.Qualities quality, String fileSize) {
            this.language = language;
            this.uri = uri;
            this.verticalResolution = verticalResolution;
            this.quality = quality;
            this.fileSize = fileSize;
        }

        public String getLanguage() {
            return language;
        }

        public String getUri() {
            return uri;
        }

        public int getVerticalResolution() {
            return verticalResolution;
        }

        public LiveConst.Qualities getQuality() {
            return quality;
        }

        public String getFileSize() {
            return fileSize;
        }
    }
}
