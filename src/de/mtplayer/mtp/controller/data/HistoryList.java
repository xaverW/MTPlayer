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

import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.p2tools.p2Lib.tools.log.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class HistoryList extends SimpleListProperty<HistoryData> {

    private final HashSet<String> urlHash = new HashSet<>();
    private final String fileName;
    private final String settingsDir;
    private FilteredList<HistoryData> filteredList = null;
    private SortedList<HistoryData> sortedList = null;
    private boolean inRemove = false;

    public HistoryList(String fileName, String settingsDir) {
        super(FXCollections.observableArrayList());

        this.fileName = fileName;
        this.settingsDir = settingsDir;

        readHistoryDataFromFile();
    }

    public SortedList<HistoryData> getSortedList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<HistoryData> getFilteredList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return filteredList;
    }

    public synchronized void filterdListSetPred(Predicate<HistoryData> predicate) {
        filteredList.setPredicate(predicate);
    }

    public synchronized void filterdListClearPred() {
        filteredList.setPredicate(p -> false);
    }

    public synchronized void clearAll() {
        clearList();
        try {
            final Path urlPath = getUrlFilePath();
            Files.deleteIfExists(urlPath);
        } catch (final IOException ignored) {
        }
    }

    public synchronized boolean checkIfUrlAlreadyIn(String urlFilm) {
        // wenn url gefunden, dann true zurück
        return urlHash.contains(urlFilm);
    }

    public synchronized boolean checkIfLiveStream(String theme) {
        // live ist nie alt
        return theme.equals(FilmTools.THEME_LIVE);
    }

    public synchronized boolean writeHistoryDataToHistory(String theme, String title, String url) {
        if (checkIfUrlAlreadyIn(url) || checkIfLiveStream(theme)) {
            return true;
        }

        PDuration.counterStart("writeDataToHistoryFile");
        final ArrayList<HistoryData> list = new ArrayList<>();
        final String datum = StringFormatters.FORMATTER_ddMMyyyy.format(new Date());
        HistoryData historyData = new HistoryData(datum, theme, title, url);
        addToThisList(historyData);
        list.add(historyData);

        boolean ret = writeHistoryDataToFile(list, true);
        PDuration.counterStop("writeDataToHistoryFile");
        return ret;
    }

    public synchronized boolean writeFilmListToHistory(ArrayList<Film> filmList) {
        final ArrayList<HistoryData> list = new ArrayList<>(filmList.size());
        final String datum = StringFormatters.FORMATTER_ddMMyyyy.format(new Date());

        PDuration.counterStart("writeDataToHistoryFile");
        for (final Film film : filmList) {
            if (checkIfLiveStream(film.getTheme())) {
                continue;
            }

            // auch wenn schon in der History, dann doch den Film als gesehen markieren
            film.setShown(true);
            film.setActHist(true);

            if (checkIfUrlAlreadyIn(film.getUrlHistory())) {
                continue;
            }

            HistoryData historyData = new HistoryData(datum, film.arr[FilmXml.FILM_THEME], film.arr[FilmXml.FILM_TITLE], film.getUrlHistory());
            addToThisList(historyData);
            list.add(historyData);
        }

        boolean ret = writeHistoryDataToFile(list, true);
        PDuration.counterStop("writeDataToHistoryFile");
        return ret;
    }

    public synchronized boolean writeDownloadListToHistory(ArrayList<Download> downloadList) {
        final ArrayList<HistoryData> list = new ArrayList<>(downloadList.size());
        final String datum = StringFormatters.FORMATTER_ddMMyyyy.format(new Date());

        PDuration.counterStart("writeDataToHistoryFile");
        for (final Download download : downloadList) {
            if (checkIfLiveStream(download.getTheme())) {
                continue;
            }

            // auch wenn schon in der History, dann doch den Film als gesehen markieren
            if (download.getFilm() != null) {
                download.getFilm().setShown(true);
                download.getFilm().setActHist(true);
            }

            if (checkIfUrlAlreadyIn(download.getHistoryUrl())) {
                continue;
            }

            HistoryData historyData = new HistoryData(datum, download.getTheme(), download.getTitle(), download.getHistoryUrl());
            addToThisList(historyData);
            list.add(historyData);
        }

        boolean ret = writeHistoryDataToFile(list, true);
        PDuration.counterStop("writeDataToHistoryFile");
        return ret;
    }

    public synchronized void removeHistoryListFromHistory(ArrayList<HistoryData> historyDataList) {
        // Logfile einlesen, entsprechende Zeile Filtern und dann Logfile überschreiben

        if (historyDataList == null || historyDataList.isEmpty()) {
            return;
        }

        final HashSet<String> hash = new HashSet<>(historyDataList.size() + 1, 0.75F);
        for (HistoryData historyData : historyDataList) {
            hash.add(historyData.getUrl());
        }

        // in den Filmen für die zu löschenden URLs history löschen
        ProgData.getInstance().filmlist.stream().forEach(film -> {
            if (hash.contains(film.getUrlForHash())) {
                film.setShown(false);
                film.setActHist(false);
            }
        });

        remove(hash);
    }

    public synchronized void removeFilmListFromHistory(ArrayList<Film> filmList) {
        // Logfile einlesen, entsprechende Zeile Filtern und dann Logfile überschreiben

        if (filmList == null || filmList.isEmpty()) {
            return;
        }

        final HashSet<String> hash = new HashSet<>(filmList.size() + 1, 0.75F);
        filmList.stream().forEach(film -> {
            film.setShown(false); // todo mal vormerken ob evtl. die ganze Filmliste nach dieser URL durchsucht werden soll, wird sonst erst beim nächsten Start angezeigt
            film.setActHist(false);
            hash.add(film.getUrlHistory());
        });

        remove(hash);
    }

    public synchronized void removeDownloadListFromHistory(ArrayList<Download> downloadList) {
        // Logfile einlesen, entsprechende Zeile Filtern und dann Logfile überschreiben

        if (downloadList == null || downloadList.isEmpty()) {
            return;
        }

        final HashSet<String> hash = new HashSet<>(downloadList.size() + 1, 0.75F);
        downloadList.stream().forEach(download -> {
            if (download.getFilm() != null) {
                download.getFilm().setShown(false);
                download.getFilm().setActHist(false);
            }
            hash.add(download.getHistoryUrl());
        });

        remove(hash);
    }

    private void remove(HashSet<String> urlHash) {
        waitToRemove();

        try {
            inRemove = true;
            Thread th = new Thread(new Remove(urlHash));
            th.setName("remove(HashSet<String> urlHash)");
            th.start();
            // th.run();
        } catch (Exception ex) {
            PLog.errorLog(912030254, ex, "waitToRemove");
            inRemove = false;
        }
    }

    private void waitToRemove() {
        while (inRemove) {
            // sollte nicht passieren, aber wenn ..
            PLog.errorLog(741025896, "waitToRemove");

            try {
                wait(100);
            } catch (final Exception ex) {
                PLog.errorLog(915236547, ex, "waitToRemove");
                inRemove = false;
            }

        }
    }

    private class Remove implements Runnable {
        private boolean found = false;
        private final ArrayList<HistoryData> newHistoryList = new ArrayList<>();
        private final HashSet<String> urlHash;

        public Remove(HashSet<String> urlHash) {
            this.urlHash = urlHash;
        }

        public void run() {
            doWork();
            inRemove = false;
        }

        private void doWork() {
            final Path urlPath = getUrlFilePath();
            if (Files.notExists(urlPath)) {
                return;
            }

            PDuration.counterStart("removeUrlsFromHistory");
            PLog.sysLog("Anzahl Urls: " + urlHash.size() + ", löschen aus: " + fileName);

            HistoryList.super.stream().forEach(historyData -> {
                if (urlHash.contains(historyData.getUrl())) {
                    found = true; // nur dann muss das Logfile auch geschrieben werden
                } else {
                    // kommt wieder in die history
                    newHistoryList.add(historyData);
                }
            });

            if (found) {
                // und jetzt wieder schreiben, wenn nötig
                writeHistoryDataToFile(newHistoryList, false);
            }

            replaceThisList(newHistoryList);
            PDuration.counterStop("removeUrlsFromHistory");
        }
    }

    private boolean writeHistoryDataToFile(ArrayList<HistoryData> list, boolean append) {
        boolean ret = false;
        try (BufferedWriter bufferedWriter = (append ?
                new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(getUrlFilePath(), StandardOpenOption.APPEND))) :
                new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(getUrlFilePath()))))
        ) {

            for (final HistoryData historyData : list) {
                bufferedWriter.write(historyData.getLine());
            }
            ret = true;

        } catch (final Exception ex) {
            PLog.errorLog(420312459, ex);
        }

        return ret;
    }

    private void readHistoryDataFromFile() {
        // neue Liste mit den URLs aus dem Logfile bauen
        List<HistoryData> tmpList = new ArrayList<>();
        final Path urlPath = getUrlFilePath();

        try (LineNumberReader in = new LineNumberReader(new InputStreamReader(Files.newInputStream(urlPath)))) {
            String line;
            while ((line = in.readLine()) != null) {
                final HistoryData historyData = HistoryData.getHistoryDataFromLine(line);
                tmpList.add(historyData);
            }

            if (!tmpList.isEmpty()) {
                replaceThisList(tmpList);
            }

        } catch (final Exception ex) {
            PLog.errorLog(926362547, ex);
        }
    }

    private Path getUrlFilePath() {
        Path urlPath = null;
        try {

            urlPath = Paths.get(settingsDir).resolve(fileName);
            if (Files.notExists(urlPath)) {
                urlPath = Files.createFile(urlPath);
            }

        } catch (final IOException ex) {
            PLog.errorLog(915478960, ex);
        }
        return urlPath;
    }

    private void clearList() {
        urlHash.clear();
        this.clear();
    }

    private void addToThisList(HistoryData historyData) {
        this.add(historyData);
        urlHash.add(historyData.getUrl());
    }

    private void replaceThisList(List<HistoryData> historyData) {
        clearList();
        historyData.stream().forEach(h -> urlHash.add(h.getUrl()));
        this.addAll(historyData);
    }

}
