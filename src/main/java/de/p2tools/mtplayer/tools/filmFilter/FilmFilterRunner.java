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


package de.p2tools.mtplayer.tools.filmFilter;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.mtplayer.gui.tools.Listener;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

public class FilmFilterRunner {
    private final ProgData progData;
    private static final AtomicBoolean search = new AtomicBoolean(false);
    private static final AtomicBoolean research = new AtomicBoolean(false);
    int count = 0;

    /**
     * hier wird das Filtern der Filmliste "angestoßen"
     *
     * @param progData
     */
    public FilmFilterRunner(ProgData progData) {
        this.progData = progData;

        progData.actFilmFilterWorker.filterChangeProperty().addListener((observable, oldValue, newValue) -> filter()); // Filmfilter (User) haben sich geändert
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> filterList());
        progData.loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                filterList();
            }
        });

        Listener.addListener(new Listener(Listener.EVENT_BLACKLIST_CHANGED, FilmFilterRunner.class.getSimpleName()) {
            @Override
            public void pingFx() {
                filterList();
            }
        });
        Listener.addListener(new Listener(Listener.EVENT_DIACRITIC_CHANGED, FilmFilterRunner.class.getSimpleName()) {
            @Override
            public void pingFx() {
                filterList();
            }
        });
    }

    private void filter() {
        Platform.runLater(() -> filterList());
    }

    private void filterList() {
        // ist etwas "umständlich", scheint aber am flüssigsten zu laufen
        if (!search.getAndSet(true)) {
            research.set(false);
//            Thread th = new Thread(() -> {
            try {
                Platform.runLater(() -> {
                    System.out.println("========================================");
                    System.out.println("  ======== Filter: " + count++ + " ========");
                    System.out.println("========================================");
                    progData.filmlistFiltered.filteredListSetPred(
                            PredicateFactory.getPredicate(progData.actFilmFilterWorker.getActFilterSettings()));
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
