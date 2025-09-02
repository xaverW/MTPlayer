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
import de.p2tools.mtplayer.controller.filter.FilterDto;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.scene.layout.VBox;

public class FilmFilterControllerOnlyNew extends VBox {

    private final P2ToggleSwitch tglOnlyNew = new P2ToggleSwitch("Nur neue");
    private final ProgData progData;
    private final FilterDto filterDto;

    public FilmFilterControllerOnlyNew(FilterDto filterDto) {
        super();
        this.filterDto = filterDto;
        progData = ProgData.getInstance();
        addFilter();
    }

    private void addFilter() {
        this.getChildren().add(tglOnlyNew);
        tglOnlyNew.selectedProperty().bindBidirectional(filterDto.filterWorker.actFilterSettings.onlyNewProperty());
    }
}
