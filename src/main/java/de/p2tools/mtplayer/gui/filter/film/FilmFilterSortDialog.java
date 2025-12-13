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

package de.p2tools.mtplayer.gui.filter.film;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filter.FilmFilter;
import de.p2tools.mtplayer.controller.filter.FilterDto;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.pcbo.P2CboSeparator;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilmFilterSortDialog extends P2DialogExtra {

    private final Button btnOk = new Button("_Ok");
    private final Button btnUp = new Button();
    private final Button btnDown = new Button();
    private final Button btnTop = new Button();
    private final Button btnBottom = new Button();
    private final Button btnDel = new Button();
    private final Button btnAddSeparator = new Button();

    private final TableView<FilmFilter> tableView = new TableView<>();
    private final ProgData progData;
    private final FilterDto filterDto;

    public FilmFilterSortDialog(ProgData progData, FilterDto filterDto) {
        super(ProgData.getInstance().primaryStage, null, "Filmfilter",
                true, false, false, DECO.NO_BORDER);
        this.progData = progData;
        this.filterDto = filterDto;

        init(false);
    }

    @Override
    public void make() {
        addOkButton(btnOk);
        btnOk.setOnAction(a -> close());

        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(btnTop, btnUp, btnDown, btnBottom, btnDel, btnAddSeparator);

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(tableView, vBox);
        HBox.setHgrow(tableView, Priority.ALWAYS);
        VBox.setVgrow(hBox, Priority.ALWAYS);
        getVBoxCont().getChildren().add(hBox);

        // Tabelle
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        final TableColumn<FilmFilter, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnFactoryString(nameColumn);

        tableView.getColumns().add(nameColumn);
        tableView.setItems(filterDto.filterWorker.getFilmFilterList());

        // Button
        btnDel.setTooltip(new Tooltip("aktuelles Filterprofil löschen"));
        btnDel.setGraphic(PIconFactory.PICON.BTN_MINUS.getFontIcon());
        btnDel.setOnAction(e -> delFilter());

        btnAddSeparator.setTooltip(new Tooltip("einen Trenner einfügen"));
        btnAddSeparator.setGraphic(PIconFactory.PICON.BTN_SEPARATOR.getFontIcon());
        btnAddSeparator.setOnAction(e -> addSeparator());

        btnTop.setTooltip(new Tooltip("aktuelles Filterprofil an den Anfang verschieben"));
        btnTop.setGraphic(PIconFactory.PICON.BTN_TOP.getFontIcon());
        btnTop.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                P2Alert.showInfoNoSelection();
            } else {
                int res = filterDto.filterWorker.getFilmFilterList().top(sel, true);
                tableView.getSelectionModel().select(res);
            }
        });

        btnBottom.setTooltip(new Tooltip("aktuelles Filterprofil an das Ende verschieben"));
        btnBottom.setGraphic(PIconFactory.PICON.BTN_BOTTOM.getFontIcon());
        btnBottom.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                P2Alert.showInfoNoSelection();
            } else {
                int res = filterDto.filterWorker.getFilmFilterList().top(sel, false);
                tableView.getSelectionModel().select(res);
            }
        });

        btnUp.setTooltip(new Tooltip("aktuelles Filterprofil nach oben verschieben"));
        btnUp.setGraphic(PIconFactory.PICON.BTN_UP.getFontIcon());
        btnUp.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                P2Alert.showInfoNoSelection();
            } else {
                int res = filterDto.filterWorker.getFilmFilterList().up(sel, true);
                tableView.getSelectionModel().select(res);
            }
        });

        btnDown.setTooltip(new Tooltip("aktuelles Filterprofil nach unten verschieben"));
        btnDown.setGraphic(PIconFactory.PICON.BTN_DOWN.getFontIcon());
        btnDown.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                P2Alert.showInfoNoSelection();
            } else {
                int res = filterDto.filterWorker.getFilmFilterList().up(sel, false);
                tableView.getSelectionModel().select(res);
            }
        });
    }

    private void delFilter() {
        FilmFilter sf = tableView.getSelectionModel().getSelectedItem();
        if (sf == null) {
            P2Alert.showInfoNoSelection();
            return;
        }

        if (filterDto.filterWorker.getFilmFilterList().removeStoredFilter(sf)) {
            tableView.getSelectionModel().selectFirst();
        }
    }

    private void addSeparator() {
        final int sel = tableView.getSelectionModel().getSelectedIndex();
        FilmFilter sf = new FilmFilter(P2CboSeparator.SEPARATOR);
        if (sel < 0) {
            filterDto.filterWorker.getFilmFilterList().add(sf);
        } else {
            filterDto.filterWorker.getFilmFilterList().add(sel + 1, sf);
        }
    }

    private void columnFactoryString(TableColumn<FilmFilter, String> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    setStyle("");
                    return;
                }

                if (P2CboSeparator.isSeparator(item)) {
                    setGraphic(PIconFactory.PICON.BTN_SEPARATOR_WIDTH.getFontIcon());
                    setText(null);
                    setStyle("-fx-alignment: center;");
                } else {
                    setGraphic(null);
                    setText(item);
                    setStyle("");
                }
            }
        });
    }
}