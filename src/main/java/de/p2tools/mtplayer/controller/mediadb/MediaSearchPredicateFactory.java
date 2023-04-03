/*
 * P2tools Copyright (C) 2020 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.mediadb;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.p2lib.mtfilter.Filter;
import de.p2tools.p2lib.mtfilter.FilterCheck;

import java.util.function.Predicate;

public class MediaSearchPredicateFactory {
    private MediaSearchPredicateFactory() {
    }

    public static Predicate<MediaData> getPredicateMediaData(String searchStr, boolean showEmpty) {
        final String search = searchStr.trim();
        return media -> {
            if (search.isEmpty()) {
                return showEmpty;
            }

            Filter filter = new Filter(search, true);
            return FilterCheck.check(filter, media.getName());
        };
    }

    public static Predicate<HistoryData> getPredicateHistoryData(String searchStr, int searachIn) {
        final String search = searchStr.trim();
        return historyData -> {
            if (search.isEmpty()) {
                return true;
            }

            Filter filter = new Filter(search, true);
            if (searachIn == ProgConst.MEDIA_COLLECTION_SEARCH_IN_THEME) {
                return FilterCheck.check(filter, historyData.getTheme());
            } else if (searachIn == ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL) {
                return FilterCheck.check(filter, historyData.getTitle());
            } else {
                return FilterCheck.check(filter, historyData.getTheme()) ||
                        FilterCheck.check(filter, historyData.getTitle());
            }
        };
    }
}
