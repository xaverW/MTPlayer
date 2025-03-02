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
import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmToolsFactory;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtplayer.gui.infoPane.InfoPaneFactory;
import de.p2tools.mtplayer.gui.infoPane.PaneFilmButton;
import de.p2tools.mtplayer.gui.infoPane.PaneFilmInfo;
import de.p2tools.mtplayer.gui.infoPane.PaneMedia;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableFilm;
import de.p2tools.mtplayer.gui.tools.table.TableRowFilm;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneController;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneDto;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneFactory;
import de.p2tools.p2lib.tools.P2ToolsFactory;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Optional;

public class FilmGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPaneTableFilm = new ScrollPane();

    public final TableFilm tableView;
    private final ProgData progData;
    private final SortedList<FilmDataMTP> sortedList;
    private final KeyCombination STRG_A = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY);
    private boolean setShown = false;

    private final PaneFilmInfo paneFilmInfo;
    private final PaneFilmButton paneButton;
    private final PaneMedia paneMedia;
    private final P2ClosePaneController closePaneController;
    private final BooleanProperty boundInfo = new SimpleBooleanProperty(false);

    public FilmGuiController() {
        progData = ProgData.getInstance();
        sortedList = progData.filmListFiltered.getSortedList();
        tableView = new TableFilm(Table.TABLE_ENUM.FILM, progData);

        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        splitPane.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(splitPane);

        scrollPaneTableFilm.setFitToHeight(true);
        scrollPaneTableFilm.setFitToWidth(true);
        scrollPaneTableFilm.setContent(tableView);

        paneFilmInfo = new PaneFilmInfo(ProgConfig.FILM_PANE_INFO_DIVIDER);
        paneButton = new PaneFilmButton(false);

        MediaDataDto mDtoMedia = new MediaDataDto();
        MediaDataDto mDtoAbo = new MediaDataDto();
        initDto(mDtoMedia, mDtoAbo);
        paneMedia = new PaneMedia(mDtoMedia, mDtoAbo);


        ArrayList<P2ClosePaneDto> list = new ArrayList<>();
        P2ClosePaneDto infoDto = new P2ClosePaneDto(paneFilmInfo,
                ProgConfig.FILM__INFO_PANE_IS_RIP,
                ProgConfig.FILM__INFO_DIALOG_SIZE, ProgData.FILM_TAB_ON,
                "Beschreibung", "Beschreibung", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);

        infoDto = new P2ClosePaneDto(paneButton,
                ProgConfig.FILM__BUTTON_PANE_IS_RIP,
                ProgConfig.FILM__BUTTON_DIALOG_SIZE, ProgData.FILM_TAB_ON,
                "Buttons", "Buttons", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);

        infoDto = new P2ClosePaneDto(paneMedia,
                ProgConfig.FILM__MEDIA_PANE_IS_RIP,
                ProgConfig.FILM__MEDIA_DIALOG_SIZE, ProgData.FILM_TAB_ON,
                "Mediensammlung", "Mediensammlung", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);

        closePaneController = new P2ClosePaneController(list, ProgConfig.FILM__INFO_IS_SHOWING);

        ProgConfig.FILM__INFO_IS_SHOWING.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.FILM__INFO_PANE_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.FILM__BUTTON_PANE_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.FILM__MEDIA_PANE_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());

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

    public void copyFilmThemeTitle(boolean theme) {
        final Optional<FilmDataMTP> filmSelection = getSel(false, false);
        filmSelection.ifPresent(mtp -> P2ToolsFactory.copyToClipboard(theme ? mtp.getTheme() : mtp.getTitle()));
    }

    public ArrayList<FilmDataMTP> getSelList(boolean markSel/*markieren was vor dem SEL ist*/) {
        final ArrayList<FilmDataMTP> ret = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            P2Alert.showInfoNoSelection();
        } else if (markSel) {
            setLastShown(ret.get(0));
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
        if (markSel && mtp.isPresent()) {
            setLastShown(mtp.get());
        }
        return mtp;
    }

    private void setLastShown(FilmDataMTP mtp) {
        // die Filme vor dem letzten angezeigten Film markieren
        boolean set = true;
        for (FilmDataMTP filmDataMTP : sortedList) {
            if (filmDataMTP == mtp) {
                // dann ist es der Start
                set = false;
            }
            filmDataMTP.setWasHere(set);
        }

//        System.out.println("setLastShown");
//        selPos = Double.MAX_VALUE;
//        int selected = tableView.getSelectionModel().getSelectedIndex();
//        if (selected == -1) return;
//        TableViewSkin<?> skin = (TableViewSkin<?>) tableView.getSkin();
//        skin.getChildren().stream()
//                .filter(VirtualFlow.class::isInstance)
//                .map(VirtualFlow.class::cast)
//                .findAny()
//                .ifPresent(vf -> {
//                    Cell<?> cell = vf.getCell(selected);
//                    selPos = cell.getLayoutY();
//                    System.out.println("get SelPos: " + selPos);
//                });

    }

    public void bookmarkFilm(boolean bookmark) {
        // Filmmenü
        final ArrayList<FilmDataMTP> list = getSelList(true);
        if (!list.isEmpty()) {
            FilmToolsFactory.bookmarkFilmList(list, bookmark);
        }
    }

    public void searchFilmInMediaCollection() {
        // aus dem Menü
        final Optional<FilmDataMTP> film = getSel(false, true);
        film.ifPresent(mtp -> new MediaDialogController(mtp.getTheme(), mtp.getTitle()));
    }

    public void setFilmShown(boolean set) {
        // aus dem Menü/Kontext Tabelle
        final ArrayList<FilmDataMTP> list = getSelList(true);
        if (list.isEmpty()) {
            return;
        }
        FilmToolsFactory.setFilmShown(list, set);
        setShown = true;
    }

    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.FILM);
    }

    public void selectLastShown() {
        // bei der Blacklist kommt das außer der Reihe
        if (Platform.isFxApplicationThread()) {
            selectLastShown_();
            return;
        }
        Platform.runLater(this::selectLastShown_);
    }

    private void initListener() {
        ProgConfig.FILM__INFO_IS_SHOWING.addListener((observable, oldValue, newValue) -> setInfoPane());
//        sortedList.addListener((ListChangeListener<FilmDataMTP>) c -> {
//            selectLastShown();
//        });
//        tableView.getItems().addListener((ListChangeListener<FilmDataMTP>) c -> {
//            selectLastShown();
//        });

        progData.setDataList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (progData.setDataList.getSetDataListButton().size() > 2) {
                ProgConfig.FILM__INFO_IS_SHOWING.set(true);
            }
        });
        PListener.addListener(new PListener(new int[]{PListener.EVENT_HISTORY_CHANGED},
                FilmGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                P2TableFactory.refreshTable(tableView);
            }
        });
        PListener.addListener(new PListener(PListener.EVENT_BLACKLIST_CHANGED, this.getClass().getSimpleName()) {
            @Override
            public void pingFx() {
                P2TableFactory.refreshTable(tableView);
            }
        });
    }

    private void initTable() {
        Table.setTable(tableView);
        tableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(tableView -> {
            TableRowFilm<FilmDataMTP> row = new TableRowFilm<>();

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
                ContextMenu contextMenu = new FilmTableContextMenu(progData, this, tableView).getContextMenu(film);
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

    private void selectLastShown_() {
        // nach dem Filtern/ändern der Filmliste wird ... in der Tabelle selektiert
        P2Duration.counterStart("selectLastShown");

        if (setShown) {
            // dann wurde nur Shown gesetzt/gelöscht
            setShown = false;
            tableView.refresh();
            P2TableFactory.refreshTable(tableView);
            return;
        }
        if (tableView.getItems().isEmpty()) {
            return;
        }
        if (ProgConfig.SYSTEM_FILTER_NONE_ROW.get()) {
            // dann soll nix ausgewählt werden
            tableView.refresh();
            P2TableFactory.refreshTable(tableView);
            return;
        }
        if (ProgConfig.SYSTEM_FILTER_FIRST_ROW.getValue()) {
            // dann immer die erste Zeile
            tableView.getSelectionModel().clearAndSelect(0);
            tableView.scrollTo(0);
            tableView.refresh();
            P2TableFactory.refreshTable(tableView);
            return;
        }

        FilmDataMTP filmDataMTP = tableView.getSelectionModel().getSelectedItem();
        if (filmDataMTP != null) {
            // dann ist schon was selektiert, passt.
            tableView.scrollTo(filmDataMTP);

        } else {
            for (int i = sortedList.size() - 1; i >= 0; --i) {
                // von hinten den ersten Treffer suchen
                FilmDataMTP film = sortedList.get(i);
                if (film.isWasHere()) {
                    //dann haben wir den ersten
                    tableView.getSelectionModel().select(film);
                    tableView.scrollTo(film);
                    break;
                }
            }
        }
        setCenter();

        P2Duration.counterStop("selectLastShown");
    }

    private void setCenter() {
        // und dann zu selected scrollen
        int selected = tableView.getSelectionModel().getSelectedIndex();
        if (selected == -1) {
            tableView.refresh();
            P2TableFactory.refreshTable(tableView);
            return;
        }

        Platform.runLater(() -> {
            TableViewSkin<?> skin = (TableViewSkin<?>) tableView.getSkin();
            skin.getChildren().stream()
                    .filter(VirtualFlow.class::isInstance)
                    .map(VirtualFlow.class::cast)
                    .findAny()
                    .ifPresent(vf -> {

//                        vf.scrollTo(selected);
//                        vf.layout();
//                        System.out.println("sel selPos: " + selPos);
//                        if (selPos != Double.MAX_VALUE) {
//                            // letzte Pos
//                            double to = (-1 * selPos / 2);
//                            System.out.println("to: " + to);
//                            System.out.println("yy: " + -vf.getHeight() / 2); // Mitte
//                            vf.scrollPixels(to);
//                            vf.scrollPixels(to); // brauchts 2x??
//
//                        } else {
//                            // dann in die Mitte
//                            System.out.println("dann Mitte");
//                            vf.scrollPixels(-vf.getHeight() / 2);
//                        }

                        vf.scrollToTop(selected);
                        vf.layout();
                        vf.scrollPixels(-vf.getHeight() / 2);
                        tableView.refresh();
                        P2TableFactory.refreshTable(tableView);
                    });
        });
    }

    private void setFilmInfos(FilmDataMTP film) {
        // Film in FilmInfoDialog setzen
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.FILM, paneFilmInfo)) {
            paneFilmInfo.setFilm(film);
        }
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.FILM, paneMedia)) {
            paneMedia.setSearchPredicate(film);
        }
        FilmInfoDialogController.getInstance().setFilm(film);
    }

    private void setInfoPane() {
        P2ClosePaneFactory.setSplit(boundInfo, splitPane,
                closePaneController, false, scrollPaneTableFilm,
                ProgConfig.FILM__INFO_DIVIDER, ProgConfig.FILM__INFO_IS_SHOWING);
    }

    private void initDto(MediaDataDto mediaDataDtoMedia, MediaDataDto mediaDataDtoAbo) {
        mediaDataDtoMedia.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_MEDIA;
        mediaDataDtoMedia.buildSearchFrom = ProgConfig.INFO_FILM_BUILD_SEARCH_FROM_FOR_MEDIA;
        mediaDataDtoMedia.searchInWhat = ProgConfig.INFO_FILM_SEARCH_IN_WHAT_FOR_MEDIA;
        mediaDataDtoMedia.cleaning = ProgConfig.INFO_FILM_CLEAN_MEDIA;
        mediaDataDtoMedia.cleaningExact = ProgConfig.INFO_FILM_CLEAN_EXACT_MEDIA;
        mediaDataDtoMedia.cleaningAndOr = ProgConfig.INFO_FILM_CLEAN_AND_OR_MEDIA;
        mediaDataDtoMedia.cleaningList = ProgConfig.INFO_FILM_CLEAN_LIST_MEDIA;

        mediaDataDtoAbo.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_ABO;
        mediaDataDtoAbo.buildSearchFrom = ProgConfig.INFO_FILM_BUILD_SEARCH_FROM_FOR_ABO;
        mediaDataDtoAbo.searchInWhat = ProgConfig.INFO_FILM_SEARCH_IN_WHAT_FOR_ABO;
        mediaDataDtoAbo.cleaning = ProgConfig.INFO_FILM_CLEAN_ABO;
        mediaDataDtoAbo.cleaningExact = ProgConfig.INFO_FILM_CLEAN_EXACT_ABO;
        mediaDataDtoAbo.cleaningAndOr = ProgConfig.INFO_FILM_CLEAN_AND_OR_ABO;
        mediaDataDtoAbo.cleaningList = ProgConfig.INFO_FILM_CLEAN_LIST_ABO;
    }
}
