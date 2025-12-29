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

public class TipListFilter {

    private static final String START = "                                                     " +
            P2LibConst.LINE_SEPARATOR;
    private static final int listSize = 17;

    private TipListFilter() {
    }

    public static List<PTipOfDay> getTips() {
        List<PTipOfDay> pToolTipList = new ArrayList<>();

        String text = START;
        text += "Die Filteransicht kann über einen\n" +
                "zweiten Klick mit der rechten\n" +
                "Maustaste auf den Tab-Button\n" +
                "(Filme, Downloads oder Abos)\n" +
                "ein- und ausgeblendet werden.\n\n" +
                "Ein Klick mit der linken\n" +
                "Maustaste blendet den\n" +
                "Infobereich unter der\n" +
                "Tabelle ein und aus.\n\n" +
                "Beides ist auch über das\n" +
                "Menü möglich";
        String image = "/de/p2tools/mtplayer/res/tooltips/GuiFilme_Filter_ein_aus.png";
        PTipOfDay pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Im Download-Filter Panel können\n" +
                "auch noch weitere Einstellungen\n" +
                "vorgenommen werden:\n\n" +
                "* Die maximale Anzahl der\n" +
                "   gleichzeitigen Downloads kann\n" +
                "   hier vorgegeben werden.\n\n" +
                "* Die maximale Bandbreite pro\n" +
                "   Download kann hier\n" +
                "   vorgegeben werden.";
        image = "/de/p2tools/mtplayer/res/tooltips/Bandbreite.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Einstellungen zum Filtern\n" +
                "im Tab Filme/Audios können\n" +
                "hier erreicht werden.";
        image = "/de/p2tools/mtplayer/res/tooltips/GuiFilme_Filter_1.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Filtereinstellungen bei\n" +
                "den Filmen/Audios können Filter\n" +
                "ein- und ausgeschaltet werden.\n" +
                "Beim Suchen werden nur\n" +
                "die eingeschalteten Filter\n" +
                "verwendet. Das Suchen ist schneller\n" +
                "wenn nicht alle Filter eingeschaltet sind.";
        image = "/de/p2tools/mtplayer/res/tooltips/GuiFilme_Filter_2.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Filtereinstellungen bei\n" +
                "den Filmen/Audios kann eine\n" +
                "Wartezeit vorgegeben werden.\n\n" +
                "Diese wird dann bei der Suche\n" +
                "in den Textfeldern abgewartet,\n" +
                "bis die Suche beginnt.\n\n" +
                "Es wird dann nicht bei jedem\n" +
                "Buchstaben die Suche gestartet.";
        image = "/de/p2tools/mtplayer/res/tooltips/GuiFilme_Filter_3.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Filmen/Audios kann ein\n" +
                "einfacher Filter über das\n" +
                "Menü eingestellt werden. Er\n" +
                "enthält nur das nötigste und\n" +
                "ist dadurch etwas leichter\n" +
                "zu bedienen.";
        image = "/de/p2tools/mtplayer/res/tooltips/EinfacherFilter.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
