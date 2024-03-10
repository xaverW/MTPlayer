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

import de.p2tools.mtplayer.MTPlayerController;
import de.p2tools.mtplayer.MTPlayerFactory;
import de.p2tools.mtplayer.controller.config.PShortKeyFactory;
import de.p2tools.mtplayer.controller.config.PShortcut;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.film.FilmSaveFactory;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.tools.shortcut.PShortcutWorker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

import java.util.Optional;


public class LiveFilmMenu {
    final private VBox vBox;
    final private ProgData progData;
    private FilmFilter storedActFilterSettings = null;
    private static final String FILM_FILTER_BOOKMARK_TEXT = "Alle angelegte Bookmarks anzeigen\n" +
            "der zweite Klick stellt den\n" +
            "eingestellten Filter wieder her";

    public LiveFilmMenu(VBox vBox) {
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
                "Abspielen", "Markierten Film abspielen", ProgIcons.ICON_TOOLBAR_FILM_START.getImageView());
        final ToolBarButton btPlayAll = new ToolBarButton(vBox,
                "Alle Abspielen", "Alle Markierten Filme abspielen", ProgIcons.ICON_TOOLBAR_FILM_ALL_START.getImageView());
        final ToolBarButton btSave = new ToolBarButton(vBox,
                "Speichern", "Markierte Filme speichern", ProgIcons.ICON_TOOLBAR_FILM_REC.getImageView());

        btPlay.setOnAction(a -> {
            final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().liveFilmGuiController.getSel(true, true);
            if (filmSelection.isPresent()) {
                FilmPlayFactory.playFilm(filmSelection.get());
                progData.filmGuiController.tableView.refresh();
                progData.filmGuiController.tableView.requestFocus();

            }
        });
        btPlayAll.setOnAction(a -> {
            FilmPlayFactory.playFilmList(ProgData.getInstance().liveFilmGuiController.getSelList(true));
            progData.filmGuiController.tableView.refresh();
            progData.filmGuiController.tableView.requestFocus();
        });
        btSave.setOnAction(a -> {
            FilmSaveFactory.saveFilmList();
            progData.filmGuiController.tableView.refresh();
            progData.filmGuiController.tableView.requestFocus();
        });

        vBox.getChildren().add(P2GuiTools.getVBoxGrower());
        final ToolBarButton btLiveFilm = new ToolBarButton(vBox,
                "Live-Suche", "Live-Suche in der ARD-Mediathek", ProgIcons.ICON_TOOLBAR_PROPOSE.getImageView());
        btLiveFilm.setOnAction(a -> progData.mtPlayerController.changeLiveFilm());
    }

    private void initFilmMenu() {
        final MenuButton mb = new MenuButton("");
        mb.setTooltip(new Tooltip("Filmmenü anzeigen"));
        mb.setGraphic(ProgIcons.ICON_TOOLBAR_MENU.getImageView());
        mb.getStyleClass().addAll("btnFunction", "btnFunc-5");

        final MenuItem mbPlay = new MenuItem("Film abspielen");
        mbPlay.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().liveFilmGuiController.getSel(true, true);
            filmSelection.ifPresent(FilmPlayFactory::playFilm);
        });
        PShortcutWorker.addShortCut(mbPlay, PShortcut.SHORTCUT_PLAY_FILM);

        final MenuItem mbPlayAll = new MenuItem("Alle markierten Film abspielen");
        mbPlayAll.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            FilmPlayFactory.playFilmList(ProgData.getInstance().liveFilmGuiController.getSelList(true));
        });
        PShortcutWorker.addShortCut(mbPlayAll, PShortcut.SHORTCUT_PLAY_FILM_ALL);

        final MenuItem mbSave = new MenuItem("Film speichern");
        mbSave.setOnAction(e -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            FilmSaveFactory.saveFilmList();
        });
        PShortcutWorker.addShortCut(mbSave, PShortcut.SHORTCUT_SAVE_FILM);

        mb.getItems().addAll(mbPlay, mbPlayAll, mbSave);

        final MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_INFO_FILM.getActShortcut());
        miFilmInfo.setOnAction(a -> {
            progData.filmGuiController.showFilmInfo();
        });

        final MenuItem miFilmMediaCollection = new MenuItem("Film in der Mediensammlung suchen" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION.getActShortcut());
        miFilmMediaCollection.setOnAction(a -> {
            progData.filmGuiController.searchFilmInMediaCollection();
        });

        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD.getActShortcut());
        miCopyTheme.setOnAction(a -> progData.filmGuiController.copyFilmThemeTitle(true));

        final MenuItem miCopyTitle = new MenuItem("Titel in die Zwischenablage kopieren" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD.getActShortcut());
        miCopyTitle.setOnAction(a -> progData.filmGuiController.copyFilmThemeTitle(false));

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miFilmInfo,
                miFilmMediaCollection, miCopyTheme, miCopyTitle);

        final MenuItem miShowFilter = new MenuItem("Filter ein-/ausblenden" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SHOW_FILTER.getActShortcut());
        miShowFilter.setOnAction(a -> MTPlayerFactory.setFilter());

        final MenuItem miShowInfo = new MenuItem("Infos ein-/ausblenden" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SHOW_INFOS.getActShortcut());
        miShowInfo.setOnAction(a -> MTPlayerFactory.setInfos());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);
        vBox.getChildren().add(mb);
    }
}
