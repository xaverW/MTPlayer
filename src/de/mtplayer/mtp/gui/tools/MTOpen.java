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

package de.mtplayer.mtp.gui.tools;

import de.mtplayer.mLib.tools.MLConfigs;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;

import java.awt.*;
import java.io.File;
import java.net.URI;

public class MTOpen {

    public static void openDestDir(String path) {
        File directory;

        if (path.isEmpty()) {
            return;
        }
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }

        if (new File(path).exists()) {
            directory = new File(path);
        } else {
            directory = new File(path).getParentFile();
        }


        if (!ProgConfig.SYSTEM_PROG_OPEN_DIR.get().isEmpty()) {
            Exception exception;
            try {
                final String program = ProgConfig.SYSTEM_PROG_OPEN_DIR.get();
                final String[] arrProgCallArray = {program, directory.getAbsolutePath()};
                Runtime.getRuntime().exec(arrProgCallArray);
            } catch (final Exception ex) {
                afterPlay(directory.getAbsolutePath(), TEXT.DIR, ex);
            }


        } else {
            Thread th = new Thread(() -> {
                try {
                    if (Desktop.isDesktopSupported()) {
                        final Desktop d = Desktop.getDesktop();
                        if (d.isSupported(Desktop.Action.OPEN)) {
                            d.open(directory);
                        }
                    }
                } catch (Exception ex) {
                    Platform.runLater(() -> afterPlay(directory.getAbsolutePath(), TEXT.DIR, ex));
                }
            });
            th.setName("openDestDir");
            th.start();

        }
    }

    public static void playStoredFilm(String file) {

        File filmFile;
        if (file.isEmpty()) {
            return;
        }
        filmFile = new File(file);

        if (!filmFile.exists()) {
            new MTAlert().showErrorAlert("Fehler", "Kein Film", "Film existiert noch nicht!");
            return;
        }


        if (!ProgConfig.SYSTEM_PROG_PLAY_FILE.get().isEmpty()) {
            // dann mit dem vorgegebenen Player starten
            try {
                final String program = ProgConfig.SYSTEM_PROG_PLAY_FILE.get();
                final String[] cmd = {program, filmFile.getAbsolutePath()};
                Runtime.getRuntime().exec(cmd);
            } catch (final Exception ex) {
                afterPlay(filmFile.getAbsolutePath(), TEXT.FILE, ex);
            }


        } else {
            // den Systemeigenen Player starten
            Thread th = new Thread(() -> {
                try {
                    if (Desktop.isDesktopSupported()) {
                        final Desktop d = Desktop.getDesktop();
                        if (d.isSupported(Desktop.Action.OPEN)) {
                            d.open(filmFile);
                        }
                    }
                } catch (Exception ex) {
                    Platform.runLater(() -> afterPlay(filmFile.getAbsolutePath(), TEXT.FILE, ex));
                }
            });
            th.setName("playStoredFilm");
            th.start();

        }
    }

    public static void openURL(String url) {

        if (url.isEmpty()) {
            return;
        }


        if (!ProgConfig.SYSTEM_PROG_OPEN_URL.get().isEmpty()) {
            // dann mit dem vorgegebenen Player starten
            try {
                final String program = ProgConfig.SYSTEM_PROG_OPEN_URL.get();
                final String[] cmd = {program, url};
                Runtime.getRuntime().exec(cmd);
            } catch (final Exception ex) {
                afterPlay(url, TEXT.URL, ex);
            }


        } else {
            // den Systemeigenen Player starten
            Thread th = new Thread(() -> {
                try {
                    if (Desktop.isDesktopSupported()) {
                        final Desktop d = Desktop.getDesktop();
                        if (d.isSupported(Desktop.Action.BROWSE)) {
                            d.browse(new URI(url));
                        }
                    }
                } catch (Exception ex) {
                    Platform.runLater(() -> afterPlay(url, TEXT.URL, ex));
                }
            });
            th.setName("openURL");
            th.start();

        }
    }

    enum TEXT {FILE, DIR, URL}

    private static void afterPlay(String directory, TEXT t, Exception exception) {
        String program = "";
        boolean ok;
        String title, header, cont;
        MLConfigs conf;

        switch (t) {
            default:
            case FILE:
                title = "Kein Videoplayer";
                header = "Videoplayer auswählen";
                cont = "Ein Videoplayer zum Abspielen wird nicht gefunden. Videoplayer selbst auswählen.";
                conf = ProgConfig.SYSTEM_PROG_PLAY_FILE;
                break;
            case DIR:
                title = "Kein Dateimanager";
                header = "Dateimanager auswählen";
                cont = "Der Dateimanager zum Anzeigen des Speicherordners wird nicht gefunden.\n" +
                        "Dateimanager selbst auswählen.";
                conf = ProgConfig.SYSTEM_PROG_OPEN_DIR;
                break;
            case URL:
                title = "Kein Browser";
                header = "Browser auswählen";
                cont = "Der Browser zum Anzeigen der URL wird nicht gefunden.\n" +
                        "Browser selbst auswählen.";
                conf = ProgConfig.SYSTEM_PROG_OPEN_URL;
                break;
        }


        try {
            program = new MTAlert().showAlertFileCooser(title, header, cont, false);
            if (!program.isEmpty()) {
                final String[] cmd = {program, directory};
                Runtime.getRuntime().exec(cmd);
                conf.setValue(program);
                ok = true;
            } else {
                // abgebrochen
                ok = true;
            }

        } catch (final Exception eex) {
            ok = false;
            PLog.errorLog(959632369, eex, new String[]{"Kann nicht öffnen,", "Programm: " + program,
                    "File/Url: " + directory});
        }

        if (!ok) {
            conf.setValue("");
            new MTAlert().showErrorAlert("Fehler", "", "Kann das Programm nicht öffnen!");
        }
    }

}
