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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import de.p2tools.p2Lib.guiTools.pCheckComboBox.PCheckComboBox;
import de.p2tools.p2Lib.guiTools.pRange.PRangeBox;
import de.p2tools.p2Lib.guiTools.pRange.PTimePeriodBox;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilmFilterControllerFilter extends VBox {

    private final Slider slDays = new Slider();
    private final Label lblDays = new Label();

    private final PRangeBox slDur = new PRangeBox(0, SelectedFilter.FILTER_DURATION_MAX_MINUTE);
    private final Label lblDur = new Label("Filmlänge:");

    private final PTimePeriodBox slFilmTime = new PTimePeriodBox();
    private final PToggleSwitch tglFilmTime = new PToggleSwitch("Zeitraum \"ausschließen\"");
    private final Label lblFilmTime = new Label("Sendezeit:");

    PCheckComboBox checkOnly = new PCheckComboBox();
    PCheckComboBox checkNot = new PCheckComboBox();
    Label lblOnly = new Label("nur anzeigen");
    Label lblNot = new Label("nicht anzeigen");

    private final ComboBox<String> cboChannel = new ComboBox<>();
    private final ComboBox<String> cbxTheme = new ComboBox<>();
    private final TextField txtThemeTitle = new TextField();
    private final TextField txtTitle = new TextField();
    private final TextField txtSomewhere = new TextField();
    private final TextField txtUrl = new TextField();

    private final MenuButton mbFilterTools = new MenuButton("");

    private final String ONLY_BOOKMARK = "Bookmarks";
    private final String ONLY_HD = "HD";
    private final String ONLY_UT = "UT";
    private final String ONLY_NEW = "neue";
    private final String ONLY_LIVE = "Livestreams";
    private final String ONLY_AKT_HISTORY = "aktuelle History";

    private final String NOT_ABO = "Abos";
    private final String NOT_HISTORY = "gesehene";
    private final String NOT_DOUBLE = "doppelte";
    private final String NOT_GEO = "Geo geblockt";
    private final String NOT_FUTURE = "Zukunft";

    private final ProgData progData;

    private final IntegerProperty filterProp = ProgConfig.FILTER_FILME_SEL_FILTER.getIntegerProperty();

    public FilmFilterControllerFilter() {
        super();
        progData = ProgData.getInstance();

        setPadding(new Insets(15, 15, 15, 15));
        setSpacing(20);
        VBox.setVgrow(this, Priority.ALWAYS);

        // Sender, Thema, ..
        addStringFilter();

        // Slider
        initDaysFilter();
        initDurFilter();
        initFilmTimeFilter();
        addSlider();
        setLabelSlider();

        // CheckOnOff
        addCheckFilter();
    }

    private void addStringFilter() {
        cboChannel.editableProperty().bind(progData.storedFilters.getActFilterSettings().channelExactProperty().not());
        cboChannel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cboChannel.setVisibleRowCount(25);
        cboChannel.valueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().channelProperty());

        cboChannel.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                // wenn Änderung beim Sender -> Themen anpassen
                if (newValue.isEmpty()) {
                    progData.worker.getTheme("");
                } else {
                    progData.worker.getTheme(newValue);
                }
            }
        });
        cboChannel.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                // wenn Änderung beim Sender -> Themen anpassen
                if (newValue.isEmpty()) {
                    progData.worker.getTheme("");
                } else {
                    progData.worker.getTheme(newValue);
                }
                progData.storedFilters.getActFilterSettings().setChannel(newValue);
            }
        });


        cbxTheme.editableProperty().bind(progData.storedFilters.getActFilterSettings().themeExactProperty().not());
        cbxTheme.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cbxTheme.setVisibleRowCount(25);
        cbxTheme.valueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().themeProperty());
        cbxTheme.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                progData.storedFilters.getActFilterSettings().setTheme(newValue);
            }
        });

        cboChannel.setItems(progData.worker.getAllChannelList());
        cbxTheme.setItems(progData.worker.getThemeForChannelList());

        txtThemeTitle.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().themeTitleProperty());
        txtTitle.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().titleProperty());
        txtSomewhere.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().somewhereProperty());
        txtUrl.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().urlProperty());


        VBox vBox = new VBox(10);
        addTxt("Sender", cboChannel, vBox, progData.storedFilters.getActFilterSettings().channelVisProperty());
        addTxt("Thema", cbxTheme, vBox, progData.storedFilters.getActFilterSettings().themeVisProperty());
        addTxt("Thema oder Titel", txtThemeTitle, vBox, progData.storedFilters.getActFilterSettings().themeTitleVisProperty());
        addTxt("Titel", txtTitle, vBox, progData.storedFilters.getActFilterSettings().titleVisProperty());
        addTxt("Irgendwo", txtSomewhere, vBox, progData.storedFilters.getActFilterSettings().somewhereVisProperty());
        addTxt("URL", txtUrl, vBox, progData.storedFilters.getActFilterSettings().urlVisProperty());

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator1");
        sp.setMinHeight(10);
        vBox.getChildren().add(sp);

        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().channelVisProperty()
                .or(progData.storedFilters.getActFilterSettings().themeVisProperty()
                        .or(progData.storedFilters.getActFilterSettings().themeTitleVisProperty()
                                .or(progData.storedFilters.getActFilterSettings().titleVisProperty()
                                        .or(progData.storedFilters.getActFilterSettings().somewhereVisProperty()
                                                .or(progData.storedFilters.getActFilterSettings().urlVisProperty())
                                        )
                                )
                        )
                ));
        vBox.managedProperty().bind(vBox.visibleProperty());

        sp.visibleProperty().bind(vBox.visibleProperty());
        sp.managedProperty().bind(vBox.visibleProperty());


        getChildren().add(vBox);
    }

    private void addTxt(String txt, Control control, VBox vBoxComplete, BooleanProperty booleanProperty) {
        VBox vBox = new VBox();
        Label label = new Label(txt);
        vBox.getChildren().addAll(label, control);
        vBoxComplete.getChildren().add(vBox);

        vBox.visibleProperty().bind(booleanProperty);
        vBox.managedProperty().bind(booleanProperty);
    }

    private void initDaysFilter() {
        slDays.setMin(1);
        slDays.setMax(SelectedFilter.FILTER_DAYS_MAX);
        slDays.setShowTickLabels(true);
        slDays.setMajorTickUnit(10);
        slDays.setBlockIncrement(1);

        slDays.valueProperty().addListener(l -> {
            setLabelSlider();
        });

        // kein direktes binding wegen: valueChangingProperty, nur melden wenn "steht"
        slDays.setValue(progData.storedFilters.getActFilterSettings().getDays());

        progData.storedFilters.getActFilterSettings().daysProperty().addListener(
                l -> slDays.setValue(progData.storedFilters.getActFilterSettings().getDays()));

        slDays.valueChangingProperty().addListener((observable, oldvalue, newvalue) -> {
                    if (!newvalue) {
                        progData.storedFilters.getActFilterSettings().setDays((int) slDays.getValue());
                    }
                }
        );
    }

    private void initDurFilter() {
        slDur.minValueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minDurProperty());
        slDur.maxValueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().maxDurProperty());
        slDur.setValuePrefix("");

//        progData.storedFilter.getSelectedFilter().minDurProperty().addListener((observable, oldValue, newValue) ->
//                System.out.println("getSelectedFilter().getMinDur " + progData.storedFilter.getSelectedFilter().getMinDur()));
//        progData.storedFilter.getSelectedFilter().maxDurProperty().addListener((observable, oldValue, newValue) ->
//                System.out.println("getSelectedFilter().getMaxDur " + progData.storedFilter.getSelectedFilter().getMaxDur()));
    }

    private void initFilmTimeFilter() {
        slFilmTime.minValueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minTimeProperty());
        slFilmTime.maxValueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().maxTimeProperty());
        slFilmTime.setVluePrefix("");

//        progData.storedFilter.getSelectedFilter().minTimeProperty().addListener((observable, oldValue, newValue) ->
//                System.out.println("getSelectedFilter().getMinTime " + progData.storedFilter.getSelectedFilter().getMinTime()));
//        progData.storedFilter.getSelectedFilter().maxTimeProperty().addListener((observable, oldValue, newValue) ->
//                System.out.println("getSelectedFilter().getMaxTime " + progData.storedFilter.getSelectedFilter().getMaxTime()));

        tglFilmTime.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minMaxTimeInvertProperty());
        GridPane.setFillWidth(tglFilmTime, false);
    }

    private void addSlider() {
        // Tage
        VBox vBox = new VBox(3);
        vBox.getChildren().addAll(lblDays, slDays);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().daysVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().daysVisProperty());
        getChildren().addAll(vBox);

        // MinMax Dauer
        vBox = new VBox(3);
        vBox.getChildren().addAll(lblDur, slDur);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().minMaxDurVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().minMaxDurVisProperty());
        getChildren().addAll(vBox);

        // MinMax Uhrzeit
        vBox = new VBox(3);
        vBox.getChildren().addAll(lblFilmTime, slFilmTime, tglFilmTime);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().minMaxTimeVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().minMaxTimeVisProperty());
        getChildren().addAll(vBox);
    }

    private void addCheckFilter() {
        checkOnly.addItem(ONLY_BOOKMARK, "nur Filme der Bookmarks anzeigen", progData.storedFilters.getActFilterSettings().onlyBookmarkProperty());
        checkOnly.addItem(ONLY_HD, "nur HD-Filme anzeigen", progData.storedFilters.getActFilterSettings().onlyHdProperty());
        checkOnly.addItem(ONLY_UT, "nur Filme mit Untertitel anzeigen", progData.storedFilters.getActFilterSettings().onlyUtProperty());
        checkOnly.addItem(ONLY_NEW, "nur neue Filme anzeigen", progData.storedFilters.getActFilterSettings().onlyNewProperty());
        checkOnly.addItem(ONLY_LIVE, "nur Livestreams anzeigen", progData.storedFilters.getActFilterSettings().onlyLiveProperty());
        checkOnly.addItem(ONLY_AKT_HISTORY, "nur die aktuelle History anzeigen", progData.storedFilters.getActFilterSettings().onlyAktHistoryProperty());

        checkNot.addItem(NOT_ABO, "keine Filme für die es ein Abo gibt, anzeigen", progData.storedFilters.getActFilterSettings().notAboProperty());
        checkNot.addItem(NOT_HISTORY, "bereits gesehene Filme nicht anzeigen", progData.storedFilters.getActFilterSettings().notHistoryProperty());
        checkNot.addItem(NOT_DOUBLE, "doppelte Filme nur einmal anzeigen", progData.storedFilters.getActFilterSettings().notDoubleProperty());
        checkNot.addItem(NOT_GEO, "geo-geblockte Filme nicht anzeigen", progData.storedFilters.getActFilterSettings().notGeoProperty());
        checkNot.addItem(NOT_FUTURE, "keine Filme mit Datum in der Zukunft anzeigen", progData.storedFilters.getActFilterSettings().notFutureProperty());

        VBox vBox = new VBox();
        vBox.getChildren().addAll(lblOnly, checkOnly);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().onlyVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().onlyVisProperty());
        getChildren().add(vBox);

        vBox = new VBox();
        vBox.getChildren().addAll(lblNot, checkNot);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().notVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().notVisProperty());
        getChildren().add(vBox);
    }

    private void setLabelSlider() {
        final String txtAll = "alles";

        int i = (int) slDays.getValue();
        String tNr = i + "";

        if (i == SelectedFilter.FILTER_DAYS_MAX) {
            lblDays.setText("Zeitraum: " + txtAll);
        } else {
            lblDays.setText("Zeitraum: " + tNr + (i == 1 ? " Tag" : " Tage"));
        }
    }
}