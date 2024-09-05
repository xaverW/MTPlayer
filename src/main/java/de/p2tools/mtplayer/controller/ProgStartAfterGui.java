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

package de.p2tools.mtplayer.controller;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.gui.filter.FilmFilterDialog;
import de.p2tools.p2lib.dialogs.dialog.P2Dialog;
import de.p2tools.p2lib.guitools.P2WindowIcon;
import de.p2tools.p2lib.tools.P2ToolsFactory;
import de.p2tools.p2lib.tools.log.P2LogMessage;

import java.util.ArrayList;

public class ProgStartAfterGui {

    private ProgStartAfterGui() {
    }

    /**
     * alles was nach der GUI gemacht werden soll z.B.
     * Filmliste beim Programmstart!! laden
     */
    public static void doWorkAfterGui() {
        setProgramIcon();
        startMsg(true);
        setTitle();
        ProgData.getInstance().progTray.initProgTray();
        if (ProgConfig.FILM_GUI_FILTER_DIALOG_IS_SHOWING.getValue()) {
            new FilmFilterDialog(ProgData.getInstance()).showDialog();
        }
        ProgData.getInstance().startTimer();

        //die gespeicherte Filmliste laden, vorher den FilmFilter einschalten
        ProgData.getInstance().filterWorker.getActFilterSettings().switchFilterOff(false);
        LoadFilmFactory.getInstance().loadFilmlistProgStart();
    }

    public static void setProgramIcon() {
        if (ProgConfig.SYSTEM_USE_OWN_PROGRAM_ICON.getValue()) {
            String resource = ProgConfig.SYSTEM_PROGRAM_ICON_PATH.getValueSafe();
            P2WindowIcon.setWindowIcon(ProgData.getInstance().primaryStage, resource);
            P2Dialog.setIconPath(resource);
        } else {
            P2WindowIcon.addWindowP2Icon(ProgData.getInstance().primaryStage);
            P2Dialog.setIconPath("");
        }
    }

    public static void startMsg(boolean showAll) {
        ArrayList<String> list = new ArrayList<>();
        list.add("Verzeichnisse:");
        list.add("Programmpfad: " + ProgInfos.getPathJar());
        list.add("Verzeichnis Einstellungen: " + ProgInfos.getSettingsDirectory_String());
        P2LogMessage.startMsg(ProgConst.PROGRAM_NAME, list);
        if (showAll) {
            ProgConfig.logAllConfigs();
        }
    }

    private static void setTitle() {
        if (ProgData.debug) {
            ProgData.getInstance().primaryStage.setTitle(ProgConst.PROGRAM_NAME + " " + P2ToolsFactory.getProgVersion() + " / DEBUG");
        } else {
            ProgData.getInstance().primaryStage.setTitle(ProgConst.PROGRAM_NAME + " " + P2ToolsFactory.getProgVersion());
        }
    }
}
