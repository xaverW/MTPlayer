/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package de.mtplayer.mtp.controller.loadFilmlist;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import de.mtplayer.mLib.tools.InputStreamProgressMonitor;
import de.mtplayer.mLib.tools.MLHttpClient;
import de.mtplayer.mLib.tools.ProgressMonitorInputStream;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmList;
import de.mtplayer.mtp.controller.data.film.FilmListXml;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.p2tools.p2Lib.tools.log.PLog;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.time.FastDateFormat;
import org.tukaani.xz.XZInputStream;

import javax.swing.event.EventListenerList;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;

public class ReadFilmlist {

    private final EventListenerList listeners = new EventListenerList();
    private double max = 0;
    private double progress = 0;
    private long milliseconds = 0;

    public void addAdListener(ListenerFilmListLoad listener) {
        listeners.add(ListenerFilmListLoad.class, listener);
    }

    public void readFilmListe(String source, final FilmList filmList, int days) {
        try {
            PLog.sysLog("Liste Filme lesen von: " + source);
            filmList.clear();
            notifyStart(source, ListenerFilmListLoad.PROGRESS_MAX); // für die Progressanzeige

            checkDays(days);

            if (!source.startsWith("http")) {
                processFromFile(source, filmList);
            } else {
                processFromWeb(new URL(source), filmList);
            }

            if (Daten.getInstance().loadFilmList.getStop()) {
                PLog.sysLog("Filme lesen --> Abbruch");
                filmList.clear();
            }
        } catch (final MalformedURLException ex) {
            ex.printStackTrace();
        }

        notifyFertig(source, filmList);
        PLog.sysLog("Filme lesen --> fertig");
    }

    private InputStream selectDecompressor(String source, InputStream in) throws Exception {
        if (source.endsWith(Const.FORMAT_XZ)) {
            in = new XZInputStream(in);
        } else if (source.endsWith(Const.FORMAT_ZIP)) {
            final ZipInputStream zipInputStream = new ZipInputStream(in);
            zipInputStream.getNextEntry();
            in = zipInputStream;
        }
        return in;
    }

    private void readData(JsonParser jp, FilmList filmList) throws IOException {
        JsonToken jsonToken;
        String sender = "", thema = "";

        if (jp.nextToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Expected data to start with an Object");
        }

        while ((jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }
            if (jp.isExpectedStartArrayToken()) {
                for (int k = 0; k < FilmListXml.MAX_ELEM; ++k) {
                    filmList.metaDaten[k] = jp.nextTextValue();
                }
                break;
            }
        }
        while ((jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }
            if (jp.isExpectedStartArrayToken()) {
                // sind nur die Feldbeschreibungen, brauch mer nicht
                jp.nextToken();
                break;
            }
        }
        while (!Daten.getInstance().loadFilmList.getStop() && (jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }
            if (jp.isExpectedStartArrayToken()) {
                final Film film = new Film();
                for (int i = 0; i < FilmXml.JSON_NAMES.length; ++i) {
                    if (FilmXml.JSON_NAMES[i] == FilmXml.FILM_NEU) {
                        final String value = jp.nextTextValue();
                        // This value is unused...
                        // datenFilm.arr[DatenFilm.FILM_NEU_NR] = value;
                        film.setNewFilm(Boolean.parseBoolean(value));
                    } else {
                        film.arr[FilmXml.JSON_NAMES[i]] = jp.nextTextValue();
                    }

                    /// für die Entwicklungszeit
                    if (film.arr[FilmXml.JSON_NAMES[i]] == null) {
                        film.arr[FilmXml.JSON_NAMES[i]] = "";
                    }
                }
                if (film.arr[FilmXml.FILM_SENDER].isEmpty()) {
                    film.arr[FilmXml.FILM_SENDER] = sender;
                } else {
                    sender = film.arr[FilmXml.FILM_SENDER];
                }
                if (film.arr[FilmXml.FILM_THEMA].isEmpty()) {
                    film.arr[FilmXml.FILM_THEMA] = thema;
                } else {
                    thema = film.arr[FilmXml.FILM_THEMA];
                }

                filmList.importFilmliste(film);
                if (milliseconds > 0) {
                    // muss "rückwärts" laufen, da das Datum sonst 2x gebaut werden muss
                    // wenns drin bleibt, kann mans noch ändern
                    if (!checkDate(film)) {
                        filmList.remove(film);
                    }
                }
            }
        }
    }

    /**
     * Read a locally available filmlist.
     *
     * @param source   file path as string
     * @param filmList the list to read to
     */
    private void processFromFile(String source, FilmList filmList) {
        notifyProgress(source, ListenerFilmListLoad.PROGRESS_MAX);
        try (InputStream in = selectDecompressor(source, new FileInputStream(source));
             JsonParser jp = new JsonFactory().createParser(in)) {
            readData(jp, filmList);
        } catch (final FileNotFoundException ex) {
            PLog.errorLog(894512369, "FilmListe existiert nicht: " + source);
            filmList.clear();
        } catch (final Exception ex) {
            PLog.errorLog(945123641, ex, "FilmListe: " + source);
            filmList.clear();
        }
    }

    private void checkDays(long days) {
        if (days > 0) {
            milliseconds = System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
        } else {
            milliseconds = 0;
        }
    }

    /**
     * Download a process a filmliste from the web.
     *
     * @param source   source url as string
     * @param filmList the list to read to
     */
    private void processFromWeb(URL source, FilmList filmList) {
        final Request.Builder builder = new Request.Builder().url(source);
        builder.addHeader("User-Agent", ProgInfos.getUserAgent());

        // our progress monitor callback
        final InputStreamProgressMonitor monitor = new InputStreamProgressMonitor() {
            private int oldProgress = 0;

            @Override
            public void progress(long bytesRead, long size) {
                final int iProgress = (int) (bytesRead * 100/* zum Runden */ / size);
                if (iProgress != oldProgress) {
                    oldProgress = iProgress;
                    notifyProgress(source.toString(), 1.0 * iProgress / 100);
                }
            }
        };

        try (Response response = MLHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
             ResponseBody body = response.body()) {
            if (response.isSuccessful()) {
                try (InputStream input = new ProgressMonitorInputStream(body.byteStream(), body.contentLength(), monitor)) {
                    try (InputStream is = selectDecompressor(source.toString(), input);
                         JsonParser jp = new JsonFactory().createParser(is)) {
                        readData(jp, filmList);
                    }
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(945123641, ex, "FilmListe: " + source);
            filmList.clear();
        }
    }

    private boolean checkDate(Film film) {
        // true wenn der Film angezeigt werden kann!
        try {
            if (film.filmDate.getTime() != 0) {
                if (film.filmDate.getTime() < milliseconds) {
                    return false;
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(495623014, ex);
        }
        return true;
    }

    private void notifyStart(String url, double mmax) {
        max = mmax;
        progress = 0;
        for (final ListenerFilmListLoad l : listeners.getListeners(ListenerFilmListLoad.class)) {
            l.start(new ListenerFilmListLoadEvent(url, "", max, 0, 0, false));
        }
    }

    private void notifyProgress(String url, double iProgress) {
        progress = iProgress;
        if (progress > max) {
            progress = max;
        }
        for (final ListenerFilmListLoad l : listeners.getListeners(ListenerFilmListLoad.class)) {
            l.progress(new ListenerFilmListLoadEvent(url, "Download", max, progress, 0, false));
        }
    }

    private void notifyFertig(String url, FilmList liste) {
        PLog.sysLog("Liste Filme gelesen am: " + FastDateFormat.getInstance("dd.MM.yyyy, HH:mm").format(new Date()));
        PLog.sysLog("  erstellt am: " + liste.genDate());
        PLog.sysLog("  Anzahl Filme: " + liste.size());
        for (final ListenerFilmListLoad l : listeners.getListeners(ListenerFilmListLoad.class)) {
            l.fertig(new ListenerFilmListLoadEvent(url, "", max, progress, 0, false));
        }
    }

}
