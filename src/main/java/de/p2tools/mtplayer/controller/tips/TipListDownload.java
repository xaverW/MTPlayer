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

public class TipListDownload {

    private static final String START = "                                                     " +
            P2LibConst.LINE_SEPARATOR;
    private static final int listSize = 17;

    private TipListDownload() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = START;
        text += "Im Tab Download werden alle aktuellen " +
                "Downloads angezeigt. Hier können " +
                "Downloads gestartet und gelöscht " +
                "oder geändert werden." +
                "\n\n" +
                "In dem Infobereich unter der Tabelle " +
                "werden Infos dazu angezeigt. " +
                "Links neben der Tabelle kann dieselbe " +
                "gefiltert werden. " +
                "Unter den Filtern sind noch ein " +
                "paar Einstellungen für die Downloads: " +
                "Z.B.: Die Download-Bandbreite.";
        String image = "/de/p2tools/mtplayer/res/tips/download/download-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In dem Infobereich unter der Tabelle " +
                "kann z.B.: die Mediensammlung " +
                "durchsucht werden. In einem Diagramm " +
                "wird die laufende Bandbreite der " +
                "Downloads angezeigt.";
        image = "/de/p2tools/mtplayer/res/tips/download/download-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        return pToolTipList;
    }
}
