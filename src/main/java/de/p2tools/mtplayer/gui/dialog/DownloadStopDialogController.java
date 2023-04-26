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

package de.p2tools.mtplayer.gui.dialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.starter.DownloadState;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.File;

public class DownloadStopDialogController extends PDialogExtra {

    private VBox vBoxCont;
    private Label lblHeader = new Label("Angebrochene Filmdateien existieren bereits");
    private Button btnOk = new Button("_Dateien Löschen");
    private Button btnCancel = new Button("_Abbrechen");
    private CheckBox chkAlways = new CheckBox("Immer ausführen");
    private final ObservableList<File> list;
    private boolean delete = false;

    public DownloadStopDialogController(ObservableList<File> list) {
        super(ProgData.getInstance().primaryStage, ProgConfig.DOWNLOAD_STOP_DIALOG_SIZE, "Datei löschen",
                true, false, DECO.BORDER_SMALL);
        this.list = list;

        vBoxCont = getVBoxCont();
        init(true);
    }

    public boolean isDelete() {
        return delete;
    }

    @Override
    public void make() {
        getHBoxTitle().getChildren().add(lblHeader);

        vBoxCont.setPadding(new Insets(P2LibConst.DIST_EDGE));
        vBoxCont.setSpacing(P2LibConst.DIST_VBOX);

        TableView table = new TableView();
        final TableColumn<File, String> fileColumn = new TableColumn<>("Datei");
        fileColumn.prefWidthProperty().bind(table.widthProperty().multiply(60.0 / 100));
        fileColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        fileColumn.getStyleClass().add("special-column-style");

        final TableColumn<File, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.prefWidthProperty().bind(table.widthProperty().multiply(35.0 / 100));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("parent"));
        pathColumn.getStyleClass().add("special-column-style");

        table.getColumns().addAll(fileColumn, pathColumn);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setItems(list);

        vBoxCont.getChildren().addAll(table);
        addOkCancelButtons(btnOk, btnCancel);

        VBox vBox = new VBox(P2LibConst.DIST_VBOX);
        vBox.getChildren().addAll(new Label("Wie möchten Sie fortfahren?"), chkAlways);
        getHboxLeft().getChildren().add(vBox);

        btnOk.setOnAction(event -> {
            delete = true;// dann wird gelöscht
            if (chkAlways.isSelected()) {
                // dann merken wir uns das
                ProgConfig.DOWNLOAD_STOP.setValue(DownloadState.DOWNLOAD_STOP__DELETE);
            }
            quit();
        });
        btnCancel.setOnAction(event -> {
            if (chkAlways.isSelected()) {
                // dann merken wir uns das
                ProgConfig.DOWNLOAD_STOP.setValue(DownloadState.DOWNLOAD_STOP__NOTHING);
            }
            quit();
        });
    }

    private void quit() {
        close();
    }
}
