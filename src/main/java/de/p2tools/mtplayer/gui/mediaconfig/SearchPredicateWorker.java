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


package de.p2tools.mtplayer.gui.mediaconfig;

import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.mtplayer.controller.mediadb.MediaData;
import de.p2tools.p2lib.mtfilter.Filter;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class SearchPredicateWorker {
    private SearchPredicateWorker() {
    }

    public static Predicate<MediaData> getPredicateMediaData(String searchStr, boolean showEmpty) {
        final String search = searchStr.toLowerCase().trim();
        return media -> {
            if (search.isEmpty()) {
                return showEmpty;
            }

            // wenn einer passt, dann ists gut
            final Pattern p = Filter.makePattern(search);
            if (p != null) {
                return p.matcher(media.getName()).matches();
            } else {
                return media.getName().toLowerCase().contains(search);
            }
        };
    }

    public static Predicate<MediaData> getPredicateMediaData_(String searchStr, boolean showEmpty) {
        final String search = searchStr.toLowerCase().trim();
        Filter filter = new Filter(search, true);

        return media -> {
            if (search.isEmpty()) {
                return showEmpty;
            }

            // wenn einer passt, dann ists gut
            if (filter.filterArr.length == 1) {
                final Pattern p = Filter.makePattern(search);
                if (p != null) {
                    return p.matcher(media.getName()).matches();
                } else {
                    return media.getName().toLowerCase().contains(filter.filter);
                }
            }

            if (filter.filterAnd) {
                // Suchbegriffe müssen alle passen
                for (final String s : filter.filterArr) {
                    // dann jeden Suchbegriff checken
                    if (!media.getName().toLowerCase().contains(s)) {
                        return false;
                    }
                }
                return true;

            } else {
                // nur ein Suchbegriff muss passen
                for (final String s : filter.filterArr) {
                    // dann jeden Suchbegriff checken
                    if (media.getName().toLowerCase().contains(s)) {
                        return true;
                    }
                }
            }

            // nix wars
            return false;
        };
    }

    public static Predicate<HistoryData> getPredicateHistoryData(boolean thema, boolean title, String searchStr, boolean showEmpty) {
        // gibt Themen/Titel mit "," oder ":" -> also gehts nicht mit dem üblichen Suchen mit ":" und "," :(

        final String search = searchStr.toLowerCase().trim();
        return historyData -> {
            if (search.isEmpty()) {
                return showEmpty;
            }

            final Pattern p = Filter.makePattern(search);
            if (p != null) {
                if (thema) {
                    return p.matcher(historyData.getTheme()).matches();
                } else if (title) {
                    return p.matcher(historyData.getTitle()).matches();
                } else {
                    return p.matcher(historyData.getTheme()).matches() || p.matcher(historyData.getTitle()).matches();
                }
            } else {
                if (thema) {
                    return (historyData.getTheme().toLowerCase().contains(search));
                } else if (title) {
                    return (historyData.getTitle().toLowerCase().contains(search));
                } else {
                    return (historyData.getTheme().toLowerCase().contains(search)
                            || historyData.getTitle().toLowerCase().contains(search));
                }
            }
        };
    }
}
