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

import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.setdata.ProgramData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.gui.configdialog.panesetdata.AboSubDir;
import de.p2tools.p2lib.mtfilm.tools.FileNameUtils;
import de.p2tools.p2lib.tools.P2InfoFactory;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.file.P2FileUtils;
import de.p2tools.p2lib.tools.log.P2Log;
import de.p2tools.p2lib.tools.net.PUrlTools;

import java.io.File;
import java.nio.file.Paths;
import java.util.Date;


public class DownloadFactoryMakeParameter {

    private DownloadFactoryMakeParameter() {
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
            P2Log.errorLog(825600145, ex);
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
                                          AboData abo, String selName, String selPath) {
        String name = buildFileName(download, setData, abo, selName);
        String path = buildFilePath(download, setData, abo, selPath);

        // ###########################################################
        // zur Sicherheit bei Unsinn im Set
        if (path.isEmpty()) {
            path = P2InfoFactory.getStandardDownloadPath();
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

    private static String buildFileName(DownloadData download, SetData setData,
                                        AboData abo, String selName) {
        // selName ist nur belegt, wenn der Download über den DialogAddDownload gestartet wurde
        // (aus TabFilme)
        String name;
        if (!selName.isEmpty()) {
            // wenn vorgegeben, dann den nehmen
            name = selName;

        } else {
            if (abo != null && !abo.getAboFileName().isEmpty()) {
                // dann den aus dem Abo nehmen
                name = abo.getAboFileName();
            } else {
                // sonst den aus dem Set
                name = setData.getDestFileName(download.getUrl());
            }

            // ##############################
            // Name sinnvoll belegen
            if (name.isEmpty()) {
                name = getToday_yyyyMMdd() + "_" + download.getTheme() + "-" + download.getTitle() + ".mp4";
            }

            // Tags ersetzen
            name = replaceTags(download, name, false);

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

            name = DownloadFactory.replaceFileNameWithReplaceList(name, false /* pfad */);
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
        return name;
    }

//    private static String replaceName(DownloadData download, String name) {
//        StringBuilder ret = new StringBuilder();
//        String search = name;
//
//        while (!search.isEmpty()) {
//            String tag = getTag(search);
//            if (tag.isEmpty()) {
//                // dann ist keines drin
////                ret.append(DownloadFactory.replaceFileNameWithReplaceList(search, false /* pfad */));
//                ret.append(search);
//                search = "";
//            } else {
//                String check = search.substring(0, search.indexOf(tag));
////                ret.append(DownloadFactory.replaceFileNameWithReplaceList(check, false /* pfad */));
//                ret.append(check);
//                ret.append(replaceTags(download, tag, false));
//                search = search.substring(search.indexOf(tag) + tag.length());
//            }
//        }
//
//        return ret.toString();
//    }

    private static String buildFilePath(DownloadData download, SetData setData,
                                        AboData abo, String selPath) {
        // selPath ist nur belegt, wenn der Download über den DialogAddDownload gestartet wurde
        // (aus TabFilme)
        String path;
        if (!selPath.isEmpty()) {
            // wenn vorgegeben, dann den nehmen
            path = selPath;

        } else {
            if (abo != null && !abo.getAboDir().isEmpty()) {
                // eigener Pfad aus dem Abo
                path = abo.getAboDir();

            } else {
                // Pfad aus dem Set
                if (setData.getDestPath().isEmpty()) {
                    path = P2InfoFactory.getStandardDownloadPath();
                } else {
                    path = setData.getDestPath();
                }
            }

            if (abo == null) {
                if (setData.isGenAboSubDir()) {
                    // das sind direkte Downloads und wenn "genAboSubDir" an ist, wird ein Unterordner mit Thema angelegt??
                    // --> das wird aber nur beim ersten mal klappen, dann wird im
                    // DownloadDialog immer der letzte Pfad zuerst angeboten
                    path = P2FileUtils.addsPath(path,
                            DownloadFactory.replaceFileNameWithReplaceList(download.getTheme(), true /* pfad */));
                }
            }

            if (abo != null) {
                // bei Abos: den Namen des Abos eintragen und evtl. den Pfad erweitern
                download.setAboName(abo.getName());

                if (abo.getAboDir().isEmpty()) {
                    // wenn das Abo keinen Pfad hat, dann evtl. einen SubDir anhängen
                    String addPpath = "";

                    if (!abo.getAboSubDir().trim().isEmpty()) {
                        // SubDir aus dem Abo anhängen
                        addPpath = abo.getAboSubDir();

                    } else if (setData.isGenAboSubDir()) {
                        // SubDir aus dem Set anhängen
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
            }

//            path = replaceTags(download, path, true); // %D ... ersetzen und ReplaceList für die Tags!! anwenden
        }

        // nicht nur für Abos ändern und ReplaceList für die Tags!! anwenden
        path = replaceTags(download, path, true); // %D ... ersetzen

        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

//    private static String getTag(String search) {
//        final String[] tags = {"%t", "%T", "%s", "%N", "%D", "%d",
//                "%H", "%h", "%1", "%2", "%3", "%4", "%5", "%6",
//                "%i", "%q", "%S", "%Z", "%z"};
//        int i = -1;
//        String tag = "";
//
//        for (String s : tags) {
//            int ii = search.indexOf(s);
//            if (ii >= 0 && (i == -1 || i > ii)) {
//                i = ii;
//                tag = s;
//            }
//        }
//        return tag;
//    }

    public static String replaceTags(DownloadData download, String replStr, boolean andReplace) {
        // hier wird nur ersetzt!
        // Felder mit variabler Länge, evtl. vorher kürzen

        int length = download.getSetData().getMaxField();

        replStr = replStr.replace("%t", setMaxLength(download.getTheme(), length, andReplace));
        replStr = replStr.replace("%T", setMaxLength(download.getTitle(), length, andReplace));
        replStr = replStr.replace("%s", setMaxLength(download.getChannel(), length, andReplace));
        replStr = replStr.replace("%N", setMaxLength(PUrlTools.getFileName(download.getUrl()), length, andReplace));

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

    private static String setMaxLength(String name, int length, boolean andReplace) {
        if (andReplace) {
            name = DownloadFactory.replaceFileNameWithReplaceList(name, false /* pfad */);
        }

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
        if (!datum.isEmpty()) {
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
                P2Log.errorLog(775421006, ex, datum);
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
        if (!zeit.isEmpty()) {
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
                P2Log.errorLog(775421006, ex, zeit);
            }
        }
        return ret;
    }

    private static String turnDate(String date) {
        String ret = "";
        if (!date.isEmpty()) {
            try {
                if (date.length() == 10) {
                    String tmp = date.substring(6); // Jahr
                    tmp += "." + date.substring(3, 5); // Monat
                    tmp += "." + date.substring(0, 2); // Tag
                    ret = tmp;
                }
            } catch (final Exception ex) {
                P2Log.errorLog(775421006, ex, date);
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

        //ist für Button z.B. "search in google"
        execString = execString.replace("%w", downloadData.getUrlWebsite());
        execString = execString.replace("%t", downloadData.getTheme());
        execString = execString.replace("%T", downloadData.getTitle());
        execString = execString.replace("%s", downloadData.getChannel());

        execString = execString.replace("%a", downloadData.getDestPath());
        execString = execString.replace("%b", downloadData.getDestFileName());

        return execString;
    }
}
