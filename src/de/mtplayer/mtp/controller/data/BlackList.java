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

package de.mtplayer.mtp.controller.data;

import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.gui.tools.Listener;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

@SuppressWarnings("serial")
public class BlackList extends SimpleListProperty<BlackData> {

    private int nr = 0;
    private final Daten daten;

    public BlackList(Daten daten) {
        super(FXCollections.observableArrayList());
        this.daten = daten;
    }

    public synchronized boolean add(BlackData b) {
        b.setNr(nr++);
        return super.add(b);
    }

    public synchronized boolean addAndNotify(BlackData b) {
        b.setNr(nr++);
        final boolean ret = super.add(b);
        filterListAndNotifyListeners();
        return ret;
    }

    public synchronized boolean removeAndNotify(Object b) {
        final boolean ret = super.remove(b);
        filterListAndNotifyListeners();
        return ret;
    }

    public synchronized void clearAndNotify() {
        super.clear();
        filterListAndNotifyListeners();
    }

    public synchronized void filterListAndNotifyListeners() {
        daten.filmList.filterList();
        Listener.notify(Listener.EREIGNIS_BLACKLIST_GEAENDERT, BlackList.class.getSimpleName());
    }

}
