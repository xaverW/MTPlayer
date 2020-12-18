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

package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilter;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class AboMenu {
    final private VBox vBox;
    final private ProgData progData;
    BooleanProperty boolDivOn = ProgConfig.ABO_GUI_FILTER_DIVIDER_ON.getBooleanProperty();
    BooleanProperty boolInfoOn = ProgConfig.ABO_GUI_DIVIDER_ON.getBooleanProperty();

    public AboMenu(VBox vBox) {
        this.vBox = vBox;
        progData = ProgData.getInstance();
    }


    public void init() {
        vBox.getChildren().clear();

        initMenu();
        initButton();
    }

    private void initButton() {
        // Button
        VBox vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(0);
        vBoxSpace.setMinHeight(0);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btNew = new ToolBarButton(vBox,
                "neues Abo", "neus Abo anlegen", new ProgIcons().FX_ICON_TOOLBAR_ABO_NEW);

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btOn = new ToolBarButton(vBox,
                "Abos einschalten", "markierte Abos einschalten", new ProgIcons().FX_ICON_TOOLBAR_ABO_ON);
        final ToolBarButton btOff = new ToolBarButton(vBox,
                "Abos ausschalten", "markierte Abos ausschalten", new ProgIcons().FX_ICON_TOOLBAR_ABO_OFF);
        final ToolBarButton btDel = new ToolBarButton(vBox,
                "Abos löschen", "markierte Abos löschen", new ProgIcons().FX_ICON_TOOLBAR_ABO_DEL);
        final ToolBarButton btChange = new ToolBarButton(vBox,
                "Abos ändern", "markierte Abos ändern", new ProgIcons().FX_ICON_TOOLBAR_ABO_CONFIG);

        btNew.setOnAction(a -> progData.aboGuiController.addNewAbo());
        btOn.setOnAction(a -> progData.aboGuiController.setAboActive(true));
        btOff.setOnAction(a -> progData.aboGuiController.setAboActive(false));
        btDel.setOnAction(a -> progData.aboGuiController.deleteAbo());
        btChange.setOnAction(a -> progData.aboGuiController.changeAbo());
    }

    private void initMenu() {
        // MenuButton
        final MenuButton mb = new MenuButton("");
        mb.setTooltip(new Tooltip("Abomenü anzeigen"));
        mb.setGraphic(new ProgIcons().FX_ICON_TOOLBAR_MENU);
        mb.getStyleClass().add("btnFunctionWide");

        final MenuItem mbOn = new MenuItem("Abos einschalten");
        mbOn.setOnAction(a -> progData.aboGuiController.setAboActive(true));
        final MenuItem mbOff = new MenuItem("Abos ausschalten");
        mbOff.setOnAction(e -> progData.aboGuiController.setAboActive(false));
        final MenuItem miDel = new MenuItem("Abos löschen");
        miDel.setOnAction(a -> progData.aboGuiController.deleteAbo());
        final MenuItem miChange = new MenuItem("Abos ändern");
        miChange.setOnAction(a -> progData.aboGuiController.changeAbo());
        final MenuItem miNew = new MenuItem("neues Abo anlegen");
        miNew.setOnAction(a -> progData.aboGuiController.addNewAbo());
        final MenuItem miAboAddFilter = new MenuItem("aus dem Film-Filter ein Abo erstellen");
        miAboAddFilter.setOnAction(a -> {
            SelectedFilter selectedFilter = progData.storedFilters.getActFilterSettings();
            progData.aboList.addNewAbo(selectedFilter);
        });

        mb.getItems().addAll(mbOn, mbOff, miDel, miChange, miNew, miAboAddFilter);


        final MenuItem miSelectAll = new MenuItem("alles auswählen");
        miSelectAll.setOnAction(a -> progData.aboGuiController.selectAll());
        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> progData.aboGuiController.invertSelection());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miSelectAll, miSelection);


        final CheckMenuItem miShowFilter = new CheckMenuItem("Filter anzeigen");
        miShowFilter.selectedProperty().bindBidirectional(boolDivOn);
        final CheckMenuItem miShowInfo = new CheckMenuItem("Infos anzeigen");
        miShowInfo.selectedProperty().bindBidirectional(boolInfoOn);

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);

        vBox.getChildren().add(mb);
    }
}
