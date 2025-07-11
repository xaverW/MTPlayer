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
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkData;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkFactory;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkLoadSaveFactory;
import de.p2tools.mtplayer.gui.BookmarkTableContextMenu;
import de.p2tools.mtplayer.gui.infoPane.PaneBookmarkInfo;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableBookmark;
import de.p2tools.mtplayer.gui.tools.table.TableRowBookmark;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class BookmarkDialogController extends P2DialogExtra {

    public static boolean isRunning = false;
    private final TableBookmark tableView;
    private final ProgData progData;
    private final PaneBookmarkInfo paneBookmarkInfo;
    private final Label lblSize = new Label();
    private final Button btnDel = new Button("Löschen");
    private final P2ToggleSwitch tglShow = new P2ToggleSwitch("Infos");

    public BookmarkDialogController(ProgData progData) {
        super(progData.primaryStage, ProgConfig.BOOKMARK_DIALOG_SIZE, "Bookmarks",
                false, false, DECO.BORDER_SMALL);
        isRunning = true;
        this.progData = progData;
        this.tableView = new TableBookmark(Table.TABLE_ENUM.BOOKMARK, progData);
        this.paneBookmarkInfo = new PaneBookmarkInfo();
        initTable();
        init(true);
    }

    public void close() {
        Table.saveTable(tableView, Table.TABLE_ENUM.BOOKMARK);
        super.close();
    }

    @Override
    public void make() {
        tglShow.selectedProperty().bindBidirectional(ProgConfig.BOOKMARK_DIALOG_SHOW_INFO);
        paneBookmarkInfo.visibleProperty().bind(ProgConfig.BOOKMARK_DIALOG_SHOW_INFO);
        paneBookmarkInfo.managedProperty().bind(ProgConfig.BOOKMARK_DIALOG_SHOW_INFO);

        // Del-Button
        progData.bookmarkList.addListener((u, o, n) -> {
            btnDel.setDisable(progData.bookmarkList.isEmpty());
        });
        btnDel.setDisable(progData.bookmarkList.isEmpty());

        btnDel.setTooltip(new Tooltip("Bookmarks werden gelöscht und in einem Dialog " +
                "wird vorher abgefragt, welche."));
        btnDel.setOnAction(a -> {
            BookmarkDelDialog b = new BookmarkDelDialog(progData, this.getStage());
            if (b.isOk()) {
                // dann löschen
                BookmarkFactory.deleteFromDialog(this.getStage(), false);
            }
        });

        HBox hBoxSize = new HBox(P2LibConst.SPACING_HBOX);
        hBoxSize.getChildren().addAll(btnDel, tglShow,
                P2GuiTools.getHBoxGrower(),
                new Label("Anzahl: "), lblSize);
        hBoxSize.setAlignment(Pos.CENTER_RIGHT);

        VBox vbox = new VBox(P2LibConst.SPACING_VBOX);
        vbox.getChildren().addAll(tableView, paneBookmarkInfo, hBoxSize);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        getVBoxCont().getChildren().add(vbox);
        getVBoxCont().setPadding(new Insets(P2LibConst.PADDING));
        getVBoxCont().setSpacing(P2LibConst.PADDING_VBOX);
        VBox.setVgrow(vbox, Priority.ALWAYS);

        Button btnOk = new Button("Ok");
        btnOk.setOnAction(a -> {
            quit();
        });
        Button btnHelp = P2Button.helpButton(getStage(), "Bookmarks", "Hier werden alle Bookmarks angezeigt. " +
                "Sie können gelöscht werden, es können die Filme angesehen oder gespeichert werden. Für rot markierte Bookmarks " +
                "gibt es keinen Film mehr in der Filmliste.");
        addHlpButton(btnHelp);
        addOkButton(btnOk);
    }

    private Optional<BookmarkData> getSel(boolean show) {
        Optional<BookmarkData> mtp;
        final int selectedTableRow = tableView.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            mtp = Optional.of(tableView.getSelectionModel().getSelectedItem());
        } else {
            if (show) {
                P2Alert.showInfoNoSelection();
            }
            mtp = Optional.empty();
        }
        return mtp;
    }

    private void initTable() {
        Table.setTable(tableView);
        tableView.setItems(progData.bookmarkList.getSortedList());
        progData.bookmarkList.getSortedList().comparatorProperty().bind(tableView.comparatorProperty());

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setRowFactory(tableView -> {
            TableRowBookmark<BookmarkData> row = new TableRowBookmark<>();
            row.hoverProperty().addListener((observable) -> {
                final BookmarkData bookmarkData = row.getItem();
                if (row.isHover() && bookmarkData != null) { // null bei den leeren Zeilen unterhalb
                    paneBookmarkInfo.setBookmarkData(bookmarkData);
                } else if (bookmarkData == null) {
                    paneBookmarkInfo.setBookmarkData(tableView.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });
        tableView.hoverProperty().addListener((o) -> {
            if (!tableView.isHover()) {
                paneBookmarkInfo.setBookmarkData(tableView.getSelectionModel().getSelectedItem());
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                //wird auch durch FilmlistenUpdate ausgelöst
                Platform.runLater(() -> {
                    paneBookmarkInfo.setBookmarkData(tableView.getSelectionModel().getSelectedItem());
                }));
        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<BookmarkData> optionalFilm = getSel(false);
                BookmarkData bookmarkData;
                bookmarkData = optionalFilm.orElse(null);
                ContextMenu contextMenu = new BookmarkTableContextMenu(progData, tableView).getContextMenu(bookmarkData);
                tableView.setContextMenu(contextMenu);
            }
        });

        tableView.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (P2TableFactory.SPACE.match(event)) {
                P2TableFactory.scrollVisibleRangeDown(tableView);
                event.consume();
            }
            if (P2TableFactory.SPACE_SHIFT.match(event) ||
                    P2TableFactory.SPACE_ALT.match(event) ||
                    P2TableFactory.SPACE_STRG.match(event)) {
                P2TableFactory.scrollVisibleRangeUp(tableView);
                event.consume();
            }
        });

        tableView.getItems().addListener((ListChangeListener<BookmarkData>)
                change -> lblSize.setText("" + tableView.getItems().size()));
        lblSize.setText("" + tableView.getItems().size());
    }

    private void quit() {
        Table.saveTable(tableView, Table.TABLE_ENUM.BOOKMARK);
        if (paneBookmarkInfo.isChanged()) {
            BookmarkLoadSaveFactory.saveBookmark();
        }
        isRunning = false;
        close();
    }
}
