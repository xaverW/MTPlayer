/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

import de.mtplayer.mLib.tools.SysTools;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.BlackData;
import de.mtplayer.mtp.controller.data.SetList;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.gui.tools.Table;
import javafx.scene.control.*;

public class FilmGuiContextMenu {

    private final Daten daten;
    private final FilmGuiController filmGuiController;
    private final TableView tableView;

    public FilmGuiContextMenu(Daten daten, FilmGuiController filmGuiController, TableView tableView) {
        this.daten = daten;
        this.filmGuiController = filmGuiController;
        this.tableView = tableView;
    }

    public ContextMenu getContextMenue(Film film) {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, film);
        return contextMenu;
    }

    private void getMenu(ContextMenu contextMenu, Film film) {

        // Start/Stop
        MenuItem miStart = new MenuItem("Film abspielen");
        miStart.setOnAction(a -> filmGuiController.playFilmUrl());

        MenuItem miSave = new MenuItem("Film speichern");
        miSave.setOnAction(a -> filmGuiController.filmSpeichern());

        contextMenu.getItems().addAll(miStart, miSave, new SeparatorMenuItem());

        // Filter
        Menu submenuFilter = new Menu("Filter");
        final MenuItem miFilterSender = new MenuItem("nach Sender filtern");
        miFilterSender.setOnAction(event -> daten.storedFilter.getSelectedFilter().setSenderAndVis(film.getSender()));
        final MenuItem miFilterThema = new MenuItem("nach Thema filtern");
        miFilterThema.setOnAction(event -> daten.storedFilter.getSelectedFilter().setThemeAndVis(film.getThema()));
        final MenuItem miFilterSenderThema = new MenuItem("nach Sender und Thema filtern");
        miFilterSenderThema.setOnAction(event -> {
            daten.storedFilter.getSelectedFilter().setSenderAndVis(film.getSender());
            daten.storedFilter.getSelectedFilter().setThemeAndVis(film.getThema());
        });
        final MenuItem miFilterSenderThemaTitle = new MenuItem("nach Sender, Thema und Titel filtern");
        miFilterSenderThemaTitle.setOnAction(event -> {
            daten.storedFilter.getSelectedFilter().setSenderAndVis(film.getSender());
            daten.storedFilter.getSelectedFilter().setThemeAndVis(film.getThema());
            daten.storedFilter.getSelectedFilter().setTitleAndVis(film.getTitel());
        });
        submenuFilter.getItems().addAll(miFilterSender, miFilterThema, miFilterSenderThema, miFilterSenderThemaTitle);
        contextMenu.getItems().add(submenuFilter);

        // Abo
        Menu submenuAbo = new Menu("Abo");
        final MenuItem miAboDel = new MenuItem("Abo Löschen");
        final MenuItem miAboAddSenderThema = new MenuItem("Abo mit Sender und Thema anlegen");
        final MenuItem miAboAddSenderThemaTitle = new MenuItem("Abo mit Sender und Thema und Titel anlegen");
        final MenuItem miAboChange = new MenuItem("Abo ändern");

        if (film.getAbo() == null) {
            miAboDel.setDisable(true);
            miAboChange.setDisable(true);
            miAboAddSenderThema.setOnAction(a ->
                    daten.aboList.addAbo(film.getThema(), film.getSender(), film.getThema(), ""));
            miAboAddSenderThemaTitle.setOnAction(a ->
                    daten.aboList.addAbo(film.getThema(), film.getSender(), film.getThema(), film.getTitel()));
        } else {
            miAboAddSenderThema.setDisable(true);
            miAboAddSenderThemaTitle.setDisable(true);
            miAboDel.setOnAction(event ->
                    daten.aboList.aboLoeschen(film.getAbo()));
            miAboChange.setOnAction(event ->
                    daten.aboList.changeAbo(film.getAbo()));
        }
        submenuAbo.getItems().addAll(miAboDel, miAboAddSenderThema, miAboAddSenderThemaTitle, miAboChange);
        contextMenu.getItems().add(submenuAbo);

        // Film mit Set starten
        final SetList liste = daten.setList.getListeButton();
        if (liste.size() > 1) {

            Menu submenuSet = new Menu("Film mit Set starten");
            liste.stream().forEach(datenPset -> {

                final MenuItem item = new MenuItem(datenPset.getName());
                item.setOnAction(event -> filmGuiController.playFilmUrlWithSet(datenPset));
                submenuSet.getItems().add(item);

            });
            contextMenu.getItems().add(submenuSet);
        }

        // Blacklist
        Menu submenuBlacklist = new Menu("Blacklist");
        final MenuItem miBlackSender = new MenuItem("Sender in die Blacklist einfügen");
        miBlackSender.setOnAction(event -> daten.blackList.addAndNotify(new BlackData(film.getSender(), "", "", "")));
        final MenuItem miBlackThema = new MenuItem("Thema in die Blacklist einfügen");
        miBlackThema.setOnAction(event -> daten.blackList.addAndNotify(new BlackData("", film.getThema(), "", "")));
        final MenuItem miBlackSenderThema = new MenuItem("Sender und Thema in die Blacklist einfügen");
        miBlackSenderThema.setOnAction(event -> daten.blackList.addAndNotify(new BlackData(film.getSender(), film.getThema(), "", "")));

        submenuBlacklist.getItems().addAll(miBlackSender, miBlackThema, miBlackSenderThema);
        contextMenu.getItems().addAll(submenuBlacklist, new SeparatorMenuItem());

        // URL kopieren
        final String uNormal = film.getUrlFuerAufloesung(Film.AUFLOESUNG_NORMAL);
        String uHd = film.getUrlFuerAufloesung(Film.AUFLOESUNG_HD);
        String uLow = film.getUrlFuerAufloesung(Film.AUFLOESUNG_KLEIN);
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
                final Menu submenueURL = new Menu("Film-URL kopieren");

                // HD
                if (!uHd.isEmpty()) {
                    item = new MenuItem("in HD-Auflösung");
                    item.setOnAction(a -> SysTools.copyToClipboard(film.getUrlFuerAufloesung(Film.AUFLOESUNG_HD)));
                    submenueURL.getItems().add(item);
                }

                // normale Auflösung, gibts immer
                item = new MenuItem("in hoher Auflösung");
                item.setOnAction(a -> SysTools.copyToClipboard(film.getUrlFuerAufloesung(Film.AUFLOESUNG_NORMAL)));
                submenueURL.getItems().add(item);

                // kleine Auflösung
                if (!uLow.isEmpty()) {
                    item = new MenuItem("in geringer Auflösung");
                    item.setOnAction(a -> SysTools.copyToClipboard(film.getUrlFuerAufloesung(Film.AUFLOESUNG_KLEIN)));
                    submenueURL.getItems().add(item);
                }

                if (!film.getUrlSubtitle().isEmpty()) {
                    item = new MenuItem("Untertitel-URL kopieren");
                    item.setOnAction(a -> SysTools.copyToClipboard(film.getUrlSubtitle()));
                    submenueURL.getItems().addAll(new SeparatorMenuItem(), item);
                }

                contextMenu.getItems().add(submenueURL);
            } else {
                item = new MenuItem("Film-URL kopieren");
                item.setOnAction(a -> SysTools.copyToClipboard(film.getUrlFuerAufloesung(Film.AUFLOESUNG_NORMAL)));
                contextMenu.getItems().add(item);
            }
        }

        MenuItem miMediaDb = new MenuItem("Titel in der Mediensammlung suchen");
        miMediaDb.setOnAction(a -> filmGuiController.guiFilmMediensammlung());
        contextMenu.getItems().addAll(new SeparatorMenuItem(), miMediaDb);

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> filmGuiController.showFilmInfo());
        contextMenu.getItems().add(miFilmInfo);

        if (film.isShown()) {
            final MenuItem miFilmeUngesehen = new MenuItem("Filme als ungesehen markieren");
            miFilmeUngesehen.setOnAction(a -> filmGuiController.filmUngesehen());
            contextMenu.getItems().add(miFilmeUngesehen);
        } else {
            final MenuItem miFilmeGesehen = new MenuItem("Filme als gesehen markieren");
            miFilmeGesehen.setOnAction(a -> filmGuiController.filmGesehen());
            contextMenu.getItems().add(miFilmeGesehen);
        }

        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> new Table().resetTable(tableView, Table.TABLE.FILM));
        contextMenu.getItems().add(resetTable);
    }

}
