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

package de.p2tools.mtplayer.gui.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.mediadb.MediaData;
import de.p2tools.mtplayer.controller.mediadb.MediaDataWorker;
import de.p2tools.mtplayer.controller.mediadb.MediaFileSize;
import de.p2tools.mtplayer.gui.mediadialog.PaneMediaContextMenu;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.POpen;
import de.p2tools.p2lib.guitools.ptable.CellCheckBox;
import de.p2tools.p2lib.tools.file.PFileUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneMediaData {

    private final TableView<MediaData> tableView = new TableView<>();
    private final Label lblTitleMedia = new Label();
    private final Label lblPathMedia = new Label();
    private final Label lblSize = new Label();
    private final Button btnPlay = new Button();
    private final Button btnOpen = new Button();
    private final Button btnCreateMediaDB = new Button("_Mediensammlung neu aufbauen");
    private ChangeListener sizeListener;


    private ProgData progData = ProgData.getInstance();
    private final Stage stage;

    public PaneMediaData(Stage stage) {
        this.stage = stage;
    }

    public void make(Collection<TitledPane> result) {
        VBox vBox = new VBox(P2LibConst.DIST_VBOX);
        vBox.setPadding(new Insets(P2LibConst.DIST_EDGE));

        TitledPane tpConfig = new TitledPane("Medien", vBox);
        result.add(tpConfig);

        initTable(vBox);
        initLabelSum(vBox);
        initLabel(vBox);
    }

    public void close() {
        btnCreateMediaDB.disableProperty().unbind();
        progData.mediaDataList.sizeProperty().removeListener(sizeListener);
    }

    private void initTable(VBox vBox) {
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(true);

        final TableColumn<MediaData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        final TableColumn<MediaData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        final TableColumn<MediaData, MediaFileSize> sizeColumn = new TableColumn<>("Größe [MB]");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        sizeColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<MediaData, Boolean> externalColumn = new TableColumn<>("extern");
        externalColumn.setCellValueFactory(new PropertyValueFactory<>("external"));
        externalColumn.setCellFactory(new CellCheckBox().cellFactory);

        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(25.0 / 100));
        pathColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(40.0 / 100));
        sizeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(15.0 / 100));
        externalColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(15.0 / 100));

        tableView.getColumns().addAll(nameColumn, pathColumn, sizeColumn, externalColumn);
        tableView.setItems(progData.mediaDataList);

        tableView.setOnMousePressed(m -> {
            if (tableView.getItems().isEmpty()) {
                return;
            }
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                MediaData mediaData = tableView.getSelectionModel().getSelectedItem();
                if (mediaData == null) {
                    PAlert.showInfoNoSelection();

                } else {
                    ContextMenu contextMenu = new PaneMediaContextMenu(stage, mediaData).getContextMenu();
                    tableView.setContextMenu(contextMenu);
                }
            }
        });
        tableView.setRowFactory(tv -> {
            TableRow<MediaData> row = new TableRow<>();
            row.hoverProperty().addListener((observable) -> {
                final MediaData mediaData = row.getItem();
                if (row.isHover() && mediaData != null) {
                    setTableSel(mediaData);
                } else {
                    setTableSel(tableView.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);
    }

    private void setTableSel(MediaData mediaData) {
        if (mediaData != null) {
            lblPathMedia.setText(mediaData.getPath());
            lblTitleMedia.setText(mediaData.getName());
        } else {
            lblPathMedia.setText("");
            lblTitleMedia.setText("");
        }
    }

    private void initLabelSum(VBox vBox) {
        btnCreateMediaDB.disableProperty().bind(progData.mediaDataList.searchingProperty());
        btnCreateMediaDB.setOnAction(e -> MediaDataWorker.createMediaDb());
        sizeListener = (observable, oldValue, newValue) ->
                Platform.runLater(() -> lblSize.setText(progData.mediaDataList.size() + ""));
        progData.mediaDataList.sizeProperty().addListener(sizeListener);

        lblSize.setText(progData.mediaDataList.size() + "");
        HBox hBoxSum = new HBox(P2LibConst.DIST_BUTTON);
        hBoxSum.setAlignment(Pos.CENTER_LEFT);
        hBoxSum.getChildren().addAll(btnCreateMediaDB, P2GuiTools.getHBoxGrower(),
                new Label("Anzahl Medien gesamt:"),
                lblSize);
        vBox.getChildren().addAll(hBoxSum);
    }

    private void initLabel(VBox vBox) {
        btnOpen.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
        btnOpen.setTooltip(new Tooltip("Ausgewählten Pfad im Dateimanager öffnen"));
        btnOpen.setOnAction(e -> open());
        btnOpen.disableProperty().bind(lblPathMedia.textProperty().isEmpty().and(lblTitleMedia.textProperty().isEmpty()));

        btnPlay.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_PLAY.getImageView());
        btnPlay.setTooltip(new Tooltip("Ausgewählten Film abspielen"));
        btnPlay.setOnAction(e -> play());
        btnPlay.disableProperty().bind(lblPathMedia.textProperty().isEmpty().and(lblTitleMedia.textProperty().isEmpty()));

        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        gridPane.add(new Label("Titel:"), 0, 0);
        gridPane.add(lblTitleMedia, 1, 0);
        gridPane.add(btnPlay, 2, 0);
        gridPane.add(new Label("Pfad:"), 0, 1);
        gridPane.add(lblPathMedia, 1, 1);
        gridPane.add(btnOpen, 2, 1);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        vBox.getChildren().addAll(gridPane);
    }

    private void play() {
        final String path = lblPathMedia.getText();
        final String name = lblTitleMedia.getText();
        if (!name.isEmpty() && !path.isEmpty()) {
            POpen.playStoredFilm(PFileUtils.addsPath(path, name),
                    ProgConfig.SYSTEM_PROG_PLAY_FILME, ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
        }
    }

    private void open() {
        POpen.openDir(lblPathMedia.getText(), ProgConfig.SYSTEM_PROG_OPEN_DIR, ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
    }
}
