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

import de.p2tools.p2lib.P2LibConst;

import java.util.ArrayList;
import java.util.List;

public class TipListSet {

    private static final String START = "                                                     " +
            P2LibConst.LINE_SEPARATOR;

    private TipListSet() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = START;
        text += "Sets sind die Einstellungen " +
                "die zum Aufzeichnen und Abspielen " +
                "der Filme gebraucht werden." +
                "\n\n" +
                "Es muss mindestens ein Set zum Abspielen " +
                "und eins zum Aufzeichnen vorhanden sein.\n\n" +
                "In den Programm-Einstellungen können die Sets " +
                "angelegt und geändert werden. " +
                "\n\n" +
                "Ein Set hat einen Namen (nur zur eigenen Info) und eine " +
                "Funktion (Abspielen, Speichern, ...) " +
                "Die Sets zum Speichern haben ein Speicherziel " +
                "(wo, wie und welchen Namen der gespeicherte Film haben soll." +
                "\n\n" +
                "In den Hilfsprogrammen wird aufgeführt, welches Programms " +
                "den Film abspielen oder speichern soll.";
        String image = "/de/p2tools/mtplayer/res/tips/set/set-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
