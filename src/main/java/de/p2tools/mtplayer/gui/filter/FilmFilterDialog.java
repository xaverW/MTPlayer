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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2SeparatorComboBox;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilmFilterDialog extends P2DialogExtra {

    private final Button btnOk = new Button("_Ok");
    private final Button btnClearFilter = new Button("Filter löschen");

    private final TableView<FilmFilter> tableView = new TableView<>();
    private final ProgData progData;

    public FilmFilterDialog(ProgData progData) {
        super(ProgData.getInstance().primaryStage, ProgConfig.FILM__FILTER_DIALOG_SIZE, "Filmfilter",
                false, false, DECO.NO_BORDER, true);
        this.progData = progData;
        init(false);
    }

    @Override
    public void make() {
//        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> updateCss());

        setMaskerPane();
        progData.maskerPane.visibleProperty().addListener((u, o, n) -> {
            setMaskerPane();
        });

        // Button
        addOkButton(btnOk);
        btnOk.setOnAction(a -> close());
        addAnyButton(btnClearFilter);
        btnClearFilter.setTooltip(new Tooltip("Der Filter (nicht das Filterprofil) wird gelöscht"));
        btnClearFilter.setOnAction(a -> {
            progData.filterWorker.clearFilter();
            tableView.getSelectionModel().clearSelection();
        });

        // Tabelle
        getVBoxCont().getChildren().add(tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setMaxHeight(Double.MAX_VALUE);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            if (n != null) {
                progData.filterWorker.setActFilterSettings(n);
            }
        });
        tableView.setRowFactory(param -> new TableRow<>() {
            @Override
            protected void updateItem(FilmFilter item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    this.setDisable(P2SeparatorComboBox.isSeparator(item.toString()));
                }
            }
        });

        final TableColumn<FilmFilter, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnFactoryString(nameColumn);

//        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> columnFactoryString(nameColumn));
        tableView.getColumns().add(nameColumn);
        tableView.setItems(progData.filterWorker.getFilmFilterList());
    }

    @Override
    public void showDialog() {
        ProgConfig.FILM_GUI_FILTER_DIALOG_IS_SHOWING.setValue(true);
        super.showDialog();
    }

    @Override
    public void close() {
        ProgConfig.FILM_GUI_FILTER_DIALOG_IS_SHOWING.setValue(false);
        super.close();
    }

    private void setMaskerPane() {
        if (progData.maskerPane.isVisible()) {
            this.setMaskerVisible(true);
        } else {
            this.setMaskerVisible(false);
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

                if (P2SeparatorComboBox.isSeparator(item.toString())) {
                    setGraphic(ProgIcons.ICON_BUTTON_SEPARATOR_WIDTH.getImageView());
                    setText(null);
                    setStyle("-fx-alignment: center;");
                    setDisable(true);
                } else {
                    setGraphic(null);
                    setText(item.toString());
                    setStyle("");
                    setDisable(false);
                }
            }
        });
    }
}