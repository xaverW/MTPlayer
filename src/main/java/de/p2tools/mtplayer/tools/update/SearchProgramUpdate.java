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

package de.p2tools.mtplayer.tools.update;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2Lib.checkForUpdates.SearchProgUpdate;
import de.p2tools.p2Lib.checkForUpdates.UpdateSearchData;
import de.p2tools.p2Lib.tools.ProgramTools;
import de.p2tools.p2Lib.tools.date.PDateFactory;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.Date;

import static java.lang.Thread.sleep;

public class SearchProgramUpdate {

    private final ProgData progData;
    private final Stage stage;
    private static final String TITLE_TEXT_PROGRAM_VERSION_IS_UPTODATE = "Programmversion ist aktuell";
    private static final String TITLE_TEXT_PROGRAMMUPDATE_EXISTS = "Ein Programmupdate ist verfügbar";
    private String title = "";

    public SearchProgramUpdate(ProgData progData) {
        this.progData = progData;
        this.stage = progData.primaryStage;
    }

    public SearchProgramUpdate(Stage stage, ProgData progData) {
        this.progData = progData;
        this.stage = stage;
    }

    /**
     * @return
     */
    public boolean searchNewProgramVersion() {
        // prüft auf neue Version, ProgVersion und auch (wenn gewünscht) BETA-Version
        boolean ret;
        ProgConfig.SYSTEM_UPDATE_DATE.setValue(PDateFactory.F_FORMATTER_yyyyMMdd.format(new Date()));

        if (!ProgConfig.SYSTEM_UPDATE_SEARCH.getBool()) {
            // dann ist es nicht gewünscht
            return false;
        }

        UpdateSearchData updateSearchData = new UpdateSearchData(ProgConst.ADRESSE_MTPLAYER_VERSION,
                ProgramTools.getProgVersionInt(), ProgramTools.getBuildInt(),
                ProgConfig.SYSTEM_UPDATE_VERSION_SHOWN.getIntegerProperty(),
                null,
                ProgConfig.SYSTEM_UPDATE_INFO_NR_SHOWN.getIntegerProperty(),
                ProgConfig.SYSTEM_UPDATE_SEARCH.getBooleanProperty());

        UpdateSearchData updateSearchDataBeta = null;
        if (ProgConfig.SYSTEM_UPDATE_BETA_SEARCH.getBool()) {
            updateSearchDataBeta = new UpdateSearchData(ProgConst.ADRESSE_MTPLAYER_BETA_VERSION,
                    ProgramTools.getProgVersionInt(), ProgramTools.getBuildInt(),
                    ProgConfig.SYSTEM_UPDATE_BETA_VERSION_SHOWN.getIntegerProperty(),
                    ProgConfig.SYSTEM_UPDATE_BETA_BUILD_NO_SHOWN.getIntegerProperty(),
                    null,
                    ProgConfig.SYSTEM_UPDATE_BETA_SEARCH.getBooleanProperty());
        }

        ret = new SearchProgUpdate(stage).checkAllUpdates(updateSearchData, updateSearchDataBeta, false);
        setTitleInfo(ret);
        return ret;
    }

    private void setTitleInfo(boolean newVersion) {
        title = progData.primaryStage.getTitle();
        if (newVersion) {
            Platform.runLater(() -> setUpdateTitle());
        } else {
            Platform.runLater(() -> setNoUpdateTitle());
        }
        try {
            sleep(10_000);
        } catch (Exception ignore) {
        }
        Platform.runLater(() -> setOrgTitle());
    }

    private void setUpdateTitle() {
        progData.primaryStage.setTitle(TITLE_TEXT_PROGRAMMUPDATE_EXISTS);
    }

    private void setNoUpdateTitle() {
        progData.primaryStage.setTitle(TITLE_TEXT_PROGRAM_VERSION_IS_UPTODATE);
    }

    private void setOrgTitle() {
        progData.primaryStage.setTitle(title);
    }

    /**
     * @return
     */
    public boolean searchNewVersionInfos() {
        // prüft auf neue Version und zeigts immer an, auch (wenn gewünscht) BETA-Version

        UpdateSearchData updateSearchData = new UpdateSearchData(ProgConst.ADRESSE_MTPLAYER_VERSION,
                ProgramTools.getProgVersionInt(), ProgramTools.getBuildInt(),
                null,
                null,
                null,
                null);

        UpdateSearchData updateSearchDataBeta = null;
        if (ProgConfig.SYSTEM_UPDATE_BETA_SEARCH.getBool()) {
            updateSearchDataBeta = new UpdateSearchData(ProgConst.ADRESSE_MTPLAYER_BETA_VERSION,
                    ProgramTools.getProgVersionInt(), ProgramTools.getBuildInt(),
                    null,
                    null,
                    null,
                    null);
        }

        return new SearchProgUpdate(stage).checkAllUpdates(updateSearchData, updateSearchDataBeta, true);
    }

}
