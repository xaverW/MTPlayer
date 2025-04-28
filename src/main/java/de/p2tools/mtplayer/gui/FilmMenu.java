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
import de.p2tools.mtplayer.controller.config.*;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFactory;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkFactory;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.film.FilmSaveFactory;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.controller.filmfilter.FilterSamples;
import de.p2tools.mtplayer.gui.dialog.BookmarkDelDialog;
import de.p2tools.mtplayer.gui.dialog.BookmarkDialogController;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.tools.shortcut.P2ShortcutWorker;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;


public class FilmMenu {
    final private VBox vBox;
    final private ProgData progData;
    private FilmFilter storedActFilterSettings = null;
    private static final String FILM_FILTER_BOOKMARK_TEXT = "Alle angelegte Bookmarks anzeigen\n" +
            "der zweite Klick stellt den\n" +
            "eingestellten Filter wieder her";
    private static final String FILM_SHOW_BOOKMARK_TEXT = "Alle Bookmarks in einem Dialog anzeigen";

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
                "Abspielen", "Markierten Film abspielen", ProgIcons.ICON_TOOLBAR_START.getImageView());
        final ToolBarButton btPlayAll = new ToolBarButton(vBox,
                "Alle Abspielen", "Alle Markierten Filme abspielen", ProgIcons.ICON_TOOLBAR_START_ALL.getImageView());
        final ToolBarButton btSave = new ToolBarButton(vBox,
                "Speichern", "Markierte Filme speichern", ProgIcons.ICON_TOOLBAR_REC.getImageView());

        btPlay.setOnAction(a -> {
            final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().filmGuiController.getSel(true, true);
            if (filmSelection.isPresent()) {
                FilmPlayFactory.playFilm(filmSelection.get());
                progData.filmGuiController.tableView.refresh();
                progData.filmGuiController.tableView.requestFocus();
            }
        });
        btPlayAll.setOnAction(a -> {
            FilmPlayFactory.playFilmList(ProgData.getInstance().filmGuiController.getSelList(true));
            progData.filmGuiController.tableView.refresh();
            progData.filmGuiController.tableView.requestFocus();
        });
        btSave.setOnAction(a -> {
            FilmSaveFactory.saveFilmList();
            progData.filmGuiController.tableView.refresh();
            progData.filmGuiController.tableView.requestFocus();
        });

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btBookmark = new ToolBarButton(vBox,
                "Bookmarks anlegen", "Bookmarks für die markierten Filme anlegen", ProgIcons.ICON_TOOLBAR_BOOKMARK.getImageView());
        final ToolBarButton btDelBookmark = new ToolBarButton(vBox,
                "Bookmarks löschen", "Bookmarks für die markierten Filme löschen", ProgIcons.ICON_TOOLBAR_DEL_BOOKMARK.getImageView());
        final ToolBarButton btDelAllBookmark = new ToolBarButton(vBox,
                "Alle Bookmarks löschen", "Alle angelegten Bookmarks löschen", ProgIcons.ICON_TOOLBAR_DEL_ALL_BOOKMARK.getImageView());
        final ToolBarButton btFilterBookmark = new ToolBarButton(vBox,
                "Bookmarks anzeigen", FILM_FILTER_BOOKMARK_TEXT, ProgIcons.ICON_TOOLBAR_BOOKMARK_FILTER.getImageView());
        final ToolBarButton btShowBookmark = new ToolBarButton(vBox,
                "Alle Bookmarks anzeigen", FILM_SHOW_BOOKMARK_TEXT, ProgIcons.ICON_TOOLBAR_BOOKMARK_DIALOG.getImageView());

        btBookmark.setOnAction(a -> {
            BookmarkFactory.addBookmarkList(progData.filmGuiController.getSelList(true));
            progData.filmGuiController.tableView.refresh();
            progData.filmGuiController.tableView.requestFocus();
            ;
        });
        btDelBookmark.setOnAction(a -> {
            BookmarkFactory.removeBookmarkList(progData.filmGuiController.getSelList(true));
            progData.filmGuiController.tableView.refresh();
            progData.filmGuiController.tableView.requestFocus();
        });
        btDelAllBookmark.setOnAction(a -> {
            BookmarkDelDialog b = new BookmarkDelDialog(progData, progData.primaryStage);
            if (b.isOk()) {
                // dann löschen
                BookmarkFactory.del(progData.primaryStage);
            }
            progData.filmGuiController.tableView.refresh();
            progData.filmGuiController.tableView.requestFocus();
        });

        btFilterBookmark.setOnAction(a -> {
            FilmFilter sf = progData.filterWorker.getActFilterSettings();
            FilmFilter bookmarkFilter = FilterSamples.getBookmarkFilter();

            if (sf.isSame(bookmarkFilter)) {
                // dann ist der BlackFilter aktiv, dann zurückschalten
                if (storedActFilterSettings != null) {
                    // dann haben wir einen gespeicherten Filter
                    storedActFilterSettings.setOnlyBookmark(false); // falls der eingeschaltet war
                    progData.filterWorker.setActFilterSettings(storedActFilterSettings);
                    storedActFilterSettings = null;

                } else {
                    // dann gibts keinen gespeicherten, dann einfach löschen
                    progData.filterWorker.getActFilterSettings().clearFilter();
                }

            } else {
                // dann ist es ein anderer Filter, Black einschalten und ActFilter merken
                storedActFilterSettings = progData.filterWorker.getActFilterSettings().getCopy();
                progData.filterWorker.setActFilterSettings(bookmarkFilter);
            }

            progData.filmGuiController.tableView.refresh();
            progData.filmGuiController.tableView.requestFocus();
        });
        btShowBookmark.setOnAction(a -> {
            if (!BookmarkDialogController.isRunning) {
                new BookmarkDialogController(progData);
            }
        });

        vBox.getChildren().add(P2GuiTools.getVBoxGrower());
        final ToolBarButton btLiveFilm = new ToolBarButton(vBox,
                "Live-Suche", "Live-Suche in der ARD/ZDF-Mediathek", ProgIcons.ICON_TOOLBAR_LIVE.getImageView());
        btLiveFilm.setOnAction(a -> ProgConfig.LIVE_FILM_IS_VISIBLE.set(!ProgConfig.LIVE_FILM_IS_VISIBLE.get()));
        vBox.getChildren().add(P2GuiTools.getVDistance(10));
    }

    private void initFilmMenu() {
        final MenuButton mb = new MenuButton("");
        mb.setTooltip(new Tooltip("Filmmenü anzeigen"));
        mb.setGraphic(ProgIcons.ICON_TOOLBAR_MENU.getImageView());
        mb.getStyleClass().addAll("btnFunction", "btnFunc-0");

        final MenuItem mbPlay = new MenuItem("Film abspielen");
        mbPlay.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().filmGuiController.getSel(true, true);
            filmSelection.ifPresent(FilmPlayFactory::playFilm);
        });
        P2ShortcutWorker.addShortCut(mbPlay, PShortcut.SHORTCUT_PLAY_FILM);

        final MenuItem mbPlayAll = new MenuItem("Alle markierten Film abspielen");
        mbPlayAll.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            FilmPlayFactory.playFilmList(ProgData.getInstance().filmGuiController.getSelList(true));
        });
        P2ShortcutWorker.addShortCut(mbPlayAll, PShortcut.SHORTCUT_PLAY_FILM_ALL);

        final MenuItem mbSave = new MenuItem("Film speichern");
        mbSave.setOnAction(e -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            FilmSaveFactory.saveFilmList();
        });
        P2ShortcutWorker.addShortCut(mbSave, PShortcut.SHORTCUT_SAVE_FILM);

        mb.getItems().addAll(mbPlay, mbPlayAll, mbSave);

        final MenuItem miFilmShown = new MenuItem("Filme als gesehen markieren");
        miFilmShown.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            progData.filmGuiController.setFilmShown(true);
        });
        P2ShortcutWorker.addShortCut(miFilmShown, PShortcut.SHORTCUT_FILM_SHOWN);

        final MenuItem miFilmNotShown = new MenuItem("Filme als ungesehen markieren");
        miFilmNotShown.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            progData.filmGuiController.setFilmShown(false);
        });
        P2ShortcutWorker.addShortCut(miFilmNotShown, PShortcut.SHORTCUT_FILM_NOT_SHOWN);

        final MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_INFO_FILM.getActShortcut());
        miFilmInfo.setOnAction(a -> {
            progData.filmGuiController.showFilmInfo();
        });

        final MenuItem miFilmMediaCollection = new MenuItem("Film in der Mediensammlung suchen" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION.getActShortcut());
        miFilmMediaCollection.setOnAction(a -> {
            progData.filmGuiController.searchFilmInMediaCollection();
        });

        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD.getActShortcut());
        miCopyTheme.setOnAction(a -> progData.filmGuiController.copyFilmThemeTitle(true));

        final MenuItem miCopyTitle = new MenuItem("Titel in die Zwischenablage kopieren" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD.getActShortcut());
        miCopyTitle.setOnAction(a -> progData.filmGuiController.copyFilmThemeTitle(false));

        //Blacklist
        Menu submenuBlacklist = new Menu("Blacklist");
        final MenuItem miBlack = new MenuItem("Blacklist-Eintrag für den Film erstellen" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_ADD_BLACKLIST.getActShortcut());
        miBlack.setOnAction(event -> BlacklistFactory.addBlackFilm(true));

        final MenuItem miBlackTheme = new MenuItem("Thema direkt in die Blacklist einfügen" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_ADD_BLACKLIST_THEME.getActShortcut());
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
        miBookmarkAdd.setOnAction(a -> BookmarkFactory.addBookmarkList(progData.filmGuiController.getSelList(true)));

        final MenuItem miBookmarkDel = new MenuItem("Bookmarks löschen");
        miBookmarkDel.setOnAction(a -> BookmarkFactory.removeBookmarkList(progData.filmGuiController.getSelList(true)));

        final MenuItem miBookmarkDelAll = new MenuItem("Alle angelegten Bookmarks löschen");
        miBookmarkDelAll.setOnAction(a -> {
            BookmarkDelDialog b = new BookmarkDelDialog(progData, progData.primaryStage);
            if (b.isOk()) {
                // dann löschen
                BookmarkFactory.del(progData.primaryStage);
            }
        });

        submenuBookmark.getItems().addAll(miBookmarkAdd, miBookmarkDel, miBookmarkDelAll);
        mb.getItems().add(submenuBookmark);

        final MenuItem miShowFilter = new MenuItem("Filter ein-/ausblenden" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SHOW_FILTER.getActShortcut());
        miShowFilter.disableProperty().bind(ProgConfig.FILM__FILTER_IS_RIP);
        miShowFilter.setOnAction(a -> MTPlayerFactory.setFilter());

        final MenuItem miShowInfo = new MenuItem("Infos ein-/ausblenden" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SHOW_INFOS.getActShortcut());

        miShowInfo.disableProperty().bind(ProgConfig.FILM__INFO_PANE_IS_RIP
                .and(ProgConfig.FILM__BUTTON_PANE_IS_RIP)
                .and(ProgConfig.FILM__MEDIA_PANE_IS_RIP));
        miShowInfo.setOnAction(a -> MTPlayerFactory.setInfos());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);
        vBox.getChildren().add(mb);
    }
}
