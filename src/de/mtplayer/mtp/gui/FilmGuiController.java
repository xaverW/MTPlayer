/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

import de.mtplayer.mLib.tools.Duration;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.SetList;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.mediaDialog.MediaDialogController;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.gui.tools.Table;
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
    TableView<Film> table = new TableView<>();

    private final AnchorPane filmInfoPane = new AnchorPane();

    private final Daten daten;
    private FilmGuiInfoController filmGuiInfoController;

    DoubleProperty splitPaneProperty = Config.FILM_GUI_DIVIDER.getDoubleProperty();
    BooleanProperty boolInfoOn = Config.FILM_GUI_DIVIDER_ON.getBooleanProperty();
    private boolean bound = false;

    private final SortedList<Film> sortedList;

    public FilmGuiController() {
        daten = Daten.getInstance();

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(table);

        setInfoTabPane();

        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        splitPane.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(splitPane);

        sortedList = daten.filmListFiltered.getSortedList();

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
        return table.getItems().size();
    }

    public int getSelCount() {
        return table.getSelectionModel().getSelectedItems().size();
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
        Film film = table.getSelectionModel().getSelectedItem();
        filmGuiInfoController.setFilm(film);
        daten.filmInfosDialogController.set(film);
        return;
    }

    public void showFilmInfo() {
        daten.filmInfosDialogController.showFilmInfo();
    }

    public void playFilmUrl() {
        // Menü/Button Film (URL) abspielen
        startFilmUrl();
    }

    public void playFilmUrlWithSet(SetData psetData) {
        startFilmUrlWithSet(psetData);
    }

    public void filmSpeichern() {
        saveFilm();
    }

    public void guiFilmMediensammlung() {
        final Optional<Film> film = getSel();
        if (film.isPresent()) {
            new MediaDialogController(film.get().getTitel());
        }
    }

    public void filmGesehen() {
        final ArrayList<Film> liste = getSelList();
        FilmTools.setFilmShown(daten, liste, true);

        Table.refresh_table(table);
    }

    public void filmUngesehen() {
        Duration.counterStart("filmUngesehen");
        //todo-> ~1s Dauer
        
        final ArrayList<Film> liste = getSelList();
        FilmTools.setFilmShown(daten, liste, false);

        Table.refresh_table(table);
        Duration.counterStop("filmUngesehen");
    }


    public void saveTable() {
        new Table().saveTable(table, Table.TABLE.FILM);
    }

    public ArrayList<Film> getSelList() {
        final ArrayList<Film> ret = new ArrayList<>();
        ret.addAll(table.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            new MTAlert().showInfoNoSelection();
        }
        return ret;
    }

    public Optional<Film> getSel() {
        final int selectedTableRow = table.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            return Optional.of(table.getSelectionModel().getSelectedItem());
        } else {
            new MTAlert().showInfoNoSelection();
            return Optional.empty();
        }
    }

    private void initListener() {
        daten.setList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (daten.setList.getListeButton().size() > 2) {
                boolInfoOn.set(true);
            }
            setSplit();
        });
        Listener.addListener(new Listener(Listener.EREIGNIS_GUI_COLOR_CHANGED, FilmGuiController.class.getSimpleName()) {
            @Override
            public void ping() {
                table.refresh();
//                Table.refresh_table(table);
            }
        });
    }

    private void setInfoTabPane() {
        final SetList liste = daten.setList.getListeButton();
        splitPane.getItems().clear();

        if (liste.isEmpty()) {
            // dann brauchen wir den Tab mit den Button nicht
            splitPane.getItems().addAll(scrollPane, filmInfoPane);
            return;
        }

        TilePane tilePane = new TilePane();
        tilePane.setVgap(15);
        tilePane.setHgap(15);
        tilePane.setPadding(new Insets(20));
        liste.stream().forEach(setData -> {
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
        table.setTableMenuButtonVisible(true);
        table.setEditable(false);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        new Table().setTable(table, Table.TABLE.FILM);

        table.setItems(sortedList);
        sortedList.comparatorProperty().bind(table.comparatorProperty());

        table.setOnMouseClicked(m -> {
            if (m.getButton().equals(MouseButton.PRIMARY) && m.getClickCount() == 2) {
                daten.filmInfosDialogController.showFilmInfo();
            }
        });

        table.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<Film> film = getSel();
                if (film.isPresent()) {
                    ContextMenu contextMenu = new FilmGuiContextMenu(daten, this, table).getContextMenue(film.get());
                    table.setContextMenu(contextMenu);
                }
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
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
        final ArrayList<Film> liste = getSelList();
        daten.filmList.saveFilm(liste, pSet);
    }

}
