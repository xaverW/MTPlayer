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


package de.p2tools.mtplayer.tools.filmFilter;

import java.util.regex.Pattern;

public class Filter {

    public String filter = "";
    public String[] filterArr = {""};
    public boolean filterAnd = false;
    public boolean exact = false;
    public Pattern pattern = null;
    public boolean empty = true;

    public Filter() {
    }

    public Filter(String filter, boolean makeArr) {
        this.filter = filter;
        this.filterArr = new String[]{filter};
        if (makeArr) {
            makeFilterArray();
        } else {
            makeFilter();
        }
    }

    public Filter(String filter, boolean exact, boolean makeArr) {
        this.filter = filter;
        this.filterArr = new String[]{filter};
        this.exact = exact;
        if (makeArr) {
            makeFilterArray();
        } else {
            makeFilter();
        }
    }

    public void makeFilter() {
        // keine Auftrennung mit ":" oder "," für z.B. URLs

        if (filter.isEmpty()) {
            filterArr = new String[]{""};
            pattern = null;
            empty = true;
            return;
        }

        empty = false;
        pattern = makePattern(filter);

        if (exact || pattern != null) {
            filterArr = new String[]{filter};

        } else {
            filterArr = new String[]{filter.trim().toLowerCase()};
        }

        checkArray();
    }

    public void makeFilterArray() {
        if (filter.isEmpty()) {
            filterArr = new String[]{""};
            pattern = null;
            empty = true;
            return;
        }

        empty = false;
        pattern = makePattern(filter);

        if (exact || pattern != null) {
            filterArr = new String[]{filter};

        } else {
            if (filter.contains(":")) {
                filterAnd = true;
                filterArr = filter.split(":");
            } else {
                filterAnd = false;
                filterArr = filter.split(",");
            }

            for (int i = 0; i < filterArr.length; ++i) {
                filterArr[i] = filterArr[i].trim().toLowerCase();
            }
        }

        checkArray();
    }

    private void checkArray() {
        if (filterArr == null || filterArr.length == 0) {
            filterArr = new String[]{""};
            pattern = null;
            empty = true;
        }
    }

    public static Pattern makePattern(String filter) {
        Pattern p = null;
        try {
            if (isPattern(filter)) {
                p = Pattern.compile(filter.substring(2), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
            }
        } catch (final Exception ex) {
            p = null;
        }
        return p;
    }

    public static boolean isPattern(String searchText) {
        return searchText.startsWith("#:");
    }
}

