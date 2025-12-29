/*
 * P2tools Copyright (C) 2021 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.tips;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.p2lib.P2LibConst;

import java.util.ArrayList;
import java.util.List;

public class TipListInfos {

    private static final String START = "                                                     " +
            P2LibConst.LINE_SEPARATOR;

    private TipListInfos() {
    }

    public static List<PTipOfDay> getTips() {
        List<PTipOfDay> pToolTipList = new ArrayList<>();


        pToolTipList.add(PTipOfDay.getTipWebsite(ProgConfig.SYSTEM_PROG_OPEN_URL));

        String text = START;
        text += "Über das Menü ist die\n" +
                "Funktion zum Zurücksetzen\n" +
                "von Programmeinstellungen\n" +
                "erreichbar.\n\n" +
                "Zuerst sollten nur die\n" +
                "Einstellungen zum Abspielen\n" +
                "und Aufzeichnen zurückgesetzt\n" +
                "werden.";
        String image = "/de/p2tools/mtplayer/res/tooltips/Ruecksetzen.png";
        PTipOfDay pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Über das Menü ist die\n" +
                "Funktion zum Zurücksetzen\n" +
                "von Programmeinstellungen\n" +
                "erreichbar.\n\n" +
                "Wenn gar nichts mehr geht,\n" +
                "kann das Programm komplett\n" +
                "zurückgesetzt werden.\n\n" +
                "Es werden alle Einstellungen\n" +
                "zurückgesetzt und gehen\n" +
                "verloren!";
        image = "/de/p2tools/mtplayer/res/tooltips/Ruecksetzen_alles.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);


        return pToolTipList;
    }
}
