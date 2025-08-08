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
import de.p2tools.mtplayer.controller.load.LoadFilmFactory;
import de.p2tools.mtplayer.gui.configdialog.ConfigDialogController;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
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

    public static void loadFilmlist() {
        LoadFilmFactory.loadFilmListFromWeb(true);
    }

    public static void updateFilmlist() {
        LoadFilmFactory.loadFilmListFromWeb(false);
    }

    public static void minimizeGui() {
        ProgData.getInstance().primaryStage.setIconified(true);
        P2DialogExtra.getDialogList().forEach(p2Dialog -> p2Dialog.getStage().setIconified(true));
    }

    public static void setFilter() {
        if (MTPlayerController.TAB_FILM_ON.get()) {
            ProgConfig.FILM__FILTER_IS_SHOWING.setValue(!ProgConfig.FILM__FILTER_IS_SHOWING.getValue());
        } else if (MTPlayerController.TAB_AUDIO_ON.get()) {
            ProgConfig.AUDIO__FILTER_IS_SHOWING.setValue(!ProgConfig.AUDIO__FILTER_IS_SHOWING.getValue());
        } else if (MTPlayerController.TAB_LIVE_ON.get()) {
            ProgConfig.LIVE_FILM__FILTER_IS_SHOWING.setValue(!ProgConfig.LIVE_FILM__FILTER_IS_SHOWING.getValue());
        } else if (MTPlayerController.TAB_DOWNLOAD_ON.get()) {
            ProgConfig.DOWNLOAD__FILTER_IS_SHOWING.setValue(!ProgConfig.DOWNLOAD__FILTER_IS_SHOWING.getValue());
        } else if (MTPlayerController.TAB_ABO_ON.get()) {
            ProgConfig.ABO__FILTER_IS_SHOWING.setValue(!ProgConfig.ABO__FILTER_IS_SHOWING.getValue());
        }
    }

    public static void setInfos() {
        if (MTPlayerController.TAB_FILM_ON.get()) {
            ProgConfig.FILM__INFO_IS_SHOWING.setValue(!ProgConfig.FILM__INFO_IS_SHOWING.getValue());
        } else if (MTPlayerController.TAB_AUDIO_ON.get()) {
            ProgConfig.AUDIO__INFO_IS_SHOWING.setValue(!ProgConfig.AUDIO__INFO_IS_SHOWING.getValue());
        } else if (MTPlayerController.TAB_LIVE_ON.get()) {
            ProgConfig.LIVE_FILM__INFO_IS_SHOWING.setValue(!ProgConfig.LIVE_FILM__INFO_IS_SHOWING.getValue());
        } else if (MTPlayerController.TAB_DOWNLOAD_ON.get()) {
            ProgConfig.DOWNLOAD__INFO_IS_SHOWING.setValue(!ProgConfig.DOWNLOAD__INFO_IS_SHOWING.getValue());
        } else if (MTPlayerController.TAB_ABO_ON.get()) {
            ProgConfig.ABO__INFO_IS_SHOWING.setValue(!ProgConfig.ABO__INFO_IS_SHOWING.getValue());
        }
    }

    public static void showFilmInfos() {
        if (MTPlayerController.TAB_FILM_ON.get()) {
            ProgData.getInstance().filmGuiController.showFilmInfo();
        } else if (MTPlayerController.TAB_AUDIO_ON.get()) {
            ProgData.getInstance().audioGuiController.showFilmInfo();
        } else if (MTPlayerController.TAB_LIVE_ON.get()) {
            ProgData.getInstance().liveFilmGuiController.showFilmInfo();
        } else if (MTPlayerController.TAB_DOWNLOAD_ON.get()) {
            ProgData.getInstance().downloadGuiController.showFilmInfo();
        }
    }

    public static void copyTheme() {
        if (MTPlayerController.TAB_FILM_ON.get()) {
            ProgData.getInstance().filmGuiController.copyFilmThemeTitle(true);
        } else if (MTPlayerController.TAB_AUDIO_ON.get()) {
            ProgData.getInstance().audioGuiController.copyFilmThemeTitle(true);
        } else if (MTPlayerController.TAB_LIVE_ON.get()) {
            ProgData.getInstance().liveFilmGuiController.copyFilmThemeTitle(true);
        } else if (MTPlayerController.TAB_DOWNLOAD_ON.get()) {
            ProgData.getInstance().downloadGuiController.copyFilmThemeTitle(true);
        }
    }

    public static void copyTitle() {
        if (MTPlayerController.TAB_FILM_ON.get()) {
            ProgData.getInstance().filmGuiController.copyFilmThemeTitle(false);
        } else if (MTPlayerController.TAB_AUDIO_ON.get()) {
            ProgData.getInstance().audioGuiController.copyFilmThemeTitle(false);
        } else if (MTPlayerController.TAB_LIVE_ON.get()) {
            ProgData.getInstance().liveFilmGuiController.copyFilmThemeTitle(false);
        } else if (MTPlayerController.TAB_DOWNLOAD_ON.get()) {
            ProgData.getInstance().downloadGuiController.copyFilmThemeTitle(false);
        }
    }

    public static void addBlacklist() {
        if (MTPlayerController.TAB_FILM_ON.get()) {
            BlacklistFactory.addBlackFilm(BlacklistFactory.BLACK.FILM);
        } else if (MTPlayerController.TAB_AUDIO_ON.get()) {
            BlacklistFactory.addBlackFilm(BlacklistFactory.BLACK.AUDIO);
        } else if (MTPlayerController.TAB_DOWNLOAD_ON.get()) {
            BlacklistFactory.addBlackFilm(BlacklistFactory.BLACK.DOWNLOAD);
        }
    }

    public static void addBlacklistTheme() {
        if (MTPlayerController.TAB_FILM_ON.get()) {
            BlacklistFactory.addBlackThemeFilm(BlacklistFactory.BLACK.FILM);
        } else if (MTPlayerController.TAB_AUDIO_ON.get()) {
            BlacklistFactory.addBlackThemeFilm(BlacklistFactory.BLACK.AUDIO);
        } else if (MTPlayerController.TAB_DOWNLOAD_ON.get()) {
            BlacklistFactory.addBlackThemeDownload();
        }
    }

    public static void showBlacklist() {
        new ConfigDialogController(ProgData.getInstance(), true);
    }

    public static void setMediaCollection() {
        if (MTPlayerController.TAB_FILM_ON.get()) {
            ProgData.getInstance().filmGuiController.searchFilmInMediaCollection();
        } else if (MTPlayerController.TAB_AUDIO_ON.get()) {
            ProgData.getInstance().audioGuiController.searchFilmInMediaCollection();
        } else if (MTPlayerController.TAB_LIVE_ON.get()) {
            ProgData.getInstance().liveFilmGuiController.searchFilmInMediaCollection();
        } else if (MTPlayerController.TAB_DOWNLOAD_ON.get()) {
            ProgData.getInstance().downloadGuiController.searchFilmInMediaCollection();
        }
    }

    public static void undoDels() {
        if (MTPlayerController.TAB_DOWNLOAD_ON.get()) {
            ProgData.getInstance().downloadList.undoDownloads();
        } else if (MTPlayerController.TAB_ABO_ON.get()) {
            ProgData.getInstance().aboList.undoAbos();
        }
    }
}
