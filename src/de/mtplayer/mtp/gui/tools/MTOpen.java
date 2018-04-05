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
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.p2tools.p2Lib.tools.Log;
import javafx.application.Platform;

import java.awt.*;
import java.io.File;
import java.net.URI;

public class MTOpen {

    public static void openDestDir(String ordner) {
        File directory;

        if (ordner.isEmpty()) {
            return;
        }
        if (!ordner.endsWith(File.separator)) {
            ordner += File.separator;
        }

        if (new File(ordner).exists()) {
            directory = new File(ordner);
        } else {
            directory = new File(ordner).getParentFile();
        }


        if (!Config.SYSTEM_PROG_OPEN_DIR.get().isEmpty()) {
            Exception exception;
            try {
                final String programm = Config.SYSTEM_PROG_OPEN_DIR.get();
                final String[] arrProgCallArray = {programm, directory.getAbsolutePath()};
                Runtime.getRuntime().exec(arrProgCallArray);
            } catch (final Exception ex) {
                afterPlay(directory.getAbsolutePath(), TEXT.DIR, ex);
            }


        } else {
            new Thread(() -> {
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
            }).start();

        }
    }

    public static void playStoredFilm(String datei) {

        File filmFile;
        if (datei.isEmpty()) {
            return;
        }
        filmFile = new File(datei);

        if (!filmFile.exists()) {
            new MTAlert().showErrorAlert("Fehler", "Kein Film", "Film existiert noch nicht!");
            return;
        }


        if (!Config.SYSTEM_PROG_PLAY_FILE.get().isEmpty()) {
            // dann mit dem vorgegebenen Player starten
            try {
                final String programm = Config.SYSTEM_PROG_PLAY_FILE.get();
                final String[] cmd = {programm, filmFile.getAbsolutePath()};
                Runtime.getRuntime().exec(cmd);
            } catch (final Exception ex) {
                afterPlay(filmFile.getAbsolutePath(), TEXT.FILE, ex);
            }


        } else {
            // den Systemeigenen Player starten
            new Thread(() -> {
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
            }).start();

        }
    }

    public static void openURL(String url) {

        if (url.isEmpty()) {
            return;
        }


        if (!Config.SYSTEM_PROG_OPEN_URL.get().isEmpty()) {
            // dann mit dem vorgegebenen Player starten
            try {
                final String programm = Config.SYSTEM_PROG_OPEN_URL.get();
                final String[] cmd = {programm, url};
                Runtime.getRuntime().exec(cmd);
            } catch (final Exception ex) {
                afterPlay(url, TEXT.URL, ex);
            }


        } else {
            // den Systemeigenen Player starten
            new Thread(() -> {
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
            }).start();

        }
    }

    enum TEXT {FILE, DIR, URL}

    private static void afterPlay(String directory, TEXT t, Exception exception) {
        String programm = "";
        boolean ok;
        String title, header, cont;
        MLConfigs conf;

        switch (t) {
            default:
            case FILE:
                title = "Kein Videoplayer";
                header = "Videoplayer auswählen";
                cont = "Ein Videoplayer zum Abspielen wird nicht gefunden. Videoplayer selbst auswählen.";
                conf = Config.SYSTEM_PROG_PLAY_FILE;
                break;
            case DIR:
                title = "Kein Dateimanager";
                header = "Dateimanager auswählen";
                cont = "Der Dateimanager zum Anzeigen des Speicherordners wird nicht gefunden.\n" +
                        "Dateimanager selbst auswählen.";
                conf = Config.SYSTEM_PROG_OPEN_DIR;
                break;
            case URL:
                title = "Kein Browser";
                header = "Browser auswählen";
                cont = "Der Browser zum Anzeigen der URL wird nicht gefunden.\n" +
                        "Browser selbst auswählen.";
                conf = Config.SYSTEM_PROG_OPEN_URL;
                break;
        }


        try {
            programm = new MTAlert().showAlertFileCooser(title, header, cont, false);
            if (!programm.isEmpty()) {
                final String[] cmd = {programm, directory};
                Runtime.getRuntime().exec(cmd);
                conf.setValue(programm);
                ok = true;
            } else {
                // abgebrochen
                ok = true;
            }

        } catch (final Exception eex) {
            ok = false;
            Log.errorLog(959632369, eex, new String[]{"Kann nicht öffnen,", "Programm: " + programm,
                    "File/Url: " + directory});
        }

        if (!ok) {
            conf.setValue("");
            new MTAlert().showErrorAlert("Fehler", "", "Kann das Programm nicht öffnen!");
        }
    }

}
