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

package de.p2tools.mtplayer.controller.filterlive;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmListMTP;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.mediathek.filter.FilmFilterCheck;
import de.p2tools.p2lib.mediathek.filter.Filter;
import de.p2tools.p2lib.p2event.P2Listener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.function.Predicate;

public final class LiveFilmFilterWorker {

    private final LiveFilter actFilterSettings = new LiveFilter();
    private final FilmListMTP liveFilmList; // Filmliste der Live-Filme

    public LiveFilmFilterWorker(ProgData progData) {
        this.liveFilmList = new FilmListMTP();
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_LIVE_FILTER_CHANGED) {
            @Override
            public void ping() {
                filterList();
            }
        });
    }

    public FilteredList<FilmDataMTP> getFilteredList() {
        return liveFilmList.getFilteredList();
    }

    public SortedList<FilmDataMTP> getSortedList() {
        return liveFilmList.getSortedList();
    }

    public FilmListMTP getLiveFilmList() {
        return liveFilmList;
    }

    public LiveFilter getActFilterSettings() {
        return actFilterSettings;
    }

    public synchronized void clearFilter() {
        actFilterSettings.clearFilter();
        filterList();
    }

    private void filterList() {
        Predicate<FilmData> predicate = filter -> true;

        final String channel = getActFilterSettings().channelProperty().getValueSafe();
        final String theme = getActFilterSettings().themeProperty().getValueSafe();
        final String title = getActFilterSettings().titleProperty().getValueSafe();

        de.p2tools.p2lib.mediathek.filter.Filter fChannel = new de.p2tools.p2lib.mediathek.filter.Filter(channel, true);
        if (!fChannel.isEmpty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchChannelSmart(fChannel, f));
        }

        de.p2tools.p2lib.mediathek.filter.Filter fTheme = new de.p2tools.p2lib.mediathek.filter.Filter(theme, false, true);
        if (!fTheme.isEmpty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchThemeExact(fTheme, f));
        }

        // Titel
        de.p2tools.p2lib.mediathek.filter.Filter fTitle = new Filter(title, true);
        if (!fTitle.isEmpty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchTitle(fTitle, f));
        }

        liveFilmList.getFilteredList().setPredicate(predicate);
    }
}
