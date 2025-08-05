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
import de.p2tools.mtplayer.controller.data.film.FilmListMTP;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.Collator;
import java.util.*;

public class ThemeListFactory {
    public static final ObservableList<String> allChannelListFilm = FXCollections.observableArrayList("");
    public static ObservableList<String> themeForChannelListFilm = FXCollections.observableArrayList("");
    public static BooleanProperty themeForChannelChangedFilm = new SimpleBooleanProperty(false);

    public static final ObservableList<String> allChannelListAudio = FXCollections.observableArrayList("");
    public static ObservableList<String> themeForChannelListAudio = FXCollections.observableArrayList("");
    public static BooleanProperty themeForChannelChangedAudio = new SimpleBooleanProperty(false);

    public static final ObservableList<String> channelsForAbosList = FXCollections.observableArrayList("");
    public static final ObservableList<String> allAboNamesList = FXCollections.observableArrayList("");


    private ThemeListFactory() {
    }

    public synchronized static void createThemeList(boolean audio, ProgData progData, String sender) {
        //toDo geht vielleicht besser??
        P2Duration.counterStart("createThemeList");

        FilmListMTP listFiltered; // Filmliste, wie im TabFilme angezeigt
        if (audio) {
            listFiltered = progData.audioListFiltered;
        } else {
            listFiltered = progData.filmListFiltered;
        }

        final ArrayList<String> newThemeList = new ArrayList<>();
        if (sender.isEmpty()) {
            newThemeList.addAll(Arrays.asList(listFiltered.themePerChannel[0]));
        } else {
            makeTheme(listFiltered, sender.trim(), newThemeList);
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

        progData.worker.saveFilter();
        if (audio) {
            themeForChannelListAudio = FXCollections.observableArrayList("");
            themeForChannelListAudio.setAll(newThemeList);
        } else {
            themeForChannelListFilm = FXCollections.observableArrayList("");
            themeForChannelListFilm.setAll(newThemeList);
        }
        progData.worker.resetFilter();
        if (audio) {
            themeForChannelChangedAudio.setValue(!themeForChannelChangedAudio.getValue());
        } else {
            themeForChannelChangedFilm.setValue(!themeForChannelChangedFilm.getValue());
        }
        P2Duration.counterStop("createThemeList");
    }

    private static void makeTheme(FilmListMTP listMTP, String sender, ArrayList<String> theme) {
        if (sender.contains(",")) {
            String[] senderArr = sender.toLowerCase().split(",");
            final TreeSet<String> tree = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            tree.add("");

            for (int i = 1; i < listMTP.themePerChannel.length; ++i) {
                for (String s : senderArr) {
                    if (listMTP.sender[i].equalsIgnoreCase(s.trim())) {
                        tree.addAll(Arrays.asList(listMTP.themePerChannel[i]));
                        break;
                    }
                }
            }
            theme.addAll(tree);

        } else {
            for (int i = 1; i < listMTP.themePerChannel.length; ++i) {
                if (listMTP.sender[i].equalsIgnoreCase(sender)) {
                    theme.addAll(Arrays.asList(listMTP.themePerChannel[i]));
                    break;
                }
            }
        }
    }
}
