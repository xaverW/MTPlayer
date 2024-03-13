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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.livesearch.JsonInfoDto;
import de.p2tools.mtplayer.controller.livesearch.LiveSearch;
import de.p2tools.mtplayer.gui.filter.helper.PCboStringSearch;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
    private final JsonInfoDto jsonInfoDto = new JsonInfoDto();
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
        Button btnSearch = new Button();
        btnSearch.setGraphic(ProgIcons.ICON_BUTTON_SEARCH_16.getImageView());
        btnSearch.setTooltip(new Tooltip("Suche starten"));
        btnSearch.setOnAction(a -> {
            siteNo.set(0);
            search(0);
        });
        btnSearch.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH.length().lessThan(5))
                .or(jsonInfoDto.getProgressProperty().isNotEqualTo(JsonInfoDto.PROGRESS_NULL)));

        Button btnKeepOn = new Button();
        btnKeepOn.setGraphic(ProgIcons.ICON_BUTTON_FORWARD.getImageView());
        btnKeepOn.setTooltip(new Tooltip("Weitersuchen"));
        btnKeepOn.setOnAction(a -> {
            long res = jsonInfoDto.getSizeOverAll().get() - (long) siteNo.get() * JsonInfoDto.PAGE_SIZE - JsonInfoDto.PAGE_SIZE;
            if (res > 0) {
                siteNo.setValue(siteNo.get() + 1);
                search(siteNo.get());
            }
        });
        btnKeepOn.disableProperty().bind((jsonInfoDto.getSizeOverAll().lessThanOrEqualTo(siteNo.get() * JsonInfoDto.PAGE_SIZE))
                .or(jsonInfoDto.getProgressProperty().isNotEqualTo(JsonInfoDto.PROGRESS_NULL)));

        Button btnClear = new Button();
        btnClear.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnClear.setTooltip(new Tooltip("Suche löschen"));
        btnClear.setOnAction(a -> {
            ProgConfig.LIVE_FILM_GUI_SEARCH.set("");
        });


        HBox hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(cboSearch, btnClear, btnSearch);
        HBox.setHgrow(cboSearch, Priority.ALWAYS);
        VBox vBox = new VBox();
        vBox.setSpacing(2);
        vBox.getChildren().addAll(new Label("Livesuche"), hBox);
        vBoxFilter.getChildren().add(vBox);

        hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(btnKeepOn);
        vBoxFilter.getChildren().add(hBox);

        Separator sp1 = new Separator();
        sp1.getStyleClass().add("pseperator3");
        sp1.setMinHeight(0);

        Separator sp2 = new Separator();
        sp2.getStyleClass().add("pseperator1");
        sp2.setMinHeight(0);

        vBoxFilter.getChildren().addAll(sp1, liveFilmFilterText);
        vBoxFilter.getChildren().add(P2GuiTools.getVBoxGrower());
        vBoxFilter.getChildren().addAll(sp2, liveFilmFilterClearList);

        progress.progressProperty().bind(jsonInfoDto.getProgressProperty());
        progress.visibleProperty().bind(jsonInfoDto.getProgressProperty().greaterThan(-1));
        vBoxBottom.getChildren().add(progress);

    }

    private void search(int page) {
        new Thread(() -> {
            jsonInfoDto.init();
            jsonInfoDto.setPageNo(page);
            jsonInfoDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH.getValue());
            jsonInfoDto.setPageNo(siteNo.get());

            List<FilmDataMTP> list = LiveSearch.loadAudioFromWeb(jsonInfoDto);
            Platform.runLater(() -> {
                list.forEach(ProgData.getInstance().liveFilmFilterWorker.getLiveFilmList()::importFilmOnlyWithNr);
            });

        }).start();
    }
}
