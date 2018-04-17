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

public class MediaDbData extends Data<MediaDbData> {

    public final static int MEDIA_DB_NAME = 0;
    public final static int MEDIA_DB_PATH = 1;
    public final static int MEDIA_DB_SIZE = 2;
    public final static int MEDIA_DB_COLLECTION_NAME = 3;
    public final static int MEDIA_DB_EXTERN = 4;

    public final static int MAX_ELEM = 5;
    public final static String[] COLUMN_NAMES = {"Name", "Pfad", "Größe [MB]", "Sammlung", "Extern"};
    public final static String[] XML_NAMES = {"Name", "Pfad", "Groesse", "Sammlung", "Extern"};
    public static final String TAG = "Mediensammlung";

    public String[] arr;
    public MediaDbFileSize mediaDbFileSize;
    private boolean extern = false;

    public MediaDbData() {
        makeArr();
    }

    public MediaDbData(String name, String pfad, long size, String sammlung, boolean extern) {
        makeArr();
        arr[MEDIA_DB_NAME] = putzen(name);
        arr[MEDIA_DB_PATH] = putzen(pfad);

        mediaDbFileSize = new MediaDbFileSize(size);
        arr[MEDIA_DB_SIZE] = mediaDbFileSize.toString();

        arr[MEDIA_DB_COLLECTION_NAME] = putzen(sammlung);
        setExtern(extern);
        arr[MEDIA_DB_EXTERN] = Boolean.toString(extern);
    }

    public String getName() {
        return arr[MEDIA_DB_NAME];
    }

    public void setName(String name) {
        arr[MEDIA_DB_NAME] = name;
    }

    public String getPath() {
        return arr[MEDIA_DB_PATH];
    }

    public void setPath(String path) {
        arr[MEDIA_DB_PATH] = path;
    }

    public String getSize() {
        return arr[MEDIA_DB_SIZE];
    }

    public void setSize(String size) {
        arr[MEDIA_DB_SIZE] = size;
    }

    public String getCollectionName() {
        return arr[MEDIA_DB_COLLECTION_NAME];
    }

    public void setCollectionName(String sammlung) {
        arr[MEDIA_DB_COLLECTION_NAME] = sammlung;
    }


    public boolean isExtern() {
        return extern;
    }

    public void setExtern(String extern) {
        arr[MEDIA_DB_EXTERN] = extern;
    }

    public void setExtern(boolean extern) {
        this.extern = extern;
    }


    public boolean equal(MediaDbData m) {
        return m.arr[MEDIA_DB_NAME].equals(arr[MEDIA_DB_NAME])
                && m.arr[MEDIA_DB_PATH].equals(arr[MEDIA_DB_PATH])
                && m.arr[MEDIA_DB_SIZE].equals(arr[MEDIA_DB_SIZE]);
    }

    public String getEqual() {
        return arr[MEDIA_DB_NAME] + arr[MEDIA_DB_PATH] + arr[MEDIA_DB_SIZE];
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

    public void setPropsFromXml() {
        mediaDbFileSize = new MediaDbFileSize(arr[MEDIA_DB_SIZE]);
        boolean ex;
        try {
            ex = Boolean.parseBoolean(arr[MEDIA_DB_EXTERN]);
        } catch (Exception ignore) {
            ex = false;
        }
        setExtern(ex);
    }

    //===================================
    // Private
    //===================================
    private void makeArr() {
        arr = new String[MAX_ELEM];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
    }

}
