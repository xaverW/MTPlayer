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

package de.p2tools.mtplayer.controller.filteraudio;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filterfilm.FilmFilter;
import de.p2tools.p2lib.guitools.P2SeparatorComboBox;

public class AudioFilterSamples {

    private AudioFilterSamples() {
    }

    public static FilmFilter getBookmarkFilter() {
        FilmFilter sf = new FilmFilter(true, "nur Bookmarks");
        sf.clearFilter();
        sf.setOnlyVis(true);
        sf.setOnlyBookmark(true);
        return sf;
    }

    public static void addStandardFilter() {
        ProgData progData = ProgData.getInstance();

        //========================================================
        FilmFilter sf = new FilmFilter(true, "alles anzeigen");
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

        progData.audioFilterWorker.getFilmFilterList().add(sf);

        //========================================================
        sf = new FilmFilter(true, "nur \"neue\" anzeigen");
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

        progData.audioFilterWorker.getFilmFilterList().add(sf);

        //========================================================
        sf = new FilmFilter(true, "nur \"neue\" in HD");
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

        sf.setMinMaxDurVis(true);
        sf.setMinDur(20);
        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);

        sf.setOnlyVis(true);
        sf.setOnlyBookmark(false);
        sf.setOnlyHd(true);
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

        progData.audioFilterWorker.getFilmFilterList().add(sf);

        //========================================================
        progData.audioFilterWorker.getFilmFilterList().add(new FilmFilter(true, P2SeparatorComboBox.SEPARATOR));

        //========================================================
        sf = new FilmFilter(true, "Livestreams");
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
        sf.setOnlyLive(true);
        sf.setOnlyActHistory(false);

        sf.setNotVis(true);
        sf.setNotAbo(false);
        sf.setNotHistory(false);
        sf.setNotDouble(false);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.audioFilterWorker.getFilmFilterList().add(sf);

        //========================================================
        // nur Bookmark
        sf = getBookmarkFilter();
        progData.audioFilterWorker.getFilmFilterList().add(sf);

        //========================================================
        progData.audioFilterWorker.getFilmFilterList().add(new FilmFilter(true, P2SeparatorComboBox.SEPARATOR));

        //========================================================
        sf = new FilmFilter(true, "nur neue Fernsehfilme");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setThemeVis(true);
        sf.setThemeIsExact(false);
        sf.setTheme("#:^(Der |ZDF-)?Fernsehfilm( der Woche|e (&|und) Serien( - Fernsehfilme)?)?$|^Film( \\& Fiktion|e, Dokus(,| &) Kabarett|e? im (Ersten|MDR|NDR|rbb)|Mittwoch im Ersten| und Serie)$|^Kino - (Filme|Stummfilme)$|^(3sat |CH:|Schweizer |Spiel)?Film(kultur|szene)?$");
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(10);
        sf.setMinMaxDurVis(true);
        sf.setMinDur(40);

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
        sf.setNotHistory(false);
        sf.setNotDouble(true);
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.audioFilterWorker.getFilmFilterList().add(sf);

        //========================================================
        sf = new FilmFilter(true, "nur Serien");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setThemeVis(true);
        sf.setThemeIsExact(false);
        sf.setThemeTitleVis(true);
        sf.setThemeTitleAndVis("#:.*#\\d.*|.*Folge \\d.*|.*(Folge \\d).*" +
                "|.*Teil \\d.*|.*S0X E0Y -.*|.*(1 4).*|.*(S\\d E\\d).*" +
                "|.*(S\\d/E\\d).*|.*(S2023/E\\d).*|.*(\\d) (S\\d/E\\d).*" +
                "|.*Folge \\d ... (S\\d E\\d).*|.*(Staffel \\d, Folge \\d).*");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(25);
        sf.setMinMaxDurVis(true);
        sf.setMinDur(10);

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

        progData.audioFilterWorker.getFilmFilterList().add(sf);

        //========================================================
        sf = new FilmFilter(true, "Abendkrimi");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setChannel("ard,zdf");
        sf.setThemeVis(false);
        sf.setThemeIsExact(true);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(true);
        sf.setSomewhere("Krimi,Thriller,Tatort,Film");
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(10);
        sf.setMinMaxDurVis(true);
        sf.setMinDur(40);
        sf.setMinMaxTimeVis(true);
        sf.setMinMaxTimeInvert(false);
        sf.setMinTime(64800);

        sf.setOnlyVis(false);
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
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.audioFilterWorker.getFilmFilterList().add(sf);

        //========================================================
        sf = new FilmFilter(true, "aktuelle Nachrichten");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeIsExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Nachrichten");
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

        progData.audioFilterWorker.getFilmFilterList().add(sf);

        //========================================================
        sf = new FilmFilter(true, "Nachrichten mit Europa UND Brexit");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeIsExact(true);

        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Europa:Brexit");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(true);
        sf.setMinMaxDurVis(true);
        sf.setMaxDur(45);
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
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.audioFilterWorker.getFilmFilterList().add(sf);

        //========================================================
        sf = new FilmFilter(true, "nur ARD ODER ZDF");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setChannel("ard,zdf");
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
        sf.setNotGeo(true);
        sf.setNotFuture(false);

        progData.audioFilterWorker.getFilmFilterList().add(sf);

        //========================================================
        sf = new FilmFilter(true, "keine Beitrage mit: \"Audiodeskription\" und \"Gebärdensprache\" anzeigen");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeIsExact(true);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(true);
        sf.setTitle("#:(?!.*Audiodeskription)(?!.*Gebärdensprache).*");
        sf.setSomewhereVis(false);
        sf.setUrlVis(false);

        sf.setTimeRangeVis(false);
        sf.setMinMaxDurVis(false);
        sf.setMinMaxTimeVis(false);
        sf.setMinMaxTimeInvert(false);

        sf.setOnlyVis(false);
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

        progData.audioFilterWorker.getFilmFilterList().add(sf);
    }
}
