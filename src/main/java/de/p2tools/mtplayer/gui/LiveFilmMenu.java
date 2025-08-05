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
import de.p2tools.mtplayer.controller.config.*;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.data.film.FilmSaveFactory;
import de.p2tools.mtplayer.controller.filter.FilmFilter;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.tools.shortcut.P2ShortcutWorker;
import javafx.scene.control.*;
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
                "Abspielen", "Markierten Film abspielen", ProgIcons.ICON_TOOLBAR_START.getImageView());
        final ToolBarButton btPlayAll = new ToolBarButton(vBox,
                "Alle Abspielen", "Alle markierten Filme abspielen", ProgIcons.ICON_TOOLBAR_START_ALL.getImageView());
        final ToolBarButton btSave = new ToolBarButton(vBox,
                "Speichern", "Markierte Filme speichern", ProgIcons.ICON_TOOLBAR_REC.getImageView());

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
            FilmSaveFactory.saveFilmList(ProgData.getInstance().liveFilmGuiController.getSelList(true), null);
            progData.filmGuiController.tableView.refresh();
            progData.filmGuiController.tableView.requestFocus();
        });

        vBox.getChildren().add(P2GuiTools.getVBoxGrower());
        final ToolBarButton btLiveFilm = new ToolBarButton(vBox,
                "Live-Suche", "Live-Suche in der ARD/ZDF-Mediathek", ProgIcons.ICON_TOOLBAR_LIVE.getImageView());
        btLiveFilm.setOnAction(a -> ProgConfig.LIVE_FILM_IS_VISIBLE.set(!ProgConfig.LIVE_FILM_IS_VISIBLE.get()));
        vBox.getChildren().add(P2GuiTools.getVDistance(10));
    }

    private void initFilmMenu() {
        final MenuButton mb = new MenuButton("");
        mb.setTooltip(new Tooltip("Live-Filmmenü anzeigen"));
        mb.setGraphic(ProgIcons.ICON_TOOLBAR_MENU.getImageView());
        mb.getStyleClass().addAll("btnFunction", "btnFunc-0");

        final MenuItem mbPlay = new MenuItem("Film abspielen");
        mbPlay.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().liveFilmGuiController.getSel(true, true);
            filmSelection.ifPresent(FilmPlayFactory::playFilm);
        });
        P2ShortcutWorker.addShortCut(mbPlay, PShortcut.SHORTCUT_PLAY_FILM);

        final MenuItem mbPlayAll = new MenuItem("Alle markierten Film abspielen");
        mbPlayAll.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            FilmPlayFactory.playFilmList(ProgData.getInstance().liveFilmGuiController.getSelList(true));
        });
        P2ShortcutWorker.addShortCut(mbPlayAll, PShortcut.SHORTCUT_PLAY_FILM_ALL);

        final MenuItem mbSave = new MenuItem("Film speichern");
        mbSave.setOnAction(e -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.FILM) {
                return;
            }
            FilmSaveFactory.saveFilmList();
        });
        P2ShortcutWorker.addShortCut(mbSave, PShortcut.SHORTCUT_SAVE_FILM);
        mb.getItems().addAll(mbPlay, mbPlayAll, mbSave);

        final MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_INFO_FILM.getActShortcut());
        miFilmInfo.setOnAction(a -> {
            progData.filmGuiController.showFilmInfo();
        });
        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miFilmInfo, copyInfos(progData));


        final MenuItem miFilmMediaCollection = new MenuItem("Film in der Mediensammlung suchen" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION.getActShortcut());
        miFilmMediaCollection.setOnAction(a -> {
            progData.filmGuiController.searchFilmInMediaCollection();
        });
        mb.getItems().addAll(miFilmMediaCollection);


        final MenuItem miShowFilter = new MenuItem("Filter ein-/ausblenden" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SHOW_FILTER.getActShortcut());
        miShowFilter.disableProperty().bind(ProgConfig.LIVE_FILM__FILTER_IS_RIP);
        miShowFilter.setOnAction(a -> MTPlayerFactory.setFilter());

        final MenuItem miShowInfo = new MenuItem("Infos ein-/ausblenden" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SHOW_INFOS.getActShortcut());
        miShowInfo.disableProperty().bind(ProgConfig.LIVE_FILM__INFO_PANE_IS_RIP
                .and(ProgConfig.LIVE_FILM__BUTTON_PANE_IS_RIP)
                .and(ProgConfig.LIVE_FILM__MEDIA_PANE_IS_RIP));
        miShowInfo.setOnAction(a -> MTPlayerFactory.setInfos());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);
        vBox.getChildren().add(mb);
    }

    public static Menu copyInfos(ProgData progData) {
        final Menu subMenuURL = new Menu("Film-Infos kopieren");

        final MenuItem miCopyTheme = new MenuItem("Thema");
        miCopyTheme.setOnAction(a -> {
            progData.liveFilmGuiController.copyFilmThemeTitle(true);
        });

        final MenuItem miCopyName = new MenuItem("Titel");
        miCopyName.setOnAction(a -> {
            progData.liveFilmGuiController.copyFilmThemeTitle(false);
        });

        final MenuItem miCopyWeb = new MenuItem("Website-URL");
        miCopyWeb.setOnAction(a -> {
            progData.liveFilmGuiController.copyWebsite();
        });

        final MenuItem miCopyHd = new MenuItem("URL in HD-Auflösung");
        miCopyHd.setOnAction(a -> {
            progData.liveFilmGuiController.copyUrl(FilmData.RESOLUTION_HD);
        });

        final MenuItem miCopyUrl = new MenuItem("URL in hoher Auflösung");
        miCopyUrl.setOnAction(a -> {
            progData.liveFilmGuiController.copyUrl(FilmData.RESOLUTION_NORMAL);
        });

        final MenuItem miCopyLow = new MenuItem("URL in kleiner Auflösung");
        miCopyLow.setOnAction(a -> {
            progData.liveFilmGuiController.copyUrl(FilmData.RESOLUTION_SMALL);
        });

        subMenuURL.getItems().addAll(miCopyTheme, miCopyName, miCopyWeb);
        subMenuURL.getItems().add(new SeparatorMenuItem());
        subMenuURL.getItems().addAll(miCopyHd, miCopyUrl, miCopyLow);

        return subMenuURL;
    }
}
