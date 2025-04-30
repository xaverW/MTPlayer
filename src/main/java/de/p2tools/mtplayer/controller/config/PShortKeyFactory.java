/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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

import de.p2tools.mtplayer.MTPlayerFactory;
import de.p2tools.mtplayer.controller.ProgQuit;
import de.p2tools.p2lib.tools.shortcut.P2ShortcutKey;
import javafx.scene.Scene;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class PShortKeyFactory {

    public static String SHORT_CUT_LEER = "              ";

    private PShortKeyFactory() {
    }

    public static void addShortKey(Scene scene) {
        P2ShortcutKey pShortcut;
        KeyCombination kc;
        Runnable rn;

        // quitt and wait
        pShortcut = PShortcut.SHORTCUT_QUIT_PROGRAM_WAIT;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = () -> ProgQuit.quit(true);
        scene.getAccelerators().put(kc, rn);

        // Filmliste komplett laden
        pShortcut = PShortcut.SHORTCUT_LOAD_FILMLIST;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::loadFilmlist;
        scene.getAccelerators().put(kc, rn);

        // Filmliste aktualisieren
        pShortcut = PShortcut.SHORTCUT_UPDATE_FILMLIST;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::updateFilmlist;
        scene.getAccelerators().put(kc, rn);

        // Center GUI
        pShortcut = PShortcut.SHORTCUT_CENTER_GUI;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::centerGui;
        scene.getAccelerators().put(kc, rn);

        // Minimize GUI
        pShortcut = PShortcut.SHORTCUT_MINIMIZE_GUI;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::minimizeGui;
        scene.getAccelerators().put(kc, rn);

        // Info
        pShortcut = PShortcut.SHORTCUT_SHOW_INFOS;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::setInfos;
        scene.getAccelerators().put(kc, rn);

        // Filter
        pShortcut = PShortcut.SHORTCUT_SHOW_FILTER;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::setFilter;
        scene.getAccelerators().put(kc, rn);

        // Mediensammlung
        pShortcut = PShortcut.SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::setMediaCollection;
        scene.getAccelerators().put(kc, rn);

        // FilmInfos
        pShortcut = PShortcut.SHORTCUT_INFO_FILM;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::showFilmInfos;
        scene.getAccelerators().put(kc, rn);


        // Thema kopieren
        pShortcut = PShortcut.SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::copyTheme;
        scene.getAccelerators().put(kc, rn);

        // Titel kopieren
        pShortcut = PShortcut.SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::copyTitle;
        scene.getAccelerators().put(kc, rn);

        // Blacklist
        pShortcut = PShortcut.SHORTCUT_ADD_BLACKLIST;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::addBlacklist;
        scene.getAccelerators().put(kc, rn);

        // Blacklist, Thema
        pShortcut = PShortcut.SHORTCUT_ADD_BLACKLIST_THEME;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::addBlacklistTheme;
        scene.getAccelerators().put(kc, rn);

        // Blacklist-Einstellungen anzeigen
        pShortcut = PShortcut.SHORTCUT_SHOW_BLACKLIST;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::showBlacklist;
        scene.getAccelerators().put(kc, rn);

        // Undo, gelöschte (Downloads, Abos) wieder herstellen
        pShortcut = PShortcut.SHORTCUT_UNDO_DELETE;
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::undoDels;
        scene.getAccelerators().put(kc, rn);
    }
}
