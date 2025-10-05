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

package de.p2tools.mtplayer.gui.configdialog.panesetdata;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

public class ControllerSet extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();

    private final PaneSetName paneSetName;
    private final PaneSetFunction paneSetFunction;
    private final PaneSetDestination paneSetDestination;
    private final PaneSetDownload paneSetDownload;
    private final PaneSetProgram paneSetProgram;
    private final PaneSetList paneSetList;

    public ControllerSet(Stage stage) {
        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);

        ObjectProperty<SetData> setDataObjectProperty = new SimpleObjectProperty<>(null);
        paneSetName = new PaneSetName(stage, setDataObjectProperty);
        paneSetFunction = new PaneSetFunction(stage, setDataObjectProperty);
        paneSetDestination = new PaneSetDestination(stage, setDataObjectProperty);
        paneSetDownload = new PaneSetDownload(stage, setDataObjectProperty);
        paneSetProgram = new PaneSetProgram(stage, setDataObjectProperty);
        paneSetList = new PaneSetList(stage, setDataObjectProperty);

        ArrayList<TitledPane> titledPanes = new ArrayList<>();
        paneSetName.makePane(titledPanes);
        paneSetFunction.makePane(titledPanes);
        paneSetDestination.makePane(titledPanes);
        paneSetDownload.makePane(titledPanes);
        paneSetProgram.makePane(titledPanes);

        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(titledPanes);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(accordion);
        splitPane.getItems().addAll(paneSetList, scrollPane);
        splitPane.getItems().get(0).autosize();
        SplitPane.setResizableWithParent(paneSetList, Boolean.FALSE);
        getChildren().addAll(splitPane);

        splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.CONFIG_DIALOG_SET_DIVIDER);
    }

    public void close() {
        splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.CONFIG_DIALOG_SET_DIVIDER);
        paneSetName.close();
        paneSetFunction.close();
        paneSetDestination.close();
        paneSetDownload.close();
        paneSetProgram.close();
        paneSetList.close();
    }

    public Optional<SetData> getSel() {
        return paneSetList.getSel(true);
    }
}