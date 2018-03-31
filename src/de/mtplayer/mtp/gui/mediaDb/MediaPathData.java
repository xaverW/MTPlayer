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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MediaPathData extends Data<MediaPathData> {

    private final static int MEDIA_PATH_PATH = 0;
    private final static int MEDIA_PATHE_SAVE = 1; // damit merkt er sich Pfade die "Offline" sind, kann entfallen??

    public final static String[] COLUMN_NAMES = {"Pfad", "Speichern"};
    public final static String[] XML_NAMES = COLUMN_NAMES;
    public static final String TAG = "MediaPath";
    public final static int MAX_ELEM = 2;
    public String[] arr;

    public String getPath() {
        return path.get();
    }

    public StringProperty pathProperty() {
        return path;
    }

    public void setPath(String path) {
        this.path.set(path);
    }

    public boolean isSave() {
        return save.get();
    }

    public BooleanProperty saveProperty() {
        return save;
    }

    public void setSave(boolean save) {
        this.save.set(save);
    }

    private StringProperty path = new SimpleStringProperty("");
    private BooleanProperty save = new SimpleBooleanProperty(false);


    public MediaPathData(String pfad, boolean sichern) {
        makeArr();
        setPath(pfad);
        setSave(sichern);
    }

    public MediaPathData(String pfad) {
        makeArr();
        setPath(pfad);
        setSave(false);
    }

    public MediaPathData() {
        makeArr();
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

    public void setPropsFromXml() {
        setPath(arr[MEDIA_PATH_PATH]);
        setSave(Boolean.parseBoolean(arr[MEDIA_PATHE_SAVE]));
    }

    public void setXmlFromProps() {
        arr[MEDIA_PATH_PATH] = getPath();
        arr[MEDIA_PATHE_SAVE] = String.valueOf(isSave());
    }

}
