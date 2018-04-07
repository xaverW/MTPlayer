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

package de.mtplayer.mtp.controller.data;

import de.mtplayer.mLib.tools.FileUtils;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.tools.file.GetFile;
import de.p2tools.p2Lib.tools.log.PLog;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

import static de.mtplayer.mLib.tools.Functions.getOs;
import static de.mtplayer.mLib.tools.Functions.getOsString;

@SuppressWarnings("serial")
public class ListePsetVorlagen extends LinkedList<String[]> {
    //
    public static final String PGR = "Vorlage";
    public static final String PGR_NAME = "Name";
    public static final int PGR_NAME_NR = 0;
    public static final String PGR_BESCHREIBUNG = "Beschreibung";
    public static final int PGR_BESCHREIBUNG_NR = 1;
    public static final String PGR_VERSION = "Version";
    public static final int PGR_VERSION_NR = 2;
    public static final String PGR_BS = "Bs";
    public static final int PGR_BS_NR = 3;
    public static final String PGR_URL = "URL";
    public static final int PGR_URL_NR = 4;
    public static final String PGR_INFO = "Info";
    public static final int PGR_INFO_NR = 5;
    public static final int PGR_MAX_ELEM = 6;
    public static final String[] PGR_COLUMN_NAMES = {PGR_NAME, PGR_BESCHREIBUNG, PGR_VERSION, PGR_BS, PGR_URL, PGR_INFO};
    private final static int TIMEOUT = 10000;

    public static SetList getStandarset(boolean replaceMuster) {
        SetList setList = null;
        String[] vorlage = null;

        final ListePsetVorlagen listePsetVorlagen = new ListePsetVorlagen();
        if (listePsetVorlagen.loadListOfSets()) {
            for (final String[] ar : listePsetVorlagen) {
                if (ar[PGR_NAME_NR].equalsIgnoreCase("Standardset " + getOsString())) {
                    vorlage = ar;
                    break;
                }
            }
            if (vorlage != null) {
                if (!vorlage[PGR_URL_NR].isEmpty()) {
                    setList = ListePsetVorlagen.importPsetFile(vorlage[ListePsetVorlagen.PGR_URL_NR]);
                    if (setList != null) {
                        setList.version = vorlage[PGR_VERSION_NR];
                    }
                }
            }
        }

        if (setList == null) {
            // dann nehmen wir halt die im jar-File
            // liefert das Standard Programmset für das entsprechende BS
            InputStreamReader inReader;
            switch (getOs()) {
                case LINUX:
                    inReader = new GetFile().getPsetVorlageLinux();
                    break;
                default:
                    inReader = new GetFile().getPsetVorlageWindows();
            }
            // Standardgruppen laden
            setList = ListePsetVorlagen.importPset(inReader);
        }

        if (replaceMuster && setList != null) {
            // damit die Variablen ersetzt werden
            SetList.progMusterErsetzen(setList);
        }
        return setList;
    }

    public boolean loadListOfSets() {
        try {
            clear();
            int event;
            final XMLInputFactory inFactory = XMLInputFactory.newInstance();
            inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
            XMLStreamReader parser;
            InputStreamReader inReader;
            HttpURLConnection conn;
            conn = (HttpURLConnection) new URL(Const.ADRESSE_VORLAGE_PROGRAMMGRUPPEN).openConnection();
            conn.setRequestProperty("User-Agent", ProgInfos.getUserAgent());
            conn.setReadTimeout(TIMEOUT);
            conn.setConnectTimeout(TIMEOUT);
            inReader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
            parser = inFactory.createXMLStreamReader(inReader);
            while (parser.hasNext()) {
                event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (parser.getLocalName().equals(PGR)) {
                        //wieder ein neuer Server, toll
                        final String[] p = new String[PGR_MAX_ELEM];
                        get(parser, PGR, PGR_COLUMN_NAMES, p);
                        if (!p[PGR_URL_NR].isEmpty()) {
                            this.add(p);
                        }
                    }
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(398001963, ex);
            return false;
        }
        return true;
    }

    public static SetList importPsetFile(String dateiUrl) {
        final int timeout = 10_000; //10 Sekunden
        try {
            if (FileUtils.istUrl(dateiUrl)) {
                HttpURLConnection conn;
                conn = (HttpURLConnection) new URL(dateiUrl).openConnection();
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
                conn.setRequestProperty("User-Agent", ProgInfos.getUserAgent());
                return ListePsetVorlagen.importPset(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                return ListePsetVorlagen.importPset(new InputStreamReader(new FileInputStream(dateiUrl), StandardCharsets.UTF_8));
            }
        } catch (final Exception ex) {
            PLog.errorLog(630048926, ex);
            return null;
        }
    }

//    public static SetList importPsetText(Daten dd, String text, boolean log) {
//        return ListePsetVorlagen.importPset(new InputStreamReader(new ByteArrayInputStream(text.getBytes())), log);
//    }

    private static SetList importPset(InputStreamReader in) {
        SetData psetData = null;
        final SetList liste = new SetList();
        try {
            int event;
            final XMLInputFactory inFactory = XMLInputFactory.newInstance();
            inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
            XMLStreamReader parser;
            parser = inFactory.createXMLStreamReader(in);
            while (parser.hasNext()) {
                event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    //String t = parser.getLocalName();
                    switch (parser.getLocalName()) {
                        case SetData.TAG:
                            psetData = new SetData();
                            if (!get(parser, SetData.TAG, SetData.XML_NAMES, psetData.arr)) {
                                psetData = null;
                            } else {
                                if (!psetData.isEmpty()) {
                                    //kann beim Einlesen der Konfigdatei vorkommen
                                    psetData.setPropsFromXml();
                                    liste.add(psetData);
                                }
                            }
                            break;
                        case ProgData.TAG:
                            if (psetData != null) {
                                final ProgData progData = new ProgData();
                                if (get(parser, ProgData.TAG, ProgData.XML_NAMES, progData.arr)) {
                                    progData.setPropsFromXml();
                                    psetData.addProg(progData);
                                }
                            }
                            break;
                    }
                }
            }
            in.close();
        } catch (final Exception ex) {
            PLog.errorLog(467810360, ex);

            return null;
        }
        if (liste.isEmpty()) {
            return null;
        } else {
            return liste;
        }
    }

    private static boolean get(XMLStreamReader parser, String xmlElem, String[] xmlNames, String[] strRet) {
        boolean ret = true;
        final int maxElem = strRet.length;
        for (int i = 0; i < maxElem; ++i) {
            strRet[i] = "";
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
            PLog.errorLog(467256394, ex);
        }
        return ret;
    }

}
