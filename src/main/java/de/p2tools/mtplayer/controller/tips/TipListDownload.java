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

public class TipListDownload {

    private TipListDownload() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = "Im Tab Download werden alle aktuellen " +
                "Downloads angezeigt. Hier können " +
                "Downloads gestartet und gelöscht " +
                "oder geändert werden." +
                "\n\n" +
                "In dem Infobereich unter der Tabelle " +
                "werden Infos dazu angezeigt. " +
                "Links neben der Tabelle kann dieselbe " +
                "gefiltert werden." +
                "\n\n" +
                "Unter den Filtern sind noch ein " +
                "paar Einstellungen für die Downloads: " +
                "Z.B.: die Download-Bandbreite.";
        String image = "/de/p2tools/mtplayer/res/tips/download/download-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "In dem Infobereich unter der Tabelle " +
                "können z.B.: die Mediensammlung und die erledigten " +
                "Abos durchsucht werden." +
                "\n\n" +
                "In einem Diagramm " +
                "wird die laufende Bandbreite der " +
                "Downloads angezeigt.";
        image = "/de/p2tools/mtplayer/res/tips/download/download-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Bein Anlegen oder Ändern eines Downloads, wird dieser Dialog " +
                "angezeigt." +
                "\n\n" +
                "Hier können die Einstellungen für den Download vorgenommen werden." +
                "\n\n" +
                "Die Auflösung (HD, hoch, niedrig) kann festgelegt werden. Der Dateiname " +
                "und der Speicherpfad können vorgegeben werden. Ob eine Infodatei oder die " +
                "Untertitel gespeichert werden sollen, wird auch hier festgelegt." +
                "\n\n" +
                "Zuletzt kann auch noch ausgewählt werden, ob der Download sofort starten soll oder ob er erst " +
                "zu einer bestimmten Uhrzeit starten soll (z.B.: erst nachts).";
        image = "/de/p2tools/mtplayer/res/tips/download/download-3.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        return pToolTipList;
    }
}
