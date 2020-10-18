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

package de.p2tools.mtplayer.controller.data;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.io.File;

public final class ReplaceList extends SimpleListProperty<ReplaceData> {

    public ReplaceList() {
        super(FXCollections.observableArrayList());
    }

    public void init() {
        clear();
        add(new ReplaceData(" ", "_"));
        add(new ReplaceData("?", "_"));
    }

    public String replace(String strCheck, boolean path) {

        // hat der Nutzer als Suchbegriff "leer" eingegeben, dann weg damit
        this.removeIf(replaceData -> replaceData.getFrom().isEmpty());

        for (ReplaceData replaceData : this) {

            if (path && replaceData.getFrom().equals(File.separator)) {
                // bei Pfaden darf / oder \ natürlich nicht entfernt werden
                continue;
            } else {
                strCheck = strCheck.replace(replaceData.getFrom(), replaceData.getTo());
            }

        }

        return strCheck;
    }

    public int up(int idx, boolean up) {
        ReplaceData replace = remove(idx);
        int neu = idx;
        if (up) {
            if (neu > 0) {
                --neu;
            }
        } else if (neu < size()) {
            ++neu;
        }
        add(neu, replace);
        return neu;
    }

}
