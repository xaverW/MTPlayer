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

public class TipListFilter {

    private TipListFilter() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = "Der Filter der Filme ist in " +
                "mehrere Bereiche geteilt. " +
                "Oben sind die Textfilter " +
                "(z.B.: Thema oder Titel)." +
                "\n\n" +
                "Danach " +
                "kommen die Filter die nach " +
                "Filmeigenschaften (z.B.: " +
                "Filmlänge) suchen." +
                "\n\n" +
                "Unten sind die Einstellungen " +
                "der Filter. Dort kann ausgewählt " +
                "werden, welche Filter angezeigt " +
                "werden sollen. Dort können auch Filtereinstellungen " +
                "in Profilen gespeichert und " +
                "wieder abgerufen werden.";
        String image = "/de/p2tools/mtplayer/res/tips/filter/filter-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Welche Filter angezeigt werden " +
                "sollen, kann mit dem \"Zahnrad\" " +
                "ausgewählt werden." +
                "\n\n" +
                "Mit den beiden Pfeilen " +
                "(rechts/links) kann in den " +
                "verwendeten Filtern zurück und " +
                "vorwärts geblättert werden." +
                "\n\n" +
                "Der \"Trichter\" löscht die Filter. " +
                "Der erste Klick darauf, löscht nur " +
                "die Textfilter oben. Der zweite " +
                "Klick löscht auch die weiteren " +
                "Filter darunter." +
                "\n\n" +
                "Unten kann man Filtereinstellungen " +
                "in einem Profil speichern und wieder " +
                "abrufen" +
                "\n\n" +
                "Ganz unter kann die Blacklist ein- und " +
                "ausgeschaltet werden. Mit dem Zahnrad daneben " +
                "kommt man zu den Einstellungen der Blacklist.";
        image = "/de/p2tools/mtplayer/res/tips/filter/filter-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Filter-Profile sind gespeicherte Filtereinstellungen." +
                "\n\n" +
                "Das beinhaltet, " +
                "welche Filter (Thema, Titel, ..) eingeschaltet sind. Auch der Suchtext eines Filters " +
                "(z.B. \"Sport\") wird im Profil gespeichert." +
                "\n\n" +
                "Mit der Auswahlbox wählt man eins aus, das " +
                "dann auch eingestellt wird. " +
                "Im Filterpanel werden dann die im Filter-Profil gespeicherten " +
                "Einstellungen angezeigt." +
                "\n\n" +
                "Mit dem \"Plus\" wird ein neues Profil angelegt. Der " +
                "\"Pfeil nach oben\" stellt das ausgewählte Profil wieder her. " +
                "Der \"Pfeil nach unten\" speichert die aktuellen Filtereinstellungen in " +
                "dem ausgewählten Profil. In dem Menü darunter können die Profile verwaltet (Sortiert, ..) " +
                "werden.";
        image = "/de/p2tools/mtplayer/res/tips/filter/filter-5.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Im Filterpanel werden hier die angelegten Filter-Profile " +
                "angezeigt." +
                "\n\n" +
                "In den Einstellungen kann man eins dieser " +
                "Filter-Profile auswählen, " +
                "das dann beim Programmstart eingestellt wird." +
                "\n\n" +
                "Ansonsten wird " +
                "immer der zuletzt verwendete Filter wieder eingestellt.";
        image = "/de/p2tools/mtplayer/res/tips/filter/filter-6.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Hier werden die zuletzt verwendeten Textfilter (Thema, Titel, ..) " +
                "angezeigt und können so wieder abgerufen werden.";
        image = "/de/p2tools/mtplayer/res/tips/filter/filter-7.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Im Menü (Filmmenü) kann ein vereinfachter " +
                "Filter eingestellt werden. Er enthält nur das " +
                "nötigste und ist dadurch übersichtlicher.";
        image = "/de/p2tools/mtplayer/res/tips/filter/filter-3.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Hier kann ein einfaches Filterfeld " +
                "eingestellt werden, wenn man nur einen Textfilter " +
                "zum Suchen braucht. Mit dem Button (zwei Pfeile) " +
                "kann ausgewählt werden, wo gesucht wird (Thema, Titel)." +
                "\n\n" +
                "Dazu zuerst den Filmfilter (links) so einstellen wie man möchte. " +
                "Dann diese Funktion in der Toolbar auswählen. Es ist dann nur " +
                "noch das Filterfeld oben zu sehen." +
                "\n\n" +
                "Jetzt wird dann mit den Filtereinstellungen " +
                "des Filmfilter (links, der jetzt ausgeblendet ist) und dem Filterfeld in " +
                "der Toolbar gefiltert.";
        image = "/de/p2tools/mtplayer/res/tips/filter/filter-4.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        return pToolTipList;
    }
}
