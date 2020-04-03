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

import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.tools.file.GetFile;
import de.p2tools.p2Lib.tools.ProgramTools;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.net.PUrlTools;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;


@SuppressWarnings("serial")
public class ListePsetVorlagen extends LinkedList<String[]> {
    //
    public static final String PGR = "Vorlage";
    public static final String PGR_NAME = "Name";
    public static final int PGR_NAME_NR = 0;
    public static final String PGR_DESCRIPTION = "Beschreibung";
    public static final int PGR_DESCRIOPTION_NR = 1;
    public static final String PGR_VERSION = "Version";
    public static final int PGR_VERSION_NR = 2;
    public static final String PGR_BS = "Bs";
    public static final int PGR_BS_NR = 3;
    public static final String PGR_URL = "URL";
    public static final int PGR_URL_NR = 4;
    public static final String PGR_INFO = "Info";
    public static final int PGR_INFO_NR = 5;
    public static final int PGR_MAX_ELEM = 6;
    public static final String[] PGR_COLUMN_NAMES = {PGR_NAME, PGR_DESCRIPTION, PGR_VERSION, PGR_BS, PGR_URL, PGR_INFO};
    private final static int TIMEOUT = 10000;

    public static SetDataList getStandarset(boolean replaceMuster) {
        SetDataList setDataList = null;
        String[] template = null;

        final ListePsetVorlagen listePsetVorlagen = new ListePsetVorlagen();
        if (listePsetVorlagen.loadListOfSets()) {
            for (final String[] ar : listePsetVorlagen) {
                if (ar[PGR_NAME_NR].equalsIgnoreCase("Standardset " + ProgramTools.getOsString())) {
                    template = ar;
                    break;
                }
            }
            if (template != null) {
                if (!template[PGR_URL_NR].isEmpty()) {
                    setDataList = ListePsetVorlagen.importPsetFile(template[ListePsetVorlagen.PGR_URL_NR]);
                    if (setDataList != null) {
                        setDataList.version = template[PGR_VERSION_NR];
                    }
                }
            }
        }

        if (setDataList == null) {
            // dann nehmen wir halt die im jar-File
            // liefert das Standard Programmset für das entsprechende BS
            InputStreamReader inReader;
            switch (ProgramTools.getOs()) {
                case LINUX:
                    inReader = new GetFile().getPsetTamplateLinux();
                    break;
                default:
                    inReader = new GetFile().getPsetTemplateWindows();
            }
            // Standardgruppen laden
            setDataList = ListePsetVorlagen.importPset(inReader);
        }

        if (replaceMuster && setDataList != null) {
            // damit die Variablen ersetzt werden
            SetDataList.progReplacePattern(setDataList);
        }
        return setDataList;
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
            conn = (HttpURLConnection) new URL(ProgConst.URL_MTPLAYER_PROGRAM_SETS).openConnection();
            conn.setRequestProperty("User-Agent", ProgInfos.getUserAgent());
            conn.setReadTimeout(TIMEOUT);
            conn.setConnectTimeout(TIMEOUT);
            inReader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
            parser = inFactory.createXMLStreamReader(inReader);
            while (parser.hasNext()) {
                event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (parser.getLocalName().equals(PGR)) {
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

    public static SetDataList importPsetFile(String fileUrl) {
        final int timeout = 10_000; //10 Sekunden
        try {
            if (PUrlTools.isUrl(fileUrl)) {
                HttpURLConnection conn;
                conn = (HttpURLConnection) new URL(fileUrl).openConnection();
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
                conn.setRequestProperty("User-Agent", ProgInfos.getUserAgent());
                return ListePsetVorlagen.importPset(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                return ListePsetVorlagen.importPset(new InputStreamReader(new FileInputStream(fileUrl), StandardCharsets.UTF_8));
            }
        } catch (final Exception ex) {
            PLog.errorLog(630048926, ex);
            return null;
        }
    }

    private static SetDataList importPset(InputStreamReader in) {
        SetData psetData = null;
        final SetDataList list = new SetDataList();
        try {
            int event;
            final XMLInputFactory inFactory = XMLInputFactory.newInstance();
            inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
            XMLStreamReader parser;
            parser = inFactory.createXMLStreamReader(in);
            while (parser.hasNext()) {
                event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    switch (parser.getLocalName()) {
                        case SetData.TAG:
                            psetData = new SetData();
                            if (!get(parser, SetData.TAG, SetData.XML_NAMES, psetData.arr)) {
                                psetData = null;
                            } else {
                                if (!psetData.isEmpty()) {
                                    //kann beim Einlesen der Konfigdatei vorkommen
                                    psetData.setPropsFromXml();
                                    list.add(psetData);
                                }
                            }
                            break;
                        case ProgramData.TAG:
                            if (psetData != null) {
                                final ProgramData progData = new ProgramData();
                                if (get(parser, ProgramData.TAG, ProgramData.XML_NAMES, progData.arr)) {
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
        if (list.isEmpty()) {
            return null;
        } else {
            return list;
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
