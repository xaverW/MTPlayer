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

package de.p2tools.mtplayer.gui.dialog.downloaddialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConfigAskBeforeDelete;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class DownloadOnlyStopDialogController extends PDialogExtra {

    private final VBox vBoxCont;
    private final Button btnDelDl = new Button("DL löschen, Datei behalten");
    private final Button btnCancel = new Button("Abbrechen");
    private final CheckBox chkAlways = new CheckBox("Nicht mehr fragen");
    private final boolean delete; //  nur zur Anzeige des Button-Textes
    private final ObservableList<DownloadData> foundDownloadList;
    private STATE state;

    public DownloadOnlyStopDialogController(ObservableList<DownloadData> foundDownloadList, boolean delete) {
        super(ProgData.getInstance().primaryStage, ProgConfig.DOWNLOAD_ONLY_STOP_DIALOG_SIZE, "Download löschen",
                true, false, DECO.BORDER_SMALL);
        this.foundDownloadList = foundDownloadList;
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
            btnDelDl.setText("Download löschen");
        } else {
            // dann werden die Downloads nur gestoppt
            btnDelDl.setText("Download abbrechen");
        }

        getHBoxTitle().getChildren().add(delete ?
                new Label("Download löschen") :
                new Label("Download abbrechen"));

        vBoxCont.setPadding(new Insets(P2LibConst.PADDING));
        vBoxCont.setSpacing(P2LibConst.PADDING_VBOX);


        TableView<DownloadData> table = new TableView<>();
        final TableColumn<DownloadData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.prefWidthProperty().bind(table.widthProperty().multiply(40.0 / 100));
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("special-column-style");

        final TableColumn<DownloadData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.prefWidthProperty().bind(table.widthProperty().multiply(55.0 / 100));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("special-column-style");

        table.getColumns().addAll(themeColumn, titleColumn);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setItems(foundDownloadList);
        vBoxCont.getChildren().addAll(table);


        getHBoxOverButtons().setAlignment(Pos.CENTER_RIGHT);
        getHBoxOverButtons().getChildren().addAll(chkAlways);

        btnDelDl.setTooltip(new Tooltip("Der Download wird abgebrochen oder gelöscht."));
        btnCancel.setTooltip(new Tooltip("Der Download wird nicht abgebrochen oder gelöscht."));

        btnDelDl.setOnAction(event -> {
            // löschen: nur Download
            state = STATE.STATE_OK;
            if (chkAlways.isSelected()) {
                // dann merken wir uns das
                ProgConfig.DOWNLOAD_ONLY_STOP.setValue(ProgConfigAskBeforeDelete.DOWNLOAD_ONLY_STOP__DELETE);
            }
            quit();
        });
        btnCancel.setOnAction(a -> {
            // nix
            state = STATE.STATE_CANCEL;
            quit();
        });
        Button btnHelp = P2Button.helpButton(getStage(),
                "Download abbrechen oder löschen", HelpText.DOWNLOAD_ONLY_CANCEL);
        addHlpButton(btnHelp);
        addOkCancelButtons(btnDelDl, btnCancel);
    }

    private void quit() {
        close();
    }
}
