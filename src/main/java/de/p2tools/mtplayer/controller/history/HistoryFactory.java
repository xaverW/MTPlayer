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

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.tools.log.P2Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HistoryFactory {
    private final static String SEPARATOR_1 = " |#| ";
    private final static String SEPARATOR_2 = "  |###|  ";

    private HistoryFactory() {
    }

    public static boolean checkIfLiveStream(String theme) {
        // live ist nie alt
        return theme.equals(ProgConst.THEME_LIVE);
    }

    public static synchronized Path getUrlFilePath(String settingsDir, String fileName) {
        Path urlPath = null;
        try {
            urlPath = Paths.get(settingsDir).resolve(fileName);
            if (Files.notExists(urlPath)) {
                urlPath = Files.createFile(urlPath);
            }
        } catch (final IOException ex) {
            P2Log.errorLog(915478960, ex);
        }
        return urlPath;
    }

    public static synchronized void deleteHistoryFile(String settingsDir, String fileName) {
        try {
            final Path urlPath = getUrlFilePath(settingsDir, fileName);
            Files.deleteIfExists(urlPath);
        } catch (final IOException ignored) {
        }
    }

    public static synchronized void readHistoryDataFromFile(String settingsDir, String fileName, List<HistoryData> dataList) {
        // neue Liste mit den URLs aus dem Logfile bauen
        final Path urlPath = getUrlFilePath(settingsDir, fileName);
        try (LineNumberReader in = new LineNumberReader(new InputStreamReader(Files.newInputStream(urlPath)))) {
            String line;
            while ((line = in.readLine()) != null) {
                final HistoryData historyData = HistoryFactory.getHistoryDataFromLine(line);
                // ==================================================================
                // todo --> kommt die nächste Version wieder raus,
                // da können Problematische Zeichen enthalten sein
                // können aber keine Fehler mehr nachfolgen

                // 0,2s bei 80_000 Einträgen
                // PDuration.counterStart("readHistoryDataFromFile");
//                historyData.setTheme(FilmFactory.cleanUnicode(historyData.getTheme()));
//                historyData.setTitle(FilmFactory.cleanUnicode(historyData.getTitle()));
                // PDuration.counterStop("readHistoryDataFromFile");
                // ==================================================================
                dataList.add(historyData);
            }
        } catch (final Exception ex) {
            P2Log.errorLog(926362547, ex);
        }
    }

    public static String getLine(HistoryData historyData) {
        String dateStr = historyData.getDate().toString();
        String theme = historyData.getTheme();
        String title = historyData.getTitle();
        String url = historyData.getUrl();

        if (dateStr.isEmpty() && theme.isEmpty() && title.isEmpty()) {
            // dann das alte Format
            return url + P2LibConst.LINE_SEPARATOR;
        }

        // schneller
//        final int MAX_THEME = 25;
//        final int MAX_TITLE = 40;
//        if (theme.length() < MAX_THEME) {
//            // nur wenn zu kurz, dann anpassen, so bleibt das Log ~lesbar
//            // und Titel werden nicht abgeschnitten
//            theme = PStringUtils.shortenString(MAX_THEME, theme, false /* mitte */, false /*addVorne*/);
//        }
//        if (title.length() < MAX_TITLE) {
//            // nur wenn zu kurz, dann anpassen, so bleibt das Log ~lesbar
//            // und Titel werden nicht abgeschnitten
//            title = PStringUtils.shortenString(MAX_TITLE, title, false /* mitte */, false /*addVorne*/);
//        }

        return dateStr + SEPARATOR_1
                + cleanUp(theme) + SEPARATOR_1
                + cleanUp(title) + SEPARATOR_2
                + url + P2LibConst.LINE_SEPARATOR;
    }

    private static String cleanUp(String s) {
        s = s.replace("\n", ""); // zur Vorsicht bei Win
        s = s.replace("\r\n", ""); // zur Vorsicht bei Ux
        s = s.replace(P2LibConst.LINE_SEPARATOR, "");
        s = s.replace("|", "");
        s = s.replace(SEPARATOR_1, "");
        s = s.replace(SEPARATOR_2, "");
        return s;
    }

    private static HistoryData getHistoryDataFromLine(String line) {
        // 29.05.2014 |#| Abendschau                |#| Patenkind trifft Groß                     |###|  http://cdn-storage.br.de/iLCpbHJGNLT6NK9HsLo6s61luK4C_2rc5U1S/_-OS/5-8y9-NP/5bb33365-038d-46f7-914b-eb83fab91448_E.mp4
        String url = "", theme = "", title = "", date = "";
        int a1;
        try {
            if (line.contains(SEPARATOR_2)) {
                //neues Logfile-Format
                a1 = line.lastIndexOf(SEPARATOR_2);
                a1 += SEPARATOR_2.length();
                url = line.substring(a1).trim();
                // titel
                title = line.substring(line.lastIndexOf(SEPARATOR_1) + SEPARATOR_1.length(), line.lastIndexOf(SEPARATOR_2)).trim();
                date = line.substring(0, line.indexOf(SEPARATOR_1)).trim();
                theme = line.substring(line.indexOf(SEPARATOR_1) + SEPARATOR_1.length(), line.lastIndexOf(SEPARATOR_1)).trim();
            } else {
                url = line;
            }
        } catch (final Exception ex) {
            P2Log.errorLog(398853224, ex);
        }
        return new HistoryData(date, theme, title, url);
    }
}
