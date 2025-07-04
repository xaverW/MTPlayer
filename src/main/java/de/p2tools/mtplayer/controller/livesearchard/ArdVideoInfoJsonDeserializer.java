/*
angepasste Version aus:
https://github.com/mediathekview/MLib
*/

package de.p2tools.mtplayer.controller.livesearchard;


import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveConst;
import de.p2tools.mtplayer.controller.livesearch.tools.MVHttpClient;
import de.p2tools.mtplayer.controller.livesearch.tools.UrlUtils;
import de.p2tools.p2lib.tools.log.P2Log;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Converts json with basic video from
 * {@literal http://www.ardmediathek.de/play/media/[documentId]?devicetype=pc&features=flash}
 * to a map of {@link LiveConst.Qualities} with corresponding urls.
 */
public class ArdVideoInfoJsonDeserializer {

    private static final String ELEMENT_SUBTITLE_URL = "_subtitleUrl";

    private static final Request.Builder REQUEST_BUILDER = new Request.Builder();

    public ArdVideoInfoDto deserialize(final JsonNode aJsonElement) {
        final ArdVideoInfoDto videoInfo = new ArdVideoInfoDto();
        final JsonNode subtitleElement = aJsonElement.get(ELEMENT_SUBTITLE_URL);
        if (subtitleElement != null && !subtitleElement.isEmpty()) {
            videoInfo.setSubtitleUrl(subtitleElement.asText());
        }

        final Map<LiveConst.Qualities, URL> resolutionUrlMap = new ArdMediaArrayToDownloadUrlsConverter().toDownloadUrls(aJsonElement);

        // if map contains only a m3u8 url, load the m3u8 file and use the containing
        // urls
        if (resolutionUrlMap.size() == 1 && resolutionUrlMap.containsKey(LiveConst.Qualities.NORMAL)
                && UrlUtils.getFileType(resolutionUrlMap.get(LiveConst.Qualities.NORMAL).getFile())
                .get()
                .equals("m3u8")) {

            loadM3U8(resolutionUrlMap);
        }

        resolutionUrlMap.forEach((key, value) -> videoInfo.put(key, value.toString()));
        return videoInfo;
    }

    private void loadM3U8(Map<LiveConst.Qualities, URL> resolutionUrlMap) {
        final URL m3u8File = resolutionUrlMap.get(LiveConst.Qualities.NORMAL);
        final Optional<String> m3u8Content = readContent(m3u8File);
        resolutionUrlMap.clear();
        if (m3u8Content.isPresent()) {
            final String url = m3u8File.toString();
            String baseUrl = url.replaceAll(UrlUtils.getFileName(url).get(), "");

            M3U8Parser parser = new M3U8Parser();
            List<M3U8Dto> m3u8Data = parser.parse(m3u8Content.get());

            m3u8Data.forEach(entry -> {
                Optional<LiveConst.Qualities> resolution = entry.getResolution();
                if (resolution.isPresent()) {
                    try {
                        String videoUrl = entry.getUrl();
                        if (!UrlUtils.getProtocol(videoUrl).isPresent()) {
                            videoUrl = baseUrl + videoUrl;
                        }
                        resolutionUrlMap.put(resolution.get(), new URL(videoUrl));
                    } catch (MalformedURLException e) {
                        P2Log.errorLog(959654789, e, "ArdVideoInfoJsonDeserializer: invalid url " + entry.getUrl());
                    }
                }
            });
        }
    }

    /**
     * reads an url.
     *
     * @param aUrl the url
     * @return the content of the url
     */
    private static Optional<String> readContent(final URL aUrl) {
        Request request = REQUEST_BUILDER.url(aUrl).build();
        try (okhttp3.Response response = MVHttpClient.getInstance().getHttpClient().newCall(request).execute(); ResponseBody body = response.body()) {
            if (response.isSuccessful() && body != null) {
                return Optional.of(body.string());
            } else {
                P2Log.errorLog(202014589,
                        String.format("ArdVideoInfoJsonDeserializer: Request '%s' failed: %s", aUrl, response.code()));
            }
        } catch (IOException ex) {
            P2Log.errorLog(501478963, ex, "ArdVideoInfoJsonDeserializer: ");
        }

        return Optional.empty();
    }
}
