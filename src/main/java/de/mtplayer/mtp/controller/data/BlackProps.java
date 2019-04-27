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

package de.mtplayer.mtp.controller.data;

import de.mtplayer.mLib.tools.Data;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BlackProps extends Data<BlackProps> {


    public static final int BLACKLIST_NR = 0;
    public static final int BLACKLIST_SENDER = 1;
    public static final int BLACKLIST_SENDER_EXACT = 2;
    public static final int BLACKLIST_THEME = 3;
    public static final int BLACKLIST_THEME_EXACT = 4;
    public static final int BLACKLIST_TITLE = 5;
    public static final int BLACKLIST_THEME_TITLE = 6;
    public static final int BLACKLIST_COUNT_HITS = 7;

    public static final String TAG = "Blacklist";
    public static final String[] XML_NAMES = {
            "black-nr",
            "black-sender",
            "black-sender-exakt",
            "black-thema",
            "black-thema-exakt",
            "black-titel",
            "black-thema-titel",
            "black-count-hits"};
    public static int MAX_ELEM = XML_NAMES.length;

    public String[] arr;

    private int nr = 0;
    private final StringProperty channel = new SimpleStringProperty("");
    private final BooleanProperty channelExact = new SimpleBooleanProperty(true);
    private final StringProperty theme = new SimpleStringProperty("");
    private final BooleanProperty themeExact = new SimpleBooleanProperty(true);
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty themeTitle = new SimpleStringProperty("");
    private int countHits = 0;


    public BlackProps() {
        arr = super.makeArr(MAX_ELEM);
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

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
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

    public boolean getChannelExact() {
        return channelExact.get();
    }

    public BooleanProperty channelExactProperty() {
        return channelExact;
    }

    public void setChannelExact(boolean channelExact) {
        this.channelExact.set(channelExact);
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

    public boolean getThemeExact() {
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

    public void setPropsFromXml() {
        setChannel(arr[BLACKLIST_SENDER]);
        setChannelExact(arr[BLACKLIST_SENDER_EXACT].isEmpty() ? true : Boolean.parseBoolean(arr[BLACKLIST_SENDER_EXACT]));
        setTheme(arr[BLACKLIST_THEME]);
        setThemeExact(arr[BLACKLIST_THEME_EXACT].isEmpty() ? true : Boolean.parseBoolean(arr[BLACKLIST_THEME_EXACT]));
        setTitle(arr[BLACKLIST_TITLE]);
        setThemeTitle(arr[BLACKLIST_THEME_TITLE]);
        try {
            setCountHits(Integer.parseInt(arr[BLACKLIST_COUNT_HITS]));
        } catch (Exception ex) {
            setCountHits(0);
        }
    }

    public void setXmlFromProps() {
        arr[BLACKLIST_NR] = getNr() + "";
        arr[BLACKLIST_SENDER] = getChannel();
        arr[BLACKLIST_SENDER_EXACT] = String.valueOf(getChannelExact());
        arr[BLACKLIST_THEME] = getTheme();
        arr[BLACKLIST_THEME_EXACT] = String.valueOf(getThemeExact());
        arr[BLACKLIST_TITLE] = getTitle();
        arr[BLACKLIST_THEME_TITLE] = getThemeTitle();
        arr[BLACKLIST_COUNT_HITS] = String.valueOf(getCountHits());
    }
}
