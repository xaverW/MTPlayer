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

import de.p2tools.p2lib.tools.shortcut.P2ShortcutKey;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashSet;

public class PShortcut {

    public static final P2ShortcutKey SHORTCUT_CENTER_GUI =
            new P2ShortcutKey(ProgConfig.SHORTCUT_CENTER_GUI, ProgConfig.SHORTCUT_CENTER_INIT,
                    "Center Programm",
                    "Das Programmfenster wird auf dem Bildschirm zentriert positioniert.");

    public static final P2ShortcutKey SHORTCUT_MINIMIZE_GUI =
            new P2ShortcutKey(ProgConfig.SHORTCUT_MINIMIZE_GUI, ProgConfig.SHORTCUT_MINIMIZE_INIT,
                    "Programm-GUI minimieren",
                    "Das Programmfenster wird minimiert.");

    // Menü
    public static final P2ShortcutKey SHORTCUT_SEARCH_MEDIACOLLECTION =
            new P2ShortcutKey(ProgConfig.SHORTCUT_SEARCH_MEDIA_COLLECTION, ProgConfig.SHORTCUT_SEARCH_MEDIA_COLLECTION_INIT,
                    "Mediensammlung",
                    "Der Dialog zum Durchsuchen der Mediensammlung wird angezeigt.");

    public static final P2ShortcutKey SHORTCUT_QUIT_PROGRAM =
            new P2ShortcutKey(ProgConfig.SHORTCUT_QUIT_PROGRAM, ProgConfig.SHORTCUT_QUIT_PROGRAM_INIT,
                    "Programm beenden",
                    "Das Programm wird beendet. Wenn noch ein Download läuft, wird in einem Dialog abgefragt, was getan werden soll.");

    public static final P2ShortcutKey SHORTCUT_QUIT_PROGRAM_WAIT =
            new P2ShortcutKey(ProgConfig.SHORTCUT_QUIT_PROGRAM_WAIT, ProgConfig.SHORTCUT_QUIT_PROGRAM_WAIT_INIT,
                    "Programm beenden, Downloads abwarten",
                    "Das Programm wird beendet. Wenn noch ein Download läuft, wird dieser noch abgeschlossen und " +
                            "das Programm wartet auf den Download. Der Dialog mit der Abfrage was getan werden soll, wird aber übersprungen.");

    // Tabelle Filme
    public static final P2ShortcutKey SHORTCUT_FILM_SHOWN =
            new P2ShortcutKey(ProgConfig.SHORTCUT_FILM_SHOWN, ProgConfig.SHORTCUT_FILM_SHOWN_INIT,
                    "Film als gesehen markieren",
                    "Der Film wird zur Liste der gesehenen Filme hinzugefügt.");

    public static final P2ShortcutKey SHORTCUT_FILM_NOT_SHOWN =
            new P2ShortcutKey(ProgConfig.SHORTCUT_FILM_NOT_SHOWN, ProgConfig.SHORTCUT_FILM_NOT_SHOWN_INIT,
                    "Film als ungesehen markieren",
                    "Der Film wird aus der Liste der gesehenen Filme gelöscht.");

    public static final P2ShortcutKey SHORTCUT_ADD_BLACKLIST =
            new P2ShortcutKey(ProgConfig.SHORTCUT_ADD_BLACKLIST, ProgConfig.SHORTCUT_ADD_BLACKLIST_INIT,
                    "Einen neuen Eintrag in der Blacklist erstellen",
                    "Einen neuen Eintrag in der Blacklist erstellen, der markierte Film dient dabei Vorlage.");

    public static final P2ShortcutKey SHORTCUT_ADD_BLACKLIST_THEME =
            new P2ShortcutKey(ProgConfig.SHORTCUT_ADD_BLACKLIST_THEME, ProgConfig.SHORTCUT_ADD_BLACKLIST_THEME_INIT,
                    "Thema direkt in die Blacklist einfügen",
                    "Einen neuen Eintrag mit dem exakten Thema in der Blacklist erstellen, " +
                            "der markierte Film dient dabei Vorlage.");

    public static final P2ShortcutKey SHORTCUT_SHOW_BLACKLIST =
            new P2ShortcutKey(ProgConfig.SHORTCUT_SHOW_BLACKLIST, ProgConfig.SHORTCUT_SHOW_BLACKLIST_INIT,
                    "Einstellungen zur Blacklist anzeigen",
                    "Die Programmeinstellungen zum Anpassen und Ändern der Blacklist " +
                            "werden angezeigt.");

    public static final P2ShortcutKey SHORTCUT_SHOW_FILTER =
            new P2ShortcutKey(ProgConfig.SHORTCUT_SHOW_FILTER, ProgConfig.SHORTCUT_SHOW_FILTER_INIT,
                    "Filter anzeigen",
                    "In der Tabelle \"Filme\", \"Downloads\" und \"Abos\" die Filter anzeigen.");

    public static final P2ShortcutKey SHORTCUT_SHOW_INFOS =
            new P2ShortcutKey(ProgConfig.SHORTCUT_SHOW_INFOS, ProgConfig.SHORTCUT_SHOW_INFOS_INIT,
                    "Infos anzeigen",
                    "In der Tabelle \"Filme\", \"Downloads\" und \"Abos\" die Infos anzeigen.");

    public static final P2ShortcutKey SHORTCUT_INFO_FILM =
            new P2ShortcutKey(ProgConfig.SHORTCUT_INFO_FILM, ProgConfig.SHORTCUT_INFO_FILM_INIT,
                    "Filminformation anzeigen",
                    "In der Tabelle \"Filme\" und \"Downloads\" die Infos des markierten Films anzeigen.");

    public static final P2ShortcutKey SHORTCUT_PLAY_FILM =
            new P2ShortcutKey(ProgConfig.SHORTCUT_PLAY_FILM, ProgConfig.SHORTCUT_PLAY_FILM_INIT,
                    "Film abspielen",
                    "Der markierte Film in der Tabelle \"Filme\" wird abgespielt.");

    public static final P2ShortcutKey SHORTCUT_PLAY_FILM_ALL =
            new P2ShortcutKey(ProgConfig.SHORTCUT_PLAY_FILM_ALL, ProgConfig.SHORTCUT_PLAY_FILM_ALL_INIT,
                    "Alle markierten Filme abspielen",
                    "Alle markierten Filme in der Tabelle \"Filme\" werden abgespielt.");

    public static final P2ShortcutKey SHORTCUT_SAVE_FILM =
            new P2ShortcutKey(ProgConfig.SHORTCUT_SAVE_FILM, ProgConfig.SHORTCUT_SAVE_FILM_INIT,
                    "Film speichern",
                    "Der markierte Film in der Tabelle \"Filme\" wird aufgezeichnet.");

    public static final P2ShortcutKey SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION =
            new P2ShortcutKey(ProgConfig.SHORTCUT_SEARCH_FILM_IN_MEDIA_COLLECTION, ProgConfig.SHORTCUT_SEARCH_FILM_IN_MEDIA_COLLECTION_INIT,
                    "Filmtitel in Mediensammlung suchen",
                    "Der Titel des markierten Films in der Tabelle \"Filme\" und \"Downloads\" wird in der Mediensammlung gesucht.");

    public static final P2ShortcutKey SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD =
            new P2ShortcutKey(ProgConfig.SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD, ProgConfig.SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD_INIT,
                    "Filmthema in die Zwischenablage Kopieren",
                    "Das Thema des markierten Films/Downloads in der " +
                            "Tabelle \"Filme\" und \"Downloads\" wird in die Zwischenablage kopiert.");

    public static final P2ShortcutKey SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD =
            new P2ShortcutKey(ProgConfig.SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD, ProgConfig.SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD_INIT,
                    "Filmtitel in die Zwischenablage Kopieren",
                    "Der Titel des markierten Films/Downloads in der " +
                            "Tabelle \"Filme\" und \"Downloads\" wird in die Zwischenablage kopiert.");

    // Tabelle Download
    public static final P2ShortcutKey SHORTCUT_DOWNLOAD_START =
            new P2ShortcutKey(ProgConfig.SHORTCUT_DOWNLOAD_START, ProgConfig.SHORTCUT_DOWNLOAD_START_INIT,
                    "Download starten",
                    "Der markierte Download in der Tabelle \"Downloads\" wird gestartet.");

    public static final P2ShortcutKey SHORTCUT_DOWNLOAD_STOP =
            new P2ShortcutKey(ProgConfig.SHORTCUT_DOWNLOAD_STOP, ProgConfig.SHORTCUT_DOWNLOAD_STOP_INIT,
                    "Download stoppen",
                    "Der markierte Download in der Tabelle \"Downloads\" wird gestoppt.");

    public static final P2ShortcutKey SHORTCUT_DOWNLOAD_CHANGE =
            new P2ShortcutKey(ProgConfig.SHORTCUT_DOWNLOAD_CHANGE, ProgConfig.SHORTCUT_DOWNLOAD_CHANGE_INIT,
                    "Download ändern",
                    "Der markierte Download in der Tabelle \"Downloads\" kann geändert werden.");

    public static final P2ShortcutKey SHORTCUT_UNDO_DELETE =
            new P2ShortcutKey(ProgConfig.SHORTCUT_UNDO_DELETE, ProgConfig.SHORTCUT_UNDO_DELETE_INIT,
                    "Gelöschte Downloads oder Abos wieder anlegen",
                    "Die zuletzt gelöschten Downloads in der Tabelle \"Downloads\" oder " +
                            "die zuletzt gelöschten Abos in der Tabelle \"Abos\" können wieder angelegt werden.");

    public static final P2ShortcutKey SHORTCUT_DOWNLOADS_UPDATE =
            new P2ShortcutKey(ProgConfig.SHORTCUT_DOWNLOAD_UPDATE, ProgConfig.SHORTCUT_DOWNLOADS_UPDATE_INIT,
                    "Downloads aktualisieren",
                    "Die Liste der Downloads in der Tabelle \"Downloads\" wird aktualisiert.");

    public static final P2ShortcutKey SHORTCUT_DOWNLOADS_CLEAN_UP =
            new P2ShortcutKey(ProgConfig.SHORTCUT_DOWNLOAD_CLEAN_UP, ProgConfig.SHORTCUT_DOWNLOAD_CLEAN_UP_INIT,
                    "Downloads aufräumen",
                    "Die Liste der Downloads in der Tabelle \"Downloads\" wird aufgeräumt.");

    private static final ObservableList<P2ShortcutKey> shortcutList = FXCollections.observableArrayList();

    public PShortcut() {
        shortcutList.add(SHORTCUT_CENTER_GUI);
        shortcutList.add(SHORTCUT_MINIMIZE_GUI);

        shortcutList.add(SHORTCUT_SEARCH_MEDIACOLLECTION);
        shortcutList.add(SHORTCUT_QUIT_PROGRAM);
        shortcutList.add(SHORTCUT_QUIT_PROGRAM_WAIT);

        shortcutList.add(SHORTCUT_FILM_SHOWN);
        shortcutList.add(SHORTCUT_FILM_NOT_SHOWN);
        shortcutList.add(SHORTCUT_ADD_BLACKLIST);
        shortcutList.add(SHORTCUT_ADD_BLACKLIST_THEME);
        shortcutList.add(SHORTCUT_SHOW_BLACKLIST);
        shortcutList.add(SHORTCUT_SHOW_FILTER);
        shortcutList.add(SHORTCUT_SHOW_INFOS);
        shortcutList.add(SHORTCUT_INFO_FILM);
        shortcutList.add(SHORTCUT_PLAY_FILM);
        shortcutList.add(SHORTCUT_PLAY_FILM_ALL);
        shortcutList.add(SHORTCUT_SAVE_FILM);
        shortcutList.add(SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION);
        shortcutList.add(SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD);
        shortcutList.add(SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD);

        shortcutList.add(SHORTCUT_DOWNLOAD_START);
        shortcutList.add(SHORTCUT_DOWNLOAD_STOP);
        shortcutList.add(SHORTCUT_DOWNLOAD_CHANGE);
        shortcutList.add(SHORTCUT_DOWNLOADS_UPDATE);
        shortcutList.add(SHORTCUT_DOWNLOADS_CLEAN_UP);
        shortcutList.add(SHORTCUT_UNDO_DELETE);
    }

    public static synchronized ObservableList<P2ShortcutKey> getShortcutList() {
        return shortcutList;
    }

    public static synchronized boolean checkDoubleShortcutList() {
        HashSet<String> hashSet = new HashSet<>();
        for (P2ShortcutKey ps : shortcutList) {
            if (!hashSet.add(ps.getActShortcut())) {
                hashSet.clear();
                return true;
            }
        }
        hashSet.clear();
        return false;
    }
}
