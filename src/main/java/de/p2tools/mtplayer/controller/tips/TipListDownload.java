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

import de.p2tools.mtplayer.controller.config.ProgConfig;
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
        text += "Im Download-Filter Panel können\n" +
                "auch noch weitere Einstellungen\n" +
                "vorgenommen werden:\n\n" +
                "* Die maximale Anzahl der\n" +
                "   gleichzeitigen Downloads kann\n" +
                "   hier vorgegeben werden.\n\n" +
                "* Die maximale Bandbreite pro\n" +
                "   Download kann hier\n" +
                "   vorgegeben werden.";
        String image = "/de/p2tools/mtplayer/res/tips/Bandbreite.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        pToolTipList.add(TipData.getTipWebsite(ProgConfig.SYSTEM_PROG_OPEN_URL));


        text = START;
        text += "In den Einstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "wird der Downloadpfad\n" +
                "und -name für Downloads\n" +
                "vorgegeben. Der Hilfebutton\n" +
                "verrät die Möglichkeiten.";
        image = "/de/p2tools/mtplayer/res/tips/Einstellungen_SetSpeicherziel.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
