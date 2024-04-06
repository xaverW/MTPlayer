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

import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.date.P2LDateTimeFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CellDownloadStartTime<S, T> extends TableCell<S, T> {

    public final Callback<TableColumn<DownloadData, String>, TableCell<DownloadData, String>> cellFactory
            = (final TableColumn<DownloadData, String> param) -> {

        final TableCell<DownloadData, String> cell = new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                if (!item.isEmpty()) {
                    LocalDateTime ldt = P2LDateTimeFactory.fromString(item);
                    if (ldt.equals(LocalDateTime.MIN)) {
                        setText("");

                    } else {
                        final LocalDateTime ldtTomorrow = LocalDate.now().atStartOfDay().plusDays(1);
                        if (ldt.isEqual(ldtTomorrow) || ldt.isAfter(ldtTomorrow)) {
                            // dann morgen
                            setText("Morgen, " + ldt.format(P2DateConst.DT_FORMATTER_HH__mm));
                        } else {
                            // dann heute noch
                            setText(ldt.format(P2DateConst.DT_FORMATTER_HH__mm));
                        }
                    }
                }
            }
        };
        return cell;
    };
}