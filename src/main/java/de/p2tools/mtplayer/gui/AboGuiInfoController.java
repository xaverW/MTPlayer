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

package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboFieldNames;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AboGuiInfoController extends VBox {
    private final SplitPane splitPane = new SplitPane();

    private final TextArea txtInfo = new TextArea();
    private final TextField txtName = new TextField("");

    private final Label lblSender = new Label();
    private final Label lblThema = new Label();
    private final CheckBox chkExact = new CheckBox();
    private final Label lblThemaTitel = new Label();
    private final Label lblTitel = new Label();
    private final Label lblSomewhere = new Label();

    private AboData abo = null;

    public AboGuiInfoController() {
        txtName.setFont(Font.font(null, FontWeight.BOLD, -1));
        txtName.setTooltip(new Tooltip("Name des Abos"));
        txtInfo.setWrapText(true);
        txtInfo.setPrefRowCount(4);
        txtInfo.setTooltip(new Tooltip("Beschreibung des Abos"));

        final GridPane gridPaneLeft = new GridPane();
        gridPaneLeft.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPaneLeft.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPaneLeft.setPadding(new Insets(P2LibConst.DIST_EDGE));
        GridPane.setVgrow(txtInfo, Priority.ALWAYS);

        gridPaneLeft.add(new Label(AboFieldNames.ABO_NAME), 0, 0);
        gridPaneLeft.add(txtName, 1, 0);
        gridPaneLeft.add(new Label(AboFieldNames.ABO_DESCRIPTION), 0, 1);
        gridPaneLeft.add(txtInfo, 1, 1);
        gridPaneLeft.getColumnConstraints().addAll(new ColumnConstraints(), PColumnConstraints.getCcComputedSizeAndHgrow());
        VBox.setVgrow(gridPaneLeft, Priority.ALWAYS);


        final GridPane gridPaneRight = new GridPane();
        gridPaneRight.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPaneRight.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPaneRight.setPadding(new Insets(P2LibConst.DIST_EDGE));

        chkExact.setDisable(true);
        int row = 0;
        gridPaneRight.add(new Label("Sender: "), 0, row);
        gridPaneRight.add(lblSender, 1, row);
        gridPaneRight.add(new Label("Thema: "), 0, ++row);
        gridPaneRight.add(lblThema, 1, row);
        gridPaneRight.add(new Label("Exakt: "), 0, ++row);
        gridPaneRight.add(chkExact, 1, row);
        gridPaneRight.add(new Label("Thema-Titel: "), 0, ++row);
        gridPaneRight.add(lblThemaTitel, 1, row);
        gridPaneRight.add(new Label("Titel: "), 0, ++row);
        gridPaneRight.add(lblTitel, 1, row);
        gridPaneRight.add(new Label("Irgendwo: "), 0, ++row);
        gridPaneRight.add(lblSomewhere, 1, row);

        splitPane.getItems().addAll(gridPaneLeft, gridPaneRight);
        splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.ABO_GUI_INFO_DIVIDER);
        SplitPane.setResizableWithParent(gridPaneRight, false);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        setSpacing(0);
        setPadding(new Insets(0));
        getChildren().add(splitPane);
    }

    public void setAbo(AboData newAbo) {
        if (abo != null) {
            txtName.textProperty().unbindBidirectional(abo.nameProperty());
            txtInfo.textProperty().unbindBidirectional(abo.descriptionProperty());

            lblSender.textProperty().unbindBidirectional(abo.channelProperty());
            lblThema.textProperty().unbindBidirectional(abo.themeProperty());
            chkExact.selectedProperty().unbindBidirectional(abo.themeExactProperty());
            lblThemaTitel.textProperty().unbindBidirectional(abo.themeTitleProperty());
            lblTitel.textProperty().unbindBidirectional(abo.titleProperty());
            lblSomewhere.textProperty().unbindBidirectional(abo.somewhereProperty());
        }

        abo = newAbo;
        if (newAbo == null) {
            txtName.setText("");
            txtInfo.setText("");

            lblSender.setText("");
            lblThema.setText("");
            chkExact.setSelected(false);
            lblThemaTitel.setText("");
            lblTitel.setText("");
            lblSomewhere.setText("");
            return;
        }

        txtName.textProperty().bindBidirectional(abo.nameProperty());
        txtInfo.textProperty().bindBidirectional(abo.descriptionProperty());

        lblSender.textProperty().bindBidirectional(abo.channelProperty());
        lblThema.textProperty().bindBidirectional(abo.themeProperty());
        chkExact.selectedProperty().bindBidirectional(abo.themeExactProperty());
        lblThemaTitel.textProperty().bindBidirectional(abo.themeTitleProperty());
        lblTitel.textProperty().bindBidirectional(abo.titleProperty());
        lblSomewhere.textProperty().bindBidirectional(abo.somewhereProperty());
    }
}

