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

    final private ProgData progData;

    public NameLists(ProgData progData) {
        this.progData = progData;
        progData.loadFilmlist.addAdListener(new ListenerFilmlistLoad() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                getAllChannel();
                getThemen("");
            }
        });

        getAbosnames();
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) ->
                getAbosnames());

        progData.downloadList.downloadsChangedProperty().addListener((observable, oldValue, newValue) ->
                getAbosnames());
        progData.downloadList.sizeProperty().addListener((observable, oldValue, newValue) ->
                getAbosnames());
    }

    private void getAllChannel() {
        obsAllChannel.setAll(Arrays.asList(progData.filmlist.sender));
    }


    private void getAbosnames() {
        ArrayList<String> list = progData.aboList.generateAboChannelList();
        obsChannelsForAbos.setAll(list);

        list = progData.aboList.generateAboNameList();
        obsAllAboNames.setAll(list);

        list = progData.downloadList.generateAboNameList(list);
        obsAboNamesForDownloads.setAll(list);
    }

    public void getThemen(String sender) {
        final ArrayList<String> thema = new ArrayList<>();
        if (sender.isEmpty()) {
            thema.addAll(Arrays.asList(progData.filmlistFiltered.themenPerChannel[0]));
        } else {
            for (int i = 1; i < progData.filmlistFiltered.themenPerChannel.length; ++i) {
                if (progData.filmlistFiltered.sender[i].equalsIgnoreCase(sender)) {
                    thema.addAll(Arrays.asList(progData.filmlistFiltered.themenPerChannel[i]));
                    break;
                }
            }
        }

        obsThemeForSelChannel.setAll(thema);
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
