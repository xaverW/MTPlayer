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

package de.p2tools.mtplayer.controller.data.download;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.setdata.ProgramData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.gui.configdialog.panesetdata.AboSubDir;
import de.p2tools.p2lib.mtfilm.tools.FileNameUtils;
import de.p2tools.p2lib.tools.PSystemUtils;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.file.P2FileUtils;
import de.p2tools.p2lib.tools.log.PLog;
import de.p2tools.p2lib.tools.net.PUrlTools;

import java.io.File;
import java.nio.file.Paths;
import java.util.Date;


public class DownloadFactoryProgram {

    private DownloadFactoryProgram() {
    }

    public static boolean makeProgParameter(DownloadData download, AboData abo, String name, String path) {
        // zieldatei und pfad bauen und eintragen
        try {
            final ProgramData programData = download.getSetData().getProgUrl(download.getUrl());
            if (programData == null) {
                return false; //todo ist das gut da wenn kein Set zum Download???
            }

            // Direkter Download nur wenn url passt und wenn im Programm ein Zielpfad ist sonst Abspielen
            // legt fest, dass NICHT Abspielen, Abspielen immer über Programm!
            download.setType((download.getSetData().checkDownloadDirect(download.getUrl()) && download.getSetData().progsContainPath()) ?
                    DownloadConstants.TYPE_DOWNLOAD : DownloadConstants.TYPE_PROGRAM);

            if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
                download.setProgramName(DownloadConstants.TYPE_DOWNLOAD);
            } else {
                download.setProgramName(programData.getName());
            }

            download.setProgramDownloadmanager(programData.isDownManager());

            buildFileNamePath(download, download.getSetData(), abo, name, path);
            buildProgParameter(download, programData);
        } catch (final Exception ex) {
            PLog.errorLog(825600145, ex);
        }
        return true;
    }

    private static void buildProgParameter(DownloadData download, ProgramData program) {
        if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
            download.setProgramCall("");
            download.setProgramCallArray("");
        } else {
            String befehlsString = program.getProgrammAufruf();
            befehlsString = buildUrl(download, befehlsString);
            befehlsString = replaceExec(download, befehlsString);
            download.setProgramCall(befehlsString);

            String progArray = program.getProgrammAufrufArray();
            progArray = buildUrl(download, progArray);
            progArray = replaceExec(download, progArray);
            download.setProgramCallArray(progArray);
        }
    }

    private static void buildFileNamePath(DownloadData download, SetData setData,
                                          AboData abo, String nname, String ppath) {
        // nname und ppfad sind nur belegt, wenn der Download über den DialogAddDownload gestartet wurde
        // (aus TabFilme)
        String name;
        String path;

        // ##############################################
        // Name
        // ##############################################
        if (!nname.isEmpty()) {
            // wenn vorgegeben, dann den nehmen
            name = nname;
        } else {
            name = setData.getDestFileName(download.getUrl());
            // ##############################
            // Name sinnvoll belegen
            if (name.isEmpty()) {
                name = getToday_yyyyMMdd() + "_" + download.getTheme() + "-" + download.getTitle() + ".mp4";
            }

            // Tags ersetzen
            name = replaceString(download, name); // %D ... ersetzen

            String suff = "";
            if (name.contains(".")) {
                // Suffix (und den . ) nicht ändern
                suff = name.substring(name.lastIndexOf("."));
                if (suff.length() <= 4 && suff.length() > 1) {
                    // dann ist es sonst was??
                    name = name.substring(0, name.lastIndexOf("."));
                } else {
                    suff = "";
                }
            }

            name = DownloadDataFactory.replaceEmptyFileName(name,
                    false /* pfad */,
                    ProgConfig.SYSTEM_USE_REPLACETABLE.getValue(),
                    ProgConfig.SYSTEM_ONLY_ASCII.getValue());
            name = name + suff;

            // prüfen ob das Suffix 2x vorkommt
            if (name.length() > 8) {
                final String suf1 = name.substring(name.length() - 8, name.length() - 4);
                final String suf2 = name.substring(name.length() - 4);
                if (suf1.startsWith(".") && suf2.startsWith(".")) {
                    if (suf1.equalsIgnoreCase(suf2)) {
                        name = name.substring(0, name.length() - 4);
                    }
                }
            }

            // Kürzen
            if (setData.getMaxSize() > 0) {
                int length = setData.getMaxSize();
                name = P2FileUtils.cutName(name, length);
            }
        }

        // ##############################################
        // Pfad
        // ##############################################
        if (!ppath.isEmpty()) {
            // wenn vorgegeben, dann den nehmen
            path = ppath;
        } else {
            // Pfad sinnvoll belegen
            if (setData.getDestPath().isEmpty()) {
                path = PSystemUtils.getStandardDownloadPath();
            } else {
                path = setData.getDestPath();
            }

            if (abo != null) {
                // bei Abos: den Namen des Abos eintragen und evtl. den Pfad erweitern
                download.setAboName(abo.getName());
                if (setData.isGenAboSubDir() || !abo.getAboSubDir().trim().isEmpty()) {
                    // und Abopfad an den Pfad anhängen
                    // wenn im Set angegeben oder im Abo ein Pfad angegeben
                    String addPpath;
                    if (!abo.getAboSubDir().trim().isEmpty()) {
                        addPpath = abo.getAboSubDir();

                    } else {
                        AboSubDir.ENSubDir ENSubDir = AboSubDir.getENSubDir(setData.getAboSubDir_ENSubDirNo());
                        switch (ENSubDir) {
                            case TITLE:
                                addPpath = download.getTitle();
                                break;
                            case SENDER:
                                addPpath = download.getChannel();
                                break;
                            case ABONAME:
                                addPpath = abo.getName();
                                break;
                            case SENDEDATUM:
                                addPpath = download.getFilmDate().get_yyyy_MM_dd();
                                break;
                            case DOWNLOADDATUM:
                                addPpath = getToday_yyyy_MM_dd();
                                break;
                            case THEME:
                            default:
                                addPpath = download.getTheme();
                                break;
                        }
                    }
                    addPpath = addPpath.trim();
                    if (!addPpath.isEmpty()) {
                        path = P2FileUtils.addsPath(path, FileNameUtils.removeIllegalCharacters(addPpath, true));
                    }
                }

            } else if (setData.isGenAboSubDir()) {
                // direkte Downloads
                // und den Namen des Themas an den Zielpfad anhängen
                // --> das wird aber nur beim ersten mal klappen, dann wird im
                // Downloaddialog immer der letzte Pfad zuerst angeboten
                path = P2FileUtils.addsPath(path,
                        DownloadDataFactory.replaceEmptyFileName(download.getTheme(),
                                true /* pfad */,
                                ProgConfig.SYSTEM_USE_REPLACETABLE.getValue(),
                                ProgConfig.SYSTEM_ONLY_ASCII.getValue()));
            }

            path = replaceString(download, path); // %D ... ersetzen
        }

        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }

        // ###########################################################
        // zur Sicherheit bei Unsinn im Set
        if (path.isEmpty()) {
            path = PSystemUtils.getStandardDownloadPath();
        }
        if (name.isEmpty()) {
            name = getToday_yyyyMMdd() + "_" + download.getTheme() + "-" + download.getTitle() + ".mp4";
        }

        // in Win dürfen die Pfade nicht länger als 255 Zeichen haben (für die Infodatei kommen noch
        // ".txt" dazu)
        final String[] pathName = {path, name};
        P2FileUtils.checkLengthPath(pathName);
        download.setFile(Paths.get(path, name).toFile());
    }

    private static String replaceString(DownloadData download, String replStr) {
        // hier wird nur ersetzt!
        // Felder mit variabler Länge, evtl. vorher kürzen

        int length = download.getSetData().getMaxField();

        replStr = replStr.replace("%t", getField(download.getTheme(), length));
        replStr = replStr.replace("%T", getField(download.getTitle(), length));
        replStr = replStr.replace("%s", getField(download.getChannel(), length));
        replStr = replStr.replace("%N", getField(PUrlTools.getFileName(download.getUrl()), length));

        // Felder mit fester Länge werden immer ganz geschrieben
        replStr = replStr.replace("%D",
                download.getFilmDate().isEmpty() ? getToday_yyyyMMdd()
                        : cleanDate(turnDate(download.getFilmDateStr())));
        replStr = replStr.replace("%d",
                download.getFilmTime().isEmpty() ? getNow_HHMMSS()
                        : cleanDate(download.getFilmTime()));
        replStr = replStr.replace("%H", getToday_yyyyMMdd());
        replStr = replStr.replace("%h", getNow_HHMMSS());

        replStr = replStr.replace("%1",
                getDMY("%1", download.getFilmDateStr().isEmpty() ? getToday__yyyy_o_MM_o_dd() : download.getFilmDateStr()));
        replStr = replStr.replace("%2",
                getDMY("%2", download.getFilmDateStr().isEmpty() ? getToday__yyyy_o_MM_o_dd() : download.getFilmDateStr()));
        replStr = replStr.replace("%3",
                getDMY("%3", download.getFilmDateStr().isEmpty() ? getToday__yyyy_o_MM_o_dd() : download.getFilmDateStr()));

        replStr = replStr.replace("%4",
                getHMS("%4", download.getFilmTime().isEmpty() ? getNow_HH_MM_SS() : download.getFilmTime()));
        replStr = replStr.replace("%5",
                getHMS("%5", download.getFilmTime().isEmpty() ? getNow_HH_MM_SS() : download.getFilmTime()));
        replStr = replStr.replace("%6",
                getHMS("%6", download.getFilmTime().isEmpty() ? getNow_HH_MM_SS() : download.getFilmTime()));

        replStr = replStr.replace("%i", String.valueOf(download.getFilmNo()));

        String res = "";
        if (download.getUrl().equals(download.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL))) {
            res = "H";
        } else if (download.getUrl().equals(download.getUrlForResolution(FilmDataMTP.RESOLUTION_HD))) {
            res = "HD";
        } else if (download.getUrl().equals(download.getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL))) {
            res = "L";
        }
        replStr = replStr.replace("%q", res); // %q Qualität des Films ("HD", "H", "L")

        replStr = replStr.replace("%S", PUrlTools.getSuffixFromUrl(download.getUrl()));
        replStr = replStr.replace("%Z", P2FileUtils.getHash(download.getUrl()));
        replStr = replStr.replace("%z",
                P2FileUtils.getHash(download.getUrl()) + "."
                        + PUrlTools.getSuffixFromUrl(download.getUrl()));

        return replStr;
    }

    private static String getField(String name, int length) {
        name = DownloadDataFactory.replaceEmptyFileName(name,
                false /* pfad */,
                ProgConfig.SYSTEM_USE_REPLACETABLE.getValue(),
                ProgConfig.SYSTEM_ONLY_ASCII.getValue());

        if (length <= 0) {
            return name;
        }

        if (name.length() > length) {
            name = name.substring(0, length);
        }
        return name;
    }

    private static String getNow_HHMMSS() {
        return P2DateConst.F_FORMAT_HHmmss.format(new Date());
    }

    private static String getNow_HH_MM_SS() {
        return P2DateConst.F_FORMAT_HH__mm__ss.format(new Date());
    }

    private static String getToday_yyyyMMdd() {
        return P2DateConst.F_FORMAT_yyyyMMdd.format(new Date());
    }

    private static String getToday_yyyy_MM_dd() {
        return P2DateConst.F_FORMAT_yyyy_MM_dd.format(new Date());
    }

    private static String getToday__yyyy_o_MM_o_dd() {
        return P2DateConst.F_FORMAT_dd_MM_yyyy.format(new Date());
    }

    private static String getDMY(String s, String datum) {
        // liefert das Datum: Jahr - Monat - Tag aus dd.MM.yyyy
        // %1 - Tag
        // %2 - Monat
        // %3 - Jahr
        String ret = "";
        if (!datum.equals("")) {
            try {
                if (datum.length() == 10) {
                    switch (s) {
                        case "%1":
                            ret = datum.substring(0, 2); // Tag
                            break;
                        case "%2":
                            ret = datum.substring(3, 5); // Monat
                            break;
                        case "%3":
                            ret = datum.substring(6); // Jahr
                            break;

                    }
                }
            } catch (final Exception ex) {
                PLog.errorLog(775421006, ex, datum);
            }
        }
        return ret;
    }

    private static String getHMS(String s, String zeit) {
        // liefert die Zeit: Stunde, Minute, Sekunde aus "HH:mm:ss"
        // %4 - Stunde
        // %5 - Minute
        // %6 - Sekunde
        String ret = "";
        if (!zeit.equals("")) {
            try {
                if (zeit.length() == 8) {
                    switch (s) {
                        case "%4":
                            ret = zeit.substring(0, 2); // Stunde
                            break;
                        case "%5":
                            ret = zeit.substring(3, 5); // Minute
                            break;
                        case "%6":
                            ret = zeit.substring(6); // Sekunde
                            break;

                    }
                }
            } catch (final Exception ex) {
                PLog.errorLog(775421006, ex, zeit);
            }
        }
        return ret;
    }

    private static String turnDate(String date) {
        String ret = "";
        if (!date.equals("")) {
            try {
                if (date.length() == 10) {
                    String tmp = date.substring(6); // Jahr
                    tmp += "." + date.substring(3, 5); // Monat
                    tmp += "." + date.substring(0, 2); // Tag
                    ret = tmp;
                }
            } catch (final Exception ex) {
                PLog.errorLog(775421006, ex, date);
            }
        }
        return ret;
    }

    private static String cleanDate(String date) {
        String ret;
        ret = date;
        ret = ret.replace(":", "");
        ret = ret.replace(".", "");
        return ret;
    }

    private static String buildUrl(DownloadData downloadData, String execString) {
        // die URL bauen
        if (downloadData.getUrlList().size() <= 1) {
            return execString.replace("%f", downloadData.getUrl());
        }

        final String TRENNER;
        // dann sind es mehrere Filme
        if (execString.contains(DownloadConstants.TRENNER_PROG_ARRAY)) {
            // dann solls das Array sein
            TRENNER = DownloadConstants.TRENNER_PROG_ARRAY;
        } else {
            // dann ist der einfache Aufruf
            TRENNER = " ";
        }
        StringBuilder url = new StringBuilder();
        boolean append = false;
        for (String u : downloadData.getUrlList()) {
            if (!append) {
                append = true;
            } else {
                url.append(TRENNER);
            }
            url.append(u);
        }
        return execString.replace("%f", url);
    }

    private static String replaceExec(DownloadData downloadData, String execString) {
        // hier werden die Parameter beim Programmaufruf ersetzt
        execString = execString.replace("**", downloadData.getDestPathFile());

        if (downloadData != null) {
            //ist für Button z.B. "search in google"
            execString = execString.replace("%w", downloadData.getUrlWebsite());
            execString = execString.replace("%t", downloadData.getTheme());
            execString = execString.replace("%T", downloadData.getTitle());
            execString = execString.replace("%s", downloadData.getChannel());
        } else {
            execString = execString.replace("%w", "");
            execString = execString.replace("%t", "");
            execString = execString.replace("%T", "");
            execString = execString.replace("%s", "");
        }

        execString = execString.replace("%a", downloadData.getDestPath());
        execString = execString.replace("%b", downloadData.getDestFileName());

        return execString;
    }
}
