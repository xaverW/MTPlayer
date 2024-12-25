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
import de.p2tools.p2lib.tools.P2ToolsFactory;
import de.p2tools.p2lib.tools.date.P2LDateFactory;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class WhatsNewFactory {

    private WhatsNewFactory() {
    }

    public static void checkUpdate() {
        // nach dem Laden der Filmliste
        P2Log.sysLog("Programmstart, alte Programm-Release-Nr: " + ProgConfig.SYSTEM_PROG_BUILD_NO.getValueSafe());
        P2Log.sysLog("Programmstart, aktuelle Programm-Release-Nr: " + P2ToolsFactory.getBuildNo());
        if (!ProgConfig.SYSTEM_PROG_BUILD_NO.getValueSafe().equals(P2ToolsFactory.getBuildNo())) {
            // dann hat sich die BUILD_NO geändert, ist eine neue Version: Anzeigen was neu ist
            showWhatsNew(false);

        } else {
            // sonst prüfen, obs eine neue Version gibt
            checkProgUpdate();
        }
    }

    public static void setLastShown() {
        ProgConfig.SYSTEM_WHATS_NEW_DATE_LAST_SHOWN.setValue(P2LDateFactory.getNowStringR());
    }

    public static void showWhatsNew(boolean showAlways) {
        // zeigt die Infos "whatsNew" an, wenn "showAlways" oder wenn die letzte Anzeige vor "whatsNewDate"
        WhatsNewList whatsNewList = new WhatsNewList();

        try {
            ArrayList<WhatsNewInfo> list = showAlways ? whatsNewList : whatsNewList.getOnlyNews();
            if (!list.isEmpty()) {
                Platform.runLater(() -> new WhatsNewDialog(ProgData.getInstance().primaryStage, ProgConst.PROGRAM_NAME,
                        ProgConst.URL_WEBSITE, ProgConfig.SYSTEM_PROG_OPEN_URL,
                        ProgConfig.SYSTEM_DARK_THEME.getValue(), list).make());
            }
        } catch (Exception ignore) {
        }

        whatsNewList.setLastShown();
    }

    private static void checkProgUpdate() {
        // Prüfen obs ein Programmupdate gibt
        P2Duration.onlyPing("checkProgUpdate");
        if (ProgConfig.SYSTEM_SEARCH_UPDATE.getValue() &&
                !isUpdateCheckTodayDone()) {
            // nach Updates suchen
            runUpdateCheck();

        } else {
            // will der User nicht --oder-- wurde heute schon gemacht
            List<String> list = new ArrayList<>(5);
            list.add("Kein Update-Check:");
            if (!ProgConfig.SYSTEM_SEARCH_UPDATE.getValue()) {
                list.add("  der User will nicht");
            }
            if (isUpdateCheckTodayDone()) {
                list.add("  heute schon gemacht");
            }
            P2Log.sysLog(list);
        }
    }

    private static boolean isUpdateCheckTodayDone() {
        return ProgConfig.SYSTEM_SEARCH_UPDATE_TODAY_DONE.get().equals(P2LDateFactory.getNowStringR());
    }

    private static void runUpdateCheck() {
        ProgConfig.SYSTEM_SEARCH_UPDATE_TODAY_DONE.setValue(P2LDateFactory.getNowStringR());
        new SearchProgramUpdate(ProgData.getInstance()).searchNewProgramVersion(false);
    }
}
