package de.p2tools.mtplayer.controller.livesearch;

import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class LiveFactory {
    public static int PROGRESS_NULL = -1;
    public static DoubleProperty progressPropertyARD = new SimpleDoubleProperty(PROGRESS_NULL);
    public static DoubleProperty progressPropertyZDF = new SimpleDoubleProperty(PROGRESS_NULL);

    public enum CHANNEL {ARD, ZDF}

    private LiveFactory() {
    }

    public static void setProgress(CHANNEL channel, double count, int max) {
        final double progress = count / max;
        switch (channel) {
            case ARD -> Platform.runLater(() -> LiveFactory.progressPropertyARD.setValue(progress));

            case ZDF -> Platform.runLater(() -> LiveFactory.progressPropertyZDF.setValue(progress));

        }
        System.out.println("Filme suchen: " + progress);
    }

    public static void setSearchString(String searchString) {
        searchString = searchString;
    }

    public DoubleProperty getProgressProperty(CHANNEL channel) {
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
        String content = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();
        return content;
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
