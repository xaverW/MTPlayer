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


package de.mtplayer.mtp.tools.filmListFilter;

import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.mtplayer.mtp.gui.FilmGuiController;
import de.mtplayer.mtp.gui.tools.Listener;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

public class FilmListFilter {
    private final Daten daten;

    public FilmListFilter(Daten daten) {
        this.daten = daten;

        daten.storedFilter.filterChangeProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> filterList()));
        daten.aboList.listChangedProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> filterList()));

        daten.loadFilmlist.addAdListener(new ListenerFilmlistLoad() {
            @Override
            public void fertig(ListenerFilmlistLoadEvent event) {
                filterList();
            }
        });

        Listener.addListener(new Listener(Listener.EREIGNIS_BLACKLIST_GEAENDERT, FilmGuiController.class.getSimpleName()) {
            @Override
            public void ping() {
                filterList();
            }
        });
    }

    private static final AtomicBoolean search = new AtomicBoolean(false);
    private static final AtomicBoolean research = new AtomicBoolean(false);

    public void filterList() {
        // ist etwas "umständlich", scheint aber am flüssigsten zu laufen

        if (!search.getAndSet(true)) {

            research.set(false);
            Thread th = new Thread(() -> {
                try {
                    Platform.runLater(() -> {
                        daten.filmListFiltered.filterdListSetPred(daten.storedFilter.getSelectedFilter().getPred());
                        search.set(false);
                        if (research.get()) {
                            filterList();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace(); //todo???
                }
            });

            th.setName("filterList");
            th.start();

        } else {
            research.set(true);
        }

    }

}
