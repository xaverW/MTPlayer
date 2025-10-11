/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.data.offer;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;


/**
 * das style der box ist: "checkbox-table"
 */
public class PCellCheckBoxOfferData<S, T> extends TableCell<S, T> {

    public Callback<TableColumn<OfferData, Boolean>, TableCell<OfferData, Boolean>> cellFactory
            = (final TableColumn<OfferData, Boolean> param) -> new TableCell<>() {

        @Override
        public void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setGraphic(null);
                setText(null);
                return;
            }

            setAlignment(Pos.CENTER);
            CheckBox box = new CheckBox();
            box.setMaxHeight(6);
            box.setMinHeight(6);
            box.setPrefSize(6, 6);
            box.getStyleClass().add("checkbox-table");
            setGraphic(box);

            box.setSelected(item);
            box.setOnAction(a -> {
                OfferData offerData = getTableView().getItems().get(getIndex());
                offerData.setActive(!item);
            });
        }
    };
}
