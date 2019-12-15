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


package de.mtplayer.mtp.gui.configDialog.setData;

public class AboSubDir {
    public enum DirName {

        THEME("Thema", 0), TITLE("Titel", 1), SENDER("Sender", 2),
        ABONAME("Abo-Name", 3), ABODESCRIPTION("Abo-Beschreibung", 4),
        SENDEDATUM("Datum der Sendung", 5), DOWNLOADDATUM("Datum des Downloads", 6);
        private final String name;
        private final int no;

        DirName(String name, int no) {
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

    public static String getName(int no) {
        for (DirName s : DirName.values()) {
            if (s.getNo() == no) {
                return s.getName();
            }
        }
        return "";
    }

    public static DirName getAboSub(int no) {
        for (DirName s : DirName.values()) {
            if (s.getNo() == no) {
                return s;
            }
        }
        return DirName.THEME;
    }
}
