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

package de.p2tools.mtplayer.controller.data.setdata;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.dialog.CheckSetDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.tools.P2InfoFactory;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SetFactory {
    private static final ArrayList<String> winPath = new ArrayList<>();

    public static String getTemplatePathVlc() { // todo -> p2lib
        // Startdialog und ProgConfig als init

        // liefert den Standardpfad für das entsprechende BS
        // Programm muss auf dem Rechner instelliert sein
        final String PATH_LINUX_VLC = "/usr/bin/vlc";
        final String PATH_FREEBSD = "/usr/local/bin/vlc";
        final String PATH_WIN = "\\VideoLAN\\VLC\\vlc.exe";
        String path = "";
        try {
            switch (P2InfoFactory.getOs()) {
                case LINUX:
                    if (System.getProperty("os.name").toLowerCase().contains("freebsd")) {
                        path = PATH_FREEBSD;
                    } else {
                        path = PATH_LINUX_VLC;
                    }
                    break;
                default:
                    setWinProgPathVLC();
                    for (final String s : winPath) {
                        path = s + PATH_WIN;
                        if (new File(path).exists()) {
                            break;
                        }
                    }
            }
            if (!new File(path).exists() && System.getenv("PATH_VLC") != null) {
                path = System.getenv("PATH_VLC");
            }
            if (!new File(path).exists()) {
                path = "";
            }
        } catch (final Exception ignore) {
        }
        return path;
    }

    public static String getTemplatePathFFmpeg() { //todo -> p2lib
        // Startdialog und ProgConfig als init

        // liefert den Standardpfad für das entsprechende BS
        // bei Win wird das Programm mitgeliefert und liegt
        // im Ordner "bin" der mit dem Programm mitgeliefert wird
        // bei Linux muss das Programm auf dem Rechner installiert sein
        final String PATH_LINUX_FFMPEG = "/usr/bin/ffmpeg";
        final String PATH_FREEBSD_FFMPEG = "/usr/local/bin/ffmpeg";
        final String PATH_WINDOWS_FFMPEG = "bin\\ffmpeg.exe";
        String path = "";
        try {
            switch (P2InfoFactory.getOs()) {
                case LINUX:
                    if (System.getProperty("os.name").toLowerCase().contains("freebsd")) {
                        path = PATH_FREEBSD_FFMPEG;
                    } else {
                        path = PATH_LINUX_FFMPEG;
                    }
                    break;
                default:
                    path = PATH_WINDOWS_FFMPEG;
            }
            if (!new File(path).exists() && System.getenv("PATH_FFMPEG") != null) {
                path = System.getenv("PATH_FFMPEG");
            }
            if (!new File(path).exists()) {
                path = "";
            }
        } catch (final Exception ignore) {
        }
        return path;
    }

    private static void setWinProgPathVLC() { // todo -> p2lib
        String pfad;
        if (System.getenv("ProgramFiles") != null) {
            pfad = System.getenv("ProgramFiles");
            if (new File(pfad).exists() && !winPath.contains(pfad)) {
                winPath.add(pfad);
            }
        }
        if (System.getenv("ProgramFiles(x86)") != null) {
            pfad = System.getenv("ProgramFiles(x86)");
            if (new File(pfad).exists() && !winPath.contains(pfad)) {
                winPath.add(pfad);
            }
        }
        final String[] PATH = {"C:\\Program Files", "C:\\Programme", "C:\\Program Files (x86)"};
        for (final String s : PATH) {
            if (new File(s).exists() && !winPath.contains(s)) {
                winPath.add(s);
            }
        }
    }

    public static boolean testPrefix(String str, String uurl, boolean prefix) {
        //prüfen ob url beginnt/endet mit einem Argument in str
        //wenn str leer dann true
        boolean ret = false;
        final String url = uurl.toLowerCase();
        String s1 = "";
        if (str.isEmpty()) {
            ret = true;
        } else {
            for (int i = 0; i < str.length(); ++i) {
                if (str.charAt(i) != ',') {
                    s1 += str.charAt(i);
                }
                if (str.charAt(i) == ',' || i >= str.length() - 1) {
                    if (prefix) {
                        //Präfix prüfen
                        if (url.startsWith(s1.toLowerCase())) {
                            ret = true;
                            break;
                        }
                    } else //Suffix prüfen
                        if (url.endsWith(s1.toLowerCase())) {
                            ret = true;
                            break;
                        }
                    s1 = "";
                }
            }
        }
        return ret;
    }

    public static boolean checkPathWritable(String path) {
        boolean ret = false;
        final File testPath = new File(path);
        try {
            if (!testPath.exists()) {
                testPath.mkdirs();
            }
            if (path.isEmpty()) {
            } else if (!testPath.isDirectory()) {
            } else if (testPath.canWrite()) {
                final File tmpFile = File.createTempFile("mtplayer", "tmp", testPath);
                tmpFile.delete();
                ret = true;
            }
        } catch (final Exception ignored) {
        }
        return ret;
    }

    public static void checkPrograms(Stage stage, ProgData data, boolean showWhenOk) {
        // prüfen, ob die eingestellten Programmsets passen
        if (data.setDataList.isEmpty()) {
            // dann gibts nix
            P2Alert.showErrorAlert(stage, "Set", "Sets prüfen", "Es sind keine Sets vorhanden! " +
                    "Bitte die Standardsets importieren.");
            return;
        }

        final String PIPE = "| ";
        final String LEER = "      ";
        final String PFEIL = " -> ";
        boolean ret;
        boolean show = showWhenOk;
        StringBuilder text = new StringBuilder();

        final SetData set = ProgData.getInstance().setDataList.getSetDataPlay();
        if (set == null) {
            text.append("+++++++++++++++++++++++++++++++").append(P2LibConst.LINE_SEPARATOR);
            text.append("Kein Videoplayer!").append(P2LibConst.LINE_SEPARATOR);
            text.append("Es ist kein Videoplayer zum Abspielen ").append(P2LibConst.LINE_SEPARATOR);
            text.append("der Filme angelegt.").append(P2LibConst.LINE_SEPARATOR);
            text.append("+++++++++++++++++++++++++++++++").append(P2LibConst.LINE_SEPARATOR);
            text.append(P2LibConst.LINE_SEPARATOR);
            show = true;
        }

        final boolean setEmpty = ProgData.getInstance().setDataList.getSetDataListSave().isEmpty();
        if (setEmpty) {
            text.append("+++++++++++++++++++++++++++++++").append(P2LibConst.LINE_SEPARATOR);
            text.append("Kein Set zum Speichern!").append(P2LibConst.LINE_SEPARATOR);
            text.append("Es ist kein Set zum Speichern ").append(P2LibConst.LINE_SEPARATOR);
            text.append("der Filme angelegt.").append(P2LibConst.LINE_SEPARATOR);
            text.append("+++++++++++++++++++++++++++++++").append(P2LibConst.LINE_SEPARATOR);
            text.append(P2LibConst.LINE_SEPARATOR);
            show = true;
        }

        for (final SetData setData : data.setDataList) {
            ret = true;
            if (setData.isPlay() || setData.isSaveAbo()) {
                // nur Sets die auch zum Abspielen/Speichern verwendet werden
                text.append("+++++++++++++++++++++++++++++++").append(P2LibConst.LINE_SEPARATOR);
                text.append(PIPE + "Programmgruppe: ").append(setData.getVisibleName()).append(P2LibConst.LINE_SEPARATOR);
                if (!setData.isPlay() && setData.isSaveAbo()) {
                    // Speichern/Abspielen und nicht Play - beim Abspielen wird er nicht gebraucht
                    // auch nicht bei "nur-Button"
                    final String destPath = setData.getDestPath();
                    if (destPath.isEmpty()) {
                        ret = false;
                        text.append(PIPE + LEER + "Zielpfad fehlt!").append(P2LibConst.LINE_SEPARATOR);
                    } else // Pfad beschreibbar?
                        if (!checkPathWritable(destPath)) {
                            //da Pfad-leer und "kein" Pfad schon abgeprüft
                            ret = false;
                            text.append(PIPE + LEER + "Falscher Zielpfad!").append(P2LibConst.LINE_SEPARATOR);
                            text.append(PIPE + LEER + PFEIL + "Zielpfad \"").append(destPath).append("\" nicht beschreibbar!").append(P2LibConst.LINE_SEPARATOR);
                        }
                }

                for (final ProgramData progData : setData.getProgramList()) {
                    // Programmpfad prüfen
                    if (progData.getProgPath().isEmpty()) {
                        ret = false;
                        text.append(PIPE + LEER + "Kein Programm angegeben!").append(P2LibConst.LINE_SEPARATOR);
                        text.append(PIPE + LEER + PFEIL + "Programmname: ").append(progData.getName()).append(P2LibConst.LINE_SEPARATOR);
                        text.append(PIPE + LEER + LEER + "Pfad: ").append(progData.getProgPath()).append(P2LibConst.LINE_SEPARATOR);

                    } else {
                        Path path = Paths.get(progData.getProgPath());
                        if (!Files.exists(path)) {
                            // Pfad passt nicht
                            if (progData.getProgPath().contains(File.separator)) {
                                // ist dann ein absoluter Pfad und muss stimmen
                                ret = false;
                                text.append(PIPE + LEER + "Falscher Programmpfad!").append(P2LibConst.LINE_SEPARATOR);
                                text.append(PIPE + LEER + PFEIL + "Programmname: ").append(progData.getName()).append(P2LibConst.LINE_SEPARATOR);
                                text.append(PIPE + LEER + LEER + "Pfad: ").append(progData.getProgPath()).append(P2LibConst.LINE_SEPARATOR);

                            } else {
                                // ist nur der Programmname
                                if (!Files.isExecutable(path)) {
                                    // und noch mit RuntimeExec versuchen
                                    Process process = null;
                                    try {
                                        process = Runtime.getRuntime().exec(progData.getProgPath());
                                    } catch (Exception ignored) {
                                    }
                                    if (process != null) {
                                        // dann passts ja
                                        try {
                                            process.destroy();
                                        } catch (Exception ignore) {
                                        }

                                    } else {
                                        ret = false;
                                        text.append(PIPE + LEER + "Falscher Programmpfad!").append(P2LibConst.LINE_SEPARATOR);
                                        text.append(PIPE + LEER + PFEIL + "Programmname: ").append(progData.getName()).append(P2LibConst.LINE_SEPARATOR);
                                        text.append(PIPE + LEER + LEER + "Pfad: ").append(progData.getProgPath()).append(P2LibConst.LINE_SEPARATOR);
                                        text.append(PIPE + "Das Programm braucht einen Pfad").append(P2LibConst.LINE_SEPARATOR);
                                        text.append(PIPE + "oder muss im Systempfad liegen,").append(P2LibConst.LINE_SEPARATOR);
                                        text.append(PIPE + "damit der Start klappt!").append(P2LibConst.LINE_SEPARATOR);
                                    }
                                }
                            }

                        } else if (!Files.isExecutable(path)) {
                            // dann noch mit RuntimeExec versuchen
                            Process process = null;
                            try {
                                process = Runtime.getRuntime().exec(progData.getProgPath());
                            } catch (Exception ignored) {
                            }
                            if (process != null) {
                                // dann passts ja
                                try {
                                    process.destroy();
                                } catch (Exception ignore) {
                                }

                            } else {
                                // lässt sich nicht starten
                                ret = false;
                                text.append(PIPE + LEER + "Programm kann nicht gestartet werden").append(P2LibConst.LINE_SEPARATOR);
                                text.append(PIPE + LEER + PFEIL + "Programmname: ").append(progData.getName()).append(P2LibConst.LINE_SEPARATOR);
                                text.append(PIPE + LEER + LEER + "Pfad: ").append(progData.getProgPath()).append(P2LibConst.LINE_SEPARATOR);
                            }
                        }
                    }
                }

                if (ret) {
                    //sollte alles passen
                    text.append(PIPE + PFEIL + "Ok!").append(P2LibConst.LINE_SEPARATOR);
                } else {
                    show = true;
                }
                text.append("+++++++++++++++++++++++++++++++").append(P2LibConst.LINE_SEPARATORx3);
            }
        }
        if (show) {
            // dann Fehler oder immer anzeigen
            new CheckSetDialogController(text.toString());
            // P2Alert.showInfoAlert(stage, "Set", "Sets prüfen", text.toString(), true);
        }
    }
}
