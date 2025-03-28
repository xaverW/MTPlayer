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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboFactory;
import de.p2tools.mtplayer.controller.data.abo.AboListFactory;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFactory;
import de.p2tools.mtplayer.controller.data.setdata.SetDataList;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.film.FilmSaveFactory;
import de.p2tools.mtplayer.controller.film.FilmToolsFactory;
import de.p2tools.mtplayer.controller.starter.StartDownloadFactory;
import de.p2tools.mtplayer.gui.tools.table.TableLiveFilm;
import de.p2tools.p2lib.tools.P2ToolsFactory;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.Optional;

public class LiveFilmTableContextMenu {

    private final ProgData progData;
    private final LiveFilmGuiController filmGuiController;
    private final TableLiveFilm tableView;

    public LiveFilmTableContextMenu(ProgData progData, LiveFilmGuiController filmGuiController, TableLiveFilm tableView) {
        this.progData = progData;
        this.filmGuiController = filmGuiController;
        this.tableView = tableView;
    }

    public ContextMenu getContextMenu(FilmDataMTP film) {
        final ContextMenu contextMenu = new ContextMenu();

        // Start/Save
        MenuItem miStart = new MenuItem("Film abspielen");
        miStart.setOnAction(a -> {
            final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().liveFilmGuiController.getSel(true, true);
            filmSelection.ifPresent(FilmPlayFactory::playFilm);
        });

        MenuItem miSave = new MenuItem("Film speichern");
        miSave.setOnAction(a -> FilmSaveFactory.saveFilmList());
        miStart.setDisable(film == null);
        miSave.setDisable(film == null);
        contextMenu.getItems().addAll(miStart, miSave);

        // Filter
        Menu mFilter = addFilter(film);

        Menu mStartFilm = startFilmWithSet(film); // Film mit Set starten
        if (mStartFilm != null) {
            contextMenu.getItems().add(mStartFilm);
        }

        // URL kopieren
        Menu mCopyUrl = copyUrl(film);
        contextMenu.getItems().addAll(mCopyUrl);

        final MenuItem miLoadUt = new MenuItem("Untertitel speichern");
        miLoadUt.setDisable(film == null || film.getUrlSubtitle().isEmpty());
        miLoadUt.setOnAction(a -> StartDownloadFactory.downloadSubtitle(film));

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setDisable(film == null);
        miFilmInfo.setOnAction(a -> filmGuiController.showFilmInfo());

        MenuItem miMediaDb = new MenuItem("Film in der Mediensammlung suchen");
        miMediaDb.setDisable(film == null);
        miMediaDb.setOnAction(a -> filmGuiController.searchFilmInMediaCollection());

        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren");
        miCopyTheme.setDisable(film == null);
        miCopyTheme.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getTheme()));

        final MenuItem miCopyName = new MenuItem("Titel in die Zwischenablage kopieren");
        miCopyName.setDisable(film == null);
        miCopyName.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getTitle()));

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miLoadUt, miFilmInfo, miMediaDb, miCopyTheme, miCopyName);

        MenuItem toolTipTable = new MenuItem(ProgConfig.LIVE_FILM_GUI_SHOW_TABLE_TOOL_TIP.getValue() ?
                "Keine Infos beim Überfahren einer Zeile anzeigen" : "Infos beim Überfahren einer Zeile anzeigen");
        toolTipTable.setOnAction(a -> ProgConfig.LIVE_FILM_GUI_SHOW_TABLE_TOOL_TIP.setValue(!ProgConfig.LIVE_FILM_GUI_SHOW_TABLE_TOOL_TIP.getValue()));
        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> tableView.resetTable());
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(toolTipTable, resetTable);

        return contextMenu;
    }

    private Menu addFilter(FilmDataMTP film) {
        Menu submenuFilter = new Menu("Filter");

        final MenuItem miFilterChannel = new MenuItem("nach Sender filtern");
        miFilterChannel.setOnAction(event -> progData.filterWorker.getActFilterSettings().setChannelAndVis(film.getChannel()));
        final MenuItem miFilterTheme = new MenuItem("nach Thema filtern");
        miFilterTheme.setOnAction(event -> progData.filterWorker.getActFilterSettings().setThemeAndVis(film.getTheme(), false));
        final MenuItem miFilterChannelTheme = new MenuItem("nach Sender und Thema filtern");
        miFilterChannelTheme.setOnAction(event -> {
            progData.filterWorker.getActFilterSettings().setChannelAndVis(film.getChannel());
            progData.filterWorker.getActFilterSettings().setThemeAndVis(film.getTheme(), false);
        });
        final MenuItem miFilterChannelThemeTitle = new MenuItem("nach Sender, Thema und Titel filtern");
        miFilterChannelThemeTitle.setOnAction(event -> {
            progData.filterWorker.getActFilterSettings().setChannelAndVis(film.getChannel());
            progData.filterWorker.getActFilterSettings().setThemeAndVis(film.getTheme(), false);
            progData.filterWorker.getActFilterSettings().setTitleAndVis(film.getTitle());
        });

        miFilterChannel.setDisable(film == null);
        miFilterTheme.setDisable(film == null);
        miFilterChannelTheme.setDisable(film == null);
        miFilterChannelThemeTitle.setDisable(film == null);
        submenuFilter.getItems().addAll(miFilterChannel, miFilterTheme, miFilterChannelTheme, miFilterChannelThemeTitle);
        return submenuFilter;
    }

    private Menu addAbo(FilmDataMTP film) {
        Menu submenuAbo = new Menu("Abo");
        final MenuItem miAboAddFilter = new MenuItem("aus dem Filter ein Abo erstellen");
        final MenuItem miAboAddChannelTheme = new MenuItem("Abo mit Sender und Thema anlegen");
        final MenuItem miAboAddChannelThemeTitle = new MenuItem("Abo mit Sender und Thema und Titel anlegen");
        final MenuItem miAboChange = new MenuItem("Abo ändern");
        final MenuItem miAboDel = new MenuItem("Abo löschen");

        // gleich hier weil dann evtl. nochmals ausgeschaltet
        miAboAddChannelTheme.setDisable(film == null);
        miAboAddChannelThemeTitle.setDisable(film == null);
        miAboChange.setDisable(film == null);
        miAboDel.setDisable(film == null);

        // neues Abo aus Filter anlegen
        miAboAddFilter.setOnAction(a -> {
            AboListFactory.addNewAboFromFilterButton();
        });
        AboData aboData = film == null ? null : AboFactory.findAbo(film);
        if (aboData == null) {
            //nur dann gibts kein Abo, auch kein ausgeschaltetes, ...
            //neues Abo anlegen
            miAboAddChannelTheme.setOnAction(a ->
                    AboListFactory.addNewAbo(film.getTheme(), film.getChannel(), film.getTheme(), ""));
            miAboAddChannelThemeTitle.setOnAction(a ->
                    AboListFactory.addNewAbo(film.getTheme(), film.getChannel(), film.getTheme(), film.getTitle()));
            //Abo löschen/ändern
            miAboChange.setDisable(true);
            miAboDel.setDisable(true);
        } else {
            //Abo gibts, auch wenn es evtl. ausgeschaltet, Film zu kurz, .. ist
            miAboAddChannelTheme.setDisable(true);
            miAboAddChannelThemeTitle.setDisable(true);
            //Abo löschen/ändern
            miAboChange.setOnAction(event ->
                    AboListFactory.editAbo(aboData));
            miAboDel.setOnAction(event ->
                    AboListFactory.deleteAbo(aboData));
        }

        submenuAbo.getItems().addAll(miAboAddChannelTheme, miAboAddChannelThemeTitle, miAboAddFilter, miAboChange, miAboDel);
        return submenuAbo;
    }

    private Menu startFilmWithSet(FilmDataMTP film) {
        final SetDataList list = progData.setDataList.getSetDataListButton();
        if (!list.isEmpty()) {
            Menu submenuSet = new Menu("Film mit Set starten");
            list.forEach(setData -> {
                final MenuItem item = new MenuItem(setData.getVisibleName());
                item.setDisable(film == null);
                item.setOnAction(event -> FilmPlayFactory.playFilmListWithSet(setData,
                        ProgData.getInstance().liveFilmGuiController.getSelList(true)));
                submenuSet.getItems().add(item);
            });

            return submenuSet;
        }

        return null;
    }

    private Menu addBlacklist(FilmDataMTP film) {
        Menu submenuBlacklist = new Menu("Blacklist");

        final MenuItem miBlack = new MenuItem("Blacklist-Eintrag für den Film erstellen");
        miBlack.setOnAction(event -> BlacklistFactory.addBlackFilm(true));

        final MenuItem miBlackSenderTheme = new MenuItem("Sender und Thema direkt in die Blacklist einfügen");
        miBlackSenderTheme.setOnAction(event -> BlacklistFactory.addBlack(film.getChannel(), film.getTheme(), ""));
        final MenuItem miBlackTheme = new MenuItem("Thema direkt in die Blacklist einfügen");
        miBlackTheme.setOnAction(event -> BlacklistFactory.addBlack("", film.getTheme(), ""));
        final MenuItem miBlackTitle = new MenuItem("Titel direkt in die Blacklist einfügen");
        miBlackTitle.setOnAction(event -> BlacklistFactory.addBlack("", "", film.getTitle()));

        miBlack.setDisable(film == null);
        miBlackSenderTheme.setDisable(film == null);
        miBlackTheme.setDisable(film == null);
        miBlackTitle.setDisable(film == null);
        submenuBlacklist.getItems().addAll(miBlack, miBlackSenderTheme, miBlackTheme, miBlackTitle);
        return submenuBlacklist;
    }

    private Menu addBookmark(FilmDataMTP film) {
        Menu submenuBookmark = new Menu("Bookmark");
        final MenuItem miBookmarkAdd = new MenuItem("neues Bookmark anlegen");
        final MenuItem miBookmarkDel = new MenuItem("Bookmark löschen");
        final MenuItem miBookmarkDelAll = new MenuItem("alle Bookmarks löschen");

        if (film != null && film.isBookmark()) {
            // Bookmark löschen
            miBookmarkDel.setOnAction(a -> FilmToolsFactory.bookmarkFilm(film, false));
            miBookmarkAdd.setDisable(true);

        } else {
            // Bookmark anlegen
            miBookmarkDel.setDisable(true);
            miBookmarkAdd.setOnAction(a -> FilmToolsFactory.bookmarkFilm(film, true));
        }
        miBookmarkDelAll.setOnAction(a -> progData.historyListBookmarks.clearAll(progData.primaryStage));

        miBookmarkAdd.setDisable(film == null);
        miBookmarkDel.setDisable(film == null);
        miBookmarkDelAll.setDisable(film == null);
        submenuBookmark.getItems().addAll(miBookmarkAdd, miBookmarkDel, new SeparatorMenuItem(), miBookmarkDelAll);
        return submenuBookmark;
    }

    private Menu copyUrl(FilmDataMTP film) {
        final Menu subMenuURL = new Menu("Film-URL kopieren");
        subMenuURL.setDisable(film == null);

        final String uNormal = film == null ? "" : film.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL);
        String uHd = film == null ? "" : film.getUrlForResolution(FilmDataMTP.RESOLUTION_HD);
        String uLow = film == null ? "" : film.getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL);
        String uSub = film == null ? "" : film.getUrlSubtitle();

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
                item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmDataMTP.RESOLUTION_HD)));
                subMenuURL.getItems().add(item);
            }

            // normale Auflösung, gibts immer
            item = new MenuItem("in hoher Auflösung");
            item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);

            // kleine Auflösung
            if (!uLow.isEmpty()) {
                item = new MenuItem("in geringer Auflösung");
                item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL)));
                subMenuURL.getItems().add(item);
            }

            // Untertitel
            if (!film.getUrlSubtitle().isEmpty()) {
                item = new MenuItem("Untertitel-URL kopieren");
                item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlSubtitle()));
                subMenuURL.getItems().addAll(new SeparatorMenuItem(), item);
            }

        } else {
            item = new MenuItem("Film-URL kopieren");
            item.setDisable(film == null);
            item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);
        }

        return subMenuURL;
    }
}
