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
import de.p2tools.mtplayer.controller.livesearch.LiveSearch;
import de.p2tools.p2lib.P2LibConst;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class LiveFilmFilterController extends FilterController {

    private final ProgressBar progress = new ProgressBar();
    private final DoubleProperty progressProp = new SimpleDoubleProperty(-1);
    private final VBox vBoxFilter;
    private final VBox vBoxBottom;

    public LiveFilmFilterController() {
        super(ProgConfig.FILM_GUI_FILTER_DIVIDER_ON);


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
        TextField txtSearch = new TextField("München");

        Button btnSearch = new Button();
        btnSearch.setGraphic(ProgIcons.ICON_BUTTON_SEARCH_16.getImageView());
        btnSearch.setOnAction(a -> {
            new Thread(() -> {
                List<FilmDataMTP> list = LiveSearch.loadAudioFromWeb(progressProp, txtSearch.getText());
                Platform.runLater(() -> {
                    list.forEach(ProgData.getInstance().liveFilmList::importFilmOnlyWithNr);
                });
            }).start();
        });
        btnSearch.disableProperty().bind(txtSearch.textProperty().length().lessThan(5));
        Button btnClear = new Button();
        btnClear.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnClear.setOnAction(a -> {
            txtSearch.clear();
            ProgData.getInstance().liveFilmList.clear();
        });
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(txtSearch, btnSearch, btnClear);
        HBox.setHgrow(txtSearch, Priority.ALWAYS);
        vBoxFilter.getChildren().add(hBox);

        progress.progressProperty().bind(progressProp);
        progress.visibleProperty().bind(progressProp.greaterThan(-1));
        vBoxBottom.getChildren().add(progress);
    }
}
