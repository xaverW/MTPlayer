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

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ProgIcons;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.VBox;

public class FilmMenu {
    final private VBox vbox;
    final private ProgData progData;
    private static final String FILM_PLAY_TEXT = "Film abspielen";
    private static final String FILM_RECORD_TEXT = "Film aufzeichnen";
    BooleanProperty boolFilterOn = ProgConfig.FILM_GUI_FILTER_DIVIDER_ON.getBooleanProperty();
    BooleanProperty boolInfoOn = ProgConfig.FILM_GUI_DIVIDER_ON.getBooleanProperty();

    public FilmMenu(VBox vbox) {
        this.vbox = vbox;
        progData = ProgData.getInstance();
    }


    public void init() {
        vbox.getChildren().clear();

        initFilmMenu();
        initButton();
    }

    private void initButton() {
        // Button
        final ToolBarButton btPlay =
                new ToolBarButton(vbox, "Abspielen", FILM_PLAY_TEXT, new ProgIcons().FX_ICON_TOOLBAR_FILM_START);

        final ToolBarButton btSave =
                new ToolBarButton(vbox, "Speichern", FILM_RECORD_TEXT, new ProgIcons().FX_ICON_TOOLBAR_FILM_REC);

        btPlay.setOnAction(a -> progData.filmGuiController.playFilmUrl());
        btSave.setOnAction(a -> progData.filmGuiController.saveTheFilm());
    }

    private void initFilmMenu() {
        final MenuButton mb = new MenuButton("");
        mb.setGraphic(new ProgIcons().FX_ICON_TOOLBAR_MENU);
        mb.getStyleClass().add("btnFunction");

        final MenuItem mbPlay = new MenuItem("Film abspielen");
        mbPlay.setOnAction(a -> progData.filmGuiController.playFilmUrl());

        final MenuItem mbSave = new MenuItem("Film aufzeichnen");
        mbSave.setOnAction(e -> progData.filmGuiController.saveTheFilm());

        final MenuItem miFilmShown = new MenuItem("Filme als gesehen markieren");
        miFilmShown.setOnAction(a -> progData.filmGuiController.setFilmShown());

        final MenuItem miFilmNotShown = new MenuItem("Filme als ungesehen markieren");
        miFilmNotShown.setOnAction(a -> progData.filmGuiController.setFilmNotShown());

        final MenuItem miFilmMediaCollection = new MenuItem("Titel in der Mediensammlung suchen");
        miFilmMediaCollection.setOnAction(a -> progData.filmGuiController.guiFilmMediaCollection());

        final CheckMenuItem miShowFilter = new CheckMenuItem("Filter anzeigen");
        miShowFilter.selectedProperty().bindBidirectional(boolFilterOn);

        final CheckMenuItem miShowInfo = new CheckMenuItem("Filminfos anzeigen");
        miShowInfo.selectedProperty().bindBidirectional(boolInfoOn);

        mb.getItems().addAll(mbPlay, mbSave);
        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miFilmShown, miFilmNotShown, miFilmMediaCollection);
        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);

        vbox.getChildren().add(mb);
    }
}
