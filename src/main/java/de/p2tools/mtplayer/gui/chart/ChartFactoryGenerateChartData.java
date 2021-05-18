/*
 * P2tools Copyright (C) 2020 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.gui.chart;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.util.Set;

public class ChartFactoryGenerateChartData {

    private ChartFactoryGenerateChartData() {
    }

    public static synchronized void generateChartDataSeparated(LineChart<Number, Number> lineChart, ChartData chartData) {
        // und jetzt die sichtbaren Daten eintragen
        final boolean chartOnlyRunning = ProgConfig.DOWNLOAD_CHART_ONLY_RUNNING.getBool();
        final boolean chartOnlyExisting = ProgConfig.DOWNLOAD_CHART_ONLY_EXISTING.getBool();
        final double secondsPerTickInTheChart = chartData.getTimePerTick();

        setBandwidthShowing(chartData, secondsPerTickInTheChart);//feststellen ob noch ein Wert über 0 ist und zu sehen ist
        genChartSeries(chartData);//Anzahl der XYChart.Series wird angelegt
        lineChart.setData(chartData.getChartSeriesList_SeparateCharts());

        int bandwidthLastIdx = 0;//letzter Wert: Suchen von bandwidthIdx+1 bis bandwidthLastIdx
        for (int i = 0; i < ChartFactory.MAX_CHART_DATA_PER_SCREEN; ++i) {
            //0 ..... ChartFactory.MAX_CHART_DATA_PER_SCREEN-1

            int countChart = 0;
            final int indexChart = ChartFactory.MAX_CHART_DATA_PER_SCREEN - 1 - i;//ChartFactory.MAX_CHART_DATA_PER_SCREEN - 1 ... 0
            final int bandwidthTimeIdx = (int) Math.round(i * secondsPerTickInTheChart / ChartFactory.DATA_ALL_SECONDS);//0 ... xx, 0=letzter Wert in der Bandwidthliste
            final double actTimeMin = (chartData.getCountSek() - secondsPerTickInTheChart * i) / 60.0;//jetzt[min] ... vor[min]

            for (BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
                if (!bandwidthData.isShowing()) {
                    //hat dann keine sichtbaren Daten mehr
                    continue;
                }

                boolean downL = bandwidthData.getDownload() != null;
                boolean downLRunning = bandwidthData.getDownload() != null && bandwidthData.getDownload().isStateStartedRun();
                if (chartOnlyRunning && !downLRunning) {
                    //dann gibts den Download nicht mehr und soll auch nicht angezeigt werden
                    continue;
                } else if (chartOnlyExisting && !downL) {
                    //sollen nur laufende angezeigt werden
                    continue;
                }

                final XYChart.Series<Number, Number> cSeries = chartData.getChartSeriesList_SeparateCharts().get(countChart++);
                if (bandwidthData.getDownload() != null) {
                    cSeries.setName(bandwidthData.getName());
                } else {
                    cSeries.setName("-");
                }

                int count = 0;
                int actValue = 0;
                int bandwidthIdx = bandwidthData.size() - 1 - bandwidthTimeIdx;
                if (bandwidthIdx >= 0 && bandwidthIdx < bandwidthData.size()) {
                    ++count;
                    actValue += bandwidthData.get(bandwidthIdx);

                    for (int ii = bandwidthIdx + 1; ii < bandwidthLastIdx; ++ii) {
                        if (ii >= 0 && ii < bandwidthData.size()) {
                            ++count;
                            actValue += bandwidthData.get(ii);
                        }
                    }
                }
                bandwidthLastIdx = bandwidthIdx;
                if (count > 1) {
                    actValue /= count;
                }

                cSeries.getData().get(indexChart).setXValue(actTimeMin);
                cSeries.getData().get(indexChart).setYValue(actValue);
            }
        }
        colorChartName(lineChart, chartData);
    }

    public static synchronized void generateChartDataAll(LineChart<Number, Number> lineChart, ChartData chartData) {
        final double timePerTick_sec = chartData.getTimePerTick();
        int bandwidthLastIdx = 0;//letzter Wert: Suchen von bandwidthIdx+1 bis bandwidthLastIdx

        for (int i = 0; i < ChartFactory.MAX_CHART_DATA_PER_SCREEN; ++i) {
            //0 ..... ChartFactory.MAX_CHART_DATA_PER_SCREEN-1

            final int indexChart = ChartFactory.MAX_CHART_DATA_PER_SCREEN - 1 - i;//ChartFactory.MAX_CHART_DATA_PER_SCREEN - 1 ... 0
            final int bandwidthTimeIdx = (int) Math.round(i * timePerTick_sec / ChartFactory.DATA_ALL_SECONDS);//0 ... xx, 0=letzter Wert in der Bandwidthliste
            final double actTimeMin = (chartData.getCountSek() - timePerTick_sec * i) / 60.0;//jetzt[min] ... vor[min]

            double actVal = 0;
            for (BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
                if (bandwidthData.size() == 0) {
                    continue;
                }

                int count = 0;
                int act = 0;
                int bandwidthIdx = bandwidthData.size() - 1 - bandwidthTimeIdx;
                if (bandwidthIdx >= 0 && bandwidthIdx < bandwidthData.size()) {
                    ++count;
                    act += bandwidthData.get(bandwidthIdx);

                    for (int ii = bandwidthIdx + 1; ii < bandwidthLastIdx; ++ii) {
                        if (ii >= 0 && ii < bandwidthData.size()) {
                            ++count;
                            act += bandwidthData.get(ii);
                        }
                    }
                }
                bandwidthLastIdx = bandwidthIdx;
                if (count > 1) {
                    act /= count;
                }
                actVal += act;
            }

            chartData.getChartSeriesSum().getData().get(indexChart).setXValue(actTimeMin);
            chartData.getChartSeriesSum().getData().get(indexChart).setYValue(actVal);
        }
        colorChartName(lineChart, chartData);
    }

    private static void setBandwidthShowing(ChartData chartData, double timePerTick_sec) {
        //feststellen ob noch ein Wert über 0 ist und im sichtbaren Bereich zu sehen ist
        for (int bi = 0; bi < chartData.getBandwidthDataList().size(); ++bi) {
            BandwidthData bandwidthData = chartData.getBandwidthDataList().get(bi);
            boolean onlyNull = true;
            for (int i = 0; i < ChartFactory.MAX_CHART_DATA_PER_SCREEN; ++i) {
                final double aktTime_sec = (chartData.getCountSek() - timePerTick_sec * i);
                final long actVal;
                int bandwidthIdx = (int) Math.round((aktTime_sec - bandwidthData.getStartTimeSec()) / ChartFactory.DATA_ALL_SECONDS);
                if (bandwidthIdx >= 0 && bandwidthIdx < bandwidthData.size()) {
                    actVal = bandwidthData.get(bandwidthIdx);
                    if (actVal > 0) {
                        onlyNull = false;
                    }
                }
            }
            if (onlyNull && bandwidthData.getDownload() == null) {
                // dann wird kein Downloadwert mehr im sichtbaren Bereich des Displays angezeigt
                bandwidthData.setShowing(false);
            } else {
                bandwidthData.setShowing(true);
            }
        }
    }

    private static void genChartSeries(ChartData chartData) {
        //Anzahl der XYChart.Series<Number, Number> wird angelegt
        final boolean chartOnlyRunning = ProgConfig.DOWNLOAD_CHART_ONLY_RUNNING.getBool();
        final boolean chartOnlyExisting = ProgConfig.DOWNLOAD_CHART_ONLY_EXISTING.getBool();
        int countCseries = 0;

        for (int bi = 0; bi < chartData.getBandwidthDataList().size(); ++bi) {
            BandwidthData bandwidthData = chartData.getBandwidthDataList().get(bi);
            if (!bandwidthData.isShowing()) {
                continue;
            }
            boolean downL = bandwidthData.getDownload() != null;
            boolean downLRunning = bandwidthData.getDownload() != null && bandwidthData.getDownload().isStateStartedRun();
            if (chartOnlyRunning && !downLRunning) {
                //dann gibts den Download nicht mehr und soll auch nicht angezeigt werden
                continue;
            } else if (chartOnlyExisting && !downL) {
                //sollen nur laufende angezeigt werden
                continue;
            }
            ++countCseries;
        }

        int sumChartSeries = chartData.getChartSeriesList_SeparateCharts().size();
        if (sumChartSeries > countCseries) {
            chartData.getChartSeriesList_SeparateCharts().remove(0, sumChartSeries - countCseries);
        }
        while (sumChartSeries < countCseries) {
            final XYChart.Series<Number, Number> cSeries = new XYChart.Series<>("", FXCollections.observableArrayList());
            ChartFactory.initChartSeries(cSeries);
            chartData.getChartSeriesList_SeparateCharts().add(cSeries);
            ++sumChartSeries;
        }
    }

    private static synchronized void colorChartName(LineChart<Number, Number> lineChart, ChartData chartData) {
        // chart einfärben
        for (int bi = 0; bi < chartData.getBandwidthDataList().size(); ++bi) {
            BandwidthData bandwidthData = chartData.getBandwidthDataList().get(bi);
            setColor(lineChart, bandwidthData);
        }
    }

    private static synchronized void setColor(LineChart<Number, Number> lineChart, BandwidthData bandwidthData) {
        final String cRed = ProgConfig.SYSTEM_DARK_THEME.getBooleanProperty().get() ? "#ff0000" : "#de0000";
        final String cGreen = ProgConfig.SYSTEM_DARK_THEME.getBooleanProperty().get() ? "#00ff00" : "#00aa00";
        Set<Node> items = lineChart.lookupAll("Label.chart-legend-item");
        for (Node item : items) {
            Label label = (Label) item;
            if (label.getText().equals(bandwidthData.getName())) {
                switch (bandwidthData.getDownloadState()) {
                    case DownloadConstants.STATE_FINISHED:
                        label.setStyle(" -fx-text-fill: " + cGreen + ";");
                        break;
                    case DownloadConstants.STATE_ERROR:
                        label.setStyle(" -fx-text-fill: " + cRed + ";");
                        break;
                    default:
                        label.setStyle("");
                }
            }
        }
    }
}
