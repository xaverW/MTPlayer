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

package de.p2tools.mtplayer.controller.filmlist.filmlistUrls;

import de.p2tools.p2Lib.configFile.config.Config;
import de.p2tools.p2Lib.configFile.config.ConfigIntPropExtra;
import de.p2tools.p2Lib.configFile.config.ConfigStringPropExtra;
import de.p2tools.p2Lib.configFile.pData.PDataSample;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class FilmlistUrlData extends PDataSample<FilmlistUrlData> {

    public static final String TAG = "FilmlistUrlData";

    public static final String SERVER_ART_AKT = "akt";
    public static final String SERVER_ART_DIFF = "diff";

    public static final String FILMLIST_UPDATE_SERVER_PRIO_1 = "1";
    public static final String FILMLIST_UPDATE_SERVER = "filmlist-update-server";

    public static final String FILMLIST_UPDATE_SERVER_NR = "filmlist-update-server-nr";
    public static final int FILMLIST_UPDATE_SERVER_NR_NR = 0;
    public static final String FILMLIST_UPDATE_SERVER_URL = "filmlist-update-server-url";
    public static final int FILMLIST_UPDATE_SERVER_URL_NR = 1;
    public static final String FILMLIST_UPDATE_SERVER_PRIO = "filmlist-update-server-prio";
    public static final int FILMLIST_UPDATE_SERVER_PRIO_NR = 2;
    public static final String FILMLIST_UPDATE_SERVER_SORT = "filmlist-update-server-art";
    public static final int FILMLIST_UPDATE_SERVER_SORT_NR = 3;
    public static final int FILMLIST_UPDATE_SERVER_MAX_ELEM = 4;

    public static final String[] FILMLIST_UPDATE_SERVER_COLUMN_NAMES = {FILMLIST_UPDATE_SERVER_NR, FILMLIST_UPDATE_SERVER_URL,
            FILMLIST_UPDATE_SERVER_PRIO, FILMLIST_UPDATE_SERVER_SORT};

    private IntegerProperty no = new SimpleIntegerProperty(0);
    private StringProperty url = new SimpleStringProperty("");
    private StringProperty prio = new SimpleStringProperty("");
    private StringProperty kind = new SimpleStringProperty("");


    public String[] arr;

    public FilmlistUrlData() {
        makeArr();
    }

    FilmlistUrlData(String url, String prio, String sort) {
        makeArr();
        arr[FILMLIST_UPDATE_SERVER_URL_NR] = url;
        arr[FILMLIST_UPDATE_SERVER_PRIO_NR] = prio;
        arr[FILMLIST_UPDATE_SERVER_SORT_NR] = sort;
        setUrl(url);
        setPrio(prio);
        setKind(sort);
    }

    FilmlistUrlData(String url, String sort) {
        makeArr();
        arr[FILMLIST_UPDATE_SERVER_URL_NR] = url;
        arr[FILMLIST_UPDATE_SERVER_PRIO_NR] = FilmlistUrlData.FILMLIST_UPDATE_SERVER_PRIO_1;
        arr[FILMLIST_UPDATE_SERVER_SORT_NR] = sort;
        setUrl(url);
        setPrio(FilmlistUrlData.FILMLIST_UPDATE_SERVER_PRIO_1);
        setKind(sort);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "FilmlistUrlData";
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new ConfigIntPropExtra("no", "filmlist-update-server-nr", no));
        list.add(new ConfigStringPropExtra("url", "filmlist-update-server-url", url));
        list.add(new ConfigStringPropExtra("prio", "filmlist-update-server-prio", prio));
        list.add(new ConfigStringPropExtra("kind", "filmlist-update-server-kind", kind));

        return list.toArray(new Config[]{});
    }

    public int getNo() {
        return no.get();
    }

    public IntegerProperty noProperty() {
        return no;
    }

    public void setNo(int no) {
        this.no.set(no);
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public String getPrio() {
        return prio.get();
    }

    public StringProperty prioProperty() {
        return prio;
    }

    public void setPrio(String prio) {
        this.prio.set(prio);
    }

    public String getKind() {
        return kind.get();
    }

    public StringProperty kindProperty() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind.set(kind);
    }

    @Override
    public int compareTo(FilmlistUrlData arg0) {
        int ret = 0;
        try {
            return arr[FILMLIST_UPDATE_SERVER_URL_NR].compareTo(arg0.arr[FILMLIST_UPDATE_SERVER_URL_NR]);
        } catch (Exception ex) {
            PLog.errorLog(936542876, ex);
        }
        return ret;
    }

    private void makeArr() {
        arr = new String[FILMLIST_UPDATE_SERVER_MAX_ELEM];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
    }
}
