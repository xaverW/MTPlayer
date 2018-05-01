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

package de.mtplayer.mtp.tools.update;

import de.mtplayer.mLib.tools.Functions;
import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.p2tools.p2Lib.checkForUpdates.SearchProgInfo;
import javafx.beans.property.BooleanProperty;

import java.util.Date;

public class SearchProgramUpdate {

    BooleanProperty updateProp;

    public SearchProgramUpdate() {
        updateProp = ProgConfig.SYSTEM_UPDATE_SEARCH.getBooleanProperty();
    }

    /**
     * @param showError
     * @param showProgramInformation
     * @return
     */
    public boolean checkVersion(boolean showError, boolean showProgramInformation) {
        // prüft auf neue Version,
        ProgConfig.SYSTEM_UPDATE_BUILD_NR.setValue(Functions.getProgVersion());
        ProgConfig.SYSTEM_UPDATE_DATE.setValue(StringFormatters.FORMATTER_yyyyMMdd.format(new Date()));

        return new SearchProgInfo().checkUpdate(ProgConst.ADRESSE_MTPLAYER_VERSION, ProgConfig.SYSTEM_UPDATE_BUILD_NR.getInt(),
                ProgConfig.SYSTEM_UPDATE_INFO_NR_SHOWN.getIntegerProperty(), updateProp,
                showProgramInformation, showError);
    }

}
