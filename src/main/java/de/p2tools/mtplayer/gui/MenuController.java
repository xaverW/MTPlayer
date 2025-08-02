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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MenuController extends ScrollPane {

    public enum StartupMode {
        Film, AUDIO, LIVE_FILM, DOWNLOAD, ABO
    }

    public MenuController(StartupMode sm) {
        VBox vb = new VBox();

        setMinWidth(Region.USE_PREF_SIZE);
        setFitToHeight(true);
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.NEVER);
        setContent(vb);

        vb.setPadding(new Insets(5));
        vb.setSpacing(15);
        vb.setAlignment(Pos.TOP_CENTER);

        switch (sm) {
            case Film:
                new FilmMenu(vb).init();
                visibleProperty().bind(ProgConfig.FILM_GUI_SHOW_MENU);
                managedProperty().bind(ProgConfig.FILM_GUI_SHOW_MENU);
                setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        ProgConfig.FILM_GUI_SHOW_MENU.set(!ProgConfig.FILM_GUI_SHOW_MENU.get());
                    }
                });
                break;
            case AUDIO:
                new AudioMenu(vb).init();
                visibleProperty().bind(ProgConfig.AUDIO_GUI_SHOW_MENU);
                managedProperty().bind(ProgConfig.AUDIO_GUI_SHOW_MENU);
                setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        ProgConfig.AUDIO_GUI_SHOW_MENU.set(!ProgConfig.AUDIO_GUI_SHOW_MENU.get());
                    }
                });
                break;
            case LIVE_FILM:
                new LiveFilmMenu(vb).init();
                visibleProperty().bind(ProgConfig.LIVE_FILM_GUI_SHOW_MENU);
                managedProperty().bind(ProgConfig.LIVE_FILM_GUI_SHOW_MENU);
                setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        ProgConfig.LIVE_FILM_GUI_SHOW_MENU.set(!ProgConfig.LIVE_FILM_GUI_SHOW_MENU.get());
                    }
                });
                break;
            case DOWNLOAD:
                new DownloadMenu(vb).init();
                visibleProperty().bind(ProgConfig.DOWNLOAD_GUI_SHOW_MENU);
                managedProperty().bind(ProgConfig.DOWNLOAD_GUI_SHOW_MENU);
                setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        ProgConfig.DOWNLOAD_GUI_SHOW_MENU.set(!ProgConfig.DOWNLOAD_GUI_SHOW_MENU.get());
                    }
                });
                break;
            case ABO:
                new AboMenu(vb).init();
                visibleProperty().bind(ProgConfig.ABO_GUI_SHOW_MENU);
                managedProperty().bind(ProgConfig.ABO_GUI_SHOW_MENU);
                setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        ProgConfig.ABO_GUI_SHOW_MENU.set(!ProgConfig.ABO_GUI_SHOW_MENU.get());
                    }
                });
                break;
        }
    }
}
