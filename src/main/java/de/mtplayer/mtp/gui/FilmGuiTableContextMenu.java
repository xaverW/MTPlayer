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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.BlackData;
import de.mtplayer.mtp.controller.data.SetDataList;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.gui.tools.table.Table;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
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

        // Separator
        contextMenu.getItems().addAll(new SeparatorMenuItem());
        // Filter
        addFilter(contextMenu, film);
        // Abo
        addAbo(contextMenu, film);
        // Film mit Set starten
        startFilmWithSet(contextMenu, film);
        // Blacklist
        addBlacklist(contextMenu, film);
        // Bookmark
        addBookmark(contextMenu, film);

        // Separator
        contextMenu.getItems().addAll(new SeparatorMenuItem());
        // URL kopieren
        copyUrl(contextMenu, film);

        MenuItem miMediaDb = new MenuItem("Titel in der Mediensammlung suchen");
        miMediaDb.setOnAction(a -> filmGuiController.guiFilmMediaCollection());
        contextMenu.getItems().addAll(new SeparatorMenuItem(), miMediaDb);

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> filmGuiController.showFilmInfo());
        contextMenu.getItems().add(miFilmInfo);

        if (film.isShown()) {
            final MenuItem miFilmsNotShown = new MenuItem("Filme als ungesehen markieren");
            miFilmsNotShown.setOnAction(a -> filmGuiController.setFilmNotShown());
            contextMenu.getItems().add(miFilmsNotShown);
        } else {
            final MenuItem miFilmsShown = new MenuItem("Filme als gesehen markieren");
            miFilmsShown.setOnAction(a -> filmGuiController.setFilmShown());
            contextMenu.getItems().add(miFilmsShown);
        }

        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> new Table().resetTable(tableView, Table.TABLE.FILM));
        contextMenu.getItems().add(resetTable);
    }

    private void addFilter(ContextMenu contextMenu, Film film) {
        Menu submenuFilter = new Menu("Filter");
        final MenuItem miFilterChannel = new MenuItem("nach Sender filtern");
        miFilterChannel.setOnAction(event -> progData.storedFilter.getSelectedFilter().setChannelAndVis(film.getChannel()));
        final MenuItem miFilterTheme = new MenuItem("nach Thema filtern");
        miFilterTheme.setOnAction(event -> progData.storedFilter.getSelectedFilter().setThemeAndVis(film.getTheme()));
        final MenuItem miFilterChannelTheme = new MenuItem("nach Sender und Thema filtern");
        miFilterChannelTheme.setOnAction(event -> {
            progData.storedFilter.getSelectedFilter().setChannelAndVis(film.getChannel());
            progData.storedFilter.getSelectedFilter().setThemeAndVis(film.getTheme());
        });
        final MenuItem miFilterChannelThemeTitle = new MenuItem("nach Sender, Thema und Titel filtern");
        miFilterChannelThemeTitle.setOnAction(event -> {
            progData.storedFilter.getSelectedFilter().setChannelAndVis(film.getChannel());
            progData.storedFilter.getSelectedFilter().setThemeAndVis(film.getTheme());
            progData.storedFilter.getSelectedFilter().setTitleAndVis(film.getTitle());
        });
        submenuFilter.getItems().addAll(miFilterChannel, miFilterTheme, miFilterChannelTheme, miFilterChannelThemeTitle);
        contextMenu.getItems().add(submenuFilter);
    }

    private void addAbo(ContextMenu contextMenu, Film film) {
        Menu submenuAbo = new Menu("Abo");
        final MenuItem miAboDel = new MenuItem("Abo löschen");
        final MenuItem miAboAddFilter = new MenuItem("aus dem Filter ein Abo erstellen");
        final MenuItem miAboAddChannelTheme = new MenuItem("Abo mit Sender und Thema anlegen");
        final MenuItem miAboAddChannelThemeTitle = new MenuItem("Abo mit Sender und Thema und Titel anlegen");
        final MenuItem miAboChange = new MenuItem("Abo ändern");


        if (film.getAbo() == null) {
            // neues Abo anlegen
            miAboAddFilter.setOnAction(a -> {
                SelectedFilter selectedFilter = progData.storedFilter.getSelectedFilter();
                progData.aboList.addNewAbo(selectedFilter);
            });
            miAboAddChannelTheme.setOnAction(a ->
                    progData.aboList.addNewAbo(film.getTheme(), film.getChannel(), film.getTheme(), ""));
            miAboAddChannelThemeTitle.setOnAction(a ->
                    progData.aboList.addNewAbo(film.getTheme(), film.getChannel(), film.getTheme(), film.getTitle()));
            // Abo löschen/ändern
            miAboChange.setDisable(true);
            miAboDel.setDisable(true);
        } else {
            // Abo gibts schon
            miAboAddFilter.setDisable(true);
            miAboAddChannelTheme.setDisable(true);
            miAboAddChannelThemeTitle.setDisable(true);
            // Abo löschen/ändern
            miAboChange.setOnAction(event ->
                    progData.aboList.changeAbo(film.getAbo()));
            miAboDel.setOnAction(event ->
                    progData.aboList.deleteAbo(film.getAbo()));
        }

        submenuAbo.getItems().addAll(miAboAddFilter, miAboAddChannelTheme, miAboAddChannelThemeTitle, miAboChange, miAboDel);
        contextMenu.getItems().add(submenuAbo);
    }

    private void startFilmWithSet(ContextMenu contextMenu, Film film) {
        final SetDataList list = progData.setDataList.getSetDataListButton();
        if (list.size() > 1) {

            Menu submenuSet = new Menu("Film mit Set starten");
            list.stream().forEach(setData -> {

                final MenuItem item = new MenuItem(setData.getVisibleName());
                item.setOnAction(event -> filmGuiController.playFilmUrlWithSet(setData));
                submenuSet.getItems().add(item);

            });
            contextMenu.getItems().add(submenuSet);
        }
    }

    private void addBlacklist(ContextMenu contextMenu, Film film) {
        Menu submenuBlacklist = new Menu("Blacklist");
        final MenuItem miBlackChannel = new MenuItem("Sender in die Blacklist einfügen");
        miBlackChannel.setOnAction(event -> progData.blackList.addAndNotify(new BlackData(film.getChannel(), "", "", "")));
        final MenuItem miBlackTheme = new MenuItem("Thema in die Blacklist einfügen");
        miBlackTheme.setOnAction(event -> progData.blackList.addAndNotify(new BlackData("", film.getTheme(), "", "")));
        final MenuItem miBlackChannelTheme = new MenuItem("Sender und Thema in die Blacklist einfügen");
        miBlackChannelTheme.setOnAction(event -> progData.blackList.addAndNotify(new BlackData(film.getChannel(), film.getTheme(), "", "")));

        submenuBlacklist.getItems().addAll(miBlackChannel, miBlackTheme, miBlackChannelTheme);
        contextMenu.getItems().addAll(submenuBlacklist);

    }

    private void addBookmark(ContextMenu contextMenu, Film film) {
        Menu submenuBookmark = new Menu("Bookmarks");
        final MenuItem miBookmarkDel = new MenuItem("Bookmark löschen");
        final MenuItem miBookmarkAdd = new MenuItem("neues Bookmark anlegen");

        if (film.isBookmark()) {
            // Bookmark löschen
            miBookmarkDel.setOnAction(a -> FilmTools.bookmarkFilm(progData, film, false));
            miBookmarkAdd.setDisable(true);
        } else {
            // Bookmark anlegen
            miBookmarkDel.setDisable(true);
            miBookmarkAdd.setOnAction(a -> FilmTools.bookmarkFilm(progData, film, true));
        }

        submenuBookmark.getItems().addAll(miBookmarkDel, miBookmarkAdd);
        contextMenu.getItems().add(submenuBookmark);
    }

    private void copyUrl(ContextMenu contextMenu, Film film) {
        final String uNormal = film.getUrlForResolution(Film.RESOLUTION_NORMAL);
        String uHd = film.getUrlForResolution(Film.RESOLUTION_HD);
        String uLow = film.getUrlForResolution(Film.RESOLUTION_SMALL);
        String uSub = film.getUrlSubtitle();
        MenuItem item;
        if (uHd.equals(uNormal)) {
            uHd = ""; // dann gibts keine
        }
        if (uLow.equals(uNormal)) {
            uLow = ""; // dann gibts keine
        }
        if (!uNormal.isEmpty()) {
            if (!uHd.isEmpty() || !uLow.isEmpty() || !uSub.isEmpty()) {
                final Menu subMenuURL = new Menu("Film-URL kopieren");

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

                if (!film.getUrlSubtitle().isEmpty()) {
                    item = new MenuItem("Untertitel-URL kopieren");
                    item.setOnAction(a -> PSystemUtils.copyToClipboard(film.getUrlSubtitle()));
                    subMenuURL.getItems().addAll(new SeparatorMenuItem(), item);
                }

                contextMenu.getItems().add(subMenuURL);
            } else {
                item = new MenuItem("Film-URL kopieren");
                item.setOnAction(a -> PSystemUtils.copyToClipboard(film.getUrlForResolution(Film.RESOLUTION_NORMAL)));
                contextMenu.getItems().add(item);
            }
        }
    }
}
