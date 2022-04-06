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

public class DownloadState {

    //die Vorgaben des Users:
    public static final int DOWNLOAD_RESTART__ASK = 0;
    public static final int DOWNLOAD_RESTART__CONTINUE = 1;
    public static final int DOWNLOAD_RESTART__RESTART = 2;

    //Antwort des Dialogs, wenn der User gefragt wird
    public enum ContinueDownload {
        CANCEL_DOWNLOAD,
        CONTINUE_DOWNLOAD,
        RESTART_DOWNLOAD
    }
}
