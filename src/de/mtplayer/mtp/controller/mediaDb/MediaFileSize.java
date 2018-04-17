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

package de.mtplayer.mtp.controller.mediaDb;

public class MediaFileSize implements Comparable<MediaFileSize> {

    public Long sizeL = 0L;
    private String sizeStr = "";

    public MediaFileSize(long size) {
        sizeL = size;
        sizeStr = setSize(size);
    }

    public MediaFileSize(String size) {
        try {
            sizeL = Long.parseLong(size);
        } catch (Exception ignore) {
            sizeL = 0L;
        }
        sizeStr = setSize(sizeL);
    }

    @Override
    public int compareTo(MediaFileSize ll) {
        return (sizeL.compareTo(ll.sizeL));
    }

    @Override
    public String toString() {
        return sizeStr;
    }

    private String setSize(long l) {
        // l: Anzahl Bytes
        String ret = "";
        if (l > 1000 * 1000) {
            // größer als 1MB sonst kann ich mirs sparen
            ret = String.valueOf(l / (1000 * 1000));
        } else if (l > 0) {
            //0<....<1M
            ret = "< 1";
        } else if (l == 0) {
            ret = "0";
        }
        return ret;
    }
}
