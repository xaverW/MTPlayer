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

package de.p2tools.mtplayer.controller.history;

import de.p2tools.p2lib.mtfilm.tools.FilmDate;
import de.p2tools.p2lib.tools.GermanStringSorter;
import org.apache.commons.lang3.time.FastDateFormat;

public class HistoryData implements Comparable<HistoryData> {

    private final static FastDateFormat sdf_datum = FastDateFormat.getInstance("dd.MM.yyyy");
    private final static GermanStringSorter sorter = GermanStringSorter.getInstance();

    public final String title;
    public final String theme;
    public final String url;
    public FilmDate date;

    public HistoryData(String date, String theme, String title, String url) {
        this.title = title;
        this.theme = theme;
        this.url = url;
        try {
            this.date = new FilmDate(sdf_datum.parse(date).getTime());
        } catch (final Exception ignore) {
            this.date = new FilmDate(0);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getTheme() {
        return theme;
    }

    public String getUrl() {
        return url;
    }

    public FilmDate getDate() {
        return date;
    }

    @Override
    public int compareTo(HistoryData arg0) {
        return sorter.compare(getTitle(), arg0.getTitle());
    }
}
