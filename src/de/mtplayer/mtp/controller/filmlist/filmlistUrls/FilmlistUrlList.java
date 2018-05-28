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

package de.mtplayer.mtp.controller.filmlist.filmlistUrls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

@SuppressWarnings("serial")
public class FilmlistUrlList extends LinkedList<FilmlistUrlData> {
    // ist die Liste mit den URLs zum Download einer Filmliste
    public boolean addWithCheck(FilmlistUrlData filmlist) {
        for (FilmlistUrlData dataUrlFilmlist : this) {
            if (dataUrlFilmlist.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_URL_NR]
                    .equals(filmlist.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_URL_NR])) {
                return false;
            }
        }
        return add(filmlist);
    }

    public void sort() {
        int nr = 0;
        Collections.sort(this);
        for (FilmlistUrlData dataUrlFilmlist : this) {
            String str = String.valueOf(nr++);
            while (str.length() < 3) {
                str = "0" + str;
            }
            dataUrlFilmlist.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_NR_NR] = str;
        }
    }

    public ArrayList<String> getUrls() {
        ArrayList<String> ret = new ArrayList<>();
        this.stream().forEach(filmlistUrlData -> ret.add(filmlistUrlData.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_URL_NR]));
        return ret;
    }

    public String getRand(ArrayList<String> alreadyUsed) {
        // gibt nur noch akt.xml und diff.xml und da sind alle Listen
        // aktuell, Prio: momentan sind alle Listen gleich gewichtet
        if (this.isEmpty()) {
            return "";
        }

        LinkedList<FilmlistUrlData> listPrio = new LinkedList<>();
        //nach prio gewichten
        for (FilmlistUrlData filmlistUrlData : this) {
            if (alreadyUsed != null) {
                if (alreadyUsed.contains(filmlistUrlData.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_URL_NR])) {
                    // wurde schon versucht
                    continue;
                }
                if (filmlistUrlData.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_PRIO_NR].equals(FilmlistUrlData.FILMLIST_UPDATE_SERVER_PRIO_1)) {
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
        return filmlistUrlData.arr[FilmlistUrlData.FILMLIST_UPDATE_SERVER_URL_NR];
    }

}
