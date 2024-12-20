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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.p2lib.guitools.P2LDatePicker;
import de.p2tools.p2lib.guitools.pcheckcombobox.P2CheckComboBox;
import de.p2tools.p2lib.guitools.prange.P2RangeBox;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.tools.date.P2LDateFactory;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class FilmFilterControllerFilter extends VBox {

    private final Slider slTimeRange = new Slider();
    private final Label lblTimeRange = new Label("Zeitraum:");
    private final Label lblTimeRangeValue = new Label();

    private final P2RangeBox slDur = new P2RangeBox("Filmlänge:", true, 0, FilterCheck.FILTER_DURATION_MAX_MINUTE);

    private final P2RangeBox slTime = new P2RangeBox("Sendezeit:", true, 0, (FilterCheck.FILTER_TIME_MAX_SEC));
    //    private final P2TimePeriodBox slFilmTime = new P2TimePeriodBox();
    private final P2ToggleSwitch tglFilmTime = new P2ToggleSwitch("Zeitraum ausschließen");

    private final Label lblShowDate = new Label("Sendedatum:");
    private final P2LDatePicker pDatePicker = new P2LDatePicker();
    private final Button btnClearDatePicker = new Button("");

    private final ProgData progData;

    public FilmFilterControllerFilter() {
        super();
        progData = ProgData.getInstance();

        setSpacing(FilterController.FILTER_SPACING_TEXTFILTER);
        addSlider();
        addCheckFilter();
    }

    private void initDaysFilter() {
        slTimeRange.setMin(FilterCheck.FILTER_ALL_OR_MIN);
        slTimeRange.setMax(FilterCheck.FILTER_TIME_RANGE_MAX_VALUE);
        slTimeRange.setShowTickLabels(true);

        slTimeRange.setMajorTickUnit(10);
        slTimeRange.setBlockIncrement(5);

        slTimeRange.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double x) {
                if (x == FilterCheck.FILTER_ALL_OR_MIN) return "alles";

                return x.intValue() + "";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        slTimeRange.setValue(progData.filterWorker.getActFilterSettings().getTimeRange());
        setLabelSlider();
        progData.filterWorker.getActFilterSettings().timeRangeProperty().addListener(
                l -> slTimeRange.setValue(progData.filterWorker.getActFilterSettings().getTimeRange()));

        // kein direktes binding wegen: valueChangingProperty, nur melden wenn "steht"
        slTimeRange.valueProperty().addListener((o, oldV, newV) -> {
            setLabelSlider();
            if (!slTimeRange.isValueChanging()) {
                progData.filterWorker.getActFilterSettings().setTimeRange((int) slTimeRange.getValue());
            }
        });

        slTimeRange.valueChangingProperty().addListener((observable, oldvalue, newvalue) -> {
                    if (!newvalue) {
                        progData.filterWorker.getActFilterSettings().setTimeRange((int) slTimeRange.getValue());
                    }
                }
        );
    }

    private void initDurFilter() {
        slDur.minValueProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().minDurProperty());
        slDur.maxValueProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().maxDurProperty());
    }

    private void initFilmTimeAndDateFilter() {
        slTime.set24h();
        slTime.minValueProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().minTimeProperty());
        slTime.maxValueProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().maxTimeProperty());
        tglFilmTime.selectedProperty().bindBidirectional(progData.filterWorker.getActFilterSettings().minMaxTimeInvertProperty());

        pDatePicker.valueProperty().addListener((u, o, n) -> {
            LocalDate newDate = pDatePicker.getDateLDate();
            if (newDate != null) {
                try {
                    progData.filterWorker.getActFilterSettings().setShowDate(P2LDateFactory.toString(newDate));
                } catch (Exception ex) {
                    progData.filterWorker.getActFilterSettings().setShowDate(FilterCheck.FILTER_SHOW_DATE_ALL);
                }

            } else {
                progData.filterWorker.getActFilterSettings().setShowDate(FilterCheck.FILTER_SHOW_DATE_ALL);
            }
        });
        progData.filterWorker.getActFilterSettings().showDateProperty().addListener((observable, oldValue, newValue) -> {
            initPDatePicker();
        });
        initPDatePicker();
        btnClearDatePicker.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnClearDatePicker.setOnAction(a -> pDatePicker.clearDate());
    }

    private void initPDatePicker() {
        try {
            final String s = progData.filterWorker.getActFilterSettings().getShowDate();
            if (!P2LDateFactory.fromString(s).equals(LocalDate.MIN) && !s.isEmpty()) {
                LocalDate localDate = P2LDateFactory.fromString(progData.filterWorker.getActFilterSettings().getShowDate());
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
        initFilmTimeAndDateFilter();
        VBox vBox;

        // Tage
        vBox = new VBox(2);
        HBox h = new HBox();
        HBox hh = new HBox();
        h.getChildren().addAll(lblTimeRange, hh, lblTimeRangeValue);
        HBox.setHgrow(hh, Priority.ALWAYS);
        lblTimeRange.setMinWidth(0);
        vBox.getChildren().addAll(h, slTimeRange);
        vBox.visibleProperty().bind(progData.filterWorker.getActFilterSettings().timeRangeVisProperty());
        vBox.managedProperty().bind(progData.filterWorker.getActFilterSettings().timeRangeVisProperty());
        getChildren().addAll(vBox);

        // MinMax Dauer
        slDur.visibleProperty().bind(progData.filterWorker.getActFilterSettings().minMaxDurVisProperty());
        slDur.managedProperty().bind(progData.filterWorker.getActFilterSettings().minMaxDurVisProperty());
        getChildren().addAll(slDur);

        // MinMax Uhrzeit
        vBox = new VBox(2);
        vBox.getChildren().addAll(slTime, tglFilmTime);
        vBox.visibleProperty().bind(progData.filterWorker.getActFilterSettings().minMaxTimeVisProperty());
        vBox.managedProperty().bind(progData.filterWorker.getActFilterSettings().minMaxTimeVisProperty());
        getChildren().addAll(vBox);

        //Sendedatum
        vBox = new VBox(2);
        HBox hBox = new HBox(5);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(pDatePicker, btnClearDatePicker);
        HBox.setHgrow(pDatePicker, Priority.ALWAYS);
        pDatePicker.setMaxWidth(Double.MAX_VALUE);

        vBox.getChildren().addAll(lblShowDate, hBox);
        vBox.visibleProperty().bind(progData.filterWorker.getActFilterSettings().showDateVisProperty());
        vBox.managedProperty().bind(progData.filterWorker.getActFilterSettings().showDateVisProperty());
        getChildren().addAll(vBox);
    }

    private void addCheckFilter() {
        P2CheckComboBox checkOnly = new P2CheckComboBox();
        P2CheckComboBox checkNot = new P2CheckComboBox();
        Label lblOnly = new Label("Anzeigen:");
        Label lblNot = new Label("Ausschließen:");

        final String ONLY_BOOKMARK = "Bookmarks";
        final String ONLY_HD = "HD";
        final String ONLY_UT = "UT";
        final String ONLY_MARK = "Markiert";
        final String ONLY_NEW = "Neue";
        final String ONLY_LIVE = "Livestreams";
        final String ONLY_AKT_HISTORY = "Aktuelle History";

        final String NOT_ABO = "Abos";
        final String NOT_HISTORY = "Gesehene";
        final String NOT_DOUBLE = "Doppelte";
        final String NOT_GEO = "Geo geblockt";
        final String NOT_FUTURE = "Zukunft";

        checkOnly.setEmptyText("Alles");
        checkOnly.addItem(ONLY_BOOKMARK, "Nur Filme der Bookmarks anzeigen", progData.filterWorker.getActFilterSettings().onlyBookmarkProperty());
        checkOnly.addItem(ONLY_HD, "Nur HD-Filme anzeigen", progData.filterWorker.getActFilterSettings().onlyHdProperty());
        checkOnly.addItem(ONLY_UT, "Nur Filme mit Untertitel anzeigen", progData.filterWorker.getActFilterSettings().onlyUtProperty());
        checkOnly.addItem(ONLY_MARK, "Nur markierte Filme anzeigen", progData.filterWorker.getActFilterSettings().onlyMarkProperty());
        checkOnly.addItem(ONLY_NEW, "Nur neue Filme anzeigen", progData.filterWorker.getActFilterSettings().onlyNewProperty());
        checkOnly.addItem(ONLY_LIVE, "Nur Livestreams anzeigen", progData.filterWorker.getActFilterSettings().onlyLiveProperty());
        checkOnly.addItem(ONLY_AKT_HISTORY, "Nur die aktuelle History anzeigen", progData.filterWorker.getActFilterSettings().onlyActHistoryProperty());

        checkNot.setEmptyText("Nichts");
        checkNot.addItem(NOT_ABO, "Keine Filme für die es ein Abo gibt, anzeigen", progData.filterWorker.getActFilterSettings().notAboProperty());
        checkNot.addItem(NOT_HISTORY, "Bereits gesehene Filme nicht anzeigen", progData.filterWorker.getActFilterSettings().notHistoryProperty());
        checkNot.addItem(NOT_DOUBLE, "Doppelte Filme nur einmal anzeigen", progData.filterWorker.getActFilterSettings().notDoubleProperty());
        checkNot.addItem(NOT_GEO, "Geo-geblockte Filme nicht anzeigen", progData.filterWorker.getActFilterSettings().notGeoProperty());
        checkNot.addItem(NOT_FUTURE, "Keine Filme mit Datum in der Zukunft anzeigen", progData.filterWorker.getActFilterSettings().notFutureProperty());

        VBox vBox = new VBox(2);
        vBox.getChildren().addAll(lblOnly, checkOnly);
        vBox.visibleProperty().bind(progData.filterWorker.getActFilterSettings().onlyVisProperty());
        vBox.managedProperty().bind(progData.filterWorker.getActFilterSettings().onlyVisProperty());
        getChildren().add(vBox);

        vBox = new VBox(2);
        vBox.getChildren().addAll(lblNot, checkNot);
        vBox.visibleProperty().bind(progData.filterWorker.getActFilterSettings().notVisProperty());
        vBox.managedProperty().bind(progData.filterWorker.getActFilterSettings().notVisProperty());
        getChildren().add(vBox);
    }

    private void setLabelSlider() {
        final String txtAll = "Alles";

        int i = (int) slTimeRange.getValue();
        String tNr = i + "";

        if (i == FilterCheck.FILTER_ALL_OR_MIN) {
            lblTimeRangeValue.setText(txtAll);
        } else {
            lblTimeRangeValue.setText(tNr + (i == 1 ? " Tag" : " Tage"));
        }
    }
}
