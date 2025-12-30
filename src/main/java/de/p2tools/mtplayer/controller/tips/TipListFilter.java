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
                "(z.B.: Thema oder Titel). Danach " +
                "kommen die Filter die nach " +
                "Filmeigenschaften (z.B.: " +
                "Filmlänge) suchen." +
                "\n\n" +
                "Unten sind die Einstellungen " +
                "der Filter. Dort kann ausgewählt " +
                "werden, welche Filter angezeigt " +
                "werden sollen." +
                "\n\n" +
                "Dort können auch Filtereinstellungen " +
                "in Profilen gespeichert und " +
                "wieder abgerufen werden." +
                "";
        String image = "/de/p2tools/mtplayer/res/tips/filter/filter-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Welche Filter angezeigt werden " +
                "sollen, kann mit dem \"Zahnrad\" " +
                "ausgewählt werden." +
                "\n\n" +
                "Der \"Trichter\" löscht die Filter. " +
                "Der erste Klick darauf, löscht nur " +
                "die Textfilter oben. Der zweite " +
                "Klick löscht auch die weiteren " +
                "Filter darunter." +
                "\n\n" +
                "Mit den beiden Pfeilen " +
                "(rechts/links) kann in den " +
                "verwendeten Filtern zurück und " +
                "vorwärts geblättert werden." +
                "\n\n" +
                "Unten kann man Filtereinstellungen " +
                "in einem Profil speichern. Das " +
                "\"Plus\" legt ein neues Profil mit " +
                "den aktuellen Filtereinstellungen " +
                "an. Der \"Pfeil nach oben\" stellt " +
                "das ausgewählte Profil wieder her. " +
                "Der \"Pfeil nach unter\" speichert " +
                "die aktuellen Filtereinstellungen in " +
                "dem ausgewählten Profile.";
        image = "/de/p2tools/mtplayer/res/tips/filter/filter-2.png";
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
                "kann ausgewählt werden, wo gesucht wird (Thema, Titel).";
        image = "/de/p2tools/mtplayer/res/tips/filter/filter-4.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        return pToolTipList;
    }
}
