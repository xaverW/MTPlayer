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

import de.mtplayer.mLib.tools.*;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import javafx.scene.control.Label;

public class DownloadTools {


    /**
     * Calculate free disk space on volume and check if the movies can be safely downloaded.
     */
    public static void calculateAndCheckDiskSpace(Download download, String path, Label lblSizeFree) {
        if (path == null || path.isEmpty()) {
            return;
        }
        try {
            String noSize = "";
            long usableSpace = FileUtils.getFreeDiskSpace(path);
            String sizeFree = "";
            if (usableSpace == 0) {
                lblSizeFree.setText("");
            } else {
                sizeFree = SizeTools.humanReadableByteCount(usableSpace, true);
            }

            // jetzt noch prüfen, obs auf die Platte passt
            usableSpace /= 1_000_000;
            if (usableSpace > 0) {
                long size = download.getDownloadSize().getFilmSize();
                size /= 1_000_000;
                if (size > usableSpace) {
                    noSize = " [ nicht genug Speicher: ";

                }
            }

            if (noSize.isEmpty()) {
                lblSizeFree.setText(" [ noch frei: " + sizeFree + " ]");
            } else {
                lblSizeFree.setText(noSize + sizeFree + " ]");
            }


        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getDownloadPath() {
        return Config.START_DIALOG_DOWNLOAD_PATH.get().isEmpty() ?
                SysTools.getStandardDownloadPath() : Config.START_DIALOG_DOWNLOAD_PATH.get();
    }

    /**
     * Entferne verbotene Zeichen aus Dateiname.
     *
     * @param name        Dateiname
     * @param isPath
     * @param userReplace
     * @param onlyAscii
     * @return Bereinigte Fassung
     */
    public static String replaceLeerDateiname(String name, boolean isPath, boolean userReplace, boolean onlyAscii) {
        String ret = name;
        boolean isWindowsPath = false;
        if (SystemInfo.isWindows() && isPath && ret.length() > 1 && ret.charAt(1) == ':') {
            // damit auch "d:" und nicht nur "d:\" als Pfad geht
            isWindowsPath = true;
            ret = ret.replaceFirst(":", ""); // muss zum Schluss wieder rein, kann aber so nicht ersetzt werden
        }

        // zuerst die Ersetzungstabelle mit den Wünschen des Users
        if (userReplace) {
            ret = Daten.getInstance().replaceList.replace(ret, isPath);
        }

        // und wenn gewünscht: "NUR Ascii-Zeichen"
        if (onlyAscii) {
            ret = FileNameUtils.convertToASCIIEncoding(ret, isPath);
        } else {
            ret = FileNameUtils.convertToNativeEncoding(ret, isPath);
        }

        if (isWindowsPath) {
            // c: wieder herstellen
            if (ret.length() == 1) {
                ret = ret + ":";
            } else if (ret.length() > 1) {
                ret = ret.charAt(0) + ":" + ret.substring(1);
            }
        }
        return ret;
    }
}
