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
    public final static String PARAMETER_URL = "%f";
    public final static String PARAMETER_PATH_FILE = "**";
    public final static String PARAMETER_WEBSITE = "%w";

    public final static String PARAMETER_CHANNEL = "%s";
    public final static String PARAMETER_THEME = "%t";
    public final static String PARAMETER_TITLE = "%T";
    public final static String PARAMETER_DURATION_MINUTE = "%m";
    public final static String PARAMETER_DURATION_MINUTE_000 = "%M";

    public final static String PARAMETER_DEST_PATH = "%a";
    public final static String PARAMETER_DEST_FILE_NAME = "%b";

    public final static String PARAMETER_ORG_FILM_NAME = "%N";
    public final static String PARAMETER_FILM_DATE = "%D";
    public final static String PARAMETER_FILM_TIME = "%d";

    public final static String PARAMETER_TODAY = "%H";
    public final static String PARAMETER_TIME_NOW = "%h";
    public final static String PARAMETER_DAY = "%1";
    public final static String PARAMETER_MONTH = "%2";
    public final static String PARAMETER_YEAR = "%3";
    public final static String PARAMETER_HOUR = "%4";
    public final static String PARAMETER_MINUTE = "%5";
    public final static String PARAMETER_SECOND = "%6";

    public final static String PARAMETER_FILM_NO = "%i";
    public final static String PARAMETER_QUALITY = "%q";
    public final static String PARAMETER_SUFFIX = "%S";
    public final static String PARAMETER_HASH = "%Z";
    public final static String PARAMETER_HASH_SUFFIX = "%z";


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

    public static String replaceTags(DownloadData download, String replStr, boolean andReplace) {
        // hier wird nur ersetzt!
        // Felder mit variabler Länge, evtl. vorher kürzen

        int length = download.getSetData().getMaxField();

        replStr = replStr.replace(PARAMETER_THEME, setMaxLength(download.getTheme(), length, andReplace));
        replStr = replStr.replace(PARAMETER_TITLE, setMaxLength(download.getTitle(), length, andReplace));
        replStr = replStr.replace(PARAMETER_CHANNEL, setMaxLength(download.getChannel(), length, andReplace));
        replStr = replStr.replace(PARAMETER_ORG_FILM_NAME, setMaxLength(PUrlTools.getFileName(download.getUrl()), length, andReplace));

        StringBuilder duration = new StringBuilder(download.getDurationMinute() + "");
        replStr = replStr.replace(PARAMETER_DURATION_MINUTE, duration.toString());
        while (duration.length() < 3) {
            duration.insert(0, "0");
        }
        replStr = replStr.replace(PARAMETER_DURATION_MINUTE_000, duration.toString());

        // Felder mit fester Länge werden immer ganz geschrieben
        replStr = replStr.replace(PARAMETER_FILM_DATE,
                download.getFilmDate().isEmpty() ? getToday_yyyyMMdd()
                        : cleanDate(turnDate(download.getFilmDateStr())));
        replStr = replStr.replace(PARAMETER_FILM_TIME,
                download.getFilmTime().isEmpty() ? getNow_HHMMSS()
                        : cleanDate(download.getFilmTime()));
        replStr = replStr.replace(PARAMETER_TODAY, getToday_yyyyMMdd());
        replStr = replStr.replace(PARAMETER_TIME_NOW, getNow_HHMMSS());

        replStr = replStr.replace(PARAMETER_DAY,
                getDMY(PARAMETER_DAY, download.getFilmDateStr().isEmpty() ? getToday__yyyy_o_MM_o_dd() : download.getFilmDateStr()));
        replStr = replStr.replace(PARAMETER_MONTH,
                getDMY(PARAMETER_MONTH, download.getFilmDateStr().isEmpty() ? getToday__yyyy_o_MM_o_dd() : download.getFilmDateStr()));
        replStr = replStr.replace(PARAMETER_YEAR,
                getDMY(PARAMETER_YEAR, download.getFilmDateStr().isEmpty() ? getToday__yyyy_o_MM_o_dd() : download.getFilmDateStr()));

        replStr = replStr.replace(PARAMETER_HOUR,
                getHMS(PARAMETER_HOUR, download.getFilmTime().isEmpty() ? getNow_HH_MM_SS() : download.getFilmTime()));
        replStr = replStr.replace(PARAMETER_MINUTE,
                getHMS(PARAMETER_MINUTE, download.getFilmTime().isEmpty() ? getNow_HH_MM_SS() : download.getFilmTime()));
        replStr = replStr.replace(PARAMETER_SECOND,
                getHMS(PARAMETER_SECOND, download.getFilmTime().isEmpty() ? getNow_HH_MM_SS() : download.getFilmTime()));

        replStr = replStr.replace(PARAMETER_FILM_NO, String.valueOf(download.getFilmNo()));

        String res = "";
        if (download.getUrl().equals(download.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL))) {
            res = "H";
        } else if (download.getUrl().equals(download.getUrlForResolution(FilmDataMTP.RESOLUTION_HD))) {
            res = "HD";
        } else if (download.getUrl().equals(download.getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL))) {
            res = "L";
        }
        replStr = replStr.replace(PARAMETER_QUALITY, res); // %q Qualität des Films ("HD", "H", "L")

        replStr = replStr.replace(PARAMETER_SUFFIX, PUrlTools.getSuffixFromUrl(download.getUrl()));
        replStr = replStr.replace(PARAMETER_HASH, P2FileUtils.getHash(download.getUrl()));
        replStr = replStr.replace(PARAMETER_HASH_SUFFIX,
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
                        case PARAMETER_DAY:
                            ret = datum.substring(0, 2); // Tag
                            break;
                        case PARAMETER_MONTH:
                            ret = datum.substring(3, 5); // Monat
                            break;
                        case PARAMETER_YEAR:
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
                        case PARAMETER_HOUR:
                            ret = zeit.substring(0, 2); // Stunde
                            break;
                        case PARAMETER_MINUTE:
                            ret = zeit.substring(3, 5); // Minute
                            break;
                        case PARAMETER_SECOND:
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
            return execString.replace(PARAMETER_URL, downloadData.getUrl());
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
        return execString.replace(PARAMETER_URL, url);
    }

    private static String replaceExec(DownloadData downloadData, String execString) {
        // hier werden die Parameter beim Programmaufruf ersetzt
        execString = execString.replace(PARAMETER_PATH_FILE, downloadData.getDestPathFile());

        //ist für Button z.B. "search in google"
        execString = execString.replace(PARAMETER_WEBSITE, downloadData.getUrlWebsite());
        execString = execString.replace(PARAMETER_THEME, downloadData.getTheme());
        execString = execString.replace(PARAMETER_TITLE, downloadData.getTitle());
        execString = execString.replace(PARAMETER_CHANNEL, downloadData.getChannel());

        execString = execString.replace(PARAMETER_DEST_PATH, downloadData.getDestPath());
        execString = execString.replace(PARAMETER_DEST_FILE_NAME, downloadData.getDestFileName());

        return execString;
    }
}
