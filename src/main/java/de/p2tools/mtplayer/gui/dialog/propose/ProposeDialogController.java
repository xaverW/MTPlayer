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

package de.p2tools.mtplayer.gui.dialog.propose;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ProposeDialogController extends PDialogExtra {

    private final TabPane tabPane = new TabPane();
    private final Label lblHeader = new Label("Filme vorschlagen");
    private final ProgData progData;

    private PaneProposeList paneProposeList;
    private PaneCleaningList paneCleaningList;
    private PanePropFilmList panePropFilmList;

    public ProposeDialogController(ProgData progData, StringProperty conf) {
        super(progData.primaryStage, conf, "Download weiterführen",
                true, false, DECO.BORDER_SMALL);
        this.progData = progData;

        init(true);
    }

    @Override
    public void make() {
        getHBoxTitle().getChildren().add(lblHeader);

        final Button btnOk = new Button("_Ok");
        final Button btnHelp = P2Button.helpButton(getStage(), "Medien", HelpText.MEDIA_CLEANING_CONFIG_DIALOG);
        btnOk.setOnAction(a -> quit());
        addOkButton(btnOk);
        addHlpButton(btnHelp);

        getVBoxCont().setPadding(new Insets(0));
        getVBoxCont().getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        initPanel();
    }

    @Override
    public void close() {
        paneProposeList.close();
        paneCleaningList.close();
        panePropFilmList.close();
        super.close();
    }

    private void initPanel() {
        try {
            Tab tabFilmlist;
            Tab tabProposeList;
            Tab tabCleaningList;

            this.panePropFilmList = new PanePropFilmList(progData, getStage());
            tabFilmlist = new Tab("Filme");
            tabFilmlist.setClosable(false);
            tabFilmlist.setContent(panePropFilmList.makePane());
            tabPane.getTabs().add(tabFilmlist);

            this.paneProposeList = new PaneProposeList(progData, getStage());
            tabProposeList = new Tab("Vorschläge");
            tabProposeList.setClosable(false);
            tabProposeList.setContent(paneProposeList.makePane());
            tabPane.getTabs().add(tabProposeList);

            this.paneCleaningList = new PaneCleaningList(getStage(), true);
            tabCleaningList = new Tab("Cleaning Liste");
            tabCleaningList.setClosable(false);
            tabCleaningList.setContent(paneCleaningList.makePane());
            tabPane.getTabs().add(tabCleaningList);
        } catch (final Exception ex) {
            PLog.errorLog(894210365, ex);
        }
    }

    private void quit() {
        close();
    }
}
