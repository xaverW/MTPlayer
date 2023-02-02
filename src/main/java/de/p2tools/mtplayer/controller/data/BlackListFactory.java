/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.data;

public class BlackListFactory {
    private BlackListFactory() {
    }

    public static void addStandardsList(BlackList list) {
        //nach Auftreten sortiert!
        BlackData bl = new BlackData("", "", "- Audiodeskription", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "", "(Audiodeskription)", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "", "(Gebärdensprache)", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "", "(mit Gebärdensprache)", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "", "(mit Untertitel)", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "", "(Originalversion mit Untertitel)", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "", "in Gebärdensprache", "");
        bl.setThemeExact(false);
        list.add(bl);
    }
}
