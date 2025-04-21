package de.p2tools.mtplayer.controller.data.bookmark;

import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.tools.FileFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.tools.log.P2Log;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BookmarkFileFactory {
    private final static String SEPARATOR_1 = " |#| ";
    private final static String SEPARATOR_2 = "  |###|  ";

    private BookmarkFileFactory() {
    }

    public static void writeToFile(List<BookmarkData> list, boolean append) {
        BookmarkWriteToFile.waitWhileWorking();
        BookmarkWriteToFile.isWorking.setValue(true);

        try {
            Thread th = new Thread(new BookmarkWriteToFile(list, append));
            th.setName("writeToFile");
            th.start();
        } catch (Exception ex) {
            P2Log.errorLog(959623657, ex, "writeToFile");
            BookmarkWriteToFile.isWorking.setValue(false);
        }
    }

    public static synchronized void readBookmarkDataFromFile(String fileName, List<BookmarkData> dataList) {
        // neue Liste mit den URLs aus dem Logfile bauen
        String settingsDir = ProgInfos.getSettingsDirectory_String();
        final Path urlPath = FileFactory.getUrlFilePath(settingsDir, fileName);
        try (LineNumberReader in = new LineNumberReader(new InputStreamReader(Files.newInputStream(urlPath)))) {
            String line;
            while ((line = in.readLine()) != null) {
                final BookmarkData bookmarkData = getDataFromLine(line);
                dataList.add(bookmarkData);
            }
        } catch (final Exception ex) {
            P2Log.errorLog(926362547, ex);
        }
    }

    public static String getLine(BookmarkData bookmarkData) {
        String dateStr = bookmarkData.getDate().toString();
        String theme = bookmarkData.getTheme();
        String title = bookmarkData.getTitle();
        String url = bookmarkData.getUrl();

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

    private static BookmarkData getDataFromLine(String line) {
        // 20.04.2025 |#| 37 Grad |#| 37°: Wechseljahre: heißkalt erwischt (S2025/E08) (Audiodeskription)  |###|  https://nrodlzdyxvcf-a.akamaihd.net/none/zdf/25/03/250318_2215_sendung_37g/1/250318_2215_sendung_37g_a3a4_3360k_p36v17.mp4
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
        return new BookmarkData(date, theme, title, url);
    }
}
