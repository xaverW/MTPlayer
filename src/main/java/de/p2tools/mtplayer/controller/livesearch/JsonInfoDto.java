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

import de.p2tools.mtplayer.controller.film.FilmDataMTP;

import java.util.ArrayList;

public class JsonInfoDto {
    private String startUrl = "";
    private String hitUrl = "";
    private int hitNo = 0;

    private FilmDataMTP filmDataMTP = null;
    private ArrayList<FilmDataMTP> list = new ArrayList<>();

    public JsonInfoDto() {
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
