/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.mv;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.BlackData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboFactory;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MVFactory {
    private MVFactory() {
    }

    public static String getSettingsDirectory() {
        final String stdDir = ".mediathek3";
        Path baseDirectoryPath;

        baseDirectoryPath = Paths.get(System.getProperty("user.home"), stdDir);
        if (baseDirectoryPath.toFile().exists() && baseDirectoryPath.toFile().isDirectory()) {
            return baseDirectoryPath.toString();
        }
        return "";
    }

    public static int addAbos(ObservableList<AboData> aboList) {
        int ret = 0;
        for (AboData aboData : aboList) {
            if (!AboFactory.aboExistsAlready(aboData)) {
                ++ret;
                ProgData.getInstance().aboList.addAbo(aboData);
            }
        }
        return ret;
    }

    public static int addBlacks(ObservableList<BlackData> blackList) {
        int ret = 0;
        for (BlackData blackData : blackList) {
            if (!ProgData.getInstance().blackList.blackExistsAlready(blackData)) {
                ++ret;
                ProgData.getInstance().blackList.add(blackData);
            }
        }
        return ret;
    }
}
