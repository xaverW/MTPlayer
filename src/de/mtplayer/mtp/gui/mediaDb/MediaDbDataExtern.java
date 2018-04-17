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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class MediaDbDataExtern extends Data<MediaDbDataExtern> {

    private String collectionName = "";
    private String path = "";
    private IntegerProperty count = new SimpleIntegerProperty(0);


    public MediaDbDataExtern(String name, String path) {
        setCollectionName(clean(name));
        setPath(clean(path));
        setCount(0);
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public boolean equal(MediaDbData m) {
        return m.arr[MediaDbData.MEDIA_DB_COLLECTION_NAME].equals(getCollectionName());
    }

    public boolean equal(MediaDbDataExtern m) {
        return m.getCollectionName().equals(getCollectionName())
                && m.getPath().equals(getPath());
    }

    private static String clean(String s) {
        s = s.replace("\n", "");
        s = s.replace("|", "");
        return s;
    }

}
