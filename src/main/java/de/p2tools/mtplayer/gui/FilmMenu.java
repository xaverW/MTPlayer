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

package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.config.ProgShortcut;
import de.p2tools.mtplayer.controller.filmfilter.BlacklistFactory;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilterFactory;
import de.p2tools.p2lib.tools.shortcut.PShortcutWorker;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;


public class FilmMenu {
    final private VBox vBox;
    final private ProgData progData;
    private FilmFilter storedActFilterSettings = null;
    private FilmFilter storedBookmarkFilter = null;
    private static final String FILM_FILTER_BOOKMARK_TEXT = "Alle angelegte Bookmarks anzeigen\n\n" +
            "der zweite Klick stellt den\n" +
            "eingestellten Filter wieder her";

    public FilmMenu(VBox vBox) {
        this.vBox = vBox;
        progData = ProgData.getInstance();
    }

    public void init() {
        vBox.getChildren().clear();

        initFilmMenu();
        initButton();
    }

    private void initButton() {
        // Button
        VBox vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(0);
        vBoxSpace.setMinHeight(0);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btPlay = new ToolBarButton(vBox,
                "Abspielen", "Markierten Film abspielen", ProgIcons.Icons.ICON_TOOLBAR_FILM_START.getImageView());
        final ToolBarButton btSave = new ToolBarButton(vBox,
                "Speichern", "Markierte Filme speichern", ProgIcons.Icons.ICON_TOOLBAR_FILM_REC.getImageView());

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btBookmark = new ToolBarButton(vBox,
                "Bookmarks anlegen", "Bookmarks für die markierten Filme anlegen", ProgIcons.Icons.ICON_TOOLBAR_FILM_BOOKMARK.getImageView());
        final ToolBarButton btDelBookmark = new ToolBarButton(vBox,
                "Bookmarks löschen", "Bookmarks für die markierten Filme löschen", ProgIcons.Icons.ICON_TOOLBAR_FILM_DEL_BOOKMARK.getImageView());
        final ToolBarButton btDelAllBookmark = new ToolBarButton(vBox,
                "Alle Bookmarks löschen", "Alle angelegten Bookmarks löschen", ProgIcons.Icons.ICON_TOOLBAR_FILM_DEL_ALL_BOOKMARK.getImageView());
        final ToolBarButton btFilterBookmakr = new ToolBarButton(vBox,
                "Bookmarks anzeigen", FILM_FILTER_BOOKMARK_TEXT, ProgIcons.Icons.ICON_TOOLBAR_FILM_BOOKMARK_FILTER.getImageView());

        btPlay.setOnAction(a -> progData.filmGuiController.playFilmUrl());
        btSave.setOnAction(a -> progData.filmGuiController.saveTheFilm());

        btBookmark.setOnAction(a -> progData.filmGuiController.bookmarkFilm(true));
        btDelBookmark.setOnAction(a -> progData.filmGuiController.bookmarkFilm(false));
        btDelAllBookmark.setOnAction(a -> progData.bookmarks.clearAll(progData.primaryStage));

        btFilterBookmakr.setOnAction(a -> {
            if (storedActFilterSettings != null && storedBookmarkFilter != null) {
                // prüfen, ob sich der Filter geändert hat, wenn ja, dann auf Anfang
                FilmFilter sf = progData.actFilmFilterWorker.getActFilterSettings();
                if (storedBookmarkFilter.isSame(sf, false)) {
                    // dann hat sich der Filter geändert
                    storedActFilterSettings = null;
                }
            }

            if (storedActFilterSettings == null) {
                // dann wurde es noch nicht aufgerufen
                if (progData.actFilmFilterWorker.getActFilterSettings().isOnlyVis() &&
                        progData.actFilmFilterWorker.getActFilterSettings().isOnlyBookmark()) {
                    // dann ist Bookmark schon gesetzt, dann erst mal ausschalten
                    storedActFilterSettings = progData.actFilmFilterWorker.getActFilterSettings().getCopy();
                    progData.actFilmFilterWorker.getActFilterSettings().clearFilter();

                } else {
                    // dann setzen des Bookmarkfilters
                    storedActFilterSettings = progData.actFilmFilterWorker.getActFilterSettings().getCopy();
                    storedBookmarkFilter = FilmFilterFactory.getBookmarkFilter(storedActFilterSettings);
                    progData.actFilmFilterWorker.setActFilterSettings(storedBookmarkFilter);
                }

            } else {
                // dann den gemerkten Filter wieder setzen, aber ohne Bookmark
                storedActFilterSettings.setOnlyBookmark(false);
                progData.actFilmFilterWorker.setActFilterSettings(storedActFilterSettings);
                storedActFilterSettings = null;
            }
        });
    }

    private void initFilmMenu() {
        final MenuButton mb = new MenuButton("");
        mb.setTooltip(new Tooltip("Filmmenü anzeigen"));
        mb.setGraphic(ProgIcons.Icons.ICON_TOOLBAR_MENU.getImageView());
        mb.getStyleClass().addAll("btnFunction", "btnFunc-1");

        final MenuItem mbPlay = new MenuItem("Film abspielen");
        mbPlay.setOnAction(a -> progData.filmGuiController.playFilmUrl());
        PShortcutWorker.addShortCut(mbPlay, ProgShortcut.SHORTCUT_PLAY_FILM);

        final MenuItem mbSave = new MenuItem("Film speichern");
        mbSave.setOnAction(e -> progData.filmGuiController.saveTheFilm());
        PShortcutWorker.addShortCut(mbSave, ProgShortcut.SHORTCUT_SAVE_FILM);

        mb.getItems().addAll(mbPlay, mbSave);

        final MenuItem miFilmShown = new MenuItem("Filme als gesehen markieren");
        miFilmShown.setOnAction(a -> progData.filmGuiController.setFilmShown());
        PShortcutWorker.addShortCut(miFilmShown, ProgShortcut.SHORTCUT_FILM_SHOWN);

        final MenuItem miFilmNotShown = new MenuItem("Filme als ungesehen markieren");
        miFilmNotShown.setOnAction(a -> progData.filmGuiController.setFilmNotShown());
        PShortcutWorker.addShortCut(miFilmNotShown, ProgShortcut.SHORTCUT_FILM_NOT_SHOWN);

        final MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> progData.filmGuiController.showFilmInfo());
        PShortcutWorker.addShortCut(miFilmInfo, ProgShortcut.SHORTCUT_INFO_FILM);

        final MenuItem miFilmMediaCollection = new MenuItem("Titel in der Mediensammlung suchen");
        miFilmMediaCollection.setOnAction(a -> progData.filmGuiController.guiFilmMediaCollection());
        PShortcutWorker.addShortCut(miFilmMediaCollection, ProgShortcut.SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION);

        final MenuItem miCopyName = new MenuItem("Titel in die Zwischenablage kopieren");
        miCopyName.setOnAction(a -> progData.filmGuiController.copyFilmThemeTitle(false));
        PShortcutWorker.addShortCut(miCopyName, ProgShortcut.SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD);
        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren");
        miCopyTheme.setOnAction(a -> progData.filmGuiController.copyFilmThemeTitle(true));
        PShortcutWorker.addShortCut(miCopyTheme, ProgShortcut.SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD);

        //Blacklist
        Menu submenuBlacklist = new Menu("Blacklist");
        final MenuItem miBlack = new MenuItem("Blacklist-Eintrag für den Film erstellen");
        miBlack.setOnAction(event -> BlacklistFactory.addBlack());
        PShortcutWorker.addShortCut(miBlack, ProgShortcut.SHORTCUT_ADD_BLACKLIST);

        final MenuItem miBlackTheme = new MenuItem("Thema direkt in die Blacklist einfügen");
        miBlackTheme.setOnAction(event -> BlacklistFactory.addBlackTheme());
        PShortcutWorker.addShortCut(miBlackTheme, ProgShortcut.SHORTCUT_ADD_BLACKLIST_THEME);
        submenuBlacklist.getItems().addAll(miBlack, miBlackTheme);

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miFilmShown, miFilmNotShown, miFilmInfo,
                miFilmMediaCollection, miCopyTheme, miCopyName, submenuBlacklist);

        // Bookmarks
        Menu submenuBookmark = new Menu("Bookmarks");
        final MenuItem miBookmarkAdd = new MenuItem("Neue Bookmarks anlegen");
        miBookmarkAdd.setOnAction(a -> progData.filmGuiController.bookmarkFilm(true));
        final MenuItem miBookmarkDel = new MenuItem("Bookmarks löschen");
        miBookmarkDel.setOnAction(a -> progData.filmGuiController.bookmarkFilm(false));
        final MenuItem miBookmarkDelAll = new MenuItem("Alle angelegten Bookmarks löschen");
        miBookmarkDelAll.setOnAction(a -> progData.bookmarks.clearAll(progData.primaryStage));

        submenuBookmark.getItems().addAll(miBookmarkAdd, miBookmarkDel, miBookmarkDelAll);
        mb.getItems().add(submenuBookmark);

        final MenuItem miShowFilter = new MenuItem("Filter ein-/ausblenden");
        miShowFilter.setOnAction(a -> progData.mtPlayerController.setFilter());
        PShortcutWorker.addShortCut(miShowFilter, ProgShortcut.SHORTCUT_SHOW_FILTER);

        final MenuItem miShowInfo = new MenuItem("Infos ein-/ausblenden");
        miShowInfo.setOnAction(a -> progData.mtPlayerController.setInfos());
        PShortcutWorker.addShortCut(miShowInfo, ProgShortcut.SHORTCUT_SHOW_INFOS);

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);
        vBox.getChildren().add(mb);
    }
}
