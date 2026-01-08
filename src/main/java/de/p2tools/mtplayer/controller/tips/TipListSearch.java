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

public class TipListSearch {

    private TipListSearch() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = "In den Text-Suchfeldern werden die zuletzt eingegebenen " +
                "Suchbegriffe gespeichert und können wieder abgerufen werden." +
                "\n\n" +
                "Das \"X\" löscht einen Suchbegriff aus der Liste, das erste \"X\" löscht " +
                "die gesamte Liste.";
        String image = "/de/p2tools/mtplayer/res/tips/search/search-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "In den Text-Suchfeldern wird mit einem Doppelklick " +
                "ein Fenster geöffnet. Dort sind Filtervorschläge aufgelistet. Es kann " +
                "ein Vorschlag ausgewählt werden, der dann ins Filterfeld " +
                "eingefügt wird." +
                "\n\n" +
                "In den Programmeinstellungen können die Vorschläge angelegt werden. Die Funktion " +
                "kann dort auch abgeschaltet werden.";
        image = "/de/p2tools/mtplayer/res/tips/search/search-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Beim Suchen wird Groß- und Kleinschreibung nicht unterschieden. Gesucht wird, nach dem " +
                "eingegebenen Begriff, der muss im Film enthalten sein. Z.B.: \"Sport\" im Suchfeld \"Titel\" " +
                "sucht nach allen Filmen, die \"Sport\" im Titel enthalten." +
                "\n\n" +
                "Soll nach mehreren Begriffen gesucht werden, können die mit \",\" oder \":\" verkettet werden." +
                "\n\n" +
                "Beim \",\" muss nur einer der Suchbegriffe enthalten sein. \"Sport,Nachrichten\" sucht nach Filmen " +
                "die entweder \"Sport\" oder \"Nachrichten\" enthalten, z.B.: " +
                "\"Sportbericht\" oder \"Nachrichten des Tages\"" +
                "\n\n" +
                "Beim \":\" müssen beide Begriffe enthalten sein. \"Sport:Nachrichten\" sucht nach Filmen " +
                "die \"Sport\" und \"Nachrichten\" enthalten, z.B.: \"Sportnachrichten\"";
        image = "/de/p2tools/mtplayer/res/tips/search/search-3.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "In den Text-Filtern kann auch mit \"Regular Expression\" gesucht werden. " +
                "Diese müssen mit \"#:\" eingeleitet werden." +
                "\n\n" +
                "\"#:.*Sport\" sucht nach Filmen die mit \"Sport\" enden." +
                "\n" +
                "\"#:Sport.*\" such nach Filmen die mit \"Sport\" beginnen." +
                "\n" +
                "\"#:Sport\" sucht nach Filmen deren Titel genau \"Sport\" ist." +
                "\n\n" +
                "Das Programm prüft, ob die Regex korrekt ist. Das Suchfeld hat dann einen " +
                "grünen Hintergrund, bei einem Fehler ist es rot.";
        image = "/de/p2tools/mtplayer/res/tips/search/search-4.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        return pToolTipList;
    }
}
