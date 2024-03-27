/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.livesearchard;

import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.livesearch.LiveFactory;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

import java.util.ArrayList;

public class JsonInfoDtoArd {
    public static int PAGE_SIZE = 20;

    private String searchString;
    private int pageNo;

    private String startUrl = "";
    private String hitUrl = "";
    private int hitNo = 0;
    private LongProperty sizeOverAll = new SimpleLongProperty(0);

    private FilmDataMTP filmDataMTP = null;
    private ArrayList<FilmDataMTP> list = new ArrayList<>();


    public JsonInfoDtoArd() {
    }

    public void init() {
        searchString = "";
        LiveFactory.progressPropertyARD.setValue(LiveFactory.PROGRESS_NULL);
        pageNo = 0;

        startUrl = "";
        hitUrl = "";
        hitNo = 0;
        sizeOverAll.set(0);

        FilmDataMTP filmDataMTP = null;
        list.clear();
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public String getHitUrl() {
        return hitUrl;
    }

    public void setHitUrl(String hitUrl) {
        this.hitUrl = hitUrl;
    }

    public int getHitNo() {
        return hitNo;
    }

    public void setHitNo(int hitNo) {
        this.hitNo = hitNo;
    }

    public LongProperty getSizeOverAll() {
        return sizeOverAll;
    }

    public void setSizeOverAll(long sizeOverAll) {
        this.sizeOverAll.set(sizeOverAll);
    }

    public FilmDataMTP getFilmDataMTP() {
        return filmDataMTP;
    }

    public void setFilmDataMTP(FilmDataMTP filmDataMTP) {
        this.filmDataMTP = filmDataMTP;
    }

    public ArrayList<FilmDataMTP> getList() {
        return list;
    }

    public void setList(ArrayList<FilmDataMTP> list) {
        this.list = list;
    }
}
