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

import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.gui.tools.Table;
import javafx.scene.control.*;

public class DownloadGuiContextMenu {

    private final Daten daten;
    private final DownloadGuiController downloadGuiController;
    private final TableView tableView;

    public DownloadGuiContextMenu(Daten daten, DownloadGuiController downloadGuiController, TableView tableView) {

        this.daten = daten;
        this.downloadGuiController = downloadGuiController;
        this.tableView = tableView;

    }

    public ContextMenu getContextMenue(Download download) {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, download);
        return contextMenu;
    }

    private void getMenu(ContextMenu contextMenu, Download download) {

        MenuItem miStarten = new MenuItem("Download starten");
        miStarten.setOnAction(a -> downloadGuiController.starten(false));

        MenuItem miStoppen = new MenuItem("Download stoppen");
        miStoppen.setOnAction(a -> downloadGuiController.stoppen(false));

        MenuItem miVorziehen = new MenuItem("Download vorziehen");
        miVorziehen.setOnAction(a -> downloadGuiController.vorziehen());

        MenuItem miLoeschen = new MenuItem("Download zurückstellen");
        miLoeschen.setOnAction(a -> downloadGuiController.zurueckstellen());

        MenuItem miDauerhaftLoeschen = new MenuItem("Download aus Liste entfernen");
        miDauerhaftLoeschen.setOnAction(a -> downloadGuiController.loeschen());

        MenuItem miAendern = new MenuItem("Download ändern");
        miAendern.setOnAction(a -> downloadGuiController.aendern());

        MenuItem miAlleStarten = new MenuItem("alle Downloads starten");
        miAlleStarten.setOnAction(a -> downloadGuiController.starten(true /* alle */));

        MenuItem miAlleStoppen = new MenuItem("alle Downloads stoppen");
        miAlleStoppen.setOnAction(a -> downloadGuiController.stoppen(true /* alle */));

        MenuItem miWartendeStoppen = new MenuItem("wartende Downloads stoppen");
        miWartendeStoppen.setOnAction(a -> downloadGuiController.wartendeStoppen());

        MenuItem miAktualisieren = new MenuItem("Liste der Downloads aktualisieren");
        miAktualisieren.setOnAction(e -> downloadGuiController.aktualisieren());

        MenuItem miAufraeumen = new MenuItem("Liste der Downloads aufräumen");
        miAufraeumen.setOnAction(e -> downloadGuiController.aufraeumen());

        MenuItem miPlayerDownload = new MenuItem("gespeicherten Film (Datei) abspielen");
        miPlayerDownload.setOnAction(a -> downloadGuiController.filmAbspielen());

        MenuItem miDeleteDownload = new MenuItem("gespeicherten Film (Datei) löschen");
        miDeleteDownload.setOnAction(a -> downloadGuiController.filmDateiLoeschen());

        MenuItem miOeffnen = new MenuItem("Zielordner öffnen");
        miOeffnen.setOnAction(e -> downloadGuiController.openDestDir());

        // Abo
        Menu submenueAbo = new Menu("Abo");
        MenuItem miChangeAbo = new MenuItem("Abo ändern");
        MenuItem miDelAbo = new MenuItem("Abo löschen");

        if (download.getAbo() == null) {
            miChangeAbo.setDisable(true);
            miDelAbo.setDisable(true);
        } else {
            miChangeAbo.setOnAction(event ->
                    daten.aboList.changeAbo(download.getAbo()));
            miDelAbo.setOnAction(event -> daten.aboList.aboLoeschen(download.getAbo()));
        }
        submenueAbo.getItems().addAll(miChangeAbo, miDelAbo);

        // MediaDB
        MenuItem miMediaDb = new MenuItem("Titel in der Mediensammlung suchen");
        miMediaDb.setOnAction(a -> downloadGuiController.guiFilmMediensammlung());


        MenuItem miPlayUrl = new MenuItem("Film (URL) abspielen");
        miPlayUrl.setOnAction(a -> downloadGuiController.playUrl());

        MenuItem miCopyUrl = new MenuItem("URL kopieren");
        miCopyUrl.setOnAction(a -> downloadGuiController.copyUrl());

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> downloadGuiController.showFilmInfo());

        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> downloadGuiController.invertSelection());

        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> new Table().resetTable(tableView, Table.TABLE.DOWNLOAD));

        contextMenu.getItems().addAll(miStarten, miStoppen,
                new SeparatorMenuItem(),
                miVorziehen, miLoeschen, miDauerhaftLoeschen, miAendern,
                new SeparatorMenuItem(),
                miAlleStarten, miAlleStoppen, miWartendeStoppen, miAktualisieren, miAufraeumen,
                new SeparatorMenuItem(),
                miPlayerDownload, miDeleteDownload, miOeffnen,
                new SeparatorMenuItem(),
                submenueAbo,
                new SeparatorMenuItem(),
                miMediaDb, miPlayUrl, miCopyUrl, miFilmInfo,
                new SeparatorMenuItem(),
                miSelection, resetTable);

    }

}
