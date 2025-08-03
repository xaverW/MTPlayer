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

package de.p2tools.mtplayer.controller.filterfilm;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public final class FastFilmFilter extends FastFilmFilterProps implements Filter {

    private final PauseTransition pause = new PauseTransition(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue())); // nach Ablauf wird Änderung gemeldet - oder nach Return

    public FastFilmFilter() {
        initFilter();
    }

    public void clearFilter() {
        // alle Filter löschen, Button Black bleibt, wie er ist
        setFilterTerm("");
        if (ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
            // dann noch melden
            reportFilterReturn();
        }
    }

    public void reportFilterReturn() {
        // sind die ComboBoxen wenn return gedrückt wird
        P2Log.debugLog("reportFilterReturn");
        pause.stop();
//        PListener.notify(PListener.EVENT_FILTER_CHANGED, FilmFilter.class.getSimpleName());
        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_CHANGED);
    }

    private void initFilter() {
        pause.setOnFinished(event -> {
//            PListener.notify(PListener.EVENT_FILTER_CHANGED, FastFilmFilter.class.getSimpleName());
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_CHANGED);
        });
        pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        ProgConfig.SYSTEM_FILTER_WAIT_TIME.addListener((observable, oldValue, newValue) -> {
            P2Log.debugLog("SYSTEM_FILTER_WAIT_TIME: " + ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue());
            pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        });

        clearFilter();

        filterTermProperty().addListener(l -> {
            setTxtFilterChange();
        });
    }

    private void setTxtFilterChange() {
        if (ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
            //dann wird erst nach "RETURN" gestartet
            pause.stop();

        } else {
            pause.playFromStart();
        }
    }
}
