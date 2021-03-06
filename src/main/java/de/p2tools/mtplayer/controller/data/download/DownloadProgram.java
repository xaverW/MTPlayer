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
import de.p2tools.mtplayer.controller.data.ProgramData;
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.controller.data.abo.Abo;
import de.p2tools.mtplayer.controller.data.film.Film;
import de.p2tools.mtplayer.controller.data.film.FilmXml;
import de.p2tools.mtplayer.gui.configDialog.setData.AboSubDir;
import de.p2tools.mtplayer.tools.FileNameUtils;
import de.p2tools.p2Lib.tools.PSystemUtils;
import de.p2tools.p2Lib.tools.date.PDateFactory;
import de.p2tools.p2Lib.tools.file.PFileUtils;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.net.PUrlTools;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.File;
import java.util.Date;


public class DownloadProgram {

    private final Download download;

    public DownloadProgram(Download download) {
        this.download = download;
    }

    public boolean makeProgParameter(Film film, Abo abo, String name, String path) {
        // zieldatei und pfad bauen und eintragen
        try {
            final ProgramData programData = download.getSetData().getProgUrl(download.getUrl());
            if (programData == null) {
                return false; //todo ist das gut da wenn kein Set zum Download???
            }

            // Direkter Download nur wenn url passt und wenn im Programm ein Zielpfad ist sonst Abspielen
            //legt fest, dass NICHT Abspielen, Abspielen immer über Programm!
            download.setType((download.getSetData().checkDownloadDirect(download.getUrl()) && download.getSetData().progsContainPath()) ?
                    DownloadConstants.TYPE_DOWNLOAD : DownloadConstants.TYPE_PROGRAM);
            if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
                download.setProgram(DownloadConstants.TYPE_DOWNLOAD);
            } else {
                download.setProgram(programData.getName());
            }

            download.setProgramRestart(programData.isRestart());
            download.setProgramDownloadmanager(programData.isDownManager());
            buildFileNamePath(download.getSetData(), film, abo, name, path);
            buildProgParameter(programData);
        } catch (final Exception ex) {
            PLog.errorLog(825600145, ex);
        }
        return true;
    }

    private void buildProgParameter(ProgramData program) {
        if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
            download.setProgramCall("");
            download.setProgramCallArray("");
        } else {
            String befehlsString = program.getProgrammAufruf();
            befehlsString = replaceExec(befehlsString);
            download.setProgramCall(befehlsString);

            String progArray = program.getProgrammAufrufArray();
            progArray = replaceExec(progArray);
            download.setProgramCallArray(progArray);
        }
    }


    private void buildFileNamePath(SetData setData, Film film, Abo abo, String nname, String ppath) {
        // nname und ppfad sind nur belegt, wenn der Download über den DialogAddDownload gestartet wurde
        // (aus TabFilme)
        String name;
        String path;
        //bei Downloadmanager ist es auch nicht enthalten aber für die Info.txt brauchts doch den Pfad
//        if (!setData.progsContainPath()) {
//            // dann können wir uns das sparen
//            download.setDestFileName("");
//            download.setDestPath("");
//            return;
//        }

        // ##############################################
        // Name
        // ##############################################
        if (!nname.equals("")) {
            // wenn vorgegeben, dann den nehmen
            name = nname;
        } else {
            name = setData.getDestFileName(download.getUrl());
            download.setDestFileName(name);
            // ##############################
            // Name sinnvoll belegen
            if (name.equals("")) {
                name = getToday_yyyyMMdd() + "_" + download.getTheme() + "-" + download.getTitle() + ".mp4";
            }

            // Tags ersetzen
            name = replaceString(name, film); // %D ... ersetzen

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

            name = DownloadTools.replaceEmptyFileName(name,
                    false /* pfad */,
                    Boolean.parseBoolean(ProgConfig.SYSTEM_USE_REPLACETABLE.get()),
                    Boolean.parseBoolean(ProgConfig.SYSTEM_ONLY_ASCII.get()));
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
                name = PFileUtils.cutName(name, length);
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
                        AboSubDir.DirName dirName = setData.getAboSubDir();
                        switch (dirName) {
                            case TITLE:
                                addPpath = download.getTitle();
                                break;
                            case SENDER:
                                addPpath = download.getChannel();
                                break;
                            case ABONAME:
                                addPpath = abo.getName();
                                break;
                           /* case ABODESCRIPTION:
                                addPpath = abo.getDescription();
                                break;*/
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
                        path = PFileUtils.addsPath(path, FileNameUtils.removeIllegalCharacters(addPpath, true));
                    }
                }

            } else if (setData.isGenAboSubDir()) {
                // direkte Downloads
                // und den Namen des Themas an den Zielpfad anhängen
                // --> das wird aber nur beim ersten mal klappen, dann wird im
                // Downloaddialog immer der letzte Pfad zuerst angeboten
                path = PFileUtils.addsPath(path,
                        DownloadTools.replaceEmptyFileName(download.getTheme(),
                                true /* pfad */,
                                Boolean.parseBoolean(ProgConfig.SYSTEM_USE_REPLACETABLE.get()),
                                Boolean.parseBoolean(ProgConfig.SYSTEM_ONLY_ASCII.get())));
            }

            path = replaceString(path, film); // %D ... ersetzen
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
        PFileUtils.checkLengthPath(pathName);

        download.setDestFileName(pathName[1]);
        download.setDestPath(pathName[0]);
        download.setDestPathFile(PFileUtils.addsPath(pathName[0], pathName[1]));
    }

    private String replaceString(String replStr, Film film) {
        // hier wird nur ersetzt!
        // Felder mit variabler Länge, evtl. vorher kürzen

        int length = download.getSetData().getMaxField();

        replStr = replStr.replace("%t", getField(film.arr[FilmXml.FILM_THEME], length));
        replStr = replStr.replace("%T", getField(film.arr[FilmXml.FILM_TITLE], length));
        replStr = replStr.replace("%s", getField(film.arr[FilmXml.FILM_CHANNEL], length));
        replStr = replStr.replace("%N", getField(PUrlTools.getFileName(download.getUrl()), length));

        // Felder mit fester Länge werden immer ganz geschrieben
        replStr = replStr.replace("%D",
                film.arr[FilmXml.FILM_DATE].equals("") ? getToday_yyyyMMdd()
                        : cleanDate(turnDate(film.arr[FilmXml.FILM_DATE])));
        replStr = replStr.replace("%d",
                film.arr[FilmXml.FILM_TIME].equals("") ? getNow_HHMMSS()
                        : cleanDate(film.arr[FilmXml.FILM_TIME]));
        replStr = replStr.replace("%H", getToday_yyyyMMdd());
        replStr = replStr.replace("%h", getNow_HHMMSS());

        replStr = replStr.replace("%1",
                getDMY("%1", film.arr[FilmXml.FILM_DATE].equals("") ? getToday__yyyy_o_MM_o_dd() : film.arr[FilmXml.FILM_DATE]));
        replStr = replStr.replace("%2",
                getDMY("%2", film.arr[FilmXml.FILM_DATE].equals("") ? getToday__yyyy_o_MM_o_dd() : film.arr[FilmXml.FILM_DATE]));
        replStr = replStr.replace("%3",
                getDMY("%3", film.arr[FilmXml.FILM_DATE].equals("") ? getToday__yyyy_o_MM_o_dd() : film.arr[FilmXml.FILM_DATE]));

        replStr = replStr.replace("%4",
                getHMS("%4", film.arr[FilmXml.FILM_TIME].equals("") ? getNow_HH_MM_SS() : film.arr[FilmXml.FILM_TIME]));
        replStr = replStr.replace("%5",
                getHMS("%5", film.arr[FilmXml.FILM_TIME].equals("") ? getNow_HH_MM_SS() : film.arr[FilmXml.FILM_TIME]));
        replStr = replStr.replace("%6",
                getHMS("%6", film.arr[FilmXml.FILM_TIME].equals("") ? getNow_HH_MM_SS() : film.arr[FilmXml.FILM_TIME]));

        replStr = replStr.replace("%i", String.valueOf(film.no));

        String res = "";
        if (download.getUrl().equals(film.getUrlForResolution(Film.RESOLUTION_NORMAL))) {
            res = "H";
        } else if (download.getUrl().equals(film.getUrlForResolution(Film.RESOLUTION_HD))) {
            res = "HD";
        } else if (download.getUrl().equals(film.getUrlForResolution(Film.RESOLUTION_SMALL))) {
            res = "L";
        } else if (download.getUrl().equals(film.getUrlFlvstreamerForResolution(Film.RESOLUTION_NORMAL))) {
            res = "H";
        } else if (download.getUrl().equals(film.getUrlFlvstreamerForResolution(Film.RESOLUTION_HD))) {
            res = "HD";
        } else if (download.getUrl().equals(film.getUrlFlvstreamerForResolution(Film.RESOLUTION_SMALL))) {
            res = "L";
        }
        replStr = replStr.replace("%q", res); // %q Qualität des Films ("HD", "H", "L")

        replStr = replStr.replace("%S", PUrlTools.getSuffixFromUrl(download.getUrl()));
        replStr = replStr.replace("%Z", PFileUtils.getHash(download.getUrl()));
        replStr = replStr.replace("%z",
                PFileUtils.getHash(download.getUrl()) + "."
                        + PUrlTools.getSuffixFromUrl(download.getUrl()));

        return replStr;
    }

    private String getField(String name, int length) {
        name = DownloadTools.replaceEmptyFileName(name,
                false /* pfad */,
                Boolean.parseBoolean(ProgConfig.SYSTEM_USE_REPLACETABLE.get()),
                Boolean.parseBoolean(ProgConfig.SYSTEM_ONLY_ASCII.get()));

        if (length <= 0) {
            return name;
        }

        if (name.length() > length) {
            name = name.substring(0, length);
        }
        return name;
    }

    private String getNow_HHMMSS() {
        return FastDateFormat.getInstance("HHmmss").format(new Date());
    }

    private String getNow_HH_MM_SS() {
        return PDateFactory.F_FORMAT_HH_mm_ss.format(new Date());
    }

    private String getToday_yyyyMMdd() {
        return PDateFactory.F_FORMAT_yyyyMMdd.format(new Date());
    }

    private String getToday_yyyy_MM_dd() {
        return PDateFactory.F_FORMAT_yyyy_MM_dd.format(new Date());
    }

    private String getToday__yyyy_o_MM_o_dd() {
        return PDateFactory.F_FORMAT_dd_MM_yyyy.format(new Date());
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


    private String replaceExec(String execString) {
        execString = execString.replace("**", download.getDestPathFile());
        execString = execString.replace("%f", download.getUrl());
        execString = execString.replace("%F", download.getUrlRtmp());
        if (download.getFilm() != null) {
            execString = execString.replace("%w", download.getFilm().getWebsite());
        }

        execString = execString.replace("%a", download.getDestPath());
        execString = execString.replace("%b", download.getDestFileName());

        return execString;
    }
}
