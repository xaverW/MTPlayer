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

package de.p2tools.mtplayer.controller.filmlist.filmlistUrls;

import de.p2tools.p2Lib.configFile.pData.PDataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class FilmlistUrlList extends SimpleListProperty<FilmlistUrlData> implements PDataList<FilmlistUrlData> {
    // ist die Liste mit den URLs zum Download einer Filmliste

    public String TAG = "FilmlistUrlList";

    public FilmlistUrlList() {
        super(FXCollections.observableArrayList());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    public String setTag(String TAG) {
        return this.TAG = TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller SetData";
    }

    @Override
    public FilmlistUrlData getNewItem() {
        return new FilmlistUrlData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(FilmlistUrlData.class)) {
            add((FilmlistUrlData) obj);
        }
    }

//    @Override
//    public boolean add(FilmlistUrlData addFilmlist) {
//        return addWithCheck(addFilmlist);
//    }

    public boolean addWithCheck(FilmlistUrlData addFilmlist) {
        for (FilmlistUrlData dataUrlFilmlist : this) {
            if (dataUrlFilmlist.getUrl().equals(addFilmlist.getUrl())) {
                return false;
            }
        }

//        addFilmlist.setUrl(addFilmlist.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_URL_NR]);
//        addFilmlist.setPrio(addFilmlist.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_PRIO_NR]);
//        addFilmlist.setKind(addFilmlist.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_SORT_NR]);
        return super.add(addFilmlist);
    }

    public ArrayList<String> getUrls() {
        ArrayList<String> ret = new ArrayList<>();
        this.stream().forEach(filmlistUrlData -> ret.add(filmlistUrlData.getUrl()));
        return ret;
    }

    public String getRand(ArrayList<String> alreadyUsed) {
        // Prio: momentan sind alle Listen gleich gewichtet
        if (this.isEmpty()) {
            return "";
        }

        LinkedList<FilmlistUrlData> listPrio = new LinkedList<>();

        // unbenutze URLs nach prio gewichten
        if (alreadyUsed != null) {
            for (FilmlistUrlData filmlistUrlData : this) {
                if (alreadyUsed.contains(filmlistUrlData.getUrl())) {
                    // wurde schon versucht
                    continue;
                }
                if (filmlistUrlData.getPrio().equals(FilmlistUrlData.FILMLIST_UPDATE_SERVER_PRIO_1)) {
                    listPrio.add(filmlistUrlData);
                    listPrio.add(filmlistUrlData);
                } else {
                    listPrio.add(filmlistUrlData);
                    listPrio.add(filmlistUrlData);
                    listPrio.add(filmlistUrlData);
                }
            }
        }

        FilmlistUrlData filmlistUrlData;
        if (!listPrio.isEmpty()) {
            int nr = new Random().nextInt(listPrio.size());
            filmlistUrlData = listPrio.get(nr);

        } else {
            // dann wird irgendeine Versucht
            int nr = new Random().nextInt(this.size());
            filmlistUrlData = this.get(nr);
        }

        return filmlistUrlData.getUrl();
    }

}
