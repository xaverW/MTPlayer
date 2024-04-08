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
import de.p2tools.p2lib.tools.P2ToolsFactory;
import de.p2tools.p2lib.tools.date.P2Date;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;

import static java.lang.Thread.sleep;

public class SearchProgramUpdate {

    private static final String TITLE_TEXT_PROGRAM_VERSION_IS_UPTODATE = "Programmversion ist aktuell";
    private static final String TITLE_TEXT_PROGRAMMUPDATE_EXISTS = "Ein Programmupdate ist verfügbar";
    private final ProgData progData;
    private String title = "";

    public SearchProgramUpdate(final ProgData progData) {
        this.progData = progData;
    }

    public SearchProgramUpdate(final ProgData progData, final Stage stage) {
        this.progData = progData;
    }

    /**
     * @return
     */
    public void searchNewProgramVersion(final boolean showAlways) {
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

        final P2Date pd = new P2Date(P2ToolsFactory.getCompileDate());
        final String buildDate = pd.get_yyyy_MM_dd();

        final FoundSearchData foundSearchData;
        if (ProgData.showUpdate) {
            foundSearchData = new FoundSearchData(
                    progData.primaryStage,
                    SEARCH_URL,
                    SEARCH_URL_DOWNLOAD,

                    new SimpleBooleanProperty(true), // ProgConfig.SYSTEM_UPDATE_SEARCH_ACT,
                    new SimpleBooleanProperty(true), // ProgConfig.SYSTEM_UPDATE_SEARCH_BETA,
                    new SimpleBooleanProperty(true), // ProgConfig.SYSTEM_UPDATE_SEARCH_DAILY,

                    new SimpleStringProperty("2020.10.20"), // ProgConfig.SYSTEM_UPDATE_LAST_INFO,
                    new SimpleStringProperty("2020.10.20"), // ProgConfig.SYSTEM_UPDATE_LAST_ACT,
                    new SimpleStringProperty("2020.10.20"), // ProgConfig.SYSTEM_UPDATE_LAST_BETA,
                    new SimpleStringProperty("2020.10.20"), // ProgConfig.SYSTEM_UPDATE_LAST_DAILY,

                    ProgConst.URL_WEBSITE,
                    ProgConst.URL_WEBSITE_DOWNLOAD,
                    ProgConst.PROGRAM_NAME,
                    "0", // ProgramToolsFactory.getProgVersion(),
                    "1", // ProgramToolsFactory.getBuild(),
                    "2020.10.20", // buildDate,
                    ProgConfig.SYSTEM_DOWNLOAD_DIR_NEW_VERSION,
                    true); //showAlways);

        } else {
            foundSearchData = new FoundSearchData(
                    progData.primaryStage,
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
                    P2ToolsFactory.getProgVersion(),
                    P2ToolsFactory.getBuild(),
                    buildDate,
                    ProgConfig.SYSTEM_DOWNLOAD_DIR_NEW_VERSION,
                    showAlways);
        }
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
