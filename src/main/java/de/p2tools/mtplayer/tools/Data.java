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

import de.p2tools.p2Lib.tools.GermanStringSorter;

public class Data<E> implements Comparable<E> {

    public static String TAG;
    public static String[] COLUMN_NAMES;
    public static String[] XML_NAMES;
    public static int MAX_ELEM;

    public String[] arr;

    public static GermanStringSorter sorter = GermanStringSorter.getInstance();

    public Data() {
    }

    public String[] makeArr(int max) {
        final String[] a = new String[max];
        for (int i = 0; i < max; ++i) {
            a[i] = "";
        }
        return a;
    }

    public void setPropsFromXml() {
    }

    public void setXmlFromProps() {
    }

    @Override
    public int compareTo(E o) {
        return 0;
    }

}
