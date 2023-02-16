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

package de.p2tools.mtplayer.gui.configDialog.setData;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.SetData;
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

public class SetPaneController extends AnchorPane {

    private final Accordion accordion = new Accordion();
    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private final ArrayList<TitledPane> titledPanes = new ArrayList<>();

    private final SetPaneName setPaneName;
    private final SetPaneFunction setPaneFunction;
    private final SetPaneDestination setPaneDestination;
    private final SetPaneDownload setPaneDownload;
    private final SetPaneProgram setPaneProgram;
    private final SetPaneSetList setPaneSetList;

    private final ObjectProperty<SetData> setDataObjectProperty = new SimpleObjectProperty<>(null);

    public SetPaneController(Stage stage) {
        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        setPaneName = new SetPaneName(stage, setDataObjectProperty);
        setPaneFunction = new SetPaneFunction(stage, setDataObjectProperty);
        setPaneDestination = new SetPaneDestination(stage, setDataObjectProperty);
        setPaneDownload = new SetPaneDownload(stage, setDataObjectProperty);
        setPaneProgram = new SetPaneProgram(stage, setDataObjectProperty);
        setPaneSetList = new SetPaneSetList(stage, setDataObjectProperty);
        setPaneName.makePane(titledPanes);
        setPaneFunction.makePane(titledPanes);
        setPaneDestination.makePane(titledPanes);
        setPaneDownload.makePane(titledPanes);
        setPaneProgram.makePane(titledPanes);

        accordion.getPanes().addAll(titledPanes);
        scrollPane.setContent(accordion);
        splitPane.getItems().addAll(setPaneSetList, scrollPane);
        splitPane.getItems().get(0).autosize();
        SplitPane.setResizableWithParent(setPaneSetList, Boolean.FALSE);
        getChildren().addAll(splitPane);

        splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.CONFIG_DIALOG_SET_DIVIDER);
    }

    public void close() {
        splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.CONFIG_DIALOG_SET_DIVIDER);
        setPaneName.close();
        setPaneFunction.close();
        setPaneDestination.close();
        setPaneDownload.close();
        setPaneProgram.close();
        setPaneSetList.close();
    }

    public Optional<SetData> getSel() {
        return setPaneSetList.getSel();
    }
}