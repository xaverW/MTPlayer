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

import de.p2tools.mtplayer.MTPlayerController;
import de.p2tools.mtplayer.MTPlayerFactory;
import de.p2tools.mtplayer.ShortKeyFactory;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.config.ProgShortcut;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFactory;
import de.p2tools.mtplayer.controller.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.film.FilmSaveFactory;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilterSamples;
import de.p2tools.mtplayer.gui.dialog.propose.ProposeDialogController;
import de.p2tools.p2lib.tools.shortcut.PShortcutWorker;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;


public class FilmMenu {
    final private VBox vBox;
    final private ProgData progData;
    private FilmFilter storedActFilterSettings = null;
    private static final String FILM_FILTER_BOOKMARK_TEXT = "Alle angelegte Bookmarks anzeigen\n" +
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
                "Abspielen", "Markierten Film abspielen", ProgIcons.ICON_TOOLBAR_FILM_START.getImageView());
        final ToolBarButton btPlayAll = new ToolBarButton(vBox,
                "Alle Abspielen", "Alle Markierten Filme abspielen", ProgIcons.ICON_TOOLBAR_FILM_ALL_START.getImageView());
        final ToolBarButton btSave = new ToolBarButton(vBox,
                "Speichern", "Markierte Filme speichern", ProgIcons.ICON_TOOLBAR_FILM_REC.getImageView());

        btPlay.setOnAction(a -> FilmPlayFactory.playFilm());
        btPlayAll.setOnAction(a -> FilmPlayFactory.playFilmList());
        btSave.setOnAction(a -> FilmSaveFactory.saveFilmList());

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btBookmark = new ToolBarButton(vBox,
                "Bookmarks anlegen", "Bookmarks für die markierten Filme anlegen", ProgIcons.ICON_TOOLBAR_FILM_BOOKMARK.getImageView());
        final ToolBarButton btDelBookmark = new ToolBarButton(vBox,
                "Bookmarks löschen", "Bookmarks für die markierten Filme löschen", ProgIcons.ICON_TOOLBAR_FILM_DEL_BOOKMARK.getImageView());
        final ToolBarButton btDelAllBookmark = new ToolBarButton(vBox,
                "Alle Bookmarks löschen", "Alle angelegten Bookmarks löschen", ProgIcons.ICON_TOOLBAR_FILM_DEL_ALL_BOOKMARK.getImageView());
        final ToolBarButton btFilterBookmark = new ToolBarButton(vBox,
                "Bookmarks anzeigen", FILM_FILTER_BOOKMARK_TEXT, ProgIcons.ICON_TOOLBAR_FILM_BOOKMARK_FILTER.getImageView());

        btBookmark.setOnAction(a -> progData.filmGuiController.bookmarkFilm(true));
        btDelBookmark.setOnAction(a -> progData.filmGuiController.bookmarkFilm(false));
        btDelAllBookmark.setOnAction(a -> progData.historyListBookmarks.clearAll(progData.primaryStage));

        btFilterBookmark.setOnAction(a -> {
            FilmFilter sf = progData.filmFilterWorker.getActFilterSettings();
            FilmFilter black = FilmFilterSamples.getBookmarkFilter();

            if (sf.isSame(black, false)) {
                // dann ist der BlackFilter aktiv, dann zurückschalten
                if (storedActFilterSettings != null) {
                    // dann haben wir einen gespeicherten Filter
                    progData.filmFilterWorker.setActFilterSettings(storedActFilterSettings);
                    storedActFilterSettings = null;
                } else {
                    // dann gibts keinen gespeicherten, dann einfach löschen
                    progData.filmFilterWorker.getActFilterSettings().clearFilter();
                }
            } else {
                // dann ist es ein anderer Filter, Black einschalten und ActFilter merken
                storedActFilterSettings = progData.filmFilterWorker.getActFilterSettings().getCopy();
                progData.filmFilterWorker.setActFilterSettings(black);
            }
        });

        if (ProgData.debug) {
            vBoxSpace = new VBox();
            vBoxSpace.setMaxHeight(10);
            vBoxSpace.setMinHeight(10);
            vBox.getChildren().add(vBoxSpace);

            final ToolBarButton btPropose = new ToolBarButton(vBox,
                    "Filme vorschlagen", "Filme suchen, die zu den bisherigen gesehenen Filmen passen", ProgIcons.ICON_TOOLBAR_PROPOSE.getImageView());
            btPropose.setOnAction(a -> new ProposeDialogController(progData, ProgConfig.PROPOSE_DIALOG_CONTROLLER_SIZE));
        }
    }

    private void initFilmMenu() {
        final MenuButton mb = new MenuButton("");
        mb.setTooltip(new Tooltip("Filmmenü anzeigen"));
        mb.setGraphic(ProgIcons.ICON_TOOLBAR_MENU.getImageView());
        mb.getStyleClass().addAll("btnFunction", "btnFunc-2");

        final MenuItem mbPlay = new MenuItem("Film abspielen");
        mbPlay.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            FilmPlayFactory.playFilm();
        });
        PShortcutWorker.addShortCut(mbPlay, ProgShortcut.SHORTCUT_PLAY_FILM);

        final MenuItem mbPlayAll = new MenuItem("Alle markierten Film abspielen");
        mbPlayAll.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            FilmPlayFactory.playFilmList();
        });
        PShortcutWorker.addShortCut(mbPlayAll, ProgShortcut.SHORTCUT_PLAY_FILM_ALL);

        final MenuItem mbSave = new MenuItem("Film speichern");
        mbSave.setOnAction(e -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            FilmSaveFactory.saveFilmList();
        });
        PShortcutWorker.addShortCut(mbSave, ProgShortcut.SHORTCUT_SAVE_FILM);

        mb.getItems().addAll(mbPlay, mbPlayAll, mbSave);

        final MenuItem miFilmShown = new MenuItem("Filme als gesehen markieren");
        miFilmShown.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            progData.filmGuiController.setFilmShown(true);
        });
        PShortcutWorker.addShortCut(miFilmShown, ProgShortcut.SHORTCUT_FILM_SHOWN);

        final MenuItem miFilmNotShown = new MenuItem("Filme als ungesehen markieren");
        miFilmNotShown.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            progData.filmGuiController.setFilmShown(false);
        });
        PShortcutWorker.addShortCut(miFilmNotShown, ProgShortcut.SHORTCUT_FILM_NOT_SHOWN);

        final MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen" +
                ShortKeyFactory.SHORT_CUT_LEER + ProgShortcut.SHORTCUT_INFO_FILM.getActShortcut());
        miFilmInfo.setOnAction(a -> {
            progData.filmGuiController.showFilmInfo();
        });

        final MenuItem miFilmMediaCollection = new MenuItem("Film in der Mediensammlung suchen" +
                ShortKeyFactory.SHORT_CUT_LEER + ProgShortcut.SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION.getActShortcut());
        miFilmMediaCollection.setOnAction(a -> {
            progData.filmGuiController.searchFilmInMediaCollection();
        });

        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren" +
                ShortKeyFactory.SHORT_CUT_LEER + ProgShortcut.SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD.getActShortcut());
        miCopyTheme.setOnAction(a -> progData.filmGuiController.copyFilmThemeTitle(true));

        final MenuItem miCopyTitle = new MenuItem("Titel in die Zwischenablage kopieren" +
                ShortKeyFactory.SHORT_CUT_LEER + ProgShortcut.SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD.getActShortcut());
        miCopyTitle.setOnAction(a -> progData.filmGuiController.copyFilmThemeTitle(false));

        //Blacklist
        Menu submenuBlacklist = new Menu("Blacklist");
        final MenuItem miBlack = new MenuItem("Blacklist-Eintrag für den Film erstellen" +
                ShortKeyFactory.SHORT_CUT_LEER + ProgShortcut.SHORTCUT_ADD_BLACKLIST.getActShortcut());
        miBlack.setOnAction(event -> BlacklistFactory.addBlackFilm(true));

        final MenuItem miBlackTheme = new MenuItem("Thema direkt in die Blacklist einfügen" +
                ShortKeyFactory.SHORT_CUT_LEER + ProgShortcut.SHORTCUT_ADD_BLACKLIST_THEME.getActShortcut());
        miBlackTheme.setOnAction(event -> {
            BlacklistFactory.addBlackThemeFilm();
        });
        submenuBlacklist.getItems().addAll(miBlack, miBlackTheme);

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miFilmShown, miFilmNotShown, miFilmInfo,
                miFilmMediaCollection, miCopyTheme, miCopyTitle, submenuBlacklist);

        // Bookmarks
        Menu submenuBookmark = new Menu("Bookmarks");
        final MenuItem miBookmarkAdd = new MenuItem("Neue Bookmarks anlegen");
        miBookmarkAdd.setOnAction(a -> progData.filmGuiController.bookmarkFilm(true));
        final MenuItem miBookmarkDel = new MenuItem("Bookmarks löschen");
        miBookmarkDel.setOnAction(a -> progData.filmGuiController.bookmarkFilm(false));
        final MenuItem miBookmarkDelAll = new MenuItem("Alle angelegten Bookmarks löschen");
        miBookmarkDelAll.setOnAction(a -> progData.historyListBookmarks.clearAll(progData.primaryStage));

        submenuBookmark.getItems().addAll(miBookmarkAdd, miBookmarkDel, miBookmarkDelAll);
        mb.getItems().add(submenuBookmark);

        final MenuItem miShowFilter = new MenuItem("Filter ein-/ausblenden" +
                ShortKeyFactory.SHORT_CUT_LEER + ProgShortcut.SHORTCUT_SHOW_FILTER.getActShortcut());
        miShowFilter.setOnAction(a -> MTPlayerFactory.setFilter());

        final MenuItem miShowInfo = new MenuItem("Infos ein-/ausblenden" +
                ShortKeyFactory.SHORT_CUT_LEER + ProgShortcut.SHORTCUT_SHOW_INFOS.getActShortcut());
        miShowInfo.setOnAction(a -> MTPlayerFactory.setInfos());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);
        vBox.getChildren().add(mb);
    }
}
