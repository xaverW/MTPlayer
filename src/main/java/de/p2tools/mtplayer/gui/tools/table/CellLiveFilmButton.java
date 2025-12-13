/*
 * P2tools Copyright (C) 2022 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.data.film.FilmSaveFactory;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class CellLiveFilmButton<S, T> extends TableCell<S, T> {

    public final Callback<TableColumn<FilmDataMTP, String>, TableCell<FilmDataMTP, String>> cellFactory
            = (final TableColumn<FilmDataMTP, String> param) -> {

        final TableCell<FilmDataMTP, String> cell = new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                final HBox hbox = new HBox();
                hbox.setSpacing(4);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final Button btnPlay;
                final Button btnSave;

                btnPlay = new Button("");
                btnPlay.getStyleClass().addAll("btnProgMenu", "btnFuncTable");
                btnPlay.setGraphic(PIconFactory.PICON.TABLE_FILM_PLAY.getFontIcon());

                btnSave = new Button("");
                btnSave.getStyleClass().addAll("btnProgMenu", "btnFuncTable");
                btnSave.setGraphic(PIconFactory.PICON.TABLE_FILM_SAVE.getFontIcon());

                if (ProgConfig.SYSTEM_SMALL_TABLE_ROW_LIVE.get()) {
                    btnPlay.setMaxHeight(18);
                    btnPlay.setMinHeight(18);
                    btnSave.setMaxHeight(18);
                    btnSave.setMinHeight(18);
                }

                btnPlay.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    FilmDataMTP film = getTableView().getItems().get(getIndex());
                    FilmPlayFactory.playFilm(false, film);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });
                btnSave.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    FilmDataMTP film = getTableView().getItems().get(getIndex());
                    FilmSaveFactory.saveFilm(false, film);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });
                hbox.getChildren().addAll(btnPlay, btnSave);
                setGraphic(hbox);
            }
        };
        return cell;
    };
}