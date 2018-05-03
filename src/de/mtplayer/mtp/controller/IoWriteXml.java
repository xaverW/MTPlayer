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

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.controller.data.BlackData;
import de.mtplayer.mtp.controller.data.ReplaceData;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.controller.data.abo.AboXml;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadXml;
import de.mtplayer.mtp.controller.filmlist.filmlistUrls.FilmlistUrlData;
import de.mtplayer.mtp.controller.mediaDb.MediaPathData;
import de.mtplayer.mtp.tools.storedFilter.FilterToXml;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import de.mtplayer.mtp.tools.storedFilter.StoredFilter;
import de.p2tools.p2Lib.tools.log.PLog;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class IoWriteXml implements AutoCloseable {

    private XMLStreamWriter writer = null;
    private OutputStreamWriter out = null;
    private Path xmlFilePath = null;
    private OutputStream os = null;
    private ProgData progData = null;
    private final ArrayList<String> list = new ArrayList<>();

    public IoWriteXml(ProgData progData) {
        this.progData = progData;
    }

    public synchronized void writeData() {
        xmlFilePath = new ProgInfos().getSettingsFile();
        list.add("Daten schreiben nach: " + xmlFilePath.toString());
        xmlWriteData();
        PLog.userLog(list);
    }

    public synchronized void exportPset(SetData[] pSet, String datei) {
        try {
            xmlFilePath = Paths.get(datei);
            PLog.userLog("Pset exportieren nach: " + xmlFilePath.toString());
            xmlWriteStart();
            xmlWritePset(pSet);
            xmlWriteEnd();
        } catch (final Exception ex) {
            PLog.errorLog(392846204, ex, "nach: " + datei);
        }
    }

    private void xmlWriteData() {
        try {
            xmlWriteStart();

            writer.writeCharacters("\n\n");
            writer.writeComment("Abos");
            writer.writeCharacters("\n");
            xmlWriteAbo();

            writer.writeCharacters("\n\n");
            writer.writeComment("Blacklist");
            writer.writeCharacters("\n");
            xmlWriteBlackList();

            writer.writeCharacters("\n\n");
            writer.writeComment("Filter-Film");
            writer.writeCharacters("\n");
            xmlWriteFilterFilm();

            writer.writeCharacters("\n\n");
            writer.writeComment(ProgConfig.PARAMETER_INFO);
            writer.writeCharacters("\n\n");
            writer.writeComment("Programmeinstellungen");
            writer.writeCharacters("\n");
            xmlWriteConfig(ProgConfig.SYSTEM, ProgConfig.getAll());
            writer.writeCharacters("\n");

            writer.writeCharacters("\n\n");
            writer.writeComment("Programmsets");
            writer.writeCharacters("\n");
            xmlWriteProg();

            writer.writeCharacters("\n\n");
            writer.writeComment("Ersetzungstabelle");
            writer.writeCharacters("\n");
            xmlWriteReplaceData();

            writer.writeCharacters("\n\n");
            writer.writeComment("Downloads");
            writer.writeCharacters("\n");
            xmlWriteDownloads();

            writer.writeCharacters("\n\n");
            writer.writeComment("Pfade MedienDB");
            writer.writeCharacters("\n");
            xmlWriteMediaPath();

            writer.writeCharacters("\n\n");
            writer.writeComment("Update Filmliste");
            writer.writeCharacters("\n");
            xmlWriteFilmUpdateServer();

            writer.writeCharacters("\n\n");
            xmlWriteEnd();
        } catch (final Exception ex) {
            PLog.errorLog(656328109, ex);
        }
    }

    private void xmlWriteStart() throws IOException, XMLStreamException {
        list.add("Start Schreiben nach: " + xmlFilePath.toAbsolutePath());
        os = Files.newOutputStream(xmlFilePath);
        out = new OutputStreamWriter(os, StandardCharsets.UTF_8);

        final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
        writer = outFactory.createXMLStreamWriter(out);
        writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
        writer.writeCharacters("\n");// neue Zeile
        writer.writeStartElement(ProgConst.XML_START);
        writer.writeCharacters("\n");// neue Zeile
    }

    private void xmlWriteReplaceData() {
        progData.replaceList.stream().forEach(replaceData -> {
            replaceData.setXmlFromProps();
            xmlWriteData(ReplaceData.TAG, ReplaceData.COLUMN_NAMES, replaceData.arr, false);
        });
    }

    private void xmlWriteProg() {
        // Proggruppen schreiben, bei Konfig-Datei
        for (final SetData psetData : progData.setList) {
            psetData.setXmlFromProps();
            xmlWriteData(SetData.TAG, SetData.XML_NAMES, psetData.arr, false);
            for (final de.mtplayer.mtp.controller.data.ProgData progData : psetData.getProgList()) {
                progData.setXmlFromProps();
                xmlWriteData(de.mtplayer.mtp.controller.data.ProgData.TAG, de.mtplayer.mtp.controller.data.ProgData.XML_NAMES, progData.arr, false);
            }
        }
    }

    private void xmlWritePset(SetData[] psetArray) throws XMLStreamException {
        // wird beim Export Sets verwendete
        writer.writeCharacters("\n\n");
        for (final SetData pset : psetArray) {
            pset.setXmlFromProps();
            xmlWriteData(SetData.TAG, SetData.XML_NAMES, pset.arr, true);
            for (final de.mtplayer.mtp.controller.data.ProgData progData : pset.getProgList()) {
                progData.setXmlFromProps();
                xmlWriteData(de.mtplayer.mtp.controller.data.ProgData.TAG, de.mtplayer.mtp.controller.data.ProgData.XML_NAMES, progData.arr, true);
            }
            writer.writeCharacters("\n\n");
        }
    }

    private void xmlWriteDownloads() {
        // Downloads schreiben
        for (final Download download : progData.downloadList) {
            if (download.isStateStoped()) {
                // unterbrochene werden gespeichert, dass die Info "Interrupt" erhalten bleibt
                download.setXmlFromProps();
                xmlWriteData(DownloadXml.TAG, DownloadXml.XML_NAMES, download.arr, false);
            } else if (!download.isAbo() && !download.isStateFinished()) {
                // Download, (Abo müssen neu angelegt werden)
                download.setXmlFromProps();
                xmlWriteData(DownloadXml.TAG, DownloadXml.XML_NAMES, download.arr, false);
            }
        }
    }

    private void xmlWriteAbo() {
        // Abo schreiben
        for (final Abo abo : progData.aboList) {
            abo.setXmlFromProps();
            xmlWriteData(AboXml.TAG, AboXml.XML_NAMES, abo.arr, false);
        }
    }

    private void xmlWriteMediaPath() {
        // Pfade der MedienDB schreiben
        for (final MediaPathData mp : progData.mediaPathList) {
            mp.setXmlFromProps();
            xmlWriteData(MediaPathData.TAG, MediaPathData.XML_NAMES, mp.arr, false);
        }
    }

    private void xmlWriteBlackList() {
        // Blacklist schreiben
        for (final BlackData blacklist : progData.blackList) {
            blacklist.setXmlFromProps();
            xmlWriteData(BlackData.TAG, BlackData.XML_NAMES, blacklist.arr, false);
        }
    }

    private void xmlWriteFilterFilm() throws XMLStreamException {
        // Filter schreiben, aktuellen
        final SelectedFilter akt_sf = progData.storedFilter.getSelectedFilter();
        // nur zur Info im Config-File
        akt_sf.setName(StoredFilter.SELECTED_FILTER_NAME);

        writer.writeComment("aktuelle Filtereinstellungen");
        writer.writeCharacters("\n");

        xmlWriteData(FilterToXml.TAG,
                FilterToXml.getXmlArray(),
                FilterToXml.getValueArray(progData.storedFilter.getSelectedFilter()),
                true);

        writer.writeCharacters("\n");
        writer.writeComment("gespeicherte Filter");
        writer.writeCharacters("\n");
        // Liste der Filterprofile
        progData.storedFilter.getStordeFilterList().stream().forEach((sf) -> {
            xmlWriteData(FilterToXml.TAG, FilterToXml.getXmlArray(), FilterToXml.getValueArray(sf), true);
        });
    }

    private void xmlWriteFilmUpdateServer() throws XMLStreamException {
        // FilmUpdate schreiben
        writer.writeCharacters("\n");
        writer.writeComment("Akt-Filmliste");
        writer.writeCharacters("\n");

        for (final FilmlistUrlData datenUrlFilmliste : progData.loadFilmlist.getDownloadUrlsFilmlisten_akt()) {
            datenUrlFilmliste.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_ART_NR] = FilmlistUrlData.SERVER_ART_AKT;
            xmlWriteData(FilmlistUrlData.FILMLIST_UPDATE_SERVER,
                    FilmlistUrlData.FILMLIST_UPDATE_SERVER_COLUMN_NAMES,
                    datenUrlFilmliste.arr,
                    false);
        }

        writer.writeCharacters("\n");
        writer.writeComment("Diff-Filmliste");
        writer.writeCharacters("\n");
        for (final FilmlistUrlData datenUrlFilmliste : progData.loadFilmlist.getDownloadUrlsFilmlisten_diff()) {
            datenUrlFilmliste.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_ART_NR] = FilmlistUrlData.SERVER_ART_DIFF;
            xmlWriteData(FilmlistUrlData.FILMLIST_UPDATE_SERVER,
                    FilmlistUrlData.FILMLIST_UPDATE_SERVER_COLUMN_NAMES,
                    datenUrlFilmliste.arr,
                    false);
        }
    }

    private void xmlWriteData(String xmlName, String[] xmlSpalten, String[] datenArray, boolean newLine) {
        final int xmlMax = datenArray.length;
        try {
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
        } catch (final Exception ex) {
            PLog.errorLog(198325017, ex);
        }
    }

    private void xmlWriteConfig(String xmlName, String[][] xmlSpalten) {
        try {
            writer.writeStartElement(xmlName);
            writer.writeCharacters("\n"); // neue Zeile

            for (final String[] xmlSpalte : xmlSpalten) {
                writer.writeCharacters("\t"); // Tab
                writer.writeStartElement(xmlSpalte[0]);
                writer.writeCharacters(xmlSpalte[1]);
                writer.writeEndElement();
                writer.writeCharacters("\n"); // neue Zeile
            }
            writer.writeEndElement();
            writer.writeCharacters("\n"); // neue Zeile
        } catch (final Exception ex) {
            PLog.errorLog(951230478, ex);
        }
    }

    private void xmlWriteEnd() throws Exception {
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();

        list.add("geschrieben!");
    }

    @Override
    public void close() throws Exception {
        writer.close();
        out.close();
        os.close();
    }
}
