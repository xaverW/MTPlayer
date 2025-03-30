/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.gui.dialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.P2DirFileChooser;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class DownloadSubtitleDialog extends P2DialogExtra {

    private final ProgData progData;
    private final Button btnOk = new Button("_Ok");
    private final Button btnCancel = new Button("_Abbrechen");
    private final TextField txtPath = new TextField();
    private final TextField txtFileName = new TextField();
    private final StringProperty pathProp;
    private final StringProperty nameProp;
    private final BooleanProperty okProp;
    private final boolean subtitel;

    public DownloadSubtitleDialog(ProgData progData, boolean subtitel,
                                  StringProperty pathProp, StringProperty nameProp, BooleanProperty okProp) {

        super(progData.primaryStage, ProgConfig.DOWNLOAD_SUBTITLE_DIALOG_SIZE,
                "Untertitel laden", true, false, DECO.BORDER_SMALL, true);
        this.subtitel = subtitel;
        this.progData = progData;
        this.pathProp = pathProp;
        this.nameProp = nameProp;
        this.okProp = okProp;
        okProp.set(false);

        initDialog();
        init(false);
    }

    @Override
    public void close() {
        txtPath.textProperty().unbindBidirectional(pathProp);
        txtFileName.textProperty().unbindBidirectional(nameProp);
        super.close();
    }

    private void initDialog() {
        VBox vBox = getVBoxCont();
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(P2LibConst.PADDING));
        addPathName(vBox);

        txtPath.textProperty().bindBidirectional(pathProp);
        txtFileName.textProperty().bindBidirectional(nameProp);

        addOkCancelButtons(btnOk, btnCancel);
        btnOk.setOnAction(a -> {
            okProp.set(true);
            close();
        });
        btnCancel.setOnAction(a -> close());
    }

    private void addPathName(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        vBox.getChildren().add(gridPane);

        final Button btnFile = new Button();
        btnFile.setTooltip(new Tooltip("Ordner zum Speichern auswählen"));
        btnFile.setOnAction(event -> {
            P2DirFileChooser.DirChooser(getStage(), txtPath);
        });
        btnFile.setGraphic(ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());

        int row = 0;
        gridPane.add(new Label("Pfad"), 0, row);
        gridPane.add(txtPath, 0, ++row);
        gridPane.add(btnFile, 1, row);

        gridPane.add(new Label("Dateiname"), 0, ++row);
        gridPane.add(txtFileName, 0, ++row);
        if (subtitel) {
            gridPane.add(new Label(".srt  .ttml"), 1, row);
        } else {
            gridPane.add(new Label(".txt"), 1, row);
        }
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize());
    }
}
