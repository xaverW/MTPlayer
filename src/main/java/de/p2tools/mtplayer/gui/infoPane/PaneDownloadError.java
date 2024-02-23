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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class PaneDownloadError extends AnchorPane {

    private final ScrollPane scrollPane = new ScrollPane();
    private final GridPane gridPane = new GridPane();
    private final Button btnClear = new Button();

    private final ProgData progData;

    public PaneDownloadError() {
        progData = ProgData.getInstance();

        btnClear.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnClear.setTooltip(new Tooltip("Die Liste der Downloadfehler löschen"));
        btnClear.setOnAction(a -> progData.downloadErrorList.clear());

        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(5));
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize());

        scrollPane.setContent(gridPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setTopAnchor(scrollPane, 0.0);
        getChildren().add(scrollPane);

        progData.downloadErrorList.addListener((u, o, n) -> setInfoText());
        setInfoText();
    }

    public void setInfoText() {
        final IntegerProperty row = new SimpleIntegerProperty(0);
        gridPane.getChildren().clear();

        if (progData.downloadErrorList.isEmpty()) {
            // dann gibts keine Fehler
            Label lblNoError = new Label("Keine Fehler");
            gridPane.add(lblNoError, 1, row.get());
            GridPane.setHgrow(lblNoError, Priority.ALWAYS);

        } else {
            progData.downloadErrorList.forEach(d -> {
                TextField txtTitle = new TextField(d.getTitle());
                txtTitle.setText(d.getTitle());
                txtTitle.setEditable(false);

                TextField txtUrl = new TextField();
                txtUrl.setText(d.getUrl());
                txtUrl.setEditable(false);

                TextField txtFile = new TextField();
                txtFile.setText(d.getFile());
                txtFile.setEditable(false);

                TextArea taError = new TextArea();
                taError.setText(d.getError());
                taError.setEditable(false);
                taError.setWrapText(true);
                taError.setPrefRowCount(4);

                gridPane.add(new Label("Titel: "), 0, row.get());
                gridPane.add(txtTitle, 1, row.get());

                row.setValue(row.get() + 1);
                gridPane.add(new Label("URL: "), 0, row.get());
                gridPane.add(txtUrl, 1, row.get());

                row.setValue(row.get() + 1);
                gridPane.add(new Label("Datei:"), 0, row.get());
                gridPane.add(txtFile, 1, row.get());

                row.setValue(row.get() + 1);
                gridPane.add(taError, 0, row.get(), 2, 1);
                GridPane.setHgrow(taError, Priority.ALWAYS);

                row.setValue(row.get() + 1);
                gridPane.add(new Label(""), 0, row.get());
                row.setValue(row.get() + 1);
            });

            gridPane.add(btnClear, 2, row.get() - 2);
        }
    }
}
