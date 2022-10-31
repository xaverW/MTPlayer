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

package de.p2tools.mtplayer.controller.mediaDb;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.mtFilm.tools.Data;

public class MediaData extends Data<MediaData> {
    //sind die Daten die im "mediadb.txt" liegen!!

    public final static int MEDIA_DATA_NAME = 0;
    public final static int MEDIA_DATA_PATH = 1;
    public final static int MEDIA_DATA_SIZE = 2;
    public final static int MEDIA_DATA_COLLECTION_NAME = 3;
    public final static int MEDIA_DATA_COLLECTION_ID = 4;
    public final static int MEDIA_DATA_EXTERN = 5;

    public final static int MAX_ELEM = 6;
    public final static String[] COLUMN_NAMES = {"Name", "Pfad", "Größe [MB]", "Sammlung", "SammlungsId", "Extern"};
    public final static String[] XML_NAMES = {"Name", "Pfad", "Groesse", "Sammlung", "SammlungsId", "Extern"};
    public static final String TAG = "Mediensammlung";

    public String[] arr;

    private MediaCollectionData mediaCollectionData;
    private MediaFileSize mediaFileSize = new MediaFileSize(0);
    private long collectionId = 0;


    public MediaData() {
        makeArr();
    }

    public MediaData(String name, String path, long size, MediaCollectionData mediaCollectionData) {
        makeArr();
        this.mediaCollectionData = mediaCollectionData; // todo brauchts das

        setName(cleanUp(name));
        setPath(cleanUp(path));
        setSize(size);
        setCollectionId(mediaCollectionData.getId());
    }

    public String getName() {
        return arr[MEDIA_DATA_NAME];
    }

    public void setName(String name) {
        arr[MEDIA_DATA_NAME] = name;
    }

    public String getPath() {
        return arr[MEDIA_DATA_PATH];
    }

    public void setPath(String path) {
        arr[MEDIA_DATA_PATH] = path;
    }

    public MediaFileSize getSize() {
        return mediaFileSize;
    }

    public void setSize(String size) {
        mediaFileSize.setSize(size);
    }

    public void setSize(long size) {
        mediaFileSize.setSize(size);
    }

    public long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(long collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionName() {
        if (checkMediaPathData()) {
            return mediaCollectionData.getCollectionName();
        } else {
            return "";
        }
    }

    public boolean isExternal() {
        if (checkMediaPathData()) {
            return mediaCollectionData.isExternal();
        } else {
            return false;
        }
    }

    private boolean checkMediaPathData() {
        if (mediaCollectionData == null) {
            this.mediaCollectionData = ProgData.getInstance().mediaCollectionDataList.getMediaCollectionData(getCollectionId());
        }
        return mediaCollectionData != null;
    }

    public boolean equal(MediaData m) {
        return m.arr[MEDIA_DATA_NAME].equals(arr[MEDIA_DATA_NAME])
                && m.arr[MEDIA_DATA_PATH].equals(arr[MEDIA_DATA_PATH])
                && m.mediaCollectionData.equals(mediaCollectionData)
                && m.arr[MEDIA_DATA_SIZE].equals(arr[MEDIA_DATA_SIZE]);
    }

    @Override
    public String toString() {
        String ret = "";
        for (int i = 0; i < MAX_ELEM; ++i) {
            if (i == 0) {
                ret += "| ***|" + COLUMN_NAMES[i] + ": " + arr[i] + P2LibConst.LINE_SEPARATOR;
            } else {
                ret += "|    |" + COLUMN_NAMES[i] + ": " + arr[i] + P2LibConst.LINE_SEPARATOR;
            }
        }
        return ret;
    }

    public String getHash() {
        return getName() + "##" + getPath() + "##" + getCollectionId();
    }

    @Override
    public void setPropsFromXml() {
        setSize(arr[MEDIA_DATA_SIZE]);
        try {
            setCollectionId(Long.parseLong(arr[MEDIA_DATA_COLLECTION_ID]));
        } catch (Exception ex) {
            setCollectionId(0);
        }
    }

    @Override
    public void setXmlFromProps() {
        arr[MEDIA_DATA_SIZE] = getSize().getSizeAsStr();
        arr[MEDIA_DATA_COLLECTION_NAME] = getCollectionName();
        arr[MEDIA_DATA_COLLECTION_ID] = String.valueOf(getCollectionId());
        arr[MEDIA_DATA_EXTERN] = String.valueOf(isExternal());
    }

    private static String cleanUp(String s) {
        s = s.replace(P2LibConst.LINE_SEPARATOR, "");
        s = s.replace("|", "");
        return s;
    }

    private void makeArr() {
        arr = new String[MAX_ELEM];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
    }
}
