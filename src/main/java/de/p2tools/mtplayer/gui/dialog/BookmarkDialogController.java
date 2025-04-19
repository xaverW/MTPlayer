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

package de.p2tools.mtplayer.gui.dialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.mtfilm.tools.FilmDate;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class BookmarkDialogController extends P2DialogExtra {

    private final VBox vBoxCont;

    public BookmarkDialogController() {
        super(ProgData.getInstance().primaryStage, ProgConfig.BOOKMARK_DIALOG_SIZE, "Bookmarks",
                false, false, DECO.BORDER_SMALL);

        vBoxCont = getVBoxCont();
        init(true);
    }

    @Override
    public void make() {
        getHBoxTitle().getChildren().add(new Label("Bookmarks"));
        vBoxCont.setPadding(new Insets(P2LibConst.PADDING));
        vBoxCont.setSpacing(P2LibConst.PADDING_VBOX);

        TableView<HistoryData> table = new TableView<>();
        final TableColumn<HistoryData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("special-column-style");

        final TableColumn<HistoryData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("special-column-style");

        final TableColumn<HistoryData, String> urlColumn = new TableColumn<>("Url");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlColumn.getStyleClass().add("special-column-style");

        final TableColumn<HistoryData, FilmDate> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.getStyleClass().add("special-column-style");

        table.getColumns().addAll(titleColumn, themeColumn, urlColumn, dateColumn);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setItems(ProgData.getInstance().historyListBookmarks);
        vBoxCont.getChildren().addAll(table);


        Button btnOk = new Button("Ok");
        btnOk.setOnAction(a -> {
            quit();
        });
        Button btnHelp = P2Button.helpButton(getStage(),
                "Bookmarks", "");
        addHlpButton(btnHelp);
        addOkButton(btnOk);
    }

    private void quit() {
        close();
    }
}
