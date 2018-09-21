/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
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


package de.mtplayer.mtp.controller.filmlist;

import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;

import javax.swing.event.EventListenerList;

public class NotifyProgress {
    public enum NOTIFY {START, PROGRESS, LOADED, FINISHED}

    public final EventListenerList listeners = new EventListenerList();

    public void notifyFinishedOk() {
        notifyEvent(NotifyProgress.NOTIFY.FINISHED, ListenerFilmlistLoadEvent.getEmptyEvent());
    }

    public void notifyEvent(NOTIFY notify, ListenerFilmlistLoadEvent event) {
        try {
            Platform.runLater(() -> {

                for (final ListenerFilmlistLoad l : listeners.getListeners(ListenerFilmlistLoad.class)) {
                    switch (notify) {
                        case START:
                            l.start(event);
                            break;
                        case PROGRESS:
                            l.progress(event);
                            break;
                        case LOADED:
                            l.loaded(event);
                            break;
                        case FINISHED:
                            l.finished(event);
                            break;
                    }
                }

            });
        } catch (final Exception ex) {
            PLog.errorLog(912045120, ex);
        }
    }
}
