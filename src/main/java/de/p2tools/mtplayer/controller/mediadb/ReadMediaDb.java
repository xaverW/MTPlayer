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


package de.p2tools.mtplayer.controller.mediadb;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReadMediaDb implements AutoCloseable {

    private final XMLInputFactory inFactory;
    private final ArrayList<MediaData> list;

    public ReadMediaDb() {
        this.list = new ArrayList<>();

        inFactory = XMLInputFactory.newInstance();
        inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
    }

    // ******************************************************
    // EXTERNAL MediaData aus File lesen und schreiben
    public List<MediaData> loadSavedExternalMediaData() {
        final Path urlPath = getPathMediaDB();
        return new ReadMediaDb().readDb(urlPath);
    }

    private Path getPathMediaDB() {
        Path urlPath = null;
        try {
            urlPath = Paths.get(ProgInfos.getSettingsDirectory_String()).resolve(ProgConst.FILE_MEDIA_DB);
            if (Files.notExists(urlPath)) {
                urlPath = Files.createFile(urlPath);
            }
        } catch (final IOException ex) {
            P2Log.errorLog(951201201, ex);
        }
        return urlPath;
    }

    public ArrayList<MediaData> readDb(Path xmlFilePath) {

        if (!Files.exists(xmlFilePath) || xmlFilePath.toFile().length() == 0) {
            return list;
        }

        P2Duration.counterStart("readDb");
        XMLStreamReader parser = null;

        try (InputStream is = Files.newInputStream(xmlFilePath);
             InputStreamReader in = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            parser = inFactory.createXMLStreamReader(in);
            while (parser.hasNext()) {
                final int event = parser.next();
                if (event != XMLStreamConstants.START_ELEMENT) {
                    continue;
                }
                if (!parser.getLocalName().equals(MediaData.TAG)) {
                    continue;
                }

                String[] arr = MediaData.getArr();
                if (get(parser, MediaData.TAG, MediaData.XML_NAMES, arr)) {
                    final MediaData mediaData = new MediaData(arr);
                    list.add(mediaData);
                }
            }
        } catch (final Exception ex) {
            list.clear();
            P2Log.errorLog(936251078, ex);
        } finally {
            try {
                if (parser != null) {
                    parser.close();
                }
            } catch (final Exception ignored) {
            }
        }

        P2Duration.counterStop("readDb");
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
            P2Log.errorLog(912036578, ex);
        }
        return ret;
    }

    @Override
    public void close() throws Exception {
    }
}
