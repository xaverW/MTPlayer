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
import de.mtplayer.mtp.tools.storedFilter.BookmarkFilter;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilterFactory;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;


public class FilmMenu {
    final private VBox vBox;
    final private ProgData progData;
    private SelectedFilter storedActFilterSettings = null;
    private SelectedFilter storedBookmarkFilter = null;
    private static final String FILM_FILTER_BOOKMARK_TEXT = "alle angelegte Bookmarks anzeigen\n\n" +
            "der zweite Klick stellt den\n" +
            "eingestellten Filter wieder her";

    BooleanProperty boolFilterOn = ProgConfig.FILM_GUI_FILTER_DIVIDER_ON.getBooleanProperty();
    BooleanProperty boolInfoOn = ProgConfig.FILM_GUI_DIVIDER_ON.getBooleanProperty();

    public FilmMenu(VBox vBox) {
        this.vBox = vBox;
        progData = ProgData.getInstance();
    }


    public void init() {
        vBox.getChildren().clear();

        initFilmMenu();
        initButton();
    }

    private void initButton() {
        // Button
        VBox vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(0);
        vBoxSpace.setMinHeight(0);
        vBox.getChildren().add(vBoxSpace);


        final ToolBarButton btPlay = new ToolBarButton(vBox,
                "Abspielen", "markierten Film abspielen", new ProgIcons().FX_ICON_TOOLBAR_FILM_START);
        final ToolBarButton btSave = new ToolBarButton(vBox,
                "Speichern", "markierte Filme speichern", new ProgIcons().FX_ICON_TOOLBAR_FILM_REC);

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btBookmark = new ToolBarButton(vBox,
                "Bookmarks anlegen", "Bookmarks für die markierten Filme anlegen", new ProgIcons().FX_ICON_TOOLBAR_FILM_BOOKMARK);
        final ToolBarButton btDelBookmark = new ToolBarButton(vBox,
                "Bookmarks löschen", "Bookmarks für die markierten Filme löschen", new ProgIcons().FX_ICON_TOOLBAR_FILM_DEL_BOOKMARK);
        final ToolBarButton btDelAllBookmark = new ToolBarButton(vBox,
                "alle Bookmarks löschen", "alle angelegten Bookmarks löschen", new ProgIcons().FX_ICON_TOOLBAR_FILM_DEL_ALL_BOOKMARK);
        final ToolBarButton btFilterBookmakr = new ToolBarButton(vBox,
                "Bookmarks anzeigen", FILM_FILTER_BOOKMARK_TEXT, new ProgIcons().FX_ICON_TOOLBAR_FILM_BOOKMARK_FILTER);

        btPlay.setOnAction(a -> progData.filmGuiController.playFilmUrl());
        btSave.setOnAction(a -> progData.filmGuiController.saveTheFilm());

        btBookmark.setOnAction(a -> progData.filmGuiController.bookmarkFilm(true));
        btDelBookmark.setOnAction(a -> progData.filmGuiController.bookmarkFilm(false));
        btDelAllBookmark.setOnAction(a -> progData.bookmarks.clearAll(progData.primaryStage));

        btFilterBookmakr.setOnAction(a -> {

            if (storedActFilterSettings != null && storedBookmarkFilter != null) {
                SelectedFilter sf = progData.storedFilters.getActFilterSettings();
                if (!SelectedFilterFactory.compareFilterWithoutNameOfFilter(storedBookmarkFilter, sf)) {
                    // dann hat sich der Filter geändert
                    storedActFilterSettings = null;
                }
            }

            if (storedActFilterSettings == null) {
                // dann setzen des Bookmarkfilters
                storedActFilterSettings = SelectedFilterFactory.getFilterCopy(progData.storedFilters.getActFilterSettings());

                storedBookmarkFilter = BookmarkFilter.getBookmarkFilter(storedActFilterSettings);
                progData.storedFilters.setActFilterSettings(storedBookmarkFilter);

            } else {
                // dann den gemerkten Filter wieder setzen
                progData.storedFilters.setActFilterSettings(storedActFilterSettings);
                storedActFilterSettings = null;
            }
        });

    }

    private void initFilmMenu() {
        final MenuButton mb = new MenuButton("");
        mb.setTooltip(new Tooltip("Filmmenü anzeigen"));
        mb.setGraphic(new ProgIcons().FX_ICON_TOOLBAR_MENU);
        mb.getStyleClass().add("btnFunction");

        final MenuItem mbPlay = new MenuItem("Film abspielen");
        mbPlay.setOnAction(a -> progData.filmGuiController.playFilmUrl());
        final MenuItem mbSave = new MenuItem("Film speichern");
        mbSave.setOnAction(e -> progData.filmGuiController.saveTheFilm());

        mb.getItems().addAll(mbPlay, mbSave);


        final MenuItem miFilmShown = new MenuItem("Filme als gesehen markieren");
        miFilmShown.setOnAction(a -> progData.filmGuiController.setFilmShown());
        final MenuItem miFilmNotShown = new MenuItem("Filme als ungesehen markieren");
        miFilmNotShown.setOnAction(a -> progData.filmGuiController.setFilmNotShown());
        final MenuItem miFilmMediaCollection = new MenuItem("Titel in der Mediensammlung suchen");
        miFilmMediaCollection.setOnAction(a -> progData.filmGuiController.guiFilmMediaCollection());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miFilmShown, miFilmNotShown, miFilmMediaCollection);


        // Bookmarks
        Menu submenuBookmark = new Menu("Bookmarks");
        final MenuItem miBookmarkAdd = new MenuItem("neue Bookmarks anlegen");
        miBookmarkAdd.setOnAction(a -> progData.filmGuiController.bookmarkFilm(true));
        final MenuItem miBookmarkDel = new MenuItem("Bookmarks löschen");
        miBookmarkDel.setOnAction(a -> progData.filmGuiController.bookmarkFilm(false));
        final MenuItem miBookmarkDelAll = new MenuItem("alle angelegten Bookmarks löschen");
        miBookmarkDelAll.setOnAction(a -> progData.bookmarks.clearAll(progData.primaryStage));

        submenuBookmark.getItems().addAll(miBookmarkAdd, miBookmarkDel, miBookmarkDelAll);
        mb.getItems().add(submenuBookmark);


        final CheckMenuItem miShowFilter = new CheckMenuItem("Filter anzeigen");
        miShowFilter.selectedProperty().bindBidirectional(boolFilterOn);
        final CheckMenuItem miShowInfo = new CheckMenuItem("Infos anzeigen");
        miShowInfo.selectedProperty().bindBidirectional(boolInfoOn);

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);

        vBox.getChildren().add(mb);
    }
}
