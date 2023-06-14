/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
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

package de.p2tools.mtplayer.gui.infoPane;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.p2lib.guitools.pclosepane.PClosePaneH;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AboInfoController extends PClosePaneH {

    private PaneAboInfo paneAboInfo;

    public AboInfoController() {
        super(ProgConfig.ABO_GUI_DIVIDER_ON, true, true);

        initInfoPane();
        setInfoTabPane();
    }

    public void setAboInfos(AboData abo) {
        paneAboInfo.setAbo(abo);
    }

    private void initInfoPane() {
        paneAboInfo = new PaneAboInfo();
        super.getRipProperty().addListener((u, o, n) -> {
            if (!ProgConfig.ABO_PANE_DIALOG_INFO_ON.getValue()) {
                // dann jetzt in den Dialog schicken
                dialogInfo();
            }

            setInfoTabPane();
        });

        if (ProgConfig.ABO_PANE_DIALOG_INFO_ON.getValue()) {
            dialogInfo();
        }

        ProgConfig.ABO_PANE_DIALOG_INFO_ON.addListener((u, o, n) -> setInfoTabPane());
    }

    private void dialogInfo() {
        new InfoPaneDialog(paneAboInfo, "Aboinfos",
                ProgConfig.ABO_PANE_DIALOG_INFO_SIZE, ProgConfig.ABO_PANE_DIALOG_INFO_ON,
                ProgConfig.ABO_GUI_DIVIDER_ON, ProgConfig.ABO_TAB_ON);
    }

    private void setInfoTabPane() {
        getVBoxAll().getChildren().clear();
        if (!ProgConfig.ABO_PANE_DIALOG_INFO_ON.getValue()) {
            // dann anzeigen
            getVBoxAll().getChildren().setAll(paneAboInfo);
            VBox.setVgrow(paneAboInfo, Priority.ALWAYS);
        } else {
            // dann gibts nix zu sehen und dann das InfoPane ausblenden
            ProgConfig.ABO_GUI_DIVIDER_ON.set(false);
        }
    }
}
