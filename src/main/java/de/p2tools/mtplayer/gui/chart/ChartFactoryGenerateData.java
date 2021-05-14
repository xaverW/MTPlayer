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
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.Download;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.util.Iterator;
import java.util.Set;

public class ChartFactoryGenerateData {
    private static int runChart = 0;
    private static long[] bandwidthSumArr = new long[ChartFactory.MAX_CHART_DATA_PER_SCREEN];

    private ChartFactoryGenerateData() {
    }

    private static synchronized long getMaxYChartData(ChartData chartData) {
        // und jetzt die sichtbaren Daten eintragen
        final boolean chartOnlyRunning = ProgConfig.DOWNLOAD_CHART_ONLY_RUNNING.getBool();
        final boolean chartOnlyExisting = ProgConfig.DOWNLOAD_CHART_ONLY_EXISTING.getBool();

        long maxValue = 0;
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

            final double aktTime_sec = (chartData.getCountSek());
            int bandwidthIdx = (int) Math.round((aktTime_sec - bandwidthData.getStartTimeSec()) / ChartFactory.DATA_ALL_SECONDS);
            if (bandwidthIdx < 0) {
                bandwidthIdx = 0;
            }

            System.out.println("max (bandwidthIdx): " + bandwidthData.get(bandwidthIdx));
            System.out.println("max (0): " + bandwidthData.get(0));

            for (int i = bandwidthIdx; i < bandwidthData.size(); ++i) {
                long actVal = bandwidthData.get(i);
                if (actVal > maxValue) {
                    maxValue = actVal;
                }
            }
        }
        return maxValue;
    }

    private static synchronized void cleanUpData(ChartData chartData, ProgData progData) {
        chartData.setScale(1);
        boolean foundDownload;

        //alle Messpunkte vor MAX-Zeit löschen
        chartData.getBandwidthDataList().stream().forEach(bandwidthData -> {
            while (bandwidthData.size() > ChartFactory.MAX_DATA) {
                bandwidthData.removeFirst();
            }
            while ((chartData.getCountSek() - bandwidthData.getStartTimeSec()) > ChartFactory.MAX_SECONDS_SHOWING) {
                if (bandwidthData.isEmpty()) {
                    break;
                }
                bandwidthData.removeFirst();
            }
        });

        //Downloads die es nicht mehr gibt: "Download" entfernen und
        //wurde bereits alles aus "BandwidthData" gelöscht, kommts auch weg
        Iterator<BandwidthData> it = chartData.getBandwidthDataList().listIterator();
        while (it.hasNext()) {
            foundDownload = false;
            final BandwidthData bandwidthData = it.next();
            for (final Download download : progData.downloadList) {
                if (bandwidthData.getDownload() != null && bandwidthData.getDownload().equals(download)) {
                    foundDownload = true;
                    break;
                }
            }
            if (!foundDownload) {
                bandwidthData.setDownload(null);
                if (bandwidthData.isEmpty()) {
                    it.remove();
                }
            }
        }

        //fertige Downloads abschließen, Download gibts nicht mehr oder läuft nicht mehr,
        //dann einen Wert "0" anfügen, wenn noch nicht geschehen
        it = chartData.getBandwidthDataList().listIterator();
        while (it.hasNext()) {
            BandwidthData bandwidthData = it.next();
            foundDownload = false;
            for (final Download download : progData.downloadList.getListOfStartsNotFinished(DownloadConstants.ALL)) {
                if (bandwidthData.getDownload() != null && bandwidthData.getDownload().equals(download)) {
                    foundDownload = true;
                    break;
                }
            }
            if (!foundDownload) {
                int size = bandwidthData.size();
                if (size > 0 && bandwidthData.get(size - 1).longValue() > 0) {
                    bandwidthData.add(0L);
                }
            }
        }
    }

    private static synchronized void inputDownloadData(ChartData chartData, ProgData progData) {
        //Downloads in BandwidthData eintragen und jeden Download prüfen
        boolean foundDownload;
        for (final Download download : progData.downloadList) {
            foundDownload = false;
            for (final BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
                if (bandwidthData.getDownload() != null && bandwidthData.getDownload().equals(download)) {
                    foundDownload = true;
                    break;
                }
            }
            if (!foundDownload && download.isStarted()) {
                // dann ist es ein neue gestarteter
                BandwidthData bwd = new BandwidthData(download, chartData.getCountSek());
                chartData.getBandwidthDataList().add(bwd);
            }
        }

        for (final BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
            final Download download = bandwidthData.getDownload();
            if (download != null && download.isStateStartedRun()) {
                // sonst läuft er noch nicht/nicht mehr
                final long bandw = download.getStart().getBandwidth();
                bandwidthData.add(bandw);
            } else {
                bandwidthData.add(0L);
            }

            //damit beim Pausieren die Nummer nicht verloren geht
            if (download != null && download.getNo() != DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED) {
                bandwidthData.setName(download.getNo() + "");
            } else if (download != null && download.getFilm() != null) {
                final String fNo = download.getFilm().getNo() == DownloadConstants.FILM_NUMBER_NOT_FOUND ?
                        " " : "[" + download.getFilm().getNo() + "]";
                bandwidthData.setName(fNo);
            }
        }
    }

    public static synchronized void generateChartData(LineChart<Number, Number> lineChart, ChartData chartData) {
        // und jetzt die sichtbaren Daten eintragen
        final boolean separatBool = ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool();
        if (separatBool) {
            generateChartDataSeparated(lineChart, chartData);
        } else {
            generateChartDataAll(lineChart, chartData);
        }
    }

    private static synchronized void generateChartDataSeparated(LineChart<Number, Number> lineChart, ChartData chartData) {
        // und jetzt die sichtbaren Daten eintragen
        final boolean chartOnlyRunning = ProgConfig.DOWNLOAD_CHART_ONLY_RUNNING.getBool();
        final boolean chartOnlyExisting = ProgConfig.DOWNLOAD_CHART_ONLY_EXISTING.getBool();

        int startChartDataSecond = chartData.getCountSek() - chartData.getDownloadChartMaxTimeMinutes() * 60;
        if (startChartDataSecond < 0) {
            startChartDataSecond = 0;
        }
        final double secondsPerTickInTheChart = 1.0 * (chartData.getCountSek() - startChartDataSecond) / ChartFactory.MAX_CHART_DATA_PER_SCREEN;

        setBandwidthShowing(chartData, secondsPerTickInTheChart);
        genChartSeries(chartData); //Anzahl der XYChart.Series<Number, Number> wird angelegt
        lineChart.setData(chartData.getChartSeriesList_SeparateCharts());

        int countChart = 0;
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

            final XYChart.Series<Number, Number> cSeries = chartData.getChartSeriesList_SeparateCharts().get(countChart++);
            cSeries.setName(bandwidthData.getName());

            boolean onlyNull = true;
            if (bandwidthData.getDownload() != null) {
                //dann ist er noch da und wird auch immer angezeigt, auch wenn Bandbreite 0
                onlyNull = false;
            }
            for (int i = 0; i < ChartFactory.MAX_CHART_DATA_PER_SCREEN; ++i) {
                final int indexChart = ChartFactory.MAX_CHART_DATA_PER_SCREEN - 1 - i;
                final double aktTime_sec = (chartData.getCountSek() - secondsPerTickInTheChart * i);
                final double time = (chartData.getCountSek() - secondsPerTickInTheChart * i) / 60.0;
                final long actVal;
                int bandwidthIdx = (int) Math.round((aktTime_sec - bandwidthData.getStartTimeSec()) / ChartFactory.DATA_ALL_SECONDS);
                if (bandwidthIdx >= 0 && bandwidthIdx < bandwidthData.size()) {
                    actVal = bandwidthData.get(bandwidthIdx);
                    if (actVal > 0) {
                        onlyNull = false;
                    }
                } else {
                    //gibts keine Downloads
                    actVal = 0;
                }
                cSeries.getData().get(indexChart).setXValue(time);
                cSeries.getData().get(indexChart).setYValue(actVal);
            }
            if (onlyNull) {
                cSeries.setName("NO_DOWN");
                chartData.getChartSeriesList_SeparateCharts().remove(cSeries);
            }
        }
        colorChartName(lineChart, chartData);
//         logData(chartData, displayMinTimeShowing_sec, false);
    }

    private static synchronized void generateChartDataAll(LineChart<Number, Number> lineChart, ChartData chartData) {

        int displayMinTimeShowing_sec = chartData.getTimeShowingSeconds();
        final double timePerTick_sec = 1.0 * (chartData.getCountSek() - displayMinTimeShowing_sec) / ChartFactory.MAX_CHART_DATA_PER_SCREEN;

        //löschen und dann die aktuellen Downloads eintragen
        for (int i = 0; i < bandwidthSumArr.length; ++i) {
            bandwidthSumArr[i] = 0;
        }

        for (BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
            if (bandwidthData.size() == 0) {
                continue;
            }

            for (int i = 0; i < ChartFactory.MAX_CHART_DATA_PER_SCREEN; ++i) {

                final int indexChart = ChartFactory.MAX_CHART_DATA_PER_SCREEN - 1 - i;
                final double aktTime_sec = (chartData.getCountSek() - timePerTick_sec * i);
                final long actVal;

                int bandwidthIdx = (int) Math.round((aktTime_sec - bandwidthData.getStartTimeSec()) / ChartFactory.DATA_ALL_SECONDS);
                if (bandwidthIdx >= 0 && bandwidthIdx < bandwidthData.size()) {
                    actVal = bandwidthData.get(bandwidthIdx);
                } else {
                    //gibts keine Downloads
                    actVal = 0;
                }
                bandwidthSumArr[indexChart] = bandwidthSumArr[indexChart] + actVal;
            }
        }

        //ins Chart eintragen
        for (int i = 0; i < ChartFactory.MAX_CHART_DATA_PER_SCREEN; ++i) {
            final int indexChart = ChartFactory.MAX_CHART_DATA_PER_SCREEN - 1 - i;
            final double time = (chartData.getCountSek() - timePerTick_sec * i) / 60.0;
            final long val = bandwidthSumArr[indexChart];

            chartData.getChartSeriesSum().getData().get(indexChart).setXValue(time);
            chartData.getChartSeriesSum().getData().get(indexChart).setYValue(val);
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

    public static synchronized void zoomXAxis(LineChart<Number, Number> lineChart, ChartData chartData) {
        final NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setUpperBound(chartData.getCountMin());
        final double MIN = chartData.getCountMin() - chartData.getDownloadChartMaxTimeMinutes();
        if (MIN <= 0) {
            xAxis.setLowerBound(0);
            return;
        }
        xAxis.setLowerBound(MIN);
    }

    public static synchronized void zoomYAxis(LineChart<Number, Number> lineChart, ChartData chartData) {
        double max = 0;
        int scale = 1;
        final ObservableList<XYChart.Series<Number, Number>> list;

        if (ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool()) {
            list = chartData.getChartSeriesList_SeparateCharts();
        } else {
            list = chartData.getChartSeriesList_OneSumChart();
        }

        for (final XYChart.Series<Number, Number> cSeries : list) {
            for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                if (date.getYValue().longValue() > max) {
                    max = date.getYValue().longValue();
                }
            }
        }

        while (max > 25_000) {
            max /= 1_000;
            scale *= 1_000;
            chartData.setScale(chartData.getScale() * 1000);
        }

        double absMax = getMaxYChartData(chartData);
        absMax /= scale;
        if (absMax > max) {
            PLog.sysLog("=====> absMax: " + absMax + "  max: " + max);
            max = absMax;
        }

        int lowDiv = max > 250 ? 100 : 50;
        long upper = Math.round(max / lowDiv);
        upper *= lowDiv;
        upper = upper < max ? upper + lowDiv : upper;
        upper = upper <= 0 ? lowDiv : upper;
        int unit = Math.round(upper / 5);

        if (upper < max) {
            PLog.sysLog("=====> max: " + max + "  upper: " + upper + "  unit: " + unit);
        }
        PLog.sysLog("absMax: " + absMax + "  max: " + max + "  upper: " + upper + "  unit: " + unit);

        NumberAxis axis = (NumberAxis) lineChart.getYAxis();
        axis.setUpperBound(upper);
        axis.setTickUnit(unit);
        setYAxisLabel(lineChart, chartData);

        for (final XYChart.Series<Number, Number> cSeries : list) {
            for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                date.setYValue(date.getYValue().longValue() / chartData.getScale());
            }
        }
    }

    public static void setYAxisLabel(LineChart<Number, Number> lineChart, ChartData chartData) {
        switch (chartData.getScale()) {
            case 1:
                lineChart.getYAxis().setLabel("Bandbreite [B/s]");
                break;
            case 1_000:
                lineChart.getYAxis().setLabel("Bandbreite [kB/s]");
                break;
            case 1_000_000:
                lineChart.getYAxis().setLabel("Bandbreite [MB/s]");
                break;
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
