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


package de.p2tools.mtplayer.controller.filter.film;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filter.FilmFilter;
import de.p2tools.mtplayer.controller.filter.FilterWorker;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

public class FilmFilterRunner {
    private final ProgData progData;
    private static final AtomicBoolean search = new AtomicBoolean(false);
    private static final AtomicBoolean research = new AtomicBoolean(false);
    private int count = 0;
    private final boolean audio;

    /**
     * hier wird das Filtern der Filmliste "angestoßen"
     *
     * @param progData
     */
    public FilmFilterRunner(ProgData progData, boolean audio) {
        this.progData = progData;
        this.audio = audio;

        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> filterList());
        if (audio) {
            progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILTER_AUDIO_CHANGED) {
                @Override
                public void ping() {
                    filterList();
                }
            });

        } else {
            progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILTER_FILM_CHANGED) {
                @Override
                public void ping() {
                    filterList();
                }
            });
        }

        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_HISTORY_CHANGED) {
            @Override
            public void ping() {
                FilmFilter filmFilter = audio ? progData.filterWorkerAudio.getActFilterSettings() :
                        progData.filterWorkerFilm.getActFilterSettings();

                if (filmFilter.isNotVis() && filmFilter.isNotHistory() ||
                        filmFilter.isOnlyVis() && filmFilter.getOnlyActHistory()) {
                    //nur dann wird History gefiltert
                    filterList();
                }
            }
        });

        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_BLACKLIST_CHANGED) {
            @Override
            public void ping() {
                filterList();
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_DIACRITIC_CHANGED) {
            @Override
            public void ping() {
                filterList();
            }
        });
        ProgConfig.SYSTEM_FILTER_REG_EX_ONLY_CONTAIN.addListener((u, o, n) -> {
            filterList();
        });
    }

    private void filterList() {
        // ist etwas "umständlich", scheint aber am flüssigsten zu laufen
        P2Duration.counterStart("filterList");
        if (!search.getAndSet(true)) {
            research.set(false);

            try {
                Platform.runLater(() -> {
                    String text = "=======================================\n" +
                            "   ===== FILTERN" + (audio ? " AUDIO: " : " FILM: ") + ++count + " =====\n" +
                            "=======================================";
                    P2Log.debugLog(text);

                    final FilterWorker filterWorker;
                    if (audio) {
                        filterWorker = ProgData.getInstance().filterWorkerAudio;
                    } else {
                        filterWorker = ProgData.getInstance().filterWorkerFilm;
                    }
                    if (filterWorker.getActFilterSettings().isThemeVis() &&
                            filterWorker.getActFilterSettings().isThemeIsExact() &&
                            !(audio ? ThemeListFactory.themeForChannelListAudio : ThemeListFactory.themeForChannelListFilm)
                                    .contains(filterWorker.getActFilterSettings().getExactTheme())) {
                        // Filter ExactTheme kontrollieren
                        P2Log.debugLog("Clear film-filter");

                        filterWorker.getActFilterSettings().switchFilterOff(true);
                        filterWorker.getActFilterSettings().setExactTheme("");
                        filterWorker.getActFilterSettings().switchFilterOff(false);
                    }

                    if (audio) {
                        progData.audioGuiController.getSel(true, false); // damit die letzte Pos gesetzt wird
                        progData.audioListFiltered.filteredListSetPred(FilmFilterPredicateFactory.getPredicate(progData, true));
                        progData.audioGuiController.selectLastShown(); // und jetzt wieder setzen

                    } else {
                        progData.filmGuiController.getSel(true, false); // damit die letzte Pos gesetzt wird
                        progData.filmListFiltered.filteredListSetPred(FilmFilterPredicateFactory.getPredicate(progData, false));
                        progData.filmGuiController.selectLastShown(); // und jetzt wieder setzen
                    }

                    search.set(false);
                    if (research.get()) {
                        filterList();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace(); //todo???
            }
        } else {
            research.set(true);
        }
        P2Duration.counterStop("filterList");
    }
}
