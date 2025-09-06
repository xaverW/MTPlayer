/*
 * P2tools Copyright (C) 2022 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboListFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class CellAboButton<S, T> extends TableCell<S, T> {

    public final Callback<TableColumn<AboData, String>, TableCell<AboData, String>> cellFactory
            = (final TableColumn<AboData, String> param) -> {

        final TableCell<AboData, String> cell = new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                final HBox hbox = new HBox();
                hbox.setSpacing(4);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                AboData aboData = getTableView().getItems().get(getIndex());
                final Button btnActivate;
                btnActivate = new Button("");
                btnActivate.setTooltip(new Tooltip("Abo ein- oder ausschalten"));
                btnActivate.getStyleClass().addAll("btnFunction", "btnFuncTable");
                if (aboData.isActive()) {
                    // dann ausschalen
                    btnActivate.setGraphic(ProgIcons.IMAGE_TABLE_ABO_OFF.getImageView());
                } else {
                    btnActivate.setGraphic(ProgIcons.IMAGE_TABLE_ABO_ON.getImageView());
                }
                btnActivate.setOnAction(a -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    ProgData.getInstance().aboList.setAboActive(aboData, !aboData.isActive());

                    getTableView().refresh();
                    getTableView().requestFocus();
                });

                final Button btnDel;
                btnDel = new Button("");
                btnDel.setTooltip(new Tooltip("Abo löschen"));
                btnDel.getStyleClass().addAll("btnFunction", "btnFuncTable");
                btnDel.setGraphic(ProgIcons.IMAGE_TABLE_ABO_DEL.getImageView());
                btnDel.setOnAction(a -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    AboListFactory.deleteAbo(aboData);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });

                if (ProgConfig.SYSTEM_SMALL_TABLE_ROW_ABO.get()) {
                    btnActivate.setMaxHeight(18);
                    btnActivate.setMinHeight(18);
                    btnDel.setMaxHeight(18);
                    btnDel.setMinHeight(18);
                }

                hbox.getChildren().addAll(btnActivate, btnDel/*, btnBookmark*/);
                setGraphic(hbox);
            }
        };
        return cell;
    };
}