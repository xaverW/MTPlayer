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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filter.FilmFilter;
import de.p2tools.p2lib.guitools.pcbo.P2CboSeparator;

public class AudioFilterSamples {

    private AudioFilterSamples() {
    }

    public static FilmFilter getBookmarkFilter() {
        FilmFilter sf = new FilmFilter("nur Bookmarks");
        sf.clearFilter();
        sf.setOnlyVis(true);
        sf.setOnlyBookmark(true);
        return sf;
    }

    public static void addStandardFilter() {
        ProgData progData = ProgData.getInstance();

        //========================================================
        FilmFilter sf = new FilmFilter("alles anzeigen");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeIsExact(true);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setMinMaxDurVis(false);
        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyMark(false);
        sf.setOnlyLive(false);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(false);
        sf.setNotFuture(false);

        progData.filterWorkerAudio.getFilmFilterList().add(sf);

        //========================================================
        sf = new FilmFilter("nur \"neue\" anzeigen");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeIsExact(true);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(10);
        sf.setMinMaxDurVis(false);
        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(true);
        sf.setOnlyUt(false);
        sf.setOnlyMark(false);
        sf.setOnlyLive(false);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(true);
        sf.setNotDouble(true);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.filterWorkerAudio.getFilmFilterList().add(sf);

        //========================================================
        // nur Bookmark
        sf = getBookmarkFilter();
        progData.filterWorkerAudio.getFilmFilterList().add(sf);

        //========================================================
        progData.filterWorkerAudio.getFilmFilterList().add(new FilmFilter(P2CboSeparator.SEPARATOR));

        //========================================================
        sf = new FilmFilter("aktuelle Nachrichten");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeIsExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Tagesschau, Nachrichten");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(15);
        sf.setMinMaxDurVis(true);
        sf.setMaxDur(30);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyMark(false);
        sf.setOnlyLive(false);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(true);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.filterWorkerAudio.getFilmFilterList().add(sf);

        //========================================================
        sf = new FilmFilter("Krimi, Hörspiel");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeIsExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Krimi, Hörspiel");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(30);
        sf.setMinMaxDurVis(false);

        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(false);
        sf.setOnlyNew(false);
        sf.setOnlyUt(false);
        sf.setOnlyMark(false);
        sf.setOnlyLive(false);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(true);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.filterWorkerAudio.getFilmFilterList().add(sf);
    }
}
