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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;


public class FilmFilterEditDialog extends PDialogExtra {

    final ProgData progData;
    IntegerProperty waitTime = ProgConfig.SYSTEM_FILTER_WAIT_TIME.getIntegerProperty();

    public FilmFilterEditDialog(ProgData progData) {
        super(progData.primaryStage, null, "Filtereinstellungen", true, true, DECO.NONE);
        this.progData = progData;

        init(true);
    }

    @Override
    public void make() {
        init(getvBoxCont());

        final Button btnHelp = PButton.helpButton(getStage(), "Filtereinstellungen",
                HelpText.GUI_FILMS_EDIT_FILTER);

        Button btnOk = new Button("_Ok");
        btnOk.setOnAction(event -> close());

        addOkButton(btnOk);
        addHlpButton(btnHelp);
    }

    public void init(VBox vBox) {
        vBox.setSpacing(15);

        PToggleSwitch tglChannel = new PToggleSwitch("Sender");
        tglChannel.setMaxWidth(Double.MAX_VALUE);
        tglChannel.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().channelVisProperty());
        vBox.getChildren().add(tglChannel);

        VBox v = new VBox(5);
        PToggleSwitch tglTheme = new PToggleSwitch("Thema");
        tglTheme.setMaxWidth(Double.MAX_VALUE);
        tglTheme.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().themeVisProperty());
        v.getChildren().add(tglTheme);

        PToggleSwitch tglThemeExact = new PToggleSwitch("  -> freie Suche mit Eingabefeld");
        tglThemeExact.disableProperty().bind(progData.storedFilters.getActFilterSettings().themeVisProperty().not());
        tglThemeExact.setMaxWidth(Double.MAX_VALUE);
        tglThemeExact.setSelected(!progData.storedFilters.getActFilterSettings().isThemeExact());
        tglThemeExact.selectedProperty().addListener((observable, oldValue, newValue) ->
                progData.storedFilters.getActFilterSettings().themeExactProperty().setValue(!newValue));
//        tglThemeExact.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().themeExactProperty());

        v.getChildren().add(tglThemeExact);
        vBox.getChildren().add(v);

        PToggleSwitch tglThemeTitle = new PToggleSwitch("Thema oder Titel");
        tglThemeTitle.setMaxWidth(Double.MAX_VALUE);
        tglThemeTitle.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().themeTitleVisProperty());
        vBox.getChildren().add(tglThemeTitle);

        PToggleSwitch tglTitle = new PToggleSwitch("Titel");
        tglTitle.setMaxWidth(Double.MAX_VALUE);
        tglTitle.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().titleVisProperty());
        vBox.getChildren().add(tglTitle);

        PToggleSwitch tglSomewhere = new PToggleSwitch("Irgendwo");
        tglSomewhere.setMaxWidth(Double.MAX_VALUE);
        tglSomewhere.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().somewhereVisProperty());
        vBox.getChildren().add(tglSomewhere);

        PToggleSwitch tglUrl = new PToggleSwitch("Url");
        tglUrl.setMaxWidth(Double.MAX_VALUE);
        tglUrl.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().urlVisProperty());
        vBox.getChildren().add(tglUrl);

        PToggleSwitch tglTimeRange = new PToggleSwitch("Zeitraum [Tage]");
        tglTimeRange.setMaxWidth(Double.MAX_VALUE);
        tglTimeRange.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().timeRangeVisProperty());
        vBox.getChildren().add(tglTimeRange);

        PToggleSwitch tglMinMax = new PToggleSwitch("Filmlänge Min/Max");
        tglMinMax.setMaxWidth(Double.MAX_VALUE);
        tglMinMax.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minMaxDurVisProperty());
        vBox.getChildren().add(tglMinMax);

        PToggleSwitch tglMinMaxTime = new PToggleSwitch("Sendezeit");
        tglMinMaxTime.setMaxWidth(Double.MAX_VALUE);
        tglMinMaxTime.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minMaxTimeVisProperty());
        vBox.getChildren().add(tglMinMaxTime);

        PToggleSwitch tglShowDate = new PToggleSwitch("Sendedatum");
        tglShowDate.setMaxWidth(Double.MAX_VALUE);
        tglShowDate.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().showDateVisProperty());
        vBox.getChildren().add(tglShowDate);

        PToggleSwitch tglOnly = new PToggleSwitch("\"anzeigen\"");
        tglOnly.setMaxWidth(Double.MAX_VALUE);
        tglOnly.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().onlyVisProperty());
        vBox.getChildren().add(tglOnly);

        PToggleSwitch tglNot = new PToggleSwitch("\"ausschließen\"");
        tglNot.setMaxWidth(Double.MAX_VALUE);
        tglNot.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().notVisProperty());
        vBox.getChildren().add(tglNot);

        //Wartezeit
        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator2");
        sp.setMinHeight(0);

        final ToggleGroup group = new ToggleGroup();
        RadioButton rboWait = new RadioButton();
        RadioButton rboReturn = new RadioButton();
        rboWait.setToggleGroup(group);
        rboReturn.setToggleGroup(group);
        rboWait.setSelected(!ProgConfig.SYSTEM_FILTER_RETURN.getBool());
        rboReturn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_FILTER_RETURN.getBooleanProperty());

        Label lblValue = new Label();
        lblValue.setMinWidth(Region.USE_COMPUTED_SIZE);

        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(ProgConst.SYSTEM_FILTER_MAX_WAIT_TIME);
        slider.setMinorTickCount(9);//dann 10 Teile, 1000/10=alle 100 kann eingeloggt werden :)
        slider.setBlockIncrement(100);//Bedienung über die Tastatur
        slider.setMajorTickUnit(1000);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            waitTime.setValue(Double.valueOf(slider.getValue()).intValue());
            setLabel(lblValue);
        });


        slider.disableProperty().bind(rboReturn.selectedProperty());
        rboReturn.selectedProperty().addListener((u, o, n) -> {
            changeRbo(rboReturn, slider, lblValue);
        });
        changeRbo(rboReturn, slider, lblValue);


        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(0));
        gridPane.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(gridPane, Priority.ALWAYS);

        int row = 0;
        gridPane.add(new Label("In Textfeldern:"), 0, row, 3, 1);

        gridPane.add(rboWait, 0, ++row);
        gridPane.add(new Label("Suchbeginn wenn keine\nEingabe für:"), 1, row);
        gridPane.add(lblValue, 2, row);

        gridPane.add(slider, 1, ++row, 2, 1);
        GridPane.setHgrow(slider, Priority.ALWAYS);
        slider.setPadding(new Insets(5, 0, 0, 0));

        gridPane.add(new Label(), 0, ++row);

        gridPane.add(rboReturn, 0, ++row);
        gridPane.add(new Label("Suchbeginn bei Return-Eingabe"), 1, row, 2, 1);

        gridPane.getColumnConstraints().addAll(
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrowRight());

        vBox.getChildren().addAll(sp, gridPane);
    }

    private void changeRbo(RadioButton rboReturn, Slider slider, Label lblValue) {
        if (rboReturn.isSelected()) {
            slider.setValue(0);
            waitTime.setValue(Double.valueOf(slider.getValue()).intValue());
        } else {
            slider.setValue(waitTime.getValue());
        }
        setLabel(lblValue);
    }

    private int setLabel(Label lblValue) {
        int intValue = waitTime.getValue();
        lblValue.setText("  " + intValue + " ms");
        return intValue;
    }
}
