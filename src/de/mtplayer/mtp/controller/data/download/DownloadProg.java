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

package de.mtplayer.mtp.controller.data.download;

import de.mtplayer.mLib.tools.FileNameUtils;
import de.mtplayer.mLib.tools.FileUtils;
import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.data.ProgramData;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.controller.data.abo.AboXml;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.p2tools.p2Lib.tools.SysTools;
import de.p2tools.p2Lib.tools.log.PLog;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.File;
import java.util.Date;


public class DownloadProg {

    private final Download download;
    private SetData pSet = null;

    public DownloadProg(Download download) {
        this.download = download;
    }

    public boolean makeProgParameter(SetData pSet, Film film, Abo abo, String name, String path) {
        this.pSet = pSet;
        // zieldatei und pfad bauen und eintragen
        try {
            final ProgramData progData = pSet.getProgUrl(download.getUrl());
            if (progData == null) {
                return false; //todo ist das gut da wenn kein Set zum Download???
            }

            // ##############################################
            // pSet und ... eintragen
            download.setSet(pSet.getName());

            // Direkter Download nur wenn url passt und wenn im Programm ein Zielpfad ist sonst Abspielen
            //legt fest, dass NICHT Abspielen, Abspielen immer über Programm!
            download.setArt((pSet.checkDownloadDirect(download.getUrl()) && pSet.progsContainPath()) ?
                    DownloadInfos.ART_DOWNLOAD : DownloadInfos.ART_PROGRAM);
            if (download.getArt().equals(DownloadInfos.ART_DOWNLOAD)) {
                download.setProgram(DownloadInfos.ART_DOWNLOAD);
            } else {
                download.setProgram(progData.getName());
            }

            download.setProgramRestart(progData.isRestart());
            download.setProgramDownloadmanager(progData.isDownManager());
            buildFileNamePath(pSet, film, abo, name, path);
            buildProgParameter(progData);
        } catch (final Exception ex) {
            PLog.errorLog(825600145, ex);
        }
        return true;
    }

    private void buildProgParameter(ProgramData program) {
        if (download.getArt().equals(DownloadInfos.ART_DOWNLOAD)) {
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


    private void buildFileNamePath(SetData pSet, Film film, Abo abo, String nname, String ppath) {
        // nname und ppfad sind nur belegt, wenn der Download über den DialogAddDownload gestartet wurde
        // (aus TabFilme)
        String name;
        String path;
        if (!pSet.progsContainPath()) {
            // dann können wir uns das sparen
            download.setDestFileName("");
            download.setDestPath("");
            return;
        }

        // ##############################################
        // Name
        // ##############################################
        if (!nname.equals("")) {
            // wenn vorgegeben, dann den nehmen
            name = nname;
        } else {
            name = pSet.getDestFileName(download.getUrl());
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
            if (pSet.getMaxSize() > 0) {
                int length = pSet.getMaxSize();
                name = FileUtils.cutName(name, length);
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
            if (pSet.getDestPath().isEmpty()) {
                path = SysTools.getStandardDownloadPath();
            } else {
                path = pSet.getDestPath();
            }

            if (abo != null) {
                // Abos: den Namen des Abos eintragen
                download.setAboName(abo.getName());
                if (pSet.getGenTheme()) {
                    // und Abopfad an den Pfad anhängen
                    path = FileUtils.addsPath(path, FileNameUtils.removeIllegalCharacters(abo.arr[AboXml.ABO_DEST_PATH], true));
                }
            } else // Downloads
                if (pSet.getGenTheme()) {
                    // und den Namen des Themas an den Zielpfad anhängen
                    path = FileUtils.addsPath(path,
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
            path = SysTools.getStandardDownloadPath();
        }
        if (name.isEmpty()) {
            name = getToday_yyyyMMdd() + "_" + download.getTheme() + "-" + download.getTitle() + ".mp4";
        }

        // in Win dürfen die Pfade nicht länger als 255 Zeichen haben (für die Infodatei kommen noch
        // ".txt" dazu)
        final String[] pathName = {path, name};
        FileUtils.checkLengthPath(pathName);

        download.setDestFileName(pathName[1]);
        download.setDestPath(pathName[0]);
        download.setDestPathFile(FileUtils.addsPath(pathName[0], pathName[1]));
    }

    private String replaceString(String replStr, Film film) {
        // hier wird nur ersetzt!
        // Felder mit variabler Länge, evtl. vorher kürzen

        int length = pSet.getMaxField();

        replStr = replStr.replace("%t", getField(film.arr[FilmXml.FILM_THEME], length));
        replStr = replStr.replace("%T", getField(film.arr[FilmXml.FILM_TITLE], length));
        replStr = replStr.replace("%s", getField(film.arr[FilmXml.FILM_CHANNEL], length));
        replStr = replStr.replace("%N", getField(FileUtils.getFileName(download.getUrl()), length));

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
                getDMY("%1", film.arr[FilmXml.FILM_DATE].equals("") ? getToday_yyyy_MM_dd() : film.arr[FilmXml.FILM_DATE]));
        replStr = replStr.replace("%2",
                getDMY("%2", film.arr[FilmXml.FILM_DATE].equals("") ? getToday_yyyy_MM_dd() : film.arr[FilmXml.FILM_DATE]));
        replStr = replStr.replace("%3",
                getDMY("%3", film.arr[FilmXml.FILM_DATE].equals("") ? getToday_yyyy_MM_dd() : film.arr[FilmXml.FILM_DATE]));

        replStr = replStr.replace("%4",
                getHMS("%4", film.arr[FilmXml.FILM_TIME].equals("") ? getNow_HH_MM_SS() : film.arr[FilmXml.FILM_TIME]));
        replStr = replStr.replace("%5",
                getHMS("%5", film.arr[FilmXml.FILM_TIME].equals("") ? getNow_HH_MM_SS() : film.arr[FilmXml.FILM_TIME]));
        replStr = replStr.replace("%6",
                getHMS("%6", film.arr[FilmXml.FILM_TIME].equals("") ? getNow_HH_MM_SS() : film.arr[FilmXml.FILM_TIME]));

        replStr = replStr.replace("%i", String.valueOf(film.nr));

        String res = "";
        if (download.getUrl().equals(film.getUrlForResolution(FilmXml.RESOLUTION_NORMAL))) {
            res = "H";
        } else if (download.getUrl().equals(film.getUrlForResolution(FilmXml.RESOLUTION_HD))) {
            res = "HD";
        } else if (download.getUrl().equals(film.getUrlForResolution(FilmXml.RESOLUTION_SMALL))) {
            res = "L";
        } else if (download.getUrl().equals(film.getUrlFlvstreamerForResolution(FilmXml.RESOLUTION_NORMAL))) {
            res = "H";
        } else if (download.getUrl().equals(film.getUrlFlvstreamerForResolution(FilmXml.RESOLUTION_HD))) {
            res = "HD";
        } else if (download.getUrl().equals(film.getUrlFlvstreamerForResolution(FilmXml.RESOLUTION_SMALL))) {
            res = "L";
        }
        replStr = replStr.replace("%q", res); // %q Qualität des Films ("HD", "H", "L")

        replStr = replStr.replace("%S", FileUtils.getSuffixFromUrl(download.getUrl()));
        replStr = replStr.replace("%Z", FileUtils.getHash(download.getUrl()));
        replStr = replStr.replace("%z",
                FileUtils.getHash(download.getUrl()) + "."
                        + FileUtils.getSuffixFromUrl(download.getUrl()));

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
        return StringFormatters.FORMATTER_HHmmss.format(new Date());
    }

    private String getToday_yyyyMMdd() {
        return StringFormatters.FORMATTER_yyyyMMdd.format(new Date());
    }

    private String getToday_yyyy_MM_dd() {
        return StringFormatters.FORMATTER_ddMMyyyy.format(new Date());
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

        execString = execString.replace("%a", download.getDestPath());
        execString = execString.replace("%b", download.getDestFileName());

        return execString;
    }
}
