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

package de.p2tools.mtplayer.controller.mediaDb;

public class MediaFileSize implements Comparable<MediaFileSize> {

    public Long sizeL = 0L;
    private String sizeStr = "";

    public MediaFileSize(long size) {
        setSize(size);
    }

    public MediaFileSize(String size) {
        setSize(size);
    }

    public void setSize(long size) {
        sizeL = size;
        setSizeFromLong();
    }

    public void setSize(String size) {
        try {
            sizeL = Long.parseLong(size);
        } catch (Exception ignore) {
            sizeL = 0L;
        }
        setSizeFromLong();
    }

    public long getSizeLong() {
        return sizeL;
    }

    public String getSizeAsStr() {
        return sizeL + "";
    }

    private void setSizeFromLong() {
        // l: Anzahl Bytes
        if (sizeL > 1000 * 1000) {
            // größer als 1MB sonst kann ich mirs sparen
            sizeStr = String.valueOf(sizeL / (1000 * 1000));

        } else if (sizeL > 0) {
            //0<....<1M
            sizeStr = "< 1";

        } else if (sizeL == 0) {
            sizeStr = "";
        }
    }

    @Override
    public String toString() {
        return sizeStr;
    }

    @Override
    public int compareTo(MediaFileSize ll) {
        return (sizeL.compareTo(ll.sizeL));
    }
}
