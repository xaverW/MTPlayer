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

import de.p2tools.mtplayer.controller.config.SizeTools;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class CellDownloadBandwidth<S, T> extends TableCell<S, T> {

    public final Callback<TableColumn<DownloadData, Long>, TableCell<DownloadData, Long>> cellFactory
            = (final TableColumn<DownloadData, Long> param) -> {

        final TableCell<DownloadData, Long> cell = new TableCell<>() {

            @Override
            public void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                if (item == 0) {
                    setText("");
                } else if (item < 0) {
                    // dann ist es der Durchschnitt
                    setText("Ø " + SizeTools.humanReadableByteCount(-1 * item, true));
                } else {
                    setText(SizeTools.humanReadableByteCount(item, true));
                }
            }
        };
        return cell;
    };
}
