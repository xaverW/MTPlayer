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
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboFactory;
import de.p2tools.mtplayer.controller.data.blackdata.BlackData;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFactory;
import de.p2tools.p2lib.alert.P2Alert;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

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

    public static int importAbosMediathekView(Stage stage, ObservableList<AboData> aboList) {
        // Import MediathekView
        int ret = 0;
        BooleanProperty remember = new SimpleBooleanProperty(false);
        boolean todo = true; //Abo nehmen

        for (AboData aboData : aboList) {
            AboData abo;

            if ((abo = AboFactory.aboExistsAlready(aboData, false)) != null) {
                // dann gibts das Abo schon, "themeExact" gibts hier nicht, ist immer false
                P2Alert.BUTTON btn;
                if (!remember.getValue()) {
                    btn = P2Alert.alert_yes_no_remember(stage, "Fehler", "Abo anlegen",
                            "Ein Abo mit den Einstellungen existiert bereits: " +
                                    "\n\n" +
                                    (abo.getChannel().isEmpty() ? "" : "Sender: " + abo.getChannel() + "\n") +
                                    (abo.getTheme().isEmpty() ? "" : "Thema: " + abo.getTheme() + "\n") +
                                    (abo.getThemeTitle().isEmpty() ? "" : "Thema-Titel: " + abo.getThemeTitle() + "\n") +
                                    (abo.getTitle().isEmpty() ? "" : "Titel: " + abo.getTitle() + "\n") +
                                    (abo.getSomewhere().isEmpty() ? "" : "Irgendwo: " + abo.getSomewhere()) +
                                    "\n\n" +
                                    "Trotzdem anlegen?", remember, "Immer");
                    todo = btn.equals(P2Alert.BUTTON.YES);
                }

                if (!todo) {
                    continue;
                }
            }

            ++ret;
            ProgData.getInstance().aboList.addAbo(aboData);
        }
        return ret;
    }

    public static int addBlacks(ObservableList<BlackData> blackList) {
        int ret = 0;
        for (BlackData blackData : blackList) {
            //Sender und Thema sind immer "exact"
            if (!BlacklistFactory.blackExistsAlready(blackData, ProgData.getInstance().blackList)) {
                ++ret;
                ProgData.getInstance().blackList.add(blackData);
            }
        }
        return ret;
    }
}
