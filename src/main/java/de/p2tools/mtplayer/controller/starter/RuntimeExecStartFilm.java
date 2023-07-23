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

package de.p2tools.mtplayer.controller.starter;

import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.tools.log.PLog;

public class RuntimeExecStartFilm {

    public static final String TRENNER_PROG_ARRAY = "<>";
    private final DownloadData filmPlayerData;
    private final String strProgCall;
    private String[] arrProgCallArray;
    private String strProgCallArray;
    private Process process = null;

    public RuntimeExecStartFilm(DownloadData filmPlayerData) {
        this.filmPlayerData = filmPlayerData;

        this.strProgCall = filmPlayerData.getProgramCall();
        this.strProgCallArray = filmPlayerData.getProgramCallArray();
        arrProgCallArray = strProgCallArray.split(TRENNER_PROG_ARRAY);
        if (arrProgCallArray.length <= 1) {
            arrProgCallArray = null;
        }
    }

    //===================================
    // Public
    //===================================
    public Process exec(boolean log) {
        try {
            if (arrProgCallArray != null) {
                if (log) {
                    PLog.sysLog("=====================");
                    PLog.sysLog("Starte Array: ");
                    PLog.sysLog(" -> " + strProgCallArray);
                    PLog.sysLog("=====================");
                }
//                process = Runtime.getRuntime().exec(arrProgCallArray);
                new ProcessBuilder(arrProgCallArray).inheritIO().start();
            } else {
                if (log) {
                    PLog.sysLog("=====================");
                    PLog.sysLog("Starte nicht als Array:");
                    PLog.sysLog(" -> " + strProgCall);
                    PLog.sysLog("=====================");
                }
//                process = Runtime.getRuntime().exec(strProgCall);
                new ProcessBuilder(strProgCall).inheritIO().start().waitFor();
            }
        } catch (final Exception ex) {
            PLog.errorLog(450028932, ex, "Fehler beim Starten");
        }
        return process;
    }
}
