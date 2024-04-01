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


package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

public class FilmFilterRunner {
    private final ProgData progData;
    private static final AtomicBoolean search = new AtomicBoolean(false);
    private static final AtomicBoolean research = new AtomicBoolean(false);
    private int count = 0;

    /**
     * hier wird das Filtern der Filmliste "angestoßen"
     *
     * @param progData
     */
    public FilmFilterRunner(ProgData progData) {
        this.progData = progData;

        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> filterList());
        PListener.addListener(new PListener(PListener.EVENT_FILTER_CHANGED, FilmFilterRunner.class.getSimpleName()) {
            @Override
            public void ping() {
                filterList();
            }
        });
        PListener.addListener(new PListener(PListener.EVENT_HISTORY_CHANGED, FilmFilterRunner.class.getSimpleName()) {
            @Override
            public void ping() {
                FilmFilter filmFilter = progData.filmFilterWorker.getActFilterSettings();
                if (filmFilter.isNotVis() && filmFilter.isNotHistory() ||
                        filmFilter.isOnlyVis() && filmFilter.getOnlyActHistory()) {
                    //nur dann wird History gefiltert
                    filterList();
                }
            }
        });
        PListener.addListener(new PListener(PListener.EVENT_BLACKLIST_CHANGED, FilmFilterRunner.class.getSimpleName()) {
            @Override
            public void ping() {
                filterList();
            }
        });
        PListener.addListener(new PListener(PListener.EVENT_DIACRITIC_CHANGED, FilmFilterRunner.class.getSimpleName()) {
            @Override
            public void ping() {
                filterList();
            }
        });
    }

    private void filterList() {
        // ist etwas "umständlich", scheint aber am flüssigsten zu laufen
        PDuration.counterStart("filterList");
        if (!search.getAndSet(true)) {
            research.set(false);

            try {
                Platform.runLater(() -> {
                    String text = "=======================================\n" +
                            "   ===== FILTERN: " + ++count + " =====\n" +
                            "=======================================";
                    P2Log.debugLog(text);

                    if (ProgData.getInstance().filmFilterWorker.getActFilterSettings().isThemeVis() &&
                            ProgData.getInstance().filmFilterWorker.getActFilterSettings().isThemeIsExact() &&
                            !ThemeListFactory.themeForChannelList
                                    .contains(ProgData.getInstance().filmFilterWorker.getActFilterSettings().getExactTheme())) {
                        P2Log.debugLog("Clear filter");

                        progData.filmFilterWorker.getActFilterSettings().switchFilterOff(true);
                        ProgData.getInstance().filmFilterWorker.getActFilterSettings().setExactTheme("");
                        progData.filmFilterWorker.getActFilterSettings().switchFilterOff(false);
                    }

                    progData.filmListFiltered.filteredListSetPred(PredicateFactory.getPredicate(progData));
                    progData.filmGuiController.selectLastShown();

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
        PDuration.counterStop("filterList");
    }
}
