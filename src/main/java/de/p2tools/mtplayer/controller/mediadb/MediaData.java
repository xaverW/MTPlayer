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

package de.p2tools.mtplayer.controller.mediadb;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibConst;

import java.util.Arrays;

public class MediaData {
    //sind die Daten die im "mediadb.txt" liegen!!

    public final static int MEDIA_DATA_NAME = 0;
    public final static int MEDIA_DATA_PATH = 1;
    public final static int MEDIA_DATA_SIZE = 2;
    public final static int MEDIA_DATA_COLLECTION_ID = 3;
    public final static int MEDIA_DATA_EXTERN = 4;

    public final static int MAX_ELEM = 5;
    public final static String[] COLUMN_NAMES = {"Name", "Pfad", "Größe [MB]", "SammlungsId", "Extern"};
    public final static String[] XML_NAMES = {"Name", "Pfad", "Groesse", "SammlungsId", "Extern"};
    public static final String TAG = "Mediensammlung";

    private final String name;
    private final String path;
    private final boolean extern;
    private final MediaFileSize mediaFileSize = new MediaFileSize(0);
    private long collectionIdLong;


    public MediaData(String[] arr) {
        this.name = arr[MEDIA_DATA_NAME];
        this.path = arr[MEDIA_DATA_PATH];
        mediaFileSize.setSize(arr[MEDIA_DATA_SIZE]);
        try {
            collectionIdLong = Long.parseLong(arr[MEDIA_DATA_COLLECTION_ID]);
        } catch (Exception ex) {
            collectionIdLong = 0;
        }
        this.extern = setExternal(); // wird gesetzt mit der Sammlung-Info, muss also nicht gespeichert werden
    }

    public MediaData(String name, String path, long size, long collectionIdLong) {
        mediaFileSize.setSize(size);
        this.collectionIdLong = collectionIdLong;
        this.name = cleanUp(name);
        this.path = cleanUp(path);
        this.extern = setExternal(); // wird gesetzt mit der Sammlung-Info, muss also nicht gespeichert werden
    }

    public String getHash() {
        return getName() + "##" + getPath() + "##" + getCollectionIdLong();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public MediaFileSize getSize() {
        return mediaFileSize;
    }

    public long getCollectionIdLong() {
        return collectionIdLong;
    }

    public boolean isExternal() {
        return extern;
    }

    private boolean setExternal() {
        if (collectionIdLong > 0) {
            return ProgData.getInstance().mediaCollectionDataList.isMediaCollectionDataExternal(getCollectionIdLong());
        }
        return false;
    }

    private static String cleanUp(String s) {
        s = s.replace(P2LibConst.LINE_SEPARATOR, "");
        s = s.replace("|", "");
        return s;
    }

    public boolean equal(MediaData m) {
        return m.name.equals(name)
                && m.path.equals(path)
                && m.collectionIdLong == collectionIdLong
                && m.getSize() == getSize();
    }

    @Override
    public String toString() {
        String ret = "";
        String[] arr = setXmlFromProps();
        for (int i = 0; i < MAX_ELEM; ++i) {
            if (i == 0) {
                ret += "| ***|" + COLUMN_NAMES[i] + ": " + arr[i] + P2LibConst.LINE_SEPARATOR;
            } else {
                ret += "|    |" + COLUMN_NAMES[i] + ": " + arr[i] + P2LibConst.LINE_SEPARATOR;
            }
        }
        return ret;
    }

    public String[] setXmlFromProps() {
        String[] arr = getArr();
        arr[MEDIA_DATA_NAME] = getName();
        arr[MEDIA_DATA_PATH] = getPath();
        arr[MEDIA_DATA_SIZE] = getSize().getSizeAsStr();
        arr[MEDIA_DATA_COLLECTION_ID] = String.valueOf(getCollectionIdLong());
        arr[MEDIA_DATA_EXTERN] = String.valueOf(isExternal());
        return arr;
    }

    public static String[] getArr() {
        String[] arr = new String[MAX_ELEM];
        Arrays.fill(arr, "");
        return arr;
    }
}
