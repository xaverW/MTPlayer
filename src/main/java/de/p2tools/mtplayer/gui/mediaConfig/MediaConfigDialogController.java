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

package de.p2tools.mtplayer.gui.mediaConfig;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.mediaDb.MediaDataWorker;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.dialogs.PDirFileChooser;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class MediaConfigDialogController extends PDialogExtra {

    private TabPane tabPane = new TabPane();
    private Button btnOk = new Button("_Ok");
    private Button btnCreateMediaDB = new Button("_Mediensammlung neu aufbauen");
    private Button btnExportMediaDB = new Button("Mediensammlung exportieren");
    private ProgressBar progress = new ProgressBar();
    private Button btnStopSearching = new Button();

    private PaneConfigController paneConfigController;
    private PaneMediaController paneMediaController;
    private PaneHistoryController paneHistoryController;
    private PaneHistoryController paneAboController;

    private Tab tabConfig = null;
    private Tab tabMedia = null;
    private Tab tabHistory = null;
    private Tab tabAbo = null;

    IntegerProperty propSelectedTab = ProgConfig.SYSTEM_MEDIA_DIALOG_TAB;
    StringProperty searchText = new SimpleStringProperty();

    private final ProgData progData;

    public MediaConfigDialogController() {
        super(ProgData.getInstance().primaryStage, ProgConfig.MEDIA_CONFIG_DIALOG_SIZE, "Mediensammlung",
                true, false, DECO.NONE);

        this.progData = ProgData.getInstance();
        init(true);
    }

    @Override
    public void make() {
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        getVBoxCont().getChildren().add(tabPane);
        getVBoxCont().setPadding(new Insets(0));

        final Button btnHelp = PButton.helpButton(getStage(), "Medien", HelpText.MEDIA_DIALOG);
        btnOk.setOnAction(a -> close());
        progress.visibleProperty().bind(progData.mediaDataList.searchingProperty());
        btnCreateMediaDB.disableProperty().bind(progData.mediaDataList.searchingProperty());
        btnCreateMediaDB.setOnAction(event -> MediaDataWorker.createMediaDb());

        btnExportMediaDB.disableProperty().bind(progData.mediaDataList.searchingProperty());
        btnExportMediaDB.setOnAction(a -> {
            String file = PDirFileChooser.FileChooserSave(ProgData.getInstance().primaryStage, "", "Mediensammlung.json");
            new WriteMediaCollection().write(file, progData.mediaDataList);
        });

        addOkButton(btnOk);
        addHlpButton(btnHelp);

        progress.setMaxHeight(Double.MAX_VALUE);
        progress.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(progress, Priority.ALWAYS);

        btnStopSearching.setGraphic(ProgIcons.Icons.ICON_BUTTON_STOP.getImageView());
        btnStopSearching.setOnAction(event -> progData.mediaDataList.setStopSearching(true));
        btnStopSearching.visibleProperty().bind(progData.mediaDataList.searchingProperty());

        getHBoxOverButtons().getChildren().addAll(btnCreateMediaDB, btnExportMediaDB, progress, btnStopSearching);
        initPanel();
    }

    @Override
    public void close() {
        progress.visibleProperty().unbind();
        btnCreateMediaDB.disableProperty().unbind();

        paneConfigController.close();
        paneMediaController.close();
        paneHistoryController.close();
        paneAboController.close();
        super.close();
    }

    private void initPanel() {
        try {
            paneConfigController = new PaneConfigController(getStage());
            tabConfig = new Tab("Einstellungen Mediensammlung");
            tabConfig.setClosable(false);
            tabConfig.setContent(paneConfigController);
            tabPane.getTabs().add(tabConfig);

            paneMediaController = new PaneMediaController(getStage());
            tabMedia = new Tab("Mediensammlung");
            tabMedia.setClosable(false);
            tabMedia.setContent(paneMediaController);
            tabPane.getTabs().add(tabMedia);

            paneHistoryController = new PaneHistoryController(getStage(), true, searchText);
            tabHistory = new Tab("gesehene Filme");
            tabHistory.setClosable(false);
            tabHistory.setContent(paneHistoryController);
            tabPane.getTabs().add(tabHistory);

            paneAboController = new PaneHistoryController(getStage(), false, searchText);
            tabAbo = new Tab("erledigte Abos");
            tabAbo.setClosable(false);
            tabAbo.setContent(paneAboController);
            tabPane.getTabs().add(tabAbo);

            tabPane.getSelectionModel().select(propSelectedTab.get());
            tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.equals(tabHistory)) {
                    paneHistoryController.tabChange();
                } else if (newValue.equals(tabAbo)) {
                    paneAboController.tabChange();
                }
            });
            tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                // readOnlyBinding!!
                propSelectedTab.setValue(newValue);
            });

        } catch (final Exception ex) {
            PLog.errorLog(962104652, ex);
        }
    }
}
