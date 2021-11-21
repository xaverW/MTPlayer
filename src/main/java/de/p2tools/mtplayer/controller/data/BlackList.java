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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.mtplayer.tools.filmListFilter.FilmlistBlackFilterCountHits;
import de.p2tools.p2Lib.configFile.pData.PDataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.Collections;
import java.util.Comparator;

@SuppressWarnings("serial")
public class BlackList extends SimpleListProperty<BlackData> implements PDataList<BlackData> {

    public static final String TAG = "BlackList";
    private int nr = 0;
    private final ProgData progData;

    public BlackList(ProgData progData) {
        super(FXCollections.observableArrayList());
        this.progData = progData;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller BlackData";
    }

    @Override
    public BlackData getNewItem() {
        return new BlackData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(BlackData.class)) {
            add((BlackData) obj);
        }
    }


    @Override
    public synchronized boolean add(BlackData b) {
        b.setNo(nr++);
        return super.add(b);
    }

    public synchronized boolean addAndNotify(BlackData b) {
        b.setNo(nr++);
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
        progData.filmlist.filterListWithBlacklist(true);
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
            Collections.sort(this, Comparator.comparingInt(BlackDataProps::getCountHits).reversed());

            // dann die tatsächlichen Trefferzahlen ermitteln
            FilmlistBlackFilterCountHits.countHits(true);
        }

        // und dann endgültig sortieren
        Collections.sort(this, Comparator.comparingInt(BlackDataProps::getCountHits).reversed());

        // zum Schluss noch neu nummerieren 1, 2, ...
        int i = 0;
        for (BlackData blackData : this) {
            blackData.setNo(++i);
        }
    }

}
