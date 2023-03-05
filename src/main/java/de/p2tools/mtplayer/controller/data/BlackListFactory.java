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

import java.util.List;

public class BlackListFactory {
    private BlackListFactory() {
    }

    public static boolean blackIsEmpty(BlackData blackData) {
        // true, wenn es das Black schon gibt
        if (blackData.getChannel().isEmpty() &&
                blackData.getTheme().isEmpty() &&
                blackData.getTitle().isEmpty() &&
                blackData.getThemeTitle().isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean blackExistsAlready(BlackData blackData, List<BlackData> list) {
        // true, wenn es das Black schon gibt
        for (final BlackData data : list) {
            if (data.getChannel().equalsIgnoreCase(blackData.getChannel()) &&
                    data.getTheme().equalsIgnoreCase(blackData.getTheme()) &&

                    ((data.getTheme().isEmpty() && blackData.getTheme().isEmpty()) ||
                            data.isThemeExact() == blackData.isThemeExact()) &&

                    data.getTitle().equalsIgnoreCase(blackData.getTitle()) &&
                    data.getThemeTitle().equalsIgnoreCase(blackData.getThemeTitle())) {
                return true;
            }
        }
        return false;
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
