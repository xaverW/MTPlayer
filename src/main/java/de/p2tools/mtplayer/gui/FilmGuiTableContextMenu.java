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

import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilter;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.BlackData;
import de.p2tools.mtplayer.controller.data.SetDataList;
import de.p2tools.mtplayer.controller.data.film.Film;
import de.p2tools.mtplayer.controller.data.film.FilmTools;
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

    public ContextMenu getContextMenu(Film film) {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, film);
        return contextMenu;
    }

    private void getMenu(ContextMenu contextMenu, Film film) {
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

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miFilmsSetShown, miFilmInfo, miMediaDb);


        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> new Table().resetTable(tableView, Table.TABLE.FILM));
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(resetTable);
    }

    private Menu addFilter(Film film) {
        Menu submenuFilter = new Menu("Filter");
        if (film == null) {
            submenuFilter.setDisable(true);
            return submenuFilter;
        }

        final MenuItem miFilterChannel = new MenuItem("nach Sender filtern");
        miFilterChannel.setOnAction(event -> progData.storedFilters.getActFilterSettings().setChannelAndVis(film.getChannel()));
        final MenuItem miFilterTheme = new MenuItem("nach Thema filtern");
        miFilterTheme.setOnAction(event -> progData.storedFilters.getActFilterSettings().setThemeAndVis(film.getTheme()));
        final MenuItem miFilterChannelTheme = new MenuItem("nach Sender und Thema filtern");
        miFilterChannelTheme.setOnAction(event -> {
            progData.storedFilters.getActFilterSettings().setChannelAndVis(film.getChannel());
            progData.storedFilters.getActFilterSettings().setThemeAndVis(film.getTheme());
        });
        final MenuItem miFilterChannelThemeTitle = new MenuItem("nach Sender, Thema und Titel filtern");
        miFilterChannelThemeTitle.setOnAction(event -> {
            progData.storedFilters.getActFilterSettings().setChannelAndVis(film.getChannel());
            progData.storedFilters.getActFilterSettings().setThemeAndVis(film.getTheme());
            progData.storedFilters.getActFilterSettings().setTitleAndVis(film.getTitle());
        });

        submenuFilter.getItems().addAll(miFilterChannel, miFilterTheme, miFilterChannelTheme, miFilterChannelThemeTitle);
        return submenuFilter;
    }

    private Menu addAbo(Film film) {
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
            SelectedFilter selectedFilter = progData.storedFilters.getActFilterSettings();
            progData.aboList.addNewAbo(selectedFilter);
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

    private Menu startFilmWithSet(Film film) {
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

    private Menu addBlacklist(Film film) {
        Menu submenuBlacklist = new Menu("Blacklist");
        if (film == null) {
            submenuBlacklist.setDisable(true);
            return submenuBlacklist;
        }

        final MenuItem miBlackChannel = new MenuItem("Sender in die Blacklist einfügen");
        miBlackChannel.setOnAction(event -> progData.blackList.addAndNotify(new BlackData(film.getChannel(), "", "", "")));
        final MenuItem miBlackTheme = new MenuItem("Thema in die Blacklist einfügen");
        miBlackTheme.setOnAction(event -> progData.blackList.addAndNotify(new BlackData("", film.getTheme(), "", "")));
        final MenuItem miBlackChannelTheme = new MenuItem("Sender und Thema in die Blacklist einfügen");
        miBlackChannelTheme.setOnAction(event -> progData.blackList.addAndNotify(new BlackData(film.getChannel(), film.getTheme(), "", "")));

        submenuBlacklist.getItems().addAll(miBlackChannel, miBlackTheme, miBlackChannelTheme);
        return submenuBlacklist;
    }

    private Menu addBookmark(Film film) {
        Menu submenuBookmark = new Menu("Bookmark");
        final MenuItem miBookmarkAdd = new MenuItem("neues Bookmark anlegen");
        final MenuItem miBookmarkDel = new MenuItem("Bookmark löschen");
        final MenuItem miBookmarkDelAll = new MenuItem("alle Bookmarks löschen");

        miBookmarkAdd.setDisable(film == null);
        miBookmarkDel.setDisable(film == null);

        if (film != null && film.isBookmark()) {
            // Bookmark löschen
            miBookmarkDel.setOnAction(a -> FilmTools.bookmarkFilm(progData, film, false));
            miBookmarkAdd.setDisable(true);

        } else {
            // Bookmark anlegen
            miBookmarkDel.setDisable(true);
            miBookmarkAdd.setOnAction(a -> FilmTools.bookmarkFilm(progData, film, true));
        }
        miBookmarkDelAll.setOnAction(a -> progData.bookmarks.clearAll(progData.primaryStage));

        submenuBookmark.getItems().addAll(miBookmarkAdd, miBookmarkDel, new SeparatorMenuItem(), miBookmarkDelAll);
        return submenuBookmark;
    }

    private Menu copyUrl(Film film) {
        final Menu subMenuURL = new Menu("Film-URL kopieren");
        if (film == null) {
            subMenuURL.setDisable(true);
            return subMenuURL;
        }

        final String uNormal = film.getUrlForResolution(Film.RESOLUTION_NORMAL);
        String uHd = film.getUrlForResolution(Film.RESOLUTION_HD);
        String uLow = film.getUrlForResolution(Film.RESOLUTION_SMALL);
        String uSub = film.getUrlSubtitle();
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
                item.setOnAction(a -> PSystemUtils.copyToClipboard(film.getUrlForResolution(Film.RESOLUTION_HD)));
                subMenuURL.getItems().add(item);
            }

            // normale Auflösung, gibts immer
            item = new MenuItem("in hoher Auflösung");
            item.setOnAction(a -> PSystemUtils.copyToClipboard(film.getUrlForResolution(Film.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);

            // kleine Auflösung
            if (!uLow.isEmpty()) {
                item = new MenuItem("in geringer Auflösung");
                item.setOnAction(a -> PSystemUtils.copyToClipboard(film.getUrlForResolution(Film.RESOLUTION_SMALL)));
                subMenuURL.getItems().add(item);
            }

            // Untertitel
            if (!film.getUrlSubtitle().isEmpty()) {
                item = new MenuItem("Untertitel-URL kopieren");
                item.setOnAction(a -> PSystemUtils.copyToClipboard(film.getUrlSubtitle()));
                subMenuURL.getItems().addAll(new SeparatorMenuItem(), item);
            }

        } else {
            item = new MenuItem("Film-URL kopieren");
            item.setOnAction(a -> PSystemUtils.copyToClipboard(film.getUrlForResolution(Film.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);
        }

        return subMenuURL;
    }
}
