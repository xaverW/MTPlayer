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

import de.p2tools.mtplayer.controller.data.download.Download;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class BandwidthData extends ArrayList<Long> {
    private Download download;
    private int downloadState = DownloadConstants.STATE_INIT;
    private String name = "";
    private int startTimeSec; //Startzeit in Sekunden
    private boolean isShowing = false;
    private long tmpData = 0;
    private int tmpCount = 0;

    private int lastIdx = 0;
    //    private int amountDataPerPixel = 1;
    private int secondsPerPixel = 1;
    final private ChartData chartData;
    private int dataAllSecond = ChartFactory.DATA_ALL_SECONDS;
//    private final XYChart.Series<Number, Number> chartSeries;

    public BandwidthData(ChartData chartData, Download download) {
        this.chartData = chartData;
        this.download = download;
//        this.chartSeries = new XYChart.Series<>("", FXCollections.observableArrayList());
//        this.add(0L);
//        ChartFactory.initChartSeries(chartSeries);
        setDownloadState();
        genData();
    }

    public Download getDownload() {
        return download;
    }

    public void setDownload(Download download) {
        setDownloadState();
        this.download = download;
        setDownloadState();
    }

    public int getDownloadState() {
        setDownloadState();
        return downloadState;
    }

    private void setDownloadState() {
        if (this.download != null) {
            downloadState = download.getState();
        }
    }

    public void setStartTimeNow() {
        this.startTimeSec = chartData.getCountProgRunningTimeSeconds();
    }

    public void cleanUpData() {
        if (size() > 0 && get(size() - 1).longValue() > 0) {
            super.add(0L);
        }
    }

    public void addTmpData(long a) {
        ++tmpCount;
        tmpData += a;
    }

    public void addFromTmpData() {
        if (tmpCount > 0) {
            addData(tmpData / tmpCount);
        }
        tmpData = 0;
        tmpCount = 0;
    }

    private boolean addData(Long a) {
        if (this.isEmpty()) {
            //dann auch die Startzeit neu setzen
            setStartTimeNow();
        }
        genData();
        return super.add(a);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
//        chartSeries.setName(name);
    }

    public int getStartTimeSec() {
        return startTimeSec;
    }

//    public int getTimeSec(int sec) {
//        return startTimeSec + sec * dataAllSecond;
//    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setShowing(boolean showing) {
        isShowing = showing;
    }

//    public double getTimeMin(int sec) {
//        return getTimeSec(sec) / 60.0;
//    }

    public int getDataAllSecond() {
        return dataAllSecond;
    }

    public void setDataAllSecond(int dataAllSecond) {
        this.dataAllSecond = dataAllSecond;
    }

//    public XYChart.Series<Number, Number> getChartSeries() {
//        return chartSeries;
//    }

    public void removeFirst() {
        //ersten Wert entfernen und dann auch!! die Startzeit weiterschieben
        this.remove(0);
        this.startTimeSec += dataAllSecond;
    }

    public void fillData(XYChart.Series<Number, Number> chartSeries, boolean addData) {
        final int secondsPerPixel = chartData.getSecondsPerPixel();//nur vom Slider abhängig!
        int startIdx = 0, endIdx = 0;
        long value = 0;
        int extraIdx = 0;

        System.out.println("--------");
        if (!addData) {
            chartSeries.setName(getName());
        }
        if (this.size() % secondsPerPixel != 0) {
            extraIdx = this.size() % secondsPerPixel;
        }

        final int sizeWithoutExtra = this.size() - extraIdx;
        for (int i = 0; i < ChartFactory.MAX_CHART_DATA_PER_SCREEN; ++i) {
            //ChartFactory.MAX_CHART_DATA_PER_SCREEN-1 (aktuellster Wert) ... 0

            double actTimeMin = (chartData.getCountProgRunningTimeSeconds() - secondsPerPixel * i) / 60.0;//jetzt[min] ... vor[min]
            final int chartIndex = ChartFactory.MAX_CHART_DATA_PER_SCREEN - 1 - i;//MAX_CHART_DATA - 1 ... 0, aktuellster zuerst

            if (actTimeMin < 0) {
                if (!addData) {
                    chartSeries.getData().get(chartIndex).setYValue(0);
                }
                chartSeries.getData().get(chartIndex).setXValue(0);
                continue;
            }

            if (i == 0 && extraIdx != 0) {
                //dann ist das EXTRA das in das erste Pixel kommt
                startIdx = this.size() - extraIdx;
                endIdx = this.size();
                value = getValue(startIdx, endIdx);
                actTimeMin = (chartData.getCountProgRunningTimeSeconds() - secondsPerPixel * i) / 60.0;//jetzt[min] ... vor[min]

                if (addData) {
                    chartSeries.getData().get(chartIndex).setYValue(chartSeries.getData().get(chartIndex).getYValue().longValue() + value);
                } else {
                    chartSeries.getData().get(chartIndex).setYValue(value);
                }
                chartSeries.getData().get(chartIndex).setXValue(actTimeMin);
                System.out.println("   actTimeMin: " + (((int) (actTimeMin * 100)) / 100.0) + " EXTRA->" + " sizeWithoutExtra: " + sizeWithoutExtra + "/" + this.size() + " startIdx: " + startIdx + " endIdx: " + endIdx +
                        " value: " + value + " secondsPerPixel: " + secondsPerPixel);

            } else {
                //der Rest oder Wenn kein EXTRA alles
                int idx = extraIdx == 0 ? i : i - 1; //da wird wieder bei Null angefangen: neue Größe
                startIdx = sizeWithoutExtra - idx * secondsPerPixel / ChartFactory.DATA_ALL_SECONDS - secondsPerPixel / ChartFactory.DATA_ALL_SECONDS;
                endIdx = sizeWithoutExtra - idx * secondsPerPixel / ChartFactory.DATA_ALL_SECONDS;
                value = getValue(startIdx, endIdx);
                actTimeMin = (chartData.getCountProgRunningTimeSeconds() - secondsPerPixel * i) / 60.0;//jetzt[min] ... vor[min]

                if (endIdx <= 0) {
                    chartSeries.getData().get(chartIndex).setYValue(0);
                    chartSeries.getData().get(chartIndex).setXValue(actTimeMin);
                    continue;
                }

                if (addData) {
                    chartSeries.getData().get(chartIndex).setYValue(chartSeries.getData().get(chartIndex).getYValue().longValue() + value);
                } else {
                    chartSeries.getData().get(chartIndex).setYValue(value);
                }
                chartSeries.getData().get(chartIndex).setXValue(actTimeMin);
                System.out.println("   actTimeMin: " + (((int) (actTimeMin * 100)) / 100.0) + " sizeWithoutExtra: " + sizeWithoutExtra + "/" + this.size() + " startIdx: " + startIdx + " endIdx: " + endIdx +
                        " value: " + value + " secondsPerPixel: " + secondsPerPixel);
            }
        }
    }

    private long getValue(int from, int to) {
        long value = 0;
        int count = 0;

        for (int i = from; i < to; ++i) {
            if (i < 0 || i >= this.size()) {
                continue;
            }
            ++count;
            value += this.get(i);
        }

        if (count > 0) {
            value = value / count / chartData.getyScale();
        }
        return value;
    }

//    public long getSecondsBack(int chartIdx) {
//        int secondsPerPixel = chartData.getSecondsPerPixel();
//
//        int idx2, idx = this.size() - 1 - chartIdx / secondsPerPixel;
//        long ret = 0;
//        int count = 0;
//        int change = chartIdx / secondsPerPixel;
//
////        if (idx >= lastIdx - secondsPerPixel) {
////            //dann ists ein "Zwischenwert"
////            idx = lastIdx;
////        }
//        System.out.println("idx: " + idx + " - " + lastIdx + " secondsPerPixel: " + secondsPerPixel + " chartIdx: " + chartIdx);
//        lastIdx = idx;
//
//        if (idx >= 0 && idx < this.size()) {
//            ++count;
//            ret = this.get(idx);
//        }
//
//        idx2 = this.size() - 1 - (chartIdx - 1) / secondsPerPixel + 1;//eins vor dem "nächstem"
//        if (idx2 >= 0) {
//            for (int i = idx2; i < idx; ++i) {
//                ++count;
//                ret += this.get(i);
//            }
//        }
//
//        if (count > 0) {
//            ret = ret / count;
//        }
//        return ret;
//    }

//    public long getLast() {
//        if (this.isEmpty()) {
//            return 0;
//        }
//
//        return this.get(this.size() - 1);
//    }
//
//    public long removeLast() {
//        if (this.isEmpty()) {
//            return 0;
//        }
//
//        return this.remove(this.size() - 1);
//    }

    public int getMaxFirstIdx() {
        final int first = size() - (chartData.getDownloadChartMaxTimeMinutes() * 60) / dataAllSecond;
        return first < 0 ? 0 : first;
    }

    public boolean allValuesEmpty() {
        for (int i = getMaxFirstIdx(); i < size(); ++i) {
            if (get(i) > 0) {
                return false;
            }
        }
        return true;
    }

    public long getMaxValue() {
        long max = 0;
        for (int i = getMaxFirstIdx(); i < size(); ++i) {
            if (get(i) > max) {
                max = get(i);
            }
        }
        return max;
    }

//    public int getAmountDataPerPixel() {
//        return amountDataPerPixel;
//    }

    private void genData() {
        secondsPerPixel = chartData.getSecondsPerPixel();

//        amountDataPerPixel = (int) Math.round(1.0 * secondsPerPixel / ChartFactory.DATA_ALL_SECONDS);
//        if (amountDataPerPixel < secondsPerPixel / ChartFactory.DATA_ALL_SECONDS) {
//            ++amountDataPerPixel;
//        }
//        amountDataPerPixel = amountDataPerPixel <= 0 ? 1 : amountDataPerPixel;
    }
}