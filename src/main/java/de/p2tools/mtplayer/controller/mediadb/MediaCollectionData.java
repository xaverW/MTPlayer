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

import de.p2tools.p2lib.configfile.config.*;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import de.p2tools.p2lib.tools.P2Index;
import javafx.beans.property.*;

import java.util.ArrayList;

public class MediaCollectionData extends P2DataSample<MediaCollectionData> {

    public final static String[] COLUMN_NAMES = {"Id", "Pfad", "Sammlung", "Extern"};
    public final static String[] XML_NAMES = COLUMN_NAMES;
    public static final String TAG = "MediaCollectionData";
    public final static int MAX_ELEM = XML_NAMES.length;

    private final LongProperty idLong = new SimpleLongProperty(0L); // sind die alten, bis V14
    private final IntegerProperty idInt = new SimpleIntegerProperty(0);
    private final StringProperty path = new SimpleStringProperty("");
    private final StringProperty collectionName = new SimpleStringProperty("");
    private final BooleanProperty external = new SimpleBooleanProperty(false);
    private final IntegerProperty count = new SimpleIntegerProperty(0);

    public MediaCollectionData() {
    }

    public MediaCollectionData(String path, String collectionName, boolean external) {
        setPath(path);
        setCollectionName(collectionName);
        setExternal(external);
        setIdInt(P2Index.getIndexInt());
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
        list.add(new Config_longProp("id", "Id", idLong));
        list.add(new Config_intProp("idInt", "IdInt", idInt));
        list.add(new Config_stringProp("path", "Pfad", path));
        list.add(new Config_stringProp("collectionName", "Sammlung", collectionName));
        list.add(new Config_boolProp("external", "Extern", external));

        return list.toArray(new Config[]{});
    }

    public int getIdInt() {
        return idInt.get();
    }

    public IntegerProperty idIntProperty() {
        return idInt;
    }

    public void setIdInt(int idInt) {
        this.idInt.set(idInt);
    }

    public long getIdLong() {
        return idLong.get();
    }

    public LongProperty idLongProperty() {
        return idLong;
    }

    public void setIdLong(long idLong) {
        this.idLong.set(idLong);
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
}
