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

import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;

public class NameLists {

    private ObservableList<String> obsAllSender = FXCollections.observableArrayList("");
    private ObservableList<String> obsThemaForSelSender = FXCollections.observableArrayList("");

    private ObservableList<String> obsSenderForAbos = FXCollections.observableArrayList("");
    private ObservableList<String> obsAllAbonames = FXCollections.observableArrayList("");

    private ObservableList<String> obsAbonamesForDownloads = FXCollections.observableArrayList("");

    final private Daten daten;

    public NameLists(Daten daten) {
        this.daten = daten;
        daten.loadFilmlist.addAdListener(new ListenerFilmlistLoad() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
            }

            @Override
            public void fertig(ListenerFilmlistLoadEvent event) {
                getAllSender();
                getThemen("");
            }
        });

        getAbosnames();
        daten.aboList.listChangedProperty().addListener((observable, oldValue, newValue) ->
                getAbosnames());

        daten.downloadList.downloadsChangedProperty().addListener((observable, oldValue, newValue) ->
                getAbosnames());
        daten.downloadList.sizeProperty().addListener((observable, oldValue, newValue) ->
                getAbosnames());
    }

    private void getAllSender() {
        obsAllSender.setAll(Arrays.asList(daten.filmlist.sender));
    }


    private void getAbosnames() {
        ArrayList<String> list = daten.aboList.generateAboSenderList();
        obsSenderForAbos.setAll(list);

        list = daten.aboList.generateAboNameList();
        obsAllAbonames.setAll(list);

        list = daten.downloadList.generateAboNameList(list);
        obsAbonamesForDownloads.setAll(list);
    }

    public void getThemen(String sender) {
        final ArrayList<String> thema = new ArrayList<>();
        if (sender.isEmpty()) {
            thema.addAll(Arrays.asList(daten.filmlistFiltered.themenPerSender[0]));
        } else {
            for (int i = 1; i < daten.filmlistFiltered.themenPerSender.length; ++i) {
                if (daten.filmlistFiltered.sender[i].equalsIgnoreCase(sender)) {
                    thema.addAll(Arrays.asList(daten.filmlistFiltered.themenPerSender[i]));
                    break;
                }
            }
        }

        obsThemaForSelSender.setAll(thema);
    }

    public ObservableList<String> getObsAllSender() {
        return obsAllSender;
    }

    public ObservableList<String> getObsThemaForSelSender() {
        return obsThemaForSelSender;
    }

    public ObservableList<String> getObsSenderForAbos() {
        return obsSenderForAbos;
    }

    public ObservableList<String> getObsAllAbonames() {
        return obsAllAbonames;
    }

}
