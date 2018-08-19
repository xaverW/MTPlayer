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

import de.mtplayer.mLib.tools.Functions;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.p2tools.p2Lib.tools.log.PLog;

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

public class SearchFilmListUrls {

    // damit werden die DownloadURLs zum Laden einer Filmliste gesucht
    // Liste mit den URLs zum Download der Filmliste
    public FilmlistUrlList filmlistUrlList_akt = new FilmlistUrlList();
    public FilmlistUrlList filmlistUrlList_diff = new FilmlistUrlList();
    private static boolean firstSearchAkt = true;
    private static boolean firstSearchDiff = true;
    private final int UPDATE_LIST_MAX = 10; // die Downloadliste für die Filmlisten nur jeden 10. Programmstart aktualisieren

    public String searchCompleteListUrl() {
        return searchCompleteListUrl(new ArrayList<>());
    }

    public String searchCompleteListUrl(ArrayList<String> alreadyTried) {
        // passende URL zum Laden der Filmliste suchen
        String retUrl;
        if (filmlistUrlList_akt.isEmpty()) {
            // bei leerer Liste immer aktualisieren
            updateURLsFilmlists(true);
        } else if (firstSearchAkt) {
            // nach dem Programmstart wird die Liste einmal aktualisiert aber
            // da sich die Listen nicht ändern, nur jeden xx Start
            int nr = new Random().nextInt(UPDATE_LIST_MAX);
            if (nr == 0) {
                updateURLsFilmlists(true);
            }
        }
        firstSearchAkt = false;
        retUrl = (filmlistUrlList_akt.getRand(alreadyTried)); //eine Zufällige Adresse wählen
        if (alreadyTried != null) {
            alreadyTried.add(retUrl);
        }
        return retUrl;
    }

    public String searchDiffListUrl(ArrayList<String> alreadyTried) {
        // passende URL zum Laden der Filmliste suchen
        String retUrl;
        if (filmlistUrlList_diff.isEmpty()) {
            // bei leerer Liste immer aktualisieren
            updateURLsFilmlists(false);
        } else if (firstSearchDiff) {
            // nach dem Programmstart wird die Liste einmal aktualisiert aber
            // da sich die Listen nicht ändern, nur jeden xx Start
            int nr = new Random().nextInt(UPDATE_LIST_MAX);
            if (nr == 0) {
                updateURLsFilmlists(false);
            }
        }
        firstSearchDiff = false;
        retUrl = (filmlistUrlList_diff.getRand(alreadyTried)); //eine Zufällige Adresse wählen
        if (alreadyTried != null) {
            alreadyTried.add(retUrl);
        }
        return retUrl;
    }

    /**
     * Add our default full list servers.
     */
    private void insertDefaultCompleteListServers() {
        filmlistUrlList_akt.add(new FilmlistUrlData("http://verteiler1.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("http://verteiler2.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("http://verteiler3.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("http://verteiler4.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("http://verteiler5.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("http://verteiler6.mediathekview.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
        filmlistUrlList_akt.add(new FilmlistUrlData("hhttps://archiv.mediathekviewweb.de/Filmliste-akt.xz", FilmlistUrlData.SERVER_ART_AKT));
    }

    /**
     * Add our default diff list servers.
     */
    private void insertDefaultDifferentialListServers() {
        filmlistUrlList_diff.add(new FilmlistUrlData("http://verteiler1.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("http://verteiler2.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("http://verteiler3.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("http://verteiler4.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("http://verteiler5.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("http://verteiler6.mediathekview.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
        filmlistUrlList_diff.add(new FilmlistUrlData("https://archiv.mediathekviewweb.de/Filmliste-diff.xz", FilmlistUrlData.SERVER_ART_DIFF));
    }

    /**
     * Update the download server URLs.
     *
     * @param updateFullList if true, update full list server, otherwise diff servers.
     **/
    public void updateURLsFilmlists(final boolean updateFullList) {
        FilmlistUrlList tmp = new FilmlistUrlList();
        if (updateFullList) {
            getDownloadUrlsFilmlists(ProgConst.ADRESSE_FILMLISTEN_SERVER_AKT, tmp, ProgInfos.getUserAgent(), FilmlistUrlData.SERVER_ART_AKT);
            if (!tmp.isEmpty()) {
                filmlistUrlList_akt = tmp;
            } else if (filmlistUrlList_akt.isEmpty()) {
                insertDefaultCompleteListServers();
            }
            filmlistUrlList_akt.sort();
        } else {
            getDownloadUrlsFilmlists(ProgConst.ADRESSE_FILMLISTEN_SERVER_DIFF, tmp, ProgInfos.getUserAgent(), FilmlistUrlData.SERVER_ART_DIFF);
            if (!tmp.isEmpty()) {
                filmlistUrlList_diff = tmp;
            } else if (filmlistUrlList_diff.isEmpty()) {
                insertDefaultDifferentialListServers();
            }
            filmlistUrlList_diff.sort();
        }
        if (tmp.isEmpty()) {
            PLog.errorLog(491203216, new String[]{"Es ist ein Fehler aufgetreten!",
                    "Es konnten keine Updateserver zum aktualisieren der Filme",
                    "gefunden werden."});
        }
    }

    private void getDownloadUrlsFilmlists(String dateiUrl, FilmlistUrlList filmlistUrlList, String userAgent, String art) {
        //String[] ret = new String[]{""/* version */, ""/* release */, ""/* updateUrl */};
        try {
            int event;
            XMLInputFactory inFactory = XMLInputFactory.newInstance();
            inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
            XMLStreamReader parser;
            InputStreamReader inReader;
            if (Functions.istUrl(dateiUrl)) {
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
            PLog.errorLog(821069874, ex, "Die URL-Filmlisten konnte nicht geladen werden: " + dateiUrl);
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
