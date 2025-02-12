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

package de.p2tools.mtplayer.controller.config;

import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.EventListener;


public class PListener implements EventListener {
    private static final ArrayList<PListener> listeners = new ArrayList<>();
    static int count = 0;
    public static final int EVENT_BLACKLIST_CHANGED = count++;
    public static final int EVENT_DIACRITIC_CHANGED = count++;
    public static final int EVENT_TIMER_SECOND = count++;
    public static final int EVENT_TIMER_HALF_SECOND = count++;
    public static final int EVENT_MEDIA_DB_START = count++;
    public static final int EVENT_MEDIA_DB_STOP = count++;
    public static final int EVENT_HISTORY_CHANGED = count++;
    public static final int EVENT_SET_DATA_CHANGED = count++;
    public static final int EVENT_FILTER_CHANGED = count++;
    public static final int EVENT_LIVE_FILTER_CHANGED = count++;
    public static final int EVENT_FILM_BUTTON_CHANGED = count++;
    public static final int EVENT_REFRESH_TABLE = ++count;
    public static final int EVENT_ABO_HIT_CHANGED = ++count;

    public int[] event = {-1};
    public String eventClass = "";

    public PListener() {
    }

    public PListener(int event, String eventClass) {
        this.event = new int[]{event};
        this.eventClass = eventClass;
    }

    public PListener(int[] event, String eventClass) {
        this.event = event;
        this.eventClass = eventClass;
    }

    public static synchronized void addListener(PListener listener) {
        P2Log.debugLog("Anz. Listener: " + listeners.size());
        listeners.add(listener);
    }

    public static synchronized void removeListener(PListener listener) {
        listeners.remove(listener);
    }

    public static synchronized void notify(int eventNotify, String eventClass) {
        listeners.forEach(listener -> {
            for (final int event : listener.event) {
                // um einen Kreislauf zu verhindern
                if (event == eventNotify && !listener.eventClass.equals(eventClass)) {
                    listener.pingen();
                }

            }
        });
    }

    public void pingFx() {
        // das passiert im application thread
    }

    public void ping() {
        // das ist asynchron zum application thread
    }

    private void pingen() {
        try {
            ping();
            Platform.runLater(this::pingFx);
        } catch (final Exception ex) {
            P2Log.errorLog(698989743, ex);
        }
    }
}
