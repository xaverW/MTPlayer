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

package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.data.film.FilmData;
import de.p2tools.mtplayer.controller.data.film.FilmSize;
import de.p2tools.mtplayer.controller.data.film.FilmTools;
import de.p2tools.p2Lib.guiTools.PCheckBoxCell;
import de.p2tools.p2Lib.guiTools.PTableFactory;
import de.p2tools.p2Lib.tools.date.PDate;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class TableFilm extends PTable<FilmData> {

    private final ProgData progData;
    private final BooleanProperty small;

    public TableFilm(Table.TABLE_ENUM table_enum, ProgData progData) {
        super(table_enum);
        this.table_enum = table_enum;
        this.progData = progData;
        small = ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM;

//        setFixedCellSize(25);

        initFileRunnerColumn();
    }

    @Override
    public Table.TABLE_ENUM getETable() {
        return table_enum;
    }

    public void resetTable() {
        initFileRunnerColumn();
        Table.resetTable(this);
    }

    private void initFileRunnerColumn() {
        getColumns().clear();

        setTableMenuButtonVisible(true);
        setEditable(false);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM.addListener((observableValue, s, t1) -> refresh());

        // bei Farbänderung der Schriftfarbe klappt es damit besser: Table.refresh_table(table)
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> PTableFactory.refreshTable(this));
        ProgColorList.FILM_LIVESTREAM.colorProperty().addListener((a, b, c) -> PTableFactory.refreshTable(this));
        ProgColorList.FILM_GEOBLOCK.colorProperty().addListener((a, b, c) -> PTableFactory.refreshTable(this));
        ProgColorList.FILM_NEW.colorProperty().addListener((a, b, c) -> PTableFactory.refreshTable(this));
        ProgColorList.FILM_HISTORY.colorProperty().addListener((a, b, c) -> PTableFactory.refreshTable(this));
        ProgColorList.FILM_BOOKMARK.colorProperty().addListener((a, b, c) -> PTableFactory.refreshTable(this));

        final TableColumn<FilmData, Integer> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("no"));
        nrColumn.getStyleClass().add("alignCenterRightPadding_10");

        final TableColumn<FilmData, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        senderColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<FilmData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<FilmData, String> startColumn = new TableColumn<>("");
        startColumn.setCellFactory(cellFactoryStart);
        startColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmData, PDate> datumColumn = new TableColumn<>("Datum");
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        datumColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmData, String> timeColumn = new TableColumn<>("Zeit");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmData, Integer> durationColumn = new TableColumn<>("Dauer [min]");
        durationColumn.setCellFactory(cellFactoryDuration);
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinute"));
        durationColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<FilmData, FilmSize> sizeColumn = new TableColumn<>("Größe [MB]");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("filmSize"));
        sizeColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<FilmData, Boolean> hdColumn = new TableColumn<>("HD");
        hdColumn.setCellValueFactory(new PropertyValueFactory<>("hd"));
        hdColumn.setCellFactory(new PCheckBoxCell().cellFactoryBool);
        hdColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmData, Boolean> utColumn = new TableColumn<>("UT");
        utColumn.setCellValueFactory(new PropertyValueFactory<>("ut"));
        utColumn.setCellFactory(new PCheckBoxCell().cellFactoryBool);
        utColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmData, String> geoColumn = new TableColumn<>("Geo");
        geoColumn.setCellValueFactory(new PropertyValueFactory<>("geo"));
        geoColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmData, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<FilmData, String> aboColumn = new TableColumn<>("Abo");
        aboColumn.setCellValueFactory(new PropertyValueFactory<>("aboName"));
        aboColumn.getStyleClass().add("alignCenterLeft");

        nrColumn.setPrefWidth(50);
        senderColumn.setPrefWidth(80);
        themeColumn.setPrefWidth(180);
        titleColumn.setPrefWidth(230);

//        addRowFact(table);

        getColumns().addAll(
                nrColumn,
                senderColumn, themeColumn, titleColumn,
                startColumn,
                datumColumn, timeColumn, durationColumn, sizeColumn,
                hdColumn, utColumn,
                geoColumn,
                urlColumn, aboColumn);
    }

    private Callback<TableColumn<FilmData, String>, TableCell<FilmData, String>> cellFactoryStart
            = (final TableColumn<FilmData, String> param) -> {

        final TableCell<FilmData, String> cell = new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                FilmData film = getTableView().getItems().get(getIndex());

                final HBox hbox = new HBox();
                hbox.setSpacing(4);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final Button btnPlay;
                final Button btnSave;

                btnPlay = new Button("");
                btnPlay.setGraphic(ProgIcons.Icons.IMAGE_TABLE_FILM_PLAY.getImageView());

                btnSave = new Button("");
                btnSave.setGraphic(ProgIcons.Icons.IMAGE_TABLE_FILM_SAVE.getImageView());

                if (small.get()) {
                    btnPlay.setMaxHeight(18);
                    btnPlay.setMinHeight(18);
                    btnSave.setMaxHeight(18);
                    btnSave.setMinHeight(18);
                }

                btnPlay.setOnAction((ActionEvent event) -> {
                    FilmTools.playFilm(film, null);
                });
                btnSave.setOnAction(event -> {
                    ProgData.getInstance().filmlist.saveFilm(film, null);
                });
                hbox.getChildren().addAll(btnPlay, btnSave/*, btnBookmark*/);
                setGraphic(hbox);
            }
        };
        return cell;
    };

    private Callback<TableColumn<FilmData, Integer>, TableCell<FilmData, Integer>> cellFactoryDuration
            = (final TableColumn<FilmData, Integer> param) -> {

        final TableCell<FilmData, Integer> cell = new TableCell<>() {

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                if (item == 0) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(item + "");
                }

            }
        };
        return cell;
    };
}
