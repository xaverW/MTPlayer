/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.infotools;

import de.p2tools.mtplayer.controller.ProgSave;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.blackdata.BlackData;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.picon.IconRunner;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.controller.update.SearchProgramUpdate;
import de.p2tools.mtplayer.controller.worker.Busy;
import de.p2tools.mtplayer.gui.dialog.QuitDialogController;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.ikonli.P2IconShow;
import de.p2tools.p2lib.tools.P2Wait;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class ToolDebugPane extends VBox {
    private final ProgData progData;

    public ToolDebugPane() {
        this.progData = ProgData.getInstance();
        init();
    }

    public Tab getTab() {
        Tab tab = new Tab("Debugtools");
        tab.setClosable(false);
        tab.setContent(this);
        return tab;
    }

    private void init() {
        int row = 0;
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(10));
        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcComputedSizeAndHgrow());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(gridPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        getChildren().addAll(scrollPane);

        Button btnIcon = new Button("Programm-Icons anzeigen");
        btnIcon.setMaxWidth(Integer.MAX_VALUE);
        btnIcon.setOnAction(a -> new P2IconShow(Arrays.asList(PIconFactory.PICON.values())));
        gridPane.add(btnIcon, 0, ++row);


        Button btnFilmlist = new Button("Gibt eine neue Filmliste, setzen");
        btnFilmlist.setMaxWidth(Integer.MAX_VALUE);
        btnFilmlist.setOnAction(a -> progData.checkForNewFilmlist.foundNewListProperty()
                .set(!progData.checkForNewFilmlist.foundNewListProperty().get()));
        gridPane.add(btnFilmlist, 0, ++row);


        Button btnShowDownload = new Button("Alle Programmupdates anzeigen");
        btnShowDownload.setMaxWidth(Integer.MAX_VALUE);
        btnShowDownload.setOnAction(a -> new SearchProgramUpdate(progData)
                .searchNewProgramVersion());
        gridPane.add(btnShowDownload, 0, ++row);


        Button btnIconRunner = new Button("Icon-Zirkus");
        btnIconRunner.setMaxWidth(Integer.MAX_VALUE);
        btnIconRunner.setOnAction(a -> IconRunner.run());
        gridPane.add(btnIconRunner, 0, ++row);


        Button btnResetTodayDone = new Button("<SYSTEM_SEARCH_UPDATE_TODAY_DONE> zurücksetzen");
        btnResetTodayDone.setMaxWidth(Integer.MAX_VALUE);
        btnResetTodayDone.setOnAction(a -> ProgConfig.SYSTEM_SEARCH_UPDATE_TODAY_DONE.set("2020.01.01")); // heute noch nicht gemacht
        gridPane.add(btnResetTodayDone, 0, ++row);


        Button btnResetLastSearch = new Button("<SYSTEM_SEARCH_UPDATE_LAST_DATE> zurücksetzen\"");
        btnResetLastSearch.setMaxWidth(Integer.MAX_VALUE);
        btnResetLastSearch.setOnAction(a -> ProgConfig.SYSTEM_SEARCH_UPDATE_LAST_DATE.set("2020.01.01")); // letztes Datum, bis zu dem geprüft wurde, wenn leer wird das buildDate genommen
        gridPane.add(btnResetLastSearch, 0, ++row);


        Button btnResetUpdate = new Button("<SYSTEM_SEARCH_UPDATE_TODAY_DONE und \n" +
                "SYSTEM_SEARCH_UPDATE_LAST_DATE> zurücksetzen");
        btnResetUpdate.setMaxWidth(Integer.MAX_VALUE);
        btnResetUpdate.setOnAction(a -> {
            ProgConfig.SYSTEM_SEARCH_UPDATE_TODAY_DONE.set("2020.01.01"); // heute noch nicht gemacht
            ProgConfig.SYSTEM_SEARCH_UPDATE_LAST_DATE.set("2020.01.01"); // letztes Datum, bis zu dem geprüft wurde, wenn leer wird das buildDate genommen
        });
        gridPane.add(btnResetUpdate, 0, ++row);


        Button btnSave = new Button("Alles Speichern");
        btnSave.setMaxWidth(Double.MAX_VALUE);
        btnSave.setOnAction(a -> ProgSave.saveAll());
        gridPane.add(btnSave, 0, ++row);


        Button btnClearBlack = new Button("Blacklist löschen");
        btnClearBlack.setMaxWidth(Integer.MAX_VALUE);
        btnClearBlack.setOnAction(a -> progData.blackList.clearList());
        gridPane.add(btnClearBlack, 0, ++row);


        Button btnAddBlack = new Button("5_000 Einträge in der Blacklist erstellen");
        btnAddBlack.setMaxWidth(Integer.MAX_VALUE);
        btnAddBlack.setOnAction(a -> {
            progData.blackList.clearList();
            for (int i = 0; i < 5_000; ++i) {
                BlackData bl = new BlackData();
                bl.setThemeTitle(i + "");
                progData.blackList.add(bl);
            }
        });
        gridPane.add(btnAddBlack, 0, ++row);


        Button btnBlack = new Button("Blacklist: Filme markieren");
        btnBlack.setMaxWidth(Integer.MAX_VALUE);
        btnBlack.setOnAction(a -> {
            ProgData.busy.busyOnFx(Busy.BUSY_SRC.GUI, "Blacklist", -1, false);
            new Thread(() -> {
                BlacklistFilterFactory.markFilmsIfBlack(true);
                ProgData.busy.busyOffFx();
            }).start();
        });
        gridPane.add(btnBlack, 0, ++row);


        Button btnQuitt = new Button("Quitt, nicht warten");
        btnQuitt.setMaxWidth(Integer.MAX_VALUE);
        btnQuitt.setOnAction(a -> {
            new QuitDialogController(false);
        });
        gridPane.add(btnQuitt, 0, ++row);


        Button btnBusy = new Button("Set Busy");
        btnBusy.setMaxWidth(Integer.MAX_VALUE);
        btnBusy.setOnAction(a -> {
            Thread busy = new Thread(() -> {
                ProgData.busy.busyOnFx(Busy.BUSY_SRC.GUI, "Test:", -1.0, false);
                for (int i = 1; i < 100; ++i) {
                    double d = (double) i / 100;
                    ProgData.busy.setProgress(d);
                    P2Wait.pause(20);
                }
                ProgData.busy.busyOffFx();
            });
            busy.start();
        });
        gridPane.add(btnBusy, 0, ++row);
    }
}
