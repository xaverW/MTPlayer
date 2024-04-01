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

package de.p2tools.mtplayer.gui.mediacleaningdialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.dialog.propose.PaneCleaningList;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class MediaCleaningDialogController extends P2DialogExtra {

    private final TabPane tabPane = new TabPane();
    private final Button btnOk = new Button("_Ok");
    private final ProgData progData;

    private PaneCleaningConfigController paneCleaningConfigControllerMedia;
    private PaneCleaningConfigController paneCleaningConfigControllerAbo;
    private PaneCleaningList paneCleaningList;
    private final MediaDataDto mediaDataDtoMedia;
    private final MediaDataDto mediaDataDtoAbo;

    public MediaCleaningDialogController(MediaDataDto mediaDataDtoMedia, MediaDataDto mediaDataDtoAbo) {
        super(ProgData.getInstance().primaryStage, ProgConfig.GUI_MEDIA_CONFIG_DIALOG_SIZE, "Einstellungen",
                true, false, DECO.NO_BORDER);

        this.progData = ProgData.getInstance();
        this.mediaDataDtoMedia = mediaDataDtoMedia;
        this.mediaDataDtoAbo = mediaDataDtoAbo;
        init(true);
    }

    @Override
    public void make() {
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        final Button btnHelp = P2Button.helpButton(getStage(), "Medien", HelpText.MEDIA_CLEANING_CONFIG_DIALOG);
        btnOk.setOnAction(a -> close());

        addOkButton(btnOk);
        addHlpButton(btnHelp);

        getVBoxCont().setPadding(new Insets(0));
        getVBoxCont().getChildren().add(tabPane);
        initPanel();
    }

    @Override
    public void close() {
        paneCleaningConfigControllerMedia.close();
        paneCleaningConfigControllerAbo.close();
        paneCleaningList.close();
        super.close();
    }

    private void initPanel() {
        try {
            Tab tabConfig;
            Tab tabCleaningList;

            paneCleaningConfigControllerMedia = new PaneCleaningConfigController(getStage(), mediaDataDtoMedia);
            tabConfig = new Tab("Einstellungen Mediensammlung");
            tabConfig.setClosable(false);
            tabConfig.setContent(paneCleaningConfigControllerMedia.makePane());
            tabPane.getTabs().add(tabConfig);

            paneCleaningConfigControllerAbo = new PaneCleaningConfigController(getStage(), mediaDataDtoAbo);
            tabConfig = new Tab("Einstellungen Abos");
            tabConfig.setClosable(false);
            tabConfig.setContent(paneCleaningConfigControllerAbo.makePane());
            tabPane.getTabs().add(tabConfig);

            paneCleaningList = new PaneCleaningList(getStage(), false);
            tabCleaningList = new Tab("Cleaning Liste");
            tabCleaningList.setClosable(false);
            tabCleaningList.setContent(paneCleaningList.makePane());
            tabPane.getTabs().add(tabCleaningList);
        } catch (final Exception ex) {
            P2Log.errorLog(962104652, ex);
        }
    }
}
