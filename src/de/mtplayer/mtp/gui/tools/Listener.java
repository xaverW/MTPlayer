/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

package de.mtplayer.mtp.gui.tools;

import de.mtplayer.mLib.tools.Log;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.EventListener;


public class Listener implements EventListener {

    static int count = 0;

    public static final int EREIGNIS_BLACKLIST_GEAENDERT = count++;
    public static final int EREIGNIS_TIMER = count++;

    public static final int EREIGNIS_MEDIA_DB_START = count++;
    public static final int EREIGNIS_MEDIA_DB_STOP = count++;

    public static final int EREIGNIS_REPLACELIST_CHANGED = count++;

    public static final int EREIGNIS_GUI_ORG_TITEL = count++;
    public static final int EREIGNIS_GUI_PROGRAMM_AKTUELL = count++;
    public static final int EREIGNIS_GUI_UPDATE_VERFUEGBAR = count++;
    public static final int EREIGNIS_GUI_COLOR_CHANGED = count++;


    public int[] event = {-1};
    public String eventClass = "";
    private static final ArrayList<Listener> listeners = new ArrayList<>();

    public Listener(int event, String eventClass) {
        this.event = new int[]{event};
        this.eventClass = eventClass;
    }

    public Listener(int[] event, String eventClass) {
        this.event = event;
        this.eventClass = eventClass;
    }

    public void ping() {
    }

    public static synchronized void addListener(Listener listener) {
        System.out.println("Anz. Listener: " + listeners.size());
        listeners.add(listener);
    }

    public static synchronized void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public static synchronized void notify(int eventNotify, String eventClass) {

        listeners.stream().forEach(listener -> {

            for (final int event : listener.event) {

                if (event == eventNotify && !listener.eventClass.equals(eventClass)) {
                    // um einen Kreislauf zu verhindern
                    try {
                        //System.out.println("Ping: " + ereignis);
                        listener.pingen();
                    } catch (final Exception ex) {
                        Log.errorLog(512021043, ex);
                    }
                }

            }


        });

    }

    private void pingen() {
        try {
            Platform.runLater(() -> ping());
        } catch (final Exception ex) {
            Log.errorLog(698989743, ex);
        }
    }

}
