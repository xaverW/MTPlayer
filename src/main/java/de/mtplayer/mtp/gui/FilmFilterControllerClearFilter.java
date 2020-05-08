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

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import de.p2tools.p2Lib.tools.duration.PDuration;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FilmFilterControllerClearFilter extends VBox {

    private final Button btnClearFilter = new Button("Filter _löschen");
    private final Button btnEditFilter = new Button("");
    private final Button btnGoBack = new Button("");
    private final Button btnGoForward = new Button("");

    private final ProgData progData;

    public FilmFilterControllerClearFilter() {
        super();
        progData = ProgData.getInstance();
        progData.filmFilterControllerClearFilter = this;

        setPadding(new Insets(15, 15, 15, 15));
        setSpacing(20);

        addButton();
    }

    public void setClearText(String txt) {
        btnClearFilter.setText(txt);
    }

    private void addButton() {
        btnGoBack.setGraphic(new ProgIcons().ICON_BUTTON_BACKWARD);
        btnGoBack.setOnAction(a -> progData.storedFilters.goBack());
        btnGoBack.disableProperty().bind(progData.storedFilters.backwardProperty().not());
        btnGoBack.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));
        btnGoForward.setGraphic(new ProgIcons().ICON_BUTTON_FORWARD);
        btnGoForward.setOnAction(a -> progData.storedFilters.goForward());
        btnGoForward.disableProperty().bind(progData.storedFilters.forwardProperty().not());
        btnGoForward.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));

        btnClearFilter.setOnAction(a -> clearFilter());
        btnClearFilter.setTooltip(new Tooltip("Textfilter löschen, ein zweiter Klick löscht alle Filter"));

        btnEditFilter.setGraphic(new ProgIcons().ICON_BUTTON_EDIT_FILTER);
        btnEditFilter.setOnAction(a -> editFilter());
        btnEditFilter.setTooltip(new Tooltip("Filter ein/ausschalten"));

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(btnEditFilter, PGuiTools.getHBoxGrower(), btnGoBack, btnGoForward, btnClearFilter);
        getChildren().addAll(hBox);
    }

    private void clearFilter() {
        PDuration.onlyPing("Filter löschen");
        progData.storedFilters.clearFilter();
//        if (progData.storedFilters.txtFilterIsEmpty()) {
//            progData.storedFilters.clearFilter();
//        } else {
//            progData.storedFilters.clearTxtFilter();
//        }
    }

    private void editFilter() {
        final FilmFilterEditDialog editFilterDialog = new FilmFilterEditDialog(progData);
    }
}
