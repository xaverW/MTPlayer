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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.SetList;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.mediaDialog.MediaDialogController;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.gui.tools.Table;
import de.p2tools.p2Lib.tools.log.Duration;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

import java.util.ArrayList;
import java.util.Optional;

public class FilmGuiController extends AnchorPane {
    SplitPane splitPane = new SplitPane();
    ScrollPane scrollPane = new ScrollPane();
    TableView<Film> tableView = new TableView<>();

    private final AnchorPane filmInfoPane = new AnchorPane();

    private final ProgData progData;
    private FilmGuiInfoController filmGuiInfoController;

    DoubleProperty splitPaneProperty = ProgConfig.FILM_GUI_DIVIDER.getDoubleProperty();
    BooleanProperty boolInfoOn = ProgConfig.FILM_GUI_DIVIDER_ON.getBooleanProperty();
    private boolean bound = false;

    private final SortedList<Film> sortedList;

    public FilmGuiController() {
        progData = ProgData.getInstance();

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(tableView);

        setInfoTabPane();

        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        splitPane.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(splitPane);

        sortedList = progData.filmlistFiltered.getSortedList();

        filmGuiInfoController = new FilmGuiInfoController(filmInfoPane);

        boolInfoOn.addListener((observable, oldValue, newValue) -> setSplit());
        setSplit();

        initTable();
        initListener();
    }

    public void isShown() {
        setFilm();
    }

    public int getFilmCount() {
        return tableView.getItems().size();
    }

    public int getSelCount() {
        return tableView.getSelectionModel().getSelectedItems().size();
    }

    private void setSplit() {
        //     splitPane.getItems().addAll(scrollPane, filmInfoPane);
        filmInfoPane.setVisible(boolInfoOn.getValue());
        filmInfoPane.setManaged(boolInfoOn.getValue());
        if (!boolInfoOn.getValue()) {

            if (bound) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(splitPaneProperty);
            }

            splitPane.getItems().clear();
            splitPane.getItems().add(scrollPane);

        } else {
            bound = true;
            setInfoTabPane();
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(splitPaneProperty);
        }
    }

    private void setFilm() {
        Film film = tableView.getSelectionModel().getSelectedItem();
        filmGuiInfoController.setFilm(film);
        progData.filmInfoDialogController.set(film);
        return;
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
        Duration.counterStart("filmUngesehen");
        //todo-> ~1s Dauer

        final ArrayList<Film> list = getSelList();
        FilmTools.setFilmShown(progData, list, false);

        Table.refresh_table(tableView);
        Duration.counterStop("filmUngesehen");
    }


    public void saveTable() {
        new Table().saveTable(tableView, Table.TABLE.FILM);
    }

    public ArrayList<Film> getSelList() {
        final ArrayList<Film> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            new MTAlert().showInfoNoSelection();
        }
        return ret;
    }

    public Optional<Film> getSel() {
        final int selectedTableRow = tableView.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            return Optional.of(tableView.getSelectionModel().getSelectedItem());
        } else {
            new MTAlert().showInfoNoSelection();
            return Optional.empty();
        }
    }

    private void initListener() {
        progData.setList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (progData.setList.getListButton().size() > 2) {
                boolInfoOn.set(true);
            }
            setSplit();
        });
        Listener.addListener(new Listener(Listener.EREIGNIS_GUI_COLOR_CHANGED, FilmGuiController.class.getSimpleName()) {
            @Override
            public void ping() {
                tableView.refresh();
//                Table.refresh_table(table);
            }
        });
    }

    private void setInfoTabPane() {
        final SetList list = progData.setList.getListButton();
        splitPane.getItems().clear();

        if (list.isEmpty()) {
            // dann brauchen wir den Tab mit den Button nicht
            splitPane.getItems().addAll(scrollPane, filmInfoPane);
            return;
        }

        TilePane tilePane = new TilePane();
        tilePane.setVgap(15);
        tilePane.setHgap(15);
        tilePane.setPadding(new Insets(20));
        list.stream().forEach(setData -> {
            Button btn = new Button(setData.getName());
            btn.setMaxWidth(Double.MAX_VALUE);
            tilePane.getChildren().add(btn);
            btn.setOnAction(a -> playFilmUrlWithSet(setData));
        });

        ScrollPane sc = new ScrollPane();
        sc.setFitToWidth(true);
        sc.setContent(tilePane);

        Tab filmInfoTab = new Tab("Filminfo");
        filmInfoTab.setClosable(false);
        filmInfoTab.setContent(filmInfoPane);

        Tab setTab = new Tab("Startbutton");
        setTab.setClosable(false);
        setTab.setContent(sc);

        final TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(filmInfoTab, setTab);
        splitPane.getItems().addAll(scrollPane, tabPane);
    }

    private void initTable() {
        tableView.setTableMenuButtonVisible(true);

        tableView.setEditable(false);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        new Table().setTable(tableView, Table.TABLE.FILM);

        tableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setOnMouseClicked(m -> {
            if (m.getButton().equals(MouseButton.PRIMARY) && m.getClickCount() == 2) {
                progData.filmInfoDialogController.showFilmInfo();
            }
        });

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<Film> film = getSel();
                if (film.isPresent()) {
                    ContextMenu contextMenu = new FilmGuiContextMenu(progData, this, tableView).getContextMenu(film.get());
                    tableView.setContextMenu(contextMenu);
                }
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
