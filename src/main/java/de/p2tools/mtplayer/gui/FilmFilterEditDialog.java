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
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;


public class FilmFilterEditDialog extends PDialogExtra {

    final ProgData progData;

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

    public void init(VBox vbox) {
        vbox.setSpacing(15);

        PToggleSwitch tglChannel = new PToggleSwitch("Sender");
        tglChannel.setMaxWidth(Double.MAX_VALUE);
        tglChannel.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().channelVisProperty());
        vbox.getChildren().add(tglChannel);

        VBox v = new VBox();
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
        vbox.getChildren().add(v);

        PToggleSwitch tglThemeTitle = new PToggleSwitch("Thema oder Titel");
        tglThemeTitle.setMaxWidth(Double.MAX_VALUE);
        tglThemeTitle.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().themeTitleVisProperty());
        vbox.getChildren().add(tglThemeTitle);

        PToggleSwitch tglTitle = new PToggleSwitch("Titel");
        tglTitle.setMaxWidth(Double.MAX_VALUE);
        tglTitle.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().titleVisProperty());
        vbox.getChildren().add(tglTitle);

        PToggleSwitch tglSomewhere = new PToggleSwitch("Irgendwo");
        tglSomewhere.setMaxWidth(Double.MAX_VALUE);
        tglSomewhere.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().somewhereVisProperty());
        vbox.getChildren().add(tglSomewhere);

        PToggleSwitch tglUrl = new PToggleSwitch("Url");
        tglUrl.setMaxWidth(Double.MAX_VALUE);
        tglUrl.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().urlVisProperty());
        vbox.getChildren().add(tglUrl);

        PToggleSwitch tglTimeRange = new PToggleSwitch("Zeitraum [Tage]");
        tglTimeRange.setMaxWidth(Double.MAX_VALUE);
        tglTimeRange.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().timeRangeVisProperty());
        vbox.getChildren().add(tglTimeRange);

        PToggleSwitch tglMinMax = new PToggleSwitch("Filmlänge Min/Max");
        tglMinMax.setMaxWidth(Double.MAX_VALUE);
        tglMinMax.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minMaxDurVisProperty());
        vbox.getChildren().add(tglMinMax);

        PToggleSwitch tglMinMaxTime = new PToggleSwitch("Sendezeit");
        tglMinMaxTime.setMaxWidth(Double.MAX_VALUE);
        tglMinMaxTime.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minMaxTimeVisProperty());
        vbox.getChildren().add(tglMinMaxTime);

        PToggleSwitch tglOnly = new PToggleSwitch("\"anzeigen\"");
        tglOnly.setMaxWidth(Double.MAX_VALUE);
        tglOnly.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().onlyVisProperty());
        vbox.getChildren().add(tglOnly);

        PToggleSwitch tglNot = new PToggleSwitch("\"ausschließen\"");
        tglNot.setMaxWidth(Double.MAX_VALUE);
        tglNot.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().notVisProperty());
        vbox.getChildren().add(tglNot);

        //Wartezeit
        VBox vBoxWait = new VBox(10);
        Border b = new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                CornerRadii.EMPTY, new BorderWidths(3), new Insets(10, 0, 0, 0)));
        vBoxWait.setBorder(b);
        vBoxWait.setSpacing(10);
        vBoxWait.setPadding(new Insets(10, 0, 0, 0));

        Label lbl = new Label();
        lbl.setMaxWidth(Double.MAX_VALUE);
        Label lblValue = new Label();
        lblValue.setMinWidth(Region.USE_COMPUTED_SIZE);
//        Label lblTitle = new Label("Zeit bis die Suche in Textfeldern\n" +
//                "ohne Eingabe startet:");
        Label lblTitle = new Label("Zeit bis zum Start der Suche\n" +
                "in Textfeldern ohne Eingabe:");
        lblTitle.setMinWidth(0);

        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(ProgConst.SYSTEM_FILTER_MAX_WAIT_TIME);
        slider.setMinorTickCount(9);//dann 10 Teile, 1000/10=alle 100 kann eingeloggt werden :)
        slider.setBlockIncrement(100);//Bedienung über die Tastatur
        slider.setMajorTickUnit(1000);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            ProgConfig.SYSTEM_FILTER_WAIT_TIME.setValue(setValue(slider, lblValue));
        });
        slider.setValue(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getInt());
        setValue(slider, lblValue);

        HBox hBoxLbl = new HBox(0);
        hBoxLbl.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(lbl, Priority.ALWAYS);
        hBoxLbl.getChildren().addAll(lblTitle, lbl, lblValue);

        HBox hBoxSlider = new HBox(10);
        hBoxSlider.getChildren().addAll(slider);
        HBox.setHgrow(slider, Priority.ALWAYS);

        vBoxWait.getChildren().addAll(hBoxLbl, hBoxSlider);
        vbox.getChildren().addAll(vBoxWait);
    }

    private int setValue(Slider slider, Label lblValue) {
        int intValue = Double.valueOf(slider.getValue()).intValue();
        lblValue.setText("  " + intValue + " ms");
        return intValue;
    }
}
