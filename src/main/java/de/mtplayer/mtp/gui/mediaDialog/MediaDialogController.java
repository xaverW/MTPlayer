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
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MediaDialogController extends PDialogExtra {

    private final TextField txtSearch = new TextField();
    private final Button btnOk = new Button("_Ok");
    private final Button btnReset = new Button("");

    private final RadioButton rbMedien = new RadioButton("Mediensammlung");
    private final RadioButton rbAbos = new RadioButton("erledigte Abos");

    private final StackPane stackPane = new StackPane();
    private final BooleanProperty propSearchMedia = ProgConfig.SYSTEM_MEDIA_DIALOG_SEARCH_MEDIA;

    private PaneMedia paneMedia;
    private PaneAbo paneAbo;
    private final Listener listenerDbStart;
    private final Listener listenerDbStop;
    private final String searchStr;

    private final ProgData progData = ProgData.getInstance();

    public MediaDialogController(String searchStr) {
        super(ProgData.getInstance().primaryStage, ProgConfig.MEDIA_DIALOG_SIZE.getStringProperty(), "Mediensammlung",
                true, false);

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

        init(true);
    }

    @Override
    public void make() {
        initPanel();

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

        final ToggleGroup group = new ToggleGroup();
        rbMedien.setToggleGroup(group);
        rbAbos.setToggleGroup(group);
        rbMedien.selectedProperty().bindBidirectional(propSearchMedia);
        rbMedien.setOnAction(a -> {
            setPane();
            filter();
        });
        rbAbos.setSelected(!propSearchMedia.get());
        rbAbos.setOnAction(a -> {
            setPane();
            filter();
        });
        setPane();
        filter();
    }

    public void close() {
        rbMedien.selectedProperty().unbindBidirectional(propSearchMedia);
        Listener.removeListener(listenerDbStart);
        Listener.removeListener(listenerDbStop);

        paneAbo.close();
        paneMedia.close();

        progData.erledigteAbos.filterdListSetPredFalse();
        progData.mediaDataList.filterdListSetPredFalse();
        super.close();
    }

    private void initPanel() {
        try {
            HBox hBox = new HBox(10);
            HBox.setHgrow(txtSearch, Priority.ALWAYS);
            hBox.getChildren().addAll(txtSearch, btnReset);
            getvBoxCont().getChildren().add(hBox);

            hBox = new HBox(20);
            hBox.getChildren().addAll(rbMedien, rbAbos);
            getvBoxCont().getChildren().add(hBox);

            // Stackpane
            paneMedia = new PaneMedia(getStage());
            paneMedia.make();
            paneMedia.setFitToHeight(true);
            paneMedia.setFitToWidth(true);

            paneAbo = new PaneAbo(getStage());
            paneAbo.make();
            paneAbo.setFitToHeight(true);
            paneAbo.setFitToWidth(true);

            stackPane.getChildren().addAll(paneMedia, paneAbo);
            VBox.setVgrow(stackPane, Priority.ALWAYS);
            getvBoxCont().getChildren().add(stackPane);

            Button btnHelp = PButton.helpButton(getStage(),
                    "Suche in der Mediensammlung", HelpText.SEARCH_MEDIA_DIALOG);
            addOkButton(btnOk);
            addHlpButton(btnHelp);
        } catch (final Exception ex) {
            PLog.errorLog(951203030, ex);
        }
    }

    private void setPane() {
        if (rbMedien.isSelected()) {
            paneMedia.toFront();
        } else {
            paneAbo.toFront();
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
