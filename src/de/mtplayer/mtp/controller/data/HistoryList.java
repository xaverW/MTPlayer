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
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.p2tools.p2Lib.PConst;
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
import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("serial")
public class HistoryList extends SimpleListProperty<HistoryData> {

    private final HashSet<String> urlHash = new HashSet<>();
    private final String fileName;
    private final String settingsDir;
    private FilteredList<HistoryData> filteredList = null;
    private SortedList<HistoryData> sortedList = null;

    public HistoryList(String fileName, String settingsDir) {
        super(FXCollections.observableArrayList());

        this.fileName = fileName;
        this.settingsDir = settingsDir;

        createList();
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
        final Path urlPath = getUrlFilePath();
        try {
            Files.deleteIfExists(urlPath);
        } catch (final IOException ignored) {
        }
    }

    public synchronized boolean checkIfExists(String urlFilm) {
        // wenn url gefunden, dann true zurück
        return urlHash.contains(urlFilm);
    }

    public synchronized boolean checkIfExists(String theme, String urlFilm) {
        // live ist nie alt || oder url schon vorhanden
        if (theme.equals(FilmTools.THEME_LIVE) || checkIfExists(urlFilm)) {
            return true;
        }
        return false;
    }

    public synchronized boolean writeHistory(String theme, String title, String url) {
        if (checkIfExists(theme, url)) {
            return true;
        }

        boolean ret = false;
        String text;
        final String datum = StringFormatters.FORMATTER_ddMMyyyy.format(new Date());
        HistoryData historyData = new HistoryData(datum, theme, title, url);
        addToList(historyData);

        try (BufferedWriter bufferedWriter =
                     new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(getUrlFilePath(), StandardOpenOption.APPEND)))) {
            text = HistoryData.getLine(datum, theme, title, url);
            bufferedWriter.write(text);
            ret = true;
        } catch (final Exception ex) {
            PLog.errorLog(945258023, ex);
        }
        return ret;
    }

    public synchronized boolean writeFilmArray(ArrayList<Film> arrayFilms) {
        boolean ret = false;
        final String datum = StringFormatters.FORMATTER_ddMMyyyy.format(new Date());

        try (BufferedWriter bufferedWriter =
                     new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(getUrlFilePath(), StandardOpenOption.APPEND)))) {

            for (final Film film : arrayFilms) {
                if (checkIfExists(film.getTheme(), film.getUrlHistory())) {
                    continue;
                }

                film.setShown(true);
                film.setActHist(true);

                HistoryData historyData = new HistoryData(datum, film.arr[FilmXml.FILM_THEME], film.arr[FilmXml.FILM_TITLE], film.getUrlHistory());
                addToList(historyData);
                bufferedWriter.write(historyData.getLine());
            }

            ret = true;
        } catch (final Exception ex) {
            PLog.errorLog(420312459, ex);
        }
        return ret;
    }

    public synchronized boolean writeDownloadArray(ArrayList<Download> arrayDownloads) {
        boolean ret = false;
        final String datum = StringFormatters.FORMATTER_ddMMyyyy.format(new Date());

        try (BufferedWriter bufferedWriter =
                     new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(getUrlFilePath(), StandardOpenOption.APPEND)))) {

            for (final Download download : arrayDownloads) {
                if (checkIfExists(download.getTheme(), download.getHistoryUrl())) {
                    continue;
                }

                if (download.getFilm() != null) {
                    download.getFilm().setShown(true);
                    download.getFilm().setActHist(true);
                }

                HistoryData historyData = new HistoryData(datum, download.getTheme(), download.getTitle(), download.getHistoryUrl());
                addToList(historyData);
                bufferedWriter.write(historyData.getLine());

            }

            ret = true;
        } catch (final Exception ex) {
            PLog.errorLog(940120459, ex);
        }
        return ret;
    }

    public synchronized void removeFilmListFromHistory(ArrayList<Film> filmArrayList) {
        // Logfile einlesen, entsprechende Zeile Filtern und dann Logfile überschreiben
        // wenn die URL im Logfiel ist, dann true zurück


        if (filmArrayList == null || filmArrayList.isEmpty()) {
            return;
        }

        final LinkedList<String> urlList = new LinkedList<>();
        filmArrayList.stream().forEach(film -> {
            film.setShown(false);
            film.setActHist(false);
            urlList.add(film.getUrlHistory());
        });

        Thread th = new Thread(new Remove(urlList));
        th.setName("removeFilmListFromHistory");
        th.start();
    }

    public synchronized void removeListFromHistory(ArrayList<HistoryData> historyDataArrayList) {
        // Logfile einlesen, entsprechende Zeile Filtern und dann Logfile überschreiben
        // wenn die URL im Logfiel ist, dann true zurück


        if (historyDataArrayList == null || historyDataArrayList.isEmpty()) {
            return;
        }

        final LinkedList<String> urlList = new LinkedList<>();
        for (HistoryData historyData : historyDataArrayList) {
            urlList.add(historyData.getUrl());
        }

        Thread th = new Thread(new Remove(urlList));
        th.setName("removeListFromHistory");
        th.start();
    }

    public synchronized void removeDownloadListFromHistory(ArrayList<Download> downloads) {
        // Logfile einlesen, entsprechende Zeile Filtern und dann Logfile überschreiben
        // wenn die URL im Logfiel ist, dann true zurück

        if (downloads == null || downloads.isEmpty()) {
            return;
        }

        final LinkedList<String> urlList = new LinkedList<>();
        downloads.stream().forEach(download -> {
            if (download.getFilm() != null) {
                download.getFilm().setShown(false);
                download.getFilm().setActHist(false);
                urlList.add(download.getHistoryUrl());
            }
        });

        Thread th = new Thread(new Remove(urlList));
        th.setName("removeDownloadListFromHistory");
        th.start();
    }

    private class Remove implements Runnable {
        String line;
        boolean found = false, gef;
        final LinkedList<String> newList = new LinkedList<>();
        LinkedList<String> urlList;

        public Remove(LinkedList<String> urlList) {
            this.urlList = urlList;
        }

        public void run() {

            final Path urlPath = getUrlFilePath();
            if (Files.notExists(urlPath)) {
                return;
            }


            PDuration.counterStart("removeDownloadListFromHistory");
            PLog.sysLog(String.format("Urls aus der History: %s löschen: " + urlList.size(), fileName));

            //todo-> ~1s Dauer

            try (LineNumberReader in = new LineNumberReader(new InputStreamReader(Files.newInputStream(urlPath)))) {
                while ((line = in.readLine()) != null) {
                    gef = false;
                    final String url = HistoryData.getUrlFromLine(line).getUrl();

                    for (final String histUrl : urlList) {
                        if (url.equals(histUrl)) {
                            found = true; // nur dann muss das Logfile auch geschrieben werden
                            gef = true; // und die Zeile wird verworfen
                            break;
                        }
                    }
                    if (!gef) {
                        newList.add(line);
                    }

                }
            } catch (final Exception ex) {
                PLog.errorLog(401020398, ex);
            }

            // todo -> synchronize
            // und jetzt wieder schreiben, wenn nötig
            writeTmpList(newList, found);
            PDuration.counterStop("removeDownloadListFromHistory");
        }

    }

    private void writeTmpList(LinkedList<String> newList, boolean found) {
        if (found) {
            try (BufferedWriter bufferedWriter =
                         new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(getUrlFilePath())))) {
                for (final String entry : newList) {
                    bufferedWriter.write(entry + PConst.LINE_SEPARATOR);
                }
            } catch (final Exception ex) {
                PLog.errorLog(784512067, ex);
            }
        }

        clearList();
        createList();
    }

    private void clearList() {
        urlHash.clear();
        this.clear();
    }

    private void addToList(HistoryData historyData) {
        this.add(historyData);
        urlHash.add(historyData.getUrl());
    }

    private void addToList(List<HistoryData> historyData) {
        historyData.stream().forEach(h -> urlHash.add(h.getUrl()));
        this.addAll(historyData);
    }

    private Path getUrlFilePath() {
        Path urlPath = null;
        try {
            urlPath = Paths.get(settingsDir).resolve(fileName);
            if (Files.notExists(urlPath)) {
                urlPath = Files.createFile(urlPath);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        return urlPath;
    }

    private void createList() {
        List<HistoryData> tmpList = new ArrayList<>();
        // LinkedList mit den URLs aus dem Logfile bauen
        final Path urlPath = getUrlFilePath();
        // use Automatic Resource Management
        try (LineNumberReader in = new LineNumberReader(new InputStreamReader(Files.newInputStream(urlPath)))) {
            String line;
            while ((line = in.readLine()) != null) {
                final HistoryData historyData = HistoryData.getUrlFromLine(line);
                tmpList.add(historyData);
            }
            if (!tmpList.isEmpty()) {
                addToList(tmpList);
            }
        } catch (final Exception ex) {
            PLog.errorLog(926362547, ex);
        }
    }

}
