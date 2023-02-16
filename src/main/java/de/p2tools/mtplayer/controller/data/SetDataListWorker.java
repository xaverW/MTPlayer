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

import de.p2tools.p2Lib.configFile.pData.PDataList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.Collection;

@SuppressWarnings("serial")
public class SetDataListWorker extends SimpleListProperty<SetData> implements PDataList<SetData> {
    // Liste aller Programmsets
    public static final String TAG = "SetDataList";
//    public static final String PATTERN_PATH_DEST = "ZIELPFAD";
//    public static final String PATTERN_PATH_VLC = "PFAD_VLC";
//    public static final String PATTERN_PATH_FLV = "PFAD_FLVSTREAMER";
//    public static final String PATTERN_PATH_FFMPEG = "PFAD_FFMPEG";
//    public static final String PATTERN_PATH_SCRIPT = "PFAD_SCRIPT";
//    public String version = "";

    private BooleanProperty listChanged = new SimpleBooleanProperty(true);

    public SetDataListWorker() {
        super(FXCollections.observableArrayList());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller SetData";
    }

    @Override
    public SetData getNewItem() {
        return new SetData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(SetData.class)) {
            add((SetData) obj);
        }
    }

    public boolean isListChanged() {
        return listChanged.get();
    }

    public BooleanProperty listChangedProperty() {
        return listChanged;
    }

    public void setListChanged() {
        this.listChanged.set(!listChanged.get());
    }

    @Override
    public boolean add(SetData setData) {
        checkSetDataName(setData);
        return super.add(setData);
    }

    @Override
    public boolean addAll(Collection<? extends SetData> elements) {
        elements.stream().forEach(sd -> checkSetDataName(sd));
        return super.addAll(elements);
    }

    @Override
    public boolean addAll(int i, Collection<? extends SetData> elements) {
        elements.stream().forEach(sd -> checkSetDataName(sd));
        return super.addAll(i, elements);
    }

    void checkSetDataName(SetData setData) {
        // Id auf einmaligkeit prüfen und leere visibleNames füllen
        // dient der geänderten Funktion -> SetData im Abo
        String setDataId = setData.getId();
        boolean found = false;
        int id = 0;

        do {

            for (SetData sd : this) {
                if (sd.getId().equals(setDataId)) {
                    found = true;
                    setDataId = setData.getId() + "-" + ++id;
                    break;
                }
                found = false;
            }

        } while (found);

        setData.setId(setDataId);
        if (setData.getVisibleName().isEmpty()) {
            setData.setVisibleName(setData.getId());
        }
    }
}
