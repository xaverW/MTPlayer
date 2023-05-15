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


package de.p2tools.mtplayer;

import de.p2tools.mtplayer.controller.config.ProgShortcut;
import de.p2tools.p2lib.tools.shortcut.PShortcut;
import javafx.scene.Scene;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class ShortKeyFactory {

    public static String SHORT_CUT_LEER = "              ";

    private ShortKeyFactory() {
    }


    public static void addShortKey(Scene scene) {
        PShortcut pShortcut;
        KeyCombination kc;
        Runnable rn;

        // Info
        pShortcut = ProgShortcut.SHORTCUT_SHOW_INFOS;
        pShortcut.actShortcutProperty().addListener(c -> {
            System.out.println("--->");
        });
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::setInfos;
        scene.getAccelerators().put(kc, rn);

        // Filter
        pShortcut = ProgShortcut.SHORTCUT_SHOW_FILTER;
        pShortcut.actShortcutProperty().addListener(c -> {
            System.out.println("--->");
        });
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::setFilter;
        scene.getAccelerators().put(kc, rn);

        // Mediensammlung
        pShortcut = ProgShortcut.SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION;
        pShortcut.actShortcutProperty().addListener(c -> {
            System.out.println("--->");
        });
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::setMediaCollection;
        scene.getAccelerators().put(kc, rn);

        // FilmInfos
        pShortcut = ProgShortcut.SHORTCUT_INFO_FILM;
        pShortcut.actShortcutProperty().addListener(c -> {
            System.out.println("--->");
        });
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::showFilmInfos;
        scene.getAccelerators().put(kc, rn);


        // Thema kopieren
        pShortcut = ProgShortcut.SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD;
        pShortcut.actShortcutProperty().addListener(c -> {
            System.out.println("--->");
        });
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::copyTheme;
        scene.getAccelerators().put(kc, rn);

        // Titel kopieren
        pShortcut = ProgShortcut.SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD;
        pShortcut.actShortcutProperty().addListener(c -> {
            System.out.println("--->");
        });
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::copyTitle;
        scene.getAccelerators().put(kc, rn);

        // Blacklist
        pShortcut = ProgShortcut.SHORTCUT_ADD_BLACKLIST;
        pShortcut.actShortcutProperty().addListener(c -> {
            System.out.println("--->");
        });
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::addBlacklist;
        scene.getAccelerators().put(kc, rn);

        // Blacklist, Thema
        pShortcut = ProgShortcut.SHORTCUT_ADD_BLACKLIST_THEME;
        pShortcut.actShortcutProperty().addListener(c -> {
            System.out.println("--->");
        });
        kc = KeyCodeCombination.keyCombination(pShortcut.getActShortcut());
        rn = MTPlayerFactory::addBlacklistTheme;
        scene.getAccelerators().put(kc, rn);
    }
}
