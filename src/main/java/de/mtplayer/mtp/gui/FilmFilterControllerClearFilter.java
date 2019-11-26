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
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import de.p2tools.p2Lib.tools.duration.PDuration;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FilmFilterControllerClearFilter extends VBox {

    private final Button btnClearFilter = new Button("Filter löschen");
    private final Button btnEditFilter = new Button("");

    private final ProgData progData;

    public FilmFilterControllerClearFilter() {
        super();
        progData = ProgData.getInstance();

        setPadding(new Insets(15, 15, 15, 15));
        setSpacing(20);

        addButton();
    }

    private void addButton() {
        btnClearFilter.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
        btnClearFilter.setOnAction(a -> clearFilter());

        btnEditFilter.setGraphic(new ProgIcons().ICON_BUTTON_EDIT_FILTER);
        btnEditFilter.setOnAction(a -> editFilter());
        btnEditFilter.setTooltip(new Tooltip("Filter ein/ausschalten"));

        HBox hBox = new HBox();
        hBox.getChildren().addAll(btnEditFilter, PGuiTools.getHBoxGrower(), btnClearFilter);
        getChildren().addAll(hBox);
    }

    private void clearFilter() {
        PDuration.onlyPing("Filter löschen");
        if (progData.storedFilters.txtFilterIsEmpty()) {
            progData.storedFilters.clearFilter();
        } else {
            progData.storedFilters.clearTxtFilter();
        }
    }

    private void editFilter() {
        final FilmFilterEditDialog editFilterDialog = new FilmFilterEditDialog(progData);
    }
}
