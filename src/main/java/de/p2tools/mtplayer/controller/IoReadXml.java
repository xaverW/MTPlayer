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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.data.BlackData;
import de.p2tools.mtplayer.controller.data.ProgramData;
import de.p2tools.mtplayer.controller.data.ReplaceData;
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.controller.data.abo.Abo;
import de.p2tools.mtplayer.controller.data.abo.AboFieldNames;
import de.p2tools.mtplayer.controller.data.download.Download;
import de.p2tools.mtplayer.controller.data.download.DownloadFieldNames;
import de.p2tools.mtplayer.controller.filmlist.filmlistUrls.FilmlistUrlData;
import de.p2tools.mtplayer.controller.mediaDb.MediaCollectionData;
import de.p2tools.mtplayer.tools.storedFilter.FilterToXml;
import de.p2tools.mtplayer.tools.storedFilter.ProgInitFilter;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilter;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilterFactory;
import de.p2tools.p2Lib.configFile.ConfigFile;
import de.p2tools.p2Lib.configFile.ReadConfigFile;
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

    private static boolean loadProgConfig() {
        final Path path = ProgInfos.getSettingsFile();
        PLog.sysLog("Programmstart und ProgConfig laden von: " + path);

        ConfigFile configFile = new ConfigFile(ProgConst.XML_START, path);
        ProgConfig.addConfigData(configFile);
        ReadConfigFile readConfigFile = new ReadConfigFile();
        readConfigFile.addConfigFile(configFile);

        return readConfigFile.readConfigFile();
    }

    private boolean readConfig(Path xmlFilePath) {
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
                                    progData.setDataList.add(psetData);
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
                                final Abo abo = new Abo();
                                if (get(parser, "Abonnement", AboFieldNames.XML_NAMES, abo.arr)) {
                                    abo.setPropsFromXml();
                                    this.progData.aboList.addAbo(abo);
                                }

                                break;
                            case "Downlad":
                                // Downloads
                                final Download d = new Download();
                                if (get(parser, "Downlad", DownloadFieldNames.XML_NAMES, d.arr)) {
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
        }

        PDuration.counterStop("Konfig lesen");
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
                    ProgConfig.getInstance().setConfigData(s, n);

//                    MLConfigs mlConfigs = ProgConfig.get(s);
//                    if (mlConfigs != null) {
//                        mlConfigs.setValue(n);
//                    }
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
}
