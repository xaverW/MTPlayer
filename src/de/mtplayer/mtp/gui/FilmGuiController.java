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
import de.mtplayer.mtp.gui.mediaDialog.MediaDialogController;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.gui.tools.Table;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.guiTools.PColor;
import de.p2tools.p2Lib.tools.log.PDuration;
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
    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private final TableView<Film> tableView = new TableView<>();

    private final TabPane infoTab = new TabPane();
    private final AnchorPane infoPane = new AnchorPane();
    private final AnchorPane filmPane = new AnchorPane();
    private final TilePane tilePaneButton = new TilePane();

    private final ProgData progData;
    private FilmGuiInfoController filmGuiInfoController;

    DoubleProperty splitPaneProperty = ProgConfig.FILM_GUI_DIVIDER.getDoubleProperty();
    BooleanProperty boolInfoOn = ProgConfig.FILM_GUI_DIVIDER_ON.getBooleanProperty();
    private boolean bound = false;

    private final SortedList<Film> sortedList;

    public FilmGuiController() {
        progData = ProgData.getInstance();
        sortedList = progData.filmlistFiltered.getSortedList();

        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        splitPane.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(splitPane);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(tableView);

        initInfoPane();
        setInfoPane();

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

    private void initInfoPane() {
        filmGuiInfoController = new FilmGuiInfoController(filmPane);
        boolInfoOn.addListener((observable, oldValue, newValue) -> setInfoPane());

        tilePaneButton.setVgap(15);
        tilePaneButton.setHgap(15);
        tilePaneButton.setPadding(new Insets(10));
        tilePaneButton.setStyle("-fx-border-color: -fx-text-box-border; " +
                "-fx-border-radius: 5px; " +
                "-fx-border-width: 1;");

//        filmPane.setMinHeight(10);
    }

    private void setInfoPane() {
        infoPane.setVisible(boolInfoOn.getValue());
        infoPane.setManaged(boolInfoOn.getValue());

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
                splitPane.getItems().add(scrollPane);
            }
        }
    }

    private void setInfoTabPane() {
        final SetList setList = progData.setList.getListButton();

        if (setList.isEmpty()) {
            // dann brauchen wir den Tab mit den Button nicht
            if (splitPane.getItems().size() != 2 || splitPane.getItems().get(1) != filmPane) {
                splitPane.getItems().clear();
                splitPane.getItems().addAll(scrollPane, filmPane);
            }
            return;
        }


        // Button wieder aufbauen
        tilePaneButton.getChildren().clear();
        setList.stream().forEach(setData -> {
            Button btn = new Button(setData.getName());
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
            splitPane.getItems().addAll(scrollPane, infoTab);

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPadding(new Insets(10));
            scrollPane.setContent(tilePaneButton);

            Tab filmInfoTab = new Tab("Filminfo");
            filmInfoTab.setClosable(false);
            filmInfoTab.setContent(filmPane);

            Tab setTab = new Tab("Startbutton");
            setTab.setClosable(false);
            setTab.setContent(scrollPane);

            infoTab.getTabs().clear();
            infoTab.getTabs().addAll(filmInfoTab, setTab);
        }
    }

    private void setFilm() {
        Film film = tableView.getSelectionModel().getSelectedItem();
        filmGuiInfoController.setFilm(film);
        progData.filmInfoDialogController.setFilm(film);
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
        PDuration.counterStart("filmUngesehen");
        //todo-> ~1s Dauer

        final ArrayList<Film> list = getSelList();
        FilmTools.setFilmShown(progData, list, false);

        Table.refresh_table(tableView);
        PDuration.counterStop("filmUngesehen");
    }


    public void saveTable() {
        new Table().saveTable(tableView, Table.TABLE.FILM);
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
        final int selectedTableRow = tableView.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            return Optional.of(tableView.getSelectionModel().getSelectedItem());
        } else {
            PAlert.showInfoNoSelection();
            return Optional.empty();
        }
    }

    private void initListener() {
        progData.setList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (progData.setList.getListButton().size() > 2) {
                boolInfoOn.set(true);
            }
            setInfoPane();
        });
        Listener.addListener(new Listener(Listener.EREIGNIS_GUI_COLOR_CHANGED, FilmGuiController.class.getSimpleName()) {
            @Override
            public void ping() {
                tableView.refresh();
//                Table.refresh_table(tableView);
            }
        });
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
