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
import javafx.beans.property.*;

public class MediaPathData extends Data<MediaPathData> {

    private final static int MEDIA_PATH_PATH = 0;
    private final static int MEDIA_PATH_COLLECTION = 1;
    private final static int MEDIA_PATH_EXTERNAL = 2;

    public final static String[] COLUMN_NAMES = {"Pfad", "Sammlung", "Extern"};
    public final static String[] XML_NAMES = COLUMN_NAMES;
    public static final String TAG = "MediaPath";
    public final static int MAX_ELEM = 3;
    public String[] arr;

    private StringProperty path = new SimpleStringProperty("");
    private StringProperty collectionName = new SimpleStringProperty("");
    private BooleanProperty external = new SimpleBooleanProperty(false);
    private IntegerProperty count = new SimpleIntegerProperty(0);


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

    public MediaPathData() {
        makeArr();
    }

    public MediaPathData(String path) {
        makeArr();
        setPath(path);
    }

    public MediaPathData(String path, String collectionName, boolean external) {
        makeArr();
        setPath(path);
        setCollectionName(collectionName);
        setExternal(external);
    }

    public void setPropsFromXml() {
        setPath(arr[MEDIA_PATH_PATH]);
        setCollectionName(arr[MEDIA_PATH_COLLECTION]);
        setExternal(Boolean.valueOf(arr[MEDIA_PATH_EXTERNAL]));
    }

    public void setXmlFromProps() {
        arr[MEDIA_PATH_PATH] = getPath();
        arr[MEDIA_PATH_COLLECTION] = getCollectionName();
        arr[MEDIA_PATH_EXTERNAL] = String.valueOf(isExternal());
    }

    private void makeArr() {
        arr = new String[MAX_ELEM];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
    }

}
