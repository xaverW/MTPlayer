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

package de.p2tools.mtplayer.controller;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.data.BlackData;
import de.p2tools.mtplayer.controller.data.ProgramData;
import de.p2tools.mtplayer.controller.data.ReplaceData;
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.controller.data.abo.Abo;
import de.p2tools.mtplayer.controller.data.abo.AboXml;
import de.p2tools.mtplayer.controller.data.download.Download;
import de.p2tools.mtplayer.controller.data.download.DownloadXml;
import de.p2tools.mtplayer.controller.filmlist.filmlistUrls.FilmlistUrlData;
import de.p2tools.mtplayer.controller.mediaDb.MediaCollectionData;
import de.p2tools.mtplayer.tools.storedFilter.FilterToXml;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilter;
import de.p2tools.mtplayer.tools.storedFilter.StoredFilters;
import de.p2tools.p2Lib.P2LibConst;
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
        PLog.sysLog(list);
    }

    private void xmlWriteData() {
        try {
            xmlWriteStart();

            writer.writeCharacters(P2LibConst.LINE_SEPARATORx2);
            writer.writeComment("Abos");
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
            xmlWriteAbo();

            writer.writeCharacters(P2LibConst.LINE_SEPARATORx2);
            writer.writeComment("Blacklist");
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
            xmlWriteBlackList();

            writer.writeCharacters(P2LibConst.LINE_SEPARATORx2);
            writer.writeComment("Filter-Film");
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
            xmlWriteFilterFilm();

            writer.writeCharacters(P2LibConst.LINE_SEPARATORx2);
            writer.writeComment(ProgConfig.PARAMETER_INFO);
            writer.writeCharacters(P2LibConst.LINE_SEPARATORx2);
            writer.writeComment("Programmeinstellungen");
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
            xmlWriteConfig(ProgConfig.SYSTEM, ProgConfig.getAll());
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR);

            writer.writeCharacters(P2LibConst.LINE_SEPARATORx2);
            writer.writeComment("Programmsets");
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
            xmlWriteSetData();

            writer.writeCharacters(P2LibConst.LINE_SEPARATORx2);
            writer.writeComment("Ersetzungstabelle");
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
            xmlWriteReplaceData();

            writer.writeCharacters(P2LibConst.LINE_SEPARATORx2);
            writer.writeComment("Downloads");
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
            xmlWriteDownloads();

            writer.writeCharacters(P2LibConst.LINE_SEPARATORx2);
            writer.writeComment("Pfade MedienDB");
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
            xmlWriteMediaPath();

            writer.writeCharacters(P2LibConst.LINE_SEPARATORx2);
            writer.writeComment("Update Filmliste");
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
            xmlWriteFilmUpdateServer();

            writer.writeCharacters(P2LibConst.LINE_SEPARATORx2);
            xmlWriteEnd();
        } catch (final Exception ex) {
            PLog.errorLog(312147896, ex);
        }
    }

    private void xmlWriteStart() throws IOException, XMLStreamException {
        list.add("Start Schreiben nach: " + xmlFilePath.toAbsolutePath());
        os = Files.newOutputStream(xmlFilePath);
        out = new OutputStreamWriter(os, StandardCharsets.UTF_8);

        final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
        writer = outFactory.createXMLStreamWriter(out);
        writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
        writer.writeCharacters(P2LibConst.LINE_SEPARATOR);// neue Zeile
        writer.writeStartElement(ProgConst.XML_START);
        writer.writeCharacters(P2LibConst.LINE_SEPARATOR);// neue Zeile
    }

    private void xmlWriteReplaceData() {
        progData.replaceList.stream().forEach(replaceData -> {
            replaceData.setXmlFromProps();
            xmlWriteData(ReplaceData.TAG, ReplaceData.COLUMN_NAMES, replaceData.arr, false);
        });
    }

    private void xmlWriteSetData() {
        // Proggruppen schreiben, bei Konfig-Datei
        for (final SetData setData : progData.setDataList) {
            setData.setXmlFromProps();
            xmlWriteData(SetData.TAG, SetData.XML_NAMES, setData.arr, false);
            for (final ProgramData programData : setData.getProgramList()) {
                programData.setXmlFromProps();
                xmlWriteData(ProgramData.TAG, ProgramData.XML_NAMES, programData.arr, false);
            }
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
        for (final MediaCollectionData mp : progData.mediaCollectionDataList) {
            mp.setXmlFromProps();
            xmlWriteData(MediaCollectionData.TAG, MediaCollectionData.XML_NAMES, mp.arr, false);
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
        final SelectedFilter akt_sf = progData.storedFilters.getActFilterSettings();
        // nur zur Info im Config-File
        akt_sf.setName(StoredFilters.SELECTED_FILTER_NAME);

        writer.writeComment("aktuelle Filtereinstellungen");
        writer.writeCharacters(P2LibConst.LINE_SEPARATOR);

        xmlWriteData(FilterToXml.TAG,
                FilterToXml.getXmlArray(),
                FilterToXml.getValueArray(progData.storedFilters.getActFilterSettings()),
                true);

        writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
        writer.writeComment("gespeicherte Filter");
        writer.writeCharacters(P2LibConst.LINE_SEPARATOR);

        // Liste der Filterprofile
        progData.storedFilters.getStoredFilterList().stream().forEach((sf) -> {
            xmlWriteData(FilterToXml.TAG, FilterToXml.getXmlArray(), FilterToXml.getValueArray(sf), true);
        });
    }

    private void xmlWriteFilmUpdateServer() throws XMLStreamException {
        // FilmUpdate schreiben
        writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
        writer.writeComment("Akt-Filmliste");
        writer.writeCharacters(P2LibConst.LINE_SEPARATOR);

        for (final FilmlistUrlData datenUrlFilmliste : progData.searchFilmListUrls.getFilmlistUrlList_akt()) {
            datenUrlFilmliste.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_SORT_NR] = FilmlistUrlData.SERVER_ART_AKT;
            xmlWriteData(FilmlistUrlData.FILMLIST_UPDATE_SERVER,
                    FilmlistUrlData.FILMLIST_UPDATE_SERVER_COLUMN_NAMES,
                    datenUrlFilmliste.arr,
                    false);
        }

        writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
        writer.writeComment("Diff-Filmliste");
        writer.writeCharacters(P2LibConst.LINE_SEPARATOR);
        for (final FilmlistUrlData datenUrlFilmliste : progData.searchFilmListUrls.getFilmlistUrlList_diff()) {
            datenUrlFilmliste.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_SORT_NR] = FilmlistUrlData.SERVER_ART_DIFF;
            xmlWriteData(FilmlistUrlData.FILMLIST_UPDATE_SERVER,
                    FilmlistUrlData.FILMLIST_UPDATE_SERVER_COLUMN_NAMES,
                    datenUrlFilmliste.arr,
                    false);
        }
    }

    private void xmlWriteData(String xmlName, String[] xmlArray, String[] dataArray, boolean newLine) {
        final int xmlMax = dataArray.length;
        try {
            writer.writeStartElement(xmlName);
            if (newLine) {
                writer.writeCharacters(P2LibConst.LINE_SEPARATOR); // neue Zeile
            }
            for (int i = 0; i < xmlMax; ++i) {
                if (!dataArray[i].isEmpty()) {
                    if (newLine) {
                        writer.writeCharacters("\t"); // Tab
                    }
                    writer.writeStartElement(xmlArray[i]);
                    writer.writeCharacters(dataArray[i]);
                    writer.writeEndElement();
                    if (newLine) {
                        writer.writeCharacters(P2LibConst.LINE_SEPARATOR); // neue Zeile
                    }
                }
            }
            writer.writeEndElement();
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR); // neue Zeile
        } catch (final Exception ex) {
            PLog.errorLog(198325017, ex);
        }
    }

    private void xmlWriteConfig(String xmlName, String[][] xmlArray) {
        try {
            writer.writeStartElement(xmlName);
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR); // neue Zeile

            for (final String[] xmlSpalte : xmlArray) {
                writer.writeCharacters("\t"); // Tab
                writer.writeStartElement(xmlSpalte[0]);
                writer.writeCharacters(xmlSpalte[1]);
                writer.writeEndElement();
                writer.writeCharacters(P2LibConst.LINE_SEPARATOR); // neue Zeile
            }
            writer.writeEndElement();
            writer.writeCharacters(P2LibConst.LINE_SEPARATOR); // neue Zeile
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
