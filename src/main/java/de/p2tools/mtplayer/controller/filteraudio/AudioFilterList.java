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

package de.p2tools.mtplayer.controller.filteraudio;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.configfile.pdata.P2DataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public final class AudioFilterList extends SimpleListProperty<AudioFilter> implements P2DataList<AudioFilter> {
    public String TAG = "SelectedFilterList";

    public AudioFilterList() {
        super(FXCollections.observableArrayList());
    }

    public AudioFilterList(String tag) {
        super(FXCollections.observableArrayList());
        TAG = tag;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller Filter";
    }

    @Override
    public AudioFilter getNewItem() {
        return new AudioFilter();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(AudioFilter.class)) {
            add((AudioFilter) obj);
        }
    }

    public AudioFilter getSameFilter(AudioFilter filmFilter) {
        for (AudioFilter f : this) {
            if (f.isSame(filmFilter)) {
                return f;
            }
        }
        return null;
    }

    public void addToList(AudioFilter e) {
        cleanBackForward();
        super.add(e);
    }

    private void cleanBackForward() {
        while (this.size() > ProgConst.MAX_FILTER_GO_BACK) {
            remove(0);
        }
    }

    public int top(int idx, boolean up) {
        AudioFilter filmFilter = remove(idx);
        int ret;
        if (up) {
            add(0, filmFilter);
            ret = 0;
        } else {
            add(filmFilter);
            ret = getSize() - 1;
        }
        return ret;
    }

    public int up(int idx, boolean up) {
        AudioFilter filmFilter = remove(idx);
        int neu = idx;
        if (up) {
            if (neu > 0) {
                --neu;
            }
        } else if (neu < size()) {
            ++neu;
        }
        add(neu, filmFilter);
        return neu;
    }

    public void addNewStoredFilter(String name) {
        // einen neuen Filter zu den gespeicherten hinzufügen
        final AudioFilter sf = new AudioFilter();
        ProgData.getInstance().audioFilterWorker.getActFilterSettings().copyTo(sf);
        sf.setName(name.isEmpty() ? getNextName() : name);
        add(sf);
    }

    public String getNextName() {
        String ret = "";
        int id = 1;
        boolean found = false;
        while (!found) {
            final String name = "Filter " + id;
            if (stream().noneMatch(f -> name.equalsIgnoreCase(f.getName()))) {
                ret = name;
                found = true;
            }
            ++id;
        }
        return ret;
    }

    public boolean removeStoredFilter(AudioFilter sf) {
        // delete stored filter
        if (sf == null) {
            return false;
        }

        if (P2Alert.showAlertOkCancel("Löschen", "Filterprofil löschen",
                "Soll das Filterprofil: " +
                        sf.getName() + "\n" +
                        "gelöscht werden?")) {
            remove(sf);
            return true;
        }
        return false;
    }

    public void removeAllStoredFilter() {
        // delete all stored Filter
        if (P2Alert.showAlertOkCancel("Löschen", "Filterprofile löschen",
                "Sollen alle Filterprofile gelöscht werden?")) {
            clear();
        }
    }

    public void saveStoredFilter(AudioFilter sf) {
        // gesicherten Filter mit den aktuellen Einstellungen überschreiben
        if (sf == null) {
            return;
        }

        final String name = sf.getName();
        ProgData.getInstance().audioFilterWorker.getActFilterSettings().copyTo(sf);
        sf.setName(name);
    }
}
