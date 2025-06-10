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

package de.p2tools.mtplayer.controller.data.bookmark;

import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.mediathek.film.FilmDate;
import org.apache.commons.lang3.time.FastDateFormat;

public class BookmarkData extends BookmarkDataProps {

    private final static FastDateFormat sdf_datum = FastDateFormat.getInstance("dd.MM.yyyy");
    private FilmDataMTP filmData = null;

    public BookmarkData() {
    }

    public BookmarkData(FilmDataMTP filmDataMTP) {
        // beim Neuanlegen eines Bookmarks
        setChannel(filmDataMTP.getChannel());
        setTheme(filmDataMTP.getTheme());
        setTitle(filmDataMTP.getTitle());
        setUrl(filmDataMTP.getUrlHistory());
        setDate(new FilmDate());
        this.filmData = filmDataMTP;
    }

    public BookmarkData(String date, String theme, String title, String url) {
        // beim Einlesen der Datei
        setTitle(title);
        setTheme(theme);
        setUrl(url);
        try {
            FilmDate d = new FilmDate(sdf_datum.parse(date).getTime());
            setDate(d);
        } catch (final Exception ignore) {
            setDate(new FilmDate(0));
        }
    }

    public FilmDataMTP getFilmData() {
        return filmData;
    }

    public void setFilmData(FilmDataMTP filmData) {
        this.filmData = filmData;
    }
}
