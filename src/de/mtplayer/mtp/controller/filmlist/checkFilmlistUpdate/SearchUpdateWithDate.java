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


package de.mtplayer.mtp.controller.filmlist.checkFilmlistUpdate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import de.mtplayer.mLib.tools.InputStreamProgressMonitor;
import de.mtplayer.mLib.tools.MLHttpClient;
import de.mtplayer.mLib.tools.ProgressMonitorInputStream;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.controller.data.film.Filmlist;
import de.mtplayer.mtp.controller.data.film.FilmlistXml;
import de.mtplayer.mtp.controller.filmlist.filmlistUrls.SearchFilmListUrls;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.tools.log.PLog;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.tukaani.xz.XZInputStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;


public class SearchUpdateWithDate {

    private String genDateLocalTime = "";
    private String aktDate = "";
    private ArrayList<String> list = new ArrayList<>();

    public boolean hasNewRemoteFilmlist(String source, String aktDate) {
        this.aktDate = aktDate;
        list.add("Filmliste auf Update prüfen");
        return readWrite(source);
    }

    private boolean readWrite(String source) {
        boolean ret = false;
        try {
            if (source.isEmpty()) {
                if ((source = new SearchFilmListUrls().getFilmlistUrlForCompleteList()).isEmpty()) {
                    return false;
                }
            }

            if (source.startsWith("http")) {
                ret = processFromWeb(new URL(source));
            } else {
                ret = processFromFile(source);
            }

            list.add("Alter der Filmliste laden von: " + source);
        } catch (final MalformedURLException ex) {
            ex.printStackTrace();
        }

        list.add("Alter der Filmliste laden --> fertig");
        PLog.sysLog(list);
        return ret;
    }

    private boolean processFromFile(String source) {
        boolean ret = false;
        try (InputStream in = selectDecompressor(source, new FileInputStream(source));
             JsonParser jp = new JsonFactory().createParser(in)) {
            ret = startReadingData(jp);
        } catch (final FileNotFoundException ex) {
            list.add(912020214, "FilmListe existiert nicht: " + source);
        } catch (final Exception ex) {
            list.add(963214587, "FilmListe: " + source + PConst.LINE_SEPARATOR + ex.getMessage());
        }
        return ret;
    }

    private boolean processFromWeb(URL source) {
        boolean ret = false;
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
                }
            }
        };

        try (Response response = MLHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
             ResponseBody body = response.body()) {

            if (response.isSuccessful() && body != null) {
                try (InputStream input = new ProgressMonitorInputStream(body.byteStream(), body.contentLength(), monitor)) {

                    try (InputStream is = selectDecompressor(source.toString(), input);
                         JsonParser jp = new JsonFactory().createParser(is)) {

                        ret = startReadingData(jp);

                    }
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(912036547, ex, "Filmliste: " + source);
        }

        return ret;
    }

    private boolean startReadingData(JsonParser jp) throws IOException {
        JsonToken jsonToken;
        ArrayList<String> metaData = new ArrayList<>();

        if (jp.nextToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Expected data to start with an Object");
        }

        while ((jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }
            if (jp.isExpectedStartArrayToken()) {
                for (int k = 0; k < FilmlistXml.MAX_ELEM; ++k) {
                    metaData.add(jp.nextTextValue());
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

        // jetzt ist das Datum der Filmliste gesetzt und kann geprüft werden
        genDateLocalTime = Filmlist.genDate(metaData.toArray(new String[]{}));
        list.add("Alter der Filmliste ist: " + genDateLocalTime);

        return isTheListNewer();
    }

    private boolean isTheListNewer() {
        if (aktDate.equals(genDateLocalTime)) {
            // dann gibts nur die gleiche Liste
            list.add("-> Filmliste online ist die selbe");
            return false;
        }

        list.add("-> Es gibt eine aktuellere Filmliste");
        return true;
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

}
