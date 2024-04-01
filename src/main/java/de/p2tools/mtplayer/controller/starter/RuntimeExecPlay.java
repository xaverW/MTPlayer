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
import de.p2tools.p2lib.tools.log.P2Log;

public class RuntimeExecPlay {

    public static final String TRENNER_PROG_ARRAY = "<>";
    private final String strProgCall;
    private String[] arrProgCallArray;
    private final String strProgCallArray;
    private Process process = null;

    public RuntimeExecPlay(DownloadData downloadData) {
        this.strProgCall = downloadData.getProgramCall();
        this.strProgCallArray = downloadData.getProgramCallArray();
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
                    P2Log.sysLog("=====================");
                    P2Log.sysLog("Starte Array: ");
                    P2Log.sysLog(" -> " + strProgCallArray);
                    P2Log.sysLog("=====================");
                }
                // process = Runtime.getRuntime().exec(arrProgCallArray);
                process = new ProcessBuilder(arrProgCallArray).inheritIO().start();
            } else {
                if (log) {
                    P2Log.sysLog("=====================");
                    P2Log.sysLog("Starte nicht als Array:");
                    P2Log.sysLog(" -> " + strProgCall);
                    P2Log.sysLog("=====================");
                }
                // process = Runtime.getRuntime().exec(strProgCall);
                process = new ProcessBuilder(strProgCall).inheritIO().start();
            }
        } catch (final Exception ex) {
            P2Log.errorLog(874587458, ex, "Fehler beim Starten");
        }
        return process;
    }
}
