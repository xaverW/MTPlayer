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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class MediaDbData extends Data<MediaDbData> {

    public final static int MEDIA_DB_NAME = 0;
    public final static int MEDIA_DB_PATH = 1;
    public final static int MEDIA_DB_SIZE = 2;
    public final static int MEDIA_DB_EXTERN = 3;

    public final static int MAX_ELEM = 4;
    public final static String[] COLUMN_NAMES = {"Name", "Pfad", "Größe [MB]", "Extern"};
    public final static String[] XML_NAMES = {"Name", "Pfad", "Groesse", "Extern"};

    public String[] arr;
    public MediaDBFileSize mVMediaDBFileSize;

    private BooleanProperty extern = new SimpleBooleanProperty(false);

    public MediaDbData(String name, String pfad, long size, boolean extern) {
        makeArr();
        arr[MEDIA_DB_NAME] = putzen(name);
        arr[MEDIA_DB_PATH] = putzen(pfad);
        mVMediaDBFileSize = new MediaDBFileSize(size);
        arr[MEDIA_DB_SIZE] = mVMediaDBFileSize.toString();

        setExtern(extern);
        arr[MEDIA_DB_EXTERN] = Boolean.toString(extern);// todo das muss noch weg
    }

    public String getName() {
        return arr[MEDIA_DB_NAME];
    }

    public String getPath() {
        return arr[MEDIA_DB_PATH];
    }

    public String getSize() {
        return arr[MEDIA_DB_SIZE];
    }

    public boolean isExtern() {
        return extern.get();
    }

    public BooleanProperty externProperty() {
        return extern;
    }

    public void setExtern(boolean extern) {
        this.extern.set(extern);
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
        s = s.replace(MediaDbList.TRENNER, "");
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
