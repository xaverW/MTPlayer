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


package de.mtplayer.mtp.controller;

import de.mtplayer.mLib.tools.MLConfigs;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.BlackData;
import de.mtplayer.mtp.controller.data.ProgramData;
import de.mtplayer.mtp.controller.data.ReplaceData;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.controller.data.abo.AboXml;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadXml;
import de.mtplayer.mtp.controller.filmlist.filmlistUrls.FilmlistUrlData;
import de.mtplayer.mtp.controller.mediaDb.MediaCollectionData;
import de.mtplayer.mtp.tools.storedFilter.FilterToXml;
import de.mtplayer.mtp.tools.storedFilter.ProgInitFilter;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import de.p2tools.p2Lib.tools.log.PDuration;
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
        PDuration.counterStart("Konfig lesen");
        boolean ret = false;
        int filtercount = 0;

        if (Files.exists(xmlFilePath)) {
            SetData psetData = null;
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
                            case SetData.TAG:
                                // Programmgruppen
                                psetData = new SetData();
                                if (get(parser, SetData.TAG, SetData.XML_NAMES, psetData.arr)) {
                                    psetData.setPropsFromXml();
                                    progData.setList.add(psetData);
                                }
                                break;
                            case ProgramData.TAG:
                                final ProgramData progData = new ProgramData();
                                if (get(parser, ProgramData.TAG, ProgramData.XML_NAMES, progData.arr)) {
                                    if (psetData != null) {
                                        progData.setPropsFromXml();
                                        psetData.addProg(progData);
                                    }
                                }
                                // ende Programgruppen
                                break;
                            case ReplaceData.TAG:
                                // Ersetzungstabelle
                                final ReplaceData replaceData = new ReplaceData();
                                if (get(parser, ReplaceData.TAG, ReplaceData.COLUMN_NAMES, replaceData.arr)) {
                                    replaceData.setPropsFromXml();
                                    this.progData.replaceList.add(replaceData);
                                }
                                break;
                            case AboXml.TAG:
                                // Abo
                                final Abo abo = new Abo();
                                if (get(parser, AboXml.TAG, AboXml.XML_NAMES, abo.arr)) {
                                    abo.setPropsFromXml();
                                    this.progData.aboList.addAbo(abo);
                                }

                                break;
                            case DownloadXml.TAG:
                                // Downloads
                                final Download d = new Download();
                                if (get(parser, DownloadXml.TAG, DownloadXml.XML_NAMES, d.arr)) {
                                    d.setPropsFromXml();
                                    this.progData.downloadList.add(d);
                                }
                                break;
                            case BlackData.TAG:
                                // Blacklist
                                final BlackData blackData = new BlackData();
                                if (get(parser, BlackData.TAG, BlackData.XML_NAMES, blackData.arr)) {
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
                                        SelectedFilter.copyFilter(sf, this.progData.storedFilter.getSelectedFilter());
                                    } else {
                                        this.progData.storedFilter.getStordeFilterList().add(sf);
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
                                    switch (filmlistUrlData.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_SORT_NR]) {
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
            progData.downloadList.initDownloads();
            progData.aboList.sort();
            // ListeFilmUpdateServer aufbauen
            if (progData.storedFilter.getStordeFilterList().isEmpty()) {
                ProgInitFilter.setProgInitFilter();
            }
        }

        PDuration.counterStop("Konfig lesen");
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
                    MLConfigs mlConfigs = ProgConfig.get(s);
                    if (mlConfigs != null) {
                        mlConfigs.setValue(n);
                    }
                }
            }
        } catch (final Exception ex) {
            ret = false;
            PLog.errorLog(945120369, ex);
        }
        return ret;
    }

    @Override
    public void close() throws Exception {

    }

    public class ImportStatistics {

        public int foundAbos = 0;
        public int foundBlacklist = 0;
        public int foundReplacements = 0;
    }

    private String[] getArr(int i) {
        String[] str = new String[i];
        for (int ii = 0; ii < i; ++ii) {
            str[ii] = "";
        }
        return str;
    }
}
