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

package de.mtplayer.mtp.controller.data;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.tools.filmListFilter.FilmlistBlackFilterCountHits;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.Collections;
import java.util.Comparator;

@SuppressWarnings("serial")
public class BlackList extends SimpleListProperty<BlackData> {

    private int nr = 0;
    private final ProgData progData;

    public BlackList(ProgData progData) {
        super(FXCollections.observableArrayList());
        this.progData = progData;
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
        progData.filmlist.filterList();
        Listener.notify(Listener.EREIGNIS_BLACKLIST_GEAENDERT, BlackList.class.getSimpleName());
    }

    public synchronized void clearCounter() {
        for (final BlackData blackData : this) {
            blackData.setCountHits(0);
        }
    }

    public synchronized void sortIncCounter(boolean searchHitsBefore) {
        if (searchHitsBefore) {
            // zuerst ohne Abbruch Treffer suchen
            FilmlistBlackFilterCountHits.countHits(false);

            // und dann sortieren
            Collections.sort(this, Comparator.comparingInt(BlackProps::getCountHits).reversed());

            // dann die tatsächlichen Trefferzahlen ermitteln
            FilmlistBlackFilterCountHits.countHits(true);
        }

        // und dann endgültig sortieren
        Collections.sort(this, Comparator.comparingInt(BlackProps::getCountHits).reversed());

        // zum Schluss noch neu nummerieren 1, 2, ...
        int i = 0;
        for (BlackData blackData : this) {
            blackData.setNr(++i);
        }
    }

}
