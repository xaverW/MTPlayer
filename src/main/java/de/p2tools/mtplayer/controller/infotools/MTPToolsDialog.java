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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class MTPToolsDialog extends P2DialogExtra {
    private final ProgData progData;
    private final ToolMemoryPane memoryTab = new ToolMemoryPane();
    private final ToolInfoPane infoTab;
    private final ToolRuntimePane runtimeTab = new ToolRuntimePane();
    private final ToolDebugPane debugTab = new ToolDebugPane();

    public MTPToolsDialog(String progName, List<Node[]> infoList) {
        super(P2LibConst.primaryStage, ProgConfig.TOOLS_DIALOG_SIZE, "Programmtools",
                false, true, true, DECO.NO_BORDER);
        this.progData = ProgData.getInstance();
        infoTab = new ToolInfoPane(progName, infoList);
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
        Button btnOk = new Button("_Ok");
        addOkButton(btnOk);
        btnOk.setOnAction(a -> close());
    }
}
