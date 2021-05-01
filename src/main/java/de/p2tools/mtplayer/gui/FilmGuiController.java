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
import de.p2tools.mtplayer.controller.data.film.Film;
import de.p2tools.mtplayer.controller.data.film.FilmTools;
import de.p2tools.mtplayer.gui.mediaDialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableRowFilm;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.guiTools.PColor;
import de.p2tools.p2Lib.guiTools.PTableFactory;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

import java.util.ArrayList;
import java.util.Optional;

public class FilmGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPaneTableFilm = new ScrollPane();

    private final TabPane infoTab = new TabPane();
    private final TilePane tilePaneButton = new TilePane();
    //    private final AnchorPane filmInfoPane = new AnchorPane();
    private FilmGuiInfoController filmGuiInfoController;
    private final TableView<Film> tableView = new TableView<>();

    private final ProgData progData;
    private boolean bound = false;
    private final SortedList<Film> sortedList;
    private final KeyCombination STRG_A = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY);

    DoubleProperty splitPaneProperty = ProgConfig.FILM_GUI_DIVIDER.getDoubleProperty();
    BooleanProperty boolInfoOn = ProgConfig.FILM_GUI_DIVIDER_ON.getBooleanProperty();

    public FilmGuiController() {
        progData = ProgData.getInstance();
        sortedList = progData.filmlistFiltered.getSortedList();
        sortedList.addListener((ListChangeListener<Film>) c -> {
            selectFilm();
        });

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
//        System.out.println("FilmGuiIsShown");
        setFilm();
        tableView.requestFocus();
        progData.filmFilterControllerClearFilter.setClearText("Filter _löschen");
        progData.downloadFilterController.setClearText("Filter löschen");
        progData.aboFilterController.setClearText("Filter löschen");
    }

    public int getFilmCount() {
        return tableView.getItems().size();
    }

    public int getSelCount() {
        return tableView.getSelectionModel().getSelectedItems().size();
    }


    private void setFilm() {
        Film film = tableView.getSelectionModel().getSelectedItem();
        filmGuiInfoController.setFilm(film);
        progData.filmInfoDialogController.setFilm(film);
    }

    private void selectFilm() {
        Platform.runLater(() -> {
            if ((tableView.getItems().size() == 0)) {
                return;
            }
            Film selFilm = tableView.getSelectionModel().getSelectedItem();
            if (selFilm != null) {
                tableView.scrollTo(selFilm);
            } else {
                tableView.getSelectionModel().clearSelection();
                tableView.scrollTo(0);
                tableView.getSelectionModel().select(0);
            }
        });
    }

    public void showFilmInfo() {
        progData.filmInfoDialogController.showFilmInfo();
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

    public void bookmarkFilm(boolean bookmark) {
        final ArrayList<Film> list = getSelList();
        if (!list.isEmpty()) {
            FilmTools.bookmarkFilm(progData, list, bookmark);
        }
    }

    public void guiFilmMediaCollection() {
        final Optional<Film> film = getSel();
        if (film.isPresent()) {
            new MediaDialogController(film.get().getTitle());
        }
    }

    public void setFilmShown() {
        final ArrayList<Film> list = getSelList();
        FilmTools.setFilmShown(progData, list, true);
        Table.refresh_table(tableView);
    }

    public void setFilmNotShown() {
        final ArrayList<Film> list = getSelList();
        FilmTools.setFilmShown(progData, list, false);
        Table.refresh_table(tableView);
    }

    public void saveTable() {
        new Table().saveTable(tableView, Table.TABLE.FILM);
    }

    public void refreshTable() {
        Table.refresh_table(tableView);
    }

    public ArrayList<Film> getSelList() {
        final ArrayList<Film> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    public Optional<Film> getSel() {
        return getSel(true);
    }

    public Optional<Film> getSel(boolean show) {
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
        progData.setDataList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (progData.setDataList.getSetDataListButton().size() > 2) {
                boolInfoOn.set(true);
            }
            setInfoPane();
        });
        Listener.addListener(new Listener(new int[]{Listener.EREIGNIS_GUI_HISTORY_CHANGED},
                FilmGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                Table.refresh_table(tableView);
            }
        });
    }

    private void initInfoPane() {
        filmGuiInfoController = new FilmGuiInfoController();
        boolInfoOn.addListener((observable, oldValue, newValue) -> setInfoPane());

        tilePaneButton.setVgap(15);
        tilePaneButton.setHgap(15);
        tilePaneButton.setPadding(new Insets(10));
        tilePaneButton.setStyle("-fx-border-color: -fx-text-box-border; " +
                "-fx-border-radius: 5px; " +
                "-fx-border-width: 1;");
    }

    private void setInfoPane() {
//        infoPane.setVisible(boolInfoOn.getValue());
//        infoPane.setManaged(boolInfoOn.getValue());

        if (boolInfoOn.getValue()) {
            bound = true;
            setInfoTabPane();
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(splitPaneProperty);

        } else {
            if (bound) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(splitPaneProperty);
            }

            if (splitPane.getItems().size() != 1) {
                splitPane.getItems().clear();
                splitPane.getItems().add(scrollPaneTableFilm);
            }
        }
    }

    private void setInfoTabPane() {
        final SetDataList setDataList = progData.setDataList.getSetDataListButton();

        if (setDataList.isEmpty()) {
            // dann brauchen wir den Tab mit den Button nicht
            if (splitPane.getItems().size() != 2 || splitPane.getItems().get(1) != filmGuiInfoController) {
                splitPane.getItems().clear();
                splitPane.getItems().addAll(scrollPaneTableFilm, filmGuiInfoController);
                SplitPane.setResizableWithParent(filmGuiInfoController, false);
            }
            return;
        }

        // Button wieder aufbauen
        tilePaneButton.getChildren().clear();
        setDataList.stream().forEach(setData -> {
            Button btn = new Button(setData.getVisibleName());
            btn.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
            btn.setMaxWidth(Double.MAX_VALUE);
            if (!setData.getColor().equals(SetData.RESET_COLOR)) {
                final String c = PColor.getCssColor(setData.getColor());
                final String css = "-fx-border-color: #" + c + "; " +
                        "-fx-border-radius: 3px; " +
                        "-fx-border-width: 2; ";

                btn.setStyle(css);
            }

            btn.setOnAction(a -> playFilmUrlWithSet(setData));
            tilePaneButton.getChildren().add(btn);
        });

        if (splitPane.getItems().size() != 2 || splitPane.getItems().get(1) != infoTab) {
            splitPane.getItems().clear();
            splitPane.getItems().addAll(scrollPaneTableFilm, infoTab);
            SplitPane.setResizableWithParent(infoTab, false);

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPadding(new Insets(10));
            scrollPane.setContent(tilePaneButton);

            Tab filmInfoTab = new Tab("Beschreibung");
            filmInfoTab.setClosable(false);
            filmInfoTab.setContent(filmGuiInfoController);

            Tab setTab = new Tab("Startbutton");
            setTab.setClosable(false);
            setTab.setContent(scrollPane);

            infoTab.getTabs().clear();
            infoTab.getTabs().addAll(filmInfoTab, setTab);
        }
    }

    private void initTable() {
        tableView.setTableMenuButtonVisible(true);
        tableView.setEditable(false);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        new Table().setTable(tableView, Table.TABLE.FILM);
        tableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(tv -> {
            TableRowFilm<Film> row = new TableRowFilm<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    progData.filmInfoDialogController.showFilmInfo();
                }
            });
            return row;
        });

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<Film> optionalFilm = getSel(false);
                Film film;
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
                Platform.runLater(this::setFilm));
    }

    private synchronized void startFilmUrl() {
        final Optional<Film> filmSelection = getSel();
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

        final Optional<Film> filmSelection = getSel();
        if (!filmSelection.isPresent()) {
            return;
        }

        FilmTools.playFilm(filmSelection.get(), pSet);
    }

    private synchronized void saveFilm() {
        saveFilm(null);
    }

    private synchronized void saveFilm(SetData pSet) {
        final ArrayList<Film> list = getSelList();
        progData.filmlist.saveFilm(list, pSet);
    }
}
