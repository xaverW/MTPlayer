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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filter.FilterDto;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class FilmSmallFilterControllerFilter extends HBox {

    private final ProgData progData;
    private final FilterDto filterDto;

    public FilmSmallFilterControllerFilter(FilterDto filterDto) {
        this.filterDto = filterDto;
        progData = ProgData.getInstance();

        Button btnSmall = new Button("Alle Filter anzeigen");
        if (filterDto.audio) {
            btnSmall.setOnAction(a -> ProgConfig.AUDIOFILTER_SMALL_FILTER.set(false));
        } else {
            btnSmall.setOnAction(a -> ProgConfig.FILMFILTER_SMALL_FILTER.set(false));
        }
        setSpacing(5);

        final Button btnClearFilter = P2ButtonClearFilterFactory.getPButtonClearFilter();
        btnClearFilter.setOnAction(a -> clearFilter());

        final Button btnHelp = P2Button.helpButton("Filter", HelpText.FILTER_SMALL_INFO);

        setAlignment(Pos.CENTER_RIGHT);
        getChildren().addAll(btnSmall, P2GuiTools.getHBoxGrower(), btnHelp, btnClearFilter);

    }

    private void clearFilter() {
        P2Duration.onlyPing("Filter löschen");
        filterDto.filterWorker.clearFilter();
    }
}
