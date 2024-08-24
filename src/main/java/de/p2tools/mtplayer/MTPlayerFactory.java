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


package de.p2tools.mtplayer;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFactory;
import de.p2tools.mtplayer.gui.configdialog.ConfigDialogController;

public class MTPlayerFactory {
    private MTPlayerFactory() {
    }

//    public static void quitAndWait() {
//        ProgQuit.quit(true);
//    }

    public static void centerGui() {
        ProgData.getInstance().primaryStage.centerOnScreen();
    }

    public static void setFilter() {
        switch (MTPlayerController.paneShown) {
            case FILM:
                ProgConfig.FILM_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.FILM_GUI_FILTER_DIVIDER_ON.getValue());
                break;
            case LIVE_FILM:
                ProgConfig.LIVE_FILM_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.LIVE_FILM_GUI_FILTER_DIVIDER_ON.getValue());
                break;
            case DOWNLOAD:
                ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.getValue());
                break;
            case ABO:
                ProgConfig.ABO_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.ABO_GUI_FILTER_DIVIDER_ON.getValue());
                break;
        }
    }

    public static void setInfos() {
        switch (MTPlayerController.paneShown) {
            case FILM:
                ProgConfig.FILM_GUI_INFO_ON.setValue(!ProgConfig.FILM_GUI_INFO_ON.getValue());
                break;
            case LIVE_FILM:
                ProgConfig.LIVE_FILM_GUI_INFO_ON.setValue(!ProgConfig.LIVE_FILM_GUI_INFO_ON.getValue());
                break;
            case DOWNLOAD:
                ProgConfig.DOWNLOAD_GUI_INFO_ON.setValue(!ProgConfig.DOWNLOAD_GUI_INFO_ON.getValue());
                break;
            case ABO:
                ProgConfig.ABO_GUI_INFO_ON.setValue(!ProgConfig.ABO_GUI_INFO_ON.getValue());
                break;
        }
    }

    public static void showFilmInfos() {
        switch (MTPlayerController.paneShown) {
            case FILM:
                ProgData.getInstance().filmGuiController.showFilmInfo();
                break;
            case LIVE_FILM:
                ProgData.getInstance().liveFilmGuiController.showFilmInfo();
                break;
            case DOWNLOAD:
                ProgData.getInstance().downloadGuiController.showFilmInfo();
                break;
            case ABO:
                break;
        }
    }

    public static void copyTheme() {
        switch (MTPlayerController.paneShown) {
            case FILM:
                ProgData.getInstance().filmGuiController.copyFilmThemeTitle(true);
                break;
            case LIVE_FILM:
                ProgData.getInstance().liveFilmGuiController.copyFilmThemeTitle(true);
                break;
            case DOWNLOAD:
                ProgData.getInstance().downloadGuiController.copyFilmThemeTitle(true);
                break;
            case ABO:
                break;
        }
    }

    public static void copyTitle() {
        switch (MTPlayerController.paneShown) {
            case FILM:
                ProgData.getInstance().filmGuiController.copyFilmThemeTitle(false);
                break;
            case LIVE_FILM:
                ProgData.getInstance().liveFilmGuiController.copyFilmThemeTitle(false);
                break;
            case DOWNLOAD:
                ProgData.getInstance().downloadGuiController.copyFilmThemeTitle(false);
                break;
            case ABO:
                break;
        }
    }

    public static void addBlacklist() {
        switch (MTPlayerController.paneShown) {
            case FILM:
                BlacklistFactory.addBlackFilm(true);
                break;
            case LIVE_FILM:
                break;
            case DOWNLOAD:
                BlacklistFactory.addBlackFilm(false);
                break;
            case ABO:
                break;
        }
    }

    public static void addBlacklistTheme() {
        switch (MTPlayerController.paneShown) {
            case FILM:
                BlacklistFactory.addBlackThemeFilm();
                break;
            case LIVE_FILM:
                break;
            case DOWNLOAD:
                BlacklistFactory.addBlackThemeDownload();
                break;
            case ABO:
                break;
        }
    }

    public static void showBlacklist() {
        new ConfigDialogController(ProgData.getInstance(), true);
    }

    public static void setMediaCollection() {
        switch (MTPlayerController.paneShown) {
            case FILM:
                ProgData.getInstance().filmGuiController.searchFilmInMediaCollection();
                break;
            case LIVE_FILM:
                ProgData.getInstance().liveFilmGuiController.searchFilmInMediaCollection();
                break;
            case DOWNLOAD:
                ProgData.getInstance().downloadGuiController.searchFilmInMediaCollection();
                break;
            case ABO:
                break;
        }
    }

    public static void undoDels() {
        switch (MTPlayerController.paneShown) {
            case FILM:
            case LIVE_FILM:
                break;
            case DOWNLOAD:
                ProgData.getInstance().downloadList.undoDownloads();
                break;
            case ABO:
                ProgData.getInstance().aboList.undoAbos();
                break;
        }
    }
}
