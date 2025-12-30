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

public class TipListFilm {

    private static final String START = "                                                     " +
            P2LibConst.LINE_SEPARATOR;
    private static final int listSize = 17;

    private TipListFilm() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();


        String text = START;
        text += "Live-Suche in den\n" +
                "ARD/ZDF Mediatheken:\n\n" +
                "Ist die Live-Suche eingeschaltet,\n" +
                "kann damit in den Mediatheken\n" +
                "von ARD und ZDF gesucht werden.\n" +
                "Es ist auch möglich, mit der URL\n" +
                "einer Filmseite den Film zu suchen.\n" +
                "Die Filme können dann ganz normal\n" +
                "angesehen oder gespeichert werden.\n" +
                "In den Einstellungen kann die \n" +
                "Live-Suche ausgeblendet werden.";
        String image = "/de/p2tools/mtplayer/res/tips/Live_Suche.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Suche in der Mediensammlung:\n\n" +
                "Ein Doppelklick auf einen\n" +
                "Suchbegriff stellt diesen frei.\n\n" +
                "Der Button (Kreis) wählt aus, wo\n" +
                "(Dateiname/Pfad) gesucht werden\n" +
                "soll.";
        image = "/de/p2tools/mtplayer/res/tips/Mediensammlung.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = START;
        text += "In den Einstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "kann die Filmliste beim\n" +
                "Laden beschränkt werden:\n\n" +
                "Z.B. können nur die letzten\n" +
                "50 Tage geladen werden. Filme\n" +
                "die kein Datum haben, werden\n" +
                "immer geladen.\n\n" +
                "Das hat den Vorteil, dass die\n" +
                "Filmliste deutlich kleiner ist\n" +
                "und das Programm schneller\n" +
                "reagiert (vor allem wenn der\n" +
                "Rechner mit wenig Speicher\n" +
                "ausgestattet ist).";
        image = "/de/p2tools/mtplayer/res/tips/Einstellungen_Filmliste.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
