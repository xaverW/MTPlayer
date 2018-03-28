/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

package de.mtplayer.mtp.gui.mediaDb;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

@SuppressWarnings("serial")
public class MediaPathList extends SimpleListProperty<MediaPathData> {

    public MediaPathList() {
        super(FXCollections.observableArrayList());
    }

    public boolean addSave(MediaPathData dmp) {
        if (contain(dmp)) {
            return false;
        }
        add(dmp);
        return true;
    }

    private boolean contain(MediaPathData dm) {
        for (final MediaPathData mediaPathData : this) {
            if (mediaPathData.getPath().equals(dm.getPath())) {
                return true;
            }
        }
        return false;
    }
}
