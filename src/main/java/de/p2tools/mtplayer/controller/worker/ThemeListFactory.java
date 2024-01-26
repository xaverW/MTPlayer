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


package de.p2tools.mtplayer.controller.worker;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.Collator;
import java.util.*;

public class ThemeListFactory {
    public static final ObservableList<String> allChannelList = FXCollections.observableArrayList("");

    public static ObservableList<String> themeForChannelList = FXCollections.observableArrayList("");
    public static BooleanProperty themeForChannelChanged = new SimpleBooleanProperty(false);

    public static final ObservableList<String> channelsForAbosList = FXCollections.observableArrayList("");
    public static final ObservableList<String> allAboNamesList = FXCollections.observableArrayList("");

    private ThemeListFactory() {
    }

    public synchronized static void createThemeList(ProgData progData, String sender) {
        //toDo geht vielleicht besser??
        PDuration.counterStart("createThemeList");
        final ArrayList<String> newThemeList = new ArrayList<>();
        if (sender.isEmpty()) {
            newThemeList.addAll(Arrays.asList(progData.filmListFiltered.themePerChannel[0]));
        } else {
            makeTheme(progData, sender.trim(), newThemeList);
        }

        Collator collator = Collator.getInstance(Locale.GERMANY);
        collator.setStrength(Collator.PRIMARY);
        collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
        Comparator<String> comparator = (arg1, arg2) -> {
            if (arg1.startsWith("\"") || arg1.startsWith("#") || arg1.startsWith("„")) {
                arg1 = arg1.substring(1);
            }
            if (arg2.startsWith("\"") || arg2.startsWith("#") || arg2.startsWith("„")) {
                arg2 = arg2.substring(1);
            }
            return collator.compare(arg1, arg2);
        };
        newThemeList.sort(comparator);

        PLog.debugLogCount("createThemeList");

//        Platform.runLater(() -> {
        progData.worker.saveFilter();
        themeForChannelList = FXCollections.observableArrayList("");
        themeForChannelList.setAll(newThemeList);
        progData.worker.resetFilter();
        themeForChannelChanged.setValue(!themeForChannelChanged.getValue());

//        Platform.runLater(() -> {
//            String exactTheme = progData.filmFilterWorker.getActFilterSettings().getExactTheme();
//            progData.filmFilterWorker.getActFilterSettings().exactThemeProperty().setValue("");
//            progData.filmFilterWorker.getActFilterSettings().exactThemeProperty().setValue(exactTheme);
//        });

//        });
        PDuration.counterStop("createThemeList");
    }

    private static void makeTheme(ProgData progData, String sender, ArrayList<String> theme) {
        if (sender.contains(",")) {
            String[] senderArr = sender.toLowerCase().split(",");
            final TreeSet<String> tree = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            tree.add("");

            for (int i = 1; i < progData.filmListFiltered.themePerChannel.length; ++i) {
                for (String s : senderArr) {
                    if (progData.filmListFiltered.sender[i].equalsIgnoreCase(s.trim())) {
                        tree.addAll(Arrays.asList(progData.filmListFiltered.themePerChannel[i]));
                        break;
                    }
                }
            }
            theme.addAll(tree);

        } else {
            for (int i = 1; i < progData.filmListFiltered.themePerChannel.length; ++i) {
                if (progData.filmListFiltered.sender[i].equalsIgnoreCase(sender)) {
                    theme.addAll(Arrays.asList(progData.filmListFiltered.themePerChannel[i]));
                    break;
                }
            }
        }
    }
}
