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


package de.p2tools.mtplayer.controller.config;

import de.p2tools.p2Lib.guiTools.pToolTip.PToolTip;
import de.p2tools.p2Lib.guiTools.pToolTip.PToolTipDialog;
import de.p2tools.p2Lib.guiTools.pToolTip.PToolTipFactory;

import java.util.ArrayList;
import java.util.List;

public class ProgToolTips {

    private int listSize = 3;

    public ProgToolTips() {
    }

    public void showDialog(ProgData progData, boolean showAlways) {
        if (!showAlways && ProgConfig.TOOLTIPS_DONT_SHOW.getBool()) {
            //dann wills der User nicht :(
            return;
        }

        if (ProgData.debug ||
                showAlways || PToolTipFactory.containsToolTipNotShown(ProgConfig.TOOLTIPS_SHOWN.get(), listSize)) {
            //nur wenn "immer" / oder noch nicht angezeigte ToolTips enthalten sind
            final List<PToolTip> pToolTipList = new ArrayList<>();
            addToolTips(pToolTipList);
            new PToolTipDialog(progData.primaryStage, pToolTipList,
                    ProgConfig.TOOLTIPS_SHOWN.getStringProperty(), ProgConfig.TOOLTIPS_DONT_SHOW.getBooleanProperty());
        }
    }

    private void addToolTips(List<PToolTip> pToolTipList) {
        String text = "Das ist der erste Tip";
        String image = "/de/p2tools/mtplayer/res/toolTips/toolTip_1.png";
        PToolTip pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        text = "Das ist der zweite Tip";
        image = "/de/p2tools/mtplayer/res/toolTips/toolTip_2.png";
        pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        text = "Das ist der dritte Tip";
        image = "/de/p2tools/mtplayer/res/toolTips/toolTip_3.png";
        pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);
    }
}
