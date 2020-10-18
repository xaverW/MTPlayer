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


package de.p2tools.mtplayer.tools;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.util.StringConverter;
import javafx.util.converter.BooleanStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MLConfigs {

    private final String key;
    private final String initValue;
    private final StringProperty actValue;

    public MLConfigs(String key) {
        this.key = key;
        initValue = "";
        actValue = new SimpleStringProperty(initValue);
    }

    public MLConfigs(String key, String init) {
        this.key = key;
        initValue = init;
        actValue = new SimpleStringProperty(initValue);
    }

    public MLConfigs(String key, int init) {
        this.key = key;
        initValue = String.valueOf(init);
        actValue = new SimpleStringProperty(initValue);
    }

    public MLConfigs(String key, boolean init) {
        this.key = key;
        initValue = String.valueOf(init);
        actValue = new SimpleStringProperty(initValue);
    }

    public synchronized void setValue(String value) {
        actValue.setValue(value == null ? "" : value);
    }

    public synchronized void setValue(boolean value) {
        actValue.setValue(String.valueOf(value));
    }

    public synchronized void setValue(int value) {
        actValue.setValue(String.valueOf(value));
    }

    public synchronized void setValue(long value) {
        actValue.setValue(String.valueOf(value));
    }

    public synchronized String get() {
        // beim binding mit Comboboxen kommts beim "Umstellen" zu NULL
        return actValue.getValueSafe();
    }

    public synchronized int getInt() {
        int ret;
        try {
            ret = Integer.parseInt(actValue.getValue());
        } catch (final Exception ignore) {
            actValue.setValue("0");
            ret = 0;
        }
        return ret;
    }

    public synchronized long getLong() {
        long ret;
        try {
            ret = Long.parseLong(actValue.getValue());
        } catch (final Exception ignore) {
            actValue.setValue("0");
            ret = 0;
        }
        return ret;
    }

    public synchronized double getDouble() {
        double ret;
        try {
            final String s = actValue.getValue();
            ret = Double.parseDouble(s);
        } catch (final Exception ignore) {
            actValue.setValue("0");
            ret = 0;
        }
        return ret;
    }

    public synchronized boolean getBool() {
        return Boolean.parseBoolean(actValue.getValueSafe());
    }

    public synchronized StringProperty getStringProperty() {
        return actValue;
    }

    public synchronized BooleanProperty getBooleanProperty() {
        final StringProperty sp = getStringProperty();
        final BooleanProperty ip = new SimpleBooleanProperty();
        try {
            ip.setValue(Boolean.parseBoolean(sp.getValue()));
        } catch (final Exception ex) {
            sp.setValue(Boolean.TRUE.toString());
            ip.setValue(true);
        }
        final StringConverter<Boolean> converter = new BooleanStringConverter();
        Bindings.bindBidirectional(sp, ip, converter);

        return ip;
    }

    public IntegerProperty getIntegerProperty() {
        final StringProperty sp = getStringProperty();
        final IntegerProperty ip = new SimpleIntegerProperty();
        try {
            ip.setValue(Integer.parseInt(sp.getValue()));
        } catch (final Exception ex) {
            sp.setValue("0");
            ip.setValue(0);
        }
        final StringConverter<Number> converter = new NumberStringConverter(new DecimalFormat("##"));
        Bindings.bindBidirectional(sp, ip, converter);

        return ip;
    }

    public synchronized DoubleProperty getDoubleProperty() {
        final StringProperty sp = getStringProperty();
        final DoubleProperty dp = new SimpleDoubleProperty();
        try {
            dp.setValue(Double.parseDouble(sp.getValue()));
        } catch (final Exception ex) {
            sp.setValue("0");
            dp.setValue(0);
        }

        final Locale locale = new Locale("en", "US");
        final NumberFormat nf = NumberFormat.getNumberInstance(locale);
        final DecimalFormat df = (DecimalFormat) nf;

        Bindings.bindBidirectional(sp, dp, new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object == null ? "0" : df.format(object);
            }

            @Override
            public Number fromString(String string) {
                return (string != null && !string.isEmpty()) ? Double.valueOf(string) : 0;
            }
        });

        return dp;
    }

    public String getKey() {
        return key;
    }

    public String getInitValue() {
        return initValue;
    }

    public StringProperty getActValue() {
        return actValue;
    }
}

