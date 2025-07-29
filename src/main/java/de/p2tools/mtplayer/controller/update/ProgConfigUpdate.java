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


package de.p2tools.mtplayer.controller.update;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;

public class ProgConfigUpdate {
    // hier werden geänderte Programmeinstellungen/Funktionen angepasst,
    // muss immer nur einmal laufen!!
    private ProgConfigUpdate() {
    }

    public static void setUpdateDone() {
        ProgConfig.SYSTEM_AFTER_UPDATE_FILTER.setValue(true);
        ProgConfig.SYSTEM_AFTER_UPDATE_THEME_EXACT_FILTER.setValue(true);
        ProgConfig.SYSTEM_AFTER_UPDATE_RBTV.setValue(true);
        ProgConfig.SYSTEM_ABO_START_TIME.setValue(true); // für Version 17
        ProgConfig.SYSTEM_CHANGE_LOG_DIR.setValue(true); // für Version 17
        ProgConfig.SYSTEM_SMALL_FILTER.setValue(true); // für Version 20
    }

    public static void update() {
        //SYSTEM_SHOW_DIACRITICS kommt weg
        ProgConfig.SYSTEM_REMOVE_DIACRITICS.setValue(!ProgConfig.SYSTEM_SHOW_DIACRITICS.getValue());
        ProgConfig.SYSTEM_SHOW_DIACRITICS.bind(ProgConfig.SYSTEM_REMOVE_DIACRITICS.not());

        if (!ProgConfig.SYSTEM_AFTER_UPDATE_FILTER.getValue()) {
            // dann müssen die gespeicherten Filter aktualisiert werden
            final int FILTER_DAYS_MAX__OLD = 30; // ist der alte Wert für "alles"

            if (ProgData.getInstance().filterWorker.getActFilterSettings().getTimeRange() == FILTER_DAYS_MAX__OLD) {
                ProgData.getInstance().filterWorker.getActFilterSettings().setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);
            }
            ProgData.getInstance().filterWorker.getFilmFilterList().forEach(sf -> {
                if (sf.getTimeRange() == FILTER_DAYS_MAX__OLD) {
                    sf.setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);
                }
            });
        }

        if (!ProgConfig.SYSTEM_AFTER_UPDATE_THEME_EXACT_FILTER.getValue()) {
            // theme exact wurde erweitert, theme/exactTheme wird getrennt gespeichert
            FilmFilter filmFilter = ProgData.getInstance().filterWorker.getActFilterSettings();
            if (filmFilter.isThemeIsExact()) {
                filmFilter.setExactTheme(filmFilter.getTheme());
            }

            ProgData.getInstance().filterWorker.getFilmFilterList().forEach(sf -> {
                // exactTheme ist neu
                if (sf.isThemeIsExact()) {
                    sf.setExactTheme(sf.getTheme());
                }
            });
        }

        if (!ProgConfig.SYSTEM_AFTER_UPDATE_RBTV.getValue()) {
            // "rbtv" und "Radio Bremen TV" wird umbenannt in "RBTV"
            FilmFilter filmFilter = ProgData.getInstance().filterWorker.getActFilterSettings();
            if (filmFilter.channelProperty().getValueSafe().contains("rbtv") ||
                    filmFilter.channelProperty().getValueSafe().contains("Radio Bremen TV")) {
                filmFilter.setChannel(filmFilter.channelProperty().getValueSafe().replaceAll("rbtv", "RBTV"));
                filmFilter.setChannel(filmFilter.channelProperty().getValueSafe().replaceAll("Radio Bremen TV", "RBTV"));
            }

            ProgData.getInstance().filterWorker.getFilmFilterList().forEach(sf -> {
                if (sf.channelProperty().getValueSafe().contains("rbtv") ||
                        sf.channelProperty().getValueSafe().contains("Radio Bremen TV")) {
                    sf.setChannel(sf.channelProperty().getValueSafe().replaceAll("rbtv", "RBTV"));
                    sf.setChannel(sf.channelProperty().getValueSafe().replaceAll("Radio Bremen TV", "RBTV"));
                }
            });

            ProgData.getInstance().aboList.forEach(aboData -> {
                if (aboData.getChannel().contains("rbtv") ||
                        aboData.getChannel().contains("Radio Bremen TV")) {
                    aboData.setChannel(aboData.getChannel().replaceAll("rbtv", "RBTV"));
                    aboData.setChannel(aboData.getChannel().replaceAll("Radio Bremen TV", "RBTV"));
                }
            });
        }

        if (!ProgConfig.SYSTEM_ABO_START_TIME.getValue()) {
            // die Abo-Startzeit ist jetzt nur noch 12:20 anstatt 12:30.00
            ProgData.getInstance().aboList.forEach(aboData -> {
                if (aboData.getStartTime().length() > 5) {
                    aboData.setStartTime(aboData.getStartTime().substring(0, 5));
                }
            });
            ProgData.getInstance().downloadList.forEach(downloadData -> {
                downloadData.setStartTime("");
            });
        }

        if (!ProgConfig.SYSTEM_CHANGE_LOG_DIR.getValue()) {
            // dann sind noch alte LogDir Einstellungen gespeichert
            final String logDir = ProgConfig.SYSTEM_LOG_DIR.getValueSafe();
            final String standardDir = ProgInfos.getStandardLogDirectory_String();
            if (logDir.equals(standardDir)) {
                // wenn eh der StandardPfad drin steht, dann löschen
                ProgConfig.SYSTEM_LOG_DIR.setValue("");
            }
        }

        if (!ProgConfig.SYSTEM_SMALL_FILTER.getValue()) {
            ProgData.getInstance().filterWorker.getActFilterSettings()
                    .copyTo(ProgData.getInstance().filterWorker.getStoredActFilterSettings());
        }

        setUpdateDone();
    }
}
