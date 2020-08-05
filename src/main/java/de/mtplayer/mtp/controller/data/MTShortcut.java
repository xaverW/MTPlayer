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


package de.mtplayer.mtp.controller.data;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.p2tools.p2Lib.tools.shortcut.PShortcut;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MTShortcut {

    // Menü
    public static final PShortcut SHORTCUT_SEARCH_MEDIACOLLECTION =
            new PShortcut(ProgConfig.SHORTCUT_SEARCH_MEDIACOLLECTION.getStringProperty(), ProgConfig.SHORTCUT_SEARCH_MEDIACOLLECTION.getInitValue(),
                    "Mediensammlung durchsuchen",
                    "Der Dialog zum Durchsuchen der Mediensammlung wird angezeigt.");
    public static final PShortcut SHORTCUT_QUIT_PROGRAM =
            new PShortcut(ProgConfig.SHORTCUT_QUIT_PROGRAM.getStringProperty(), ProgConfig.SHORTCUT_QUIT_PROGRAM.getInitValue(),
                    "Programm beenden",
                    "Das Programm wird beendet. Wenn noch ein Download läuft, wird in einem Dialog abgefragt, was getan werden soll.");
    public static final PShortcut SHORTCUT_QUIT_PROGRAM_WAIT =
            new PShortcut(ProgConfig.SHORTCUT_QUIT_PROGRAM_WAIT.getStringProperty(), ProgConfig.SHORTCUT_QUIT_PROGRAM_WAIT.getInitValue(),
                    "Programm beenden, Downloads abwarten",
                    "Das Programm wird beendet. Wenn noch ein Download läuft, wird dieser noch abgeschlossen und " +
                            "das Programm wartet auf den Download. Der Dialog mit der Abfrage was getan werden soll, wird aber übersprungen.");

    // Tabelle Filme
    public static final PShortcut SHORTCUT_PLAY_FILM =
            new PShortcut(ProgConfig.SHORTCUT_PLAY_FILM.getStringProperty(), ProgConfig.SHORTCUT_PLAY_FILM.getInitValue(),
                    "Film abspielen",
                    "Der markierte Film in der Tabelle \"Filme\" wird abgespielt.");
    public static final PShortcut SHORTCUT_SAVE_FILM =
            new PShortcut(ProgConfig.SHORTCUT_SAVE_FILM.getStringProperty(), ProgConfig.SHORTCUT_SAVE_FILM.getInitValue(),
                    "Film speichern",
                    "Der markierte Film in der Tabelle \"Filme\" wird aufgezeichnet.");

    public static final PShortcut SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION =
            new PShortcut(ProgConfig.SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION.getStringProperty(), ProgConfig.SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION.getInitValue(),
                    "Filmtitel in Mediensammlung suchen",
                    "Der Titel des markierten Films in der Tabelle \"Filme\" wird in der Mediensammlung gesucht.");

    // Tabelle Download
    public static final PShortcut SHORTCUT_DOWNLOAD_START =
            new PShortcut(ProgConfig.SHORTCUT_DOWNLOAD_START.getStringProperty(), ProgConfig.SHORTCUT_DOWNLOAD_START.getInitValue(),
                    "Download starten",
                    "Der markierte Download in der Tabelle \"Downloads\" wird gestartet.");

    public static final PShortcut SHORTCUT_DOWNLOAD_STOP =
            new PShortcut(ProgConfig.SHORTCUT_DOWNLOAD_STOP.getStringProperty(), ProgConfig.SHORTCUT_DOWNLOAD_STOP.getInitValue(),
                    "Download stoppen",
                    "Der markierte Download in der Tabelle \"Downloads\" wird gestoppt.");
    public static final PShortcut SHORTCUT_DOWNLOAD_CHANGE =
            new PShortcut(ProgConfig.SHORTCUT_DOWNLOAD_CHANGE.getStringProperty(), ProgConfig.SHORTCUT_DOWNLOAD_CHANGE.getInitValue(),
                    "Download ändern",
                    "Der markierte Download in der Tabelle \"Downloads\" kann geändert werden.");
    public static final PShortcut SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIACOLLECTION =
            new PShortcut(ProgConfig.SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIACOLLECTION.getStringProperty(), ProgConfig.SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIACOLLECTION.getInitValue(),
                    "Download in Mediensammlung suchen",
                    "Der Titel des markierten Downloads in der Tabelle \"Downloads\" wird in der Mediensammlung gesucht.");
    public static final PShortcut SHORTCUT_DOWNLOADS_UPDATE =
            new PShortcut(ProgConfig.SHORTCUT_DOWNLOADS_UPDATE.getStringProperty(), ProgConfig.SHORTCUT_DOWNLOADS_UPDATE.getInitValue(),
                    "Downloads aktualisieren",
                    "Die Liste der Downloads in der Tabelle \"Downloads\" wird aktualisiert.");
    public static final PShortcut SHORTCUT_DOWNLOADS_CLEAN_UP =
            new PShortcut(ProgConfig.SHORTCUT_DOWNLOADS_CLEAN_UP.getStringProperty(), ProgConfig.SHORTCUT_DOWNLOADS_CLEAN_UP.getInitValue(),
                    "Downloads aufräumen",
                    "Die Liste der Downloads in der Tabelle \"Downloads\" wird aufgeräumt.");


    private static ObservableList<PShortcut> shortcutList = FXCollections.observableArrayList();

    public MTShortcut() {
        shortcutList.add(SHORTCUT_SEARCH_MEDIACOLLECTION);
        shortcutList.add(SHORTCUT_QUIT_PROGRAM);
        shortcutList.add(SHORTCUT_QUIT_PROGRAM_WAIT);

        shortcutList.add(SHORTCUT_PLAY_FILM);
        shortcutList.add(SHORTCUT_SAVE_FILM);
        shortcutList.add(SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION);

        shortcutList.add(SHORTCUT_DOWNLOAD_START);
        shortcutList.add(SHORTCUT_DOWNLOAD_STOP);
        shortcutList.add(SHORTCUT_DOWNLOAD_CHANGE);
        shortcutList.add(SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIACOLLECTION);
        shortcutList.add(SHORTCUT_DOWNLOADS_UPDATE);
        shortcutList.add(SHORTCUT_DOWNLOADS_CLEAN_UP);
    }

    public static synchronized ObservableList<PShortcut> getShortcutList() {
        return shortcutList;
    }
}