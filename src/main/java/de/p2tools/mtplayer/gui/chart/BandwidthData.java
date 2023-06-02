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

import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class BandwidthData extends ArrayList<Long> {
    private DownloadData download;
    private int downloadState = DownloadConstants.STATE_INIT;
    private String name = "";
    private int startTimeSec; //Startzeit in Sekunden
    private boolean isShowing = false;
    private long tmpData = 0;
    private int tmpCount = 0;

    final private ChartData chartData;
    private int dataAllSecond = ChartFactory.DATA_ALL_SECONDS;

    public BandwidthData(ChartData chartData, DownloadData download) {
        this.chartData = chartData;
        this.download = download;
        setDownloadState();
    }

    public DownloadData getDownload() {
        return download;
    }

    public void setDownload(DownloadData download) {
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

    public boolean isShowing() {
        return isShowing;
    }

    public void setShowing(boolean showing) {
        isShowing = showing;
    }

    public int getDataAllSecond() {
        return dataAllSecond;
    }

    public void setDataAllSecond(int dataAllSecond) {
        this.dataAllSecond = dataAllSecond;
    }

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

    private void genData() {
    }
}