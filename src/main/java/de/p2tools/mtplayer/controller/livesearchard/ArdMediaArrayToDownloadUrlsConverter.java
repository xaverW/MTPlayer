/*
angepasste Version aus:
https://github.com/mediathekview/MLib
*/

package de.p2tools.mtplayer.controller.livesearchard;


import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.livesearch.tools.JsonFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveConst;
import de.p2tools.mtplayer.controller.livesearch.tools.UrlUtils;
import de.p2tools.p2lib.tools.log.P2Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ArdMediaArrayToDownloadUrlsConverter {

    private static final String ELEMENT_STREAM = "_stream";
    private static final String URL_PREFIX_PATTERN = "\\w+:";
    private static final String URL_PATTERN = "\\w+.*";
    private static final String ELEMENT_HEIGHT = "_height";
    private static final String ELEMENT_MEDIA_ARRAY = "_mediaArray";
    private static final String ELEMENT_MEDIA_STREAM_ARRAY = "_mediaStreamArray";
    private static final String ELEMENT_PLUGIN = "_plugin";
    private static final String ELEMENT_QUALITY = "_quality";
    private static final String ELEMENT_SERVER = "_server";
    private static final String ELEMENT_SORT_ARRAY = "_sortierArray";
    private static final String ELEMENT_WIDTH = "_width";
    private static final String PROTOCOL_RTMP = "rtmp";

    private static final String FILE_TYPE_F4M = "f4m";

    private final ArdUrlOptimizer ardOptimizer;
    private final Map<LiveConst.Qualities, Set<ArdFilmUrlInfoDto>> urls;
//    private MediathekReader crawler;

    public ArdMediaArrayToDownloadUrlsConverter() {
        ardOptimizer = new ArdUrlOptimizer();
        urls = new EnumMap<>(LiveConst.Qualities.class);
    }

    private static List<ArdFilmUrlInfoDto> filterUrls(
            final Set<ArdFilmUrlInfoDto> aUrls, final String aFileType) {
        return aUrls.stream()
                .filter(
                        url
                                -> url.getFileType().isPresent()
                                && url.getFileType().get().equalsIgnoreCase(aFileType))
                .collect(Collectors.toList());
    }

    private static Optional<LiveConst.Qualities> getQuality(final String qualityAsText) {
        int qualityNumber;
        try {
            if (qualityAsText.equals("auto")) {
                // Some films only contains "auto" quality with a m3u8-url
                // treat quality "auto" as NORMAL though the m3u8-url is returned
                return Optional.of(LiveConst.Qualities.NORMAL);
            } else {
                qualityNumber = Integer.parseInt(qualityAsText);
            }
        } catch (final NumberFormatException numberFormatException) {
            P2Log.debugLog("Can't convert quality %s to an integer. " + qualityAsText);
            qualityNumber = -1;
        }

        if (qualityNumber > 0) {
            return Optional.of(getQualityForNumber(qualityNumber));
        }

        return Optional.empty();
    }

    /**
     * returns the url to use for downloads uses the following order: mp4 > m3u8 >
     * Rest.
     *
     * @param aUrls list of possible urls
     * @return the download url
     */
    private static String determineUrl(
            final LiveConst.Qualities resolution, final Set<ArdFilmUrlInfoDto> aUrls) {

        if (aUrls.isEmpty()) {
            return "";
        }

        final ArdFilmUrlInfoDto ardUrlInfo;

        List<ArdFilmUrlInfoDto> urls = filterUrls(aUrls, "mp4");
        if (!urls.isEmpty()) {
            ardUrlInfo = getRelevantUrlMp4(resolution, urls);
        } else {

            urls = filterUrls(aUrls, "m3u8");
            if (!urls.isEmpty()) {
                ardUrlInfo = urls.get(0);
            } else {
                ardUrlInfo = aUrls.iterator().next();
            }
        }

        if (ardUrlInfo != null) {
            return ardUrlInfo.getUrl();
        }

        return "";
    }

    private static int extractPluginValue(final JsonNode aJsonObject) {
        if (aJsonObject.has(ELEMENT_SORT_ARRAY)) {
            final JsonNode pluginElement = aJsonObject.get(ELEMENT_SORT_ARRAY);
            Iterator<JsonNode> it = pluginElement.elements();
            if (it.hasNext()) {
                return it.next().asInt();
            }
        }

        return 1;
    }

    public Map<LiveConst.Qualities, URL> toDownloadUrls(final JsonNode jsonElement) {
        final int pluginValue = extractPluginValue(jsonElement);
        if (jsonElement.has(ELEMENT_MEDIA_ARRAY)) {
            Iterator<JsonNode> it = jsonElement.get(ELEMENT_MEDIA_ARRAY).elements();
            parseMediaArray(pluginValue, it);
        }
        return extractRelevantUrls();
    }

    private void addUrl(
            final String url,
            final String qualityText,
            final LiveConst.Qualities quality,
            final Optional<String> height,
            final Optional<String> width) {

        if (!url.isEmpty()) {
            if (url.startsWith(PROTOCOL_RTMP)) {
                P2Log.debugLog("Found an Sendung with the old RTMP format: " + url);
            } else {
                final ArdFilmUrlInfoDto info
                        = new ArdFilmUrlInfoDto(UrlUtils.removeParameters(UrlUtils.addProtocolIfMissing(url, "https:")), qualityText);
                if (height.isPresent() && width.isPresent()) {
                    info.setResolution(Integer.parseInt(width.get()), Integer.parseInt(height.get()));
                }

                if (!urls.containsKey(quality)) {
                    urls.put(quality, new LinkedHashSet<>());
                }
                urls.get(quality).add(info);
            }
        }
    }

    private Map<LiveConst.Qualities, URL> extractRelevantUrls() {
        final Map<LiveConst.Qualities, URL> downloadUrls = new EnumMap<>(LiveConst.Qualities.class);

        removeAutoM3u8IfMp4Exists();

        urls.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .filter(ArdMediaArrayToDownloadUrlsConverter::isFileTypeRelevant)
                .forEach(
                        entry -> {
                            finalizeUrl(entry).ifPresent(url -> downloadUrls.put(entry.getKey(), url));
                        });

        // add lowest HD-Url as NORMAL if normal is not present
        if (!downloadUrls.containsKey(LiveConst.Qualities.NORMAL) && urls.containsKey(LiveConst.Qualities.HD)) {
            Optional<URL> normalUrl = determineNormalUrlFromHd(urls.get(LiveConst.Qualities.HD));
            normalUrl.ifPresent(url -> downloadUrls.put(LiveConst.Qualities.NORMAL, url));
        }

        return downloadUrls;
    }

    // removes m3u8-url with quality=auto if at least one mp4 url exists
    // otherwise m3u8-url could be the normal url while small+hd contains mp4-urls
    private void removeAutoM3u8IfMp4Exists() {
        AtomicBoolean existsMp4 = new AtomicBoolean(false);

        urls.values().forEach(set ->
                set.forEach(value -> {
                    final Optional<String> fileType = UrlUtils.getFileType(value.getUrl());
                    if (fileType.isPresent() && "mp4".equalsIgnoreCase(fileType.get())) {
                        existsMp4.set(true);
                    }
                }));

        if (existsMp4.get() && urls.containsKey(LiveConst.Qualities.NORMAL)) {
            urls.get(LiveConst.Qualities.NORMAL).removeIf(urlInfo -> urlInfo.getQuality().equalsIgnoreCase("auto"));
        }
    }

    private Optional<URL> determineNormalUrlFromHd(Set<ArdFilmUrlInfoDto> ardFilmUrlInfoDtos) {
        ArdFilmUrlInfoDto relevantInfo = null;

        for (final ArdFilmUrlInfoDto info : ardFilmUrlInfoDtos) {
            if (info.getWidth() > 0 && info.getHeight() > 0) {
                if (relevantInfo == null) {
                    relevantInfo = info;
                } else if (relevantInfo.getQuality().compareTo(info.getQuality()) > 0) {
                    relevantInfo = info;
                }
            }
        }

        if (relevantInfo != null) {
            try {
                return Optional.of(new URL(relevantInfo.getUrl()));
            } catch (final MalformedURLException malformedUrlException) {
                P2Log.errorLog(987541258, malformedUrlException, "A download URL is defect.");
            }
        }

        return Optional.empty();
    }

    private static boolean isFileTypeRelevant(final Map.Entry<LiveConst.Qualities, Set<ArdFilmUrlInfoDto>> entry) {
        return entry.getValue().stream()
                .anyMatch(video -> video.getFileType().isPresent()
                        && !FILE_TYPE_F4M.equalsIgnoreCase(video.getFileType().get()));
    }

    private Optional<URL> finalizeUrl(final Map.Entry<LiveConst.Qualities, Set<ArdFilmUrlInfoDto>> entry) {
        final String url = determineUrl(entry.getKey(), entry.getValue());
        if (!url.isEmpty()) {
            try {
                return Optional.of(new URL(optimizeUrl(entry.getKey(), url)));
            } catch (final MalformedURLException malformedUrlException) {
                P2Log.errorLog(987201204, malformedUrlException, "A download URL is defect.");
            }
        }
        return Optional.empty();
    }

    private String optimizeUrl(final LiveConst.Qualities key, final String url) {
        if (key == LiveConst.Qualities.HD) {
            return ardOptimizer.optimizeHdUrl(url);
        }

        return url;
    }

    private static LiveConst.Qualities getQualityForNumber(final int i) {
        switch (i) {
            case 0:
            case 1:
                return LiveConst.Qualities.SMALL;

            case 3:
            case 4:
                return LiveConst.Qualities.HD;
            case 5:
                return LiveConst.Qualities.UHD;
            case 2:
            default:
                return LiveConst.Qualities.NORMAL;
        }
    }

    private static ArdFilmUrlInfoDto getRelevantUrlMp4(
            final LiveConst.Qualities aQualities, final List<ArdFilmUrlInfoDto> aUrls) {
        switch (aQualities) {
            case SMALL:
                // the first url is the best
                return aUrls.get(0);
            case NORMAL:
                // the last url is the best
                return aUrls.get(aUrls.size() - 1);
            case HD:
                ArdFilmUrlInfoDto relevantInfo = null;

                for (final ArdFilmUrlInfoDto info : aUrls) {
                    if (info.getWidth() >= 1280 && info.getHeight() >= 720) {
                        if (relevantInfo == null) {
                            relevantInfo = info;
                        } else if (relevantInfo.getQuality().compareTo(info.getQuality()) < 0) {
                            relevantInfo = info;
                        }
                    }
                    if (info.getWidth() == 0 && info.getHeight() == 0) {
                        final String url = info.getUrl();

                        // Sometimes videos with a resolution of 960 are listed as quality HD
                        if (!url.startsWith("960", url.lastIndexOf('/') + 1)) {
                            if (relevantInfo == null) {
                                relevantInfo = info;
                            } else if (relevantInfo.getQuality().compareTo(info.getQuality()) < 0) {
                                relevantInfo = info;
                            }
                        }
                    }
                }
                return relevantInfo;
            default:
                return null;
        }
    }

    private void parseMediaArray(final int pluginValue, final Iterator<JsonNode> mediaArray) {
        while (mediaArray.hasNext()) {
            JsonNode jsonNode = mediaArray.next();
            if (jsonNode.has(ELEMENT_PLUGIN) && jsonNode.get(ELEMENT_PLUGIN).asInt() == pluginValue) {

                if (jsonNode.has(ELEMENT_MEDIA_STREAM_ARRAY)) {
                    Iterator<JsonNode> it = jsonNode.get(ELEMENT_MEDIA_STREAM_ARRAY).elements();
                    parseMediaStreamArray(it);
                }
            }
        }
//        StreamSupport.stream(mediaArray, false)
//                .map(JsonElement::getAsJsonObject)
//                .filter(mediaObj -> mediaObj.get(ELEMENT_PLUGIN).getAsInt() == pluginValue)

//                .map(mediaObj -> mediaObj.getAsJsonArray(ELEMENT_MEDIA_STREAM_ARRAY))
//                .forEach(this::parseMediaStreamArray);
    }

    private void parseMediaStreamArray(final Iterator<JsonNode> it) {
        while (it.hasNext()) {
            JsonNode videoElement = it.next();
            final String qualityAsText = JsonFactory.getString(videoElement, ELEMENT_QUALITY);
            final Optional<LiveConst.Qualities> quality = getQuality(qualityAsText);
            if (quality.isPresent()) {
                parseMediaStreamServer(videoElement, qualityAsText, quality.get());
                parseMediaStreamStream(videoElement, qualityAsText, quality.get());
            }
        }
    }

    private void parseMediaStreamServer(
            final JsonNode videoElement, final String qualityText, final LiveConst.Qualities quality) {
        if (videoElement.has(ELEMENT_SERVER)) {
            final String baseUrl = videoElement.get(ELEMENT_SERVER).asText();
            final String downloadUrl = videoElementToUrl(videoElement, baseUrl);
            addUrl(downloadUrl, qualityText, quality, Optional.empty(), Optional.empty());
        }
    }

    private void parseMediaStreamStream(
            final JsonNode videoElement, final String qualityText, final LiveConst.Qualities quality) {
        if (videoElement.has(ELEMENT_STREAM)) {

            final JsonNode videoObject = videoElement;
            final JsonNode streamObject = videoObject.get(ELEMENT_STREAM);

            final Optional<String> height = JsonFactory.getOptStringElement(videoObject, ELEMENT_HEIGHT);
            final Optional<String> width = JsonFactory.getOptStringElement(videoObject, ELEMENT_WIDTH);

            if (streamObject.isValueNode()) {
                final String baseUrl = streamObject.asText();
                final String downloadUrl = videoElementToUrl(videoElement, baseUrl);
                addUrl(downloadUrl, qualityText, quality, height, width);
            } else if (streamObject.isArray()) {
                Iterator<JsonNode> it = streamObject.elements();
                while (it.hasNext()) {
                    JsonNode jn = it.next();
                    addUrl(jn.asText(), qualityText, quality, height, width);
                }
//                StreamSupport.stream(streamObject.getAsJsonArray().spliterator(), false)
//                        .map(JsonElement::getAsString)
//                        .forEach(baseUrl -> addUrl(baseUrl, qualityText, quality, height, width));
            }
        }
    }

    private String videoElementToUrl(final JsonNode videoElement, final String baseUrl) {
        if (baseUrl.isEmpty()) {
            return baseUrl;
        }

        String url = videoElement.get(ELEMENT_STREAM).asText();
        if (url.equals(baseUrl)) {
            return url;
        }
        if (url.matches(URL_PREFIX_PATTERN + URL_PATTERN)) {
            url = url.replaceFirst(URL_PREFIX_PATTERN, baseUrl);
        } else {
            url = baseUrl + url;
        }
        return url;
    }
}
