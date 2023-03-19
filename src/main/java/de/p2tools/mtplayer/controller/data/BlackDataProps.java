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

package de.p2tools.mtplayer.controller.data;

import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.config.Config_boolProp;
import de.p2tools.p2lib.configfile.config.Config_stringProp;
import de.p2tools.p2lib.configfile.pdata.PData;
import de.p2tools.p2lib.configfile.pdata.PDataSample;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class BlackDataProps extends PDataSample<BlackDataProps> {

//    public static final String BLACKLIST_SENDER = "Sender";
//    public static final String BLACKLIST_THEME = "Thema";
//    public static final String BLACKLIST_THEME_EXACT = "Thema exakt";
//    public static final String BLACKLIST_TITLE = "Titel";
//    public static final String BLACKLIST_THEME_TITLE = "Thema/Titel";

    public static final String TAG = "BlackData";
//    public static final String[] XML_NAMES = {
//            "black-sender",
//            "black-thema",
//            "black-thema-exakt",
//            "black-titel",
//            "black-thema-titel"};

//    public String[] arr;

    private int no = 0;
    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final BooleanProperty themeExact = new SimpleBooleanProperty(true);
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty themeTitle = new SimpleStringProperty("");
    private int countHits = 0;

    public BlackDataProps() {
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new Config_stringProp("channel", channel));
        list.add(new Config_stringProp("theme", theme));
        list.add(new Config_boolProp("themeExact", themeExact));
        list.add(new Config_stringProp("title", title));
        list.add(new Config_stringProp("themeTitle" + PData.TAGGER + "bithemeTitletrate", themeTitle));
        return list.toArray(new Config[]{});
    }

    public BlackData getCopy() {
        BlackData bl = new BlackData();
        bl.setChannel(channel.getValueSafe());
        bl.setTheme(theme.getValueSafe());
        bl.setThemeExact(themeExact.getValue());
        bl.setTitle(title.getValueSafe());
        bl.setThemeTitle(themeTitle.getValueSafe());
        return bl;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getChannel() {
        return channel.get();
    }

    public StringProperty channelProperty() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel.set(channel);
    }

    public String getTheme() {
        return theme.get();
    }

    public StringProperty themeProperty() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme.set(theme);
    }

    public boolean isThemeExact() {
        return themeExact.get();
    }

    public BooleanProperty themeExactProperty() {
        return themeExact;
    }

    public void setThemeExact(boolean themeExact) {
        this.themeExact.set(themeExact);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getThemeTitle() {
        return themeTitle.get();
    }

    public StringProperty themeTitleProperty() {
        return themeTitle;
    }

    public void setThemeTitle(String themeTitle) {
        this.themeTitle.set(themeTitle);
    }

    public int getCountHits() {
        return countHits;
    }

    public void setCountHits(int countHits) {
        this.countHits = countHits;
    }

    public synchronized void incCountHits() {
        ++this.countHits;
    }

    public void clearCounter() {
        this.countHits = 0;
    }
}
