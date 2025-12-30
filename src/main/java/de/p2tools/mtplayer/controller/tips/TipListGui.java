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

public class TipListGui {

    private static final String START = "                                                     " +
            P2LibConst.LINE_SEPARATOR;
    private static final int listSize = 17;

    private TipListGui() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = START;
        text += "Gui";
        TipData pToolTip = new TipData(text);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Alle Infofelder und Filter können\n" +
                "mit dem Symbol \"x\" ausgeblendet\n" +
                "werden. Mit dem Symbol \"Dreieck\"\n" +
                "kann man sie \"abreißen\" und\n" +
                "in einem Extrafenster anzeigen.";
        String image = "/de/p2tools/mtplayer/res/tips/gui/gui-1.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Mit dem Button, wird eine\n" +
                "neue Filmliste geladen.\n" +
                "Ist der Text unterstrichen\n" +
                "signalisiert das, dass es\n" +
                "eine neue Filmliste gibt.";
        image = "/de/p2tools/mtplayer/res/tips/gui/gui-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Im Programm sind immer zwei\n" +
                "Menüs sichtbar.\n\n" +
                "Das obere enthält Menüpunkte,\n" +
                "die für das ganze Programm\n" +
                "wichtig sind.\n\n" +
                "Das untere Menü ist immer\n" +
                "für den jeweils angezeigten\n" +
                "Tab (Filme, Audios,\n" +
                "Live, Downloads, Abos).";
        image = "/de/p2tools/mtplayer/res/tips/gui/gui-3.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Mit einem Klick\n" +
                "(RECHTE-Maustaste) kann das\n" +
                "Gui zwischen Hell- und\n" +
                "Dunkel umgeschaltet werden.\n" +
                "Ein Doppelklick schaltet\n" +
                "zwischen den beiden Farbmodi um.\n" +
                "Im Menü selbst, gibt es auch\n" +
                "zwei Menüpunkte zum Uschalten.";
        image = "/de/p2tools/mtplayer/res/tips/gui/gui-4.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = START;
        text += "In den Einstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "kann das Systemtray\n" +
                "ein- und ausgeschaltet werden.\n\n" +
                "Im Systemtray werden Infos\n" +
                "zur aktuellen Situation des\n" +
                "Programms angezeigt.";
        image = "/de/p2tools/mtplayer/res/tips/Einstellungen_Systemtray.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = START;
        text += "In den Einstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "kann die verwendete\n" +
                "Schriftgröße im Programm\n" +
                "vorgegeben werden.";
        image = "/de/p2tools/mtplayer/res/tips/Einstellungen_Schriftgroesse.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Tabellen kann\n" +
                "mit der Leertaste nach\n" +
                "unten \"geblättert\" werden.";
        image = "/de/p2tools/mtplayer/res/tips/Leertaste.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        return pToolTipList;
    }
}
