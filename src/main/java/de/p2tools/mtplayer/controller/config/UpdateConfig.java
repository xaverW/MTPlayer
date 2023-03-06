/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.config;

import de.p2tools.p2lib.mtfilter.FilterCheck;

public class UpdateConfig {
    private UpdateConfig() {
    }

    public static void setUpdateDone() {
        ProgConfig.SYSTEM_AFTER_UPDATE_FILTER.setValue(true);
    }

    public static void update() {
        //SYSTEM_SHOW_DIACRITICS kommt weg
        ProgConfig.SYSTEM_REMOVE_DIACRITICS.setValue(!ProgConfig.SYSTEM_SHOW_DIACRITICS.getValue());
        ProgConfig.SYSTEM_SHOW_DIACRITICS.bind(ProgConfig.SYSTEM_REMOVE_DIACRITICS.not());

        if (!ProgConfig.SYSTEM_AFTER_UPDATE_FILTER.getValue()) {
            // dann müssen die gespeicherten Filter aktualisiert werden
            final int FILTER_DAYS_MAX__OLD = 30; // ist der alte Wert für "alles"

            if (ProgData.getInstance().actFilmFilterWorker.getActFilterSettings().getTimeRange() == FILTER_DAYS_MAX__OLD) {
                ProgData.getInstance().actFilmFilterWorker.getActFilterSettings().setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);
            }
            ProgData.getInstance().actFilmFilterWorker.getStoredFilterList().stream().forEach(sf -> {
                if (sf.getTimeRange() == FILTER_DAYS_MAX__OLD) {
                    sf.setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);
                }
            });
        }

        setUpdateDone();
    }
}
