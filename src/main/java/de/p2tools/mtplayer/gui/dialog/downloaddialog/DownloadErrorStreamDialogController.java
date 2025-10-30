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
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DownloadErrorStreamDialogController extends P2DialogExtra {

    private final HBox hBoxTitle;
    private final VBox vBoxCont;
    private final Label lblHeader = new Label("Programmausgabe");
    private final Button btnOk = new Button("_Ok");
    private final TextArea txtCont = new TextArea();
    private final String errorStream;

    public DownloadErrorStreamDialogController(String errorStream) {
        super(ProgData.getInstance().primaryStage, ProgConfig.DOWNLOAD_DIALOG_ERROR_STREAM_SIZE, "Fehler",
                false, true, true, DECO.NO_BORDER);

        this.errorStream = errorStream;
        hBoxTitle = getHBoxTitle();
        vBoxCont = getVBoxCont();
        init(true);
    }

    @Override
    public void make() {
        initCont();
        txtCont.setEditable(false);
        txtCont.setWrapText(true);
        txtCont.setText(errorStream);
        btnOk.setOnAction(event -> {
            quit();
        });
    }

    private void initCont() {
        hBoxTitle.getChildren().add(lblHeader);
        vBoxCont.setPadding(new Insets(5));
        vBoxCont.getChildren().add(txtCont);
        VBox.setVgrow(txtCont, Priority.ALWAYS);
        addOkButton(btnOk);
    }

    private void quit() {
        close();
    }
}
