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
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.guitools.pclosepane.PClosePaneH;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilmInfoController extends PClosePaneH {

    private PaneFilmInfo paneFilmInfo;
    private PaneFilmButton paneButton;
    private PaneMedia paneMedia;
    private Tab tabFilmInfo;
    private Tab tabButton;
    private Tab tabMedia;
    private final TabPane tabPane = new TabPane();

    private final ProgData progData;

    public FilmInfoController() {
        super(ProgConfig.FILM_GUI_DIVIDER_ON, true, true);
        progData = ProgData.getInstance();
        initInfoPane();
    }

    public void setFilmInfos(FilmDataMTP film) {
        paneFilmInfo.setFilm(film);
        if (paneIsVisible(paneMedia, ProgConfig.FILM_PANE_DIALOG_MEDIA_ON)) {
            paneMedia.setSearchPredicate(film);
        }
    }

    private boolean paneIsVisible(Pane pane, BooleanProperty booleanProperty) {
        if (booleanProperty.getValue()) {
            // dann im Extrafenster
            return true;
        } else if (!ProgConfig.FILM_GUI_DIVIDER_ON.getValue()) {
            // dann wird gar nix angezeigt
            return false;
        } else if (!getVBoxAll().getChildren().isEmpty() &&
                getVBoxAll().getChildren().get(0).equals(pane)) {
            // dann wird nur das angezeigt
            return true;
        } else if (tabPane.getSelectionModel().getSelectedItem() != null &&
                tabPane.getSelectionModel().getSelectedItem().getContent().equals(pane)) {
            // dann ist der Tab ausgewählt
            return true;
        } else {
            return false;
        }
    }

    private void initInfoPane() {
        paneFilmInfo = new PaneFilmInfo(ProgConfig.FILM_GUI_INFO_DIVIDER);
        paneButton = new PaneFilmButton();
        paneMedia = new PaneMedia();
        tabFilmInfo = new Tab("Beschreibung");
        tabFilmInfo.setClosable(false);
        tabButton = new Tab("Startbutton");
        tabButton.setClosable(false);
        tabMedia = new Tab("Mediensammlung");
        tabMedia.setClosable(false);

        super.getRipProperty().addListener((u, o, n) -> {
            if (tabFilmInfo.isSelected()) {
                setDialogInfo();
            } else if (tabButton.isSelected()) {
                setDialogButton();
            } else {
                setDialogMedia();
            }
        });

        if (ProgConfig.FILM_PANE_DIALOG_INFO_ON.getValue()) {
            setDialogInfo();
        }
        if (ProgConfig.FILM_PANE_DIALOG_BUTTON_ON.getValue()) {
            setDialogButton();
        }
        if (ProgConfig.FILM_PANE_DIALOG_MEDIA_ON.getValue()) {
            setDialogMedia();
        }
        ProgConfig.FILM_PANE_DIALOG_INFO_ON.addListener((u, o, n) -> setTabs()); // kommt beim Ein- und Ausschalten der Fenster
        ProgConfig.FILM_PANE_DIALOG_BUTTON_ON.addListener((u, o, n) -> setTabs());
        ProgConfig.FILM_PANE_DIALOG_MEDIA_ON.addListener((u, o, n) -> setTabs());
        progData.setDataList.listChangedProperty().addListener((observable, oldValue, newValue) -> setTabs());

        setTabs();
    }

    private void setDialogInfo() {
        tabFilmInfo.setContent(null);
        new InfoPaneDialog(paneFilmInfo, "Filminfos",
                ProgConfig.FILM_PANE_DIALOG_INFO_SIZE, ProgConfig.FILM_PANE_DIALOG_INFO_ON,
                ProgConfig.FILM_GUI_DIVIDER_ON, ProgData.FILM_TAB_ON);
    }

    private void setDialogButton() {
        tabButton.setContent(null);
        new InfoPaneDialog(paneButton, "Startbutton",
                ProgConfig.FILM_PANE_DIALOG_BUTTON_SIZE, ProgConfig.FILM_PANE_DIALOG_BUTTON_ON,
                ProgConfig.FILM_GUI_DIVIDER_ON, ProgData.FILM_TAB_ON);
    }

    private void setDialogMedia() {
        tabMedia.setContent(null);
        new InfoPaneDialog(paneMedia, "Mediensammlung",
                ProgConfig.FILM_PANE_DIALOG_MEDIA_SIZE, ProgConfig.FILM_PANE_DIALOG_MEDIA_ON,
                ProgConfig.FILM_GUI_DIVIDER_ON, ProgData.FILM_TAB_ON);
    }

    private void setTabs() {
        int i = 0;

        if (ProgConfig.FILM_PANE_DIALOG_INFO_ON.getValue()) {
            tabPane.getTabs().remove(tabFilmInfo);
        } else {
            tabFilmInfo.setContent(paneFilmInfo);
            if (!tabPane.getTabs().contains(tabFilmInfo)) {
                tabPane.getTabs().add(i, tabFilmInfo);
            }
            ++i;
        }

        if (ProgConfig.FILM_PANE_DIALOG_BUTTON_ON.getValue()) {
            tabPane.getTabs().remove(tabButton);
        } else {
            if (progData.setDataList.getSetDataListButton().size() <= 0) {
                // dann gibts keine Button
                tabPane.getTabs().remove(tabButton);
            } else {
                tabButton.setContent(paneButton);
                if (!tabPane.getTabs().contains(tabButton)) {
                    tabPane.getTabs().add(i, tabButton);
                }
                ++i;
            }
        }

        if (ProgConfig.FILM_PANE_DIALOG_MEDIA_ON.getValue()) {
            tabPane.getTabs().remove(tabMedia);
        } else {
            tabMedia.setContent(paneMedia);
            if (!tabPane.getTabs().contains(tabMedia)) {
                tabPane.getTabs().add(i, tabMedia);
            }
            ++i;
        }

        if (i == 0) {
            getVBoxAll().getChildren().clear();
            ProgConfig.FILM_GUI_DIVIDER_ON.set(false);
        } else if (i == 1) {
            // dann gibts einen Tab
            final Node node = tabPane.getTabs().get(0).getContent();
            tabPane.getTabs().remove(0);
            getVBoxAll().getChildren().setAll(node);
            VBox.setVgrow(node, Priority.ALWAYS);
        } else {
            // dann gibts mehre Tabs
            getVBoxAll().getChildren().setAll(tabPane);
            VBox.setVgrow(tabPane, Priority.ALWAYS);
        }
    }
}
