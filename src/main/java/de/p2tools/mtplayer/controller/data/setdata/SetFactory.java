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
import de.p2tools.mtplayer.controller.starter.RuntimeExecDownload;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.tools.P2ToolsFactory;

import java.io.File;
import java.util.ArrayList;

public class SetFactory {
    private static final ArrayList<String> winPath = new ArrayList<>();

    private static void setWinProgPath() {
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

    public static String getTemplatePathVlc() {
        // liefert den Standardpfad für das entsprechende BS
        // Programm muss auf dem Rechner instelliert sein
        final String PATH_LINUX_VLC = "/usr/bin/vlc";
        final String PATH_FREEBSD = "/usr/local/bin/vlc";
        final String PATH_WIN = "\\VideoLAN\\VLC\\vlc.exe";
        String path = "";
        try {
            switch (P2ToolsFactory.getOs()) {
                case LINUX:
                    if (System.getProperty("os.name").toLowerCase().contains("freebsd")) {
                        path = PATH_FREEBSD;
                    } else {
                        path = PATH_LINUX_VLC;
                    }
                    break;
                default:
                    setWinProgPath();
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

    public static String getTemplatePathFFmpeg() {
        // liefert den Standardpfad für das entsprechende BS
        // bei Win+Mac wird das Programm mitgeliefert und liegt
        // im Ordner "bin" der mit dem Programm mitgeliefert wird
        // bei Linux muss das Programm auf dem Rechner installiert sein
        final String PATH_LINUX_FFMPEG = "/usr/bin/ffmpeg";
        final String PATH_FREEBSD_FFMPEG = "/usr/local/bin/ffmpeg";
        final String PATH_WINDOWS_FFMPEG = "bin\\ffmpeg.exe";
        String path = "";
        try {
            switch (P2ToolsFactory.getOs()) {
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

    public static boolean checkPrograms(ProgData data) {
        // prüfen ob die eingestellten Programmsets passen
        final String PIPE = "| ";
        final String LEER = "      ";
        final String PFEIL = " -> ";
        boolean ret = true;
        String text = "";

        for (final SetData setData : data.setDataList) {
            ret = true;
            if (!setData.isFreeLine() && !setData.isLable()) {
                // nur wenn kein Lable oder freeline
                text += "++++++++++++++++++++++++++" + P2LibConst.LINE_SEPARATOR;
                text += PIPE + "Programmgruppe: " + setData.getVisibleName() + P2LibConst.LINE_SEPARATOR;
                final String destPath = setData.getDestPath();
                if (setData.progsContainPath()) {
                    // beim nur Abspielen wird er nicht gebraucht
                    if (destPath.isEmpty()) {
                        ret = false;
                        text += PIPE + LEER + "Zielpfad fehlt!" + P2LibConst.LINE_SEPARATOR;
                    } else // Pfad beschreibbar?
                        if (!checkPathWritable(destPath)) {
                            //da Pfad-leer und "kein" Pfad schon abgeprüft
                            ret = false;
                            text += PIPE + LEER + "Falscher Zielpfad!" + P2LibConst.LINE_SEPARATOR;
                            text += PIPE + LEER + PFEIL + "Zielpfad \"" + destPath + "\" nicht beschreibbar!" + P2LibConst.LINE_SEPARATOR;
                        }
                }
                for (final ProgramData progData : setData.getProgramList()) {
                    // Programmpfad prüfen
                    if (progData.getProgPath().isEmpty()) {
                        ret = false;
                        text += PIPE + LEER + "Kein Programm angegeben!" + P2LibConst.LINE_SEPARATOR;
                        text += PIPE + LEER + PFEIL + "Programmname: " + progData.getName() + P2LibConst.LINE_SEPARATOR;
                        text += PIPE + LEER + LEER + "Pfad: " + progData.getProgPath() + P2LibConst.LINE_SEPARATOR;
                    } else if (!new File(progData.getProgPath()).canExecute()) {
                        // dann noch mit RuntimeExec versuchen
                        final RuntimeExecDownload r = new RuntimeExecDownload(progData.getProgPath());
                        final Process pr = r.exec(false /*log*/);
                        if (pr != null) {
                            // dann passts ja
                            pr.destroy();
                        } else {
                            // läßt sich nicht starten
                            ret = false;
                            text += PIPE + LEER + "Falscher Programmpfad!" + P2LibConst.LINE_SEPARATOR;
                            text += PIPE + LEER + PFEIL + "Programmname: " + progData.getName() + P2LibConst.LINE_SEPARATOR;
                            text += PIPE + LEER + LEER + "Pfad: " + progData.getProgPath() + P2LibConst.LINE_SEPARATOR;
                            if (!progData.getProgPath().contains(File.separator)) {
                                text += PIPE + LEER + PFEIL + "Wenn das Programm nicht im Systempfad liegt, " + P2LibConst.LINE_SEPARATOR;
                                text += PIPE + LEER + LEER + "wird der Start nicht klappen!" + P2LibConst.LINE_SEPARATOR;
                            }
                        }
                    }
                }
                if (ret) {
                    //sollte alles passen
                    text += PIPE + PFEIL + "Ok!" + P2LibConst.LINE_SEPARATOR;
                }
                text += "++++++++++++++++++++++++++" + "" + P2LibConst.LINE_SEPARATORx3;
            }
        }
        P2Alert.showInfoAlert("Set", "Sets prüfen", text);
        return ret;
    }
}
