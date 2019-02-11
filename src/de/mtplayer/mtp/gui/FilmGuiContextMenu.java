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
import de.mtplayer.mtp.gui.tools.table.Table;
import de.p2tools.p2Lib.tools.PSystemUtils;
import javafx.scene.control.*;

public class FilmGuiContextMenu {

    private final ProgData progData;
    private final FilmGuiController filmGuiController;
    private final TableView tableView;

    public FilmGuiContextMenu(ProgData progData, FilmGuiController filmGuiController, TableView tableView) {
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

        // Start/Stop
        MenuItem miStart = new MenuItem("Film abspielen");
        miStart.setOnAction(a -> filmGuiController.playFilmUrl());

        MenuItem miSave = new MenuItem("Film speichern");
        miSave.setOnAction(a -> filmGuiController.saveTheFilm());

        contextMenu.getItems().addAll(miStart, miSave, new SeparatorMenuItem());

        // Filter
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

        // Abo
        Menu submenuAbo = new Menu("Abo");
        final MenuItem miAboDel = new MenuItem("Abo Löschen");
        final MenuItem miAboAddChannelTheme = new MenuItem("Abo mit Sender und Thema anlegen");
        final MenuItem miAboAddChannelThemeTitle = new MenuItem("Abo mit Sender und Thema und Titel anlegen");
        final MenuItem miAboChange = new MenuItem("Abo ändern");

        if (film.getAbo() == null) {
            miAboDel.setDisable(true);
            miAboChange.setDisable(true);
            miAboAddChannelTheme.setOnAction(a ->
                    progData.aboList.addAbo(film.getTheme(), film.getChannel(), film.getTheme(), ""));
            miAboAddChannelThemeTitle.setOnAction(a ->
                    progData.aboList.addAbo(film.getTheme(), film.getChannel(), film.getTheme(), film.getTitle()));
        } else {
            miAboAddChannelTheme.setDisable(true);
            miAboAddChannelThemeTitle.setDisable(true);
            miAboDel.setOnAction(event ->
                    progData.aboList.deleteAbo(film.getAbo()));
            miAboChange.setOnAction(event ->
                    progData.aboList.changeAbo(film.getAbo()));
        }
        submenuAbo.getItems().addAll(miAboDel, miAboAddChannelTheme, miAboAddChannelThemeTitle, miAboChange);
        contextMenu.getItems().add(submenuAbo);

        // Film mit Set starten
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

        // Blacklist
        Menu submenuBlacklist = new Menu("Blacklist");
        final MenuItem miBlackChannel = new MenuItem("Sender in die Blacklist einfügen");
        miBlackChannel.setOnAction(event -> progData.blackList.addAndNotify(new BlackData(film.getChannel(), "", "", "")));
        final MenuItem miBlackTheme = new MenuItem("Thema in die Blacklist einfügen");
        miBlackTheme.setOnAction(event -> progData.blackList.addAndNotify(new BlackData("", film.getTheme(), "", "")));
        final MenuItem miBlackChannelTheme = new MenuItem("Sender und Thema in die Blacklist einfügen");
        miBlackChannelTheme.setOnAction(event -> progData.blackList.addAndNotify(new BlackData(film.getChannel(), film.getTheme(), "", "")));

        submenuBlacklist.getItems().addAll(miBlackChannel, miBlackTheme, miBlackChannelTheme);
        contextMenu.getItems().addAll(submenuBlacklist, new SeparatorMenuItem());

        // URL kopieren
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

}
