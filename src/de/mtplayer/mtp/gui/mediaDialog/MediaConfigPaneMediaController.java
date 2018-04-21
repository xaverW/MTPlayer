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

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Collection;

public class MediaConfigPaneMediaController extends AnchorPane {

    private final Daten daten;
    VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");
    private final BooleanProperty accordionProp = Config.MEDIA_CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    ScrollPane scrollPane = new ScrollPane();

    BooleanProperty prefSuff = Config.MEDIA_DB_WITH_OUT_SUFFIX.getBooleanProperty();
    StringProperty prefSuffStr = Config.MEDIA_DB_SUFFIX.getStringProperty();

    public MediaConfigPaneMediaController() {
        daten = Daten.getInstance();

        cbxAccordion.selectedProperty().bindBidirectional(accordionProp);
        cbxAccordion.selectedProperty().addListener((observable, oldValue, newValue) -> setAccordion());

        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        hBox.getChildren().addAll(cbxAccordion, scrollPane);
        getChildren().addAll(hBox);

        AnchorPane.setLeftAnchor(hBox, 10.0);
        AnchorPane.setBottomAnchor(hBox, 10.0);
        AnchorPane.setRightAnchor(hBox, 10.0);
        AnchorPane.setTopAnchor(hBox, 10.0);

        setAccordion();
    }

    private void setAccordion() {
        if (cbxAccordion.isSelected()) {
            noaccordion.getChildren().clear();
            accordion.getPanes().addAll(createPanes());
            scrollPane.setContent(accordion);
        } else {
            accordion.getPanes().clear();
            noaccordion.getChildren().addAll(createPanes());
            scrollPane.setContent(noaccordion);
        }
    }

    private Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        makeConfig(result);
        new MediaConfigPanePath().makeTable(result);
        new MediaConfigPanePathExtern().make(result);
        return result;
    }

    private void makeConfig(Collection<TitledPane> result) {
        VBox vBox = new VBox();

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        TitledPane tpConfig = new TitledPane("Allgemein", vBox);
        result.add(tpConfig);

        final RadioButton rbWithOutSuff = new RadioButton("Keine Dateien mit diesem Suffix (z.B.: txt,xml,jpg");
        final RadioButton rbWithSuff = new RadioButton("Nur Dateien mit diesem Suffix  (z.B.: mp4,flv,m4v");

        final Button btnHelp = new Button("");
        btnHelp.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> new MTAlert().showHelpAlert("Mediensammlungen verwalten",
                HelpText.MEDIA_COLLECTION));


        final ToggleGroup tg = new ToggleGroup();
        rbWithOutSuff.setToggleGroup(tg);
        rbWithSuff.setToggleGroup(tg);

        rbWithSuff.setSelected(!prefSuff.getValue());
        rbWithOutSuff.selectedProperty().bindBidirectional(prefSuff);

        TextField txtSuff = new TextField();
        txtSuff.textProperty().bindBidirectional(prefSuffStr);


        gridPane.add(rbWithOutSuff, 0, 0);
        gridPane.add(btnHelp, 1, 0);
        gridPane.add(rbWithSuff, 0, 1);
        gridPane.add(txtSuff, 0, 2);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSize());

        vBox.getChildren().addAll(gridPane);
    }

}
