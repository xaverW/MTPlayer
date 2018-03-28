/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

public class DownloadSizeData implements Comparable<DownloadSizeData> {
    Long l;
    String s;

    /**
     * damit wird nach long l sortiert und der Text s angezeigt
     *
     * @param l
     * @param s
     */
    DownloadSizeData(long l, String s) {
        this.l = l;
        this.s = s;
    }

    public int compareTo(DownloadSizeData d) {
        return d.l.compareTo(l);
    }

    @Override
    public String toString() {
        return s;
    }
}
