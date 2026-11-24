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

package de.p2tools.mtplayer.controller.data.compdata;

import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.config.Config_stringProp;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import de.p2tools.p2lib.mediathek.filter.Filter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

public class CompDataProps extends P2DataSample<CompDataProps> {

    public static final String TAG = "CompData";

    private int no = 0;
    private final StringProperty text = new SimpleStringProperty("");

    private String textLow = "";
    private boolean regEx = false;
    private Pattern pattern = null;

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> configList = new ArrayList<>();
        configList.add(new Config_stringProp("text", text));
        return configList.toArray(new Config[]{});
    }

    public CompData getCopy() {
        CompData bl = new CompData();
        bl.textProperty().set(bl.getText());
        return bl;
    }

    // ================
    public void init() {
        textLow = text.getValueSafe().toLowerCase(Locale.ROOT);
        regEx = Filter.isPattern(text.getValueSafe());
        pattern = Filter.makePattern(getText());
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getText() {
        return text.get();
    }

    public void setText(String text) {
        this.text.set(text);
        init();
    }

    public StringProperty textProperty() {
        return text;
    }

    public String getTextLow() {
        return textLow;
    }

    public boolean isRegEx() {
        return regEx;
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return text.getValueSafe();
    }
}
