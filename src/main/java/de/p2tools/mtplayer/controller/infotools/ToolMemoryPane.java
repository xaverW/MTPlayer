package de.p2tools.mtplayer.controller.infotools;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2Text;
import de.p2tools.p2lib.tools.P2InfoFactory;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ToolMemoryPane extends VBox {
    private LineChart<Number, Number> lineChart = null;
    private final ObservableList<XYChart.Series<Number, Number>> chartSeriesList = FXCollections.observableArrayList();
    private final Runtime rt = Runtime.getRuntime();
    private static final int MEGABYTE = 1000 * 1000;
    private final ProgressBar progressBar = new ProgressBar();
    private final Label lblMemInfo = new Label("");
    private final Button btnGc = new Button("_Speicher aufräumen");
    private final Button btnReset = new Button("_Zurücksetzen");
    private final List<MemoryData> memoryDataList = new ArrayList<>();
    private final int MIN = 100; // minimale Anzahl (!endless, oder zum Umschalten auf [min]
    private final Slider slider = new Slider();
    private final Label lblSliderValue = new Label();

    public ToolMemoryPane() {
        init();
    }

    public Tab getTab() {
        Tab tab = new Tab("Speicher");
        tab.setClosable(false);
        tab.setContent(this);
        return tab;
    }

    public void close() {
        slider.valueProperty().unbindBidirectional(ProgConfig.Tools_DIALOG_SLIDER);
    }

    private void init() {
        this.setSpacing(P2LibConst.SPACING_VBOX);
        this.setPadding(new Insets(10));

        initChartData();
        initCharts();
        initProgress();
        initSlider();
    }

    private void initSlider() {
        slider.setMin(1);
        slider.setMax(10);
        slider.setShowTickLabels(true);
        slider.setMinorTickCount(1);
        slider.setMajorTickUnit(1);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);
    }

    private void initProgress() {
        btnGc.setOnAction(a -> System.gc());
        btnReset.setOnAction(a -> reset());
        slider.valueProperty().bindBidirectional(ProgConfig.Tools_DIALOG_SLIDER);
        slider.valueProperty().addListener((u, o, n) -> lblSliderValue.setText((int) slider.getValue() + " [min]"));
        lblSliderValue.setText((int) slider.getValue() + " [min]");
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setMinWidth(10);
        lblMemInfo.setMaxWidth(Region.USE_PREF_SIZE);

        int row = 0;
        GridPane gridPane = new GridPane(P2LibConst.DIST_GRIDPANE_HGAP, P2LibConst.DIST_GRIDPANE_VGAP);

        gridPane.add(P2Text.getLblTextBold("Speicherverbrauch des Programms"), 0, row, 3, 1);

        gridPane.add(new Label("Used/Max"), 0, ++row);
        gridPane.add(progressBar, 1, row);
        gridPane.add(lblMemInfo, 2, row);
        GridPane.setHgrow(progressBar, Priority.ALWAYS);

        gridPane.add(new Label(""), 0, ++row);

        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnReset, slider, P2GuiTools.getHDistance(20), lblSliderValue, P2GuiTools.getHBoxGrower(), btnGc);
        getChildren().addAll(gridPane, lineChart, hBox);
    }

    private void reset() {
        memoryDataList.clear();
    }

    private void initCharts() {
        lineChart = new LineChart<>(MemoryFactory.createXAxis(), MemoryFactory.createYAxis());
        lineChart.getStyleClass().add("thick-chart");
        lineChart.setLegendSide(Side.RIGHT);
        lineChart.setLegendVisible(true); // rechts die Anzeige der lines Namen
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        lineChart.setTitle("Speicher");
        VBox.setVgrow(lineChart, Priority.ALWAYS);
        lineChart.setData(chartSeriesList);

        //Update every second...
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1_000), ae -> getMem()));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setDelay(Duration.seconds(1));
        timeline.play();
    }

    private void getMem() {
        final long maxMem;
        if (P2InfoFactory.getOs() == P2InfoFactory.OperatingSystemType.LINUX) {
            maxMem = rt.totalMemory();
        } else {
            maxMem = rt.maxMemory();
        }

        final long freeMemory = rt.freeMemory();
        final long totalMemory = rt.totalMemory();
        final long usedMem = totalMemory - freeMemory;

        final long used = usedMem / MEGABYTE;
        final long free = freeMemory / MEGABYTE;
        final long total = maxMem / MEGABYTE;

        double usedD = 1.0 * usedMem / totalMemory;
        progressBar.setProgress(usedD);

        final String info = used + " von " + total + "MB";
        lblMemInfo.setText(info);

        MemoryData memoryData = new MemoryData(memoryDataList.size(), used, free, total);
        memoryDataList.add(memoryData);
        while (memoryDataList.size() > slider.getValue() * 60) {
            memoryDataList.remove(0);
        }

        if (memoryDataList.size() > MIN) {
            // min
            lineChart.getXAxis().setLabel("Laufzeit [min]");
        } else {
            lineChart.getXAxis().setLabel("Laufzeit [s]");
        }

        makeChartData();
    }

    private void initChartData() {
        XYChart.Series<Number, Number> max = new XYChart.Series<>("Max", FXCollections.observableArrayList());
        XYChart.Series<Number, Number> free = new XYChart.Series<>("Free", FXCollections.observableArrayList());
        XYChart.Series<Number, Number> used = new XYChart.Series<>("Used", FXCollections.observableArrayList());
        chartSeriesList.addAll(max, free, used);
    }

    private void makeChartData() {
        XYChart.Series<Number, Number> max = chartSeriesList.get(0);
        max.getData().clear();
        XYChart.Series<Number, Number> free = chartSeriesList.get(1);
        free.getData().clear();
        XYChart.Series<Number, Number> used = chartSeriesList.get(2);
        used.getData().clear();

        if (memoryDataList.isEmpty()) {
            return;
        }

        for (int i = 0; i < memoryDataList.size(); ++i) {
            MemoryData m = memoryDataList.get(i);
            final double factor;
            if (memoryDataList.size() > MIN) {
                factor = m.getxValue() / 60.0;
            } else {
                factor = m.getxValue();
            }


            used.getData().add(new XYChart.Data<>(factor, m.getUsedMem()));
            free.getData().add(new XYChart.Data<>(factor, m.getFreeMemory()));
            max.getData().add(new XYChart.Data<>(factor, m.getTotalMemory()));
        }
    }
}
