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
import de.p2tools.p2Lib.dialogs.dialog.PDialog;
import de.p2tools.p2Lib.icons.GetIcon;
import de.p2tools.p2Lib.tools.ProgramToolsFactory;
import de.p2tools.p2Lib.tools.log.LogMessage;
import de.p2tools.p2Lib.tools.log.PLog;

import java.util.ArrayList;

public class ProgStartAfterGui {
//    private static boolean doneAtProgramStart = false;

    private ProgStartAfterGui() {
    }

    /**
     * alles was nach der GUI gemacht werden soll z.B.
     * Filmliste beim Programmstart!! laden
     */
    public static void doWorkAfterGui() {
        setProgramIcon();
        startMsg();
        setTitle();
        ProgData.getInstance().progTray.initProgTray();
        if (ProgConfig.FILM_GUI_FILTER_DIALOG_IS_SHOWING.getValue()) {
            new FilmFilterDialog(ProgData.getInstance()).showDialog();
        }

        ProgData.getInstance().startTimer();
//        LoadFilmFactory.getInstance().loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
//            @Override
//            public void finished(ListenerFilmlistLoadEvent event) {
//                int age = FilmlistFactory.getAge(ProgData.getInstance().filmlist.metaData);
//                ProgConfig.SYSTEM_FILMLIST_AGE.setValue(ProgData.getInstance().filmlist.isEmpty() ? P2LibConst.NUMBER_NOT_STARTED : age);
//
//                if (!doneAtProgramStart) {
//                    doneAtProgramStart = true;
//                    MediaDataWorker.createMediaDb();
//                    checkProgUpdate();
//                    new ProgTipOfDayFactory().showDialog(ProgData.getInstance(), false);
//                }
//            }
//        });

        //die gespeicherte Filmliste laden
        LoadFilmFactory.getInstance().loadFilmlistProgStart();
    }

    public static void setProgramIcon() {
        if (ProgConfig.SYSTEM_USE_OWN_PROGRAM_ICON.getValue()) {
            String resource = ProgConfig.SYSTEM_PROGRAM_ICON_PATH.getValueSafe();
            GetIcon.addWindowP2Icon(ProgData.getInstance().primaryStage, resource);
            PDialog.setIconPath(resource);
        } else {
            GetIcon.addWindowP2Icon(ProgData.getInstance().primaryStage);
            PDialog.setIconPath("");
        }
    }

    public static void shortStartMsg() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Verzeichnisse:");
        list.add("Programmpfad: " + ProgInfos.getPathJar());
        list.add("Verzeichnis Einstellungen: " + ProgInfos.getSettingsDirectory_String());

        LogMessage.startMsg(ProgConst.PROGRAM_NAME, list);

        list = new ArrayList<>();
        list.add(PLog.LILNE2);
        list.add("|  Programmsets:");
        list.addAll(ProgData.getInstance().setDataList.getStringListSetData());
        PLog.sysLog(list);
    }

    public static void startMsg() {
        shortStartMsg();
        ProgConfig.logAllConfigs();
    }

//    private static void checkProgUpdate() {
//        // Prüfen obs ein Programmupdate gibt
//        PDuration.onlyPing("checkProgUpdate");
//        if (ProgConfig.SYSTEM_UPDATE_SEARCH_ACT.getValue() &&
//                !updateCheckTodayDone()) {
//            // nach Updates suchen
//            runUpdateCheck(false);
//
//        } else {
//            // will der User nicht --oder-- wurde heute schon gemacht
//            List list = new ArrayList(5);
//            list.add("Kein Update-Check:");
//            if (!ProgConfig.SYSTEM_UPDATE_SEARCH_ACT.getValue()) {
//                list.add("  der User will nicht");
//            }
//            if (updateCheckTodayDone()) {
//                list.add("  heute schon gemacht");
//            }
//            PLog.sysLog(list);
//        }
//    }
//
//    private static boolean updateCheckTodayDone() {
//        return ProgConfig.SYSTEM_UPDATE_DATE.get().equals(DateFactory.F_FORMAT_yyyy_MM_dd.format(new Date()));
//    }
//
//    private static void runUpdateCheck(boolean showAlways) {
//        ProgConfig.SYSTEM_UPDATE_DATE.setValue(DateFactory.F_FORMAT_yyyy_MM_dd.format(new Date()));
//        new SearchProgramUpdate(ProgData.getInstance()).searchNewProgramVersion(showAlways);
//    }

    private static void setTitle() {
        if (ProgData.debug) {
            ProgData.getInstance().primaryStage.setTitle(ProgConst.PROGRAM_NAME + " " + ProgramToolsFactory.getProgVersion() + " / DEBUG");
        } else {
            ProgData.getInstance().primaryStage.setTitle(ProgConst.PROGRAM_NAME + " " + ProgramToolsFactory.getProgVersion());
        }
    }
}
