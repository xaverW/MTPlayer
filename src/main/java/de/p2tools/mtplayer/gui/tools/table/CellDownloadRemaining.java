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

import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class CellDownloadRemaining<S, T> extends TableCell<S, T> {

    public final Callback<TableColumn<DownloadData, Integer>, TableCell<DownloadData, Integer>> cellFactory
            = (final TableColumn<DownloadData, Integer> param) -> {

        final TableCell<DownloadData, Integer> cell = new TableCell<>() {

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                if (item == DownloadConstants.REMAINING_NOT_STARTET) {
                    // dann ist er noch nicht gestartet
                    setText("");

                } else if (item < 0) {
                    // dann ist er fertig
                    final int dauer = item / 60;
                    if (dauer == 0) {
                        setText("Dauer: " + -1 * item + " s");
                    } else {
                        setText("Dauer: " + -1 * dauer + " Min");
                    }

                } else {
                    // dann läuft er noch
                    setText(DownloadConstants.getTextTimeLeft(item));
                }
            }
        };
        return cell;
    };
}