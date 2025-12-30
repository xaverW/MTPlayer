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

public class TipListAbo {

    private TipListAbo() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();


        String text = "Im Tab Abo werden die angelegten " +
                "Abos angezeigt. Abos können hier " +
                "angelegt, geändert und auch " +
                "ein- und ausgeschaltet werden." +
                "\n\n" +
                "Links neben der Tabelle, können " +
                "die Abos in der Tabelle " +
                "gefiltert werden.";
        String image = "/de/p2tools/mtplayer/res/tips/abo/abo-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Beim Anlegen eines neuen Abos, erscheint " +
                "dieser Dialog. Im ersten Tab (Abo), wird vorgegeben " +
                "wo gesucht werden soll (Filme, Audios oder beidem)." +
                "\n\n" +
                "Die Auflösung " +
                "des Films kann vorgegeben werden. Auch kann man vorgeben, wie weit " +
                "zurück, nach Filmen gesucht werden soll (keine Filme älter als ..). " +
                "\n\n" +
                "Es kann auch eine Startzeit des Downloads ausgewählt werden. Der " +
                "Download wird dann erst zu der Uhrzeit (z.B.: nur Nachts) gestartet.";
        image = "/de/p2tools/mtplayer/res/tips/abo/abo-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Im zweiten Tab werden die Suchbegriffe die im Film vorkommen müssen, " +
                "vorgegeben (z.B.: der Titel muss \"abc\" enthalten).";
        image = "/de/p2tools/mtplayer/res/tips/abo/abo-3.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Im letzten Tab wird das Ziel des Downloads ausgewählt. Im Speichern-Set " +
                "ist auch schon ein Ziel-Pfad vorgegeben. Standardmäßig wird " +
                "der genommen, er kann aber " +
                "im Abo auch überschrieben werden." +
                "\n\n" +
                "Beim Dateinamen ist es genauso: Standardmäßig werde die Vorgaben des Sets " +
                "genommen, auch diese können aber überschrieben werden" +
                "\n\n" +
                "Zuletzt wird unter \"Ergebnis\" angezeigt, wie der ausgewählte Pfad und " +
                "Dateiname aussehen.";
        image = "/de/p2tools/mtplayer/res/tips/abo/abo-4.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "In der Tabelle \"Filme\" kann mit einem \"Rechts-Klick\" " +
                "auf einen Film, direkt ein Abo dafür angelegt werden. " +
                "Film-Titel, Film-Thema oder beides werden dann schon in den " +
                "\"Abo-Anlegen-Dialog\" eingetragen.";
        image = "/de/p2tools/mtplayer/res/tips/abo/abo-5.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        return pToolTipList;
    }
}
