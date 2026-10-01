/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.infotools;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MTPToolsDialog extends P2DialogExtra {
    private final ProgData progData;
    ToolMemoryPane memoryTab = new ToolMemoryPane();
    ToolInfoPane infoTab = new ToolInfoPane();
    ToolRuntimePane runtimeTab = new ToolRuntimePane();
    ToolDebugPane debugTab = new ToolDebugPane();

    public MTPToolsDialog() {
        super(P2LibConst.primaryStage, ProgConfig.TOOLS_DIALOG_SIZE, "Programmtools",
                false, true, true, DECO.NO_BORDER);
        this.progData = ProgData.getInstance();
        init(true);
    }

    @Override
    public void close() {
        memoryTab.close();
        super.close();
    }

    @Override
    public void make() {
        TabPane tabPane = new TabPane();

        tabPane.getTabs().addAll(memoryTab.getTab(), infoTab.getTab());
        if (ProgData.debug) {
            tabPane.getTabs().addAll(runtimeTab.getTab(), debugTab.getTab());
        }

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        getVBoxCont().getChildren().add(tabPane);
    }
}
