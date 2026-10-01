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
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

public class ToolRuntimePane extends VBox {

    private final Button btnDuration = new Button("_Laufzeiten ausgeben");
    private final TextArea textArea = new TextArea();

    private final String line = "<==/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\==>";

    public ToolRuntimePane() {
        init();
    }

    public Tab getTab() {
        Tab tab = new Tab("Programmlaufzeiten");
        tab.setClosable(false);
        tab.setContent(this);
        return tab;
    }

    protected void init() {
        setSpacing(P2LibConst.SPACING_VBOX);
        setPadding(new Insets(10));

        btnDuration.setOnAction(a -> {
            List<String> list = P2Duration.getCounter();
            list.add(0, line);
            list.add(line);
            list.add(" ");
            list.add(" ");
            P2Log.sysLog(list);

            textArea.setText("");
            StringBuilder text = new StringBuilder();
            for (String s : P2Duration.getCounter()) {
                text.append("\n").append(s);
            }
            textArea.setText(text.toString());
        });

        VBox.setVgrow(textArea, Priority.ALWAYS);

        getChildren().addAll(getGrid(), textArea);
    }

    private GridPane getGrid() {
        int row = 0;
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        //Laufzeiten
        gridPane.add(new Label(" "), 0, ++row);
        Text text = new Text("Laufzeiten des Programms");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        text.getStyleClass().add("downloadGuiMediaText");
        gridPane.add(text, 0, ++row, 3, 1);
        gridPane.add(btnDuration, 0, ++row);

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());

        return gridPane;
    }
}
