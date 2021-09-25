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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.tools.filmListFilter.FilmFilter;
import de.p2tools.p2Lib.guiTools.PDatePicker;
import de.p2tools.p2Lib.guiTools.pCheckComboBox.PCheckComboBox;
import de.p2tools.p2Lib.guiTools.pRange.PRangeBox;
import de.p2tools.p2Lib.guiTools.pRange.PTimePeriodBox;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FilmFilterControllerFilter extends VBox {

    private final Slider slTimeRange = new Slider();
    private final Label lblTimeRange = new Label("Zeitraum:");
    private final Label lblTimeRangeValue = new Label();

    private final PRangeBox slDur = new PRangeBox(0, FilmFilter.FILTER_DURATION_MAX_MINUTE);
    private final Label lblDur = new Label("Filmlänge:");

    private final PTimePeriodBox slFilmTime = new PTimePeriodBox();
    private final PToggleSwitch tglFilmTime = new PToggleSwitch("Zeitraum ausschließen");
    private final Label lblFilmTime = new Label("Sendezeit:");

    private final Label lblShowDate = new Label("Sendedatum:");
    private final PDatePicker pDatePicker = new PDatePicker();
    private final Button btnClearDatePicker = new Button("");

    PCheckComboBox checkOnly = new PCheckComboBox();
    PCheckComboBox checkNot = new PCheckComboBox();
    Label lblOnly = new Label("anzeigen:");
    Label lblNot = new Label("ausschließen:");

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

    public FilmFilterControllerFilter() {
        super();
        progData = ProgData.getInstance();

        setPadding(new Insets(15, 15, 15, 15));
        setSpacing(20);

        // Slider
        addSlider();

        // CheckOnOff
        addCheckFilter();
    }

    private void initDaysFilter() {
        slTimeRange.setMin(FilmFilter.FILTER_TIME_RANGE_MIN_VALUE);
        slTimeRange.setMax(FilmFilter.FILTER_TIME_RANGE_MAX_VALUE);
        slTimeRange.setShowTickLabels(true);

        slTimeRange.setMajorTickUnit(10);
        slTimeRange.setBlockIncrement(5);

        slTimeRange.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double x) {
                if (x == FilmFilter.FILTER_TIME_RANGE_ALL_VALUE) return "alles";

                return x.intValue() + "";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        slTimeRange.setValue(progData.storedFilters.getActFilterSettings().getTimeRange());
        setLabelSlider();
        progData.storedFilters.getActFilterSettings().timeRangeProperty().addListener(
                l -> slTimeRange.setValue(progData.storedFilters.getActFilterSettings().getTimeRange()));

        // kein direktes binding wegen: valueChangingProperty, nur melden wenn "steht"
        slTimeRange.valueProperty().addListener((o, oldV, newV) -> {
            setLabelSlider();
            if (!slTimeRange.isValueChanging()) {
                progData.storedFilters.getActFilterSettings().setTimeRange((int) slTimeRange.getValue());
            }
        });

        slTimeRange.valueChangingProperty().addListener((observable, oldvalue, newvalue) -> {
                    if (!newvalue) {
                        progData.storedFilters.getActFilterSettings().setTimeRange((int) slTimeRange.getValue());
                    }
                }
        );
    }

    private void initDurFilter() {
        slDur.minValueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minDurProperty());
        slDur.maxValueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().maxDurProperty());
        slDur.setValuePrefix("");
    }

    private void initFilmTimeFilter() {
        slFilmTime.minValueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minTimeProperty());
        slFilmTime.maxValueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().maxTimeProperty());
        slFilmTime.setVluePrefix("");

        tglFilmTime.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minMaxTimeInvertProperty());
        GridPane.setFillWidth(tglFilmTime, false);

        pDatePicker.valueProperty().addListener((u, o, n) -> {
            LocalDate newDate = pDatePicker.getValue();
            if (newDate != null) {
                try {
                    progData.storedFilters.getActFilterSettings().setShowDate(newDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                } catch (Exception ex) {

                }

            } else {
                progData.storedFilters.getActFilterSettings().setShowDate(FilmFilter.FILTER_SHOW_DATE_ALL);
            }
        });
        progData.storedFilters.getActFilterSettings().showDateProperty().addListener((observable, oldValue, newValue) -> {
            initPDatePicker();
        });
        initPDatePicker();
        btnClearDatePicker.setGraphic(new ProgIcons().ICON_BUTTON_CLEAR);
        btnClearDatePicker.setOnAction(a -> pDatePicker.clearDate());
    }

    private void initPDatePicker() {
        try {
            final String s = progData.storedFilters.getActFilterSettings().getShowDate();
            if (!s.isEmpty()) {
                LocalDate localDate = LocalDate.parse(progData.storedFilters.getActFilterSettings().getShowDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                pDatePicker.setValue(localDate);
            } else {
                pDatePicker.clearDate();
            }
        } catch (DateTimeParseException ex) {
            pDatePicker.clearDate();
        }
    }

    private void addSlider() {
        initDaysFilter();
        initDurFilter();
        initFilmTimeFilter();
        VBox vBox;

        // Tage
        vBox = new VBox(2);
        HBox h = new HBox();
        HBox hh = new HBox();
        h.getChildren().addAll(lblTimeRange, hh, lblTimeRangeValue);
        HBox.setHgrow(hh, Priority.ALWAYS);
        lblTimeRange.setMinWidth(0);
        vBox.getChildren().addAll(h, slTimeRange);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().timeRangeVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().timeRangeVisProperty());
        getChildren().addAll(vBox);

        // MinMax Dauer
        vBox = new VBox(2);
        vBox.getChildren().addAll(lblDur, slDur);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().minMaxDurVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().minMaxDurVisProperty());
        getChildren().addAll(vBox);

        // MinMax Uhrzeit
        vBox = new VBox(2);
        vBox.getChildren().addAll(lblFilmTime, slFilmTime, tglFilmTime);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().minMaxTimeVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().minMaxTimeVisProperty());
        getChildren().addAll(vBox);

        //Sendedatum
        vBox = new VBox(2);
        HBox hBox = new HBox(0);
        hBox.getChildren().addAll(pDatePicker, btnClearDatePicker);
        HBox.setHgrow(pDatePicker, Priority.ALWAYS);
        pDatePicker.setMaxWidth(Double.MAX_VALUE);

        vBox.getChildren().addAll(lblShowDate, hBox);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().showDateVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().showDateVisProperty());
        getChildren().addAll(vBox);
    }

    private void addCheckFilter() {
        checkOnly.setEmptyText("alles");
        checkOnly.addItem(ONLY_BOOKMARK, "nur Filme der Bookmarks anzeigen", progData.storedFilters.getActFilterSettings().onlyBookmarkProperty());
        checkOnly.addItem(ONLY_HD, "nur HD-Filme anzeigen", progData.storedFilters.getActFilterSettings().onlyHdProperty());
        checkOnly.addItem(ONLY_UT, "nur Filme mit Untertitel anzeigen", progData.storedFilters.getActFilterSettings().onlyUtProperty());
        checkOnly.addItem(ONLY_NEW, "nur neue Filme anzeigen", progData.storedFilters.getActFilterSettings().onlyNewProperty());
        checkOnly.addItem(ONLY_LIVE, "nur Livestreams anzeigen", progData.storedFilters.getActFilterSettings().onlyLiveProperty());
        checkOnly.addItem(ONLY_AKT_HISTORY, "nur die aktuelle History anzeigen", progData.storedFilters.getActFilterSettings().onlyAktHistoryProperty());

        checkNot.setEmptyText("nichts");
        checkNot.addItem(NOT_ABO, "keine Filme für die es ein Abo gibt, anzeigen", progData.storedFilters.getActFilterSettings().notAboProperty());
        checkNot.addItem(NOT_HISTORY, "bereits gesehene Filme nicht anzeigen", progData.storedFilters.getActFilterSettings().notHistoryProperty());
        checkNot.addItem(NOT_DOUBLE, "doppelte Filme nur einmal anzeigen", progData.storedFilters.getActFilterSettings().notDoubleProperty());
        checkNot.addItem(NOT_GEO, "geo-geblockte Filme nicht anzeigen", progData.storedFilters.getActFilterSettings().notGeoProperty());
        checkNot.addItem(NOT_FUTURE, "keine Filme mit Datum in der Zukunft anzeigen", progData.storedFilters.getActFilterSettings().notFutureProperty());

        VBox vBox = new VBox(2);
        vBox.getChildren().addAll(lblOnly, checkOnly);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().onlyVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().onlyVisProperty());
        getChildren().add(vBox);

        vBox = new VBox(2);
        vBox.getChildren().addAll(lblNot, checkNot);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().notVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().notVisProperty());
        getChildren().add(vBox);
    }

    private void setLabelSlider() {
        final String txtAll = "alles";

        int i = (int) slTimeRange.getValue();
        String tNr = i + "";

        if (i == FilmFilter.FILTER_TIME_RANGE_ALL_VALUE) {
            lblTimeRangeValue.setText(txtAll);
        } else {
            lblTimeRangeValue.setText(tNr + (i == 1 ? " Tag" : " Tage"));
        }
    }
}
