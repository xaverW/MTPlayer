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


package de.p2tools.mtplayer.controller.update;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.dialogs.WhatsNewDialog;
import de.p2tools.p2lib.dialogs.WhatsNewInfo;
import de.p2tools.p2lib.tools.ProgramToolsFactory;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.date.P2LDateFactory;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WhatsNewFactory {

    private WhatsNewFactory() {
    }

    public static void checkUpdate() {
        P2Log.sysLog("Programmstart, alte Programm-Release-Nr: " + ProgConfig.SYSTEM_PROG_BUILD_NO.getValueSafe());
        P2Log.sysLog("Programmstart, aktuelle Programm-Release-Nr: " + ProgramToolsFactory.getBuild());

        if (!ProgConfig.SYSTEM_PROG_BUILD_NO.getValueSafe().equals(ProgramToolsFactory.getBuild())) {
            // dann hat sich die BUILD_NO geändert, neue Version: Dann checken
            showWhatsNew(false);
        } else {
            // sonst ist ja eh eine neue Version
            checkProgUpdate();
        }
    }

    public static void setLastShown() {
        ProgConfig.SYSTEM_WHATS_NEW_DATE_LAST_SHOWN.setValue(P2LDateFactory.getNowStringR());
    }

    public static boolean showWhatsNew(boolean showAlways) {
        // zeigt die Infos "whatsNew" an, wenn "showAlways" oder wenn die letzte Anzeige vor "whatsNewDate"
        WhatsNewList whatsNewList = new WhatsNewList();

        boolean ret = false;
        try {
            ArrayList<WhatsNewInfo> list = showAlways ? whatsNewList : whatsNewList.getOnlyNews();
            if (list.isEmpty()) {
                // dann gibts nix
                ret = false;

            } else {
                Platform.runLater(() -> new WhatsNewDialog(ProgData.getInstance().primaryStage, ProgConst.PROGRAM_NAME,
                        ProgConst.URL_WEBSITE, ProgConfig.SYSTEM_PROG_OPEN_URL,
                        ProgConfig.SYSTEM_DARK_THEME.getValue(), list).make());
                ret = true;
            }
        } catch (Exception ignore) {
            ret = false;
        }

        whatsNewList.setLastShown();
        return ret;
    }

    private static void checkProgUpdate() {
        // Prüfen obs ein Programmupdate gibt
        PDuration.onlyPing("checkProgUpdate");
        if (ProgConfig.SYSTEM_UPDATE_SEARCH_ACT.getValue() &&
                !updateCheckTodayDone()) {
            // nach Updates suchen
            runUpdateCheck();

        } else {
            // will der User nicht --oder-- wurde heute schon gemacht
            List<String> list = new ArrayList<>(5);
            list.add("Kein Update-Check:");
            if (!ProgConfig.SYSTEM_UPDATE_SEARCH_ACT.getValue()) {
                list.add("  der User will nicht");
            }
            if (updateCheckTodayDone()) {
                list.add("  heute schon gemacht");
            }
            P2Log.sysLog(list);
        }
    }

    private static boolean updateCheckTodayDone() {
        return ProgConfig.SYSTEM_UPDATE_DATE.get().equals(P2DateConst.F_FORMAT_yyyy_MM_dd.format(new Date()));
    }

    private static void runUpdateCheck() {
        ProgConfig.SYSTEM_UPDATE_DATE.setValue(P2LDateFactory.getNowStringR());
        new SearchProgramUpdate(ProgData.getInstance()).searchNewProgramVersion(false);
    }
}
