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

package de.p2tools.mtplayer.controller.filmlist.loadFilmlist;

public class ListenerFilmlistLoadEvent {

    public String senderUrl = "";
    public String text;
    public double max;
    public double progress;
    public boolean error;
    public int countFoundFilms;

    public ListenerFilmlistLoadEvent(String senderUrl, String text, double max, double progress, int countFoundFilms, boolean error) {
        this.senderUrl = senderUrl;
        this.text = text;
        this.max = max;
        this.progress = progress;
        this.countFoundFilms = countFoundFilms;
        this.error = error;
    }

    public ListenerFilmlistLoadEvent(String senderUrl, String text, double progress, int countFoundFilms, boolean error) {
        this.senderUrl = senderUrl;
        this.text = text;
        max = 0;
        this.progress = progress;
        this.countFoundFilms = countFoundFilms;
        this.error = error;
    }

    public static ListenerFilmlistLoadEvent getEmptyEvent() {
        return new ListenerFilmlistLoadEvent("", "", 0, 0, false);
    }
}
