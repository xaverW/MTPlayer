package de.p2tools.mtplayer.controller.livesearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.atdata.AudioFactory;
import de.p2tools.p2lib.mtdownload.MLHttpClient;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.date.P2LDateTimeFactory;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

public class LiveSearchArd {

    public LiveSearchArd() {
    }

    public static List<FilmDataMTP> loadLive(JsonInfoDto jsonInfoDto) {
        jsonInfoDto.setStartUrl("https://api.ardmediathek.de/search-system/mediathek/ard/search/vods?query=" +
                jsonInfoDto.getSearchString() +
                "&pageNumber=" + jsonInfoDto.getPageNo() +
                "&pageSize=" + JsonInfoDto.PAGE_SIZE +
                "&audioDes=false&signLang=false&subtitle=false&childCont=false&sortingCriteria=SCORE_DESC&platform=MEDIA_THEK");

        int max = 0;
        try {
            final Request.Builder builder = new Request.Builder().url(jsonInfoDto.getStartUrl());
            builder.addHeader("User-Agent", ProgInfos.getUserAgent());
            Response response = MLHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
            ResponseBody body = response.body();

            if (body != null && response.isSuccessful()) {
                InputStream input = body.byteStream();
                InputStream is = AudioFactory.selectDecompressor(jsonInfoDto.getStartUrl(), input);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(is);

                if (jsonNode.get("pagination") != null) {
                    if (jsonNode.get("pagination").get("totalElements") != null) {
                        long soa = jsonNode.get("pagination").get("totalElements").asLong();
                        jsonInfoDto.setSizeOverAll(soa);
                    }
                }

                if (jsonNode.get("teasers") != null) {

                    max = jsonNode.get("teasers").size();
                    int no = -1;

                    Iterator<JsonNode> children = jsonNode.get("teasers").elements();
                    while (children.hasNext()) {
                        jsonInfoDto.setHitNo(++no);
                        jsonInfoDto.setFilmDataMTP(new FilmDataMTP());
                        if (getHit(jsonInfoDto, children.next())) {
                            LiveSearchFactory.addFilm(jsonInfoDto);
                        }

                        final double progress = 1.0 * (1 + no) / max;
                        Platform.runLater(() -> jsonInfoDto.getProgressProperty().setValue(progress));
                        System.out.println("Filme suchen: " + progress);
                    }
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(979858978, ex, "Url: " + jsonInfoDto.getStartUrl());
        }
        Platform.runLater(() -> jsonInfoDto.getProgressProperty().setValue(JsonInfoDto.PROGRESS_NULL));
        System.out.println("Filme gefunden: " + max);
        return jsonInfoDto.getList();
    }

    private static boolean getHit(JsonInfoDto jsonInfoDto, JsonNode jsonNode) {
        // hier werden die Suchtreffer abgelaufen
        if (jsonNode.get("publicationService") != null &&
                jsonNode.get("publicationService").get("partner") != null) {

            String channel = jsonNode.get("publicationService").get("partner").asText().toUpperCase();
            if (channel.equals("DAS_ERSTE")) {
                channel = "ARD";
            } else if (channel.equals("ARD-ALPHA")) {
                channel = "ARD";
            } else if (channel.equals("FUNK")) {
                channel = "Funk.net";
            }

            jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_CHANNEL] = channel;
        }
        if (jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_CHANNEL].isEmpty()) {
            jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_CHANNEL] = "ARD";
        }

        if (jsonNode.get("links") != null &&
                jsonNode.get("links").get("target") != null &&
                jsonNode.get("links").get("target").get("href") != null) {

            String url = jsonNode.get("links").get("target").get("href").asText();
            jsonInfoDto.setHitUrl(url);
            readHit(jsonInfoDto);
            return true;
        } else {
            return false;
        }
    }

    public static void readHit(JsonInfoDto jsonInfoDto) {
        try {
            final Request.Builder builder = new Request.Builder().url(jsonInfoDto.getHitUrl());
            builder.addHeader("User-Agent", ProgInfos.getUserAgent());
            Response response = MLHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
            ResponseBody body = response.body();

            if (body != null && response.isSuccessful()) {
                InputStream input = body.byteStream();
                InputStream is = AudioFactory.selectDecompressor(jsonInfoDto.getHitUrl(), input);
                ObjectMapper objectMapper = new ObjectMapper();

                JsonNode jsonNode = objectMapper.readTree(is);
                jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_THEME] = "Live-Suche";
                if (jsonNode.get("title") != null) {
                    jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_TITLE] = jsonNode.get("title").asText();
                }

                if (jsonNode.get("widgets") != null) {
                    Iterator<JsonNode> children = jsonNode.get("widgets").elements();
                    if (children.hasNext()) {
                        getMedia(jsonInfoDto, children.next());
                    }
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(201245789, ex, "Url: " + jsonInfoDto.getHitUrl());
        }
    }

    private static void getMedia(JsonInfoDto jsonInfoDto, JsonNode jsonNode) {
        if (jsonNode.get("broadcastedOn") != null) {
            String dateTime = jsonNode.get("broadcastedOn").asText();
            getDate(dateTime, jsonInfoDto.getFilmDataMTP());
        }

        if (jsonNode.get("synopsis") != null) {
            String desc = jsonNode.get("synopsis").asText();
            jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_DESCRIPTION] = desc;
        }

        if (jsonNode.get("mediaCollection") != null &&
                jsonNode.get("mediaCollection").get("href") != null) {

            String url = jsonNode.get("mediaCollection").get("href").asText();
            readMediaUrl(jsonInfoDto, url);
        }
    }

    private static void getDate(String date, FilmData filmData) {
        LocalDateTime localDate;
        try {
            if (date.isEmpty()) {
                localDate = LocalDateTime.MIN;
            } else {
                // '2011-12-03T10:15:30Z'
                localDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            }
        } catch (Exception ex) {
            localDate = LocalDateTime.MIN;
        }
        filmData.arr[FilmDataXml.FILM_DATE] = P2LDateTimeFactory.toStringDate(localDate);
        String time = localDate.format(P2DateConst.DT_FORMATTER_HH__mm__ss);
        filmData.arr[FilmDataXml.FILM_TIME] = time;
    }

    public static void readMediaUrl(JsonInfoDto jsonInfoDto, String url) {
        try {
            final Request.Builder builder = new Request.Builder().url(url);
            builder.addHeader("User-Agent", ProgInfos.getUserAgent());
            Response response = MLHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
            ResponseBody body = response.body();

            if (body != null && response.isSuccessful()) {
                InputStream input = body.byteStream();
                InputStream is = AudioFactory.selectDecompressor(url, input);
                ObjectMapper objectMapper = new ObjectMapper();

                JsonNode jsonNode = objectMapper.readTree(is);
                if (jsonNode.get("_duration") != null) {
                    try {
                        long durSecond = jsonNode.get("_duration").asInt();
                        if (durSecond > 0) {
                            final long hours = durSecond / 3600;
                            durSecond = durSecond - (hours * 3600);
                            final long min = durSecond / 60;
                            durSecond = durSecond - (min * 60);
                            final long seconds = durSecond;

                            jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_DURATION] =
                                    fillString(String.valueOf(hours)) + ':'
                                            + fillString(String.valueOf(min)) + ':'
                                            + fillString(String.valueOf(seconds));
                        } else {
                            jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_DURATION] = "";
                        }
                    } catch (final Exception ex) {
                        PLog.errorLog(359784510, ex, "url: " + url);
                    }
                }

                if (jsonNode.get("_geoblocked") != null) {
                    boolean geo = jsonNode.get("_geoblocked").asBoolean();
                    jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_GEO] = Boolean.toString(geo);
                }

                if (jsonNode.get("_mediaArray") != null) {
                    Iterator<JsonNode> children = jsonNode.get("_mediaArray").elements();
                    if (children.hasNext()) {
                        getMediaUrl1(jsonInfoDto, children.next());
                    }
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(201245789, ex, "Url: " + url);
        }
    }

    private static String fillString(String s) {
        while (s.length() < 2) {
            s = '0' + s;
        }
        return s;
    }

    private static void getMediaUrl1(JsonInfoDto jsonInfoDto, JsonNode jsonNode) {
        if (jsonNode.get("_mediaStreamArray") != null) {
            Iterator<JsonNode> children = jsonNode.get("_mediaStreamArray").elements();
            while (children.hasNext()) {
                // _height	540     _quality	2
                // _height	720     _quality	3
                // _height	1080    _quality	4

                JsonNode jn = children.next();
                String url = "";
                if (jn.get("_stream") != null) {
                    url = jn.get("_stream").asText();
                }

                if (jn.get("_quality") != null) {
                    int qual = jn.get("_quality").asInt();
                    switch (qual) {
                        case 2:
                            jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_URL_SMALL] = url;
                            if (jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_URL].isEmpty()) {
                                jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_URL] = url;
                            }
                            break;
                        case 3:
                            jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_URL] = url;
                            break;
                        case 4:
                            jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_URL_HD] = url;
                            if (jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_URL].isEmpty()) {
                                jsonInfoDto.getFilmDataMTP().arr[FilmDataXml.FILM_URL] = url;
                            }
                            break;
                    }
                }
            }
        }
    }
}
