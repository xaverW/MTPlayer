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

package de.p2tools.mtplayer.res;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GetIcon {

    private final static String PATH_PROGRAM = "/de/p2tools/mtplayer/res/program/";
    private final static String PATH_SENDER = "/de/p2tools/mtplayer/res/sender/";
    private final static String PATH_RES = "/de/p2tools/mtplayer/res/";

    public static ImageView getImageView(String strIcon, int w, int h) {
        return new ImageView(getImage(strIcon, PATH_PROGRAM, w, h));
    }

    public static ImageView getImageView(String strIcon) {
        return new ImageView(getImage(strIcon, PATH_PROGRAM, 0, 0));
    }

    public static Image getImage(String strIcon, int w, int h) {
        return getImage(strIcon, PATH_PROGRAM, w, h);
    }

    public static Image getImage(String strIcon) {
        return getImage(strIcon, PATH_PROGRAM, 0, 0);
    }

    public static Image getImage(String strIcon, String path, int w, int h) {
        Image icon;
        icon = getStdImage(strIcon, path, w, h);

//        if (Boolean.parseBoolean(ProgConfig.SYSTEM_ICON_STANDARD.get())) {
//            icon = getStdImage(strIcon, path, w, h);
//        } else {
//            try {
//                //todo das funzt nicht
//                final String pfad = PFileUtils.addsPfad(ProgConfig.SYSTEM_ICON_PFAD.get(), strIcon);
//                if (new File(pfad).exists()) {
//                    icon = new javafx.scene.image.Image(path);
//                } else {
//                    icon = getStdImage(strIcon, path, w, h);
//                }
//            } catch (final Exception ex) {
//                PLog.errorLog(932107891, strIcon);
//                icon = getStdImage(strIcon, path, w, h);
//            }
//        }

        return icon;
    }

    private static Image getStdImage(String strIcon, String path, int w, int h) {
        return new Image(GetIcon.class.getResource(path + strIcon).toExternalForm(),
                w, h, false, true);
    }
}
