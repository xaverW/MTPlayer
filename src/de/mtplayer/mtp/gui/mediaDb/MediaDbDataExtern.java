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

package de.mtplayer.mtp.gui.mediaDb;

import de.mtplayer.mLib.tools.Data;
import de.mtplayer.mtp.controller.config.Const;

public class MediaDbDataExtern extends Data<MediaDbDataExtern> {

    public final static int MEDIA_DB_COLLECTION_NAME = 0;
    public final static int MEDIA_DB_PATH = 1;
    public final static int MEDIA_DB_COUNT = 2;

    public final static int MAX_ELEM = 3;
    public final static String[] COLUMN_NAMES = {"Name", "Pfad", "Anzahl"};
    public final static String[] XML_NAMES = {"Name", "Pfad", "Anzahl"};
    public static final String TAG = "MediensammlungExtern";

    public String[] arr;

    public MediaDbDataExtern(String name, String pfad) {
        makeArr();
        arr[MEDIA_DB_COLLECTION_NAME] = putzen(name);
        arr[MEDIA_DB_PATH] = putzen(pfad);
        arr[MEDIA_DB_COUNT] = "0";
    }

    public String getCollectionName() {
        return arr[MEDIA_DB_COLLECTION_NAME];
    }

    public String getPath() {
        return arr[MEDIA_DB_PATH];
    }

    public int getCount() {
        int ret;
        try {
            ret = Integer.parseInt(arr[MEDIA_DB_COUNT]);
        } catch (Exception ignore) {
            ret = 0;
        }
        return ret;
    }

    public void setCount(int size) {
        arr[MEDIA_DB_COUNT] = size + "";
    }

    public boolean equal(MediaDbData m) {
        return m.arr[MediaDbData.MEDIA_DB_COLLECTION].equals(arr[MEDIA_DB_COLLECTION_NAME]);
    }

    public boolean equal(MediaDbDataExtern m) {
        return m.arr[MEDIA_DB_COLLECTION_NAME].equals(arr[MEDIA_DB_COLLECTION_NAME])
                && m.arr[MEDIA_DB_PATH].equals(arr[MEDIA_DB_PATH]);
    }

    private static String putzen(String s) {
        s = s.replace("\n", "");
        s = s.replace("|", "");
        return s;
    }

    @Override
    public String toString() {
        String ret = "";
        for (int i = 0; i < MAX_ELEM; ++i) {
            if (i == 0) {
                ret += "| ***|" + COLUMN_NAMES[i] + ": " + arr[i] + Const.LINE_SEPARATOR;
            } else {
                ret += "|    |" + COLUMN_NAMES[i] + ": " + arr[i] + Const.LINE_SEPARATOR;
            }
        }
        return ret;
    }

    private void makeArr() {
        arr = new String[MAX_ELEM];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
    }

}
