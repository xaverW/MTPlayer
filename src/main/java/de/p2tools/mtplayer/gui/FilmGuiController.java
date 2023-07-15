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
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmTools;
import de.p2tools.mtplayer.controller.film.FilmlistMTP;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtplayer.gui.infoPane.FilmInfoController;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableFilm;
import de.p2tools.mtplayer.gui.tools.table.TableRowFilm;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.PTableFactory;
import de.p2tools.p2lib.tools.PSystemUtils;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Optional;

public class FilmGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPaneTableFilm = new ScrollPane();
    private final FilmInfoController filmInfoController;

    public final TableFilm tableView;
    private final ProgData progData;
    private final SortedList<FilmDataMTP> sortedList;
    private final KeyCombination STRG_A = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY);
    DoubleProperty splitPaneProperty = ProgConfig.FILM_GUI_DIVIDER;
    private boolean boundSplitPaneDivPos = false;

    public FilmGuiController() {
        progData = ProgData.getInstance();
        sortedList = progData.filmlistFiltered.getSortedList();
        filmInfoController = new FilmInfoController();
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
        final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().filmGuiController.getSel(false, false);
        filmSelection.ifPresent(mtp -> PSystemUtils.copyToClipboard(theme ? mtp.getTheme() : mtp.getTitle()));
    }

    public void playFilmUrl() {
        // Menü/Button Film (URL) abspielen
        startFilmUrl();
    }

    public void playFilmUrlWithSet(SetData psetData) {
        // Button/Menü: Film mit Set starten
        startFilmUrlWithSet(psetData);
    }

    public void saveTheFilm() {
        saveFilm();
    }

    public void bookmarkFilm(boolean bookmark) {
        final ArrayList<FilmDataMTP> list = getSelList();
        if (!list.isEmpty()) {
            FilmTools.bookmarkFilm(progData, list, bookmark);
        }
    }

    public void searchFilmInMediaCollection() {
        // aus dem Menü
        final Optional<FilmDataMTP> film = getSel(false, true);
        film.ifPresent(mtp -> new MediaDialogController(mtp.getTheme(), mtp.getTitle()));
    }

    public void setFilmShown() {
        // aus dem Menü/Kontext Tabelle
        final ArrayList<FilmDataMTP> list = getSelList();
        if (list.isEmpty()) {
            return;
        }
        getSel(true, false); // damit sel gesetzt wird
        FilmTools.setFilmShown(progData, list, true);
        selectShown();
    }

    public void setFilmNotShown() {
        final ArrayList<FilmDataMTP> list = getSelList();
        FilmTools.setFilmShown(progData, list, false);
        PTableFactory.refreshTable(tableView);
    }

    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.FILM);
    }

    public ArrayList<FilmDataMTP> getSelList() {
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

        if (markSel && mtp.isPresent()) {
            setWasShown(mtp.get());
        }
        return mtp;
    }

    private void initListener() {
        ProgConfig.FILM_GUI_DIVIDER_ON.addListener((observable, oldValue, newValue) -> setInfoPane());
        sortedList.addListener((ListChangeListener<FilmDataMTP>) c -> {
            selectShown();
        });
        progData.setDataList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (progData.setDataList.getSetDataListButton().size() > 2) {
                ProgConfig.FILM_GUI_DIVIDER_ON.set(true);
            }
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
                PTableFactory.refreshTable(tableView);
            }
        });
    }

    public void remove() {
        FilmlistMTP list = new FilmlistMTP();
        tableView.setItems(list.getSortedList());
    }

    public void set() {
        tableView.setItems(sortedList);
    }

    private void initTable() {
        Table.setTable(tableView);
        tableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(tableView -> {
            TableRowFilm<FilmDataMTP> row = new TableRowFilm<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    FilmInfoDialogController.getInstanceAndShow().showFilmInfo();
                }
            });
            row.hoverProperty().addListener((observable) -> {
                final FilmDataMTP filmDataMTP = (FilmDataMTP) row.getItem();
                if (row.isHover() && filmDataMTP != null) {
                    filmInfoController.setFilmInfos(filmDataMTP);
                    FilmInfoDialogController.getInstance().setFilm(filmDataMTP);
                } else {
                    setFilmInfos();
                }
            });
            return row;
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                //wird auch durch FilmlistenUpdate ausgelöst
                Platform.runLater(this::setFilmInfos));

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<FilmDataMTP> optionalFilm = getSel(true, false); // ist für Blacklist wichtig
                FilmDataMTP film;
                film = optionalFilm.orElse(null);
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
                //macht eigentlich keinen Sinn???
                PLog.sysLog("STRG-A: lange Liste -> verhindern");
                event.consume();
            }
        });
    }

    private synchronized void startFilmUrl() {
        // Menü/Button Film (URL) abspielen
        final Optional<FilmDataMTP> filmSelection = getSel(true, true);
        filmSelection.ifPresent(this::startFilmUrl);
    }

    public synchronized void startFilmUrl(FilmDataMTP mtp) {
        // aus Menü
        if (mtp != null) {
            tableView.getSelectionModel().select(mtp);
            setWasShown(mtp);
            FilmTools.playFilm(mtp, null);
        }
    }

    private void setWasShown(FilmDataMTP mtp) {
        boolean set = true;
        for (int i = 0; i < tableView.getItems().size(); ++i) {
            FilmDataMTP f = tableView.getItems().get(i);
            if (f == mtp) {
                // dann ist der Start
                set = false;
            }
            f.setWasHere(set);
        }
    }

    private void selectShown() {
        Platform.runLater(() -> {
            // bei der Blacklist kommt das außer der Reihe
            if (tableView.getItems().isEmpty()) {
                return;
            }

            if (tableView.getSelectionModel().getSelectedItem() != null) {
                // dann ist schon was selektiert, passt.
                tableView.scrollTo(tableView.getSelectionModel().getSelectedItem());
                return;
            }

            boolean found = false;
            for (int i = tableView.getItems().size() - 1; i >= 0; --i) {
                FilmDataMTP f = tableView.getItems().get(i);
                if (f.isWasHere()) {
                    //dann haben wir den ersten
                    tableView.getSelectionModel().select(f);
                    tableView.scrollTo(f);
                    found = true;
                    break;
                }
            }

            if (!found) {
                // dann wurde der erste Tabelleneintrag entfernt
                tableView.getSelectionModel().select(0);
                tableView.scrollTo(0);
            }
        });
    }

    private void startFilmUrlWithSet(SetData pSet) {
        // Button/Menü: Film mit Set starten
        // Url mit Prognr. starten
        if (pSet.isSave()) {
            // wenn das pSet zum Speichern (über die Button) gewählt wurde,
            // weiter mit dem Dialog "Speichern"
            saveFilm(pSet);
            return;
        }

        final Optional<FilmDataMTP> filmSelection = getSel(true, true);
        if (filmSelection.isEmpty()) {
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

    private void setFilmInfos() {
        // Film in FilmInfoDialog setzen
        FilmDataMTP film = tableView.getSelectionModel().getSelectedItem();
        filmInfoController.setFilmInfos(film);
        FilmInfoDialogController.getInstance().setFilm(film);
    }

    private void setInfoPane() {
        // hier wird das InfoPane ein- ausgeblendet
        if (ProgConfig.FILM_GUI_DIVIDER_ON.getValue()) {
            boundSplitPaneDivPos = true;
            if (splitPane.getItems().size() != 2) {
                //erst mal splitPane einrichten, dass Tabelle und Infos angezeigt werden
                splitPane.getItems().clear();
                splitPane.getItems().addAll(scrollPaneTableFilm, filmInfoController);
                SplitPane.setResizableWithParent(filmInfoController, false);
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
