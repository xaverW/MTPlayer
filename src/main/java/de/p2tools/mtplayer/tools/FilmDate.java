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

package de.p2tools.mtplayer.tools;

import de.p2tools.p2Lib.tools.date.PDate;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("serial")
public class FilmDate extends PDate {

    // die Filme werden immer in der Zeitzone "Europe/Berlin" gesucht
    private static final FastDateFormat FORMAT_dd_MM_YYYY = FastDateFormat.getInstance("dd.MM.yyyy", TimeZone.getTimeZone("Europe/Berlin"));
    private static final FastDateFormat FORMAT_yyy_MM_dd = FastDateFormat.getInstance("yyyy.MM.dd", TimeZone.getTimeZone("Europe/Berlin"));

    public FilmDate() {
        super();
    }

    public FilmDate(long l) {
        super(l);
    }

    @Override
    public String toString() {
        if (this.getTime() == 0) {
            return "";
        } else {
            return FORMAT_dd_MM_YYYY.format(this);
        }
    }

    @Override
    public String toStringR() {
        if (this.getTime() == 0) {
            return FORMAT_yyy_MM_dd.format(new Date());
        } else {
            return FORMAT_yyy_MM_dd.format(this);
        }
    }
}
