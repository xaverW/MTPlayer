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
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmListMTP;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilter.FilmFilterCheck;
import de.p2tools.p2lib.mtfilter.Filter;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.function.Predicate;

public final class LiveFilmFilterWorker {

    private final LiveFilter actFilterSettings = new LiveFilter();
    private final FilmListMTP liveFilmList; // Filmliste der Live-Filme

    public LiveFilmFilterWorker() {
        this.liveFilmList = new FilmListMTP();
        PListener.addListener(new PListener(PListener.EVENT_LIVE_FILTER_CHANGED, LiveFilmFilterWorker.class.getSimpleName()) {
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

        Filter fChannel = new Filter(channel, true);
        if (!fChannel.isEmpty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchChannelSmart(fChannel, f));
        }

        Filter fTheme = new Filter(theme, false, true);
        if (!fTheme.isEmpty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchThemeExact(fTheme, f));
        }

        // Titel
        Filter fTitle = new Filter(title, true);
        if (!fTitle.isEmpty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchTitle(fTitle, f));
        }

        liveFilmList.getFilteredList().setPredicate(predicate);
    }
}
