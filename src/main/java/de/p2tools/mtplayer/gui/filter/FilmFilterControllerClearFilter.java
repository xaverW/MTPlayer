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
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.tools.duration.PDuration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilmFilterControllerClearFilter extends VBox {

    private final Button btnClearFilter = P2ButtonClearFilterFactory.getPButtonClearFilter();
    private final Button btnEditFilter = new Button("");
    private final Button btnGoBack = new Button("");
    private final Button btnGoForward = new Button("");
    private final PCboFilmFilter cboBack = new PCboFilmFilter();

    private final ProgData progData;

    public FilmFilterControllerClearFilter() {
        super();
        progData = ProgData.getInstance();
        progData.filmFilterControllerClearFilter = this;

        setSpacing(FilterController.FILTER_SPACING_TEXTFILTER);
        addButton();
    }

    private void addButton() {
        btnGoBack.setGraphic(ProgIcons.ICON_BUTTON_BACKWARD.getImageView());
        btnGoBack.setOnAction(a -> progData.filmFilterWorker.getBackwardFilmFilter().goBackward());
        btnGoBack.disableProperty().bind(ProgData.getInstance().filmFilterWorker.getBackwardFilterList().emptyProperty()
                .or(ProgData.getInstance().filmFilterWorker.getBackwardFilterList().sizeProperty().isEqualTo(1))); // 1 ist der aktuelle Filter!
        btnGoBack.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));

        btnGoForward.setGraphic(ProgIcons.ICON_BUTTON_FORWARD.getImageView());
        btnGoForward.setOnAction(a -> progData.filmFilterWorker.getBackwardFilmFilter().goForward());
        btnGoForward.disableProperty().bind(ProgData.getInstance().filmFilterWorker.getForwardFilterList().emptyProperty());
        btnGoForward.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));

        cboBack.getCbo().valueProperty().addListener((u, o, n) -> {
            if (n != null) {
                FilmFilter actFilmFilter = ProgData.getInstance().filmFilterWorker.getActFilterSettings().getCopy();
                actFilmFilter.setChannel(n.getChannel());
                actFilmFilter.setExactTheme(n.getTheme());
                actFilmFilter.setTheme(n.getTheme());
                actFilmFilter.setThemeTitle(n.getThemeTitle());
                actFilmFilter.setTitle(n.getTitle());
                actFilmFilter.setSomewhere(n.getSomewhere());
                ProgData.getInstance().filmFilterWorker.setActFilterSettings(actFilmFilter);
            }
        });

        btnClearFilter.setOnAction(a -> clearFilter());

        btnEditFilter.setGraphic(ProgIcons.ICON_BUTTON_EDIT_FILTER.getImageView());
        btnEditFilter.setOnAction(a -> new FilmFilterEditDialog(progData));
        btnEditFilter.setTooltip(new Tooltip("Filter ein/ausschalten"));


        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5, 0, 0, 0));
        hBox.getChildren().addAll(btnEditFilter,
                cboBack, btnGoBack, btnGoForward, btnClearFilter);
        HBox.setHgrow(cboBack, Priority.ALWAYS);
        getChildren().addAll(hBox);
    }


    private void clearFilter() {
        PDuration.onlyPing("Filter löschen");
        progData.filmFilterWorker.clearFilter();
    }
}
