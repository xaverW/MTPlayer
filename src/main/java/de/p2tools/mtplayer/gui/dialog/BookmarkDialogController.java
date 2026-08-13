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
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkDataProps;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.gui.BookmarkTableContextMenu;
import de.p2tools.mtplayer.gui.infoPane.PaneBookmarkInfo;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableBookmark;
import de.p2tools.mtplayer.gui.tools.table.TableRowBookmark;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.ptable.P2TableFactory;
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
import java.util.function.Predicate;

public class BookmarkDialogController extends P2DialogExtra {

    private final TableBookmark tableView;
    private final ProgData progData;
    private final PaneBookmarkInfo paneBookmarkInfo;
    private final PaneBookmarkDel paneBookmarkDel;
    private final Label lblSize = new Label();
    private final RadioButton rbAll = new RadioButton("Alle");
    private final RadioButton rbFilm = new RadioButton("Filme");
    private final RadioButton rbAudio = new RadioButton("Audios");
    private final Accordion accordion = new Accordion();
    private final TitledPane tpInfo = new TitledPane();
    private final TitledPane tpDel = new TitledPane();

    public BookmarkDialogController(ProgData progData) {
        super(progData.primaryStage, ProgConfig.BOOKMARK_DIALOG_SIZE, "Bookmarks",
                true, true, true, DECO.BORDER_VERY_SMALL);
        this.progData = progData;
        this.tableView = new TableBookmark(Table.TABLE_ENUM.BOOKMARK, progData);
        this.paneBookmarkInfo = new PaneBookmarkInfo();
        this.paneBookmarkDel = new PaneBookmarkDel(progData, getStageProp());

        initTable();
        initAccordion();
        initRadio();
        init(true);
    }

    public void close() {
        Table.saveTable(tableView, Table.TABLE_ENUM.BOOKMARK);
        ProgConfig.BOOKMARK_DIALOG_SHOW_INFO.set(accordion.getExpandedPane() != null &&
                accordion.getExpandedPane().equals(tpInfo));

        this.progData.bookmarkDialogController = null;
        paneBookmarkDel.close();
        super.close();
    }

    @Override
    public void make() {
        HBox hBoxRadio = new HBox(P2LibConst.PADDING_HBOX);
        hBoxRadio.setPadding(new Insets(4));
        hBoxRadio.setAlignment(Pos.CENTER_LEFT);
        hBoxRadio.getStyleClass().add("extra-pane-info");
        hBoxRadio.getChildren().addAll(rbAll, rbFilm, rbAudio, P2GuiTools.getHBoxGrower(), new Label("Anzahl: "), lblSize);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(hBoxRadio, tableView, P2GuiTools.getHDistance(5), accordion);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        getVBoxCont().getChildren().add(vbox);
        getVBoxCont().setPadding(new Insets(P2LibConst.PADDING));
        getVBoxCont().setSpacing(P2LibConst.PADDING_VBOX);
        VBox.setVgrow(vbox, Priority.ALWAYS);

        Button btnOk = new Button("Ok");
        btnOk.setOnAction(a -> {
            close();
        });
        Button btnHelp = PIconFactory.getHelpButton(getStage(), "Bookmarks", "Hier werden alle Bookmarks angezeigt. " +
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

    private void initRadio() {
        ToggleGroup tg = new ToggleGroup();
        rbAll.setToggleGroup(tg);
        rbFilm.setToggleGroup(tg);
        rbAudio.setToggleGroup(tg);
        rbAll.setSelected(true);
        rbAll.setOnAction(a -> setPred());
        rbFilm.setOnAction(a -> setPred());
        rbAudio.setOnAction(a -> setPred());
        setPred();
    }

    private void setPred() {
        Predicate<BookmarkData> pred = b -> true;
        if (rbFilm.isSelected()) {
            pred = pred.and(bookmarkData -> !bookmarkData.isAudio());
        } else if (rbAudio.isSelected()) {
            pred = pred.and(BookmarkDataProps::isAudio);
        }
        progData.bookmarkList.getFilteredList().setPredicate(pred);
    }

    private void initTable() {
        tableView.getStyleClass().add("extra-pane-info");
//        tableView.setPadding(new Insets(5));
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

    private void initAccordion() {
        tpInfo.setText("Info");
        tpInfo.setContent(paneBookmarkInfo);

        tpDel.setText("Löschen");
        tpDel.setContent(paneBookmarkDel);
        accordion.getPanes().addAll(tpInfo, tpDel);
        if (ProgConfig.BOOKMARK_DIALOG_SHOW_INFO.get()) {
            accordion.setExpandedPane(tpInfo);
        }
    }
}
