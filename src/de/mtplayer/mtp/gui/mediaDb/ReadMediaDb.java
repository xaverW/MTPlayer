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


package de.mtplayer.mtp.gui.mediaDb;

import de.mtplayer.mtp.controller.config.Daten;
import de.p2tools.p2Lib.tools.log.Duration;
import de.p2tools.p2Lib.tools.log.PLog;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ReadMediaDb implements AutoCloseable {

    private XMLInputFactory inFactory;
    private Daten daten;
    private ArrayList<MediaDbData> list;

    public ReadMediaDb(Daten daten) {
        this.daten = daten;
        this.list = new ArrayList<>();

        inFactory = XMLInputFactory.newInstance();
        inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
    }

    public ArrayList<MediaDbData> read(Path xmlFilePath) {

        if (!Files.exists(xmlFilePath)) {
            return list;
        }

        Duration.counterStart("Konfig lesen");
        XMLStreamReader parser = null;

        try (InputStream is = Files.newInputStream(xmlFilePath);
             InputStreamReader in = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            parser = inFactory.createXMLStreamReader(in);
            while (parser.hasNext()) {
                final int event = parser.next();
                if (event != XMLStreamConstants.START_ELEMENT) {
                    continue;
                }
                if (!parser.getLocalName().equals(MediaDbData.TAG)) {
                    continue;
                }

                final MediaDbData mediaDbData = new MediaDbData();
                if (get(parser, MediaDbData.TAG, MediaDbData.XML_NAMES, mediaDbData.arr)) {
                    mediaDbData.setPropsFromXml();
                    list.add(mediaDbData);
                }

            }
        } catch (final Exception ex) {
            list.clear();
            PLog.errorLog(936251078, ex);
        } finally {
            try {
                if (parser != null) {
                    parser.close();
                }
            } catch (final Exception ignored) {
            }
        }

        Duration.counterStop("Konfig lesen");
        return list;
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
            PLog.errorLog(912036578, ex);
        }
        return ret;
    }


    @Override
    public void close() throws Exception {

    }

}
