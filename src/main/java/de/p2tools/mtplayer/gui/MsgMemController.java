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

import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.p2lib.tools.ProgramToolsFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class MsgMemController extends AnchorPane {
    private final VBox vBoxCont = new VBox();
    private LineChart<Number, Number> lineChart = null;
    private final ObservableList<XYChart.Series<Number, Number>> listChart = FXCollections.observableArrayList();

    private int countSek = 0;
    private final Runtime rt = Runtime.getRuntime();
    private final Label lblMem = new Label("");
    private static final int MEGABYTE = 1000 * 1000;


    public MsgMemController() {
        initCharts();

        vBoxCont.setPadding(new Insets(10, 10, 10, 10));
        vBoxCont.setSpacing(5);
        AnchorPane.setTopAnchor(vBoxCont, 0.0);
        AnchorPane.setRightAnchor(vBoxCont, 0.0);
        AnchorPane.setBottomAnchor(vBoxCont, 0.0);
        AnchorPane.setLeftAnchor(vBoxCont, 0.0);

        VBox.setVgrow(lineChart, Priority.ALWAYS);
        vBoxCont.getChildren().addAll(lblMem, lineChart);
        this.getChildren().add(vBoxCont);

        PListener.addListener(new PListener(PListener.EVENT_TIMER_SECOND, MsgMemController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                searchInfos();
            }
        });
    }

    private NumberAxis createXAxis() {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(true);
        xAxis.setForceZeroInRange(false);
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                int i = (int) (object.doubleValue() * 10);
                return ((i / 10.0) + "");
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });
        return xAxis;
    }

    private NumberAxis createYAxis() {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(true);
        return xAxis;
    }

    private void initCharts() {
        lineChart = new LineChart<>(createXAxis(), createYAxis());

        listChart.add(new XYChart.Series<Number, Number>("Gesamtspeicher",
                FXCollections.observableArrayList()));
        listChart.add(new XYChart.Series<Number, Number>("Benutzt",
                FXCollections.observableArrayList()));

        lineChart.setData(listChart);
        lineChart.setTitle("Programmspeicher");
        lineChart.getXAxis().setLabel("Zeit [min]");
        lineChart.getYAxis().setLabel("[MByte]");
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
    }

    private synchronized void searchInfos() {
        ++countSek; // Sekunden
        if (countSek % 2 == 0) {
            // nur alle 2 Sekunden suchen, reicht
            return;
        }
        final double countMin = countSek / 60.0; // Minuten

        final long maxMem;
        if (ProgramToolsFactory.getOs() == ProgramToolsFactory.OperatingSystemType.LINUX) {
            maxMem = rt.totalMemory();
        } else {
            maxMem = rt.maxMemory();
        }

        final long totalMemory = rt.totalMemory();
        final long freeMemory = rt.freeMemory();
        final long usedMem = totalMemory - freeMemory;

        final long used = usedMem / MEGABYTE;
        final long total = maxMem / MEGABYTE;
        final String info = used + " von " + total + "MB";

        lineChart.setTitle("Programmspeicher" + "  [" + info + "]");
        listChart.get(0).getData().add(new XYChart.Data<>(countMin, total));
        listChart.get(1).getData().add(new XYChart.Data<>(countMin, used));
        for (final XYChart.Series<Number, Number> cSeries : listChart) {
            if (cSeries.getData().size() > 500 /* 1000s*/) {
                cSeries.getData().remove(0);
            }
        }
    }

}
