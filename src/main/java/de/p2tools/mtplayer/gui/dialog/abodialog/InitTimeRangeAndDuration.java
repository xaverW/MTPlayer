/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.gui.dialog.abodialog;

import de.p2tools.p2lib.mediathek.filter.FilterCheck;
import javafx.util.StringConverter;

import java.util.Arrays;

public class InitTimeRangeAndDuration {
    private final AddAboDto addAboDto;

    public InitTimeRangeAndDuration(AddAboDto addAboDto) {
        this.addAboDto = addAboDto;
        init();
    }

    private void init() {
        initDuration();
        initTimeRange();
    }

    public void makeAct() {
        // nach dem actFilm setzen, z.B. beim Wechsel
        addAboDto.p2RangeBoxDuration.maxValueProperty().setValue(addAboDto.getAct().abo.getMaxDurationMinute());
        addAboDto.p2RangeBoxDuration.minValueProperty().setValue(addAboDto.getAct().abo.getMinDurationMinute());
        addAboDto.slTimeRange.setValue(addAboDto.getAct().abo.getTimeRange());
    }

    private void initDuration() {
        addAboDto.p2RangeBoxDuration.maxValueProperty().addListener((u, o, n) -> setDurationMax());
        addAboDto.p2RangeBoxDuration.minValueProperty().addListener((u, o, n) -> setDurationMin());
        addAboDto.chkDurationAll.setOnAction(a -> {
            if (addAboDto.chkDurationAll.isSelected()) {
                setDurationMin();
                setDurationMax();
            }
        });
    }

    public void setDurationMin() {
        if (addAboDto.chkDurationAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(downloadAddData -> {
                downloadAddData.abo.setMinDurationMinute(addAboDto.p2RangeBoxDuration.getActMinValue());
            });

        } else {
            addAboDto.getAct().abo.setMinDurationMinute(addAboDto.p2RangeBoxDuration.getActMinValue());
        }
    }

    public void setDurationMax() {
        if (addAboDto.chkDurationAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(downloadAddData -> {
                downloadAddData.abo.setMaxDurationMinute(addAboDto.p2RangeBoxDuration.getActMaxValue());
            });

        } else {
            addAboDto.getAct().abo.setMaxDurationMinute(addAboDto.p2RangeBoxDuration.getActMaxValue());
        }
    }

    private void initTimeRange() {
        addAboDto.slTimeRange.setMin(FilterCheck.FILTER_ALL_OR_MIN);
        addAboDto.slTimeRange.setMax(FilterCheck.FILTER_TIME_RANGE_MAX_VALUE);
        addAboDto.slTimeRange.setShowTickLabels(true);
        addAboDto.slTimeRange.setMajorTickUnit(10);
        addAboDto.slTimeRange.setBlockIncrement(5);

        addAboDto.slTimeRange.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double x) {
                if (x == FilterCheck.FILTER_ALL_OR_MIN) return "Alles";
                return x.intValue() + "";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        // kein direktes binding wegen: valueChangingProperty, nur melden wenn "steht"
        addAboDto.slTimeRange.valueChangingProperty().addListener((observable, oldvalue, newvalue) -> {
            if (!newvalue) {
                setTimeRange();
            }
        });
        addAboDto.slTimeRange.valueProperty().addListener((observable, oldValue, newValue) -> {
            setLabelSlider();
        });
        setLabelSlider();
    }

    private void setLabelSlider() {
        final String txtAll = "Alles";
        int i = (int) addAboDto.slTimeRange.getValue();
        String tNr = i + "";
        if (i == FilterCheck.FILTER_ALL_OR_MIN) {
            addAboDto.lblTimeRange.setText(txtAll);
        } else {
            addAboDto.lblTimeRange.setText(tNr + (i == 1 ? " Tag" : " Tage"));
        }
    }

    public void setTimeRange() {
        if (addAboDto.chkTimeRangeAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(downloadAddData -> {
                downloadAddData.abo.setTimeRange((int) addAboDto.slTimeRange.getValue());
            });

        } else {
            addAboDto.getAct().abo.setTimeRange((int) addAboDto.slTimeRange.getValue());
        }
    }
}
