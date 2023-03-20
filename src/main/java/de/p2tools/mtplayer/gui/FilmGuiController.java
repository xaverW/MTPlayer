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
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.controller.data.SetDataList;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmTools;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableFilm;
import de.p2tools.mtplayer.gui.tools.table.TableRowFilm;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.PTableFactory;
import de.p2tools.p2lib.guitools.pclosepane.PClosePaneH;
import de.p2tools.p2lib.tools.PSystemUtils;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Optional;

public class FilmGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPaneTableFilm = new ScrollPane();
    private final PClosePaneH pClosePaneH;
    private FilmDataMTP lastShownFilmData = null;

    private FilmGuiInfoController filmGuiInfoController;
    private final TableFilm tableView;

    private final ProgData progData;
    private boolean boundSplitPaneDivPos = false;
    private final SortedList<FilmDataMTP> sortedList;
    private final KeyCombination STRG_A = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY);

    DoubleProperty splitPaneProperty = ProgConfig.FILM_GUI_DIVIDER;
    BooleanProperty boolInfoOn = ProgConfig.FILM_GUI_DIVIDER_ON;

    public FilmGuiController() {
        progData = ProgData.getInstance();
        sortedList = progData.filmlistFiltered.getSortedList();
        pClosePaneH = new PClosePaneH(ProgConfig.FILM_GUI_DIVIDER_ON, true);
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

        initInfoPane();
        setInfoPane();
        initTable();
        initListener();
    }

    public void isShown() {
        setFilmInfos();
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
        final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().filmGuiController.getSel();
        if (filmSelection.isPresent()) {
            PSystemUtils.copyToClipboard(theme ? filmSelection.get().getTheme() : filmSelection.get().getTitle());
        }
    }

    public void playFilmUrl() {
        // Menü/Button Film (URL) abspielen
        startFilmUrl();
    }

    public void playFilmUrlWithSet(SetData psetData) {
        startFilmUrlWithSet(psetData);
    }

    public void saveTheFilm() {
        saveFilm();
    }


//    public void setLastShownFilm(BlackData blackData) {
//        //todo-> Black-White-List???
//        final Optional<FilmDataMTP> filmSelection = getSel();
//        if (!filmSelection.isPresent()) {
//            //nix ausgewählt
//            return;
//        }
//
//        lastShownFilmData = null;
//        if (ProgData.getInstance().actFilmFilterWorker.getActFilterSettings().blacklistOnOffProperty().getValue() ==
//                BlacklistFilterFactory.BLACKLILST_FILTER_OFF) {
//            //dann ist der markierte Film noch zu sehen
//            lastShownFilmData = filmSelection.get();
//
//        } else {
//            //sonst wird der erste Film davor, der noch zu sehen ist, gesucht
//            int sel = tableView.getSelectionModel().getSelectedIndex();
//            for (int i = sel; i >= 0; --i) {
//                FilmDataMTP filmDataMTP = tableView.getItems().get(i);
//                if (!BlacklistFilterFactory.checkFilmIsBlocked(filmDataMTP, blackData, false)) {
//                    lastShownFilmData = filmDataMTP;
//                    break;
//                }
//            }
//        }
//    }

    public void bookmarkFilm(boolean bookmark) {
        final ArrayList<FilmDataMTP> list = getSelList();
        if (!list.isEmpty()) {
            FilmTools.bookmarkFilm(progData, list, bookmark);
        }
    }

    public void guiFilmMediaCollection() {
        final Optional<FilmDataMTP> film = getSel();
        if (film.isPresent()) {
            new MediaDialogController(film.get().getTitle());
        }
    }

    public void setFilmShown() {
        final ArrayList<FilmDataMTP> list = getSelList();
        FilmTools.setFilmShown(progData, list, true);
        PTableFactory.refreshTable(tableView);
    }

    public void setFilmNotShown() {
        final ArrayList<FilmDataMTP> list = getSelList();
        FilmTools.setFilmShown(progData, list, false);
        PTableFactory.refreshTable(tableView);
    }

    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.FILM);
    }

    public void refreshTable() {
        PTableFactory.refreshTable(tableView);
    }

    public ArrayList<FilmDataMTP> getSelList() {
        final ArrayList<FilmDataMTP> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    public Optional<FilmDataMTP> getSel() {
        return getSel(true);
    }

    public Optional<FilmDataMTP> getSel(boolean show) {
        final int selectedTableRow = tableView.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            return Optional.of(tableView.getSelectionModel().getSelectedItem());
        } else {
            if (show) {
                PAlert.showInfoNoSelection();
            }
            return Optional.empty();
        }
    }

    private void initListener() {
        sortedList.addListener((ListChangeListener<FilmDataMTP>) c -> {
//            selectFilm();
            FilmDataMTP selFilm = tableView.getSelectionModel().getSelectedItem();
            if (selFilm != null) {
                tableView.scrollTo(selFilm);
            }
        });
        progData.setDataList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (progData.setDataList.getSetDataListButton().size() > 2) {
                boolInfoOn.set(true);
            }
            setInfoPane();
        });
        Listener.addListener(new Listener(new int[]{Listener.EVENT_HISTORY_CHANGED},
                FilmGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                PTableFactory.refreshTable(tableView);
            }
        });
        Listener.addListener(new Listener(Listener.EVENT_BLACKLIST_CHANGED, this.getClass().getSimpleName()) {
            @Override
            public void pingFx() {
                lastShownFilmData = null;
                PTableFactory.refreshTable(tableView);
            }

        });
    }

//    private void selectFilm() {
//        Platform.runLater(() -> {
//            if ((tableView.getItems().size() == 0)) {
//                return;
//            }
//
//            System.out.println("=========> select");
//            if (lastShownFilmData != null) {
//                tableView.getSelectionModel().clearSelection();
//                tableView.getSelectionModel().select(lastShownFilmData);
//                FilmDataMTP selFilm = tableView.getSelectionModel().getSelectedItem();
//                if (selFilm != null) {
//                    tableView.scrollTo(selFilm);
//                }
//
//
////                int i = tableView.getSelectionModel().getSelectedIndex();
////                tableView.getSelectionModel().select(i);
////                tableView.getSelectionModel().focus(i);
////                tableView.getSelectionModel().clearAndSelect(i);
////                tableView.scrollTo(tableView.getItems().size() - 1);
////                tableView.scrollTo(i);
//
//            } else {
//                FilmDataMTP selFilm = tableView.getSelectionModel().getSelectedItem();
//                if (selFilm != null) {
//                    tableView.scrollTo(selFilm);
////                } else {
////                    tableView.getSelectionModel().clearSelection();
////                    tableView.getSelectionModel().select(0);
////                    tableView.scrollTo(0);
//                }
//            }
//        });
//    }

    private void initTable() {
        Table.setTable(tableView);

        tableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(tv -> {
            TableRowFilm<FilmDataMTP> row = new TableRowFilm<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    FilmInfoDialogController.getInstanceAndShow().showFilmInfo();
                }
            });
            return row;
        });

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<FilmDataMTP> optionalFilm = getSel(false);
                FilmDataMTP film;
                if (optionalFilm.isPresent()) {
                    film = optionalFilm.get();
                } else {
                    film = null;
                }
                ContextMenu contextMenu = new FilmGuiTableContextMenu(progData, this, tableView).getContextMenu(film);
                tableView.setContextMenu(contextMenu);
            }
        });

        tableView.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (PTableFactory.SPACE.match(event)) {
                PTableFactory.scrollVisibleRangeDown(tableView);
                event.consume();
            }
            if (PTableFactory.SPACE_SHIFT.match(event)) {
                PTableFactory.scrollVisibleRangeUp(tableView);
                event.consume();
            }

            if (STRG_A.match(event) && tableView.getItems().size() > 3_000) {
                //macht eingentlich keine Sinn???
                PLog.sysLog("STRG-A: lange Liste -> verhindern");
                event.consume();
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setFilmInfos));
    }

    private void setFilmInfos() {
        FilmDataMTP film = tableView.getSelectionModel().getSelectedItem();
        filmGuiInfoController.setFilm(film);
        FilmInfoDialogController.getInstance().setFilm(film);
    }

    private void initInfoPane() {
        filmGuiInfoController = new FilmGuiInfoController();
        boolInfoOn.addListener((observable, oldValue, newValue) -> setInfoPane());
    }

    private void setInfoPane() {
        if (boolInfoOn.getValue()) {
            boundSplitPaneDivPos = true;
            setInfoTabPane();
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

    private void setInfoTabPane() {
        if (splitPane.getItems().size() != 2) {
            //erst mal splitPane einrichten, dass Tabelle ind Info angezeigt wird
            splitPane.getItems().clear();
            splitPane.getItems().addAll(scrollPaneTableFilm, pClosePaneH);
            SplitPane.setResizableWithParent(pClosePaneH, false);
        }

        pClosePaneH.getVBoxAll().getChildren().clear();
        pClosePaneH.getVBoxAll().setMinHeight(Region.USE_PREF_SIZE);

        final SetDataList setDataList = progData.setDataList.getSetDataListButton();
        if (setDataList.isEmpty()) {
            //dann brauchen wir den Tab mit den Button nicht
            pClosePaneH.getVBoxAll().getChildren().setAll(filmGuiInfoController);
            VBox.setVgrow(filmGuiInfoController, Priority.ALWAYS);
            return;
        }

        // Button wieder aufbauen
        TilePane tilePaneButton = new FilmGuiButtonPane(this).getButtonPane(setDataList);

        Tab filmInfoTab = new Tab("Beschreibung");
        filmInfoTab.setClosable(false);
        filmInfoTab.setContent(filmGuiInfoController);

        Tab buttonTab = new Tab("Startbutton");
        buttonTab.setClosable(false);
        buttonTab.setContent(tilePaneButton);

        TabPane infoTabPane = new TabPane();
        infoTabPane.getTabs().addAll(filmInfoTab, buttonTab);

        pClosePaneH.getVBoxAll().getChildren().setAll(infoTabPane);
        VBox.setVgrow(infoTabPane, Priority.ALWAYS);
    }

    private synchronized void startFilmUrl() {
        final Optional<FilmDataMTP> filmSelection = getSel();
        if (filmSelection.isPresent()) {
            FilmTools.playFilm(filmSelection.get(), null);
        }
    }

    private void startFilmUrlWithSet(SetData pSet) {
        // Url mit Prognr. starten
        if (pSet.isSave()) {
            // wenn das pSet zum Speichern (über die Button) gewählt wurde,
            // weiter mit dem Dialog "Speichern"
            saveFilm(pSet);
            return;
        }

        final Optional<FilmDataMTP> filmSelection = getSel();
        if (!filmSelection.isPresent()) {
            return;
        }

        FilmTools.playFilm(filmSelection.get(), pSet);
    }

    private synchronized void saveFilm() {
        saveFilm(null);
    }

    private synchronized void saveFilm(SetData pSet) {
        final ArrayList<FilmDataMTP> list = getSelList();
        progData.filmlist.saveFilm(list, pSet);
    }
}
