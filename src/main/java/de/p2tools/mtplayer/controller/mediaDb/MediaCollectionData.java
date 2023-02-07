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

import de.p2tools.p2Lib.configFile.config.Config;
import de.p2tools.p2Lib.configFile.config.Config_boolProp;
import de.p2tools.p2Lib.configFile.config.Config_longProp;
import de.p2tools.p2Lib.configFile.config.Config_stringProp;
import de.p2tools.p2Lib.configFile.pData.PDataSample;
import de.p2tools.p2Lib.tools.PIndex;
import javafx.beans.property.*;

import java.util.ArrayList;

public class MediaCollectionData extends PDataSample<MediaCollectionData> {

    private final static int MEDIA_PATH_ID = 0;
    private final static int MEDIA_PATH_PATH = 1;
    private final static int MEDIA_PATH_COLLECTION_NAME = 2;
    private final static int MEDIA_PATH_EXTERNAL = 3;

    public final static String[] COLUMN_NAMES = {"Id", "Pfad", "Sammlung", "Extern"};
    public final static String[] XML_NAMES = COLUMN_NAMES;
    public static final String TAG = "MediaCollectionData";
    public final static int MAX_ELEM = XML_NAMES.length;

    private LongProperty id = new SimpleLongProperty(0L);
    private StringProperty path = new SimpleStringProperty("");
    private StringProperty collectionName = new SimpleStringProperty("");
    private BooleanProperty external = new SimpleBooleanProperty(false);
    private IntegerProperty count = new SimpleIntegerProperty(0);

    public MediaCollectionData() {
    }

    public MediaCollectionData(String path, String collectionName, boolean external) {
        setPath(path);
        setCollectionName(collectionName);
        setExternal(external);
        setId(PIndex.getIndex());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "MediaCollectionData";
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new Config_longProp("id", "Id", id));
        list.add(new Config_stringProp("path", "Pfad", path));
        list.add(new Config_stringProp("collectionName", "Sammlung", collectionName));
        list.add(new Config_boolProp("external", "Extern", external));

        return list.toArray(new Config[]{});
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
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

    public String getHash() {
        return getPath();
    }
}
