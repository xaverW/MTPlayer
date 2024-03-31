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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.filter.FilterController;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LiveFilmFilterController extends FilterController {

    //    private final ProgressBar progress = new ProgressBar();
    private final VBox vBoxFilter;
    private final ProgData progData;


    private final LiveFilmFilterText liveFilmFilterText;
    private final LiveFilmFilterClearList liveFilmFilterClearList;

    public LiveFilmFilterController() {
        super(ProgConfig.LIVE_FILM_GUI_FILTER_DIVIDER_ON);

        this.progData = ProgData.getInstance();
        this.liveFilmFilterText = new LiveFilmFilterText();
        this.liveFilmFilterClearList = new LiveFilmFilterClearList();


        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator3");
        sp.setMinHeight(0);

        vBoxFilter = getVBoxFilter(true);
        vBoxFilter.setSpacing(15);
        vBoxFilter.setAlignment(Pos.TOP_LEFT);

        init();
    }

    private void init() {
        // ARD, ZDF
        LiveFilterTabZdf liveFilterTabZdf = new LiveFilterTabZdf();
        LiveFilterTabArd liveFilterTabArd = new LiveFilterTabArd();
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(liveFilterTabArd, liveFilterTabZdf);
        vBoxFilter.getChildren().add(tabPane);

        vBoxFilter.getChildren().addAll(liveFilmFilterText);

        final Button btnHelp = P2Button.helpButton(progData.primaryStage, "Live-Suche",
                HelpText.LIVE_SEARCH);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(btnHelp);

        vBoxFilter.getChildren().add(P2GuiTools.getVBoxGrower());
        Separator sp1 = new Separator();
        sp1.getStyleClass().add("pseperator3");
        sp1.setMinHeight(0);
        vBoxFilter.getChildren().addAll(liveFilmFilterClearList, sp1, hBox);
    }
}
