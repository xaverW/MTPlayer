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

package de.mtplayer.mtp.controller.filmlist.filmlistUrls;

import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.net.PUrlTools;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

/**
 * damit können die Download-URLs der Filmliste gesucht werden
 */
public class SearchFilmListUrls {

    private FilmlistUrlList filmlistUrlList_akt = new FilmlistUrlList(); // urls der kompletten Liste
    private FilmlistUrlList filmlistUrlList_diff = new FilmlistUrlList(); // urls der diff-Liste

    private static boolean updateFilmlistUrls = false; // beim nächsten Abruf einer URL wird vorher neu geladen
    private final int UPDATE_LIST_MAX = 10; // die Downloadliste für die Filmlisten nur jeden xx Programmstart aktualisieren

    public synchronized static void setUpdateFilmlistUrls() {
        updateFilmlistUrls = true;
    }

    public FilmlistUrlList getFilmlistUrlList_akt() {
        return filmlistUrlList_akt;
    }

    public FilmlistUrlList getFilmlistUrlList_diff() {
        return filmlistUrlList_diff;
    }

    public String getFilmlistUrlForCompleteList() {
        return getFilmlistUrlForCompleteList(null);
    }

    public String getFilmlistUrlForCompleteList(ArrayList<String> alreadyTried) {
        // passende URL zum Laden der Filmliste suchen
        updateFilmlistDownloadUrls();

        String retUrl = (filmlistUrlList_akt.getRand(alreadyTried)); //eine Zufällige Adresse wählen
        if (alreadyTried != null) {
            alreadyTried.add(retUrl);
        }
        return retUrl;
    }

    public String getFilmlistUrlForDiffList() {
        return getFilmlistUrlForDiffList(null);
    }

    public String getFilmlistUrlForDiffList(ArrayList<String> alreadyTried) {
        // passende URL zum Laden der Filmliste suchen
        updateFilmlistDownloadUrls();

        String retUrl = (filmlistUrlList_diff.getRand(alreadyTried)); //eine Zufällige Adresse wählen
        if (alreadyTried != null) {
            alreadyTried.add(retUrl);
        }
        return retUrl;
    }

    private void updateFilmlistDownloadUrls() {
        final int nr = new Random().nextInt(UPDATE_LIST_MAX);
        if (nr == 0) {
            // nicht bei jedem Programmstart aktualisieren
            updateFilmlistUrls = true;
        }

        if (updateFilmlistUrls || filmlistUrlList_akt.isEmpty() || filmlistUrlList_diff.isEmpty()) {
            // wenn angefordert oder immer bei leerer Liste aktualisieren
            updateDownloadUrlsForFilmlists();
            updateFilmlistUrls = false;
        }
    }

    /**
     * Update the download server URLs.
     * immer gleich beide (akt und diff) laden
     **/
    public void updateDownloadUrlsForFilmlists() {
        PLog.sysLog("URLs der Filmlisten aktualisieren");
        PDuration.counterStart("URLs der Filmlisten aktualisieren");

        filmlistUrlList_akt.clear();
        filmlistUrlList_diff.clear();
        FilmlistUrlList tmp = new FilmlistUrlList();

        getDownloadUrlsForFilmlists(ProgConst.ADRESSE_FILMLISTEN_SERVER_AKT, tmp,
                ProgInfos.getUserAgent(), FilmlistUrlData.SERVER_ART_AKT);

        filmlistUrlList_akt.addAll(tmp);
        if (filmlistUrlList_akt.isEmpty()) {
            insertDefaultUrlForCompleteList();
            callError();
        }


        tmp.clear();
        getDownloadUrlsForFilmlists(ProgConst.ADRESSE_FILMLISTEN_SERVER_DIFF, tmp,
                ProgInfos.getUserAgent(), FilmlistUrlData.SERVER_ART_DIFF);

        filmlistUrlList_diff.addAll(tmp);
        if (filmlistUrlList_diff.isEmpty()) {
            insertDefaultUrlForDiffList();
            callError();
        }
        PDuration.counterStop("URLs der Filmlisten aktualisieren");
    }

    private void insertDefaultUrlForCompleteList() {
        filmlistUrlList_akt.add(new FilmlistUrlData("https://liste.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("https://liste.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("https://liste.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("https://liste.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("https://liste.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("https://liste.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));

        filmlistUrlList_akt.add(new FilmlistUrlData("http://verteiler1.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("http://verteiler2.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("http://verteiler3.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("http://verteiler4.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("http://verteiler5.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("http://verteiler6.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
    }

    private void insertDefaultUrlForDiffList() {
        filmlistUrlList_diff.add(new FilmlistUrlData("https://liste.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("https://liste.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("https://liste.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("https://liste.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("https://liste.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("https://liste.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("https://liste.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));

        filmlistUrlList_diff.add(new FilmlistUrlData("http://verteiler1.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("http://verteiler2.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("http://verteiler3.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("http://verteiler4.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("http://verteiler5.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("http://verteiler6.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
    }

    private void callError() {
        PLog.errorLog(491203216, new String[]{"Es ist ein Fehler aufgetreten!",
                "Es konnten keine Updateserver zum aktualisieren der Filme",
                "gefunden werden."});
    }

    private void getDownloadUrlsForFilmlists(String dateiUrl, FilmlistUrlList filmlistUrlList, String userAgent, String art) {
        //String[] ret = new String[]{""/* version */, ""/* release */, ""/* updateUrl */};
        try {
            int event;
            XMLInputFactory inFactory = XMLInputFactory.newInstance();
            inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
            XMLStreamReader parser;
            InputStreamReader inReader;
            if (PUrlTools.isUrl(dateiUrl)) {
                // eine URL verarbeiten
                int timeout = 20000; //ms
                HttpURLConnection conn;
                conn = (HttpURLConnection) new URL(dateiUrl).openConnection();
                conn.setRequestProperty("User-Agent", userAgent);
                conn.setReadTimeout(timeout);
                conn.setConnectTimeout(timeout);
                inReader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
            } else {
                // eine Datei verarbeiten
                File f = new File(dateiUrl);
                if (!f.exists()) {
                    return;
                }
                inReader = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
            }
            parser = inFactory.createXMLStreamReader(inReader);
            while (parser.hasNext()) {
                event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String parserName = parser.getLocalName();
                    if (parserName.equals("Server")) {
                        //wieder ein neuer Server, toll
                        parseServerEntry(parser, filmlistUrlList, art);
                    }
                }
            }
        } catch (Exception ex) {
            PLog.errorLog(821069874, ex, new String[]{"Die Download-URLs der Filmlisten konnten nicht " +
                    "ermittelt werden: ", dateiUrl});
        }
    }

    /**
     * Parse the server XML file.
     *
     * @param parser
     * @param filmlistUrlList
     * @param art
     */
    private void parseServerEntry(XMLStreamReader parser, FilmlistUrlList filmlistUrlList, String art) {
        String serverUrl = "";
        String prio = "";
        int event;
        try {
            while (parser.hasNext()) {
                event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    //parsername = parser.getLocalName();
                    switch (parser.getLocalName()) {
                        case "URL":
                            serverUrl = parser.getElementText();
                            break;
                        case "Prio":
                            prio = parser.getElementText();
                            break;
                    }
                }
                if (event == XMLStreamConstants.END_ELEMENT) {
                    //parsername = parser.getLocalName();
                    if (parser.getLocalName().equals("Server")) {
                        if (!serverUrl.equals("")) {
                            //public DatenFilmUpdate(String url, String prio, String zeit, String datum, String anzahl) {
                            if (prio.equals("")) {
                                prio = FilmlistUrlData.FILMLIST_UPDATE_SERVER_PRIO_1;
                            }
                            filmlistUrlList.addWithCheck(new FilmlistUrlData(serverUrl, prio, art));
                        }
                        break;
                    }
                }
            }
        } catch (XMLStreamException ignored) {
        }

    }

}
