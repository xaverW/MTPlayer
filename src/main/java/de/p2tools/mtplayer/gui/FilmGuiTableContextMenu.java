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
import de.p2tools.mtplayer.controller.data.SetDataList;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboFactory;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmTools;
import de.p2tools.mtplayer.controller.filmfilter.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.gui.tools.table.TableFilm;
import de.p2tools.p2lib.tools.PSystemUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class FilmGuiTableContextMenu {

    private final ProgData progData;
    private final FilmGuiController filmGuiController;
    private final TableFilm tableView;

    public FilmGuiTableContextMenu(ProgData progData, FilmGuiController filmGuiController, TableFilm tableView) {
        this.progData = progData;
        this.filmGuiController = filmGuiController;
        this.tableView = tableView;
    }

    public ContextMenu getContextMenu(FilmDataMTP film) {
        final ContextMenu contextMenu = new ContextMenu();

        if (film == null) {
            //dann gibts nur den
            MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
            resetTable.setOnAction(a -> tableView.resetTable());
            contextMenu.getItems().addAll(resetTable);
            return contextMenu;
        }

        // Start/Save
        MenuItem miStart = new MenuItem("Film abspielen");
        miStart.setOnAction(a -> filmGuiController.playFilmUrl());
        MenuItem miSave = new MenuItem("Film speichern");
        miSave.setOnAction(a -> filmGuiController.saveTheFilm());
        contextMenu.getItems().addAll(miStart, miSave);

        Menu mFilter = addFilter(film);// Filter
        Menu mAddAbo = addAbo(film);// Abo
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(mFilter, mAddAbo);

        Menu mStartFilm = startFilmWithSet(); // Film mit Set starten
        if (mStartFilm != null) {
            contextMenu.getItems().add(mStartFilm);
        }

        Menu mBlacklist = addBlacklist(film);// Blacklist
        Menu mBookmark = addBookmark(film);// Bookmark
        Menu mCopyUrl = copyUrl(film);// URL kopieren
        contextMenu.getItems().addAll(mBlacklist, mBookmark, mCopyUrl);

        final MenuItem miFilmsSetShown;
        if (film.isShown()) {
            miFilmsSetShown = new MenuItem("Filme als ungesehen markieren");
            miFilmsSetShown.setOnAction(a -> filmGuiController.setFilmNotShown());
        } else {
            miFilmsSetShown = new MenuItem("Filme als gesehen markieren");
            miFilmsSetShown.setOnAction(a -> filmGuiController.setFilmShown());
        }

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> filmGuiController.showFilmInfo());

        MenuItem miMediaDb = new MenuItem("Titel in der Mediensammlung suchen");
        miMediaDb.setOnAction(a -> filmGuiController.guiFilmMediaCollection());

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
        resetTable.setOnAction(a -> tableView.resetTable());
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(resetTable);

        return contextMenu;
    }

    private Menu addFilter(FilmDataMTP film) {
        Menu submenuFilter = new Menu("Filter");

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

    private Menu addAbo(FilmDataMTP film) {
        Menu submenuAbo = new Menu("Abo");
        final MenuItem miAboDel = new MenuItem("Abo löschen");
        final MenuItem miAboAddFilter = new MenuItem("aus dem Filter ein Abo erstellen");
        final MenuItem miAboAddChannelTheme = new MenuItem("Abo mit Sender und Thema anlegen");
        final MenuItem miAboAddChannelThemeTitle = new MenuItem("Abo mit Sender und Thema und Titel anlegen");
        final MenuItem miAboChange = new MenuItem("Abo ändern");

        // neues Abo aus Filter anlegen
        miAboAddFilter.setOnAction(a -> {
            FilmFilter filmFilter = progData.actFilmFilterWorker.getActFilterSettings();
            progData.aboList.addNewAboFromFilter(filmFilter);
        });
        AboData aboData = AboFactory.findAboToFilm(film, progData.aboList);
        if (aboData == null) {
            //nur dann gibts kein Abo, auch kein ausgeschaltetes, ...
            //neues Abo anlegen
            miAboAddChannelTheme.setOnAction(a ->
                    progData.aboList.addNewAbo(film.getTheme(), film.getChannel(), film.getTheme(), ""));
            miAboAddChannelThemeTitle.setOnAction(a ->
                    progData.aboList.addNewAbo(film.getTheme(), film.getChannel(), film.getTheme(), film.getTitle()));
            //Abo löschen/ändern
            miAboChange.setDisable(true);
            miAboDel.setDisable(true);
        } else {
            //Abo gibts, auch wenn es evtl. ausgeschaltet, Film zu kurz, .. ist
            miAboAddChannelTheme.setDisable(true);
            miAboAddChannelThemeTitle.setDisable(true);
            //Abo löschen/ändern
            miAboChange.setOnAction(event ->
                    progData.aboList.changeAbo(aboData));
            miAboDel.setOnAction(event ->
                    progData.aboList.deleteAbo(aboData));
        }

        submenuAbo.getItems().addAll(miAboAddFilter, miAboAddChannelTheme, miAboAddChannelThemeTitle, miAboChange, miAboDel);
        return submenuAbo;
    }

    private Menu startFilmWithSet() {
        final SetDataList list = progData.setDataList.getSetDataListButton();
        if (!list.isEmpty()) {
            Menu submenuSet = new Menu("Film mit Set starten");

            list.stream().forEach(setData -> {
                final MenuItem item = new MenuItem(setData.getVisibleName());
                item.setOnAction(event -> filmGuiController.playFilmUrlWithSet(setData));
                submenuSet.getItems().add(item);
            });

            return submenuSet;
        }

        return null;
    }

    private Menu addBlacklist(FilmDataMTP filmDataMTP) {
        Menu submenuBlacklist = new Menu("Blacklist");

        final MenuItem miBlack = new MenuItem("Blacklist-Eintrag für den Film erstellen");
        miBlack.setOnAction(event -> BlacklistFilterFactory.addBlack());

        final MenuItem miBlackSenderTheme = new MenuItem("Sender und Thema direkt in die Blacklist einfügen");
        miBlackSenderTheme.setOnAction(event -> BlacklistFilterFactory.addBlack(filmDataMTP.getChannel(), filmDataMTP.getTheme(), ""));
        final MenuItem miBlackTheme = new MenuItem("Thema direkt in die Blacklist einfügen");
        miBlackTheme.setOnAction(event -> BlacklistFilterFactory.addBlack("", filmDataMTP.getTheme(), ""));
        final MenuItem miBlackTitle = new MenuItem("Titel direkt in die Blacklist einfügen");
        miBlackTitle.setOnAction(event -> BlacklistFilterFactory.addBlack("", "", filmDataMTP.getTitle()));

        submenuBlacklist.getItems().addAll(miBlack, miBlackSenderTheme, miBlackTheme, miBlackTitle);
        return submenuBlacklist;
    }

    private Menu addBookmark(FilmDataMTP filmDataMTP) {
        Menu submenuBookmark = new Menu("Bookmark");
        final MenuItem miBookmarkAdd = new MenuItem("neues Bookmark anlegen");
        final MenuItem miBookmarkDel = new MenuItem("Bookmark löschen");
        final MenuItem miBookmarkDelAll = new MenuItem("alle Bookmarks löschen");


        if (filmDataMTP.isBookmark()) {
            // Bookmark löschen
            miBookmarkDel.setOnAction(a -> FilmTools.bookmarkFilm(progData, filmDataMTP, false));
            miBookmarkAdd.setDisable(true);

        } else {
            // Bookmark anlegen
            miBookmarkDel.setDisable(true);
            miBookmarkAdd.setOnAction(a -> FilmTools.bookmarkFilm(progData, filmDataMTP, true));
        }
        miBookmarkDelAll.setOnAction(a -> progData.bookmarks.clearAll(progData.primaryStage));

        submenuBookmark.getItems().addAll(miBookmarkAdd, miBookmarkDel, new SeparatorMenuItem(), miBookmarkDelAll);
        return submenuBookmark;
    }

    private Menu copyUrl(FilmDataMTP filmDataMTP) {
        final Menu subMenuURL = new Menu("Film-URL kopieren");

        final String uNormal = filmDataMTP.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL);
        String uHd = filmDataMTP.getUrlForResolution(FilmDataMTP.RESOLUTION_HD);
        String uLow = filmDataMTP.getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL);
        String uSub = filmDataMTP.getUrlSubtitle();
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
                item.setOnAction(a -> PSystemUtils.copyToClipboard(filmDataMTP.getUrlForResolution(FilmDataMTP.RESOLUTION_HD)));
                subMenuURL.getItems().add(item);
            }

            // normale Auflösung, gibts immer
            item = new MenuItem("in hoher Auflösung");
            item.setOnAction(a -> PSystemUtils.copyToClipboard(filmDataMTP.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);

            // kleine Auflösung
            if (!uLow.isEmpty()) {
                item = new MenuItem("in geringer Auflösung");
                item.setOnAction(a -> PSystemUtils.copyToClipboard(filmDataMTP.getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL)));
                subMenuURL.getItems().add(item);
            }

            // Untertitel
            if (!filmDataMTP.getUrlSubtitle().isEmpty()) {
                item = new MenuItem("Untertitel-URL kopieren");
                item.setOnAction(a -> PSystemUtils.copyToClipboard(filmDataMTP.getUrlSubtitle()));
                subMenuURL.getItems().addAll(new SeparatorMenuItem(), item);
            }

        } else {
            item = new MenuItem("Film-URL kopieren");
            item.setOnAction(a -> PSystemUtils.copyToClipboard(filmDataMTP.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);
        }

        return subMenuURL;
    }
}
