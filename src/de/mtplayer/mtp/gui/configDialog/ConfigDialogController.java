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

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.gui.dialog.MTDialog;
import de.mtplayer.mtp.gui.tools.Listener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class ConfigDialogController extends MTDialog {

    private TabPane tabPane = new TabPane();
    private Button btnOk = new Button("Ok");
    private String geo = Config.SYSTEM_GEO_HOME_PLACE.get();

    private final Daten daten;

    public ConfigDialogController() {
        super(null, Config.CONFIG_DIALOG_SIZE, "Einstellungen", true);

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);

        vBox.getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(btnOk);
        vBox.getChildren().add(hBox);

        this.daten = Daten.getInstance();
        init(vBox, true);
    }

    @Override
    public void make() {
        btnOk.setOnAction(a -> close());
        initPanel();
    }

    public void close() {
        if (!geo.equals(Config.SYSTEM_GEO_HOME_PLACE.get())) {
            // dann hat sich der Geo-Standort geändert
            daten.filmlist.markGeoBlocked();
        }

        // todo nur wenn die Black und Geo wirklich geändert
        if (!daten.loadFilmlist.getPropLoadFilmlist()) {
            // wird sonst dann eh gemacht
            daten.filmlist.filterList();
            Listener.notify(Listener.EREIGNIS_BLACKLIST_GEAENDERT, ConfigDialogController.class.getSimpleName());
        }
        super.close();
    }


    private void initPanel() {
        try {

            AnchorPane configPane = new ConfigPaneController();
            Tab tab = new Tab("Allgemein");
            tab.setClosable(false);
            tab.setContent(configPane);
            tabPane.getTabs().add(tab);

            AnchorPane filmPane = new FilmPaneController();
            tab = new Tab("Filme laden");
            tab.setClosable(false);
            tab.setContent(filmPane);
            tabPane.getTabs().add(tab);

            AnchorPane blackPane = new BlackListPaneController();
            tab = new Tab("Blacklist");
            tab.setClosable(false);
            tab.setContent(blackPane);
            tabPane.getTabs().add(tab);

            AnchorPane downloadPane = new DownloadPaneController();
            tab = new Tab("Download");
            tab.setClosable(false);
            tab.setContent(downloadPane);
            tabPane.getTabs().add(tab);

            AnchorPane setPane = new SetPaneController();
            tab = new Tab("Aufzeichnen/Abspielen");
            tab.setClosable(false);
            tab.setContent(setPane);
            tabPane.getTabs().add(tab);

        } catch (final Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
