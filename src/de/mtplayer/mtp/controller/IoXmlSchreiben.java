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

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.controller.data.BlackData;
import de.mtplayer.mtp.controller.data.ProgData;
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

public class IoXmlSchreiben implements AutoCloseable {

    private XMLStreamWriter writer = null;
    private OutputStreamWriter out = null;
    private Path xmlFilePath = null;
    private OutputStream os = null;
    private Daten daten = null;
    private final ArrayList<String> list = new ArrayList<>();

    public IoXmlSchreiben(Daten daten) {
        this.daten = daten;
    }

    public synchronized void datenSchreiben() {
        xmlFilePath = new ProgInfos().getXmlFilePath();
        list.add("Daten schreiben nach: " + xmlFilePath.toString());
        xmlDatenSchreiben();
        PLog.userLog(list);
    }

    public synchronized void exportPset(SetData[] pSet, String datei) {
        try {
            xmlFilePath = Paths.get(datei);
            PLog.userLog("Pset exportieren nach: " + xmlFilePath.toString());
            xmlSchreibenStart();
            xmlSchreibenPset(pSet);
            xmlSchreibenEnde();
        } catch (final Exception ex) {
            PLog.errorLog(392846204, ex, "nach: " + datei);
        }
    }

    private void xmlDatenSchreiben() {
        try {
            xmlSchreibenStart();

            writer.writeCharacters("\n\n");
            writer.writeComment("Abos");
            writer.writeCharacters("\n");
            xmlSchreibenAbo();

            writer.writeCharacters("\n\n");
            writer.writeComment("Blacklist");
            writer.writeCharacters("\n");
            xmlSchreibenBlackList();

            writer.writeCharacters("\n\n");
            writer.writeComment("Filter-Film");
            writer.writeCharacters("\n");
            xmlSchreibenFilterFilm();

            writer.writeCharacters("\n\n");
            writer.writeComment(Config.PARAMETER_INFO);
            writer.writeCharacters("\n\n");
            writer.writeComment("Programmeinstellungen");
            writer.writeCharacters("\n");
            xmlSchreibenConfig(Config.SYSTEM, Config.getAll());
            writer.writeCharacters("\n");

            writer.writeCharacters("\n\n");
            writer.writeComment("Programmsets");
            writer.writeCharacters("\n");
            xmlSchreibenProg();

            writer.writeCharacters("\n\n");
            writer.writeComment("Ersetzungstabelle");
            writer.writeCharacters("\n");
            xmlWriteReplaceData();

            writer.writeCharacters("\n\n");
            writer.writeComment("Downloads");
            writer.writeCharacters("\n");
            xmlSchreibenDownloads();

            writer.writeCharacters("\n\n");
            writer.writeComment("Pfade MedienDB");
            writer.writeCharacters("\n");
            xmlSchreibenMediaPath();

            writer.writeCharacters("\n\n");
            writer.writeComment("Update Filmliste");
            writer.writeCharacters("\n");
            xmlSchreibenFilmUpdateServer();

            writer.writeCharacters("\n\n");
            xmlSchreibenEnde();
        } catch (final Exception ex) {
            PLog.errorLog(656328109, ex);
        }
    }

    private void xmlSchreibenStart() throws IOException, XMLStreamException {
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

    private void xmlWriteReplaceData() {
        daten.replaceList.stream().forEach(replaceData -> {
            replaceData.setXmlFromProps();
            xmlSchreibenDaten(ReplaceData.TAG, ReplaceData.COLUMN_NAMES, replaceData.arr, false);
        });
    }

    private void xmlSchreibenProg() {
        // Proggruppen schreiben, bei Konfig-Datei
        for (final SetData psetData : daten.setList) {
            psetData.setXmlFromProps();
            xmlSchreibenDaten(SetData.TAG, SetData.XML_NAMES, psetData.arr, false);
            for (final ProgData progData : psetData.getProgList()) {
                progData.setXmlFromProps();
                xmlSchreibenDaten(ProgData.TAG, ProgData.XML_NAMES, progData.arr, false);
            }
        }
    }

    private void xmlSchreibenPset(SetData[] psetArray) throws XMLStreamException {
        // wird beim Export Sets verwendete
        writer.writeCharacters("\n\n");
        for (final SetData pset : psetArray) {
            pset.setXmlFromProps();
            xmlSchreibenDaten(SetData.TAG, SetData.XML_NAMES, pset.arr, true);
            for (final ProgData progData : pset.getProgList()) {
                progData.setXmlFromProps();
                xmlSchreibenDaten(ProgData.TAG, ProgData.XML_NAMES, progData.arr, true);
            }
            writer.writeCharacters("\n\n");
        }
    }

    private void xmlSchreibenDownloads() {
        // Downloads schreiben
        for (final Download download : daten.downloadList) {
            if (download.isStateStoped()) {
                // unterbrochene werden gespeichert, dass die Info "Interrupt" erhalten bleibt
                download.setXmlFromProps();
                xmlSchreibenDaten(DownloadXml.TAG, DownloadXml.XML_NAMES, download.arr, false);
            } else if (!download.isAbo() && !download.isStateFinished()) {
                // Download, (Abo müssen neu angelegt werden)
                download.setXmlFromProps();
                xmlSchreibenDaten(DownloadXml.TAG, DownloadXml.XML_NAMES, download.arr, false);
            }
        }
    }

    private void xmlSchreibenAbo() {
        // Abo schreiben
        for (final Abo abo : daten.aboList) {
            abo.setXmlFromProps();
            xmlSchreibenDaten(AboXml.TAG, AboXml.XML_NAMES, abo.arr, false);
        }
    }

    private void xmlSchreibenMediaPath() {
        // Pfade der MedienDB schreiben
        for (final MediaPathData mp : daten.mediaPathList) {
            mp.setXmlFromProps();
            xmlSchreibenDaten(MediaPathData.TAG, MediaPathData.XML_NAMES, mp.arr, false);
        }
    }

    private void xmlSchreibenBlackList() {
        // Blacklist schreiben
        for (final BlackData blacklist : daten.blackList) {
            blacklist.setXmlFromProps();
            xmlSchreibenDaten(BlackData.TAG, BlackData.XML_NAMES, blacklist.arr, false);
        }
    }

    private void xmlSchreibenFilterFilm() throws XMLStreamException {
        // Filter schreiben, aktuellen
        final SelectedFilter akt_sf = daten.storedFilter.getSelectedFilter();
        // nur zur Info im Config-File
        akt_sf.setName(StoredFilter.SELECTED_FILTER_NAME);

        writer.writeComment("aktuelle Filtereinstellungen");
        writer.writeCharacters("\n");

        xmlSchreibenDaten(FilterToXml.TAG,
                FilterToXml.getXmlArray(),
                FilterToXml.getValueArray(daten.storedFilter.getSelectedFilter()),
                true);

        writer.writeCharacters("\n");
        writer.writeComment("gespeicherte Filter");
        writer.writeCharacters("\n");
        // Liste der Filterprofile
        daten.storedFilter.getStordeFilterList().stream().forEach((sf) -> {
            xmlSchreibenDaten(FilterToXml.TAG, FilterToXml.getXmlArray(), FilterToXml.getValueArray(sf), true);
        });
    }

    private void xmlSchreibenFilmUpdateServer() throws XMLStreamException {
        // FilmUpdate schreiben
        writer.writeCharacters("\n");
        writer.writeComment("Akt-Filmliste");
        writer.writeCharacters("\n");

        for (final FilmlistUrlData datenUrlFilmliste : daten.loadFilmlist.getDownloadUrlsFilmlisten_akt()) {
            datenUrlFilmliste.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_ART_NR] = FilmlistUrlData.SERVER_ART_AKT;
            xmlSchreibenDaten(FilmlistUrlData.FILMLIST_UPDATE_SERVER,
                    FilmlistUrlData.FILMLIST_UPDATE_SERVER_COLUMN_NAMES,
                    datenUrlFilmliste.arr,
                    false);
        }

        writer.writeCharacters("\n");
        writer.writeComment("Diff-Filmliste");
        writer.writeCharacters("\n");
        for (final FilmlistUrlData datenUrlFilmliste : daten.loadFilmlist.getDownloadUrlsFilmlisten_diff()) {
            datenUrlFilmliste.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_ART_NR] = FilmlistUrlData.SERVER_ART_DIFF;
            xmlSchreibenDaten(FilmlistUrlData.FILMLIST_UPDATE_SERVER,
                    FilmlistUrlData.FILMLIST_UPDATE_SERVER_COLUMN_NAMES,
                    datenUrlFilmliste.arr,
                    false);
        }
    }

    private void xmlSchreibenDaten(String xmlName, String[] xmlSpalten, String[] datenArray, boolean newLine) {
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

    private void xmlSchreibenConfig(String xmlName, String[][] xmlSpalten) {
        try {
            writer.writeStartElement(xmlName);
            writer.writeCharacters("\n"); // neue Zeile

            for (final String[] xmlSpalte : xmlSpalten) {
//                if (!Config.find(xmlSpalte[0])) {
//                    continue; // nur Configs schreiben die es noch gibt
//                }
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

    private void xmlSchreibenEnde() throws Exception {
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
