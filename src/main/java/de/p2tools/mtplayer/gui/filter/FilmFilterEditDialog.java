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

package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;


public class FilmFilterEditDialog extends PDialogExtra {

    final ProgData progData;

    public FilmFilterEditDialog(ProgData progData) {
        super(progData.primaryStage, null, "Filtereinstellungen", true, true, DECO.NO_BORDER);
        this.progData = progData;

        init(true);
    }

    @Override
    public void make() {
        init(getVBoxCont());

        final Button btnHelp = P2Button.helpButton(getStage(), "Filtereinstellungen",
                HelpText.GUI_FILMS_EDIT_FILTER);

        Button btnOk = new Button("_Ok");
        btnOk.setOnAction(event -> close());
        addOkButton(btnOk);
        addHlpButton(btnHelp);
    }

    public void init(VBox vBox) {
        vBox.setSpacing(15);

        P2ToggleSwitch tglChannel = new P2ToggleSwitch("Sender");
        tglChannel.setMaxWidth(Double.MAX_VALUE);
        tglChannel.selectedProperty().bindBidirectional(progData.filmFilterWorker.getActFilterSettings().channelVisProperty());
        vBox.getChildren().add(tglChannel);

        // Thema
        P2ToggleSwitch tglTheme = new P2ToggleSwitch("Thema");
        tglTheme.setMaxWidth(Double.MAX_VALUE);
        tglTheme.selectedProperty().bindBidirectional(progData.filmFilterWorker.getActFilterSettings().themeVisProperty());

        P2ToggleSwitch tglThemeExact = new P2ToggleSwitch("  -> freie Suche mit Eingabefeld");
        tglThemeExact.disableProperty().bind(progData.filmFilterWorker.getActFilterSettings().themeVisProperty().not());
        tglThemeExact.setMaxWidth(Double.MAX_VALUE);
        tglThemeExact.setSelected(!progData.filmFilterWorker.getActFilterSettings().isThemeExact());
        tglThemeExact.selectedProperty().addListener((observable, oldValue, newValue) ->
                progData.filmFilterWorker.getActFilterSettings().themeExactProperty().setValue(!newValue));

        VBox v = new VBox(5);
        HBox h = new HBox(0);
        h.setPadding(new Insets(0, 5, 0, 5));
        h.getChildren().add(tglThemeExact);
        HBox.setHgrow(tglThemeExact, Priority.ALWAYS);
        v.getChildren().addAll(tglTheme, h);
        vBox.getChildren().add(v);

        P2ToggleSwitch tglThemeTitle = new P2ToggleSwitch("Thema oder Titel");
        tglThemeTitle.setMaxWidth(Double.MAX_VALUE);
        tglThemeTitle.selectedProperty().bindBidirectional(progData.filmFilterWorker.getActFilterSettings().themeTitleVisProperty());
        vBox.getChildren().add(tglThemeTitle);

        P2ToggleSwitch tglTitle = new P2ToggleSwitch("Titel");
        tglTitle.setMaxWidth(Double.MAX_VALUE);
        tglTitle.selectedProperty().bindBidirectional(progData.filmFilterWorker.getActFilterSettings().titleVisProperty());
        vBox.getChildren().add(tglTitle);

        P2ToggleSwitch tglSomewhere = new P2ToggleSwitch("Irgendwo");
        tglSomewhere.setMaxWidth(Double.MAX_VALUE);
        tglSomewhere.selectedProperty().bindBidirectional(progData.filmFilterWorker.getActFilterSettings().somewhereVisProperty());
        vBox.getChildren().add(tglSomewhere);

        P2ToggleSwitch tglUrl = new P2ToggleSwitch("Url");
        tglUrl.setMaxWidth(Double.MAX_VALUE);
        tglUrl.selectedProperty().bindBidirectional(progData.filmFilterWorker.getActFilterSettings().urlVisProperty());
        vBox.getChildren().add(tglUrl);

        P2ToggleSwitch tglTimeRange = new P2ToggleSwitch("Zeitraum [Tage]");
        tglTimeRange.setMaxWidth(Double.MAX_VALUE);
        tglTimeRange.selectedProperty().bindBidirectional(progData.filmFilterWorker.getActFilterSettings().timeRangeVisProperty());
        vBox.getChildren().add(tglTimeRange);

        P2ToggleSwitch tglMinMax = new P2ToggleSwitch("Filmlänge Min/Max");
        tglMinMax.setMaxWidth(Double.MAX_VALUE);
        tglMinMax.selectedProperty().bindBidirectional(progData.filmFilterWorker.getActFilterSettings().minMaxDurVisProperty());
        vBox.getChildren().add(tglMinMax);

        P2ToggleSwitch tglMinMaxTime = new P2ToggleSwitch("Sendezeit");
        tglMinMaxTime.setMaxWidth(Double.MAX_VALUE);
        tglMinMaxTime.selectedProperty().bindBidirectional(progData.filmFilterWorker.getActFilterSettings().minMaxTimeVisProperty());
        vBox.getChildren().add(tglMinMaxTime);

        P2ToggleSwitch tglShowDate = new P2ToggleSwitch("Sendedatum");
        tglShowDate.setMaxWidth(Double.MAX_VALUE);
        tglShowDate.selectedProperty().bindBidirectional(progData.filmFilterWorker.getActFilterSettings().showDateVisProperty());
        vBox.getChildren().add(tglShowDate);

        P2ToggleSwitch tglOnly = new P2ToggleSwitch("\"anzeigen\"");
        tglOnly.setMaxWidth(Double.MAX_VALUE);
        tglOnly.selectedProperty().bindBidirectional(progData.filmFilterWorker.getActFilterSettings().onlyVisProperty());
        vBox.getChildren().add(tglOnly);

        P2ToggleSwitch tglNot = new P2ToggleSwitch("\"ausschließen\"");
        tglNot.setMaxWidth(Double.MAX_VALUE);
        tglNot.selectedProperty().bindBidirectional(progData.filmFilterWorker.getActFilterSettings().notVisProperty());
        vBox.getChildren().add(tglNot);


        //Wartezeit
        P2ToggleSwitch tglReturn = new P2ToggleSwitch("In Textfeldern Suchbeginn erst mit \"Return\" starten");
        tglReturn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_FILTER_RETURN);

        Label lblValue = new Label();
        lblValue.setMinWidth(Region.USE_COMPUTED_SIZE);

        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(ProgConst.SYSTEM_FILTER_MAX_WAIT_TIME);
        slider.setMinorTickCount(4);//dann 5 Teile, 500/5=alle 100 kann eingeloggt werden :)
        slider.setBlockIncrement(200);//Bedienung über die Tastatur
        slider.setMajorTickUnit(500);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            ProgConfig.SYSTEM_FILTER_WAIT_TIME.setValue(Double.valueOf(slider.getValue()).intValue());
            setLabel(lblValue);
        });
        slider.setValue(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue());
        setLabel(lblValue);
        slider.disableProperty().bind(tglReturn.selectedProperty());
        lblValue.disableProperty().bind(tglReturn.selectedProperty());

        P2ToggleSwitch tglFirstTableRow = new P2ToggleSwitch("Nach der Suche immer die erste Zeile\n" +
                "in der Tabelle auswählen");
        tglFirstTableRow.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_FILTER_FIRST_ROW);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(0));
        gridPane.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(gridPane, Priority.ALWAYS);

        int row = 0;
        gridPane.add(new Label("Suchbeginn nach Eingabe verzögern:"), 0, row);
        gridPane.add(lblValue, 1, row);
        GridPane.setHalignment(lblValue, HPos.RIGHT);

        gridPane.add(slider, 0, ++row, 2, 1);
        GridPane.setHgrow(slider, Priority.ALWAYS);
        slider.setPadding(new Insets(0, 0, 0, 0));

        ++row;
        gridPane.add(tglReturn, 0, ++row, 2, 1);

        gridPane.add(new Label(), 0, ++row, 2, 1);
        gridPane.add(tglFirstTableRow, 0, ++row, 2, 1);

        gridPane.getColumnConstraints().addAll(
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrowRight());

        Separator sp1 = new Separator();
        sp1.getStyleClass().add("pseperator2");
        sp1.setMinHeight(0);
        vBox.getChildren().addAll(sp1, gridPane);
    }

    private int setLabel(Label lblValue) {
        int intValue = ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue();
        lblValue.setText("  " + intValue + " ms");
        return intValue;
    }
}
