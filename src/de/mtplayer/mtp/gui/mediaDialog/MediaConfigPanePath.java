/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
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


package de.mtplayer.mtp.gui.mediaDialog;

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.mediaDb.MediaPathData;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.Collection;

public class MediaConfigPanePath {

    private final TableView<MediaPathData> tableView = new TableView<>();
    private final GridPane gridPane = new GridPane();
    private final TextField txtPath = new TextField();
    private MediaPathData mediaPathData = null;
    private final ProgData progData;

    public MediaConfigPanePath() {
        this.progData = ProgData.getInstance();
    }

    public void makeTable(Collection<TitledPane> result) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);

        initTable(vBox);
        makeConfig(vBox);
        addConfigs(vBox);

        TitledPane tpConfig = new TitledPane("Pfade", vBox);
        result.add(tpConfig);
    }

    private void initTable(VBox vBox) {
        final TableColumn<MediaPathData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(pathColumn);
        tableView.setItems(progData.mediaPathList.getSortedListInternal());
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setActMediaData));

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);
    }

    private void makeConfig(VBox vBox) {
        Button btnDel = new Button("");
        btnDel.setGraphic(new Icons().ICON_BUTTON_REMOVE);
        btnDel.setOnAction(event -> {
            final ObservableList<MediaPathData> sels = tableView.getSelectionModel().getSelectedItems();
            if (sels == null || sels.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                progData.mediaPathList.removeAll(sels);
                tableView.getSelectionModel().clearSelection();
            }
        });

        final Button btnHelp = new PButton().helpButton("Mediensammlungen verwalten",
                HelpText.INTERN_MEDIA_COLLECTION);

        Button btnNew = new Button("");
        btnNew.setGraphic(new Icons().ICON_BUTTON_ADD);
        btnNew.setOnAction(event -> {
            MediaPathData mediaPathData = new MediaPathData();
            if (progData.mediaPathList.add(mediaPathData)) {
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(mediaPathData);
                tableView.scrollTo(mediaPathData);
            }
        });


        HBox hBoxHlp = new HBox();
        hBoxHlp.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(hBoxHlp, Priority.ALWAYS);
        hBoxHlp.getChildren().add(btnHelp);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(btnNew, btnDel, hBoxHlp);

        vBox.getChildren().addAll(hBox);
    }

    private void addConfigs(VBox vBox) {
        gridPane.setStyle("-fx-background-color: #E0E0E0;");
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtPath);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);

        gridPane.add(new Label("Pfad: "), 0, 0);
        gridPane.add(txtPath, 1, 0);
        gridPane.add(btnFile, 2, 0);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), PColumnConstraints.getCcComputedSize());

        vBox.getChildren().add(gridPane);
        gridPane.setDisable(true);
    }

    private void setActMediaData() {
        MediaPathData mediaPathDataAct = tableView.getSelectionModel().getSelectedItem();
        if (mediaPathDataAct == mediaPathData) {
            return;
        }

        if (mediaPathData != null) {
            txtPath.textProperty().unbindBidirectional(mediaPathData.pathProperty());
        }

        mediaPathData = mediaPathDataAct;
        gridPane.setDisable(mediaPathData == null);
        if (mediaPathData != null) {
            txtPath.textProperty().bindBidirectional(mediaPathData.pathProperty());
        }
    }
}
