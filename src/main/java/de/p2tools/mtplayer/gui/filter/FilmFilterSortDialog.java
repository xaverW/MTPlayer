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

package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2SeparatorComboBox;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class FilmFilterSortDialog extends P2DialogExtra {

    private final Button btnOk = new Button("_Ok");
    private final Button btnUp = new Button();
    private final Button btnDown = new Button();
    private final Button btnTop = new Button();
    private final Button btnBottom = new Button();
    private final Button btnDel = new Button();
    private final Button btnSeparator = new Button();

    private final TableView<FilmFilter> tableView = new TableView<>();
    private final ProgData progData;

    public FilmFilterSortDialog(ProgData progData) {
        super(ProgData.getInstance().primaryStage, null, "Filmfilter", true, true, DECO.NO_BORDER);
        this.progData = progData;

        init(false);
    }

    @Override
    public void make() {
        addOkButton(btnOk);
        btnOk.setOnAction(a -> close());

        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(btnTop, btnUp, btnDown, btnBottom, btnDel, btnSeparator);

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(tableView, vBox);
        HBox.setHgrow(tableView, Priority.ALWAYS);
        VBox.setVgrow(hBox, Priority.ALWAYS);
        getVBoxCont().getChildren().add(hBox);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setRowFactory(param -> new TableRow<FilmFilter>() {
            @Override
            protected void updateItem(FilmFilter item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    if (P2SeparatorComboBox.isSeparator(item.toString())) {
                        setStyle(ProgColorList.FILTER_PROFILE_SEPARATOR.getCssBackgroundAndSel());
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        final TableColumn<FilmFilter, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(cellFactory);

        tableView.getColumns().add(nameColumn);
        tableView.setItems(progData.filterWorker.getStoredFilterList());

        btnDel.setTooltip(new Tooltip("aktuelles Filterprofil löschen"));
        btnDel.setGraphic(ProgIcons.ICON_BUTTON_REMOVE.getImageView());
        btnDel.setOnAction(e -> delFilter());

        btnSeparator.setTooltip(new Tooltip("einen Trenner einfügen"));
        btnSeparator.setGraphic(ProgIcons.ICON_BUTTON_SEPARATOR.getImageView());
        btnSeparator.setOnAction(e -> addSeparator());

        btnTop.setTooltip(new Tooltip("aktuelles Filterprofil an den Anfang verschieben"));
        btnTop.setGraphic(ProgIcons.ICON_BUTTON_MOVE_TOP.getImageView());
        btnTop.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                P2Alert.showInfoNoSelection();
            } else {
                int res = progData.filterWorker.getStoredFilterList().top(sel, true);
                tableView.getSelectionModel().select(res);
            }
        });

        btnBottom.setTooltip(new Tooltip("aktuelles Filterprofil an das Ende verschieben"));
        btnBottom.setGraphic(ProgIcons.ICON_BUTTON_MOVE_BOTTOM.getImageView());
        btnBottom.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                P2Alert.showInfoNoSelection();
            } else {
                int res = progData.filterWorker.getStoredFilterList().top(sel, false);
                tableView.getSelectionModel().select(res);
            }
        });

        btnUp.setTooltip(new Tooltip("aktuelles Filterprofil nach oben verschieben"));
        btnUp.setGraphic(ProgIcons.ICON_BUTTON_MOVE_UP.getImageView());
        btnUp.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                P2Alert.showInfoNoSelection();
            } else {
                int res = progData.filterWorker.getStoredFilterList().up(sel, true);
                tableView.getSelectionModel().select(res);
            }
        });

        btnDown.setTooltip(new Tooltip("aktuelles Filterprofil nach unten verschieben"));
        btnDown.setGraphic(ProgIcons.ICON_BUTTON_MOVE_DOWN.getImageView());
        btnDown.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                P2Alert.showInfoNoSelection();
            } else {
                int res = progData.filterWorker.getStoredFilterList().up(sel, false);
                tableView.getSelectionModel().select(res);
            }
        });
    }

    private Callback<TableColumn<FilmFilter, String>, TableCell<FilmFilter, String>> cellFactory
            = (final TableColumn<FilmFilter, String> param) -> {

        final TableCell<FilmFilter, String> cell = new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                FilmFilter filmFilter = getTableView().getItems().get(getIndex());
                HBox hBox = new HBox();
                Label lbl = new Label(filmFilter.getName());
                hBox.getChildren().add(lbl);
                setGraphic(hBox);
                if (P2SeparatorComboBox.isSeparator(filmFilter.toString())) {
                    hBox.setAlignment(Pos.CENTER);
                }
            }
        };
        return cell;
    };

    private void delFilter() {
        FilmFilter sf = tableView.getSelectionModel().getSelectedItem();
        if (sf == null) {
            P2Alert.showInfoNoSelection();
            return;
        }

        if (progData.filterWorker.removeStoredFilter(sf)) {
            tableView.getSelectionModel().selectFirst();
        }
    }

    private void addSeparator() {
        final int sel = tableView.getSelectionModel().getSelectedIndex();
        FilmFilter sf = new FilmFilter(P2SeparatorComboBox.SEPARATOR);
        if (sel < 0) {
            progData.filterWorker.getStoredFilterList().add(sf);
        } else {
            progData.filterWorker.getStoredFilterList().add(sel + 1, sf);
        }
    }
}