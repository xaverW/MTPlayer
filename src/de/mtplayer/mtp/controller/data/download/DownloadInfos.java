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
    public static final double PROGRESS_1_PROZENT = 0.01; // 1%

    public static final double PROGRESS_NICHT_GESTARTET = -1;
    public static final double PROGRESS_WARTEN = 0;
    public static final double PROGRESS_GESTARTET = 0.001; // 0,1%


    public static final double PROGRESS_FAST_FERTIG = 0.995; //99,5%
    public static final double PROGRESS_FERTIG = 1; //100%

    // Startnummer (Reihenfolge) der Downloads
    public static final int DOWNLOAD_NUMBER_NOT_STARTED = Integer.MAX_VALUE;
    // FilmNr wenn kein Film mehr gefunden wird
    public static final int FILM_NUMBER_NOT_FOUND = Integer.MAX_VALUE;

    // Stati
    public static final int STATE_INIT = 0; // noch nicht gestart
    public static final int STATE_STOPED = 1; // gestartet und wieder abgebrochen
    public static final int STATE_STARTED_WAITING = 2; // gestartet, warten auf das Downloaden
    public static final int STATE_STARTED_RUN = 3; //Download läuft
    public static final int STATE_FINISHED = 4; // fertig, Ok
    public static final int STATE_ERROR = 5; // fertig, fehlerhaft

    public static final String SRC_ALL = "";
    public static final String SRC_BUTTON = "Button";
    public static final String SRC_DOWNLOAD = "Download";
    public static final String SRC_ABO = "Abo";

    public static final String SRC_COMBO_ALL = "";
    public static final String SRC_COMBO_DOWNLOAD = "nur Downloads";
    public static final String SRC_COMBO_ABO = "nur Abos";

    public static final String ART_ALL = "";
    public static final String ART_DOWNLOAD = "direkter Download";
    public static final String ART_PROGRAMM = "Programm";

    public static final String ART_COMBO_ALL = "";
    public static final String ART_COMBO_DOWNLOAD = "nur direkte Downloads";
    public static final String ART_COMBO_PROGRAMM = "nur Programme";

    //Download wird so oft gestartet, falls er beim ersten Mal nicht anspringt
    public static final int STARTCOUNTER_MAX = 3;


    public static String getTextProgress(boolean dManager, int status, double progress) {
        String ret = "";

        if (progress == PROGRESS_NICHT_GESTARTET) {
            if (status == STATE_STOPED) {
                ret = "abgebrochen";
            } else {
                ret = "nicht gestartet";
            }
        } else if (progress == PROGRESS_WARTEN) {
            ret = dManager ? "extern" : "warten";

        } else if (progress == PROGRESS_GESTARTET) {
            ret = dManager ? "extern" : "gestartet";
        } else if (progress > PROGRESS_GESTARTET && progress < PROGRESS_FERTIG) {
            if (dManager) {
                ret = "extern";
            } else {
                ret = Double.toString(progress / 10.0) + '%';
            }

        } else if (progress == PROGRESS_FERTIG && status == STATE_ERROR) {
            ret = dManager ? "extern:fehler" : "fehlerhaft";
        } else if (progress == PROGRESS_FERTIG) {
            ret = dManager ? "extern:fertig" : "fertig";
        }

        return ret;
    }


    public static String getTextRestzeit(long restSekunden) {
        if (restSekunden > 300) {
            return Long.toString(Math.round(restSekunden / 60.0)) + " Min.";
        } else if (restSekunden > 230) {
            return "5 Min.";
        } else if (restSekunden > 170) {
            return "4 Min.";
        } else if (restSekunden > 110) {
            return "3 Min.";
        } else if (restSekunden > 60) {
            return "2 Min.";
        } else if (restSekunden > 30) {
            return "1 Min.";
        } else if (restSekunden > 20) {
            return "30 s";
        } else if (restSekunden > 10) {
            return "20 s";
        } else if (restSekunden > 0) {
            return "10 s";
        } else {
            return "";
        }
    }
}
