/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.gui.configdialog.panesetdata;

import de.p2tools.p2lib.tools.date.P2DateConst;

import java.util.Date;

public class AboSubDir {
    private static String date = P2DateConst.F_FORMAT_yyyy_MM_dd.format(new Date());

    public enum ENSubDir {
        // 0--> gibts nicht, da liefern die ProgVersionen ohne die Funktion dann "Thema" wie dort üblich
        SENDER("Sender", 1), THEME("Thema", 2), TITLE("Titel", 3),
        ABONAME("Abo-Name", 4),
        SENDEDATUM("Datum der Sendung z.B.: " + date, 6), DOWNLOADDATUM("Datum des Downloads z.B.: " + date, 7);
        private final String name;
        private final int no;

        ENSubDir(String name, int no) {
            this.name = name;
            this.no = no;
        }

        public int getNo() {
            return no;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static ENSubDir getENSubDir(int no) {
        for (ENSubDir s : ENSubDir.values()) {
            if (s.getNo() == no) {
                return s;
            }
        }
        return ENSubDir.THEME;
    }
}
