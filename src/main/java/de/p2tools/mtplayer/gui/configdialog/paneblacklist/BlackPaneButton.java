/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.gui.configdialog.paneblacklist;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.blackdata.BlackData;
import de.p2tools.mtplayer.controller.data.blackdata.BlackList;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFactory;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadListener;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BlackPaneButton {

    P2LoadListener listenerLoadFilmlist;

    public BlackPaneButton() {
    }

    void close() {
        LoadFilmFactory.getInstance().loadFilmlist.p2LoadNotifier.removeListenerLoadFilmlist(listenerLoadFilmlist);
    }

    void addButton(Stage stage, VBox vBox, TableView<BlackData> tableView,
                   BooleanProperty blackDataChanged, BlackList list) {
        Button btnDel = new Button("");
        btnDel.setGraphic(ProgIcons.ICON_BUTTON_REMOVE.getImageView());
        btnDel.setOnAction(event -> {
            final ObservableList<BlackData> selected = tableView.getSelectionModel().getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                P2Alert.showInfoNoSelection();
            } else {
                blackDataChanged.set(true);
                list.removeBlackData(selected);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setGraphic(ProgIcons.ICON_BUTTON_ADD.getImageView());
        btnNew.setOnAction(event -> {
            blackDataChanged.set(true);
            BlackData blackData = new BlackData();
            list.add(blackData);
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(blackData);
            tableView.scrollTo(blackData);
        });

        final Button btnHelpCount = P2Button.helpButton(stage, "Treffer zählen",
                HelpText.BLACKLIST_COUNT);

        Button btnCountHits = new Button("_Treffer zählen");
        btnCountHits.setTooltip(new Tooltip("Damit wird die Filmliste nach \"Treffern\" durchsucht.\n" +
                "Für jeden Eintrag in der Blacklist wird gezählt,\n" +
                "wie viele Filme damit geblockt werden."));
        btnCountHits.setOnAction(a -> {
            if (ProgData.busy.isBusy()) {
                return;
            }
            ProgData.busy.busyOnFx("Blacklist", -1, false);
            new Thread(() -> {
                BlacklistFilterFactory.countHits(list);
                P2TableFactory.refreshTable(tableView);
                ProgData.busy.busyOffFx();
            }).start();
        });

        Button btnAddStandards = new Button("_Standards einfügen");
        btnAddStandards.setTooltip(new Tooltip("Die Standardeinträge der Liste anfügen"));
        btnAddStandards.setOnAction(event -> {
            blackDataChanged.set(true);
            BlacklistFactory.addStandardsList(list);
        });

        Button btnCleanList = new Button("_Putzen");
        btnCleanList.setTooltip(new Tooltip("In der Liste werden doppelte und leere Einträge gelöscht"));
        btnCleanList.setOnAction(event -> {
            blackDataChanged.set(true);
            list.cleanTheList();
        });

        Button btnClear = new Button("_Alle löschen");
        btnClear.setTooltip(new Tooltip("Alle Einträge in der Liste werden gelöscht"));
        btnClear.setOnAction(event -> {
            if (list.size() > 0) {
                if (!P2Alert.showAlertOkCancel(stage, "Liste löschen", "Sollen alle Tabelleneinträge gelöscht werden?",
                        "Die Tabelle wird komplett gelöscht und alle Einträge gehen verloren.")) {
                    return;
                }
            }
            blackDataChanged.set(true);
            list.clearList();
        });
        listenerLoadFilmlist = new P2LoadListener() {
            @Override
            public void start(P2LoadEvent event) {
                btnCountHits.setDisable(true);
            }

            @Override
            public void finished(P2LoadEvent event) {
                btnCountHits.setDisable(false);
            }
        };
        LoadFilmFactory.getInstance().loadFilmlist.p2LoadNotifier.addListenerLoadFilmlist(listenerLoadFilmlist);

        HBox hBoxButton = new HBox(P2LibConst.DIST_BUTTON);
        hBoxButton.getChildren().addAll(btnNew, btnDel, btnClear);

        hBoxButton.getChildren().addAll(P2GuiTools.getHBoxGrower(), btnCountHits, btnAddStandards, btnCleanList);
        hBoxButton.getChildren().addAll(btnHelpCount);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        VBox vb = new VBox(P2LibConst.DIST_BUTTON);
        vb.getChildren().addAll(hBoxButton);
        vBox.getChildren().add(vb);
    }
}
