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

package de.p2tools.mtplayer.controller.data.abo;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.p2lib.configfile.pdata.P2DataList;
import de.p2tools.p2lib.tools.GermanStringSorter;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class AboList extends SimpleListProperty<AboData> implements P2DataList<AboData> {
    private final ProgData progData;
    public static final String TAG = "AboList";
    private int no;
    private static final GermanStringSorter sorter = GermanStringSorter.getInstance();
    private final BooleanProperty listChanged = new SimpleBooleanProperty(true);
    private final ObservableList<AboData> undoList = FXCollections.observableArrayList();

    public AboList(ProgData progData) {
        super(FXCollections.observableArrayList());
        this.progData = progData;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller Abos";
    }

    @Override
    public AboData getNewItem() {
        return new AboData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(AboData.class)) {
            add((AboData) obj);
        }
    }

    public BooleanProperty listChangedProperty() {
        return listChanged;
    }

    public synchronized void initAboList() {
        this.forEach(abo -> abo.initAbo(progData, ++no));
    }

    public synchronized void sortAlphabetically() {
        sort(Comparator.comparing(o -> o.getName().toLowerCase(Locale.ROOT)));
    }

    public synchronized void addAbo(AboData abo) {
        // die Änderung an der Liste wird nicht gemeldet!!
        // für Import MediathekView, Dialog neues Abo anlegen
        ++no;
        abo.setNo(no);

        if (abo.getName().isEmpty()) {
            // Downloads ohne "Aboname" sind manuelle Downloads
            abo.setName("Abo_" + no);
        }
        if (abo.getResolution().isEmpty()) {
            abo.setResolution(FilmDataMTP.RESOLUTION_NORMAL);
        }
        super.add(abo);
    }

    public synchronized void setAboActive(List<AboData> lAbo, boolean on) {
        // Menü
        if (!lAbo.isEmpty()) {
            lAbo.forEach(abo -> abo.setActive(on));
            notifyChanges();
        }
    }

    public synchronized void setAboActive(AboData abo, boolean on) {
        // Menü/Button
        abo.setActive(on);
        notifyChanges();
    }

    public synchronized void deleteAbo(ObservableList<AboData> lAbo) {
        // dann soll das Abo gelöscht werden, Menü/Button
        P2Log.sysLog("Abo löschen");
        addAbosToUndoList(lAbo); // erst eintragen, dann löschen - selList ändert sich dann
        this.removeAll(lAbo);
        notifyChanges();
    }

    public synchronized void notifyChanges() {
        // Menü/Button/Dialog
        if (!ProgData.FILMLIST_IS_DOWNLOADING.get() &&
                !ProgData.AUDIOLIST_IS_DOWNLOADING.get()) {
            // wird danach eh gemacht
            AboFactory.setAboForList();
        }
        listChanged.setValue(!listChanged.get());
    }

    public synchronized ArrayList<String> getAboSubDirList() {
        // liefert ein Array mit allen Pfaden
        final ArrayList<String> path = new ArrayList<>();
        for (final AboData abo : this) {
            final String s = abo.getAboSubDir();
            if (!path.contains(s)) {
                path.add(abo.getAboSubDir());
            }
        }
        final GermanStringSorter sorter = GermanStringSorter.getInstance();
        path.sort(sorter);
        return path;
    }

    public synchronized ArrayList<String> getAboDirList() {
        // liefert ein Array mit allen Pfaden
        final ArrayList<String> path = new ArrayList<>();
        for (final AboData abo : this) {
            final String s = abo.getAboDir();
            if (!path.contains(s)) {
                path.add(abo.getAboDir());
            }
        }
        final GermanStringSorter sorter = GermanStringSorter.getInstance();
        path.sort(sorter);
        return path;
    }

    public synchronized ArrayList<String> getAboFileNameList() {
        // liefert ein Array mit allen Pfaden
        final ArrayList<String> fileName = new ArrayList<>();
        for (final AboData abo : this) {
            final String s = abo.getAboFileName();
            if (!fileName.contains(s)) {
                fileName.add(abo.getAboFileName());
            }
        }
        final GermanStringSorter sorter = GermanStringSorter.getInstance();
        fileName.sort(sorter);
        return fileName;
    }

    public synchronized ArrayList<String> getAboChannelList() {
        // liefert ein Array mit allen Sendern
        final ArrayList<String> sender = new ArrayList<>();
        sender.add("");
        for (final AboData abo : this) {

            final String s = abo.getChannel();

            List<String> channelFilterList = new ArrayList<>();
            String channelFilter = abo.getChannel();
            if (channelFilter != null) {
                if (channelFilter.contains(",")) {
                    channelFilterList.addAll(Arrays.asList(channelFilter.replace(" ", "").split(",")));
                } else {
                    channelFilterList.add(channelFilter);
                }
                channelFilterList.stream().forEach(st -> st = st.trim());
            }
            for (String sf : channelFilterList) {
                if (sf.isEmpty()) {
                    continue;
                }
                if (!sender.contains(sf)) {
                    sender.add(sf);
                }
            }
        }

        sender.sort(sorter);
        return sender;
    }

    public synchronized ArrayList<String> getAboNameList() {
        // liefert ein Array mit allen AboNamen
        final ArrayList<String> name = new ArrayList<>();
        name.add("");
        for (final AboData abo : this) {
            final String s = abo.getName();
            if (!name.contains(s)) {
                name.add(abo.getName());
            }
        }
        name.sort(sorter);
        return name;
    }

    public ObservableList<AboData> getUndoList() {
        return undoList;
    }

    public synchronized void addAbosToUndoList(List<AboData> list) {
        undoList.clear();
        undoList.addAll(list);
    }

    public synchronized void undoAbos() {
        // Menü/Button
        if (undoList.isEmpty()) {
            return;
        }
        addAll(undoList);
        undoList.clear();
        notifyChanges();
    }
}
