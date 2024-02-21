/*
 * P2tools Copyright (C) 2021 W. Xaver W.Xaver[at]googlemail.com
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

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;


public class TableRowAbo<T> extends TableRow {


    public TableRowAbo() {
    }

    @Override
    public void updateItem(Object f, boolean empty) {
        super.updateItem(f, empty);

        AboData abo = (AboData) f;
        if (abo == null || empty) {
            setStyle("");
            setTooltip(null);

        } else {
            if (ProgConfig.ABO_GUI_SHOW_TABLE_TOOL_TIP.getValue()) {
                setTooltip(new Tooltip(
                        abo.getChannel().isEmpty() ? "" : "Titel: " + abo.getChannel() + "\n" +
                                (abo.getTheme().isEmpty() ? "" : "Thema: " + abo.getTheme() + "\n") +
                                (abo.getThemeTitle().isEmpty() ? "" : "Thema oder Titel: " + abo.getThemeTitle() + "\n") +
                                (abo.getTitle().isEmpty() ? "" : "Titel: " + abo.getTitle())));
            }
            if (!abo.isActive()) {
                setStyle(ProgColorList.ABO_SWITCHED_OFF.getCssBackground());
            } else {
                setStyle("");
            }
        }
    }
}
