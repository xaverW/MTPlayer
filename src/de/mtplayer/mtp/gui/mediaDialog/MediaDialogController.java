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
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.dialog.MTDialog;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.tools.storedFilter.Filter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;

public class MediaDialogController extends MTDialog {

    private final AnchorPane rootPane = new AnchorPane();
    private final VBox vBoxDialog = new VBox();
    private final VBox vBoxCont = new VBox();

    private final TextField txtSearch = new TextField();


    private final Button btnOk = new Button("Ok");
    private final Button btnHelp = new Button();
    private final Button btnReset = new Button("");

    private final RadioButton rbMedien = new RadioButton("Mediensammlung");
    private final RadioButton rbAbos = new RadioButton("erledigte Abos");

    private final StackPane stackPane = new StackPane();

    private final MediaDialogPaneMedia mediaDialogPaneMedia = new MediaDialogPaneMedia();
    private final MediaDialogPaneAbo mediaDialogPaneAbo = new MediaDialogPaneAbo();
    private final ProgData progData = ProgData.getInstance();
    private final String searchStr;
    private final Listener listenerDbStart;
    private final Listener listenerDbStop;


    public MediaDialogController(String searchStr) {
        super(ProgConfig.MEDIA_DIALOG_SIZE, "Mediensammlung", true);
        this.searchStr = searchStr;
        txtSearch.setText(searchStr);

        listenerDbStart = new Listener(Listener.EREIGNIS_MEDIA_DB_START, MediaDialogController.class.getSimpleName()) {
            @Override
            public void ping() {
                // neue DB suchen
                txtSearch.setDisable(true);
            }
        };
        listenerDbStop = new Listener(Listener.EREIGNIS_MEDIA_DB_STOP, MediaDialogController.class.getSimpleName()) {
            @Override
            public void ping() {
                // neue DB liegt vor
                txtSearch.setDisable(false);
            }
        };
        init(rootPane, true);
    }

    public void close() {
        Listener.removeListener(listenerDbStart);
        Listener.removeListener(listenerDbStop);

        mediaDialogPaneMedia.mediaPaneClose();

        progData.erledigteAbos.filterdListClearPred();
        progData.mediaList.filterdListClearPred();
        super.close();
    }

    private void initPanel() {
        try {
            vBoxDialog.setPadding(new Insets(10));
            vBoxDialog.setSpacing(20);

            vBoxCont.getStyleClass().add("dialog-border");
            vBoxCont.setSpacing(10);
            VBox.setVgrow(vBoxCont, Priority.ALWAYS);

            rootPane.getChildren().addAll(vBoxDialog);
            AnchorPane.setLeftAnchor(vBoxDialog, 0.0);
            AnchorPane.setBottomAnchor(vBoxDialog, 0.0);
            AnchorPane.setRightAnchor(vBoxDialog, 0.0);
            AnchorPane.setTopAnchor(vBoxDialog, 0.0);


            HBox hBox = new HBox();
            hBox.setSpacing(10);
            HBox.setHgrow(txtSearch, Priority.ALWAYS);
            hBox.getChildren().addAll(txtSearch, btnReset);
            vBoxCont.getChildren().add(hBox);

            final ToggleGroup group = new ToggleGroup();
            rbMedien.setToggleGroup(group);
            rbAbos.setToggleGroup(group);
            hBox = new HBox();
            hBox.setSpacing(10);
            hBox.getChildren().addAll(rbMedien, rbAbos);
            vBoxCont.getChildren().add(hBox);

            // Stackpane
            mediaDialogPaneMedia.setFitToHeight(true);
            mediaDialogPaneMedia.setFitToWidth(true);
            mediaDialogPaneAbo.setFitToHeight(true);
            mediaDialogPaneAbo.setFitToWidth(true);

            stackPane.getChildren().addAll(mediaDialogPaneMedia, mediaDialogPaneAbo);
            VBox.setVgrow(stackPane, Priority.ALWAYS);
            vBoxCont.getChildren().add(stackPane);
            mediaDialogPaneMedia.toFront();

            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            hBox = new HBox();
            hBox.setSpacing(10);
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.getChildren().addAll(btnHelp, region, btnOk);

            vBoxDialog.getChildren().addAll(vBoxCont, hBox);
        } catch (final Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void make() {
        initPanel();
        mediaDialogPaneMedia.make();
        mediaDialogPaneAbo.make();

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

        btnReset.setGraphic(new Icons().ICON_BUTTON_RESET);
        btnReset.setOnAction(a -> txtSearch.setText(searchStr));
        btnOk.setOnAction(a -> close());
        btnHelp.setText("");
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> new MTAlert().showHelpAlert("Suche in der Mediensammlung", HelpText.SEARCH_MEDIA_DIALOG));

        rbMedien.setSelected(true);
        rbMedien.setOnAction(a -> {
            mediaDialogPaneMedia.toFront();
            filter();
        });
        rbAbos.setOnAction(a -> {
            mediaDialogPaneAbo.toFront();
            filter();
        });

        filter();
    }

    private void filter() {
        final String searchStr = txtSearch.getText().toLowerCase().trim();
        if (rbMedien.isSelected()) {
            mediaDialogPaneMedia.filter(searchStr);
        } else {
            mediaDialogPaneAbo.filter(searchStr);
        }
    }
}
