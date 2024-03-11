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

package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtplayer.gui.infoPane.LiveFilmInfoController;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableLiveFilm;
import de.p2tools.mtplayer.gui.tools.table.TableRowLiveFilm;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Optional;

public class LiveFilmGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPaneTableFilm = new ScrollPane();
    private final LiveFilmInfoController liveFilmInfoController;

    public final TableLiveFilm tableView;
    private final ProgData progData;
    private final KeyCombination STRG_A = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY);
    DoubleProperty splitPaneProperty = ProgConfig.LIVE_FILM_GUI_DIVIDER;
    private boolean boundSplitPaneDivPos = false;
    private double selPos = -1;

    public LiveFilmGuiController() {
        progData = ProgData.getInstance();
        liveFilmInfoController = new LiveFilmInfoController();
        tableView = new TableLiveFilm(Table.TABLE_ENUM.LIVE_FILM, progData);

        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        splitPane.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(splitPane);

        scrollPaneTableFilm.setFitToHeight(true);
        scrollPaneTableFilm.setFitToWidth(true);
        scrollPaneTableFilm.setContent(tableView);

        setInfoPane();
        initTable();
        initListener();
    }

    public void isShown() {
        setFilmInfos(tableView.getSelectionModel().getSelectedItem());
        tableView.requestFocus();
    }

    public int getFilmCount() {
        return tableView.getItems().size();
    }

    public int getSelCount() {
        return tableView.getSelectionModel().getSelectedItems().size();
    }

    public void showFilmInfo() {
        FilmInfoDialogController.getInstanceAndShow().showFilmInfo();
    }

    public ArrayList<FilmDataMTP> getSelList(boolean markSel/*markieren was vor dem SEL ist*/) {
        final ArrayList<FilmDataMTP> ret = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    public Optional<FilmDataMTP> getSel(boolean markSel/*markieren was vor dem SEL ist*/, boolean show) {
        Optional<FilmDataMTP> mtp;
        final int selectedTableRow = tableView.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            mtp = Optional.of(tableView.getSelectionModel().getSelectedItem());
        } else {
            if (show) {
                PAlert.showInfoNoSelection();
            }
            mtp = Optional.empty();
        }
        return mtp;
    }

    public void searchFilmInMediaCollection() {
        // aus dem Menü
        final Optional<FilmDataMTP> film = getSel(false, true);
        film.ifPresent(mtp -> new MediaDialogController(mtp.getTheme(), mtp.getTitle()));
    }

    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.LIVE_FILM);
    }

    private void initListener() {
        ProgConfig.LIVE_FILM_GUI_DIVIDER_ON.addListener((observable, oldValue, newValue) -> setInfoPane());
        progData.setDataList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (progData.setDataList.getSetDataListButton().size() > 2) {
                ProgConfig.LIVE_FILM_GUI_DIVIDER_ON.set(true);
            }
        });
    }

    private void initTable() {
        Table.setTable(tableView);

        FilteredList<FilmDataMTP> filteredList = new FilteredList<FilmDataMTP>(progData.liveFilmList, p -> true);
        SortedList<FilmDataMTP> sortedList = new SortedList<>(filteredList);
        tableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(tableView -> {
            TableRowLiveFilm<FilmDataMTP> row = new TableRowLiveFilm<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 &&
                        !row.isEmpty()) {
                    FilmInfoDialogController.getInstanceAndShow().showFilmInfo();
                }
            });
            row.hoverProperty().addListener((observable) -> {
                final FilmDataMTP filmDataMTP = row.getItem();
                if (row.isHover() && filmDataMTP != null) { // null bei den leeren Zeilen unterhalb
                    setFilmInfos(filmDataMTP);
                } else if (filmDataMTP == null) {
                    setFilmInfos(tableView.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });
        tableView.hoverProperty().addListener((o) -> {
            if (!tableView.isHover()) {
                setFilmInfos(tableView.getSelectionModel().getSelectedItem());
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                //wird auch durch FilmlistenUpdate ausgelöst
                Platform.runLater(() -> {
                    setFilmInfos(tableView.getSelectionModel().getSelectedItem());
                }));

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<FilmDataMTP> optionalFilm = getSel(true, false); // ist für Blacklist wichtig
                FilmDataMTP film;
                film = optionalFilm.orElse(null);
                ContextMenu contextMenu = new LiveFilmTableContextMenu(progData, this, tableView).getContextMenu(film);
                tableView.setContextMenu(contextMenu);
            }
        });

        tableView.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (P2TableFactory.SPACE.match(event)) {
                P2TableFactory.scrollVisibleRangeDown(tableView);
                event.consume();
            }
            if (P2TableFactory.SPACE_SHIFT.match(event)) {
                P2TableFactory.scrollVisibleRangeUp(tableView);
                event.consume();
            }

            if (STRG_A.match(event) && tableView.getItems().size() > 3_000) {
                //macht eigentlich keinen Sinn???
                PLog.sysLog("STRG-A: lange Liste -> verhindern");
                event.consume();
            }
        });
    }

    private void setFilmInfos(FilmDataMTP film) {
        // Film in FilmInfoDialog setzen
        liveFilmInfoController.setFilmInfos(film);
        FilmInfoDialogController.getInstance().setFilm(film);
    }

    private void setInfoPane() {
        // hier wird das InfoPane ein- ausgeblendet
        if (ProgConfig.LIVE_FILM_GUI_DIVIDER_ON.getValue()) {
            boundSplitPaneDivPos = true;
            if (splitPane.getItems().size() != 2) {
                // erst mal splitPane einrichten, dass Tabelle und Infos angezeigt werden
                splitPane.getItems().clear();
                splitPane.getItems().addAll(scrollPaneTableFilm, liveFilmInfoController);
                SplitPane.setResizableWithParent(liveFilmInfoController, false);
            }
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(splitPaneProperty);

        } else {
            if (boundSplitPaneDivPos) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(splitPaneProperty);
            }
            if (splitPane.getItems().size() != 1) {
                splitPane.getItems().clear();
                splitPane.getItems().add(scrollPaneTableFilm);
            }
        }
    }
}
