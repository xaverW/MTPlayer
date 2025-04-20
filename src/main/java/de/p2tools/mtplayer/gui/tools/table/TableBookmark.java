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

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkData;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.mtfilm.tools.FilmDate;
import de.p2tools.p2lib.p2event.P2Listener;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableBookmark extends PTable<BookmarkData> {

    public TableBookmark(Table.TABLE_ENUM table_enum, ProgData progData) {
        super(table_enum);
        this.table_enum = table_enum;
        iniColumn();
    }

    @Override
    public Table.TABLE_ENUM getETable() {
        return table_enum;
    }

    public void resetTable() {
        iniColumn();
        Table.resetTable(this);
    }

    private void refreshTable() {
        P2TableFactory.refreshTable(this);
    }

    private void iniColumn() {
        getColumns().clear();

        setTableMenuButtonVisible(true);
        setEditable(false);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // bei Farbänderung der Schriftfarbe klappt es damit besser: Table.refresh_table(table)
        ProgColorList.FILM_LIVESTREAM.colorProperty().addListener((a, b, c) -> P2TableFactory.refreshTable(this));
        ProgColorList.FILM_GEOBLOCK.colorProperty().addListener((a, b, c) -> P2TableFactory.refreshTable(this));
        ProgColorList.FILM_NEW.colorProperty().addListener((a, b, c) -> P2TableFactory.refreshTable(this));
        ProgColorList.FILM_HISTORY.colorProperty().addListener((a, b, c) -> P2TableFactory.refreshTable(this));
        ProgColorList.BOOKMARK.colorProperty().addListener((a, b, c) -> P2TableFactory.refreshTable(this));

        ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM.addListener((observableValue, s, t1) -> P2TableFactory.refreshTable(this)); //todo
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> P2TableFactory.refreshTable(this));
        ProgData.getInstance().pEventHandler.addListener(new P2Listener(PEvents.EVENT_REFRESH_TABLE) {
            @Override
            public void pingGui() {
                refreshTable();
            }
        });

        final TableColumn<BookmarkData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("special-column-style");

        final TableColumn<BookmarkData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("special-column-style");

        final TableColumn<BookmarkData, String> startColumn = new TableColumn<>("");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("buttonDummy"));
        startColumn.getStyleClass().add("alignCenter");
        TableBookmarkFactory.columnFactoryButton(startColumn);

        final TableColumn<BookmarkData, String> urlColumn = new TableColumn<>("Url");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlColumn.getStyleClass().add("special-column-style");

        final TableColumn<BookmarkData, FilmDate> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.getStyleClass().add("special-column-style");

        getColumns().addAll(titleColumn, themeColumn, startColumn, urlColumn, dateColumn);
    }
}
