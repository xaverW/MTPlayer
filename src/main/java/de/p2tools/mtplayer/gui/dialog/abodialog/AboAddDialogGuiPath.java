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

package de.p2tools.mtplayer.gui.dialog.abodialog;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.abo.AboFieldNames;
import de.p2tools.mtplayer.gui.dialog.downloadadd.DownloadAddDialogFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.P2DirFileChooser;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AboAddDialogGuiPath {

    private final AddAboDto addAboDto;
    private final ProgData progData;
    private final VBox vBoxCont;
    private final Stage stage;

    public AboAddDialogGuiPath(ProgData progData, Stage stage, AddAboDto addAboDto, VBox vBoxCont) {
        //hier wird ein neues Abo angelegt -> Button, abo ist immer neu
        this.progData = progData;
        this.stage = stage;
        this.addAboDto = addAboDto;
        this.vBoxCont = vBoxCont;
    }

    public void addCont() {
        addContSet();
        addContPath();
        addContFileName();
        addResFileName();

        final Button btnHelp = P2Button.helpButton(stage, "Abo-Pfad anlegen",
                HelpText.ABO_PATH);
        HBox hBoxHelp = new HBox();
        hBoxHelp.setPadding(new Insets(10));
        hBoxHelp.setAlignment(Pos.CENTER_RIGHT);
        hBoxHelp.getChildren().add(btnHelp);
        vBoxCont.getChildren().addAll(P2GuiTools.getVBoxGrower(), hBoxHelp);
    }

    private void addContSet() {
        // Grid
        final GridPane grid = new GridPane();
        grid.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        grid.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        grid.setMinWidth(Control.USE_PREF_SIZE);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSizeCenter());
        vBoxCont.getChildren().add(grid);

        int row = 0;

        // ProgrammSet -> mind. 1 Set gibts immer, Kontrolle oben bereits
        grid.add(addAboDto.textSet, 0, row);
        addAboDto.cboSetData.setMaxWidth(Double.MAX_VALUE);
        grid.add(addAboDto.cboSetData, 1, row);
        grid.add(addAboDto.chkSetAll, 2, row);
    }

    private void addContPath() {
        Text txtPath = DownloadAddDialogFactory.getText("Pfad:");
        txtPath.setUnderline(true);
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(20, 10, 10, 10));
        hBox.getChildren().add(txtPath);
        vBoxCont.getChildren().add(hBox);

        // Grid
        final GridPane grid = new GridPane();
        grid.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        grid.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        grid.setMinWidth(Control.USE_PREF_SIZE);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setPadding(new Insets(0, 10, 0, 10));
        grid.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSizeCenter());
        vBoxCont.getChildren().add(grid);


        int row = 0;
        // Set
        addAboDto.cboAboSubDir.setMaxWidth(Double.MAX_VALUE);
        addAboDto.cboAboSubDir.setEditable(true);

        final StackPane sp = new StackPane();
        sp.setAlignment(Pos.CENTER_LEFT);
        sp.getChildren().addAll(addAboDto.lblSetSubDir, addAboDto.cboAboSubDir);
        sp.setPrefWidth(20);
        sp.disableProperty().bind(addAboDto.rbSetPath.selectedProperty().not());

        HBox hBoxChk = new HBox(10);
        hBoxChk.disableProperty().bind(addAboDto.rbSetPath.selectedProperty().not());
        hBoxChk.setAlignment(Pos.CENTER_LEFT);
        hBoxChk.getChildren().addAll(addAboDto.chkAboSubDir, DownloadAddDialogFactory.getTextBlack(AboFieldNames.ABO_DEST_SET_SUB_DIR + ":"));
        HBox.setHgrow(sp, Priority.ALWAYS);

        HBox hBoxSet = new HBox(10);
        hBoxSet.disableProperty().bind(addAboDto.rbSetPath.selectedProperty().not());
        hBoxSet.setAlignment(Pos.CENTER_LEFT);
        hBoxSet.getChildren().addAll(sp);
        HBox.setHgrow(sp, Priority.ALWAYS);

        grid.add(addAboDto.rbSetPath, 0, row, 3, 1);
        grid.add(addAboDto.chkDestAboDirAll, 4, row);
        RadioButton rb = new RadioButton();
        rb.setVisible(false);
        grid.add(rb, 0, ++row);
        grid.add(hBoxChk, 1, row);
        grid.add(sp, 2, row, 2, 1);


        // eigenen Einstellungen
        final Button btnPath = new Button();
        btnPath.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen."));
        btnPath.setGraphic(ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnPath.setOnAction(event -> {
            P2DirFileChooser.DirChooser(stage, addAboDto.cboAboDir);
            addAboDto.initDestination.setPathToAbo();
        });
        btnPath.disableProperty().bind(addAboDto.rbOwnPath.selectedProperty().not());

        grid.add(addAboDto.rbOwnPath, 0, ++row, 3, 1);
        grid.add(DownloadAddDialogFactory.getTextBlack(AboFieldNames.ABO_DEST_ABO_DIR + ":"), 1, ++row);
        grid.add(addAboDto.cboAboDir, 2, row);
        grid.add(btnPath, 3, row);
        addAboDto.cboAboDir.setMaxWidth(Double.MAX_VALUE);
        addAboDto.cboAboDir.setEditable(true);
        addAboDto.cboAboDir.disableProperty().bind(addAboDto.rbOwnPath.selectedProperty().not());
        GridPane.setHgrow(addAboDto.cboAboDir, Priority.ALWAYS);
    }

    private void addContFileName() {
        Text txtFileName = DownloadAddDialogFactory.getText("Dateiname:");
        txtFileName.setUnderline(true);
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(30, 10, 10, 10));
        hBox.getChildren().add(txtFileName);
        vBoxCont.getChildren().add(hBox);

        // Grid
        final GridPane grid = new GridPane();
        grid.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        grid.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        grid.setMinWidth(Control.USE_PREF_SIZE);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setPadding(new Insets(0, 10, 10, 25));
        grid.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSizeCenter());
        vBoxCont.getChildren().add(grid);

        addAboDto.cboAboFileName.disableProperty().bind(addAboDto.rbOwnFileName.selectedProperty().not());

        int row = 0;
        // set
        grid.add(addAboDto.rbSetFileName, 0, row, 3, 1);

        RadioButton rb = new RadioButton();
        rb.setVisible(false);
        grid.add(rb, 0, ++row);
        grid.add(DownloadAddDialogFactory.getTextBlack(AboFieldNames.ABO_DEST_ABO_FILE_NAME + ":"), 1, row);
        grid.add(addAboDto.lblSetFileName, 2, row);

        grid.add(addAboDto.chkDestAboFileNameAll, 3, row);
        ++row;

        // eigenen Einstellungen
        grid.add(addAboDto.rbOwnFileName, 0, ++row, 3, 1);

        rb = new RadioButton();
        rb.setVisible(false);
        grid.add(rb, 0, ++row);
        grid.add(DownloadAddDialogFactory.getTextBlack(AboFieldNames.ABO_DEST_ABO_FILE_NAME + ":"), 1, row);
        grid.add(addAboDto.cboAboFileName, 2, row);
        addAboDto.cboAboFileName.setMaxWidth(Double.MAX_VALUE);
        addAboDto.cboAboFileName.setEditable(true);
        GridPane.setHgrow(addAboDto.cboAboFileName, Priority.ALWAYS);
    }

    private void addResFileName() {
        Text txtFileName = DownloadAddDialogFactory.getTextBig("Ergebnis:");
        txtFileName.setUnderline(true);
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(30, 10, 10, 10));
        hBox.getChildren().add(txtFileName);
        vBoxCont.getChildren().add(hBox);

        // Grid
        final GridPane grid = new GridPane();
        grid.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        grid.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        grid.setMinWidth(Control.USE_PREF_SIZE);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setPadding(new Insets(0, 10, 10, 10));
        grid.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow());
        vBoxCont.getChildren().add(grid);

        Text txtPath = DownloadAddDialogFactory.getTextBlack("Pfad:");
        Text txtName = DownloadAddDialogFactory.getTextBlack("Dateiname:");

        int row = 0;
        RadioButton rb = new RadioButton();
        rb.setVisible(false);
        grid.add(rb, 0, row);

        grid.add(txtPath, 1, row);
        grid.add(addAboDto.lblResPath, 2, row);
        grid.add(txtName, 1, ++row);
        grid.add(addAboDto.lblResFileName, 2, row);
    }

    public void init() {
        AboAddAllFactory.init(addAboDto);
    }
}
