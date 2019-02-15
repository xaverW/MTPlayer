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


package de.mtplayer.mtp.controller.data.download;

public class DownloadFactory {

    private DownloadFactory() {
    }

    // Anzahl, Anz-Abo, Anz-Down, nicht gestarted, laufen, fertig OK, fertig fehler
    public enum INFO {

        AMOUNT(0), AMOUNT_ABO(1), AMOUNT_DOWNLOAD(2), NOT_STARTED(3), LOADING(4), FINISHED_OK(5), FINISHED_NOT_OK(6);
        final private int i;

        INFO(int i) {
            this.i = i;
        }

        public int getI() {
            return i;
        }
    }
}
