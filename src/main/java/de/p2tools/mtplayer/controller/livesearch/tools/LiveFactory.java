package de.p2tools.mtplayer.controller.livesearch.tools;

import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.config.ProxyFactory;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class LiveFactory {
    private static final int TIMEOUT_LENGTH = 2000;
    public static int PROGRESS_NULL = -1;
    public static double PROGRESS_WAIT = -0.5;
    private static final DoubleProperty progressPropertyARD = new SimpleDoubleProperty(PROGRESS_NULL);
    private static final DoubleProperty progressPropertyZDF = new SimpleDoubleProperty(PROGRESS_NULL);

    public enum CHANNEL {ARD, ZDF}

    private LiveFactory() {
    }

    public static void setFilmSize(FilmDataMTP film) {
        try {
            final URL url = new URL(film.getUrl());
            final long size = getContentLength(url); // Byte
            film.arr[FilmDataXml.FILM_SIZE] = size / 1000 / 1000 + "";
        } catch (Exception ex) {
            PLog.errorLog(959874501, ex, "setFilmSize");
        }
    }

    public static long getContentLength(final URL url) {
        long ret = -1;
        HttpURLConnection connection = null;
        try {
            connection = ProxyFactory.getUrlConnection(url);
            connection.setRequestProperty("User-Agent", ProgInfos.getUserAgent());
            connection.setReadTimeout(TIMEOUT_LENGTH);
            connection.setConnectTimeout(TIMEOUT_LENGTH);
            if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                ret = connection.getContentLengthLong();
            }
            // alles unter 300k sind Playlisten, ...
            if (ret < 300 * 1000) {
                ret = -1;
            }
        } catch (final Exception ex) {
            ret = -1;
            PLog.errorLog(915254789, ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return ret;
    }


    public static void setProgress(CHANNEL channel, double count, int max) {
        final double progress = count / max;
        switch (channel) {
            case ARD -> Platform.runLater(() -> LiveFactory.progressPropertyARD.setValue(progress));

            case ZDF -> Platform.runLater(() -> LiveFactory.progressPropertyZDF.setValue(progress));

        }
        System.out.println("Filme suchen: " + progress);
    }

    public static void setProgressWait(CHANNEL channel) {
        switch (channel) {
            case ARD -> Platform.runLater(() -> LiveFactory.progressPropertyARD.setValue(PROGRESS_WAIT));

            case ZDF -> Platform.runLater(() -> LiveFactory.progressPropertyZDF.setValue(PROGRESS_WAIT));

        }
    }

    public static void setProgressNull(CHANNEL channel) {
        switch (channel) {
            case ARD -> Platform.runLater(() -> LiveFactory.progressPropertyARD.setValue(PROGRESS_NULL));

            case ZDF -> Platform.runLater(() -> LiveFactory.progressPropertyZDF.setValue(PROGRESS_NULL));

        }
    }

    public static DoubleProperty getProgressProperty(CHANNEL channel) {
        switch (channel) {
            case ARD -> {
                return progressPropertyARD;
            }
            case ZDF -> {
                return progressPropertyZDF;
            }
            default -> {
                return progressPropertyZDF;
            }
        }
    }

    public static Optional<Document> loadPage(final String url) {
        JsoupConnection jsoupConnection = new JsoupConnection();
        try {
            final Document document = jsoupConnection.getDocumentTimeoutAfter(url,
                    (int) TimeUnit.SECONDS.toMillis(60));
            return Optional.of(document);
        } catch (final IOException ex) {
            PLog.errorLog(965654120, ex, "loadPage: " + url);
        }

        return Optional.empty();
    }

    public static String getUrl(String url) throws IOException, InterruptedException {
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder(uri).build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public static void addUrlSubtitle(FilmData film, String url) {
        film.arr[FilmData.FILM_URL_SUBTITLE] = url;
    }

    public static void addUrlKlein(FilmData film, String url) {
        film.arr[FilmDataXml.FILM_URL_SMALL] = url.isEmpty() ? "" : getKlein(film.arr[FilmDataXml.FILM_URL], url);
    }

    public static void addUrlHd(FilmData film, String url) {
        film.arr[FilmDataXml.FILM_URL_HD] = url.isEmpty() ? "" : getKlein(film.arr[FilmDataXml.FILM_URL], url);
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
