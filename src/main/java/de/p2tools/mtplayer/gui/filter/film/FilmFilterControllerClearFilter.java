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

package de.p2tools.mtplayer.gui.filter.film;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.gui.filter.FilterController;
import de.p2tools.mtplayer.gui.filter.helper.PCboTextFilter;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.tools.duration.P2Duration;
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
    private final PCboTextFilter cboTextFilter = new PCboTextFilter();

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
        btnGoBack.setOnAction(a -> progData.filterWorker.getBackwardFilmFilter().goBackward());
        btnGoBack.disableProperty().bind(ProgData.getInstance().filterWorker.getBackwardFilterList().emptyProperty()
                .or(ProgData.getInstance().filterWorker.getBackwardFilterList().sizeProperty().isEqualTo(1))); // 1 ist der aktuelle Filter!
        btnGoBack.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));

        btnGoForward.setGraphic(ProgIcons.ICON_BUTTON_FORWARD.getImageView());
        btnGoForward.setOnAction(a -> progData.filterWorker.getBackwardFilmFilter().goForward());
        btnGoForward.disableProperty().bind(ProgData.getInstance().filterWorker.getForwardFilterList().emptyProperty());
        btnGoForward.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));

        btnClearFilter.setOnAction(a -> clearFilter());

        btnEditFilter.setGraphic(ProgIcons.ICON_BUTTON_EDIT.getImageView());
        btnEditFilter.setOnAction(a -> new FilmFilterEditDialog(progData));
        btnEditFilter.setTooltip(new Tooltip("Filter ein/ausschalten"));

        HBox hBox1 = new HBox(P2LibConst.DIST_BUTTON);
        hBox1.getChildren().addAll(btnEditFilter, P2GuiTools.getHBoxGrower(), btnGoBack, btnGoForward,
                P2GuiTools.getHBoxGrower(), btnClearFilter);

        final Button btnHelp = P2Button.helpButton("Filter", HelpText.FILTER_INFO);
        HBox hBox2 = new HBox(P2LibConst.DIST_BUTTON);
        hBox2.getChildren().addAll(cboTextFilter, btnHelp);
        HBox.setHgrow(cboTextFilter, Priority.ALWAYS);

        getChildren().addAll(hBox1, hBox2);
    }


    private void clearFilter() {
        P2Duration.onlyPing("Filter löschen");
        progData.filterWorker.clearFilter();
    }
}
