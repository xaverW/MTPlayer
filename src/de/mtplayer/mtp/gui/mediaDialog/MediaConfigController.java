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

package de.mtplayer.mtp.gui.mediaDialog;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.dialog.PDialog;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class MediaConfigController extends PDialog {

    private final VBox vBoxDialog = new VBox(10);
    private TabPane tabPane = new TabPane();
    private Button btnOk = new Button("Ok");
    private Button btnCreateMediaDB = new Button("Mediensammlung neu aufbauen");
    private ProgressBar progress = new ProgressBar();

    private final ProgData progData;
    private Stage stage;

    public MediaConfigController() {
        super(ProgConfig.MEDIA_CONFIG_DIALOG_SIZE.getStringProperty(), "Mediensammlung", true);
        this.progData = ProgData.getInstance();

        vBoxDialog.setPadding(new Insets(10));
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        vBoxDialog.getChildren().addAll(tabPane);
        init(vBoxDialog, true);
    }

    @Override
    public void make() {
        stage = getStage();

        final Button btnHelp = new PButton().helpButton(stage, "Medien", HelpText.MEDIA_DIALOG);
        HBox hBoxHlp = new HBox(10);
        HBox.setHgrow(hBoxHlp, Priority.ALWAYS);
        hBoxHlp.getChildren().addAll(btnHelp, btnCreateMediaDB, progress);

        HBox hBoxOk = new HBox();
        hBoxOk.setPadding(new Insets(5));
        hBoxOk.getChildren().addAll(hBoxHlp, btnOk);

        vBoxDialog.getChildren().addAll(hBoxOk);

        btnOk.setOnAction(a -> close());
        progress.visibleProperty().bind(progData.mediaDataList.searchingProperty());
        btnCreateMediaDB.disableProperty().bind(progData.mediaDataList.searchingProperty());
        btnCreateMediaDB.setOnAction(event -> progData.mediaDataList.createInternalMediaDb());

        initPanel();
    }

    private void initPanel() {
        try {
            AnchorPane mediaConfigPaneController = new MediaConfigPaneMediaController(stage);
            Tab tab = new Tab("Einstellungen Mediensammlung");
            tab.setClosable(false);
            tab.setContent(mediaConfigPaneController);
            tabPane.getTabs().add(tab);


            AnchorPane mediaListPaneController = new MediaConfigPaneMediaListController(stage);
            tab = new Tab("Mediensammlung");
            tab.setClosable(false);
            tab.setContent(mediaListPaneController);
            tabPane.getTabs().add(tab);

            AnchorPane historyListPaneController = new MediaConfigPaneHistoryController(stage, true);
            tab = new Tab("gesehene Filme");
            tab.setClosable(false);
            tab.setContent(historyListPaneController);
            tabPane.getTabs().add(tab);

            AnchorPane aboListPaneController = new MediaConfigPaneHistoryController(stage, false);
            tab = new Tab("erledigte Abos");
            tab.setClosable(false);
            tab.setContent(aboListPaneController);
            tabPane.getTabs().add(tab);

        } catch (final Exception ex) {
            PLog.errorLog(962104652, ex);
        }
    }

}
