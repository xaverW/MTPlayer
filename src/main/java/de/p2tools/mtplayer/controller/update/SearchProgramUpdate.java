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

package de.p2tools.mtplayer.controller.update;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.checkforactinfos.FoundAll;
import de.p2tools.p2lib.checkforactinfos.FoundSearchData;
import de.p2tools.p2lib.tools.ProgramToolsFactory;
import de.p2tools.p2lib.tools.date.PDate;
import javafx.application.Platform;
import javafx.stage.Stage;

import static java.lang.Thread.sleep;

public class SearchProgramUpdate {

    private static final String TITLE_TEXT_PROGRAM_VERSION_IS_UPTODATE = "Programmversion ist aktuell";
    private static final String TITLE_TEXT_PROGRAMMUPDATE_EXISTS = "Ein Programmupdate ist verfügbar";
    private final ProgData progData;
    private final Stage stage;
    private String title = "";

    public SearchProgramUpdate(final ProgData progData) {
        this.progData = progData;
        this.stage = progData.primaryStage;
    }

    public SearchProgramUpdate(final ProgData progData, final Stage stage) {
        this.progData = progData;
        this.stage = stage;
    }

    /**
     * @return
     */
    public void searchNewProgramVersion(final boolean showAllways) {
        final String SEARCH_URL;
        final String SEARCH_URL_DOWNLOAD;
        SEARCH_URL = "https://www.p2tools.de";
        SEARCH_URL_DOWNLOAD = "https://www.p2tools.de/download/";

//        SEARCH_URL = "http://hugo.localhost:8080/";
//        SEARCH_URL_DOWNLOAD = "http://hugo.localhost:8080/download/";


//        if (ProgData.debug) {
//            SEARCH_URL = "http://p2.localhost:8080";
//            SEARCH_URL_DOWNLOAD = "http://p2.localhost:8080/download/";
//        } else {
//            SEARCH_URL = "https://www.p2tools.de";
//            SEARCH_URL_DOWNLOAD = "https://www.p2tools.de/download/";
//        }

        final PDate pd = new PDate(ProgramToolsFactory.getCompileDate());
        final String buildDate = pd.get_yyyy_MM_dd();

        final FoundSearchData foundSearchData = new FoundSearchData(
                stage,
                SEARCH_URL,
                SEARCH_URL_DOWNLOAD,

                ProgConfig.SYSTEM_UPDATE_SEARCH_ACT,
                ProgConfig.SYSTEM_UPDATE_SEARCH_BETA,
                ProgConfig.SYSTEM_UPDATE_SEARCH_DAILY,

                ProgConfig.SYSTEM_UPDATE_LAST_INFO,
                ProgConfig.SYSTEM_UPDATE_LAST_ACT,
                ProgConfig.SYSTEM_UPDATE_LAST_BETA,
                ProgConfig.SYSTEM_UPDATE_LAST_DAILY,

                ProgConst.URL_WEBSITE,
                ProgConst.URL_WEBSITE_DOWNLOAD,
                ProgConst.PROGRAM_NAME,
                ProgramToolsFactory.getProgVersion(),
                ProgramToolsFactory.getBuild(),
                buildDate,
                ProgConfig.SYSTEM_DOWNLOAD_DIR_NEW_VERSION,
                showAllways
        );

        new Thread(() -> {
            FoundAll.foundAll(foundSearchData);
            setTitleInfo(foundSearchData.foundNewVersionProperty().getValue());
        }).start();
    }

    private void setTitleInfo(final boolean newVersion) {
        title = progData.primaryStage.getTitle();
        if (newVersion) {
            Platform.runLater(() -> setUpdateTitle());
        } else {
            Platform.runLater(() -> setNoUpdateTitle());
        }
        try {
            sleep(10_000);
        } catch (final Exception ignore) {
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
}
