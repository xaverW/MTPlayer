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
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;


public class FilmMenu {
    final private VBox vBox;
    final private ProgData progData;
    private SelectedFilter selectedFilter = null;
    ChangeListener changeListener;
    private static final String FILM_PLAY_TEXT = "Film abspielen";
    private static final String FILM_RECORD_TEXT = "Filme aufzeichnen";
    private static final String FILM_BOOKMARK_TEXT = "Bookmark für die Filme anlegen oder löschen";
    private static final String FILM_FILTER_BOOKMARK_TEXT = "angelegte Bookmarks anzeigen";
    private static final String FILM_DEL_BOOKMARK_TEXT = "alle angelegten Bookmarks löschen";
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
        final ToolBarButton btPlay =
                new ToolBarButton(vBox, "Abspielen", FILM_PLAY_TEXT, new ProgIcons().FX_ICON_TOOLBAR_FILM_START);
        final ToolBarButton btSave =
                new ToolBarButton(vBox, "Speichern", FILM_RECORD_TEXT, new ProgIcons().FX_ICON_TOOLBAR_FILM_REC);

        VBox vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btBookmark =
                new ToolBarButton(vBox, "Bookmark", FILM_BOOKMARK_TEXT, new ProgIcons().FX_ICON_TOOLBAR_FILM_BOOKMARK);
        final ToolBarButton btFilter =
                new ToolBarButton(vBox, "Bookmarks anzeigen", FILM_FILTER_BOOKMARK_TEXT, new ProgIcons().FX_ICON_TOOLBAR_FILM_BOOKMARK_FILTER);
        final ToolBarButton btDelBookmark =
                new ToolBarButton(vBox, "Bookmark löschen", FILM_DEL_BOOKMARK_TEXT, new ProgIcons().FX_ICON_TOOLBAR_FILM_DEL_BOOKMARK);

        btPlay.setOnAction(a -> progData.filmGuiController.playFilmUrl());
        btSave.setOnAction(a -> progData.filmGuiController.saveTheFilm());

        btBookmark.setOnAction(a -> progData.filmGuiController.bookmarkFilm());
        btDelBookmark.setOnAction(a -> progData.bookmarks.clearAll(progData.primaryStage));

        changeListener = (observable, oldValue, newValue) -> selectedFilter = null;
        btFilter.setOnAction(a -> {
            progData.storedFilter.filterChangeProperty().removeListener(changeListener);

            if (selectedFilter == null) {
                selectedFilter = SelectedFilter.getFilterCopy(progData.storedFilter.getSelectedFilter());

                progData.storedFilter.loadStoredFilter(BookmarkFilter.getBookmarkFilter(selectedFilter));
                progData.storedFilter.filterChangeProperty().addListener(changeListener);

            } else {
                progData.storedFilter.loadStoredFilter(selectedFilter);
                selectedFilter = null;
            }
        });

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


        // Bookmarks
        Menu submenuBookmark = new Menu("Bookmarks");
        final MenuItem miBookmarkAdd = new MenuItem("neue Bookmarks anlegen");
        final MenuItem miBookmarkDel = new MenuItem("Bookmarks löschen");
        final MenuItem miBookmarkDelAll = new MenuItem("alle angelegten Bookmarks löschen");

        miBookmarkAdd.setOnAction(a -> progData.filmGuiController.bookmarkFilm(true));
        miBookmarkDel.setOnAction(a -> progData.filmGuiController.bookmarkFilm(false));
        miBookmarkDelAll.setOnAction(a -> progData.bookmarks.clearAll(progData.primaryStage));
        submenuBookmark.getItems().addAll(miBookmarkAdd, miBookmarkDel, miBookmarkDelAll);


        final CheckMenuItem miShowFilter = new CheckMenuItem("Filter anzeigen");
        miShowFilter.selectedProperty().bindBidirectional(boolFilterOn);

        final CheckMenuItem miShowInfo = new CheckMenuItem("Infos anzeigen");
        miShowInfo.selectedProperty().bindBidirectional(boolInfoOn);

        mb.getItems().addAll(mbPlay, mbSave);
        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miFilmShown, miFilmNotShown, miFilmMediaCollection);
        mb.getItems().add(submenuBookmark);

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);

        vBox.getChildren().add(mb);
    }
}
