/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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

import de.p2tools.p2Lib.tools.log.PLog;

/**
 * das sind die Meldungen die externe Programme liefern (z.B.: VLC)
 * und werden auch im PLog eingetragen
 */
public class PlayerMessage {

    private static int LINE_NO = 0;
    private final int lineNo;

    public PlayerMessage() {
        lineNo = ++LINE_NO;
    }

    public synchronized void playerMessage(String text) {
        playerMessage(new String[]{text});
    }

    private void playerMessage(String[] texte) {
        final String noStr = getNr(lineNo);

        for (int i = 0; i < texte.length; ++i) {
            final String z = "[" + noStr + "] >> " + texte[0];
            PLog.extToolLog(z);
        }
    }

    private static String getNr(int nr) {
        final int MAX_STELLEN = 5;
        final String FUELL_ZEICHEN = "0";
        String str = String.valueOf(nr);
        while (str.length() < MAX_STELLEN) {
            str = FUELL_ZEICHEN + str;
        }
        return str;
    }
}
