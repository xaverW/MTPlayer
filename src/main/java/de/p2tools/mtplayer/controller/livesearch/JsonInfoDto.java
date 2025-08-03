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


package de.p2tools.mtplayer.controller.livesearch;

import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import javafx.beans.property.*;

import java.util.ArrayList;

public class JsonInfoDto {
    public static int PAGE_SIZE = 20;

    private String searchString;
    private String startUrl = "";
    private final StringProperty nextUrl = new SimpleStringProperty("");
    private final StringProperty zdfNextCursor = new SimpleStringProperty("");

    private String ardFilmId = "";
    private final LongProperty sizeOverAll = new SimpleLongProperty(0);
    private final IntegerProperty pageNo = new SimpleIntegerProperty(0);
    private ArrayList<FilmDataMTP> list = new ArrayList<>();

    private String api = "";

    public JsonInfoDto() {
    }

    public void init() {
        searchString = "";
        api = "";

        startUrl = "";
        zdfNextCursor.set("");
        nextUrl.set("");
        ardFilmId = "";
        sizeOverAll.set(0);
        pageNo.set(0);
        list.clear();
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public String getNextUrl() {
        return nextUrl.get();
    }

    public StringProperty nextUrlProperty() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl.set(nextUrl);
    }

    public String getZdfNextCursor() {
        return zdfNextCursor.get();
    }

    public void setZdfNextCursor(String set) {
        zdfNextCursor.set(set);
    }

    public StringProperty zdfNextCursorProperty() {
        return zdfNextCursor;
    }

    public String getArdFilmId() {
        return ardFilmId;
    }

    public void setArdFilmId(String ardFilmId) {
        this.ardFilmId = ardFilmId;
    }

    public long getSizeOverAll() {
        return sizeOverAll.get();
    }

    public LongProperty sizeOverAllProperty() {
        return sizeOverAll;
    }

    public void setSizeOverAll(long sizeOverAll) {
        this.sizeOverAll.set(sizeOverAll);
    }

    public int getPageNo() {
        return pageNo.get();
    }

    public IntegerProperty pageNoProperty() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo.set(pageNo);
    }

    public ArrayList<FilmDataMTP> getList() {
        return list;
    }

    public void setList(ArrayList<FilmDataMTP> list) {
        this.list = list;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }
}
