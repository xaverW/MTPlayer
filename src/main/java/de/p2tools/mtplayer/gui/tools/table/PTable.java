/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
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

import de.p2tools.p2lib.configfile.pdata.P2Data;
import javafx.scene.control.TableView;

public class PTable<E extends P2Data> extends TableView<E> {
    Table.TABLE_ENUM table_enum;

    public PTable(Table.TABLE_ENUM table_enum) {
        super();
        this.table_enum = table_enum;
    }

    public Table.TABLE_ENUM getETable() {
        return table_enum;
    }

}
