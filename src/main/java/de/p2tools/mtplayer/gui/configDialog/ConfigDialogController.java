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

package de.p2tools.mtplayer.gui.configDialog;

import de.p2tools.mtplayer.gui.configDialog.setData.SetPaneController;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class ConfigDialogController extends PDialogExtra {

    private TabPane tabPane = new TabPane();
    private Button btnOk = new Button("_Ok");
    private String geo = ProgConfig.SYSTEM_GEO_HOME_PLACE.get();
    private BooleanProperty blackChanged = new SimpleBooleanProperty(false);

    IntegerProperty propSelectedTab = ProgConfig.SYSTEM_CONFIG_DIALOG_TAB;
    private final ProgData progData;

    ConfigPaneController configPane;
    FilmPaneController filmPane;
    BlackListPaneController blackPane;
    DownloadPaneController downloadPane;
    SetPaneController setPane;

    public ConfigDialogController() {
        super(ProgData.getInstance().primaryStage, ProgConfig.CONFIG_DIALOG_SIZE.getStringProperty(), "Einstellungen",
                true, false, DECO.NONE);

        this.progData = ProgData.getInstance();
        init(true);
    }

    @Override
    public void make() {
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        getvBoxCont().getChildren().add(tabPane);
        getvBoxCont().setPadding(new Insets(0));

        addOkButton(btnOk);
        btnOk.setOnAction(a -> close());

        ProgConfig.SYSTEM_THEME_CHANGED.getStringProperty().addListener((u, o, n) -> updateCss());
        initPanel();
    }

    public void close() {
        if (!geo.equals(ProgConfig.SYSTEM_GEO_HOME_PLACE.get())) {
            // dann hat sich der Geo-Standort geändert
            System.out.println("geo changed");
            progData.filmlist.markGeoBlocked();
        }

        if (blackChanged.get() && !progData.loadFilmlist.getPropLoadFilmlist()) {
            // sonst hat sich nichts geändert oder wird dann eh gemacht
            System.out.println("black filtern");
            progData.filmlist.filterList();
            Listener.notify(Listener.EREIGNIS_BLACKLIST_GEAENDERT, ConfigDialogController.class.getSimpleName());
        }

        configPane.close();
        filmPane.close();
        blackPane.close();
        downloadPane.close();
        setPane.close();

        Listener.notify(Listener.EREIGNIS_SETDATA_CHANGED, ConfigDialogController.class.getSimpleName());
        super.close();
    }

    private void initPanel() {
        try {
            configPane = new ConfigPaneController(getStage());
            Tab tab = new Tab("Allgemein");
            tab.setClosable(false);
            tab.setContent(configPane);
            tabPane.getTabs().add(tab);

            filmPane = new FilmPaneController(getStage());
            tab = new Tab("Filmliste laden");
            tab.setClosable(false);
            tab.setContent(filmPane);
            tabPane.getTabs().add(tab);

            blackPane = new BlackListPaneController(getStage(), blackChanged);
            tab = new Tab("Blacklist");
            tab.setClosable(false);
            tab.setContent(blackPane);
            tabPane.getTabs().add(tab);

            downloadPane = new DownloadPaneController(getStage());
            tab = new Tab("Download");
            tab.setClosable(false);
            tab.setContent(downloadPane);
            tabPane.getTabs().add(tab);

            setPane = new SetPaneController(getStage());
            tab = new Tab("Aufzeichnen/Abspielen");
            tab.setClosable(false);
            tab.setContent(setPane);
            tabPane.getTabs().add(tab);

            tabPane.getSelectionModel().select(propSelectedTab.get());
            tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                // readOnlyBinding!!
                propSelectedTab.setValue(newValue);
            });

        } catch (final Exception ex) {
            PLog.errorLog(784459510, ex);
        }
    }

}
