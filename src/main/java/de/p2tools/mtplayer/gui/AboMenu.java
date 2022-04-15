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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.MTShortcut;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.tools.filmFilter.FilmFilter;
import de.p2tools.p2Lib.tools.shortcut.PShortcutWorker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

public class AboMenu {
    final private VBox vBox;
    final private ProgData progData;

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
                "Neues Abo", "Neus Abo anlegen", new ProgIcons().FX_ICON_TOOLBAR_ABO_NEW);

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btOn = new ToolBarButton(vBox,
                "Abos einschalten", "Markierte Abos einschalten", new ProgIcons().FX_ICON_TOOLBAR_ABO_ON);
        final ToolBarButton btOff = new ToolBarButton(vBox,
                "Abos ausschalten", "Markierte Abos ausschalten", new ProgIcons().FX_ICON_TOOLBAR_ABO_OFF);
        final ToolBarButton btDel = new ToolBarButton(vBox,
                "Abos löschen", "Markierte Abos löschen", new ProgIcons().FX_ICON_TOOLBAR_ABO_DEL);
        final ToolBarButton btChange = new ToolBarButton(vBox,
                "Abos ändern", "Markierte Abos ändern", new ProgIcons().FX_ICON_TOOLBAR_ABO_CONFIG);

        btNew.setOnAction(a -> progData.aboList.addNewAbo("Neu", "", "", ""));
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
        final MenuItem miNew = new MenuItem("Neues Abo anlegen");
        miNew.setOnAction(a -> progData.aboList.addNewAbo("Neu", "", "", ""));
        final MenuItem miAboAddFilter = new MenuItem("Aus dem Film-Filter ein Abo erstellen");
        miAboAddFilter.setOnAction(a -> {
            FilmFilter filmFilter = progData.actFilmFilterWorker.getActFilterSettings();
            progData.aboList.addNewAboFromFilter(filmFilter);
        });

        mb.getItems().addAll(mbOn, mbOff, miDel, miChange, miNew, miAboAddFilter);

        final MenuItem miSelectAll = new MenuItem("Alles auswählen");
        miSelectAll.setOnAction(a -> progData.aboGuiController.selectAll());
        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> progData.aboGuiController.invertSelection());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miSelectAll, miSelection);

        final MenuItem miShowFilter = new MenuItem("Filter ein-/ausblenden");
        //ausgeführt wird aber der Button im Tab Filme!!
        miShowFilter.setOnAction(a -> progData.mtPlayerController.setFilter());
        PShortcutWorker.addShortCut(miShowFilter, MTShortcut.SHORTCUT_SHOW_FILTER);

        final MenuItem miShowInfo = new MenuItem("Infos ein-/ausblenden");
        miShowInfo.setOnAction(a -> progData.mtPlayerController.setInfos());
        PShortcutWorker.addShortCut(miShowInfo, MTShortcut.SHORTCUT_SHOW_INFOS);

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);
        vBox.getChildren().add(mb);
    }
}
