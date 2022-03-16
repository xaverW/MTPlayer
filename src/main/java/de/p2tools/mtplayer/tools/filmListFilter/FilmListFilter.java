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


package de.p2tools.mtplayer.tools.filmListFilter;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.mtplayer.tools.storedFilter.PredicateFactory;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

public class FilmListFilter {
    private final ProgData progData;

    /**
     * hier wird das Filtern der Filmliste "angestoßen"
     *
     * @param progData
     */
    public FilmListFilter(ProgData progData) {
        this.progData = progData;

        progData.storedFilters.filterChangeProperty().addListener((observable, oldValue, newValue) -> filter()); // Filmfilter (User) haben sich geändert
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> filterList());
        progData.loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                filterList();
            }
        });

        Listener.addListener(new Listener(Listener.EVENT_BLACKLIST_CHANGED, FilmListFilter.class.getSimpleName()) {
            @Override
            public void pingFx() {
                filterList();
            }
        });
        Listener.addListener(new Listener(Listener.EVENT_DIACRITIC_CHANGED, FilmListFilter.class.getSimpleName()) {
            @Override
            public void pingFx() {
                filterList();
            }
        });
    }

    private void filter() {
        Platform.runLater(() -> filterList());
    }

    private static final AtomicBoolean search = new AtomicBoolean(false);
    private static final AtomicBoolean research = new AtomicBoolean(false);

    private void filterList() {
        // ist etwas "umständlich", scheint aber am flüssigsten zu laufen
        if (!search.getAndSet(true)) {
            research.set(false);
//            Thread th = new Thread(() -> {
            try {
                Platform.runLater(() -> {
                    progData.filmlistFiltered.filteredListSetPred(
                            PredicateFactory.getPredicate(progData.storedFilters.getActFilterSettings()));
                    search.set(false);
                    if (research.get()) {
                        filterList();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace(); //todo???
            }
//            });
//            th.setName("filterList");
//            th.start();
        } else {
            research.set(true);
        }
    }
}
