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

import de.p2tools.mtplayer.MTPlayerController;
import de.p2tools.mtplayer.MTPlayerFactory;
import de.p2tools.mtplayer.controller.config.PShortKeyFactory;
import de.p2tools.mtplayer.controller.config.PShortcut;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.abo.AboListFactory;
import javafx.beans.binding.Bindings;
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
                "Neues Abo", "Neus Abo anlegen", ProgIcons.ICON_TOOLBAR_ABO_NEW.getImageView());

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btOn = new ToolBarButton(vBox,
                "Abos einschalten", "Markierte Abos einschalten", ProgIcons.ICON_TOOLBAR_ABO_ON.getImageView());
        final ToolBarButton btOff = new ToolBarButton(vBox,
                "Abos ausschalten", "Markierte Abos ausschalten", ProgIcons.ICON_TOOLBAR_ABO_OFF.getImageView());
        final ToolBarButton btDel = new ToolBarButton(vBox,
                "Abos löschen", "Markierte Abos löschen", ProgIcons.ICON_TOOLBAR_ABO_DEL.getImageView());
        final ToolBarButton btChange = new ToolBarButton(vBox,
                "Abos ändern", "Markierte Abos ändern", ProgIcons.ICON_TOOLBAR_ABO_CONFIG.getImageView());

        btNew.setOnAction(a -> {
            AboListFactory.addNewAbo("Neu", "", "", "");
            progData.aboGuiController.tableView.refresh();
            progData.aboGuiController.tableView.requestFocus();
        });
        btOn.setOnAction(a -> {
            AboListFactory.setAboActive(true);
            progData.aboGuiController.tableView.refresh();
            progData.aboGuiController.tableView.requestFocus();
        });
        btOff.setOnAction(a -> {
            AboListFactory.setAboActive(false);
            progData.aboGuiController.tableView.refresh();
            progData.aboGuiController.tableView.requestFocus();
        });
        btDel.setOnAction(a -> {
            AboListFactory.deleteAbo();
            progData.aboGuiController.tableView.refresh();
            progData.aboGuiController.tableView.requestFocus();
        });
        btChange.setOnAction(a -> {
            AboListFactory.editAbo();
            progData.aboGuiController.tableView.refresh();
            progData.aboGuiController.tableView.requestFocus();
        });
    }

    private void initMenu() {
        // MenuButton
        final MenuButton mb = new MenuButton("");
        mb.setTooltip(new Tooltip("Abomenü anzeigen"));
        mb.setGraphic(ProgIcons.ICON_TOOLBAR_MENU.getImageView());
        mb.getStyleClass().addAll("btnFunction", "btnFunc-2");

        final MenuItem mbOn = new MenuItem("Abos einschalten");
        mbOn.setOnAction(a -> AboListFactory.setAboActive(true));
        final MenuItem mbOff = new MenuItem("Abos ausschalten");
        mbOff.setOnAction(e -> AboListFactory.setAboActive(false));
        final MenuItem miDel = new MenuItem("Abos löschen");
        miDel.setOnAction(a -> AboListFactory.deleteAbo());
        final MenuItem miChange = new MenuItem("Abos ändern");
        miChange.setOnAction(a -> AboListFactory.editAbo());
        final MenuItem miNew = new MenuItem("Neues Abo anlegen");
        miNew.setOnAction(a -> AboListFactory.addNewAbo("Neu", "", "", ""));
        final MenuItem miAboAddFilter = new MenuItem("Aus dem Film-Filter ein Abo erstellen");
        miAboAddFilter.setOnAction(a -> {
            AboListFactory.addNewAboFromFilterButton();
        });

        final MenuItem miUndo = new MenuItem("Gelöschte wieder anlegen" + PShortKeyFactory.SHORT_CUT_LEER +
                PShortcut.SHORTCUT_UNDO_DELETE.getActShortcut());
        miUndo.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.ABO) {
                return;
            }
            progData.aboList.undoAbos();
        });
//        PShortcutWorker.addShortCut(miUndo, ProgShortcut.SHORTCUT_DOWNLOAD_UNDO_DELETE);
        miUndo.disableProperty().bind(Bindings.isEmpty(progData.aboList.getUndoList()));

        mb.getItems().addAll(mbOn, mbOff, miDel, miChange, miNew, miAboAddFilter, miUndo);

        final MenuItem miSelectAll = new MenuItem("Alles auswählen");
        miSelectAll.setOnAction(a -> progData.aboGuiController.selectAll());
        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> progData.aboGuiController.invertSelection());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miSelectAll, miSelection);

        final MenuItem miShowFilter = new MenuItem("Filter ein-/ausblenden" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SHOW_FILTER.getActShortcut());
        //ausgeführt wird aber der Button im Tab Filme!!
        miShowFilter.setOnAction(a -> MTPlayerFactory.setFilter());

        final MenuItem miShowInfo = new MenuItem("Infos ein-/ausblenden" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SHOW_INFOS.getActShortcut());
        miShowInfo.setOnAction(a -> MTPlayerFactory.setInfos());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);
        vBox.getChildren().add(mb);
    }
}
