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

package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PButtonClearFilter;
import de.p2tools.p2lib.guitools.PGuiTools;
import de.p2tools.p2lib.tools.duration.PDuration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FilmFilterControllerClearFilter extends VBox {

    private final PButtonClearFilter btnClearFilter = new PButtonClearFilter();
    private final Button btnEditFilter = new Button("");
    private final Button btnGoBack = new Button("");
    private final Button btnGoForward = new Button("");

    private final ProgData progData;

    public FilmFilterControllerClearFilter() {
        super();
        progData = ProgData.getInstance();
        progData.filmFilterControllerClearFilter = this;

        setPadding(new Insets(10, 15, 5, 15));
        setSpacing(FilterController.FILTER_SPACING_CLEAR);

        addButton();
    }

    private void addButton() {
        btnGoBack.setGraphic(ProgIcons.Icons.ICON_BUTTON_BACKWARD.getImageView());
        btnGoBack.setOnAction(a -> progData.actFilmFilterWorker.goBackward());
        btnGoBack.disableProperty().bind(progData.actFilmFilterWorker.backwardProperty().not());
        btnGoBack.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));
        btnGoForward.setGraphic(ProgIcons.Icons.ICON_BUTTON_FORWARD.getImageView());
        btnGoForward.setOnAction(a -> progData.actFilmFilterWorker.goForward());
        btnGoForward.disableProperty().bind(progData.actFilmFilterWorker.forwardProperty().not());
        btnGoForward.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));

        btnClearFilter.setOnAction(a -> clearFilter());

        btnEditFilter.setGraphic(ProgIcons.Icons.ICON_BUTTON_EDIT_FILTER.getImageView());
        btnEditFilter.setOnAction(a -> editFilter());
        btnEditFilter.setTooltip(new Tooltip("Filter ein/ausschalten"));


        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5, 0, 0, 0));
        hBox.getChildren().addAll(btnEditFilter, PGuiTools.getHBoxGrower(), btnGoBack, btnGoForward, btnClearFilter);
        getChildren().addAll(hBox);
    }

    private void clearFilter() {
        PDuration.onlyPing("Filter löschen");
        progData.actFilmFilterWorker.clearFilter();
    }

    private void editFilter() {
        final FilmFilterEditDialog editFilterDialog = new FilmFilterEditDialog(progData);
    }
}
