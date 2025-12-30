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

public class TipListAbo {

    private static final String START = "                                                     " +
            P2LibConst.LINE_SEPARATOR;
    private static final int listSize = 17;

    private TipListAbo() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();


        String text = START;
        text += "Im Tab Abo werden die angelegten " +
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


        return pToolTipList;
    }
}
