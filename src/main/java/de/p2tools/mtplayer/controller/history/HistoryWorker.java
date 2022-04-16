/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.history;

import de.p2tools.mtplayer.controller.data.film.FilmlistFactory;
import de.p2tools.p2Lib.tools.log.PLog;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HistoryWorker {
    private final String fileName;
    private final String settingsDir;

    public HistoryWorker(String fileName, String settingsDir) {
        this.fileName = fileName;
        this.settingsDir = settingsDir;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSettingsDir() {
        return settingsDir;
    }

    synchronized void deleteHistoryFile() {

        try {
            final Path urlPath = getUrlFilePath();
            Files.deleteIfExists(urlPath);
        } catch (final IOException ignored) {
        }
    }

    synchronized void readHistoryDataFromFile(List<HistoryData> dataList) {
        // neue Liste mit den URLs aus dem Logfile bauen
        final Path urlPath = getUrlFilePath();

        try (LineNumberReader in = new LineNumberReader(new InputStreamReader(Files.newInputStream(urlPath)))) {
            String line;
            while ((line = in.readLine()) != null) {
                final HistoryData historyData = HistoryData.getHistoryDataFromLine(line);
                // ==================================================================
                // todo --> kommt die nächste Version wieder raus,
                // da können Problematische Zeichen enthalten sein
                // können aber keine Fehler mehr nachfolgen

                // 0,2s bei 80_000 Einträgen
                // PDuration.counterStart("readHistoryDataFromFile");
                historyData.setTheme(FilmlistFactory.cleanUnicode(historyData.getTheme()));
                historyData.setTitle(FilmlistFactory.cleanUnicode(historyData.getTitle()));
                // PDuration.counterStop("readHistoryDataFromFile");
                // ==================================================================
                dataList.add(historyData);
            }

        } catch (final Exception ex) {
            PLog.errorLog(926362547, ex);
        }
    }

    synchronized Path getUrlFilePath() {
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
}
