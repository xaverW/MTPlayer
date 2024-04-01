/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.mv;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.blackdata.BlackData;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.collections.ObservableList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoadMV implements AutoCloseable {
    private final XMLInputFactory inFactory;
    private final ObservableList<AboData> aboList;
    private final ObservableList<BlackData> blackList;

    public LoadMV(ObservableList<AboData> aboList, ObservableList<BlackData> blackList) {
        this.aboList = aboList;
        this.blackList = blackList;

        inFactory = XMLInputFactory.newInstance();
        inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
    }

    public boolean readConfiguration(Path xmlFilePath) {
        aboList.clear();
        blackList.clear();
        Path configPath = Path.of(xmlFilePath.toString(), "mediathek.xml");

        if (!Files.exists(configPath)) {
            return false;
        }

        PDuration.counterStart("readConfiguration");
        boolean ret;
        try (InputStream is = Files.newInputStream(configPath);
             InputStreamReader in = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            ret = read(in);

        } catch (final Exception ex) {
            ret = false;
            P2Log.errorLog(951254698, ex);
        }
        PDuration.counterStop("readConfiguration");
        return ret;
    }

    boolean read(InputStreamReader in) {
        boolean ret;
        XMLStreamReader parser = null;
        try {
            parser = inFactory.createXMLStreamReader(in);

            nextTag:
            while (parser.hasNext()) {
                final int event = parser.next();
                if (event != XMLStreamConstants.START_ELEMENT) {
                    continue nextTag;
                }

                String xmlElem = parser.getLocalName();
                if (xmlElem.equals("Abonnement")) {
                    //dann kommen die Abos
                    if (getAbos(parser)) {
                        continue nextTag;
                    }
                }
                if (xmlElem.equals("Blacklist")) {
                    //dann kommen die Blacks
                    if (getBlackList(parser)) {
                        continue nextTag;
                    }
                }

                continue nextTag;
            }
            ret = true;

        } catch (final Exception ex) {
            ret = false;
            P2Log.errorLog(915263478, ex);
        } finally {
            try {
                if (parser != null) {
                    parser.close();
                }
            } catch (final Exception ignored) {
            }
        }
        return ret;
    }

    private boolean getAbos(XMLStreamReader parser) {
        boolean ret = false;
        AboData aboData = new AboData();
        aboList.add(aboData);
        String xmlElem = parser.getLocalName(); //von der "Umrandung"
        try {
            while (parser.hasNext()) {
                final int event = parser.next();

                if (event == XMLStreamConstants.END_ELEMENT && parser.getLocalName().equals(xmlElem)) {
                    //dann ist die "Umrandung" beendet
                    break;
                }

                if (event != XMLStreamConstants.START_ELEMENT) {
                    //Inhalt geht wieder mit einem Startelement los
                    continue;
                }

                final String localName = parser.getLocalName();
                final String value = parser.getElementText();

                switch (localName) {
                    case "aktiv":
                        if (value.equals("true")) {
                            aboData.setActive(true);
                        } else {
                            aboData.setActive(false);
                        }
                        break;
                    case "Name":
                        aboData.setName("MV: " + value);
                        break;
                    case "Sender":
                        aboData.setChannel(value);
                        break;
                    case "Thema":
                        aboData.setTheme(value);
                        aboData.setThemeExact(false);
                        break;
                    case "Titel":
                        aboData.setTitle(value);
                        break;
                    case "Thema-Titel":
                        aboData.setThemeTitle(value);
                        break;
                    case "Irgendwo":
                        aboData.setSomewhere(value);
                        break;
                    case "Mindestdauer":
                        try {
                            aboData.setMinDurationMinute(Integer.parseInt(value));
                        } catch (Exception ex) {
                            aboData.setMinDurationMinute(FilterCheck.FILTER_ALL_OR_MIN);
                        }
                        break;
                    case "min_max":
                        if (value.equals("false")) {
                            //dann Maxdauer: Umbauen
                            aboData.setMaxDurationMinute(aboData.getMinDurationMinute());
                            aboData.setMinDurationMinute(FilterCheck.FILTER_ALL_OR_MIN);
                        }
                        break;
                    case "Zielpfad":
                        aboData.setAboSubDir(value);
                        break;
                    case "letztes_Abo":
                        aboData.setDate(value, "");
                        break;
                    case "Programmset":
                        aboData.setSetData(ProgData.getInstance().setDataList.getSetDataForAbo());
                        break;

                }
            }
            ret = true;

        } catch (final Exception ex) {
            P2Log.errorLog(102365494, ex);
        }
        return ret;
    }

    private boolean getBlackList(XMLStreamReader parser) {
        boolean ret = false;
        BlackData blackData = new BlackData();
        blackList.add(blackData);
        blackData.setThemeExact(false); // ist schöner

        String xmlElem = parser.getLocalName(); // von der "Umrandung"
        try {
            while (parser.hasNext()) {
                final int event = parser.next();

                if (event == XMLStreamConstants.END_ELEMENT && parser.getLocalName().equals(xmlElem)) {
                    // dann ist die "Umrandung" beendet
                    break;
                }

                if (event != XMLStreamConstants.START_ELEMENT) {
                    // Inhalt geht wieder mit einem Startelement los
                    continue;
                }

                final String localName = parser.getLocalName();
                final String value = parser.getElementText();

                switch (localName) {
                    case "black-sender":
                        blackData.setChannel(value);
                        break;
                    case "black-thema":
                        blackData.setTheme(value);
                        blackData.setThemeExact(true);
                        break;
                    case "black-titel":
                        blackData.setTitle(value);
                        break;
                    case "black-thema-titel":
                        blackData.setThemeTitle(value);
                        break;
                }
            }
            ret = true;

        } catch (final Exception ex) {
            P2Log.errorLog(102365494, ex);
        }
        return ret;
    }

    @Override
    public void close() throws Exception {
    }
}
