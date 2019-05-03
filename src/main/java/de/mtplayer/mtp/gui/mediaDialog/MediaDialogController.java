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
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.tools.storedFilter.Filter;
import de.p2tools.p2Lib.dialog.PDialog;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MediaDialogController extends PDialog {

    private final AnchorPane rootPane = new AnchorPane();
    private final VBox vBoxDialog = new VBox();
    private final VBox vBoxCont = new VBox();

    private final TextField txtSearch = new TextField();
    private final Button btnOk = new Button("Ok");
    private final Button btnReset = new Button("");

    private final RadioButton rbMedien = new RadioButton("Mediensammlung");
    private final RadioButton rbAbos = new RadioButton("erledigte Abos");

    private final StackPane stackPane = new StackPane();

    private PaneMedia paneMedia;
    private PaneAbo paneAbo;
    private final Listener listenerDbStart;
    private final Listener listenerDbStop;
    private final String searchStr;

    private final ProgData progData = ProgData.getInstance();
    private Stage stage;


    public MediaDialogController(String searchStr) {
        super(ProgConfig.MEDIA_DIALOG_SIZE.getStringProperty(), "Mediensammlung", true);
        this.searchStr = searchStr.trim();
        txtSearch.setText(this.searchStr);

        listenerDbStart = new Listener(Listener.EREIGNIS_MEDIA_DB_START, MediaDialogController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                // neue DB suchen
                txtSearch.setDisable(true);
            }
        };
        listenerDbStop = new Listener(Listener.EREIGNIS_MEDIA_DB_STOP, MediaDialogController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                // neue DB liegt vor
                txtSearch.setDisable(false);
            }
        };
        init(rootPane, true);
    }

    @Override
    public void make() {
        stage = getStage();

        paneMedia = new PaneMedia(stage);
        paneAbo = new PaneAbo(stage);
        paneMedia.make();
        paneAbo.make();

        initPanel();

        final ToggleGroup tg = new ToggleGroup();
        rbMedien.setToggleGroup(tg);
        rbAbos.setToggleGroup(tg);

        Listener.addListener(listenerDbStart);
        Listener.addListener(listenerDbStop);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            Filter.checkPattern1(txtSearch);
            filter();
        });

        txtSearch.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                String sel = txtSearch.getSelectedText();
                txtSearch.setText(sel);
            }
        });

        btnReset.setGraphic(new ProgIcons().ICON_BUTTON_RESET);
        btnReset.setTooltip(new Tooltip("Suchtext wieder herstellen"));
        btnReset.setOnAction(a -> txtSearch.setText(searchStr));
        btnOk.setOnAction(a -> close());

        rbMedien.setSelected(true);
        rbMedien.setOnAction(a -> {
            paneMedia.toFront();
            filter();
        });
        rbAbos.setOnAction(a -> {
            paneAbo.toFront();
            filter();
        });

        filter();
    }

    public void close() {
        Listener.removeListener(listenerDbStart);
        Listener.removeListener(listenerDbStop);

        paneMedia.mediaPaneClose();

        progData.erledigteAbos.filteredListClearPred();
        progData.mediaDataList.filterdListSetPredFalse();
        super.close();
    }

    private void initPanel() {
        try {
            vBoxDialog.setPadding(new Insets(10));
            vBoxDialog.setSpacing(20);
            rootPane.getChildren().addAll(vBoxDialog);

            AnchorPane.setLeftAnchor(vBoxDialog, 0.0);
            AnchorPane.setBottomAnchor(vBoxDialog, 0.0);
            AnchorPane.setRightAnchor(vBoxDialog, 0.0);
            AnchorPane.setTopAnchor(vBoxDialog, 0.0);

            VBox.setVgrow(vBoxCont, Priority.ALWAYS);
            vBoxCont.getStyleClass().add("dialog-border");
            vBoxCont.setSpacing(10);

            HBox hBox = new HBox(10);
            HBox.setHgrow(txtSearch, Priority.ALWAYS);
            hBox.getChildren().addAll(txtSearch, btnReset);
            vBoxCont.getChildren().add(hBox);

            final ToggleGroup group = new ToggleGroup();
            rbMedien.setToggleGroup(group);
            rbAbos.setToggleGroup(group);
            hBox = new HBox(20);
            hBox.getChildren().addAll(rbMedien, rbAbos);
            vBoxCont.getChildren().add(hBox);

            // Stackpane
            paneMedia.setFitToHeight(true);
            paneMedia.setFitToWidth(true);
            paneAbo.setFitToHeight(true);
            paneAbo.setFitToWidth(true);

            stackPane.getChildren().addAll(paneMedia, paneAbo);
            VBox.setVgrow(stackPane, Priority.ALWAYS);
            vBoxCont.getChildren().add(stackPane);
            paneMedia.toFront();

            Button btnHelp = PButton.helpButton(stage,
                    "Suche in der Mediensammlung", HelpText.SEARCH_MEDIA_DIALOG);

            hBox = new HBox(10);
            hBox.getChildren().addAll(btnHelp, PGuiTools.getHBoxGrower(), btnOk);

            vBoxDialog.getChildren().addAll(vBoxCont, hBox);
        } catch (final Exception ex) {
            PLog.errorLog(951203030, ex);
        }
    }

    private void filter() {
        final String searchStr = txtSearch.getText().toLowerCase().trim();
        if (rbMedien.isSelected()) {
            paneMedia.filter(searchStr);
        } else {
            paneAbo.filter(searchStr);
        }
    }
}
