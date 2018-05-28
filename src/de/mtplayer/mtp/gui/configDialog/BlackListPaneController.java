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

package de.mtplayer.mtp.gui.configDialog;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.HelpText;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.ToggleSwitch;

import java.util.ArrayList;
import java.util.Collection;

public class BlackListPaneController extends AnchorPane {

    private final ProgData progData;
    private final VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");
    private final ScrollPane scrollPane = new ScrollPane();
    private final Slider slSize = new Slider();
    private final Label lblSize = new Label("");
    private final Slider slDays = new Slider();
    private final Label lblDays = new Label("");

    BooleanProperty accordionProp = ProgConfig.CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    IntegerProperty propSize = ProgConfig.SYSTEM_BLACKLIST_FILMSIZE.getIntegerProperty();
    IntegerProperty propDay = ProgConfig.SYSTEM_BLACKLIST_SHOW_ONLY_DAYS.getIntegerProperty();
    BooleanProperty propGeo = ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_GEO.getBooleanProperty();
    BooleanProperty propAbo = ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.getBooleanProperty();
    BooleanProperty propFuture = ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_FUTURE.getBooleanProperty();

    private final int SIZE_MAX = 100;
    private final int FILTER_DAYS_MAX = 150;


    public BlackListPaneController() {
        progData = ProgData.getInstance();

        cbxAccordion.selectedProperty().bindBidirectional(accordionProp);
        cbxAccordion.selectedProperty().addListener((observable, oldValue, newValue) -> setAccordion());

        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        hBox.getChildren().addAll(cbxAccordion, scrollPane);
        getChildren().addAll(hBox);

        accordion.setPadding(new Insets(1));
        noaccordion.setPadding(new Insets(1));
        noaccordion.setSpacing(1);

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
        makeBlack(result);
        new BlackPane().makeBlackTable(result);
        return result;
    }

    private void makeBlack(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        TitledPane tpConfig = new TitledPane("Blacklist allgemein", gridPane);
        result.add(tpConfig);

        final ToggleSwitch tglAbo = new ToggleSwitch("die Blacklist beim Suchen der Abos berücksichtigen");
        tglAbo.setMaxWidth(Double.MAX_VALUE);
        tglAbo.selectedProperty().bindBidirectional(propAbo);

        final Button btnHelp = new Button("");
        btnHelp.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> new MTAlert().showHelpAlert("Blacklist",
                HelpText.BLACKLIST_ABO));


        final ToggleSwitch tglFuture = new ToggleSwitch("Filme mit Datum in der Zukunft nicht anzeigen");
        tglFuture.setMaxWidth(Double.MAX_VALUE);
        tglFuture.selectedProperty().bindBidirectional(propFuture);

        final Button btnHelpFuture = new Button("");
        btnHelpFuture.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelpFuture.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpFuture.setOnAction(a -> new MTAlert().showHelpAlert("Blacklist",
                HelpText.BLACKLIST_FUTURE));


        final ToggleSwitch tglGeo = new ToggleSwitch("Filme, die per Geoblocking gesperrt sind, nicht anzeigen");
        tglGeo.setMaxWidth(Double.MAX_VALUE);
        tglGeo.selectedProperty().bindBidirectional(propGeo);

        final Button btnHelpGeo = new Button("");
        btnHelpGeo.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelpGeo.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpGeo.setOnAction(a -> new MTAlert().showHelpAlert("Blacklist",
                HelpText.BLACKLIST_GEO));


        initDays();

        final Button btnHelpSize = new Button("");
        btnHelpSize.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelpSize.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpSize.setOnAction(a -> new MTAlert().showHelpAlert("Blacklist",
                HelpText.BLACKLIST_SIZE));


        final Button btnHelpDays = new Button("");
        btnHelpDays.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelpDays.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpDays.setOnAction(a -> new MTAlert().showHelpAlert("Blacklist",
                HelpText.BLACKLIST_DAYS));


        gridPane.add(tglAbo, 0, 0);
        gridPane.add(btnHelp, 2, 0);


        gridPane.add(tglFuture, 0, 1);
        gridPane.add(btnHelpFuture, 2, 1);
        gridPane.add(tglGeo, 0, 2);
        gridPane.add(btnHelpGeo, 2, 2);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().addAll(new Label("kurze Filme laden:"), slSize);
        gridPane.add(vBox, 0, 3);
        GridPane.setValignment(lblSize, VPos.BOTTOM);
        gridPane.add(lblSize, 1, 3);

        gridPane.add(btnHelpSize, 2, 3);

        vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().addAll(new Label("nur aktuelle Filme laden:"), slDays);
        gridPane.add(vBox, 0, 4);
        GridPane.setValignment(lblDays, VPos.BOTTOM);
        gridPane.add(lblDays, 1, 4);

        gridPane.add(btnHelpDays, 2, 4);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
    }

    private void initDays() {
        slSize.setMin(0);
        slSize.setMax(SIZE_MAX);
        slSize.setShowTickLabels(false);
        slSize.setMajorTickUnit(10);
        slSize.setBlockIncrement(10);

        slSize.valueProperty().bindBidirectional(propSize);
        slSize.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider());

        slDays.setMin(0);
        slDays.setMax(FILTER_DAYS_MAX);
        slDays.setShowTickLabels(false);
        slDays.setMajorTickUnit(10);
        slDays.setBlockIncrement(10);

        slDays.valueProperty().bindBidirectional(propDay);
        slDays.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider());
        setValueSlider();
    }

    private void setValueSlider() {
        int min = (int) slSize.getValue();
        lblSize.setText(min == 0 ? "alles laden" : "nur Filme, länger als " + min + " Minuten laden");

        min = (int) slDays.getValue();
        lblDays.setText(min == 0 ? "alles laden" : "nur Filme der letzten " + min + " Tage laden");
    }
}