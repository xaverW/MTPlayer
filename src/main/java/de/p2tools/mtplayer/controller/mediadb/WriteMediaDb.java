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
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WriteMediaDb implements AutoCloseable {

    private XMLStreamWriter writer = null;
    private OutputStreamWriter out = null;
    private Path xmlFilePath = null;
    private OutputStream os = null;
    private ArrayList<String> logList = new ArrayList<>();
    boolean writeLog = false;
    private List<MediaData> mediaDbList;
    private final ProgData progData;

    public WriteMediaDb(ProgData progData) {
        this.progData = progData;
    }

    public synchronized void writeExternalMediaData() {
        writeLog = true;
        writeExternalMediaData(logList);
    }

    public synchronized void writeExternalMediaData(ArrayList<String> logList) {
        final Path path = getPathMediaDB();
        this.logList = logList;
        logList.add("MediaDB (extern) schreiben");
        logList.add("   --> Schreiben nach: " + path.toString());

        try {
            final File file = path.toFile();
            final File dir = new File(file.getParent());
            if (!dir.exists() && !dir.mkdirs()) {
                PLog.errorLog(932102478, "Kann den Pfad nicht anlegen: " + dir.toString());
                Platform.runLater(() -> PAlert.showErrorAlert("Fehler beim Schreiben",
                        "Der Pfad zum Schreiben der Mediensammlung kann nicht angelegt werden: " + P2LibConst.LINE_SEPARATOR +
                                path.toString()));
                return;
            }

            List<MediaData> externalMediaData = getExternalMediaData();
            logList.add("   --> Anzahl externe Medien: " + externalMediaData.size());
            write(path, externalMediaData);
            logList.add("   --> geschrieben!");

        } catch (final Exception ex) {
            logList.add("   --> Fehler, nicht geschrieben!");
            PLog.errorLog(931201478, ex, "nach: " + path.toString());
            Platform.runLater(() -> PAlert.showErrorAlert("Fehler beim Schreiben",
                    "Die Mediensammlung konnte nicht geschrieben werden:" + P2LibConst.LINE_SEPARATOR +
                            path.toString()));
        }

        if (writeLog) {
            PLog.sysLog(logList);
        }
    }

    private synchronized void write(Path file, List<MediaData> mediaDbList) {
        try {
            this.mediaDbList = mediaDbList;
            xmlFilePath = file;
            writeXmlData();
        } catch (final Exception ex) {
            logList.add("Fehler, nicht geschrieben!");
            PLog.errorLog(656328109, ex);
            Platform.runLater(() -> PAlert.showErrorAlert("Fehler beim Schreiben",
                    "Die Mediensammlung konnte nicht geschrieben werden:" + P2LibConst.LINE_SEPARATOR +
                            file.toString()));
        }
    }

    // ******************************************************
    // EXTERNAL MediaData aus File lesen und schreiben
    private Path getPathMediaDB() {
        Path urlPath = null;
        try {
            urlPath = Paths.get(ProgInfos.getSettingsDirectory_String()).resolve(ProgConst.FILE_MEDIA_DB);
            if (Files.notExists(urlPath)) {
                urlPath = Files.createFile(urlPath);
            }
        } catch (final IOException ex) {
            PLog.errorLog(951201201, ex);
        }
        return urlPath;
    }

    private synchronized List<MediaData> getExternalMediaData() {
        return progData.mediaDataList.parallelStream().filter(mediaData -> mediaData != null && mediaData.isExternal())
                .collect(Collectors.toList());
    }

    private void writeXmlData() throws XMLStreamException, IOException {
        xmlWriteStart();
        writer.writeCharacters(P2LibConst.LINE_SEPARATORx2);
        writer.writeComment("MediaDb");
        writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
        xmlWrite();
        writer.writeCharacters(P2LibConst.LINE_SEPARATORx2);
        xmlWriteEnd();
    }

    private void xmlWriteStart() throws IOException, XMLStreamException {
        os = Files.newOutputStream(xmlFilePath);
        out = new OutputStreamWriter(os, StandardCharsets.UTF_8);

        final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
        writer = outFactory.createXMLStreamWriter(out);
        writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
        writer.writeCharacters(P2LibConst.LINE_SEPARATOR); // neue Zeile
        writer.writeStartElement(P2LibConst.CONFIG_XML_START);
        writer.writeCharacters(P2LibConst.LINE_SEPARATOR); // neue Zeile
    }

    private void xmlWrite() throws XMLStreamException {
        // Filme schreiben
        for (final MediaData mediaData : mediaDbList) {
            String[] arr = mediaData.setXmlFromProps();
            if (mediaData.isExternal()) {
                xmlWrite(MediaData.TAG, MediaData.XML_NAMES, arr, true);
            }
        }
    }

    private void xmlWrite(String xmlName, String[] xmlColumn, String[] dataArray, boolean newLine) throws XMLStreamException {
        final int xmlMax = dataArray.length;
        writer.writeStartElement(xmlName);
        if (newLine) {
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR); // neue Zeile
        }
        for (int i = 0; i < xmlMax; ++i) {
            if (!dataArray[i].isEmpty()) {
                if (newLine) {
                    writer.writeCharacters("\t"); // Tab
                }
                writer.writeStartElement(xmlColumn[i]);
                writer.writeCharacters(dataArray[i]);
                writer.writeEndElement();
                if (newLine) {
                    writer.writeCharacters(P2LibConst.LINE_SEPARATOR); // neue Zeile
                }
            }
        }
        writer.writeEndElement();
        writer.writeCharacters(P2LibConst.LINE_SEPARATOR); // neue Zeile
    }


    private void xmlWriteEnd() throws XMLStreamException {
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
    }

    @Override
    public void close() throws XMLStreamException, IOException {
        writer.close();
        out.close();
        os.close();
    }
}
