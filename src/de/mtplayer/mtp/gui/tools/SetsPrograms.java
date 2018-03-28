/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

import de.mtplayer.mLib.tools.FileUtils;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.controller.data.ProgData;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.SetList;
import de.mtplayer.mtp.controller.starter.RuntimeExec;
import de.mtplayer.mtp.gui.dialog.MTAlert;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static de.mtplayer.mLib.tools.Functions.getOs;

public class SetsPrograms {

    private static final ArrayList<String> winPfade = new ArrayList<>();

    private static void setWinProgPfade() {
        String pfad;
        if (System.getenv("ProgramFiles") != null) {
            pfad = System.getenv("ProgramFiles");
            if (new File(pfad).exists() && !winPfade.contains(pfad)) {
                winPfade.add(pfad);
            }
        }
        if (System.getenv("ProgramFiles(x86)") != null) {
            pfad = System.getenv("ProgramFiles(x86)");
            if (new File(pfad).exists() && !winPfade.contains(pfad)) {
                winPfade.add(pfad);
            }
        }
        final String[] PFAD = {"C:\\Program Files", "C:\\Programme", "C:\\Program Files (x86)"};
        for (final String s : PFAD) {
            if (new File(s).exists() && !winPfade.contains(s)) {
                winPfade.add(s);
            }
        }
    }

    public static String getMusterPfadVlc() {
        // liefert den Standardpfad für das entsprechende BS 
        // Programm muss auf dem Rechner instelliert sein
        final String PFAD_LINUX_VLC = "/usr/bin/vlc";
        final String PFAD_FREEBSD = "/usr/local/bin/vlc";
        final String PFAD_WIN = "\\VideoLAN\\VLC\\vlc.exe";
        String pfad = "";
        try {
            switch (getOs()) {
                case LINUX:
                    if (System.getProperty("os.name").toLowerCase().contains("freebsd")) {
                        pfad = PFAD_FREEBSD;
                    } else {
                        pfad = PFAD_LINUX_VLC;
                    }
                    break;
                default:
                    setWinProgPfade();
                    for (final String s : winPfade) {
                        pfad = s + PFAD_WIN;
                        if (new File(pfad).exists()) {
                            break;
                        }
                    }
            }
            if (!new File(pfad).exists() && System.getenv("PATH_VLC") != null) {
                pfad = System.getenv("PATH_VLC");
            }
            if (!new File(pfad).exists()) {
                pfad = "";
            }
        } catch (final Exception ignore) {
        }
        return pfad;
    }

    public static String getMusterPfadFlv() {
        // liefert den Standardpfad für das entsprechende BS 
        // bei Win+Mac wird das Programm mitgeliefert und liegt 
        // im Ordner "bin" der mit dem Programm mitgeliefert wird
        // bei Linux muss das Programm auf dem Rechner instelliert sein
        final String PFAD_LINUX_FLV = "/usr/bin/flvstreamer";
        final String PFAD_FREEBSD = "/usr/local/bin/flvstreamer";
        final String PFAD_WINDOWS_FLV = "bin\\flvstreamer_win32_latest.exe";
        String pfad = "";
        try {
            switch (getOs()) {
                case LINUX:
                    if (System.getProperty("os.name").toLowerCase().contains("freebsd")) {
                        pfad = PFAD_FREEBSD;
                    } else {
                        pfad = PFAD_LINUX_FLV;
                    }
                    break;
                default:
                    pfad = PFAD_WINDOWS_FLV;
            }
            if (!new File(pfad).exists() && System.getenv("PATH_FLVSTREAMER") != null) {
                pfad = System.getenv("PATH_FLVSTREAMER");
            }
            if (!new File(pfad).exists()) {
                pfad = "";
            }
        } catch (final Exception ignore) {
        }
        return pfad;
    }

    public static String getMusterPfadFFmpeg() {
        // liefert den Standardpfad für das entsprechende BS 
        // bei Win+Mac wird das Programm mitgeliefert und liegt 
        // im Ordner "bin" der mit dem Programm mitgeliefert wird
        // bei Linux muss das Programm auf dem Rechner installiert sein
        final String PFAD_LINUX_FFMPEG = "/usr/bin/ffmpeg";
        final String PFAD_FREEBSD_FFMPEG = "/usr/local/bin/ffmpeg";
        final String PFAD_WINDOWS_FFMPEG = "bin\\ffmpeg.exe";
        String pfad = "";
        try {
            switch (getOs()) {
                case LINUX:
                    if (System.getProperty("os.name").toLowerCase().contains("freebsd")) {
                        pfad = PFAD_FREEBSD_FFMPEG;
                    } else {
                        pfad = PFAD_LINUX_FFMPEG;
                    }
                    break;
                default:
                    pfad = PFAD_WINDOWS_FFMPEG;
            }
            if (!new File(pfad).exists() && System.getenv("PATH_FFMPEG") != null) {
                pfad = System.getenv("PATH_FFMPEG");
            }
            if (!new File(pfad).exists()) {
                pfad = "";
            }
        } catch (final Exception ignore) {
        }
        return pfad;
    }

    public static String getPfadScript() {
        // liefert den Standardpfad zum Script "Ansehen" für das entsprechende BS 
        // liegt im Ordner "bin" der mit dem Programm mitgeliefert wird
        String pfadScript;
        final String PFAD_LINUX_SCRIPT = "bin/flv.sh";
        final String PFAD_WINDOWS_SCRIPT = "bin\\flv.bat";
        switch (getOs()) {
            case LINUX:
                pfadScript = ProgInfos.getPathJar() + PFAD_LINUX_SCRIPT;
                break;
            default:
                pfadScript = PFAD_WINDOWS_SCRIPT;
        }
        return pfadScript;
    }

    public static boolean addSetVorlagen(SetList pSet) {
        if (pSet == null) {
            return false;
        }
        for (final SetData ps : pSet) {
            if (!ps.getAdOn().isEmpty() && !addOnZip(ps.getAdOn())) {
                // und Tschüss
                return false;
            }
        }

        if (Daten.getInstance().setList.addPset(pSet)) {
            Config.SYSTEM_VERSION_PROGRAMMSET.setValue(pSet.version);
            return true;
        } else {
            return false;
        }
    }

    private static boolean addOnZip(String datei) {
        final String zielPfad = FileUtils.addsPfad(ProgInfos.getPathJar(), "bin");
        File zipFile;
        final int timeout = 10_000; //10 Sekunden
        int n;
        HttpURLConnection conn;
        try {
            if (!FileUtils.istUrl(datei)) {
                zipFile = new File(datei);
                if (!zipFile.exists()) {
                    // und Tschüss
                    return false;
                }
                if (datei.endsWith(Const.FORMAT_ZIP)) {
                    if (!entpacken(zipFile, new File(zielPfad))) {
                        // und Tschüss
                        return false;
                    }
                } else {
                    try (FileInputStream in = new FileInputStream(datei);
                         FileOutputStream fOut = new FileOutputStream(FileUtils.addsPfad(zielPfad, datei))) {
                        final byte[] buffer = new byte[1024];
                        while ((n = in.read(buffer)) != -1) {
                            fOut.write(buffer, 0, n);
                        }
                    }
                }
            } else {
                conn = (HttpURLConnection) new URL(datei).openConnection();
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
                conn.setRequestProperty("User-Agent", ProgInfos.getUserAgent());
                if (datei.endsWith(Const.FORMAT_ZIP)) {

                    final File tmpFile = File.createTempFile("mtplayer", null);
                    tmpFile.deleteOnExit();
                    try (BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                         FileOutputStream fOut = new FileOutputStream(tmpFile)) {
                        final byte[] buffer = new byte[1024];
                        while ((n = in.read(buffer)) != -1) {
                            fOut.write(buffer, 0, n);
                        }
                    }
                    if (!entpacken(tmpFile, new File(zielPfad))) {
                        // und Tschüss
                        return false;
                    }

                } else {
                    final String file = FileUtils.getDateiName(datei);
                    final File f = new File(FileUtils.addsPfad(zielPfad, file));
                    try (BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                         FileOutputStream fOut = new FileOutputStream(f)) {
                        final byte[] buffer = new byte[1024];
                        while ((n = in.read(buffer)) != -1) {
                            fOut.write(buffer, 0, n);
                        }
                    }
                }
            }
        } catch (final Exception ignored) {
        }
        return true;
    }

    private static boolean entpacken(File archive, File destDir) throws Exception {
        if (!destDir.exists()) {
            return false;
        }


        try (ZipFile zipFile = new ZipFile(archive)) {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();

            final byte[] buffer = new byte[16384];
            int len;
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();

                final String entryFileName = entry.getName();

                final File dir = buildDirectoryHierarchyFor(entryFileName, destDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                if (!entry.isDirectory()) {
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destDir, entryFileName)));
                         BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry))) {
                        while ((len = bis.read(buffer)) > 0) {
                            bos.write(buffer, 0, len);
                        }
                        bos.flush();
                    }
                }
            }
        }

        return true;
    }

    private static File buildDirectoryHierarchyFor(String entryName, File destDir) {
        final int lastIndex = entryName.lastIndexOf('/');
        final String internalPathToEntry = entryName.substring(0, lastIndex + 1);
        return new File(destDir, internalPathToEntry);
    }

    public static boolean praefixTesten(String str, String uurl, boolean praefix) {
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
                    if (praefix) {
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

    public static boolean checkPfadBeschreibbar(String pfad) {
        boolean ret = false;
        final File testPfad = new File(pfad);
        try {
            if (!testPfad.exists()) {
                testPfad.mkdirs();
            }
            if (pfad.isEmpty()) {
            } else if (!testPfad.isDirectory()) {
            } else if (testPfad.canWrite()) {
                final File tmpFile = File.createTempFile("mtplayer", "tmp", testPfad);
                tmpFile.delete();
                ret = true;
            }
        } catch (final Exception ignored) {
        }
        return ret;
    }

    public static boolean programmePruefen(Daten daten) {
        // prüfen ob die eingestellten Programmsets passen
        final String PIPE = "| ";
        final String LEER = "      ";
        final String PFEIL = " -> ";
        boolean ret = true;
        String text = "";

        for (final SetData psetData : daten.setList) {
            ret = true;
            if (!psetData.isFreeLine() && !psetData.isLable()) {
                // nur wenn kein Lable oder freeline
                text += "++++++++++++++++++++++++++++++++++++++++++++" + '\n';
                text += PIPE + "Programmgruppe: " + psetData.getName() + '\n';
                final String zielPfad = psetData.getDestPath();
                if (psetData.progsContainPath()) {
                    // beim nur Abspielen wird er nicht gebraucht
                    if (zielPfad.isEmpty()) {
                        ret = false;
                        text += PIPE + LEER + "Zielpfad fehlt!\n";
                    } else // Pfad beschreibbar?
                        if (!checkPfadBeschreibbar(zielPfad)) {
                            //da Pfad-leer und "kein" Pfad schon abgeprüft
                            ret = false;
                            text += PIPE + LEER + "Falscher Zielpfad!\n";
                            text += PIPE + LEER + PFEIL + "Zielpfad \"" + zielPfad + "\" nicht beschreibbar!" + '\n';
                        }
                }
                for (final ProgData progData : psetData.getProgList()) {
                    // Programmpfad prüfen
                    if (progData.getProgPath().isEmpty()) {
                        ret = false;
                        text += PIPE + LEER + "Kein Programm angegeben!\n";
                        text += PIPE + LEER + PFEIL + "Programmname: " + progData.getName() + '\n';
                        text += PIPE + LEER + LEER + "Pfad: " + progData.getProgPath() + '\n';
                    } else if (!new File(progData.getProgPath()).canExecute()) {
                        // dann noch mit RuntimeExec versuchen
                        final RuntimeExec r = new RuntimeExec(progData.getProgPath());
                        final Process pr = r.exec(false /*log*/);
                        if (pr != null) {
                            // dann passts ja
                            pr.destroy();
                        } else {
                            // läßt sich nicht starten
                            ret = false;
                            text += PIPE + LEER + "Falscher Programmpfad!\n";
                            text += PIPE + LEER + PFEIL + "Programmname: " + progData.getName() + '\n';
                            text += PIPE + LEER + LEER + "Pfad: " + progData.getProgPath() + '\n';
                            if (!progData.getProgPath().contains(File.separator)) {
                                text += PIPE + LEER + PFEIL + "Wenn das Programm nicht im Systempfad liegt, " + '\n';
                                text += PIPE + LEER + LEER + "wird der Start nicht klappen!" + '\n';
                            }
                        }
                    }
                }
                if (ret) {
                    //sollte alles passen
                    text += PIPE + PFEIL + "Ok!" + '\n';
                }
                text += "++++++++++++++++++++++++++++++++++++++++++++" + "\n\n\n";
            }
        }
        new MTAlert().showInfoAlert("Set", "Sets prüfen", text);
        return ret;
    }
}
