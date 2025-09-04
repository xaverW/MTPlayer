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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboFactory;
import de.p2tools.mtplayer.controller.data.abo.AboListFactory;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFactory;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkFactory;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.data.film.FilmSaveFactory;
import de.p2tools.mtplayer.controller.data.setdata.SetDataList;
import de.p2tools.mtplayer.controller.starter.StartDownloadFactory;
import de.p2tools.mtplayer.gui.tools.table.TableAudio;
import de.p2tools.p2lib.tools.P2ToolsFactory;
import javafx.scene.control.*;

import java.util.Optional;

public class AudioTableContextMenu {

    private final ProgData progData;
    private final AudioGuiController audioGuiController;
    private final TableAudio tableView;

    public AudioTableContextMenu(ProgData progData, AudioGuiController audioGuiController, TableAudio tableView) {
        this.progData = progData;
        this.audioGuiController = audioGuiController;
        this.tableView = tableView;
    }

    public ContextMenu getContextMenu(FilmDataMTP audio) {
        final ContextMenu contextMenu = new ContextMenu();

        // Start/Save
        MenuItem miStart = new MenuItem("Audio abspielen");
        miStart.setOnAction(a -> {
            final Optional<FilmDataMTP> sel = ProgData.getInstance().audioGuiController.getSel(true, true);
            sel.ifPresent(f -> FilmPlayFactory.playFilm(true, f));
        });

        MenuItem miSave = new MenuItem("Audio speichern");
        miSave.setOnAction(a -> FilmSaveFactory.saveAudioList());
        miStart.setDisable(audio == null);
        miSave.setDisable(audio == null);
        contextMenu.getItems().addAll(miStart, miSave);

        // Filter
        Menu mFilter = addFilter(audio);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(mFilter);

        Menu mStartFilm = startFilmWithSet(progData, audio); // Film mit Set starten
        if (mStartFilm != null) {
            contextMenu.getItems().add(mStartFilm);
        }

        // URL kopieren
        Menu mCopyUrl = copyInfos(audio);
        // Film
        Menu mFilm = addFilm(audio);
        contextMenu.getItems().addAll(mCopyUrl, mFilm);


        // Abo
        Menu mAddAbo = addAbo(audio);
        // Blacklist
        Menu mBlacklist = addBlacklist(audio);
        // Bookmark
        Menu mBookmark = addBookmark(audio);
        contextMenu.getItems().addAll(new SeparatorMenuItem(), mAddAbo, mBlacklist, mBookmark);

        contextMenu.getItems().add(new SeparatorMenuItem());
        CheckMenuItem smallTableRow = new CheckMenuItem("Nur kleine Button anzeigen");
        smallTableRow.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_AUDIO);
        CheckMenuItem toolTipTable = new CheckMenuItem("Infos beim Überfahren einer Zeile anzeigen");
        toolTipTable.selectedProperty().bindBidirectional(ProgConfig.AUDIO_GUI_SHOW_TABLE_TOOL_TIP);
        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> tableView.resetTable());
        contextMenu.getItems().addAll(smallTableRow, toolTipTable, resetTable);

        return contextMenu;
    }

    private Menu addFilter(FilmDataMTP film) {
        Menu submenuFilter = new Menu("Filter");

        final MenuItem miFilterChannel = new MenuItem("nach Sender filtern");
        miFilterChannel.setOnAction(event -> progData.filterWorkerAudio.getActFilterSettings().setChannelAndVis(film.getChannel()));
        final MenuItem miFilterTheme = new MenuItem("nach Thema filtern");
        miFilterTheme.setOnAction(event -> progData.filterWorkerAudio.getActFilterSettings().setThemeAndVis(film.getTheme(), false));
        final MenuItem miFilterChannelTheme = new MenuItem("nach Sender und Thema filtern");
        miFilterChannelTheme.setOnAction(event -> {
            progData.filterWorkerAudio.getActFilterSettings().setChannelAndVis(film.getChannel());
            progData.filterWorkerAudio.getActFilterSettings().setThemeAndVis(film.getTheme(), false);
        });
        final MenuItem miFilterChannelThemeTitle = new MenuItem("nach Sender, Thema und Titel filtern");
        miFilterChannelThemeTitle.setOnAction(event -> {
            progData.filterWorkerAudio.getActFilterSettings().setChannelAndVis(film.getChannel());
            progData.filterWorkerAudio.getActFilterSettings().setThemeAndVis(film.getTheme(), false);
            progData.filterWorkerAudio.getActFilterSettings().setTitleAndVis(film.getTitle());
        });

        miFilterChannel.setDisable(film == null);
        miFilterTheme.setDisable(film == null);
        miFilterChannelTheme.setDisable(film == null);
        miFilterChannelThemeTitle.setDisable(film == null);
        submenuFilter.getItems().addAll(miFilterChannel, miFilterTheme, miFilterChannelTheme, miFilterChannelThemeTitle);
        return submenuFilter;
    }

    private Menu addAbo(FilmDataMTP film) {
        // todo audio
        Menu submenuAbo = new Menu("Abo");
        final MenuItem miAboAddFilter = new MenuItem("Aus dem Filter ein Abo erstellen");
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
            AboListFactory.addNewAboFromFilterButton(true);
        });
        AboData aboData = film == null ? null : AboFactory.findAbo(true, film);
        if (aboData == null) {
            //nur dann gibts kein Abo, auch kein ausgeschaltetes, ...
            //neues Abo anlegen
            miAboAddChannelTheme.setOnAction(a ->
                    AboListFactory.addNewAbo(ProgConst.LIST_AUDIO, film.getTheme(), film.getChannel(), film.getTheme(), ""));
            miAboAddChannelThemeTitle.setOnAction(a ->
                    AboListFactory.addNewAbo(ProgConst.LIST_AUDIO, film.getTheme(), film.getChannel(), film.getTheme(), film.getTitle()));
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

    public static Menu startFilmWithSet(ProgData progData, FilmDataMTP audio) {
        final SetDataList list = progData.setDataList.getSetDataListButton();
        if (!list.isEmpty()) {
            Menu submenuSet = new Menu("Audio mit Set starten");
            list.forEach(setData -> {
                final MenuItem item = new MenuItem(setData.getVisibleName());
                item.setDisable(audio == null);
                item.setOnAction(event -> FilmPlayFactory.playFilmListWithSet(true, setData,
                        ProgData.getInstance().audioGuiController.getSelList(true)));
                submenuSet.getItems().add(item);
            });

            return submenuSet;
        }

        return null;
    }

    private Menu addBlacklist(FilmDataMTP film) {
        Menu submenuBlacklist = new Menu("Blacklist");

        final MenuItem miBlack = new MenuItem("Blacklist-Eintrag für das Audio erstellen");
        miBlack.setOnAction(event -> BlacklistFactory.addBlackFilm(BlacklistFactory.BLACK.AUDIO));

        final MenuItem miBlackSenderTheme = new MenuItem("Sender und Thema direkt in die Blacklist einfügen");
        miBlackSenderTheme.setOnAction(event -> BlacklistFactory.addBlack(ProgConst.LIST_AUDIO, film.getChannel(), film.getTheme(), ""));
        final MenuItem miBlackTheme = new MenuItem("Thema direkt in die Blacklist einfügen");
        miBlackTheme.setOnAction(event -> BlacklistFactory.addBlack(ProgConst.LIST_AUDIO, "", film.getTheme(), ""));
        final MenuItem miBlackTitle = new MenuItem("Titel direkt in die Blacklist einfügen");
        miBlackTitle.setOnAction(event -> BlacklistFactory.addBlack(ProgConst.LIST_AUDIO, "", "", film.getTitle()));

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
            miBookmarkDel.setOnAction(a -> BookmarkFactory.removeBookmark(film));
            miBookmarkAdd.setDisable(true);

        } else {
            // Bookmark anlegen
            miBookmarkDel.setDisable(true);
            miBookmarkAdd.setOnAction(a -> BookmarkFactory.addBookmark(true, film));
        }
        miBookmarkDelAll.setOnAction(a -> {
            BookmarkFactory.deleteAll(progData.primaryStage);
        });

        miBookmarkAdd.setDisable(film == null);
        miBookmarkDel.setDisable(film == null);
        miBookmarkDelAll.setDisable(film == null);
        submenuBookmark.getItems().addAll(miBookmarkAdd, miBookmarkDel, new SeparatorMenuItem(), miBookmarkDelAll);
        return submenuBookmark;
    }

    private Menu addFilm(FilmDataMTP film) {
        Menu submenuFilm = new Menu("Audio");

        final MenuItem miLoadTxt = new MenuItem("Info-Datei speichern");
        miLoadTxt.setDisable(film == null);
        miLoadTxt.setOnAction(a -> StartDownloadFactory.downloadSubtitle(true, film, false));

        final MenuItem miFilmsSetShown;
        if (film != null && film.isShown()) {
            miFilmsSetShown = new MenuItem("Audio als ungesehen markieren");
            miFilmsSetShown.setOnAction(a -> audioGuiController.setFilmShown(false));
        } else {
            miFilmsSetShown = new MenuItem("Audio als gesehen markieren");
            miFilmsSetShown.setOnAction(a -> audioGuiController.setFilmShown(true));
        }
        miFilmsSetShown.setDisable(film == null);

        MenuItem miFilmInfo = new MenuItem("Audioinformation anzeigen");
        miFilmInfo.setDisable(film == null);
        miFilmInfo.setOnAction(a -> audioGuiController.showFilmInfo());

        MenuItem miMediaDb = new MenuItem("Audio in der Mediensammlung suchen");
        miMediaDb.setDisable(film == null);
        miMediaDb.setOnAction(a -> audioGuiController.searchFilmInMediaCollection());

        submenuFilm.getItems().addAll(miLoadTxt, miFilmsSetShown, miFilmInfo, miMediaDb);
        return submenuFilm;
    }

    public static Menu copyInfos(FilmDataMTP audio) {
        final Menu subMenuURL = new Menu("Audio-Infos kopieren");
        subMenuURL.setDisable(audio == null);

        final MenuItem miCopyTheme = new MenuItem("Thema");
        miCopyTheme.setDisable(audio == null);
        miCopyTheme.setOnAction(a -> P2ToolsFactory.copyToClipboard(audio.getTheme()));

        final MenuItem miCopyName = new MenuItem("Titel");
        miCopyName.setDisable(audio == null);
        miCopyName.setOnAction(a -> P2ToolsFactory.copyToClipboard(audio.getTitle()));

        final MenuItem miCopyWeb = new MenuItem("Website-URL");
        miCopyWeb.setDisable(audio == null);
        miCopyWeb.setOnAction(a -> P2ToolsFactory.copyToClipboard(audio.getWebsite()));

        subMenuURL.getItems().addAll(miCopyTheme, miCopyName, miCopyWeb);

        MenuItem item = new MenuItem("Film-URL");
        item.setDisable(audio == null);
        item.setOnAction(a -> P2ToolsFactory.copyToClipboard(audio.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL)));
        subMenuURL.getItems().add(item);

        return subMenuURL;
    }
}
