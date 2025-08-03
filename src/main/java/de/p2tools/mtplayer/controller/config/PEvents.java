/*
 * P2tools Copyright (C) 2022 W. Xaver W.Xaver[at]googlemail.com
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

import de.p2tools.p2lib.p2event.P2Events;

public class PEvents extends P2Events {
    // Prog zählt vorwärts
    private static int count = 0;
    public static final int EVENT_BLACKLIST_CHANGED = count++;
    public static final int EVENT_DIACRITIC_CHANGED = count++;
    public static final int EVENT_MEDIA_DB_START = count++;
    public static final int EVENT_MEDIA_DB_STOP = count++;
    public static final int EVENT_HISTORY_CHANGED = count++;
    public static final int EVENT_BOOKMARK_CHANGED = count++;
    public static final int EVENT_SET_DATA_CHANGED = count++;
    public static final int EVENT_FILTER_CHANGED = count++;
    public static final int EVENT_LIVE_FILTER_CHANGED = count++;
    public static final int EVENT_AUDIO_FILTER_CHANGED = count++;
    public static final int EVENT_FILM_BUTTON_CHANGED = count++;
    public static final int EVENT_REFRESH_TABLE = ++count;
    public static final int EVENT_ABO_HIT_CHANGED = ++count;
}
