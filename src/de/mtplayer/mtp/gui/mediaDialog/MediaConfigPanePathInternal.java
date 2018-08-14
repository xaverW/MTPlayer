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
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class MediaConfigPanePathInternal {

    private final TableView<MediaPathData> tableView = new TableView<>();
    private final GridPane gridPane = new GridPane();
    private final TextField txtPath = new TextField();
    private MediaPathData mediaPathData = null;
    private final ProgData progData;
    private final Stage stage;

    public MediaConfigPanePathInternal(Stage stage) {
        this.stage = stage;
        this.progData = ProgData.getInstance();
    }

    public void makeTable(Collection<TitledPane> result) {
        VBox vBox = new VBox(10);

        initTable(vBox);
        makeButton(vBox);
        makeGrid(vBox);

        TitledPane tpConfig = new TitledPane("Interne Medien", vBox);
        result.add(tpConfig);
    }

    private void initTable(VBox vBox) {
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        final TableColumn<MediaPathData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        final TableColumn<MediaPathData, Integer> countColumn = new TableColumn<>("Anzahl");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        pathColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(80.0 / 100));

        tableView.getColumns().addAll(pathColumn);

        SortedList<MediaPathData> list = progData.mediaPathDataList.getSortedListInternal();
        list.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(list);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);
    }

    private void makeButton(VBox vBox) {
        final Button btnHelp = new PButton().helpButton(stage,
                "Mediensammlungen verwalten", HelpText.INTERN_MEDIA_COLLECTION);

        Button btnDel = new Button("");
        btnDel.setGraphic(new Icons().ICON_BUTTON_REMOVE);
        btnDel.setTooltip(new Tooltip("Markierten Pfad löschen"));
        btnDel.setOnAction(event -> {
            final ObservableList<MediaPathData> sels = tableView.getSelectionModel().getSelectedItems();
            if (sels == null || sels.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                progData.mediaPathDataList.removeAll(sels);
                tableView.getSelectionModel().clearSelection();
            }
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(btnDel, PGuiTools.getHBoxGrower(), btnHelp);

        vBox.getChildren().addAll(hBox);
    }

    private void makeGrid(VBox vBox) {
        gridPane.setStyle(PConst.CSS_BACKGROUND_COLOR_GREY);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtPath);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Einen Pfad mit Medien auswählen."));

        Button btnNew = new Button("");
        btnNew.setGraphic(new Icons().ICON_BUTTON_ADD);
        btnNew.setTooltip(new Tooltip("Den Pfad zur Mediensammlung hinzufügen."));
        btnNew.setOnAction(event -> {
            MediaPathData mediaPathData = progData.mediaPathDataList.addInternalMediaPathData(txtPath.getText());

            if (mediaPathData == null) {
                PAlert.showErrorAlert("Sammlung hinzufügen", "Sammlung: " + txtPath.getText(),
                        "Eine Sammlung mit dem **Pfad** existiert bereits.");
            } else {
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(mediaPathData);
                tableView.scrollTo(mediaPathData);
            }
        });
        btnNew.disableProperty().bind(txtPath.textProperty().isEmpty());

        gridPane.add(new Label("Eine neuen Pfad mit Medien hinzufügen:"), 0, 0, 2, 1);
        gridPane.add(new Label("Pfad:"), 0, 1);
        gridPane.add(txtPath, 1, 1);
        gridPane.add(btnFile, 2, 1);
        gridPane.add(btnNew, 3, 1);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        vBox.getChildren().add(gridPane);
    }
}
