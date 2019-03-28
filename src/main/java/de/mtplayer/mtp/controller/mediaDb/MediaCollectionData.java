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

package de.mtplayer.mtp.controller.mediaDb;

import de.mtplayer.mLib.tools.Data;
import de.p2tools.p2Lib.tools.PIndex;
import javafx.beans.property.*;

public class MediaCollectionData extends Data<MediaCollectionData> {


    private final static int MEDIA_PATH_ID = 0;
    private final static int MEDIA_PATH_PATH = 1;
    private final static int MEDIA_PATH_COLLECTION_NAME = 2;
    private final static int MEDIA_PATH_EXTERNAL = 3;

    public final static String[] COLUMN_NAMES = {"Id", "Pfad", "Sammlung", "Extern"};
    public final static String[] XML_NAMES = COLUMN_NAMES;
    public static final String TAG = "MediaPath";
    public final static int MAX_ELEM = 4;
    public String[] arr;

    private long id = 0;
    private StringProperty path = new SimpleStringProperty("");
    private StringProperty collectionName = new SimpleStringProperty("");
    private BooleanProperty external = new SimpleBooleanProperty(false);
    private IntegerProperty count = new SimpleIntegerProperty(0);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path.get();
    }

    public StringProperty pathProperty() {
        return path;
    }

    public void setPath(String path) {
        this.path.set(path);
    }


    public String getCollectionName() {
        return collectionName.get();
    }

    public StringProperty collectionNameProperty() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName.set(collectionName);
    }

    public boolean isExternal() {
        return external.get();
    }

    public BooleanProperty externalProperty() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external.set(external);
    }

    public int getCount() {
        return count.get();
    }

    public IntegerProperty countProperty() {
        return count;
    }

    public void setCount(int count) {
        this.count.set(count);
    }

    public MediaCollectionData() {
        makeArr();
    }

    public MediaCollectionData(String path, String collectionName, boolean external) {
        makeArr();
        setPath(path);
        setCollectionName(collectionName);
        setExternal(external);
        setId(PIndex.getIndex());
    }

    public void setPropsFromXml() {
        try {
            id = Long.parseLong(arr[MEDIA_PATH_ID]);
        } catch (final Exception ex) {
            id = 0;
        }
        setPath(arr[MEDIA_PATH_PATH]);
        setCollectionName(arr[MEDIA_PATH_COLLECTION_NAME]);
        setExternal(Boolean.valueOf(arr[MEDIA_PATH_EXTERNAL]));
    }

    public void setXmlFromProps() {
        arr[MEDIA_PATH_ID] = String.valueOf(id);
        arr[MEDIA_PATH_PATH] = getPath();
        arr[MEDIA_PATH_COLLECTION_NAME] = getCollectionName();
        arr[MEDIA_PATH_EXTERNAL] = String.valueOf(isExternal());
    }

    public String getHash() {
        return getPath();
    }

    private void makeArr() {
        arr = new String[MAX_ELEM];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
    }
}
