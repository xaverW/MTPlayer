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

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilter;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PGuiSize;
import de.p2tools.p2Lib.guiTools.PSeparatorComboBox;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class FilmFilterDialog extends PDialogExtra {

    private final Button btnOk = new Button("_Ok");
    private final Button btnClearFilter = new Button("Filter löschen");

    private final TableView<SelectedFilter> tableView = new TableView<>();
    private final ProgData progData;

    public FilmFilterDialog(ProgData progData) {
        super(ProgData.getInstance().primaryStage, ProgConfig.FILM_GUI_FILTER_DIALOG, "Filmfilter", false, false, DECO.NONE);
        this.progData = progData;
        ProgConfig.FILM_GUI_FILTER_DIALOG_IS_SHOWING.setValue(true);

        init(false);
    }

    @Override
    public void make() {
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> updateCss());
        if (progData.maskerPane.isVisible()) {
            this.getStage().getScene().getWindow().hide();
        }
        progData.maskerPane.visibleProperty().addListener((u, o, n) -> {
            if (progData.maskerPane.isVisible()) {
                this.getStage().getScene().getWindow().hide();
            } else {
                this.showDialog();
            }
        });

        addOkButton(btnOk);
        btnOk.setOnAction(a -> close());

        this.getStage().getScene().getWindow().heightProperty().addListener((u, o, n) -> {
            PGuiSize.getSizeScene(ProgConfig.FILM_GUI_FILTER_DIALOG, this.getStage());
        });
        this.getStage().getScene().getWindow().widthProperty().addListener((u, o, n) -> {
            PGuiSize.getSizeScene(ProgConfig.FILM_GUI_FILTER_DIALOG, this.getStage());
        });
        this.getStage().getScene().getWindow().xProperty().addListener((u, o, n) -> {
            PGuiSize.getSizeScene(ProgConfig.FILM_GUI_FILTER_DIALOG, this.getStage());
        });
        this.getStage().getScene().getWindow().yProperty().addListener((u, o, n) -> {
            PGuiSize.getSizeScene(ProgConfig.FILM_GUI_FILTER_DIALOG, this.getStage());
        });

        addAnyButton(btnClearFilter);
        btnClearFilter.setTooltip(new Tooltip("Der Filter (nicht das Filterprofil) wird gelöscht"));
        btnClearFilter.setOnAction(a -> {
            progData.storedFilters.clearFilter();
            tableView.getSelectionModel().clearSelection();
        });

        getvBoxCont().getChildren().add(tableView);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            SelectedFilter selectedFilter = n;
            if (n != null) {
                progData.storedFilters.setActFilterSettings(n);
            }
        });

        tableView.setRowFactory(param -> new TableRow<SelectedFilter>() {
            @Override
            protected void updateItem(SelectedFilter item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    if (PSeparatorComboBox.isSeparator(item.toString())) {
                        this.setDisable(true);
                        setStyle(ProgColorList.FILTER_PROFILE_SEPARATOR.getCssBackgroundAndSel());
                    } else {
                        this.setDisable(false);
                        setStyle("");
                    }
                }
            }
        });


        final TableColumn<SelectedFilter, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(cellFactory);
        tableView.getColumns().add(nameColumn);
        tableView.setItems(progData.storedFilters.getStoredFilterList());

    }

    private Callback<TableColumn<SelectedFilter, String>, TableCell<SelectedFilter, String>> cellFactory
            = (final TableColumn<SelectedFilter, String> param) -> {

        final TableCell<SelectedFilter, String> cell = new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                SelectedFilter selectedFilter = getTableView().getItems().get(getIndex());
                HBox hBox = new HBox();
                Label lbl = new Label(selectedFilter.getName());
                hBox.getChildren().add(lbl);
                setGraphic(hBox);
                if (PSeparatorComboBox.isSeparator(selectedFilter.toString())) {
                    hBox.setAlignment(Pos.CENTER);
                }
            }
        };
        return cell;
    };

    @Override
    public void close() {
        ProgConfig.FILM_GUI_FILTER_DIALOG_IS_SHOWING.setValue(false);
        super.close();
    }
}