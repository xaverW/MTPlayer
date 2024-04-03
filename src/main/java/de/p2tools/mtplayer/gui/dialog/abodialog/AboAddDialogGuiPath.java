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
import de.p2tools.mtplayer.controller.data.abo.AboFieldNames;
import de.p2tools.mtplayer.gui.dialog.downloadadd.DownloadAddDialogFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
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
        // Grid
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSizeCenter());

        vBoxCont.getChildren().add(gridPane);

        int row = 0;

        // Zielpfad
        final Button btnHelp = P2Button.helpButton(stage, "Unterordner anlegen",
                HelpText.ABO_SUBDIR);

        addAboDto.cboDestination.setMaxWidth(Double.MAX_VALUE);
        addAboDto.cboDestination.setEditable(true);

        final StackPane sp = new StackPane();
        sp.getChildren().addAll(addAboDto.lblDestination, addAboDto.cboDestination);
        sp.setPrefWidth(20);

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().addAll(addAboDto.chkDestination, sp, btnHelp);
        HBox.setHgrow(sp, Priority.ALWAYS);

        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_DEST_DIR + ":"), 0, ++row);
        gridPane.add(hbox, 1, row);
        gridPane.add(addAboDto.chkDestinationAll, 2, row);

        // ProgrammSet -> mind. 1 Set gibts immer, Kontrolle oben bereits
        gridPane.add(addAboDto.textSet, 0, ++row);
        addAboDto.cboSetData.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(addAboDto.cboSetData, 1, row);
        gridPane.add(addAboDto.chkSetAll, 2, row);
    }

    public void init() {
        AboAddAllFactory.init(addAboDto);
    }

    private void setTextArea(TextArea textArea) {
        textArea.setWrapText(true);
        textArea.setPrefRowCount(2);
        textArea.setPrefColumnCount(1);
    }
}
