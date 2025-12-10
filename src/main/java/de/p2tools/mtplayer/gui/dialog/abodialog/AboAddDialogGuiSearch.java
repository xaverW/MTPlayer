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
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AboAddDialogGuiSearch extends VBox {

    private final AddAboDto addAboDto;
    private final ProgData progData;
    private final Stage stage;

    public AboAddDialogGuiSearch(ProgData progData, Stage stage, AddAboDto addAboDto) {
        //hier wird ein neues Abo angelegt -> Button, abo ist immer neu
        this.progData = progData;
        this.stage = stage;
        this.addAboDto = addAboDto;

        addCont();
        init();
    }

    private void addCont() {
        // Grid
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSizeCenter());

        getChildren().add(gridPane);

        int row = 0;

        // Sender
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_CHANNEL + ":"), 0, ++row);
        gridPane.add(addAboDto.mbChannel, 1, row);
        gridPane.add(addAboDto.chkChannelAll, 2, row);
        GridPane.setHgrow(addAboDto.mbChannel, Priority.ALWAYS);
        addAboDto.mbChannel.setMaxWidth(Double.MAX_VALUE);

        // Thema
        setTextArea(addAboDto.textAreaTheme);
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_THEME + ":"), 0, ++row);
        gridPane.add(addAboDto.textAreaTheme, 1, row);
        gridPane.add(addAboDto.chkThemeAll, 2, row);

        // Thema-Exakt
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_THEME_EXACT + ":"), 0, ++row);
        gridPane.add(addAboDto.chkThemeExact, 1, row);
        gridPane.add(addAboDto.chkThemeExactAll, 2, row);

        // Thema-Titel
        setTextArea(addAboDto.textAreaThemeTitle);
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_THEME_TITLE + ":"), 0, ++row);
        gridPane.add(addAboDto.textAreaThemeTitle, 1, row);
        gridPane.add(addAboDto.chkThemeTitleAll, 2, row);

        // Titel
        setTextArea(addAboDto.textAreaTitle);
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_TITLE + ":"), 0, ++row);
        gridPane.add(addAboDto.textAreaTitle, 1, row);
        gridPane.add(addAboDto.chkTitleAll, 2, row);

        // Irgendwo
        setTextArea(addAboDto.textAreaSomewhere);
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_SOMEWHERE + ":"), 0, ++row);
        gridPane.add(addAboDto.textAreaSomewhere, 1, row);
        gridPane.add(addAboDto.chkSomewhereAll, 2, row);
    }

    private void init() {
        AboAddAllFactory.init(addAboDto);
    }

    private void setTextArea(TextArea textArea) {
        textArea.setWrapText(true);
        textArea.setPrefRowCount(2);
        textArea.setPrefColumnCount(1);
    }
}
