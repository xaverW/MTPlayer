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
import de.mtplayer.mLib.tools.MLAlert;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.mediaDb.MediaPathData;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.table.TableRowExpanderColumn;

import java.util.Collection;

public class MediaConfigPathPane {

    private final Daten daten;

    public MediaConfigPathPane() {
        this.daten = Daten.getInstance();
    }

    public void makeTable(Collection<TitledPane> result) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);

        TitledPane tpConfig = new TitledPane("Pfade", vBox);
        result.add(tpConfig);

        TableView<MediaPathData> tableView = new TableView<>();
        tableView.setMinHeight(Const.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        final TableColumn<MediaPathData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        tableView.getColumns().addAll(expander, pathColumn);
        tableView.setItems(daten.mediaPathList);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);


        Button del = new Button("");
        del.setGraphic(new Icons().ICON_BUTTON_REMOVE);
        del.setOnAction(event -> {
            final ObservableList<MediaPathData> sels = tableView.getSelectionModel().getSelectedItems();
            if (sels == null || sels.isEmpty()) {
                new MTAlert().showInfoNoSelection();
            } else {
                daten.mediaPathList.removeAll(sels);
                tableView.getSelectionModel().clearSelection();
            }
        });

        TextField txtPath = new TextField();
        txtPath.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(txtPath, Priority.ALWAYS);

        final Button btnFile = new Button();
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnFile.setOnAction(event -> {
            DirFileChooser.DirChooser(Daten.getInstance().primaryStage, txtPath);
        });

        Button btnNeu = new Button("");
        btnNeu.setGraphic(new Icons().ICON_BUTTON_ADD);
        btnNeu.setOnAction(event -> {
            MediaPathData mediaPathData = new MediaPathData(txtPath.getText());
            if (daten.mediaPathList.addSave(mediaPathData)) {
                tableView.getSelectionModel().select(mediaPathData);
                tableView.scrollTo(mediaPathData);
            } else {
                new MLAlert().showErrorAlert("Pfad zur Mediensammlung hinzufügen",
                        "Der Pfad ist schon enthalten");
            }
        });
        btnNeu.disableProperty().bind(txtPath.textProperty().isEmpty());

        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.getChildren().addAll(del, btnNeu, txtPath, btnFile);

        HBox.setHgrow(txtPath, Priority.ALWAYS);
        vBox.getChildren().addAll(hBox1);
    }


    TableRowExpanderColumn<MediaPathData> expander = new TableRowExpanderColumn<>(param -> {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setStyle("-fx-background-color: #E0E0E0;");
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        TextField txtPath = new TextField();
        txtPath.setMinWidth(500); //todo geht eleganter
        txtPath.textProperty().bindBidirectional(param.getValue().pathProperty());

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.DirChooser(Daten.getInstance().primaryStage, txtPath);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);


        gridPane.add(new Label("Pfad: "), 0, 0);
        gridPane.add(txtPath, 1, 0);
        gridPane.add(btnFile, 2, 0);

        return gridPane;
    });
}
