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
import de.p2tools.mtplayer.controller.data.history.HistoryData;
import de.p2tools.p2lib.mtfilter.Filter;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import javafx.beans.property.IntegerProperty;

import java.util.function.Predicate;

public class MediaSearchPredicateFactory {
    private MediaSearchPredicateFactory() {
    }

    public static Predicate<MediaData> getPredicateMediaData(IntegerProperty searchInProperty, String searchStr) {
        final String search = searchStr.trim();
        return media -> {
            if (search.isEmpty()) {
                return true;
            }

            Filter filter = new Filter(search, true);
            if (searchInProperty.getValue() == ProgConst.MEDIA_SEARCH_THEME_OR_PATH) {
                return FilterCheck.check(filter, media.getPath());
            } else if (searchInProperty.getValue() == ProgConst.MEDIA_SEARCH_TITEL_OR_NAME) {
                return FilterCheck.check(filter, media.getName());
            } else {
                return FilterCheck.check(filter, media.getPath()) ||
                        FilterCheck.check(filter, media.getName());
            }
        };
    }

    public static Predicate<HistoryData> getPredicateHistoryData(IntegerProperty searchInProperty, String searchStr) {
        final String search = searchStr.trim();
        return historyData -> {
            if (search.isEmpty()) {
                return true;
            }

            Filter filter = new Filter(search, true);
            if (searchInProperty.getValue() == ProgConst.MEDIA_SEARCH_THEME_OR_PATH) {
                return FilterCheck.check(filter, historyData.getTheme());
            } else if (searchInProperty.getValue() == ProgConst.MEDIA_SEARCH_TITEL_OR_NAME) {
                return FilterCheck.check(filter, historyData.getTitle());
            } else {
                return FilterCheck.check(filter, historyData.getTheme()) ||
                        FilterCheck.check(filter, historyData.getTitle());
            }
        };
    }
}
