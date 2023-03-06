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
import de.p2tools.mtplayer.controller.filmfilter.BlacklistFilterFactory;
import de.p2tools.p2lib.configfile.pdata.PDataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@SuppressWarnings("serial")
public class BlackList extends SimpleListProperty<BlackData> implements PDataList<BlackData> {

    public String TAG = "BlackList";
    private int no = 1;
    private final ProgData progData;

    public BlackList(ProgData progData, String tag) {
        super(FXCollections.observableArrayList());
        this.progData = progData;
        this.TAG = tag;
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
        b.setNo(no++);
        return super.add(b);
    }

    public synchronized boolean addAndNotify(BlackData b) {
        //add durch Button
        b.setNo(no++);
        final boolean ret = super.add(b);
        new Thread(() -> BlacklistFilterFactory.markFilmBlack(true)).start();
        return ret;
    }

    public synchronized void clearCounter() {
        for (final BlackData blackData : this) {
            blackData.clearCounter();
        }
    }

    public synchronized void sortAndCleanTheList() {
        //mit den bestehenden Treffern sortieren
        Collections.sort(this, Comparator.comparingInt(BlackDataProps::getCountHits).reversed());

        //zum Schluss noch neu nummerieren 1, 2, ...
        cleanTheList();
    }

    public synchronized void cleanTheList() {
        ArrayList blackList = new ArrayList();

        this.stream().forEach(bl -> {
            if (!BlackListFactory.blackIsEmpty(bl) &&
                    !BlackListFactory.blackExistsAlready(bl, blackList)) {
                blackList.add(bl);
            }
        });

        this.setAll(blackList);

        //zum Schluss noch neu nummerieren 1, 2, ...
        no = 1;
        for (BlackData blackData : this) {
            blackData.setNo(no++);
        }
    }
}
