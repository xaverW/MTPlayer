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

package de.p2tools.mtplayer.controller.data.setdata;

import java.util.ArrayList;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class SetDataList extends SetDataListWorker {

    public boolean removeSetData(Object obj) {
        // remove and notify
        boolean ret = super.remove(obj);
        setListChanged();
        return ret;
    }

    public boolean addSetData(SetData psetData) {
        //add and notify
        boolean play = false;
        for (final SetData sd : this) {
            if (sd.isPlay()) {
                play = true;
                break;
            }
        }
        if (play) {
            psetData.setPlay(false);
        }
        final boolean ret = super.add(psetData);
        setListChanged();
        return ret;
    }

    public boolean addSetData(SetDataList setDataList) {
        //add and notify
        if (setDataList == null || setDataList.isEmpty()) {
            return false;
        }

        boolean ret = true;
        for (final SetData setData : setDataList) {
            if (!addSetData(setData)) {
                ret = false;
            }
        }
        setListChanged();
        return ret;
    }

    public SetData getSetDataPlay() {
        //liefert die Programmgruppe zum Abspielen
        for (final SetData psetData : this) {
            if (psetData.isPlay()) {
                return psetData;
            }
        }
        return null;
    }

    public SetData getSetDataForAbo() {
        return getSetDataForAbo("");
    }

    public SetData getSetDataForAbo(String id) {
        // liefert mit dem SetNamen das passende Set zurück
        // wird nichts gefunden, wird das erste Set (der Abos) genommen

        if (isEmpty()) {
            return null;
        }

        if (size() == 1) {
            // gibt nur eins
            return this.get(0);
        }

        // das Set mit dem Namen
        for (final SetData pset : this) {
            if (pset.isAbo() && pset.getId().equals(id)) {
                return pset;
            }
        }

        // das erste Set der Abos
        for (final SetData pset : this) {
            if (pset.isAbo()) {
                return pset;
            }
        }

        // das erste Set der Downloads
        for (final SetData pset : this) {
            if (pset.isSave()) {
                // wenns keins gibt, wird das "ABO"
                pset.setAbo(true);
                return pset;
            }
        }

        // dann eben das erste Set
        return get(0);
    }

    public SetData getSetDataForDownloads() {
        return getSetDataForDownloads("");
    }

    public SetData getSetDataForDownloads(String id) {
        // liefert mit dem SetNamen das passende Set zurück
        // wird nichts gefunden, wird das erste Set (der Abos/Downloads) genommen

        if (isEmpty()) {
            return null;
        }

        if (size() == 1) {
            // gibt nur eins
            return this.get(0);
        }

        // das Set mit dem Namen
        for (final SetData pset : this) {
            if (pset.isSave() && pset.getId().equals(id)) {
                return pset;
            }
        }

        // das erste Set der Downloads
        for (final SetData pset : this) {
            if (pset.isSave()) {
                return pset;
            }
        }

        // das erste Set der Abos
        for (final SetData pset : this) {
            if (pset.isAbo()) {
                // wenns keins gibt, wird das "SAVE"
                pset.setSave(true);
                return pset;
            }
        }

        // dann eben das erste Set
        return get(0);
    }

    public SetDataList getSetDataListSave() {
        // liefert eine Liste Programmsets, die zum Speichern angelegt sind (ist meist nur eins)
        return stream().filter(SetDataProps::isSave)
                .collect(Collectors.toCollection(SetDataList::new));
    }

    public SetDataList getSetDataListButton() {
        // liefert eine Liste Programmsets, die als Button angelegt sind
        // "leere" Button werden nicht mehr angezeigt

        return stream()
                .filter(SetDataProps::isButton)
//                .filter(setData -> !setData.getProgramList().isEmpty()) // könnte auch Sets ohne Programm geben??
//                .filter(setData -> !setData.getVisibleName().isEmpty()) // kann es nicht mehr geben
                .collect(Collectors.toCollection(SetDataList::new));
    }

    public SetDataList getSetDataListAbo() {
        // liefert eine Liste Programmsets, die für Abos angelegt sind (ist meist nur eins)
        return stream().filter(SetDataProps::isAbo)
                .collect(Collectors.toCollection(SetDataList::new));
    }

    public void setPlay(SetData setData) {
        for (final SetData sData : this) {
            sData.setPlay(false);
        }
        setData.setPlay(true);
        setListChanged();
    }

    public SetData getSetDataWithId(String id) {
        //liefert das PSet mit ID oder null
        for (final SetData setData : this) {
            if (setData.getId().equals(id)) {
                return setData;
            }
        }

        return null;
    }

    public int up(int idx, boolean up) {
        final SetData prog = this.remove(idx);
        int newIdx = idx;
        if (up) {
            if (newIdx > 0) {
                --newIdx;
            }
        } else if (newIdx < size()) {
            ++newIdx;
        }
        this.add(newIdx, prog);
        setListChanged();
        return newIdx;
    }

    public ArrayList<String> getStringListSetData() {
        return stream().map(SetData::setDataToString).collect(Collectors.toCollection(ArrayList::new));
    }
}
