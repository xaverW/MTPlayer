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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.VBox;

public class AboMenu {
    final private VBox vbox;
    final private Daten daten;
    private static final String ABO_ON_TEXT = "Abos einschalten";
    private static final String ABO_OFF_TEXT = "Abos ausschalten";
    private static final String ABO_DELETE_TEXT = "Abos löschen";
    private static final String ABO_CHANGE_TEXT = "Abos ändern";
    BooleanProperty boolDivOn = Config.ABO_GUI_FILTER_DIVIDER_ON.getBooleanProperty();

    public AboMenu(VBox vbox) {
        this.vbox = vbox;
        daten = Daten.getInstance();
    }


    public void init() {
        vbox.getChildren().clear();

        initMenu();
        initButton();
    }

    private void initButton() {
        // Button
        final ToolBarButton btOn = new ToolBarButton(vbox, "einschalten", ABO_ON_TEXT, new Icons().FX_ICON_TOOLBAR_ABO_EIN);
        final ToolBarButton btOff = new ToolBarButton(vbox, "ausschalten", ABO_OFF_TEXT, new Icons().FX_ICON_TOOLBAR_ABO_AUS);
        final ToolBarButton btDel = new ToolBarButton(vbox, "löschen", ABO_DELETE_TEXT, new Icons().FX_ICON_TOOLBAR_ABO_DEL);
        final ToolBarButton btChange =
                new ToolBarButton(vbox, "ändern", ABO_CHANGE_TEXT, new Icons().FX_ICON_TOOLBAR_ABO_CONFIG);

        btOn.setOnAction(a -> daten.aboGuiController.einAus(true));
        btOff.setOnAction(a -> daten.aboGuiController.einAus(false));
        btDel.setOnAction(a -> daten.aboGuiController.loeschen());
        btChange.setOnAction(a -> daten.aboGuiController.aendern());
    }

    private void initMenu() {
        // MenuButton
        final MenuButton mb = new MenuButton("");
        mb.setGraphic(new Icons().FX_ICON_TOOLBAR_MENUE);
        mb.getStyleClass().add("btnFunction");

        final MenuItem mbOn = new MenuItem("einschalten");
        mbOn.setOnAction(a -> daten.aboGuiController.einAus(true));

        final MenuItem mbOff = new MenuItem("ausschalten");
        mbOff.setOnAction(e -> daten.aboGuiController.einAus(false));

        final MenuItem miDel = new MenuItem("löschen");
        miDel.setOnAction(a -> daten.aboGuiController.loeschen());

        final MenuItem miChange = new MenuItem("ändern");
        miChange.setOnAction(a -> daten.aboGuiController.aendern());

        final MenuItem miNew = new MenuItem("neues Abo anlegen");
        miNew.setOnAction(a -> daten.aboGuiController.neu());

        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> daten.aboGuiController.invertSelection());

        final CheckMenuItem miShowFilter = new CheckMenuItem("Filter anzeigen");
        miShowFilter.selectedProperty().bindBidirectional(boolDivOn);

        mb.getItems().addAll(mbOn, mbOff, miDel, miChange, miNew,
                new SeparatorMenuItem(), miSelection,
                new SeparatorMenuItem(), miShowFilter);

        vbox.getChildren().add(mb);

    }
}
