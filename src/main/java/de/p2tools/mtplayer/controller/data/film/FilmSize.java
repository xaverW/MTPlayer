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

package de.p2tools.mtplayer.controller.data.film;

import de.p2tools.p2Lib.tools.log.PLog;

public class FilmSize implements Comparable<FilmSize> {

    public long l = 0L;
    public String s = "";

    public FilmSize() {
    }

    void setFilmSize(Film film) {
        if (film.arr[FilmXml.FILM_SIZE].equals("<1")) {
            film.arr[FilmXml.FILM_SIZE] = "1";
        }

        try {
            s = film.arr[FilmXml.FILM_SIZE];
            if (film.arr[FilmXml.FILM_SIZE].isEmpty()) {
                l = 0L;
            } else {
                l = Long.valueOf(film.arr[FilmXml.FILM_SIZE]);
            }
        } catch (final Exception ex) {
            PLog.errorLog(649891025, ex, "String: " + film.arr[FilmXml.FILM_SIZE]);
            l = 0L;
            s = "";
        }
    }

    @Override
    public String toString() {
//     return    l == 0 ? "" : Long.toString(l);
        return s;
    }

    @Override
    public int compareTo(FilmSize compareWith) {
        return (l < compareWith.l) ? -1 : ((l == compareWith.l) ? 0 : 1);
    }
}
