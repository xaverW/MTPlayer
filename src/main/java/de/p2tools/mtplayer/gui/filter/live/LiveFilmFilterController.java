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

package de.p2tools.mtplayer.gui.filter.live;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.filter.FilterController;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LiveFilmFilterController extends FilterController {

    private final VBox vBoxFilter;
    private final ProgData progData;
    private final LiveFilmFilterText liveFilmFilterText;
    private final LiveFilmFilterClearList liveFilmFilterClearList;

    public LiveFilmFilterController() {
        this.progData = ProgData.getInstance();
        this.liveFilmFilterText = new LiveFilmFilterText();
        this.liveFilmFilterClearList = new LiveFilmFilterClearList();
        vBoxFilter = getVBoxFilter();
        init();
    }

    private void init() {
        // ARD, ZDF
        LiveFilterTabZdf liveFilterTabZdf = new LiveFilterTabZdf();
        LiveFilterTabArd liveFilterTabArd = new LiveFilterTabArd();
        LiveFilterTabArte liveFilterTabArte = new LiveFilterTabArte();
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(liveFilterTabArd, liveFilterTabZdf, liveFilterTabArte);
        vBoxFilter.getChildren().add(tabPane);

        vBoxFilter.getChildren().addAll(liveFilmFilterText);
        VBox.setVgrow(liveFilmFilterText, Priority.ALWAYS);

        getVBoxBlack().getChildren().addAll(liveFilmFilterClearList);
    }
}
