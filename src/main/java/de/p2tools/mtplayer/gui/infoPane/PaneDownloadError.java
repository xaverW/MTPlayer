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
import de.p2tools.mtplayer.gui.dialog.downloaddialog.DownloadErrorStreamDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PaneDownloadError extends VBox {

    private final VBox vBoxCont = new VBox();
    private final ScrollPane scrollPane = new ScrollPane();
    private final Button btnClear = new Button();
    private final ProgData progData;

    public PaneDownloadError() {
        progData = ProgData.getInstance();
        VBox.setVgrow(this, Priority.ALWAYS);

        btnClear.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnClear.setTooltip(new Tooltip("Die Liste der Downloadfehler löschen"));
        btnClear.setOnAction(a -> progData.downloadErrorList.clear());

        vBoxCont.setPadding(new Insets(5));
        vBoxCont.setSpacing(5);
        scrollPane.setContent(vBoxCont);
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
        vBoxCont.getChildren().clear();

        if (progData.downloadErrorList.isEmpty()) {
            // dann gibts keine Fehler
            Label lblNoError = new Label("Keine Fehler");
            vBoxCont.getChildren().add(lblNoError);

        } else {
            progData.downloadErrorList.forEach(d -> {
                final GridPane gridPane = new GridPane();
                gridPane.setHgap(5);
                gridPane.setVgap(5);
                gridPane.setPadding(new Insets(5));
                gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                        P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                        P2ColumnConstraints.getCcPrefSize());
                gridPane.setStyle("-fx-border-width: 2px; -fx-border-color: black;");

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

                int row = 0;
                gridPane.add(new Label("Titel: "), 0, row);
                if (d.getErrorStream().isEmpty()) {
                    gridPane.add(txtTitle, 1, row);

                } else {
                    final Button btnErrorStream = new Button("Programmausgabe");
                    btnErrorStream.setOnAction(a -> new DownloadErrorStreamDialogController(d.getErrorStream()));
                    btnErrorStream.setVisible(!d.getErrorStream().isEmpty());

                    HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
                    HBox.setHgrow(txtTitle, Priority.ALWAYS);
                    hBox.getChildren().addAll(txtTitle, btnErrorStream);
                    gridPane.add(hBox, 1, row);
                }

                ++row;
                gridPane.add(new Label("URL: "), 0, row);
                gridPane.add(txtUrl, 1, row);

                ++row;
                gridPane.add(new Label("Datei:"), 0, row);
                gridPane.add(txtFile, 1, row);

                ++row;
                gridPane.add(taError, 0, row, 2, 1);
                GridPane.setHgrow(taError, Priority.ALWAYS);

                vBoxCont.getChildren().add(gridPane);
            });

            HBox hBox = new HBox();
            hBox.getChildren().add(btnClear);
            hBox.setAlignment(Pos.CENTER_RIGHT);
            vBoxCont.getChildren().add(hBox);
        }
    }
}
