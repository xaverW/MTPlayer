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

package de.p2tools.mtplayer.gui.filter.film;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filter.FilterDto;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class FilmFilterEditDialog extends P2DialogExtra {

    private final RadioButton rbLast = new RadioButton("Die letzte vorhandene Zeile auswählen");
    private final RadioButton rbFirst = new RadioButton("Die erste Tabellenzeile auswählen");
    private final RadioButton rbNothing = new RadioButton("Nichts neues auswählen");
    private final P2ToggleSwitch tglRegEx = new P2ToggleSwitch("Bei RegEx muss der Suchtext nur enthalten sein:");
    private final ProgData progData;
    private final FilterDto filterDto;

    public FilmFilterEditDialog(ProgData progData, FilterDto filterDto) {
        super(progData.primaryStage, null, "Filtereinstellungen",
                true, false, false, DECO.NO_BORDER);
        this.progData = progData;
        this.filterDto = filterDto;

        init(true);
    }

    @Override
    public void close() {
        rbFirst.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_FILTER_FIRST_ROW);
        rbNothing.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_FILTER_NONE_ROW);
        tglRegEx.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_FILTER_REG_EX_ONLY_CONTAIN);
        super.close();
    }

    @Override
    public void make() {
        init();

        final Button btnHelp = PIconFactory.getHelpButton(getStage(), "Filtereinstellungen", HelpText.GUI_FILMS_EDIT_FILTER);
        final Button btnOk = new Button("_Ok");
        btnOk.setOnAction(event -> close());
        addOkButton(btnOk);
        addHlpButton(btnHelp);
    }

    public void init() {
        final TabPane tabPane = new TabPane();
        VBox vBox = getVBoxCont();
        vBox.getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        addFilterTab(tabPane);
        addConfigTab(tabPane);
    }

    public void addFilterTab(TabPane tabPane) {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, P2LibConst.PADDING_VBOX, P2LibConst.PADDING_VBOX, P2LibConst.PADDING_VBOX));
        vBox.setSpacing(15);

        Tab tab = new Tab();
        tab.setText("Filter");
        tab.setClosable(false);
        tab.setContent(vBox);
        tabPane.getTabs().add(tab);

        P2ToggleSwitch tglChannel = new P2ToggleSwitch("Sender");
        tglChannel.setMaxWidth(Double.MAX_VALUE);
        tglChannel.selectedProperty().bindBidirectional(filterDto.filterWorker.getActFilterSettings().channelVisProperty());
        vBox.getChildren().add(tglChannel);

        // Thema
        P2ToggleSwitch tglTheme = new P2ToggleSwitch("Thema");
        tglTheme.setMaxWidth(Double.MAX_VALUE);
        tglTheme.selectedProperty().bindBidirectional(filterDto.filterWorker.getActFilterSettings().themeVisProperty());

        P2ToggleSwitch tglThemeExact = new P2ToggleSwitch("  -> Freie Suche mit Eingabefeld");
        tglThemeExact.disableProperty().bind(filterDto.filterWorker.getActFilterSettings().themeVisProperty().not());
        tglThemeExact.setMaxWidth(Double.MAX_VALUE);
        tglThemeExact.setSelected(!filterDto.filterWorker.getActFilterSettings().isThemeIsExact());
        tglThemeExact.selectedProperty().addListener((observable, oldValue, newValue) ->
                filterDto.filterWorker.getActFilterSettings().themeIsExactProperty().setValue(!newValue));

        VBox v = new VBox(5);
        HBox h = new HBox(0);
        h.setPadding(new Insets(0, 15, 0, 5));
        h.getChildren().add(tglThemeExact);
        HBox.setHgrow(tglThemeExact, Priority.ALWAYS);
        v.getChildren().addAll(tglTheme, h);
        vBox.getChildren().add(v);

        P2ToggleSwitch tglThemeTitle = new P2ToggleSwitch("Thema oder Titel");
        tglThemeTitle.setMaxWidth(Double.MAX_VALUE);
        tglThemeTitle.selectedProperty().bindBidirectional(filterDto.filterWorker.getActFilterSettings().themeTitleVisProperty());
        vBox.getChildren().add(tglThemeTitle);

        P2ToggleSwitch tglTitle = new P2ToggleSwitch("Titel");
        tglTitle.setMaxWidth(Double.MAX_VALUE);
        tglTitle.selectedProperty().bindBidirectional(filterDto.filterWorker.getActFilterSettings().titleVisProperty());
        vBox.getChildren().add(tglTitle);

        P2ToggleSwitch tglSomewhere = new P2ToggleSwitch("Irgendwo");
        tglSomewhere.setMaxWidth(Double.MAX_VALUE);
        tglSomewhere.selectedProperty().bindBidirectional(filterDto.filterWorker.getActFilterSettings().somewhereVisProperty());
        vBox.getChildren().add(tglSomewhere);

        P2ToggleSwitch tglUrl = new P2ToggleSwitch("Url");
        tglUrl.setMaxWidth(Double.MAX_VALUE);
        tglUrl.selectedProperty().bindBidirectional(filterDto.filterWorker.getActFilterSettings().urlVisProperty());
        vBox.getChildren().add(tglUrl);

        P2ToggleSwitch tglTimeRange = new P2ToggleSwitch("Zeitraum [Tage]");
        tglTimeRange.setMaxWidth(Double.MAX_VALUE);
        tglTimeRange.selectedProperty().bindBidirectional(filterDto.filterWorker.getActFilterSettings().timeRangeVisProperty());
        vBox.getChildren().add(tglTimeRange);

        P2ToggleSwitch tglMinMax = new P2ToggleSwitch("Filmlänge Min/Max");
        tglMinMax.setMaxWidth(Double.MAX_VALUE);
        tglMinMax.selectedProperty().bindBidirectional(filterDto.filterWorker.getActFilterSettings().minMaxDurVisProperty());
        vBox.getChildren().add(tglMinMax);

        P2ToggleSwitch tglMinMaxTime = new P2ToggleSwitch("Sendezeit");
        tglMinMaxTime.setMaxWidth(Double.MAX_VALUE);
        tglMinMaxTime.selectedProperty().bindBidirectional(filterDto.filterWorker.getActFilterSettings().minMaxTimeVisProperty());
        vBox.getChildren().add(tglMinMaxTime);

        P2ToggleSwitch tglShowDate = new P2ToggleSwitch("Sendedatum");
        tglShowDate.setMaxWidth(Double.MAX_VALUE);
        tglShowDate.selectedProperty().bindBidirectional(filterDto.filterWorker.getActFilterSettings().showDateVisProperty());
        vBox.getChildren().add(tglShowDate);

        P2ToggleSwitch tglOnly = new P2ToggleSwitch("\"Anzeigen\"");
        tglOnly.setMaxWidth(Double.MAX_VALUE);
        tglOnly.selectedProperty().bindBidirectional(filterDto.filterWorker.getActFilterSettings().onlyVisProperty());
        vBox.getChildren().add(tglOnly);

        P2ToggleSwitch tglNot = new P2ToggleSwitch("\"Ausschließen\"");
        tglNot.setMaxWidth(Double.MAX_VALUE);
        tglNot.selectedProperty().bindBidirectional(filterDto.filterWorker.getActFilterSettings().notVisProperty());
        vBox.getChildren().add(tglNot);
    }

    public void addConfigTab(TabPane tabPane) {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, P2LibConst.PADDING_VBOX, P2LibConst.PADDING_VBOX, P2LibConst.PADDING_VBOX));
        vBox.setSpacing(15);

        Tab tab = new Tab();
        tab.setText("Einstellungen");
        tab.setClosable(false);
        tab.setContent(vBox);
        tabPane.getTabs().add(tab);

        // Anlegen
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

        rbLast.setTooltip(new Tooltip("Nach einer Suche, sind nicht mehr die gleichen\n" +
                "Filme in der Tabelle. Hier kann vorgegeben werden,\n" +
                "welche Zeile dann ausgewählt werden soll."));
        rbFirst.setTooltip(new Tooltip("Nach einer Suche, sind nicht mehr die gleichen\n" +
                "Filme in der Tabelle. Hier kann vorgegeben werden,\n" +
                "welche Zeile dann ausgewählt werden soll."));
        rbNothing.setTooltip(new Tooltip("Nach einer Suche, sind nicht mehr die gleichen\n" +
                "Filme in der Tabelle. Hier kann vorgegeben werden,\n" +
                "welche Zeile dann ausgewählt werden soll."));

        ToggleGroup tg = new ToggleGroup();
        rbLast.setToggleGroup(tg);
        rbFirst.setToggleGroup(tg);
        rbNothing.setToggleGroup(tg);

        rbFirst.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_FILTER_FIRST_ROW);
        rbNothing.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_FILTER_NONE_ROW);
        if (!ProgConfig.SYSTEM_FILTER_FIRST_ROW.get() &&
                !ProgConfig.SYSTEM_FILTER_NONE_ROW.get()) {
            // ist die Standardeinstellung
            rbLast.setSelected(true);
        }

        tglRegEx.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_FILTER_REG_EX_ONLY_CONTAIN);

        // Einbauen
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

        gridPane.add(new Label(), 0, ++row, 2, 1);
        gridPane.add(tglReturn, 0, ++row, 2, 1);

        gridPane.add(new Label(""), 0, ++row, 2, 1);
        gridPane.add(new Label(""), 0, ++row, 2, 1);
        gridPane.add(new Label("Welche Zeile soll nach einer Suche ausgewählt werden:"), 0, ++row, 2, 1);
        gridPane.add(rbFirst, 0, ++row, 2, 1);
        gridPane.add(rbLast, 0, ++row, 2, 1);
        gridPane.add(rbNothing, 0, ++row, 2, 1);


        gridPane.add(new Label(""), 0, ++row, 2, 1);
        gridPane.add(new Label(""), 0, ++row, 2, 1);
        gridPane.add(tglRegEx, 0, ++row, 2, 1);

        gridPane.getColumnConstraints().addAll(
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrowRight());

        vBox.getChildren().addAll(gridPane);
    }

    private int setLabel(Label lblValue) {
        int intValue = ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue();
        lblValue.setText("  " + intValue + " ms");
        return intValue;
    }
}
