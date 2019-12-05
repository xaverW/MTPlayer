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

package de.mtplayer.mtp.gui.configDialog;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.gui.tools.Listener;
import de.p2tools.p2Lib.dialog.PDialog;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ConfigDialogController extends PDialog {

    private TabPane tabPane = new TabPane();
    private Button btnOk = new Button("Ok");
    private String geo = ProgConfig.SYSTEM_GEO_HOME_PLACE.get();
    private Stage stage;

    IntegerProperty propSelectedTab = ProgConfig.SYSTEM_CONFIG_DIALOG_TAB;

    private final ProgData progData;

    public ConfigDialogController() {
        super(ProgConfig.CONFIG_DIALOG_SIZE.getStringProperty(), "Einstellungen", true);

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);

        vBox.getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

//        HBox hBox = new HBox();
//        hBox.setAlignment(Pos.CENTER_RIGHT);
//        hBox.getChildren().add(btnOk);
//        vBox.getChildren().add(hBox);

        ButtonBar buttonBar = new ButtonBar();
        ButtonBar.setButtonData(btnOk, ButtonBar.ButtonData.OK_DONE);
        buttonBar.getButtons().add(btnOk);
        vBox.getChildren().add(buttonBar);

        this.progData = ProgData.getInstance();
        init(vBox, true);
    }

    @Override
    public void make() {
        stage = getStage();
//        btnOk.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
        btnOk.setOnAction(a -> close());

        ProgConfig.SYSTEM_DARK_THEME.getStringProperty().addListener((u, o, n) -> updateCss());
        initPanel();
    }

    public void close() {
        if (!geo.equals(ProgConfig.SYSTEM_GEO_HOME_PLACE.get())) {
            // dann hat sich der Geo-Standort geändert
            progData.filmlist.markGeoBlocked();
        }

        // todo nur wenn die Black und Geo wirklich geändert
        if (!progData.loadFilmlist.getPropLoadFilmlist()) {
            // wird sonst dann eh gemacht
            progData.filmlist.filterList();
            Listener.notify(Listener.EREIGNIS_BLACKLIST_GEAENDERT, ConfigDialogController.class.getSimpleName());
        }
        Listener.notify(Listener.EREIGNIS_SETDATA_CHANGED, ConfigDialogController.class.getSimpleName());
        super.close();
    }

    private void initPanel() {
        try {
            AnchorPane configPane = new ConfigPaneController(stage);
            Tab tab = new Tab("Allgemein");
            tab.setClosable(false);
            tab.setContent(configPane);
            tabPane.getTabs().add(tab);

            AnchorPane filmPane = new FilmPaneController(stage);
            tab = new Tab("Filmliste laden");
            tab.setClosable(false);
            tab.setContent(filmPane);
            tabPane.getTabs().add(tab);

            AnchorPane blackPane = new BlackListPaneController(stage);
            tab = new Tab("Blacklist");
            tab.setClosable(false);
            tab.setContent(blackPane);
            tabPane.getTabs().add(tab);

            AnchorPane downloadPane = new DownloadPaneController(stage);
            tab = new Tab("Download");
            tab.setClosable(false);
            tab.setContent(downloadPane);
            tabPane.getTabs().add(tab);

            AnchorPane setPane = new SetPaneController(stage);
            tab = new Tab("Aufzeichnen/Abspielen");
            tab.setClosable(false);
            tab.setContent(setPane);
            tabPane.getTabs().add(tab);

            tabPane.getSelectionModel().select(propSelectedTab.get());
            tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
                    // readOnlyBinding!!
                    propSelectedTab.setValue(newValue));

        } catch (final Exception ex) {
            PLog.errorLog(784459510, ex);
        }
    }

}
