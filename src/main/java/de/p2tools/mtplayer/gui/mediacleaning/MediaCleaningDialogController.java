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

package de.p2tools.mtplayer.gui.mediacleaning;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class MediaCleaningDialogController extends PDialogExtra {

    private TabPane tabPane = new TabPane();
    private Button btnOk = new Button("_Ok");
    private boolean media;
    private final ProgData progData;

    private PaneCleaningConfigController paneCleaningConfigControllerMedia;
    private PaneCleaningConfigController paneCleaningConfigControllerAbo;
    private PaneCleaningListController paneCleaningListController;

    public MediaCleaningDialogController(boolean media) {
        super(ProgData.getInstance().primaryStage, ProgConfig.DOWNLOAD_GUI_MEDIA_CONFIG_DIALOG_SIZE, "Einstellungen",
                true, false, DECO.NO_BORDER);

        this.progData = ProgData.getInstance();
        this.media = media;
        init(true);
    }

    @Override
    public void make() {
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        final Button btnHelp = PButton.helpButton(getStage(), "Medien", HelpText.MEDIA_DIALOG);
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
        paneCleaningListController.close();
        super.close();
    }

    private void initPanel() {
        try {
            Tab tabConfig;
            Tab tabCleaningList;

            paneCleaningConfigControllerMedia = new PaneCleaningConfigController(getStage(), true);
            tabConfig = new Tab("Einstellungen Mediensammlung");
            tabConfig.setClosable(false);
            tabConfig.setContent(paneCleaningConfigControllerMedia.makePane());
            tabPane.getTabs().add(tabConfig);

            paneCleaningConfigControllerAbo = new PaneCleaningConfigController(getStage(), false);
            tabConfig = new Tab("Einstellungen Abos und History");
            tabConfig.setClosable(false);
            tabConfig.setContent(paneCleaningConfigControllerAbo.makePane());
            tabPane.getTabs().add(tabConfig);

            paneCleaningListController = new PaneCleaningListController(getStage());
            tabCleaningList = new Tab("Cleaning Liste");
            tabCleaningList.setClosable(false);
            tabCleaningList.setContent(paneCleaningListController.makePane());
            tabPane.getTabs().add(tabCleaningList);

            tabPane.getSelectionModel().select(media ? 0 : 1);
        } catch (final Exception ex) {
            PLog.errorLog(962104652, ex);
        }
    }
}
