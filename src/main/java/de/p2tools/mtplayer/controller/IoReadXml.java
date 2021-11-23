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


package de.p2tools.mtplayer.controller;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.BlackData;
import de.p2tools.mtplayer.controller.data.ProgramData;
import de.p2tools.mtplayer.controller.data.ReplaceData;
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboFieldNames;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFieldNames;
import de.p2tools.mtplayer.controller.filmlist.filmlistUrls.FilmlistUrlData;
import de.p2tools.mtplayer.controller.mediaDb.MediaCollectionData;
import de.p2tools.mtplayer.tools.storedFilter.FilterToXml;
import de.p2tools.mtplayer.tools.storedFilter.ProgInitFilter;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilter;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilterFactory;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class IoReadXml implements AutoCloseable {

    private XMLInputFactory inFactory = null;
    private ProgData progData = null;

    public IoReadXml(ProgData progData) {
        this.progData = progData;

        inFactory = XMLInputFactory.newInstance();
        inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
    }

    public boolean readConfiguration(Path xmlFilePath) {
        boolean ret = readConfig(xmlFilePath);
        initAfterLoad();

        return ret;
    }

    private boolean readConfig(Path xmlFilePath) {
        PDuration.counterStart("Konfig lesen");
        boolean ret = false;
        int filtercount = 0;

        if (Files.exists(xmlFilePath)) {
            SetData setData = null;
            XMLStreamReader parser = null;
            try (InputStream is = Files.newInputStream(xmlFilePath);
                 InputStreamReader in = new InputStreamReader(is, StandardCharsets.UTF_8)) {

                parser = inFactory.createXMLStreamReader(in);
                while (parser.hasNext()) {
                    final int event = parser.next();
                    if (event == XMLStreamConstants.START_ELEMENT) {
                        switch (parser.getLocalName()) {

                            case ProgConfig.SYSTEM:
                                // System
                                getConfig(parser, ProgConfig.SYSTEM);
                                break;

                            case "Programmset":
                                // Programmgruppen
                                setData = new SetData();
                                if (get(parser, "Programmset", SetData.XML_NAMES, setData.arr)) {
                                    setData.setPropsFromXml();
                                    progData.setDataList.add(setData);
                                }
                                break;

                            case "Programm":
                                final ProgramData programData = new ProgramData();
                                if (get(parser, "Programm", ProgramData.XML_NAMES, programData.arr)) {
                                    if (setData != null) {
                                        programData.setPropsFromXml();
                                        setData.addProg(programData);
                                    }
                                }
                                // ende Programgruppen
                                break;

                            case "Ersetzungstabelle":
                                // Ersetzungstabelle
                                final ReplaceData replaceData = new ReplaceData();
                                if (get(parser, "Ersetzungstabelle", new String[]{"von", "to"}, replaceData.arr)) {
                                    replaceData.setPropsFromXml();
                                    this.progData.replaceList.add(replaceData);
                                }
                                break;

                            case "Abonnement":
                                // Abo
                                final AboData aboData = new AboData();
                                if (get(parser, "Abonnement", AboFieldNames.XML_NAMES, aboData.arr)) {
                                    aboData.setPropsFromXml();
                                    this.progData.aboList.addAbo(aboData);
                                }
                                break;

                            case "Downlad":
                                // Downloads
                                final DownloadData downloadData = new DownloadData();
                                if (get(parser, "Downlad", DownloadFieldNames.XML_NAMES, downloadData.arr)) {
                                    downloadData.setPropsFromXml();
                                    this.progData.downloadList.add(downloadData);
                                }
                                break;

                            case "Blacklist":
                                // Blacklist
                                final BlackData blackData = new BlackData();
                                if (get(parser, "Blacklist", BlackData.XML_NAMES, blackData.arr)) {
                                    blackData.setPropsFromXml();
                                    this.progData.blackList.add(blackData);
                                }
                                break;

                            case MediaCollectionData.TAG:
                                //
                                final MediaCollectionData mp = new MediaCollectionData();
                                if (get(parser, MediaCollectionData.TAG, MediaCollectionData.XML_NAMES, mp.arr)) {
                                    mp.setPropsFromXml();
                                    this.progData.mediaCollectionDataList.add(mp);
                                }
                                break;

                            case FilterToXml.TAG:
                                // Filter
                                final SelectedFilter sf = new SelectedFilter();
                                final String[] ar = FilterToXml.getEmptyArray();
                                if (get(parser, FilterToXml.TAG, FilterToXml.getXmlArray(), ar)) {
                                    FilterToXml.setValueArray(sf, ar);
                                    if (filtercount == 0) {
                                        // damit das nicht schon gemeldet wird
                                        this.progData.storedFilters.getActFilterSettings().setReportChange(false);
                                        SelectedFilterFactory.copyFilter(sf, this.progData.storedFilters.getActFilterSettings());
                                        this.progData.storedFilters.getActFilterSettings().setReportChange(true);
                                    } else {
                                        this.progData.storedFilters.getStoredFilterList().add(sf);
                                    }
                                    ++filtercount;
                                }
                                break;

                            case FilmlistUrlData.FILMLIST_UPDATE_SERVER:
                                // Urls Filmlisten
                                final FilmlistUrlData filmlistUrlData = new FilmlistUrlData();
                                if (get(parser,
                                        FilmlistUrlData.FILMLIST_UPDATE_SERVER,
                                        FilmlistUrlData.FILMLIST_UPDATE_SERVER_COLUMN_NAMES,
                                        filmlistUrlData.arr)) {
                                    filmlistUrlData.setPropsFromXml();
                                    switch (filmlistUrlData.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_TYPE_NR]) {
                                        case FilmlistUrlData.SERVER_ART_AKT:
                                            this.progData.searchFilmListUrls.getFilmlistUrlList_akt().addWithCheck(filmlistUrlData);
                                            break;
                                        case FilmlistUrlData.SERVER_ART_DIFF:
                                            this.progData.searchFilmListUrls.getFilmlistUrlList_diff().addWithCheck(filmlistUrlData);
                                            break;
                                    }
                                }
                                break;
                        }
                    }
                }
                ret = true;

            } catch (final Exception ex) {
                ret = false;
                PLog.errorLog(392840096, ex);

            } finally {
                try {
                    if (parser != null) {
                        parser.close();
                    }
                } catch (final Exception ignored) {
                }
            }
        }

        PDuration.counterStop("Konfig lesen");
        return ret;
    }


    private boolean getConfig(XMLStreamReader parser, String xmlElem) {
        boolean ret = true;
        try {
            while (parser.hasNext()) {
                final int event = parser.next();
                if (event == XMLStreamConstants.END_ELEMENT) {
                    if (parser.getLocalName().equals(xmlElem)) {
                        break;
                    }
                }
                if (event == XMLStreamConstants.START_ELEMENT) {
                    final String s = parser.getLocalName();
                    final String n = parser.getElementText();
                    ProgConfig.getInstance().setConfigData(s, n);
                }
            }
        } catch (final Exception ex) {
            ret = false;
            PLog.errorLog(945120369, ex);
        }
        return ret;
    }

    private boolean get(XMLStreamReader parser, String xmlElem, String[] xmlNames, String[] strRet) {
        boolean ret = true;
        final int maxElem = strRet.length;
        for (int i = 0; i < maxElem; ++i) {
            if (strRet[i] == null) {
                // damit Vorgaben nicht verschwinden!
                strRet[i] = "";
            }
        }
        try {
            while (parser.hasNext()) {
                final int event = parser.next();
                if (event == XMLStreamConstants.END_ELEMENT) {
                    if (parser.getLocalName().equals(xmlElem)) {
                        break;
                    }
                }
                if (event == XMLStreamConstants.START_ELEMENT) {
                    for (int i = 0; i < maxElem; ++i) {
                        if (parser.getLocalName().equals(xmlNames[i])) {
                            strRet[i] = parser.getElementText();
                            break;
                        }
                    }
                }
            }
        } catch (final Exception ex) {
            ret = false;
            PLog.errorLog(739530149, ex);
        }
        return ret;
    }

    private void initAfterLoad() {
        progData.blackList.sortIncCounter(false);
        progData.downloadList.initDownloads();
        progData.aboList.initAboList();
        progData.aboList.sort();

        // ListeFilmUpdateServer aufbauen
        if (progData.storedFilters.getStoredFilterList().isEmpty()) {
            ProgInitFilter.setProgInitFilter();
        }
    }

    @Override
    public void close() throws Exception {
    }
}
