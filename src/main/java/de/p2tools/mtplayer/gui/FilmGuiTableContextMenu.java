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
import de.p2tools.mtplayer.controller.data.BlackData;
import de.p2tools.mtplayer.controller.data.SetDataList;
import de.p2tools.mtplayer.controller.data.film.FilmData;
import de.p2tools.mtplayer.controller.data.film.FilmTools;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.tools.filmFilter.FilmFilter;
import de.p2tools.p2Lib.tools.PSystemUtils;
import javafx.scene.control.*;

public class FilmGuiTableContextMenu {

    private final ProgData progData;
    private final FilmGuiController filmGuiController;
    private final TableView tableView;

    public FilmGuiTableContextMenu(ProgData progData, FilmGuiController filmGuiController, TableView tableView) {
        this.progData = progData;
        this.filmGuiController = filmGuiController;
        this.tableView = tableView;
    }

    public ContextMenu getContextMenu(FilmData film) {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, film);
        return contextMenu;
    }

    private void getMenu(ContextMenu contextMenu, FilmData film) {
        // Start/Save
        MenuItem miStart = new MenuItem("Film abspielen");
        miStart.setOnAction(a -> filmGuiController.playFilmUrl());
        MenuItem miSave = new MenuItem("Film speichern");
        miSave.setOnAction(a -> filmGuiController.saveTheFilm());
        contextMenu.getItems().addAll(miStart, miSave);

        miStart.setDisable(film == null);
        miSave.setDisable(film == null);

        Menu mFilter = addFilter(film);// Filter
        Menu mAddAbo = addAbo(film);// Abo
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(mFilter, mAddAbo);

        Menu mStartFilm = startFilmWithSet(film); // Film mit Set starten
        if (mStartFilm != null) {
            contextMenu.getItems().add(mStartFilm);
        }

        Menu mBlacklist = addBlacklist(film);// Blacklist
        Menu mBookmark = addBookmark(film);// Bookmark
        Menu mCopyUrl = copyUrl(film);// URL kopieren
        contextMenu.getItems().addAll(mBlacklist, mBookmark, mCopyUrl);

        final MenuItem miFilmsSetShown;
        if (film != null && film.isShown()) {
            miFilmsSetShown = new MenuItem("Filme als ungesehen markieren");
            miFilmsSetShown.setOnAction(a -> filmGuiController.setFilmNotShown());
        } else {
            miFilmsSetShown = new MenuItem("Filme als gesehen markieren");
            miFilmsSetShown.setOnAction(a -> filmGuiController.setFilmShown());
        }
        miFilmsSetShown.setDisable(film == null);

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> filmGuiController.showFilmInfo());
        miFilmInfo.setDisable(film == null);

        MenuItem miMediaDb = new MenuItem("Titel in der Mediensammlung suchen");
        miMediaDb.setOnAction(a -> filmGuiController.guiFilmMediaCollection());
        miMediaDb.setDisable(film == null);

        final MenuItem miCopyName = new MenuItem("Titel in die Zwischenablage kopieren");
        miCopyName.setOnAction(a -> {
            PSystemUtils.copyToClipboard(film.getTitle());
        });
        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren");
        miCopyTheme.setOnAction(a -> {
            PSystemUtils.copyToClipboard(film.getTheme());
        });

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miFilmsSetShown, miFilmInfo, miMediaDb, miCopyName, miCopyTheme);


        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> new Table().resetTable(tableView, Table.TABLE.FILM));
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(resetTable);
    }

    private Menu addFilter(FilmData film) {
        Menu submenuFilter = new Menu("Filter");
        if (film == null) {
            submenuFilter.setDisable(true);
            return submenuFilter;
        }

        final MenuItem miFilterChannel = new MenuItem("nach Sender filtern");
        miFilterChannel.setOnAction(event -> progData.actFilmFilterWorker.getActFilterSettings().setChannelAndVis(film.getChannel()));
        final MenuItem miFilterTheme = new MenuItem("nach Thema filtern");
        miFilterTheme.setOnAction(event -> progData.actFilmFilterWorker.getActFilterSettings().setThemeAndVis(film.getTheme()));
        final MenuItem miFilterChannelTheme = new MenuItem("nach Sender und Thema filtern");
        miFilterChannelTheme.setOnAction(event -> {
            progData.actFilmFilterWorker.getActFilterSettings().setChannelAndVis(film.getChannel());
            progData.actFilmFilterWorker.getActFilterSettings().setThemeAndVis(film.getTheme());
        });
        final MenuItem miFilterChannelThemeTitle = new MenuItem("nach Sender, Thema und Titel filtern");
        miFilterChannelThemeTitle.setOnAction(event -> {
            progData.actFilmFilterWorker.getActFilterSettings().setChannelAndVis(film.getChannel());
            progData.actFilmFilterWorker.getActFilterSettings().setThemeAndVis(film.getTheme());
            progData.actFilmFilterWorker.getActFilterSettings().setTitleAndVis(film.getTitle());
        });

        submenuFilter.getItems().addAll(miFilterChannel, miFilterTheme, miFilterChannelTheme, miFilterChannelThemeTitle);
        return submenuFilter;
    }

    private Menu addAbo(FilmData film) {
        Menu submenuAbo = new Menu("Abo");
        final MenuItem miAboDel = new MenuItem("Abo löschen");
        final MenuItem miAboAddFilter = new MenuItem("aus dem Filter ein Abo erstellen");
        final MenuItem miAboAddChannelTheme = new MenuItem("Abo mit Sender und Thema anlegen");
        final MenuItem miAboAddChannelThemeTitle = new MenuItem("Abo mit Sender und Thema und Titel anlegen");
        final MenuItem miAboChange = new MenuItem("Abo ändern");

        miAboDel.setDisable(film == null);
        miAboAddChannelTheme.setDisable(film == null);
        miAboAddChannelThemeTitle.setDisable(film == null);
        miAboChange.setDisable(film == null);

        // neues Abo aus Filter anlegen
        miAboAddFilter.setOnAction(a -> {
            FilmFilter filmFilter = progData.actFilmFilterWorker.getActFilterSettings();
            progData.aboList.addNewAboFromFilter(filmFilter);
        });

        if (film != null && film.getAbo() == null) {
            // neues Abo anlegen
            miAboAddChannelTheme.setOnAction(a ->
                    progData.aboList.addNewAbo(film.getTheme(), film.getChannel(), film.getTheme(), ""));
            miAboAddChannelThemeTitle.setOnAction(a ->
                    progData.aboList.addNewAbo(film.getTheme(), film.getChannel(), film.getTheme(), film.getTitle()));
            // Abo löschen/ändern
            miAboChange.setDisable(true);
            miAboDel.setDisable(true);
        } else {
            // Abo gibts schon
            miAboAddChannelTheme.setDisable(true);
            miAboAddChannelThemeTitle.setDisable(true);
            // Abo löschen/ändern
            miAboChange.setOnAction(event ->
                    progData.aboList.changeAbo(film.getAbo()));
            miAboDel.setOnAction(event ->
                    progData.aboList.deleteAbo(film.getAbo()));
        }

        submenuAbo.getItems().addAll(miAboAddFilter, miAboAddChannelTheme, miAboAddChannelThemeTitle, miAboChange, miAboDel);
        return submenuAbo;
    }

    private Menu startFilmWithSet(FilmData film) {
        final SetDataList list = progData.setDataList.getSetDataListButton();
        if (!list.isEmpty()) {
            Menu submenuSet = new Menu("Film mit Set starten");

            if (film == null) {
                submenuSet.setDisable(true);
                return submenuSet;
            }

            list.stream().forEach(setData -> {
                final MenuItem item = new MenuItem(setData.getVisibleName());
                item.setOnAction(event -> filmGuiController.playFilmUrlWithSet(setData));
                submenuSet.getItems().add(item);
            });

            return submenuSet;
        }

        return null;
    }

    private Menu addBlacklist(FilmData filmData) {
        Menu submenuBlacklist = new Menu("Blacklist");
        if (filmData == null) {
            submenuBlacklist.setDisable(true);
            return submenuBlacklist;
        }

        final MenuItem miBlack = new MenuItem("Blacklist-Eintrag für den Film erstellen");
        miBlack.setOnAction(event -> progData.filmGuiController.addBlack(filmData.getChannel(), filmData.getTheme(), filmData.getTitle()));
//        PShortcutWorker.addShortCut(miBlack, MTShortcut.SHORTCUT_ADD_BLACKLIST);

        final MenuItem miBlackTheme = new MenuItem("Thema direkt in die Blacklist einfügen");
        miBlackTheme.setOnAction(event -> progData.blackList.addAndNotify(new BlackData("", filmData.getTheme(), "", "")));
        final MenuItem miBlackTitle = new MenuItem("Titel direkt in die Blacklist einfügen");
        miBlackTitle.setOnAction(event -> progData.blackList.addAndNotify(new BlackData("", "", filmData.getTitle(), "")));

        submenuBlacklist.getItems().addAll(miBlack, miBlackTheme, miBlackTitle);
        return submenuBlacklist;
    }

    private Menu addBookmark(FilmData filmData) {
        Menu submenuBookmark = new Menu("Bookmark");
        final MenuItem miBookmarkAdd = new MenuItem("neues Bookmark anlegen");
        final MenuItem miBookmarkDel = new MenuItem("Bookmark löschen");
        final MenuItem miBookmarkDelAll = new MenuItem("alle Bookmarks löschen");

        miBookmarkAdd.setDisable(filmData == null);
        miBookmarkDel.setDisable(filmData == null);

        if (filmData != null && filmData.isBookmark()) {
            // Bookmark löschen
            miBookmarkDel.setOnAction(a -> FilmTools.bookmarkFilm(progData, filmData, false));
            miBookmarkAdd.setDisable(true);

        } else {
            // Bookmark anlegen
            miBookmarkDel.setDisable(true);
            miBookmarkAdd.setOnAction(a -> FilmTools.bookmarkFilm(progData, filmData, true));
        }
        miBookmarkDelAll.setOnAction(a -> progData.bookmarks.clearAll(progData.primaryStage));

        submenuBookmark.getItems().addAll(miBookmarkAdd, miBookmarkDel, new SeparatorMenuItem(), miBookmarkDelAll);
        return submenuBookmark;
    }

    private Menu copyUrl(FilmData filmData) {
        final Menu subMenuURL = new Menu("Film-URL kopieren");
        if (filmData == null) {
            subMenuURL.setDisable(true);
            return subMenuURL;
        }

        final String uNormal = filmData.getUrlForResolution(FilmData.RESOLUTION_NORMAL);
        String uHd = filmData.getUrlForResolution(FilmData.RESOLUTION_HD);
        String uLow = filmData.getUrlForResolution(FilmData.RESOLUTION_SMALL);
        String uSub = filmData.getUrlSubtitle();
        if (uHd.equals(uNormal)) {
            uHd = ""; // dann gibts keine
        }
        if (uLow.equals(uNormal)) {
            uLow = ""; // dann gibts keine
        }

        MenuItem item;
        if (!uHd.isEmpty() || !uLow.isEmpty() || !uSub.isEmpty()) {
            // HD
            if (!uHd.isEmpty()) {
                item = new MenuItem("in HD-Auflösung");
                item.setOnAction(a -> PSystemUtils.copyToClipboard(filmData.getUrlForResolution(FilmData.RESOLUTION_HD)));
                subMenuURL.getItems().add(item);
            }

            // normale Auflösung, gibts immer
            item = new MenuItem("in hoher Auflösung");
            item.setOnAction(a -> PSystemUtils.copyToClipboard(filmData.getUrlForResolution(FilmData.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);

            // kleine Auflösung
            if (!uLow.isEmpty()) {
                item = new MenuItem("in geringer Auflösung");
                item.setOnAction(a -> PSystemUtils.copyToClipboard(filmData.getUrlForResolution(FilmData.RESOLUTION_SMALL)));
                subMenuURL.getItems().add(item);
            }

            // Untertitel
            if (!filmData.getUrlSubtitle().isEmpty()) {
                item = new MenuItem("Untertitel-URL kopieren");
                item.setOnAction(a -> PSystemUtils.copyToClipboard(filmData.getUrlSubtitle()));
                subMenuURL.getItems().addAll(new SeparatorMenuItem(), item);
            }

        } else {
            item = new MenuItem("Film-URL kopieren");
            item.setOnAction(a -> PSystemUtils.copyToClipboard(filmData.getUrlForResolution(FilmData.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);
        }

        return subMenuURL;
    }
}
