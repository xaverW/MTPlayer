package de.p2tools.mtplayer.controller.livesearchard;

import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.config.ProxyFactory;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.tools.log.PLog;

import java.net.HttpURLConnection;
import java.net.URL;

public class LiveSearchFactory {
    private static final int TIMEOUT_LENGTH = 2000;

    LiveSearchFactory() {
    }

    public static void addFilm(JsonInfoDtoArd jsonInfoDtoArd) {
        FilmDataMTP film = jsonInfoDtoArd.getFilmDataMTP();
        setFilmSize(film);
        film.init();
        jsonInfoDtoArd.getList().add(film);
    }

    public static void setFilmSize(FilmDataMTP film) {
        try {
            final URL url = new URL(film.getUrl());
            final long size = LiveSearchFactory.getContentLength(url); // Byte
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
}