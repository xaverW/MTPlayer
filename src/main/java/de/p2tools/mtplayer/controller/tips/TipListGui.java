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

import java.util.ArrayList;
import java.util.List;

public class TipListGui {

    private TipListGui() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = "Das Programm kann in verschiedenen " +
                "Farben angezeigt werden." +
                "\n\n" +
                "In den Programm-Einstellungen " +
                "(->Farben des Programms) sind die " +
                "Einstellungen dafür. " +
                "Die Farben können frei ausgewählt " +
                "werden.";
        String image = "/de/p2tools/mtplayer/res/tips/gui/gui-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Alle Infofelder und Filter können " +
                "mit dem Symbol \"x\" ausgeblendet " +
                "werden. Mit dem Symbol \"Dreieck\" " +
                "kann man sie \"abreißen\" und " +
                "in einem Extrafenster anzeigen.";
        image = "/de/p2tools/mtplayer/res/tips/gui/gui-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Mit dem Button, wird eine " +
                "neue Filmliste geladen. " +
                "Ist der Text unterstrichen " +
                "signalisiert das, dass es " +
                "eine neue Filmliste gibt.";
        image = "/de/p2tools/mtplayer/res/tips/gui/gui-3.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Im Programm sind immer zwei " +
                "Menüs sichtbar." +
                "\n\n" +
                "Das obere enthält Menüpunkte, " +
                "die für das ganze Programm " +
                "wichtig sind." +
                "\n\n" +
                "Das untere Menü ist immer " +
                "für den jeweils angezeigten " +
                "Tab (Filme, Audios, " +
                "Live, Downloads, Abos).";
        image = "/de/p2tools/mtplayer/res/tips/gui/gui-4.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Mit einem Klick " +
                "(RECHTE-Maustaste) kann das " +
                "Gui zwischen Hell- und " +
                "Dunkel umgeschaltet werden. " +
                "Ein Doppelklick schaltet " +
                "zwischen den beiden Farbmodi um. " +
                "Im Menü selbst, gibt es auch " +
                "zwei Menüpunkte zum Umschalten.";
        image = "/de/p2tools/mtplayer/res/tips/gui/gui-5.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "In den Tabellen können mit der " +
                "Maus Zeilen selektiert werden. " +
                "Dazu einfach in die erste " +
                "Zeile klicken (Maustaste geklickt " +
                "halten) und dann die Maus " +
                "nach unten ziehen." +
                "\n\n" +
                "In den Tabellen kann " +
                "mit der Leertaste nach " +
                "unten \"geblättert\" werden. " +
                "Jeder Klick auf die Leertaste " +
                "schiebt den Tabelleninhalt " +
                "um eine Seite nach unten.";
        image = "/de/p2tools/mtplayer/res/tips/gui/gui-6.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        return pToolTipList;
    }
}
