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

package de.p2tools.mtplayer.gui.tools;

import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.EventListener;


public class Listener implements EventListener {
    static int count = 0;

    public static final int EVENT_BLACKLIST_CHANGED = count++;
    public static final int EVENT_DIACRITIC_CHANGED = count++;
    public static final int EVENT_TIMER = count++;
    public static final int EVENT_TIMER_HALF_SECOND = count++;

    public static final int EVENT_MEDIA_DB_START = count++;
    public static final int EVENT_MEDIA_DB_STOP = count++;

    public static final int EVENT_REPLACELIST_CHANGED = count++;
    public static final int EVENT_GUI_HISTORY_CHANGED = count++;
    public static final int EVEMT_SETDATA_CHANGED = count++;

    public int[] event = {-1};
    public String eventClass = "";
    private static final ArrayList<Listener> listeners = new ArrayList<>();

    public Listener() {
    }

    public Listener(int event, String eventClass) {
        this.event = new int[]{event};
        this.eventClass = eventClass;
    }

    public Listener(int[] event, String eventClass) {
        this.event = event;
        this.eventClass = eventClass;
    }

    public void pingFx() {
        // das passiert im application thread
    }

    public void ping() {
        // das ist asynchron zum application thread
    }

    public static synchronized void addListener(Listener listener) {
        PLog.sysLog("Anz. Listener: " + listeners.size());
        listeners.add(listener);
    }

    public static synchronized void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public static synchronized void notify(int eventNotify, String eventClass) {

        listeners.stream().forEach(listener -> {

            for (final int event : listener.event) {
                // um einen Kreislauf zu verhindern
                if (event == eventNotify && !listener.eventClass.equals(eventClass)) {
                    listener.pingen();
                }

            }


        });

    }

    private void pingen() {
        try {
            ping();
            Platform.runLater(() -> pingFx());
        } catch (final Exception ex) {
            PLog.errorLog(698989743, ex);
        }
    }
}
