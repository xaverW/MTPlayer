package de.p2tools.mtplayer.controller.infotools;

import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.util.StringConverter;

public class MemoryFactory {
    private MemoryFactory() {
    }

    public static NumberAxis createXAxis() {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(true);
        xAxis.setTickUnit(10);
        setXAxisLabel(xAxis);
        xAxis.setLowerBound(0.0);
        xAxis.setSide(Side.RIGHT);
        xAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number object) {
                if (object.doubleValue() > 60) {
                    int i = (int) object.doubleValue();
                    return String.valueOf(i);
                } else {
                    int i = (int) (object.doubleValue() * 10);
                    return (String.valueOf(i / 10.0));
                }
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });
        return xAxis;
    }

    private static void setXAxisLabel(NumberAxis xAxis) {
        xAxis.setLabel("Laufzeit [s]");
    }

    public static NumberAxis createYAxis() {
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);
        yAxis.setLowerBound(0.0);
        yAxis.setLabel("Speicher [MB]");
        return yAxis;
    }
}
