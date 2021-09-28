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

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilter;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilmFilterSortDialog extends PDialogExtra {

    private final Button btnOk = new Button("_Ok");
    private final Button btnUp = new Button();
    private final Button btnDown = new Button();
    private final Button btnDel = new Button();

    private final TableView<SelectedFilter> tableView = new TableView<>();
    private final ProgData progData;

    public FilmFilterSortDialog(ProgData progData) {
        super(ProgData.getInstance().primaryStage, null, "Filmfilter", true, true, DECO.NONE);
        this.progData = progData;

        init(false);
    }


    @Override
    public void make() {
        addOkButton(btnOk);
        btnOk.setOnAction(a -> close());

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(btnUp, btnDown, btnDel);

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(tableView, vBox);
        HBox.setHgrow(tableView, Priority.ALWAYS);

        getvBoxCont().getChildren().add(hBox);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        final TableColumn<SelectedFilter, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        tableView.getColumns().add(nameColumn);
        tableView.setItems(progData.storedFilters.getStoredFilterList());

        btnDel.setTooltip(new Tooltip("aktuelles Filterprofil löschen"));
        btnDel.setGraphic(new ProgIcons().ICON_BUTTON_REMOVE);
        btnDel.setOnAction(e -> delFilter());

        btnUp.setTooltip(new Tooltip("aktuelles Filterprofil nach oben verschieben"));
        btnUp.setGraphic(new ProgIcons().ICON_BUTTON_MOVE_UP);
        btnUp.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = progData.storedFilters.getStoredFilterList().up(sel, true);
                tableView.getSelectionModel().select(res);
            }
        });

        btnDown.setTooltip(new Tooltip("aktuelles Filterprofil nach unten verschieben"));
        btnDown.setGraphic(new ProgIcons().ICON_BUTTON_MOVE_DOWN);
        btnDown.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = progData.storedFilters.getStoredFilterList().up(sel, false);
                tableView.getSelectionModel().select(res);
            }
        });
    }

    private void delFilter() {
        SelectedFilter sf = tableView.getSelectionModel().getSelectedItem();
        if (sf == null) {
            PAlert.showInfoNoSelection();
            return;
        }

        if (progData.storedFilters.removeStoredFilter(sf)) {
            tableView.getSelectionModel().selectFirst();
        }
    }
}