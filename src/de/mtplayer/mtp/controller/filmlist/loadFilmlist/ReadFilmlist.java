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

package de.mtplayer.mtp.controller.filmlist.loadFilmlist;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import de.mtplayer.mLib.tools.InputStreamProgressMonitor;
import de.mtplayer.mLib.tools.MLHttpClient;
import de.mtplayer.mLib.tools.ProgressMonitorInputStream;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.controller.data.film.Filmlist;
import de.mtplayer.mtp.controller.data.film.FilmlistXml;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;

public class ReadFilmlist {

    private final EventListenerList listeners = new EventListenerList();
    private double progress = 0;
    private long milliseconds = 0;
    private boolean checkDateBeforeLoading = false;

    public void addAdListener(ListenerFilmlistLoad listener) {
        listeners.add(ListenerFilmlistLoad.class, listener);
    }

    /*
    Hier wird die Filmliste tatsächlich geladen (von Datei/URL)
     */
    public void readFilmlist(String sourceFileUrl, final Filmlist filmlist, int days) {
        readFilmlist(sourceFileUrl, filmlist, days, false);
    }

    public void readFilmlist(String sourceFileUrl, final Filmlist filmlist, int days, boolean checkDateBeforeLoading) {
        this.checkDateBeforeLoading = checkDateBeforeLoading;
        ArrayList<String> list = new ArrayList<>();
        try {
            list.add("Liste Filme lesen von: " + sourceFileUrl);
            filmlist.clear();
            notifyStart(sourceFileUrl); // für die Progressanzeige

            checkDays(days);

            if (!sourceFileUrl.startsWith("http")) {
                processFromFile(sourceFileUrl, filmlist);
            } else {
                processFromWeb(new URL(sourceFileUrl), filmlist);
            }

            if (ProgData.getInstance().loadFilmlist.isStop()) {
                list.add("Filme lesen --> Abbruch");
                filmlist.clear();
            }
        } catch (final MalformedURLException ex) {
            ex.printStackTrace();
        }

        notifyFinished(sourceFileUrl, filmlist);
        list.add("Filme lesen --> fertig");
        PLog.sysLog(list);
    }

    private InputStream selectDecompressor(String source, InputStream in) throws Exception {
        if (source.endsWith(ProgConst.FORMAT_XZ)) {
            in = new XZInputStream(in);
        } else if (source.endsWith(ProgConst.FORMAT_ZIP)) {
            final ZipInputStream zipInputStream = new ZipInputStream(in);
            zipInputStream.getNextEntry();
            in = zipInputStream;
        }
        return in;
    }

    String channel = "", theme = "";

    private void readData(JsonParser jp, Filmlist filmlist) throws IOException {
        JsonToken jsonToken;

        if (jp.nextToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Expected data to start with an Object");
        }

        while ((jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }
            if (jp.isExpectedStartArrayToken()) {
                for (int k = 0; k < FilmlistXml.MAX_ELEM; ++k) {
                    filmlist.metaData[k] = jp.nextTextValue();
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

        // Datum checken, wenn gewollt und dann abbrechen todo
        if (checkDateBeforeLoading && filmlist.isOlderThan(ProgConst.ALTER_FILMLISTE_SEKUNDEN_FUER_AUTOUPDATE)) {
            return;
        }

        while (!ProgData.getInstance().loadFilmlist.isStop() && (jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }
            if (jp.isExpectedStartArrayToken()) {
                final Film film = new Film();

                //todo
//                addValue_(film, jp);
                addValue(film, jp);


                filmlist.importFilm(film);
                if (milliseconds > 0) {
                    // muss "rückwärts" laufen, da das Datum sonst 2x gebaut werden muss
                    // wenns drin bleibt, kann mans noch ändern
                    if (!checkDate(film)) {
                        filmlist.remove(film);
                    }
                }
            }
        }
    }

    private void addValue(Film film, JsonParser jp) throws IOException {
        for (int i = 0; i < FilmXml.JSON_NAMES.length; ++i) {
            String str = jp.nextTextValue();

            switch (FilmXml.JSON_NAMES[i]) {
                case FilmXml.FILM_NEW:
                    // This value is unused...
                    // datenFilm.arr[DatenFilm.FILM_NEU_NR] = value;
                    film.setNewFilm(Boolean.parseBoolean(str));
                    break;

                case FilmXml.FILM_CHANNEL:
                    if (!str.isEmpty()) {
                        channel = str.intern();
                    }
                    film.arr[FilmXml.FILM_CHANNEL] = channel;
                    break;

                case FilmXml.FILM_THEME:
                    if (!str.isEmpty()) {
                        theme = str.intern();
                    }
                    film.arr[FilmXml.FILM_THEME] = theme;
                    break;

                default:
                    film.arr[FilmXml.JSON_NAMES[i]] = str;
                    break;
            }

            /// für die Entwicklungszeit
            if (film.arr[FilmXml.JSON_NAMES[i]] == null) {
                film.arr[FilmXml.JSON_NAMES[i]] = "";
            }

        }
    }

    /**
     * Read a locally available filmlist.
     *
     * @param source   file path as string
     * @param filmlist the list to read to
     */
    private void processFromFile(String source, Filmlist filmlist) {
        notifyProgress(source, ListenerFilmlistLoad.PROGRESS_INDETERMINATE);
        try (InputStream in = selectDecompressor(source, new FileInputStream(source));
             JsonParser jp = new JsonFactory().createParser(in)) {
            readData(jp, filmlist);
        } catch (final FileNotFoundException ex) {
            PLog.errorLog(894512369, "FilmListe existiert nicht: " + source);
            filmlist.clear();
        } catch (final Exception ex) {
            PLog.errorLog(945123641, ex, "FilmListe: " + source);
            filmlist.clear();
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
     * @param filmlist the list to read to
     */
    private void processFromWeb(URL source, Filmlist filmlist) {
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
            if (body != null && response.isSuccessful()) {

                try (InputStream input = new ProgressMonitorInputStream(body.byteStream(), body.contentLength(), monitor)) {
                    try (InputStream is = selectDecompressor(source.toString(), input);
                         JsonParser jp = new JsonFactory().createParser(is)) {
                        readData(jp, filmlist);
                    }
                }

            }
        } catch (final Exception ex) {
            PLog.errorLog(945123641, ex, "FilmListe: " + source);
            filmlist.clear();
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

    private void notifyStart(String url) {
        progress = 0;
        for (final ListenerFilmlistLoad l : listeners.getListeners(ListenerFilmlistLoad.class)) {
            l.start(new ListenerFilmlistLoadEvent(url, "Filmliste downloaden", 0, 0, false));
        }
    }

    private void notifyProgress(String url, double iProgress) {
        progress = iProgress;
        if (progress > ListenerFilmlistLoad.PROGRESS_MAX) {
            progress = ListenerFilmlistLoad.PROGRESS_MAX;
        }
        for (final ListenerFilmlistLoad l : listeners.getListeners(ListenerFilmlistLoad.class)) {
            l.progress(new ListenerFilmlistLoadEvent(url, "Filmliste downloaden", progress, 0, false));
        }
    }

    private void notifyFinished(String url, Filmlist filmlist) {
        ArrayList<String> list = new ArrayList<>();
        list.add(PLog.LILNE3);
        list.add("Liste Filme gelesen am: " + FastDateFormat.getInstance("dd.MM.yyyy, HH:mm").format(new Date()));
        list.add("  erstellt am: " + filmlist.genDate());
        list.add("  Anzahl Filme: " + filmlist.size());
        for (final ListenerFilmlistLoad l : listeners.getListeners(ListenerFilmlistLoad.class)) {
            l.finished(new ListenerFilmlistLoadEvent(url, "Filmliste geladen", progress, 0, false));
        }
        list.add(PLog.LILNE3);
        PLog.sysLog(list);
    }

}
