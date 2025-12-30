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

public class TipListAbo {

    private static final String START = "                                                     " +
            P2LibConst.LINE_SEPARATOR;
    private static final int listSize = 17;

    private TipListAbo() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();


        String text = START;
        text += "In den Einstellungen des Abos\n" +
                "kann auch die maximale Zeit,\n" +
                "die zurück gesucht wird,\n" +
                "vorgegeben werden.\n\n" +
                "Es werden also nur Filme der\n" +
                "letzten xx Tage gesucht wenn\n" +
                "xx Tage dort vorgegeben sind.";
        String image = "/de/p2tools/mtplayer/res/tips/AboEinstellungen_Zeit.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Einstellungen des Abos\n" +
                "kann auch die Filmlänge\n" +
                "eines Films (min, max)\n" +
                "vorgegeben werden.\n\n" +
                "Es werden dann nur Filme\n" +
                "gefunden, deren Dauer zu\n" +
                "den Vorgaben passt.";
        image = "/de/p2tools/mtplayer/res/tips/AboEinstellungen_Dauer.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Einstellungen des Abos\n" +
                "kann auch vorgeben werden,\n" +
                "ob ein Beitrag in der\n" +
                "Filmliste/Audioliste oder\n" +
                "beiden gesucht werden soll.";
        image = "/de/p2tools/mtplayer/res/tips/AboEinstellungen_Quelle.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Einstellungen des Abos\n" +
                "kann auch die Startzeit\n" +
                "der Downloads aus diesem\n" +
                "Abo vorgegeben werden.\n\n" +
                "Downloads aus diesem Abo\n" +
                "haben dann automatisch\n" +
                "diese Startzeit.";
        image = "/de/p2tools/mtplayer/res/tips/AboEinstellungen_Startzeit.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Einstellungen des Abos\n" +
                "kann auch ein eigener Pfad\n" +
                "und auch ein eigener Dateiname\n" +
                "für Downloads aus diesem Abo\n" +
                "vorgegeben werden. Die Einstellungen\n" +
                "des Sets werden dann überschrieben.";
        image = "/de/p2tools/mtplayer/res/tips/AboEinstellungen_Pfad.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
