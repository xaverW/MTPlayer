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

package de.mtplayer.mtp.gui.mediaConfig;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class MediaConfigDialogController extends PDialogExtra {

    private TabPane tabPane = new TabPane();
    private Button btnOk = new Button("_Ok");
    private Button btnCreateMediaDB = new Button("_Mediensammlung neu aufbauen");
    private ProgressBar progress = new ProgressBar();

    PaneConfigController mediaConfigPaneController;
    PaneMediaListController mediaListPaneController;
    PaneHistoryController historyListPaneController;
    PaneHistoryController aboListPaneController;

    IntegerProperty propSelectedTab = ProgConfig.SYSTEM_MEDIA_DIALOG_TAB;
    private final ProgData progData;

    public MediaConfigDialogController() {
        super(ProgData.getInstance().primaryStage, ProgConfig.MEDIA_CONFIG_DIALOG_SIZE.getStringProperty(), "Mediensammlung",
                true, false, DECO.NONE);

        this.progData = ProgData.getInstance();
        init(true);
    }

    @Override
    public void make() {
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        getvBoxCont().getChildren().add(tabPane);
        getvBoxCont().setPadding(new Insets(0));

        final Button btnHelp = PButton.helpButton(getStage(), "Medien", HelpText.MEDIA_DIALOG);
        btnOk.setOnAction(a -> close());
        progress.visibleProperty().bind(progData.mediaDataList.searchingProperty());
        btnCreateMediaDB.disableProperty().bind(progData.mediaDataList.searchingProperty());
        btnCreateMediaDB.setOnAction(event -> progData.mediaDataList.createMediaDb());

        addOkButton(btnOk);
        addHlpButton(btnHelp);
        getHBoxOverButtons().getChildren().addAll(btnCreateMediaDB, progress);
        initPanel();
    }

    @Override
    public void close() {
        progress.visibleProperty().unbind();
        btnCreateMediaDB.disableProperty().unbind();

        mediaConfigPaneController.close();
        mediaListPaneController.close();
        historyListPaneController.close();
        aboListPaneController.close();
        super.close();
    }

    private void initPanel() {
        try {
            mediaConfigPaneController = new PaneConfigController(getStage());
            Tab tab = new Tab("Einstellungen Mediensammlung");
            tab.setClosable(false);
            tab.setContent(mediaConfigPaneController);
            tabPane.getTabs().add(tab);

            mediaListPaneController = new PaneMediaListController(getStage());
            tab = new Tab("Mediensammlung");
            tab.setClosable(false);
            tab.setContent(mediaListPaneController);
            tabPane.getTabs().add(tab);

            historyListPaneController = new PaneHistoryController(getStage(), true);
            tab = new Tab("gesehene Filme");
            tab.setClosable(false);
            tab.setContent(historyListPaneController);
            tabPane.getTabs().add(tab);

            aboListPaneController = new PaneHistoryController(getStage(), false);
            tab = new Tab("erledigte Abos");
            tab.setClosable(false);
            tab.setContent(aboListPaneController);
            tabPane.getTabs().add(tab);

            tabPane.getSelectionModel().select(propSelectedTab.get());
            tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                // readOnlyBinding!!
                propSelectedTab.setValue(newValue);
            });

        } catch (final Exception ex) {
            PLog.errorLog(962104652, ex);
        }
    }

}
