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
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneProxy {
    private final P2ToggleSwitch tglProxy = new P2ToggleSwitch("Einen Proxy verwenden");

    private final Stage stage;
    private final TextField txtServer = new TextField();
    private final TextField txtPort = new TextField();
    private final TextField txtUser = new TextField();
    private final TextField txtPwd = new TextField();

    public PaneProxy(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglProxy.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_USE_PROXY);
        txtServer.textProperty().unbindBidirectional(ProgConfig.SYSTEM_PROXY_HOST);
        txtPort.textProperty().unbindBidirectional(ProgConfig.SYSTEM_PROXY_PORT);
        txtUser.textProperty().unbindBidirectional(ProgConfig.SYSTEM_PROXY_USER);
        txtPwd.textProperty().unbindBidirectional(ProgConfig.SYSTEM_PROXY_PWD);
    }

    public TitledPane make(Collection<TitledPane> result) {
        tglProxy.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_USE_PROXY);
        final Button btnHelpGeo = P2Button.helpButton(stage, "Proxy", HelpText.CONFIG_PROXY);

        txtServer.textProperty().bindBidirectional(ProgConfig.SYSTEM_PROXY_HOST);
        txtPort.textProperty().bindBidirectional(ProgConfig.SYSTEM_PROXY_PORT);
        txtUser.textProperty().bindBidirectional(ProgConfig.SYSTEM_PROXY_USER);
        txtPwd.textProperty().bindBidirectional(ProgConfig.SYSTEM_PROXY_PWD);

        txtServer.disableProperty().bind(tglProxy.selectedProperty().not());
        txtPort.disableProperty().bind(tglProxy.selectedProperty().not());
        txtUser.disableProperty().bind(tglProxy.selectedProperty().not());
        txtPwd.disableProperty().bind(tglProxy.selectedProperty().not());

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        int row = 0;
        gridPane.add(tglProxy, 0, row, 2, 1);
        gridPane.add(btnHelpGeo, 2, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("Host:"), 0, ++row);
        gridPane.add(txtServer, 1, row);
        gridPane.add(new Label("Port:"), 0, ++row);
        gridPane.add(txtPort, 1, row);
        gridPane.add(new Label("User:"), 0, ++row);
        gridPane.add(txtUser, 1, row);
        gridPane.add(new Label("Passwort:"), 0, ++row);
        gridPane.add(txtPwd, 1, row);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize());


        HBox hBox = new HBox();
        hBox.getStyleClass().add("extra-pane");
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.getChildren().add(new Label("Änderungen in diesem Tab\n" +
                "wirken sich erst nach dem Neustart des Programms aus"));

        final VBox vBox = new VBox(P2LibConst.PADDING);
        vBox.setPadding(new Insets(P2LibConst.PADDING));
        vBox.getChildren().addAll(gridPane, hBox);
        VBox.setVgrow(gridPane, Priority.ALWAYS);

        TitledPane tpConfig = new TitledPane("Proxy", vBox);
        if (result != null) {
            result.add(tpConfig);
        }
        return tpConfig;
    }
}
