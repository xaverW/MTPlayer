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
import de.p2tools.p2lib.guitools.P2WindowIcon;

public class MTPlayerFactory {
    private MTPlayerFactory() {
    }

    public static String getOwnIconPath() {
        if (ProgConfig.SYSTEM_USE_OWN_PROGRAM_ICON.getValue()) {
            return ProgConfig.SYSTEM_PROGRAM_ICON_PATH.getValueSafe();
        } else {
            return "";
        }
    }

    public static void setProgramIcon() {
        if (ProgConfig.SYSTEM_USE_OWN_PROGRAM_ICON.getValue()) {
            P2WindowIcon.setStageIcon(ProgConfig.SYSTEM_PROGRAM_ICON_PATH.getValueSafe());
        } else {
            P2WindowIcon.setStageIcon("");
        }
    }

    public static void centerGui() {
        ProgData.getInstance().primaryStage.centerOnScreen();
    }

    public static void minimizeGui() {
        ProgData.getInstance().primaryStage.setIconified(true);
    }

    public static void setFilter() {
        switch (MTPlayerController.paneShown) {
            case FILM:
                ProgConfig.FILM__FILTER_IS_SHOWING.setValue(!ProgConfig.FILM__FILTER_IS_SHOWING.getValue());
                break;
            case LIVE_FILM:
                ProgConfig.LIVE_FILM__FILTER_IS_SHOWING.setValue(!ProgConfig.LIVE_FILM__FILTER_IS_SHOWING.getValue());
                break;
            case DOWNLOAD:
                ProgConfig.DOWNLOAD__FILTER_IS_SHOWING.setValue(!ProgConfig.DOWNLOAD__FILTER_IS_SHOWING.getValue());
                break;
            case ABO:
                ProgConfig.ABO__FILTER_IS_SHOWING.setValue(!ProgConfig.ABO__FILTER_IS_SHOWING.getValue());
                break;
        }
    }

    public static void setInfos() {
        switch (MTPlayerController.paneShown) {
            case FILM:
                ProgConfig.FILM__INFO_IS_SHOWING.setValue(!ProgConfig.FILM__INFO_IS_SHOWING.getValue());
                break;
            case LIVE_FILM:
                ProgConfig.LIVE_FILM__INFO_IS_SHOWING.setValue(!ProgConfig.LIVE_FILM__INFO_IS_SHOWING.getValue());
                break;
            case DOWNLOAD:
                ProgConfig.DOWNLOAD__INFO_IS_SHOWING.setValue(!ProgConfig.DOWNLOAD__INFO_IS_SHOWING.getValue());
                break;
            case ABO:
                ProgConfig.ABO__INFO_IS_SHOWING.setValue(!ProgConfig.ABO__INFO_IS_SHOWING.getValue());
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
