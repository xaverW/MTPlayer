/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Text;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.tools.P2InfoFactory;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ToolInfoPane extends VBox {

    private final List<Node[]> infoList;
    private final String progName;

    public ToolInfoPane(String progName, List<Node[]> infoList) {
        this.progName = progName;
        this.infoList = infoList;
        init();
    }

    public Tab getTab() {
        Tab tab = new Tab("Programminfos");
        tab.setClosable(false);
        tab.setContent(this);
        return tab;
    }

    private void init() {
        setSpacing(P2LibConst.SPACING_VBOX);
        setPadding(new Insets(10));

        getChildren().addAll(getGrid());
    }

    private GridPane getGrid() {
        int row = 0;
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        // ==========================
        gridPane.add(P2Text.getLblTextBoldBig(progName), 0, row, 2, 1);

        gridPane.add(new Label("Version:"), 0, ++row);
        gridPane.add(new Label(P2InfoFactory.getProgVersion()), 1, row);
        gridPane.add(new Label("[ Build: " + P2InfoFactory.getBuildNo() + " vom " +
                P2InfoFactory.getBuildDate() + " ]"), 1, ++row);


        // ==========================
        //Java
        gridPane.add(new Label(""), 0, ++row);
        gridPane.add(P2Text.getLblTextBold("Java Informationen"), 0, ++row, 2, 1);

        gridPane.add(new Label("Version:"), 0, ++row);
        gridPane.add(new Label(System.getProperty("java.version")), 1, row);

        gridPane.add(new Label("Type:"), 0, ++row);
        String strVmType = System.getProperty("java.vm.name");
        strVmType += " (" + System.getProperty("java.vendor") + ")";
        gridPane.add(new Label(strVmType), 1, row);

        gridPane.add(new Label("JavaFX:"), 0, ++row);
        gridPane.add(new Label(System.getProperty("javafx.runtime.version")), 1, row);


        // ==========================
        if (infoList != null && !infoList.isEmpty()) {
            gridPane.add(new Label(), 0, ++row);
            for (Node[] info : infoList) {

                if (info.length == 0) {
                    gridPane.add(new Label(""), 0, ++row);
                }
                if (info.length >= 1) {
                    gridPane.add((info[0]), 0, ++row);
                }
                if (info.length >= 2) {
                    gridPane.add(info[1], 1, row);
                }
                if (info.length > 2) {
                    for (int i = 2; i < info.length; ++i) {
                        gridPane.add(info[i], 1, ++row);
                    }
                }
            }
        }

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());

        return gridPane;
    }
}
