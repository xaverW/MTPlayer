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

package de.mtplayer.mtp.controller.data;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;

public class NameLists {

    private ObservableList<String> obsAllChannel = FXCollections.observableArrayList("");
    private ObservableList<String> obsThemeForSelChannel = FXCollections.observableArrayList("");

    private ObservableList<String> obsChannelsForAbos = FXCollections.observableArrayList("");
    private ObservableList<String> obsAllAboNames = FXCollections.observableArrayList("");

    private ObservableList<String> obsAboNamesForDownloads = FXCollections.observableArrayList("");

    private final ProgData progData;
    private ArrayList<String> list;

    public NameLists(ProgData progData) {
        this.progData = progData;
        progData.loadFilmlist.addAdListener(new ListenerFilmlistLoad() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                getAllChannel();
                getTheme("");
            }
        });

        getAboNames();
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) ->
                getAboNames());

        progData.downloadList.downloadsChangedProperty().addListener((observable, oldValue, newValue) ->
                getAboNames());

        progData.downloadList.sizeProperty().addListener((observable, oldValue, newValue) ->
                getAboNames());
    }

    private void getAllChannel() {
        Platform.runLater(() -> obsAllChannel.setAll(Arrays.asList(progData.filmlist.sender)));
    }


    private void getAboNames() {
        final ArrayList<String> listAbo = progData.aboList.generateAboChannelList();
        final ArrayList<String> listAboName = progData.aboList.generateAboNameList();
        final ArrayList<String> listDownAboName = progData.downloadList.generateAboNameList(listAboName);

        Platform.runLater(() -> {
            obsChannelsForAbos.setAll(listAbo);
            obsAllAboNames.setAll(listAboName);
            obsAboNamesForDownloads.setAll(listDownAboName);
        });

//        list = progData.aboList.generateAboChannelList();
//        Platform.runLater(() -> obsChannelsForAbos.setAll(list));
//
//        list = progData.aboList.generateAboNameList();
//        Platform.runLater(() -> obsAllAboNames.setAll(list));
//
//        list = progData.downloadList.generateAboNameList(list);
//        Platform.runLater(() -> obsAboNamesForDownloads.setAll(list));
    }

    public void getTheme(String sender) {
        final ArrayList<String> theme = new ArrayList<>();
        if (sender.isEmpty()) {
            theme.addAll(Arrays.asList(progData.filmlistFiltered.themePerChannel[0]));
        } else {
            for (int i = 1; i < progData.filmlistFiltered.themePerChannel.length; ++i) {
                if (progData.filmlistFiltered.sender[i].equalsIgnoreCase(sender)) {
                    theme.addAll(Arrays.asList(progData.filmlistFiltered.themePerChannel[i]));
                    break;
                }
            }
        }

        Platform.runLater(() -> obsThemeForSelChannel.setAll(theme));
    }

    public ObservableList<String> getObsAllChannel() {
        return obsAllChannel;
    }

    public ObservableList<String> getObsThemeForSelChannel() {
        return obsThemeForSelChannel;
    }

    public ObservableList<String> getObsChannelsForAbos() {
        return obsChannelsForAbos;
    }

    public ObservableList<String> getObsAllAboNames() {
        return obsAllAboNames;
    }

}
