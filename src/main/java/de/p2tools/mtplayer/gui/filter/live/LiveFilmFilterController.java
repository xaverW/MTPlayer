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
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.livesearch.LiveFactory;
import de.p2tools.mtplayer.controller.livesearchard.JsonInfoDtoArd;
import de.p2tools.mtplayer.controller.livesearchard.LiveSearchArd;
import de.p2tools.mtplayer.controller.livesearchzdf.JsonInfoDtoZdf;
import de.p2tools.mtplayer.controller.livesearchzdf.LiveSearchZdf;
import de.p2tools.mtplayer.gui.filter.FilterController;
import de.p2tools.mtplayer.gui.filter.helper.PCboStringSearch;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class LiveFilmFilterController extends FilterController {

    private final ProgressBar progress = new ProgressBar();
    private final VBox vBoxFilter;
    private final VBox vBoxBottom;
    private final PCboStringSearch cboSearch;
    private final ProgData progData;
    private final JsonInfoDtoArd jsonInfoDtoArd = new JsonInfoDtoArd();
    private IntegerProperty siteNo = new SimpleIntegerProperty(0);


    private final LiveFilmFilterText liveFilmFilterText;
    private final LiveFilmFilterClearList liveFilmFilterClearList;

    public LiveFilmFilterController() {
        super(ProgConfig.LIVE_FILM_GUI_FILTER_DIVIDER_ON);

        this.progData = ProgData.getInstance();
        this.liveFilmFilterText = new LiveFilmFilterText();
        this.liveFilmFilterClearList = new LiveFilmFilterClearList();

        cboSearch = new PCboStringSearch(progData, ProgConfig.LIVE_FILM_GUI_SEARCH);

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator3");
        sp.setMinHeight(0);

        vBoxFilter = getVBoxFilter(true);
        vBoxFilter.setSpacing(15);
        vBoxFilter.setAlignment(Pos.TOP_CENTER);
        vBoxBottom = getVBoxBottom();

        init();
    }

    private void init() {
        Button btnClear = new Button();
        btnClear.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnClear.setTooltip(new Tooltip("Suche löschen"));
        btnClear.setOnAction(a -> {
            ProgConfig.LIVE_FILM_GUI_SEARCH.set("");
        });


        HBox hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(cboSearch, btnClear);
        HBox.setHgrow(cboSearch, Priority.ALWAYS);
        VBox vBox = new VBox();
        vBox.setSpacing(2);
        vBox.getChildren().addAll(new Label("Livesuche"), hBox);
        vBoxFilter.getChildren().add(vBox);

        // ARD, ZDF
        TabPane tabPane = new TabPane();
        Tab tabArd = new Tab("ARD");
        Tab tabZdf = new Tab("ZDF");
        tabArd.setClosable(false);
        tabZdf.setClosable(false);
        tabPane.getTabs().addAll(tabArd, tabZdf);

        // ARD
        Button btnSearchArd = new Button();
        btnSearchArd.setGraphic(ProgIcons.ICON_BUTTON_SEARCH_16.getImageView());
        btnSearchArd.setTooltip(new Tooltip("Suche starten"));
        btnSearchArd.setOnAction(a -> {
            siteNo.set(0);
            searchArd(0);
        });
        btnSearchArd.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH.length().lessThan(5))
                .or(LiveFactory.progressProperty.isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        Button btnKeepOnArd = new Button();
        btnKeepOnArd.setGraphic(ProgIcons.ICON_BUTTON_FORWARD.getImageView());
        btnKeepOnArd.setTooltip(new Tooltip("Weitersuchen"));
        btnKeepOnArd.setOnAction(a -> {
            long res = jsonInfoDtoArd.getSizeOverAll().get() - (long) siteNo.get() * JsonInfoDtoArd.PAGE_SIZE - JsonInfoDtoArd.PAGE_SIZE;
            if (res > 0) {
                siteNo.setValue(siteNo.get() + 1);
                searchArd(siteNo.get());
            }
        });
        btnKeepOnArd.disableProperty().bind((jsonInfoDtoArd.getSizeOverAll().lessThanOrEqualTo(siteNo.get() * JsonInfoDtoArd.PAGE_SIZE))
                .or(LiveFactory.progressProperty.isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.setPadding(new Insets(10, 0, 10, 0));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnSearchArd, btnKeepOnArd);
        tabArd.setContent(hBox);

        // ZDF
        Button btnSearchZdf = new Button();
        btnSearchZdf.setGraphic(ProgIcons.ICON_BUTTON_SEARCH_16.getImageView());
        btnSearchZdf.setTooltip(new Tooltip("Suche starten"));
        btnSearchZdf.setOnAction(a -> {
            siteNo.set(0);
            searchZdf(0);
        });
        btnSearchZdf.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH.length().lessThan(5))
                .or(LiveFactory.progressProperty.isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        Button btnKeepOnZdf = new Button();
        btnKeepOnZdf.setGraphic(ProgIcons.ICON_BUTTON_FORWARD.getImageView());
        btnKeepOnZdf.setTooltip(new Tooltip("Weitersuchen"));
        btnKeepOnZdf.setOnAction(a -> {
            long res = jsonInfoDtoArd.getSizeOverAll().get() - (long) siteNo.get() * JsonInfoDtoArd.PAGE_SIZE - JsonInfoDtoArd.PAGE_SIZE;
            if (res > 0) {
                siteNo.setValue(siteNo.get() + 1);
                searchZdf(siteNo.get());
            }
        });
        btnKeepOnZdf.disableProperty().bind((jsonInfoDtoArd.getSizeOverAll().lessThanOrEqualTo(siteNo.get() * JsonInfoDtoArd.PAGE_SIZE))
                .or(LiveFactory.progressProperty.isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.setPadding(new Insets(10, 0, 10, 0));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnSearchZdf, btnKeepOnZdf);
        tabZdf.setContent(hBox);

        vBoxFilter.getChildren().add(tabPane);

        Separator sp1 = new Separator();
        sp1.getStyleClass().add("pseperator3");
        sp1.setMinHeight(0);

        vBoxFilter.getChildren().addAll(liveFilmFilterText);
        vBoxFilter.getChildren().add(P2GuiTools.getVBoxGrower());
        vBoxFilter.getChildren().addAll(sp1, liveFilmFilterClearList);

        progress.progressProperty().bind(LiveFactory.progressProperty);
        progress.visibleProperty().bind(LiveFactory.progressProperty.greaterThan(-1));
        progress.setMaxWidth(Double.MAX_VALUE);
        vBoxBottom.getChildren().add(progress);
        VBox.setVgrow(progress, Priority.ALWAYS);
    }

    private void searchArd(int page) {
        new Thread(() -> {
            jsonInfoDtoArd.init();
            jsonInfoDtoArd.setPageNo(page);
            jsonInfoDtoArd.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH.getValue());
            jsonInfoDtoArd.setPageNo(siteNo.get());

            List<FilmDataMTP> list = LiveSearchArd.loadLive(jsonInfoDtoArd);
            Platform.runLater(() -> {
                list.forEach(ProgData.getInstance().liveFilmFilterWorker.getLiveFilmList()::importFilmOnlyWithNr);
            });

        }).start();
    }

    private void searchZdf(int page) {
        new Thread(() -> {
            final JsonInfoDtoZdf jsonInfoDtoZdf = new JsonInfoDtoZdf();

            jsonInfoDtoZdf.init();
            jsonInfoDtoZdf.setPageNo(0);
            jsonInfoDtoZdf.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH.getValue());

            List<FilmDataMTP> list = new LiveSearchZdf().loadLive(jsonInfoDtoZdf);
            Platform.runLater(() -> {
                list.forEach(ProgData.getInstance().liveFilmFilterWorker.getLiveFilmList()::importFilmOnlyWithNr);
            });
        }).start();
    }


}
