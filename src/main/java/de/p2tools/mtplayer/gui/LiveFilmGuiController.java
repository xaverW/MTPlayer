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

import de.p2tools.mtplayer.MTPlayerController;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtplayer.gui.infoPane.InfoPaneFactory;
import de.p2tools.mtplayer.gui.infoPane.PaneFilmButton;
import de.p2tools.mtplayer.gui.infoPane.PaneFilmInfo;
import de.p2tools.mtplayer.gui.infoPane.PaneMedia;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableLiveFilm;
import de.p2tools.mtplayer.gui.tools.table.TableRowLiveFilm;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneController;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneDto;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneFactory;
import de.p2tools.p2lib.tools.P2SystemUtils;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    public final TableLiveFilm tableView;
    private final ProgData progData;
    private final KeyCombination STRG_A = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY);

    private final PaneFilmInfo paneFilmInfo;
    private final PaneFilmButton paneButton;
    private final PaneMedia paneMedia;
    private final P2ClosePaneController infoController;
    private final BooleanProperty boundInfo = new SimpleBooleanProperty(false);

    public LiveFilmGuiController() {
        progData = ProgData.getInstance();
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

        paneFilmInfo = new PaneFilmInfo(ProgConfig.LIVE_FILM_PANE_INFO_DIVIDER);
        paneButton = new PaneFilmButton(true);

        MediaDataDto mDtoMedia = new MediaDataDto();
        MediaDataDto mDtoAbo = new MediaDataDto();
        initDto(mDtoMedia, mDtoAbo);
        paneMedia = new PaneMedia(mDtoMedia, mDtoAbo);

        ArrayList<P2ClosePaneDto> list = new ArrayList<>();
        P2ClosePaneDto infoDto = new P2ClosePaneDto(paneFilmInfo,
                ProgConfig.LIVE_FILM__INFO_PANE_IS_RIP,
                ProgConfig.LIVE_FILM__INFO_DIALOG_SIZE, ProgData.LIVE_FILM_TAB_ON,
                "Beschreibung", "Beschreibung", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);

        infoDto = new P2ClosePaneDto(paneButton,
                ProgConfig.LIVE_FILM__BUTTON_PANE_IS_RIP,
                ProgConfig.LIVE_FILM__BUTTON_DIALOG_SIZE, ProgData.LIVE_FILM_TAB_ON,
                "Buttons", "Buttons", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);

        infoDto = new P2ClosePaneDto(paneMedia,
                ProgConfig.LIVE_FILM__MEDIA_PANE_IS_RIP,
                ProgConfig.LIVE_FILM__MEDIA_DIALOG_SIZE, ProgData.LIVE_FILM_TAB_ON,
                "Mediensammlung", "Mediensammlung", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);

        infoController = new P2ClosePaneController(list, ProgConfig.LIVE_FILM__INFO_IS_SHOWING);

        ProgConfig.LIVE_FILM__INFO_IS_SHOWING.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.LIVE_FILM__INFO_PANE_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.LIVE_FILM__BUTTON_PANE_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.LIVE_FILM__MEDIA_PANE_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());

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

    public ArrayList<FilmDataMTP> getSelList(boolean markSel /*markieren was vor dem SEL ist*/) {
        final ArrayList<FilmDataMTP> ret = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            P2Alert.showInfoNoSelection();
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
                P2Alert.showInfoNoSelection();
            }
            mtp = Optional.empty();
        }
        return mtp;
    }

    public void copyFilmThemeTitle(boolean theme) {
        final Optional<FilmDataMTP> filmSelection = getSel(false, false);
        filmSelection.ifPresent(mtp -> P2SystemUtils.copyToClipboard(theme ? mtp.getTheme() : mtp.getTitle()));
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
        ProgConfig.LIVE_FILM__INFO_IS_SHOWING.addListener((observable, oldValue, newValue) -> setInfoPane());
        progData.setDataList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (progData.setDataList.getSetDataListButton().size() > 2) {
                ProgConfig.LIVE_FILM__INFO_IS_SHOWING.set(true);
            }
        });
    }

    private void initTable() {
        Table.setTable(tableView);
        tableView.setItems(progData.liveFilmFilterWorker.getSortedList());
        progData.liveFilmFilterWorker.getSortedList().comparatorProperty().bind(tableView.comparatorProperty());

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
                P2Log.sysLog("STRG-A: lange Liste -> verhindern");
                event.consume();
            }
        });
    }

    private void setFilmInfos(FilmDataMTP film) {
        // Film in FilmInfoDialog setzen
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.LIVE_FILM, paneFilmInfo)) {
            paneFilmInfo.setFilm(film);
        }
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.LIVE_FILM, paneMedia)) {
            paneMedia.setSearchPredicate(film);
        }
        FilmInfoDialogController.getInstance().setFilm(film);
    }

    private void setInfoPane() {
        P2ClosePaneFactory.setSplit(boundInfo, splitPane,
                infoController, false, scrollPaneTableFilm,
                ProgConfig.LIVE_FILM__INFO_DIVIDER, ProgConfig.LIVE_FILM__INFO_IS_SHOWING);
    }

    private void initDto(MediaDataDto mediaDataDtoMedia, MediaDataDto mediaDataDtoAbo) {
        mediaDataDtoMedia.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_MEDIA;
        mediaDataDtoMedia.buildSearchFrom = ProgConfig.INFO_LIVE_FILM_BUILD_SEARCH_FROM_FOR_MEDIA;
        mediaDataDtoMedia.searchInWhat = ProgConfig.INFO_LIVE_FILM_SEARCH_IN_WHAT_FOR_MEDIA;
        mediaDataDtoMedia.cleaning = ProgConfig.INFO_LIVE_FILM_CLEAN_MEDIA;
        mediaDataDtoMedia.cleaningExact = ProgConfig.INFO_LIVE_FILM_CLEAN_EXACT_MEDIA;
        mediaDataDtoMedia.cleaningAndOr = ProgConfig.INFO_LIVE_FILM_CLEAN_AND_OR_MEDIA;
        mediaDataDtoMedia.cleaningList = ProgConfig.INFO_LIVE_FILM_CLEAN_LIST_MEDIA;

        mediaDataDtoAbo.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_ABO;
        mediaDataDtoAbo.buildSearchFrom = ProgConfig.INFO_LIVE_FILM_BUILD_SEARCH_FROM_FOR_ABO;
        mediaDataDtoAbo.searchInWhat = ProgConfig.INFO_LIVE_FILM_SEARCH_IN_WHAT_FOR_ABO;
        mediaDataDtoAbo.cleaning = ProgConfig.INFO_LIVE_FILM_CLEAN_ABO;
        mediaDataDtoAbo.cleaningExact = ProgConfig.INFO_LIVE_FILM_CLEAN_EXACT_ABO;
        mediaDataDtoAbo.cleaningAndOr = ProgConfig.INFO_LIVE_FILM_CLEAN_AND_OR_ABO;
        mediaDataDtoAbo.cleaningList = ProgConfig.INFO_LIVE_FILM_CLEAN_LIST_ABO;
    }
}
