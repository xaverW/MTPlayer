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

package de.p2tools.mtplayer.gui.infoPane;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.setdata.SetDataList;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.guitools.pclosepane.PClosePaneH;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilmInfoController extends PClosePaneH {

    private PaneFilmInfo paneFilmInfo;
    private final ProgData progData;
    private final TabPane tabPane = new TabPane();

    public FilmInfoController() {
        super(ProgConfig.FILM_GUI_DIVIDER_ON, true, true);
        progData = ProgData.getInstance();

        initInfoPane();
        setInfoTabPane();
    }

    public void setFilmInfos(FilmDataMTP film) {
        paneFilmInfo.setFilm(film);
    }

    private void initInfoPane() {
        paneFilmInfo = new PaneFilmInfo();
        super.getRipProperty().addListener((u, o, n) -> {
            if (tabPane.getTabs().isEmpty() && !getVBoxAll().getChildren().isEmpty()) {
                if (getVBoxAll().getChildren().get(0).equals(paneFilmInfo)) {
                    dialogInfo();
                } else {
                    dialogButton();
                }

            } else {
                Tab sel = tabPane.getSelectionModel().getSelectedItem();
                if (sel.getContent().equals(paneFilmInfo)) {
                    // dann filmInfo in den Dialog
                    dialogInfo();
                } else {
                    // dann die buttonInfo
                    dialogButton();
                }
            }
            setInfoTabPane();
        });

        if (ProgConfig.FILM_PANE_DIALOG_INFO_ON.getValue()) {
            dialogInfo();
        }
        if (ProgConfig.FILM_PANE_DIALOG_BUTTON_ON.getValue()) {
            dialogButton();
        }

        ProgConfig.FILM_PANE_DIALOG_INFO_ON.addListener((u, o, n) -> setInfoTabPane());
        ProgConfig.FILM_PANE_DIALOG_BUTTON_ON.addListener((u, o, n) -> setInfoTabPane());
        progData.setDataList.listChangedProperty().addListener((observable, oldValue, newValue) -> setInfoTabPane());
    }

    private void dialogInfo() {
        new InfoPaneDialog(paneFilmInfo, "Filminfos",
                ProgConfig.FILM_PANE_DIALOG_INFO_SIZE, ProgConfig.FILM_PANE_DIALOG_INFO_ON,
                ProgConfig.FILM_GUI_DIVIDER_ON, ProgData.FILM_TAB_ON);
    }

    private void dialogButton() {
        new InfoPaneDialog(PaneFilmButton.getButtonPane(), "Startbutton",
                ProgConfig.FILM_PANE_DIALOG_BUTTON_SIZE, ProgConfig.FILM_PANE_DIALOG_BUTTON_ON,
                ProgConfig.FILM_GUI_DIVIDER_ON, ProgData.FILM_TAB_ON);
    }

    private void setInfoTabPane() {
        tabPane.getTabs().clear();
        getVBoxAll().getChildren().clear();
        int count = 0;
        if (!ProgConfig.FILM_PANE_DIALOG_INFO_ON.getValue()) {
            ++count;
        }

        final SetDataList setDataList = progData.setDataList.getSetDataListButton();
        if (!setDataList.isEmpty() && !ProgConfig.FILM_PANE_DIALOG_BUTTON_ON.getValue()) {
            ++count;
        }

        if (count == 0) {
            // dann gibts nix zu sehen und dann das InfoPane ausblenden
            ProgConfig.FILM_GUI_DIVIDER_ON.set(false);

        } else if (count == 1) {
            // dann kein Tab
            if (!ProgConfig.FILM_PANE_DIALOG_INFO_ON.getValue()) {
                getVBoxAll().getChildren().setAll(paneFilmInfo);
                VBox.setVgrow(paneFilmInfo, Priority.ALWAYS);
                return;

            } else {
                getVBoxAll().getChildren().setAll(PaneFilmButton.getButtonPane());
                VBox.setVgrow(paneFilmInfo, Priority.ALWAYS);
                return;
            }

        } else {
            // dann werden beide angezeigt
            Tab filmInfoTab = new Tab("Beschreibung");
            filmInfoTab.setClosable(false);
            filmInfoTab.setContent(paneFilmInfo);

            Tab buttonTab = new Tab("Startbutton");
            buttonTab.setClosable(false);
            buttonTab.setContent(PaneFilmButton.getButtonPane());

            tabPane.getTabs().addAll(filmInfoTab, buttonTab);
            getVBoxAll().getChildren().setAll(tabPane);
            VBox.setVgrow(tabPane, Priority.ALWAYS);
        }
    }
}
