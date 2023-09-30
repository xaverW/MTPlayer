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
import de.p2tools.mtplayer.controller.data.blackdata.BlackData;
import de.p2tools.mtplayer.controller.data.blackdata.BlackList;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2Button;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BlackPaneMoveButton {
    BlackPaneMoveButton() {
    }

    static void addMoveButton(Stage stage, VBox vBox, TableView<BlackData> tableView, ProgData progData,
                              boolean controlBlackListNotFilmFilter, BooleanProperty blackDataChanged, BlackList list) {

        final Button btnHelpCount = P2Button.helpButton(stage, "Filter kopieren oder verschieben",
                HelpText.BLACKLIST_MOVE);

        Button btnCopy = new Button(controlBlackListNotFilmFilter ? "_Kopieren nach \"Filmliste laden\"" : "_Kopieren nach \"Blacklist\"");
        btnCopy.setTooltip(new Tooltip("Damit werden die markierten Filter in den " +
                "anderen Filter (Filmfilter/Blacklist) kopiert"));
        btnCopy.setOnAction(a -> {
            final ObservableList<BlackData> selected = tableView.getSelectionModel().getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                PAlert.showInfoNoSelection();

            } else {
                for (BlackData bl : selected) {
                    BlackData cpy = bl.getCopy();
                    if (controlBlackListNotFilmFilter) {
                        //dann in den FilmListFilter einfügen
                        progData.filmListFilter.addAll(cpy);
                    } else {
                        //dann in die Blacklist einfügen
                        blackDataChanged.set(true);
                        progData.blackList.addAll(cpy);
                    }
                }
            }
        });

        Button btnMove = new Button(controlBlackListNotFilmFilter ? "_Verschieben zu \"Filmliste laden\"" : "_Verschieben zu \"Blacklist\"");
        btnMove.setTooltip(new Tooltip("Damit werden die markierten Filter in den " +
                "anderen Filter (Filmfilter/Blacklist) verschoben"));
        btnMove.setOnAction(a -> {
            final ObservableList<BlackData> selected = tableView.getSelectionModel().getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                blackDataChanged.set(true);
                if (controlBlackListNotFilmFilter) {
                    //dann in den FilmListFilter verschieben
                    progData.filmListFilter.addAll(selected);
                } else {
                    //dann in die Blacklist verschieben
                    progData.blackList.addAll(selected);
                }
                list.removeAll(selected);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnLoad = new Button("_Filmliste mit diesen Einstellungen neu laden");
        btnLoad.setTooltip(new Tooltip("Eine komplette neue Filmliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Filmliste werden so sofort übernommen"));
        btnLoad.setOnAction(event -> {
            LoadFilmFactory.getInstance().loadNewListFromWeb(true);
        });

        HBox hBoxButton = new HBox(P2LibConst.DIST_BUTTON);
        if (!controlBlackListNotFilmFilter) {
            hBoxButton.getChildren().addAll(btnLoad);
        }
        hBoxButton.getChildren().addAll(P2GuiTools.getHBoxGrower(), btnCopy, btnMove, btnHelpCount);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        VBox vb = new VBox(P2LibConst.DIST_BUTTON);
        vb.getChildren().addAll(hBoxButton);
        vBox.getChildren().add(vb);
    }
}
