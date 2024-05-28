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

package de.p2tools.mtplayer.controller.film;

import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.p2lib.mtfilm.film.FilmData;

public class FilmDataMTP extends FilmData {

    private AboData abo = null; //dann gibts ein Abo ABER: es kann ausgeschaltet, Film zu klein, ... sein!!
    private boolean wasHere = false; //markiert die Filme VOR dem selektierten Film
    private String buttonDummy = "";

    @Override
    public FilmDataMTP getCopy() {
        final FilmDataMTP ret = new FilmDataMTP();
        System.arraycopy(arr, 0, ret.arr, 0, arr.length);
        ret.filmDate = filmDate;
        ret.no = no;
        ret.filmSize = filmSize;
        ret.setDurationMinute(getDurationMinute());
        ret.abo = abo;
        ret.setHd(isHd());
        ret.setSmall(isSmall());
        ret.setUt(isUt());
        return ret;
    }

//    @Override
//    public void init() {
//        super.init();
////        setShown(ProgData.getInstance().historyList.checkIfUrlAlreadyIn(getUrlHistory()));
//    }

    public synchronized AboData getAbo() {
        return abo;
    }

    public synchronized void setAbo(AboData abo) {
        this.abo = abo;
    }

    public boolean isWasHere() {
        return wasHere;
    }

    public void setWasHere(boolean wasHere) {
        this.wasHere = wasHere;
    }

    public String getButtonDummy() {
        return buttonDummy;
    }
}
