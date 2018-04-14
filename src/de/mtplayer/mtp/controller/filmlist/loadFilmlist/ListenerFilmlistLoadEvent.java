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

package de.mtplayer.mtp.controller.filmlist.loadFilmlist;

public class ListenerFilmlistLoadEvent {

    public String senderUrl = "";
    public String text;
    public double max;
    public double progress;
    public boolean fehler;
    public int count;

    public ListenerFilmlistLoadEvent(String senderUrl, String text, double max, double progress, int count, boolean fehler) {
        this.senderUrl = senderUrl;
        this.text = text;
        this.max = max;
        this.progress = progress;
        this.count = count;
        this.fehler = fehler;
    }
}
