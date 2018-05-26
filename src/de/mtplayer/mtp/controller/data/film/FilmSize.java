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

package de.mtplayer.mtp.controller.data.film;

import de.p2tools.p2Lib.tools.log.PLog;

public class FilmSize implements Comparable<FilmSize> {

    public Long l = 0L;
    public String s = "";

    public FilmSize() {
    }

    public FilmSize(long ll) {
        l = ll;
        s = l.toString();
    }

    public FilmSize(Film film) {
        if (film.arr[FilmXml.FILM_SIZE].equals("<1")) {
            film.arr[FilmXml.FILM_SIZE] = "1";
        }
        try {
            if (!film.arr[FilmXml.FILM_SIZE].isEmpty()) {
                l = Long.valueOf(film.arr[FilmXml.FILM_SIZE]);
                s = film.arr[FilmXml.FILM_SIZE];
            }
        } catch (final Exception ex) {
            PLog.errorLog(649891025, ex, "String: " + film.arr[FilmXml.FILM_SIZE]);
            l = 0L;
            s = "";
        }
    }

    @Override
    public String toString() {
        return s;
    }

    @Override
    public int compareTo(FilmSize ll) {
        return (l.compareTo(ll.l));
    }
}
