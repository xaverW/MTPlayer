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


package de.p2tools.mtplayer.controller.data;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.p2lib.tools.shortcut.PShortcut;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashSet;

public class MTShortcut {

    // Menü
    public static final PShortcut SHORTCUT_SEARCH_MEDIACOLLECTION =
            new PShortcut(ProgConfig.SHORTCUT_SEARCH_MEDIA_COLLECTION, ProgConfig.SHORTCUT_SEARCH_MEDIA_COLLECTION_INIT,
                    "Mediensammlung durchsuchen",
                    "Der Dialog zum Durchsuchen der Mediensammlung wird angezeigt.");

    public static final PShortcut SHORTCUT_QUIT_PROGRAM =
            new PShortcut(ProgConfig.SHORTCUT_QUIT_PROGRAM, ProgConfig.SHORTCUT_QUIT_PROGRAM_INIT,
                    "Programm beenden",
                    "Das Programm wird beendet. Wenn noch ein Download läuft, wird in einem Dialog abgefragt, was getan werden soll.");

    public static final PShortcut SHORTCUT_QUIT_PROGRAM_WAIT =
            new PShortcut(ProgConfig.SHORTCUT_QUIT_PROGRAM_WAIT, ProgConfig.SHORTCUT_QUIT_PROGRAM_WAIT_INIT,
                    "Programm beenden, Downloads abwarten",
                    "Das Programm wird beendet. Wenn noch ein Download läuft, wird dieser noch abgeschlossen und " +
                            "das Programm wartet auf den Download. Der Dialog mit der Abfrage was getan werden soll, wird aber übersprungen.");

    // Tabelle Filme
    public static final PShortcut SHORTCUT_FILM_SHOWN =
            new PShortcut(ProgConfig.SHORTCUT_FILM_SHOWN, ProgConfig.SHORTCUT_FILM_SHOWN_INIT,
                    "Film als gesehen markieren",
                    "Der Film wird zur Liste der gesehenen Filme hinzugefügt.");

    public static final PShortcut SHORTCUT_FILM_NOT_SHOWN =
            new PShortcut(ProgConfig.SHORTCUT_FILM_NOT_SHOWN, ProgConfig.SHORTCUT_FILM_NOT_SHOWN_INIT,
                    "Film als ungesehen markieren",
                    "Der Film wird aus der Liste der gesehenen Filme gelöscht.");

    public static final PShortcut SHORTCUT_ADD_BLACKLIST =
            new PShortcut(ProgConfig.SHORTCUT_ADD_BLACKLIST, ProgConfig.SHORTCUT_ADD_BLACKLIST_INIT,
                    "Einen neuen Eintrag in der Blacklist erstellen",
                    "Einen neuen Eintrag in der Blacklist erstellen, der markierte Film dient dabei Vorlage.");

    public static final PShortcut SHORTCUT_SHOW_FILTER =
            new PShortcut(ProgConfig.SHORTCUT_SHOW_FILTER, ProgConfig.SHORTCUT_SHOW_FILTER_INIT,
                    "Filter anzeigen",
                    "In der Tabelle \"Filme\", \"Downloads\" und \"Abos\" die Filter anzeigen.");

    public static final PShortcut SHORTCUT_SHOW_INFOS =
            new PShortcut(ProgConfig.SHORTCUT_SHOW_INFOS, ProgConfig.SHORTCUT_SHOW_INFOS_INIT,
                    "Infos anzeigen",
                    "In der Tabelle \"Filme\", \"Downloads\" und \"Abos\" die Infos anzeigen.");

    public static final PShortcut SHORTCUT_INFO_FILM =
            new PShortcut(ProgConfig.SHORTCUT_INFO_FILM, ProgConfig.SHORTCUT_INFO_FILM_INIT,
                    "Filminformation anzeigen",
                    "In der Tabelle \"Filme\" und \"Downloads\" die Infos des markierten Films anzeigen.");

    public static final PShortcut SHORTCUT_PLAY_FILM =
            new PShortcut(ProgConfig.SHORTCUT_PLAY_FILM, ProgConfig.SHORTCUT_PLAY_FILM_INIT,
                    "Film abspielen",
                    "Der markierte Film in der Tabelle \"Filme\" wird abgespielt.");

    public static final PShortcut SHORTCUT_SAVE_FILM =
            new PShortcut(ProgConfig.SHORTCUT_SAVE_FILM, ProgConfig.SHORTCUT_SAVE_FILM_INIT,
                    "Film speichern",
                    "Der markierte Film in der Tabelle \"Filme\" wird aufgezeichnet.");

    public static final PShortcut SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION =
            new PShortcut(ProgConfig.SHORTCUT_SEARCH_FILM_IN_MEDIA_COLLECTION, ProgConfig.SHORTCUT_SEARCH_FILM_IN_MEDIA_COLLECTION_INIT,
                    "Filmtitel in Mediensammlung suchen",
                    "Der Titel des markierten Films in der Tabelle \"Filme\" wird in der Mediensammlung gesucht.");

    public static final PShortcut SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD =
            new PShortcut(ProgConfig.SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD, ProgConfig.SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD_INIT,
                    "Filmthema in die Zwischenablage Kopieren",
                    "Das Thema des markierten Films in der Tabelle \"Filme\" wird in die Zwischenablage kopiert.");

    public static final PShortcut SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD =
            new PShortcut(ProgConfig.SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD, ProgConfig.SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD_INIT,
                    "Filmtitel in die Zwischenablage Kopieren",
                    "Der Titel des markierten Films in der Tabelle \"Filme\" wird in die Zwischenablage kopiert.");

    // Tabelle Download
    public static final PShortcut SHORTCUT_DOWNLOAD_START =
            new PShortcut(ProgConfig.SHORTCUT_DOWNLOAD_START, ProgConfig.SHORTCUT_DOWNLOAD_START_INIT,
                    "Download starten",
                    "Der markierte Download in der Tabelle \"Downloads\" wird gestartet.");

    public static final PShortcut SHORTCUT_DOWNLOAD_STOP =
            new PShortcut(ProgConfig.SHORTCUT_DOWNLOAD_STOP, ProgConfig.SHORTCUT_DOWNLOAD_STOP_INIT,
                    "Download stoppen",
                    "Der markierte Download in der Tabelle \"Downloads\" wird gestoppt.");

    public static final PShortcut SHORTCUT_DOWNLOAD_CHANGE =
            new PShortcut(ProgConfig.SHORTCUT_DOWNLOAD_CHANGE, ProgConfig.SHORTCUT_DOWNLOAD_CHANGE_INIT,
                    "Download ändern",
                    "Der markierte Download in der Tabelle \"Downloads\" kann geändert werden.");

    public static final PShortcut SHORTCUT_DOWNLOAD_UNDO_DELETE =
            new PShortcut(ProgConfig.SHORTCUT_DOWNLOAD_UNDO_DELETE, ProgConfig.SHORTCUT_DOWNLOAD_UNDO_DELETE_INIT,
                    "Gelöschten Download wieder anlegen",
                    "Der markierte Download in der Tabelle \"Downloads\" kann geändert werden.");

    public static final PShortcut SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIACOLLECTION =
            new PShortcut(ProgConfig.SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIA_COLLECTION, ProgConfig.SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIA_COLLECTION_INIT,
                    "Download in Mediensammlung suchen",
                    "Der Titel des markierten Downloads in der Tabelle \"Downloads\" wird in der Mediensammlung gesucht.");

    public static final PShortcut SHORTCUT_DOWNLOADS_UPDATE =
            new PShortcut(ProgConfig.SHORTCUT_DOWNLOAD_UPDATE, ProgConfig.SHORTCUT_DOWNLOADS_UPDATE_INIT,
                    "Downloads aktualisieren",
                    "Die Liste der Downloads in der Tabelle \"Downloads\" wird aktualisiert.");

    public static final PShortcut SHORTCUT_DOWNLOADS_CLEAN_UP =
            new PShortcut(ProgConfig.SHORTCUT_DOWNLOAD_CLEAN_UP, ProgConfig.SHORTCUT_DOWNLOAD_CLEAN_UP_INIT,
                    "Downloads aufräumen",
                    "Die Liste der Downloads in der Tabelle \"Downloads\" wird aufgeräumt.");

//    public static final PShortcut SHORTCUT_EXTERN_PROGRAM =
//            new PShortcut(ProgConfig.SHORTCUT_EXTERN_PROGRAM, ProgConfig.SHORTCUT_EXTERN_PROGRAM_INIT,
//                    "Externes Programm starten",
//                    "Damit wird ein externes Programm gestartet. Das Programm kann in den Einstellungen " +
//                            "unter \"Programme\" ausgewählt werden.");


    private static ObservableList<PShortcut> shortcutList = FXCollections.observableArrayList();

    public MTShortcut() {
        shortcutList.add(SHORTCUT_SEARCH_MEDIACOLLECTION);
        shortcutList.add(SHORTCUT_QUIT_PROGRAM);
        shortcutList.add(SHORTCUT_QUIT_PROGRAM_WAIT);

        shortcutList.add(SHORTCUT_FILM_SHOWN);
        shortcutList.add(SHORTCUT_FILM_NOT_SHOWN);
        shortcutList.add(SHORTCUT_ADD_BLACKLIST);
        shortcutList.add(SHORTCUT_SHOW_FILTER);
        shortcutList.add(SHORTCUT_SHOW_INFOS);
        shortcutList.add(SHORTCUT_INFO_FILM);
        shortcutList.add(SHORTCUT_PLAY_FILM);
        shortcutList.add(SHORTCUT_SAVE_FILM);
        shortcutList.add(SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION);
        shortcutList.add(SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD);
        shortcutList.add(SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD);

        shortcutList.add(SHORTCUT_DOWNLOAD_START);
        shortcutList.add(SHORTCUT_DOWNLOAD_STOP);
        shortcutList.add(SHORTCUT_DOWNLOAD_CHANGE);
        shortcutList.add(SHORTCUT_DOWNLOAD_UNDO_DELETE);
        shortcutList.add(SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIACOLLECTION);
        shortcutList.add(SHORTCUT_DOWNLOADS_UPDATE);
        shortcutList.add(SHORTCUT_DOWNLOADS_CLEAN_UP);
//        shortcutList.add(SHORTCUT_EXTERN_PROGRAM);
    }

    public static synchronized ObservableList<PShortcut> getShortcutList() {
        return shortcutList;
    }

    public static synchronized boolean checkDoubleShortcutList() {
        HashSet<String> hashSet = new HashSet<>();
        for (PShortcut ps : shortcutList) {
            if (!hashSet.add(ps.getActShortcut())) {
                return true;
            }
        }
        return false;
    }
}
