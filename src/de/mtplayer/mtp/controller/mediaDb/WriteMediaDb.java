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

package de.mtplayer.mtp.controller.mediaDb;

import de.mtplayer.mtp.controller.config.ProgConst;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WriteMediaDb implements AutoCloseable {

    private XMLStreamWriter writer = null;
    private OutputStreamWriter out = null;
    private Path xmlFilePath = null;
    private OutputStream os = null;
    private final ArrayList<String> list = new ArrayList<>();
    private List<MediaData> mediaDbList;

    public WriteMediaDb() {
    }

    public synchronized void write(Path file, List<MediaData> mediaDbList) {
        try {

            this.mediaDbList = mediaDbList;
            xmlFilePath = file;
            list.add("Medien schreiben nach: " + xmlFilePath.toString());

            writeXmlData();

        } catch (final Exception ex) {
            list.add("Fehler, nicht geschrieben!");
            PLog.errorLog(656328109, ex);
            Platform.runLater(() -> PAlert.showErrorAlert("Fehler beim Schreiben",
                    "Die Mediensammlung konnte nicht geschrieben werden:" + PConst.LINE_SEPARATOR +
                            file.toString()));
        }

        PLog.userLog(list);
    }

    private void writeXmlData() throws XMLStreamException, IOException {
        xmlWriteStart();

        writer.writeCharacters(PConst.LINE_SEPARATORx2);
        writer.writeComment("MediaDb");
        writer.writeCharacters(PConst.LINE_SEPARATOR);
        xmlWrite();
        writer.writeCharacters(PConst.LINE_SEPARATORx2);

        xmlWriteEnd();
    }

    private void xmlWriteStart() throws IOException, XMLStreamException {
        list.add("Start Schreiben nach: " + xmlFilePath.toAbsolutePath());
        os = Files.newOutputStream(xmlFilePath);
        out = new OutputStreamWriter(os, StandardCharsets.UTF_8);

        final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
        writer = outFactory.createXMLStreamWriter(out);
        writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
        writer.writeCharacters(PConst.LINE_SEPARATOR);// neue Zeile
        writer.writeStartElement(ProgConst.XML_START);
        writer.writeCharacters(PConst.LINE_SEPARATOR);// neue Zeile
    }

    private void xmlWrite() throws XMLStreamException {
        // Filme schreiben
        for (final MediaData mediaData : mediaDbList) {
            mediaData.setXmlFromProps();
            if (mediaData.isExternal()) {
                xmlWrite(MediaData.TAG, MediaData.XML_NAMES, mediaData.arr, true);
            }
        }
    }

    private void xmlWrite(String xmlName, String[] xmlColumn, String[] dataArray, boolean newLine) throws XMLStreamException {
        final int xmlMax = dataArray.length;
        writer.writeStartElement(xmlName);
        if (newLine) {
            writer.writeCharacters(PConst.LINE_SEPARATOR); // neue Zeile
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
                    writer.writeCharacters(PConst.LINE_SEPARATOR); // neue Zeile
                }
            }
        }
        writer.writeEndElement();
        writer.writeCharacters(PConst.LINE_SEPARATOR); // neue Zeile
    }


    private void xmlWriteEnd() throws XMLStreamException {
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();

        list.add("geschrieben!");
    }

    @Override
    public void close() throws XMLStreamException, IOException {
        writer.close();
        out.close();
        os.close();
    }
}
