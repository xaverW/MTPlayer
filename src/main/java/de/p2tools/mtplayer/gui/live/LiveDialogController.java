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

package de.p2tools.mtplayer.gui.live;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmListMTP;
import de.p2tools.mtplayer.controller.livesearch.LiveSearch;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableLiveFilm;
import de.p2tools.mtplayer.gui.tools.table.TableRowFilm;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.P2TableFactory;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class LiveDialogController extends PDialogExtra {

    private final Button btnOk = new Button("_Ok");
    private final ProgData progData;
    private final TableLiveFilm tableView;
    private final FilmListMTP filmList;
    private final PaneLiveFilmInfo paneLiveFilmInfo;
    private final SplitPane splitPane = new SplitPane();
    private final VBox vboxTop = new VBox();
    private final ProgressBar progress = new ProgressBar();
    private final DoubleProperty progressProp = new SimpleDoubleProperty(-1);
    private final Label lblCount = new Label();

    public LiveDialogController(ProgData progData) {
        super(ProgData.getInstance().primaryStage, ProgConfig.SYSTEM_SIZE_DIALOG_LIVE_SEARCH,
                "Live-Suche", false, false, DECO.BORDER_SMALL, true);
        this.progData = progData;
        this.tableView = new TableLiveFilm(Table.TABLE_ENUM.LIVE_FILM, progData);
        this.filmList = new FilmListMTP();
        this.paneLiveFilmInfo = new PaneLiveFilmInfo(ProgConfig.LIVE_FILM_GUI_INFO_DIVIDER);

        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().addAll(vboxTop, paneLiveFilmInfo);
        splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.LIVE_FILM_GUI_SPLITPANE);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        getVBoxCont().getChildren().add(splitPane);
        init(false);
    }

    @Override
    public void close() {
        splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.LIVE_FILM_GUI_SPLITPANE);
        super.close();
    }

    @Override
    public void make() {
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> updateCss());
        addOkButton(btnOk);
        getHboxLeft().getChildren().addAll(lblCount, progress);
        getHboxLeft().setAlignment(Pos.CENTER_LEFT);
        progress.progressProperty().bind(progressProp);
        progress.visibleProperty().bind(progressProp.greaterThan(-1));
        filmList.sizeProperty().addListener((u, o, n) -> lblCount.setText(filmList.size() + " Filme"));
        lblCount.setText(filmList.size() + " Filme");

        getMaskerPane().setTextVisible(false);
        this.getMaskerPane().visibleProperty().bind(ProgData.getInstance().maskerPane.visibleProperty());

        addSearch();
        initTable();

        btnOk.setOnAction(a -> close());
    }

    private void addSearch() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        TextField txtSearch = new TextField("");
        Button btnSearch = new Button();
        btnSearch.setGraphic(ProgIcons.ICON_BUTTON_SEARCH_16.getImageView());
        btnSearch.setOnAction(a -> {
            new Thread(() -> {
                List<FilmDataMTP> list = LiveSearch.loadAudioFromWeb(progressProp, txtSearch.getText());
                Platform.runLater(() -> {
                    list.forEach(filmList::importFilmOnlyWithNr);
                });
            }).start();
        });
        btnSearch.disableProperty().bind(txtSearch.textProperty().length().lessThan(5));
        Button btnClear = new Button();
        btnClear.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnClear.setOnAction(a -> {
            txtSearch.clear();
            filmList.clear();
        });

        hBox.getChildren().addAll(txtSearch, btnSearch, btnClear);
        hBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(txtSearch, Priority.ALWAYS);
        vboxTop.getChildren().add(hBox);
    }

    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.LIVE_FILM);
    }

    private void initTable() {
        Table.setTable(tableView);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(tableView);
        vboxTop.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        FilteredList<FilmDataMTP> filteredList = new FilteredList<FilmDataMTP>(filmList, p -> true);
        SortedList<FilmDataMTP> sortedList = new SortedList<>(filteredList);

        tableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (P2TableFactory.SPACE.match(event)) {
                P2TableFactory.scrollVisibleRangeDown(tableView);
                event.consume();
            }
            if (P2TableFactory.SPACE_SHIFT.match(event)) {
                P2TableFactory.scrollVisibleRangeUp(tableView);
                event.consume();
            }
        });
        tableView.setRowFactory(tableView -> {
            TableRowFilm<FilmDataMTP> row = new TableRowFilm<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
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

    }

    private void setFilmInfos(FilmDataMTP film) {
        // Film in FilmInfoDialog setzen
        paneLiveFilmInfo.setFilm(film);
        FilmInfoDialogController.getInstance().setFilm(film);
    }
}
