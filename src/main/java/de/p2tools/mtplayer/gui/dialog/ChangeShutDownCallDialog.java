/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.gui.dialog;

import de.p2tools.mtplayer.controller.ProgSave;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.tools.PShutDown;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChangeShutDownCallDialog extends P2DialogExtra {

    private final Button btnOk = new Button("_Ok");

    public ChangeShutDownCallDialog(Stage stage) {
        super(stage, null,
                "Den Systembefehl zum Herunterfahren anpassen", true, true, DECO.NO_BORDER);

        initDialog();
        init(false);
        super.showDialog();
    }

    private void initDialog() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING_GRIDPANE));

        final TextField txtCall = new TextField();
        txtCall.textProperty().bindBidirectional(ProgConfig.SYSTEM_SHUT_DOWN_CALL);
        if (txtCall.getText().isEmpty()) {
            txtCall.setText(PShutDown.getShutDownCommand());
        }

        Label lblStandard = new Label(PShutDown.getShutDownCommand());
        Button btnStandard = new Button("Standard setzen");
        btnStandard.setOnAction(a -> txtCall.setText(PShutDown.getShutDownCommand()));

        Button btnTest = new Button("Testen");
        btnTest.setOnAction(a -> {
            ProgSave.saveAll(); // damit nichts verloren geht
            PShutDown.shutDown(ProgConfig.SYSTEM_SHUT_DOWN_CALL.getValueSafe());
        });

        Button btnHelp = P2Button.helpButton(getStageProp(), "Rechner herunterfahren", HelpText.CONFIG_SHUT_DOWN_CALL);

        VBox vBox = new VBox(P2LibConst.PADDING_VBOX);

        HBox hBox = new HBox(P2LibConst.PADDING_HBOX);
        hBox.getChildren().addAll(new Label("Standard Befehl:"), lblStandard, P2GuiTools.getHBoxGrower(), btnStandard);
        vBox.getChildren().addAll(hBox);

        hBox = new HBox(P2LibConst.PADDING_HBOX);
        HBox.setHgrow(txtCall, Priority.ALWAYS);
        hBox.getChildren().addAll(new Label("Eigener Befehl:"), txtCall, btnTest, btnHelp);
        vBox.getChildren().addAll(P2GuiTools.getVDistance(P2LibConst.PADDING_VBOX), hBox);

        getVBoxCont().getChildren().addAll(vBox);

        addOkButton(btnOk);
        btnOk.setOnAction(a -> {
            close();
        });
    }
}
