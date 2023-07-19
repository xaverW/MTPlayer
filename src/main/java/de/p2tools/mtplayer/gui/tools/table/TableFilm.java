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
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.guitools.PTableFactory;
import de.p2tools.p2lib.guitools.ptable.CellCheckBox;
import de.p2tools.p2lib.guitools.ptable.CellIntNull;
import de.p2tools.p2lib.mtfilm.film.FilmSize;
import de.p2tools.p2lib.tools.date.PDate;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableFilm extends PTable<FilmDataMTP> {

    public TableFilm(Table.TABLE_ENUM table_enum, ProgData progData) {
        super(table_enum);
        this.table_enum = table_enum;
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

        final TableColumn<FilmDataMTP, Integer> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("no"));
        nrColumn.getStyleClass().add("alignCenterRightPadding_10");

        final TableColumn<FilmDataMTP, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        senderColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmDataMTP, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<FilmDataMTP, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<FilmDataMTP, String> startColumn = new TableColumn<>("");
        startColumn.setCellFactory(new CellFilmButton().cellFactory);
        startColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmDataMTP, PDate> datumColumn = new TableColumn<>("Datum");
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        datumColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmDataMTP, String> timeColumn = new TableColumn<>("Zeit");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmDataMTP, Integer> durationColumn = new TableColumn<>("Dauer [min]");
        durationColumn.setCellFactory(new CellIntNull().cellFactory);
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinute"));
        durationColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<FilmDataMTP, FilmSize> sizeColumn = new TableColumn<>("Größe [MB]");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("filmSize"));
        sizeColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<FilmDataMTP, Boolean> hdColumn = new TableColumn<>("HD");
        hdColumn.setCellValueFactory(new PropertyValueFactory<>("hd"));
        hdColumn.setCellFactory(new CellCheckBox().cellFactory);
        hdColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmDataMTP, Boolean> utColumn = new TableColumn<>("UT");
        utColumn.setCellValueFactory(new PropertyValueFactory<>("ut"));
        utColumn.setCellFactory(new CellCheckBox().cellFactory);
        utColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmDataMTP, String> geoColumn = new TableColumn<>("Geo");
        geoColumn.setCellValueFactory(new PropertyValueFactory<>("geo"));
        geoColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmDataMTP, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<FilmDataMTP, String> aboColumn = new TableColumn<>("Abo");
        aboColumn.setCellValueFactory(new PropertyValueFactory<>("aboName"));
        aboColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<FilmDataMTP, Boolean> bookmarkColumn = new TableColumn<>("Bookmark");
        bookmarkColumn.setCellValueFactory(new PropertyValueFactory<>("bookmark"));
        bookmarkColumn.setCellFactory(new CellCheckBox().cellFactory);
        bookmarkColumn.getStyleClass().add("alignCenterLeft");

        nrColumn.setPrefWidth(50);
        senderColumn.setPrefWidth(80);
        themeColumn.setPrefWidth(180);
        titleColumn.setPrefWidth(230);

        getColumns().addAll(
                nrColumn,
                senderColumn, themeColumn, titleColumn,
                startColumn,
                datumColumn, timeColumn, durationColumn, sizeColumn,
                hdColumn, utColumn,
                geoColumn,
                urlColumn, aboColumn, bookmarkColumn);
    }
}
