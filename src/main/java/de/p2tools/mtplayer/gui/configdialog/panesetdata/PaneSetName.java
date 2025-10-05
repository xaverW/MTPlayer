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


package de.p2tools.mtplayer.gui.configdialog.panesetdata;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneSetName {
    private final TextField txtVisibleName = new TextField("");
    private final TextArea txtDescription = new TextArea("");
    private SetData setData = null;
    private final ObjectProperty<SetData> setDataObjectProperty;

    PaneSetName(Stage stage, ObjectProperty<SetData> setDataObjectProperty) {
        this.setDataObjectProperty = setDataObjectProperty;
    }

    public void close() {
        unBindProgData();
        for (int i = 0; i < ProgData.getInstance().setDataList.size(); ++i) {
            SetData sd = ProgData.getInstance().setDataList.get(i);
            if (sd.getVisibleName().trim().isEmpty()) {
                sd.setVisibleName("Set " + (i + 1));
            }
        }
    }

    public void makePane(Collection<TitledPane> result) {
        VBox vBox = new VBox(P2LibConst.PADDING);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(10));

        TitledPane tpConfig = new TitledPane("Einstellungen", vBox);
        result.add(tpConfig);

        // Name, Beschreibung
        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        vBox.getChildren().add(gridPane);

        gridPane.add(new Label("Set Name:"), 0, 0);
        gridPane.add(txtVisibleName, 1, 0);

        gridPane.add(new Label("Beschreibung:"), 0, 1);
        gridPane.add(txtDescription, 1, 1);

        for (int i = 0; i < gridPane.getRowCount(); ++i) {
            RowConstraints rowC = new RowConstraints();
            rowC.setValignment(VPos.CENTER);
            gridPane.getRowConstraints().add(rowC);
        }
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow());

        setDataObjectProperty.addListener((u, o, n) -> {
            tpConfig.setDisable(setDataObjectProperty.getValue() == null);
            bindProgData();
        });
        tpConfig.setDisable(setDataObjectProperty.getValue() == null);
        bindProgData();
    }

    private void bindProgData() {
        unBindProgData();
        setData = setDataObjectProperty.getValue();
        if (setData != null) {
            txtVisibleName.textProperty().bindBidirectional(setData.visibleNameProperty());
            txtDescription.textProperty().bindBidirectional(setData.descriptionProperty());
        }
    }

    private void unBindProgData() {
        if (setData != null) {
            txtVisibleName.textProperty().unbindBidirectional(setData.visibleNameProperty());
            txtVisibleName.setText("");

            txtDescription.textProperty().unbindBidirectional(setData.descriptionProperty());
            txtDescription.setText("");
        }
        setData = null;
    }
}
