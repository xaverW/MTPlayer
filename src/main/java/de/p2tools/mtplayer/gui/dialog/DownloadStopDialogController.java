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
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.PButton;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.File;

public class DownloadStopDialogController extends PDialogExtra {

    private final VBox vBoxCont;
    private final Button btnDelDlFile = new Button();
    private final Button btnDelDl = new Button("DL löschen, Datei behalten");
    private final Button btnCancel = new Button("Abbrechen");
    private final CheckBox chkAlways = new CheckBox("Immer ausführen");
    private final ObservableList<File> list;
    private final boolean delete;// nur zur Anzeige des Button-Textes
    private STATE state;

    public DownloadStopDialogController(ObservableList<File> list, boolean delete) {
        super(ProgData.getInstance().primaryStage, ProgConfig.DOWNLOAD_STOP_DIALOG_SIZE, "Datei löschen",
                true, false, DECO.BORDER_SMALL);
        this.list = list;
        this.delete = delete;

        vBoxCont = getVBoxCont();
        init(true);
    }

    public STATE getState() {
        return state;
    }

    @Override
    public void make() {
        if (delete) {
            // dann werden die Downloads gelöscht
            btnDelDlFile.setText("DL und Datei löschen");
            btnDelDl.setText("DL löschen, Datei behalten");
        } else {
            // dann werden die Downloads nur gestoppt
            btnDelDlFile.setText("DL abbrechen und Datei löschen");
            btnDelDl.setText("DL abbrechen, Datei behalten");
        }

        getHBoxTitle().getChildren().add(list.isEmpty() ?
                new Label("Es liegen noch keine angebrochene Filmdateien vor") :
                new Label("Angebrochene Filmdateien existieren bereits"));

        vBoxCont.setPadding(new Insets(P2LibConst.DIST_EDGE));
        vBoxCont.setSpacing(P2LibConst.DIST_VBOX);

        TableView<File> table = new TableView<>();
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

        getHBoxOverButtons().setAlignment(Pos.CENTER_RIGHT);
        getHBoxOverButtons().getChildren().addAll(chkAlways);

        btnDelDlFile.setTooltip(new Tooltip("Der Download wird abgebrochen oder gelöscht," +
                "\ndie angefangenen Filmdateien werden auch gelöscht."));
        btnDelDl.setTooltip(new Tooltip("Der Download wird abgebrochen oder gelöscht," +
                "\ndie angefangenen Filmdateien werden aber NICHT gelöscht."));
        btnCancel.setTooltip(new Tooltip("Es wird nichts abgebrochen oder gelöscht."));
        btnDelDlFile.setOnAction(event -> {
            // löschen: DL und Dateien
            state = STATE.STATE_1;
            if (chkAlways.isSelected()) {
                // dann merken wir uns das
                ProgConfig.DOWNLOAD_STOP.setValue(DownloadState.DOWNLOAD_STOP__DELETE_FILE);
            }
            quit();
        });
        btnDelDl.setOnAction(event -> {
            // löschen: nur Download
            state = STATE.STATE_2;
            if (chkAlways.isSelected()) {
                // dann merken wir uns das
                ProgConfig.DOWNLOAD_STOP.setValue(DownloadState.DOWNLOAD_STOP__DO_NOT_DELETE);
            }
            quit();
        });
        btnCancel.setOnAction(a -> {
            // nix
            state = STATE.STATE_CANCEL;
            quit();
        });
        Button btnHelp = PButton.helpButton(getStage(),
                "Download abbrechen oder löschen", HelpText.DOWNLOAD_CANCEL);
        addHlpButton(btnHelp);
        addAnyButton(btnDelDlFile);
        addAnyButton(btnDelDl);
        addCancelButton(btnCancel);
    }

    private void quit() {
        close();
    }
}
