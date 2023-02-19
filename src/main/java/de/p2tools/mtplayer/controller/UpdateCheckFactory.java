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


package de.p2tools.mtplayer.controller;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.update.SearchProgramUpdate;
import de.p2tools.p2Lib.tools.date.DateFactory;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdateCheckFactory {
    private UpdateCheckFactory() {
    }

    public static void checkProgUpdate() {
        // Prüfen obs ein Programmupdate gibt
        PDuration.onlyPing("checkProgUpdate");
        if (ProgConfig.SYSTEM_UPDATE_SEARCH_ACT.getValue() &&
                !updateCheckTodayDone()) {
            // nach Updates suchen
            runUpdateCheck(false);

        } else {
            // will der User nicht --oder-- wurde heute schon gemacht
            List list = new ArrayList(5);
            list.add("Kein Update-Check:");
            if (!ProgConfig.SYSTEM_UPDATE_SEARCH_ACT.getValue()) {
                list.add("  der User will nicht");
            }
            if (updateCheckTodayDone()) {
                list.add("  heute schon gemacht");
            }
            PLog.sysLog(list);
        }
    }

    private static boolean updateCheckTodayDone() {
        return ProgConfig.SYSTEM_UPDATE_DATE.get().equals(DateFactory.F_FORMAT_yyyy_MM_dd.format(new Date()));
    }

    private static void runUpdateCheck(boolean showAlways) {
        ProgConfig.SYSTEM_UPDATE_DATE.setValue(DateFactory.F_FORMAT_yyyy_MM_dd.format(new Date()));
        new SearchProgramUpdate(ProgData.getInstance()).searchNewProgramVersion(showAlways);
    }
}
