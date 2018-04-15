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

import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.gui.dialog.MTAlert;
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

public class WriteMediaDb implements AutoCloseable {

    private XMLStreamWriter writer = null;
    private OutputStreamWriter out = null;
    private Path xmlFilePath = null;
    private OutputStream os = null;
    private final ArrayList<String> list = new ArrayList<>();
    private MediaDbList mediaDbList;

    public WriteMediaDb() {
    }

    public synchronized void datenSchreiben(Path file, MediaDbList mediaDbList) {
        try {

            this.mediaDbList = mediaDbList;
            xmlFilePath = file;
            list.add("Medien schreiben nach: " + xmlFilePath.toString());

            xmlDatenSchreiben();

        } catch (final Exception ex) {
            list.add("Fehler, nicht geschrieben!");
            PLog.errorLog(656328109, ex);
            Platform.runLater(() -> new MTAlert().showErrorAlert("Fehler beim Schreiben",
                    "Die Mediensammlung konnte nicht geschrieben werden:\n" +
                            file.toString()));
        }

        PLog.userLog(list);
    }

    private void xmlDatenSchreiben() throws XMLStreamException, IOException {
        xmlWriteStart();

        writer.writeCharacters("\n\n");
        writer.writeComment("MediaDb");
        writer.writeCharacters("\n");
        xmlWrite();
        writer.writeCharacters("\n\n");

        xmlWriteEnd();
    }

    private void xmlWriteStart() throws IOException, XMLStreamException {
        list.add("Start Schreiben nach: " + xmlFilePath.toAbsolutePath());
        os = Files.newOutputStream(xmlFilePath);
        out = new OutputStreamWriter(os, StandardCharsets.UTF_8);

        final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
        writer = outFactory.createXMLStreamWriter(out);
        writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
        writer.writeCharacters("\n");// neue Zeile
        writer.writeStartElement(Const.XML_START);
        writer.writeCharacters("\n");// neue Zeile
    }

    private void xmlWrite() throws XMLStreamException {
        // Filme schreiben
        for (final MediaDbData mediaDbData : mediaDbList) {
            mediaDbData.setXmlFromProps();
            xmlSchreibenDaten(MediaDbData.TAG, MediaDbData.XML_NAMES, mediaDbData.arr, true);
        }
    }

    private void xmlSchreibenDaten(String xmlName, String[] xmlSpalten, String[] datenArray, boolean newLine) throws XMLStreamException {
        final int xmlMax = datenArray.length;
        writer.writeStartElement(xmlName);
        if (newLine) {
            writer.writeCharacters("\n"); // neue Zeile
        }
        for (int i = 0; i < xmlMax; ++i) {
            if (!datenArray[i].isEmpty()) {
                if (newLine) {
                    writer.writeCharacters("\t"); // Tab
                }
                writer.writeStartElement(xmlSpalten[i]);
                writer.writeCharacters(datenArray[i]);
                writer.writeEndElement();
                if (newLine) {
                    writer.writeCharacters("\n"); // neue Zeile
                }
            }
        }
        writer.writeEndElement();
        writer.writeCharacters("\n"); // neue Zeile
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
