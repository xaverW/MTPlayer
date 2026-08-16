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


package de.p2tools.mtplayer.controller.data.history;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.tools.FileFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.tools.log.P2Log;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConvertOldHistoryFactory {
    private final static String SEPARATOR_1 = " |#| ";
    private final static String SEPARATOR_2 = "  |###|  ";

    private ConvertOldHistoryFactory() {
    }

    public static void addHistoryToShownFile() {
        ProgData.getInstance().historyListJson.loadList();
        ProgData.getInstance().historyListJson.forEach(h -> {
            if (h.getSource() == HistoryData.SOURCE_DOWNLOAD) {
                h.setSource(HistoryData.SOURCE_SHOWN_DOWNLOAD);
            }
        });
        HistoryReadWriteJsonFactory.write();
    }

    public static void delOldHistoryFile() {
        // Shown
        final Path urlPathDownload = FileFactory.getUrlFilePath(ProgInfos.getSettingsDirectory_String(),
                ProgConst.FILE_HISTORY_SHOWN_TXT);
        try {
            if (Files.exists(urlPathDownload)) {
                Files.delete(urlPathDownload);
            }
        } catch (Exception ex) {
            P2Log.errorLog(956231458, ex.getMessage());
        }

        // Abos: Downloads
        final Path urlPathAbo = FileFactory.getUrlFilePath(ProgInfos.getSettingsDirectory_String(),
                ProgConst.FILE_HISTORY_ABO_TXT);
        try {
            if (Files.exists(urlPathAbo)) {
                Files.delete(urlPathAbo);
            }
        } catch (Exception ex) {
            P2Log.errorLog(956231458, ex.getMessage());
        }
    }

    public static void convertHistoryList() {
        // laden und in die neue Liste eintragen
        ProgData.getInstance().historyListJson.clearAll();

        // erst Downloads (gesehen) -> bleiben "gesehen"
        List<HistoryData> addList = new ArrayList<>();
        ConvertOldHistoryFactory.readHistoryDataFromFile(ProgInfos.getSettingsDirectory_String(),
                ProgConst.FILE_HISTORY_SHOWN_TXT, addList);
        addList.forEach(h -> {
            h.setSource(HistoryData.SOURCE_SHOWN);
        });
        ProgData.getInstance().historyListJson.updateHistory(HistoryData.SOURCE_SHOWN, addList);

        // dann Abos (Downloads) -> werden "gesehen" und "download"
        addList.clear();
        ConvertOldHistoryFactory.readHistoryDataFromFile(ProgInfos.getSettingsDirectory_String(),
                ProgConst.FILE_HISTORY_ABO_TXT, addList);
        addList.forEach(h -> {
            h.setSource(HistoryData.SOURCE_SHOWN_DOWNLOAD);
        });
        ProgData.getInstance().historyListJson.updateHistory(HistoryData.SOURCE_SHOWN_DOWNLOAD, addList);

        HistoryReadWriteJsonFactory.write();
        ProgData.getInstance().historyListJson.clearAll(); // sonst sinds doppelt drin
    }


    private static synchronized void readHistoryDataFromFile(String settingsDir, String fileName, List<HistoryData> dataList) {
        // neue Liste mit den URLs aus dem Logfile bauen
        final Path urlPath = FileFactory.getUrlFilePath(settingsDir, fileName);
        try (LineNumberReader in = new LineNumberReader(new InputStreamReader(Files.newInputStream(urlPath)))) {
            String line;
            while ((line = in.readLine()) != null) {
                final HistoryData historyData = ConvertOldHistoryFactory.getHistoryDataFromLine(line);
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
        return new HistoryData(HistoryData.SOURCE_SHOWN, date, "", theme, title, url);
    }
}
