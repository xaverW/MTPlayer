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

public class DownloadInfos {

    // Fortschritt
    public static final double PROGRESS_1_PERCENT = 0.01; // 1%

    public static final double PROGRESS_NOT_STARTED = -1;
    public static final double PROGRESS_WAITING = 0;
    public static final double PROGRESS_STARTED = 0.001; // 0,1%


    public static final double PROGRESS_NEARLY_FINISHED = 0.995; //99,5%
    public static final double PROGRESS_FINISHED = 1; //100%

    // Startnummer (Reihenfolge) der Downloads
    public static final int DOWNLOAD_NUMBER_NOT_STARTED = Integer.MAX_VALUE;
    // FilmNr wenn kein Film mehr gefunden wird
    public static final int FILM_NUMBER_NOT_FOUND = Integer.MAX_VALUE;

    // Stati
    public static final int STATE_INIT = 0; // noch nicht gestart
    public static final int STATE_STOPPED = 1; // gestartet und wieder abgebrochen
    public static final int STATE_STARTED_WAITING = 2; // gestartet, warten auf das Downloaden
    public static final int STATE_STARTED_RUN = 3; //Download läuft
    public static final int STATE_FINISHED = 4; // fertig, Ok
    public static final int STATE_ERROR = 5; // fertig, fehlerhaft

//    public static final String STATE_INIT_STR = "0"; // noch nicht gestart
//    public static final String STATE_STOPPED_STR = "1"; // gestartet und wieder abgebrochen
//    public static final String STATE_STARTED_WAITING_STR = "2"; // gestartet, warten auf das Downloaden
//    public static final String STATE_STARTED_RUN_STR = "3"; //Download läuft
//    public static final String STATE_FINISHED_STR = "4"; // fertig, Ok
//    public static final String STATE_ERROR_STR = "5"; // fertig, fehlerhaft

    public static final String ALL = "";

    public static final String SRC_BUTTON = "Button";
    public static final String SRC_DOWNLOAD = "Download";
    public static final String SRC_ABO = "Abo";

    public static final String SRC_COMBO_DOWNLOAD = "nur Downloads";
    public static final String SRC_COMBO_ABO = "nur Abos";

    public static final String ART_DOWNLOAD = "direkter Download";
    public static final String ART_PROGRAM = "Programm";

    public static final String ART_COMBO_DOWNLOAD = "nur direkte Downloads";
    public static final String ART_COMBO_PROGRAM = "nur Programme";

    public static final String STATE_COMBO_NOT_STARTED = "noch nicht gestartet";
    public static final String STATE_COMBO_WAITING = "gestartet und wartet noch";
    public static final String STATE_COMBO_LOADING = "läuft";

    //Download wird so oft gestartet, falls er beim ersten Mal nicht anspringt
    public static final int START_COUNTER_MAX = 3;


    public static String getTextProgress(boolean dManager, int status, double progress) {
        String ret = "";

        if (progress == PROGRESS_NOT_STARTED) {
            if (status == STATE_STOPPED) {
                ret = "abgebrochen";
            } else {
                ret = "nicht gestartet";
            }
        } else if (progress == PROGRESS_WAITING) {
            ret = dManager ? "extern" : "warten";

        } else if (progress == PROGRESS_STARTED) {
            ret = dManager ? "extern" : "gestartet";
        } else if (progress > PROGRESS_STARTED && progress < PROGRESS_FINISHED) {
            if (dManager) {
                ret = "extern";
            } else {
                ret = Double.toString(progress / 10.0) + '%';
            }

        } else if (progress == PROGRESS_FINISHED && status == STATE_ERROR) {
            ret = dManager ? "extern:fehler" : "fehlerhaft";
        } else if (progress == PROGRESS_FINISHED) {
            ret = dManager ? "extern:fertig" : "fertig";
        }

        return ret;
    }


    public static String getTimeLeft(long timeLeftSeconds) {
        if (timeLeftSeconds > 300) {
            return Long.toString(Math.round(timeLeftSeconds / 60.0)) + " Min.";
        } else if (timeLeftSeconds > 230) {
            return "5 Min.";
        } else if (timeLeftSeconds > 170) {
            return "4 Min.";
        } else if (timeLeftSeconds > 110) {
            return "3 Min.";
        } else if (timeLeftSeconds > 60) {
            return "2 Min.";
        } else if (timeLeftSeconds > 30) {
            return "1 Min.";
        } else if (timeLeftSeconds > 20) {
            return "30 s";
        } else if (timeLeftSeconds > 10) {
            return "20 s";
        } else if (timeLeftSeconds > 0) {
            return "10 s";
        } else {
            return "";
        }
    }
}
