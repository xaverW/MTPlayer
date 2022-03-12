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

import de.p2tools.p2Lib.configFile.config.Config;
import de.p2tools.p2Lib.configFile.config.ConfigBoolPropExtra;
import de.p2tools.p2Lib.configFile.config.ConfigStringPropExtra;
import de.p2tools.p2Lib.configFile.pData.PDataSample;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class BlackDataProps extends PDataSample<BlackDataProps> {

    public static final String BLACKLIST_NO = "Nr";
    public static final String BLACKLIST_SENDER = "Sender";
    public static final String BLACKLIST_THEME = "Thema";
    public static final String BLACKLIST_THEME_EXACT = "Thema exakt";
    public static final String BLACKLIST_TITLE = "Titel";
    public static final String BLACKLIST_THEME_TITLE = "Thema/Titel";
    public static final String BLACKLIST_COUNT_HITS = "Treffer";

    public static final int BLACKLIST_NO_NO = 0;
    public static final int BLACKLIST_SENDER_NO = 1;
    public static final int BLACKLIST_THEME_NO = 2;
    public static final int BLACKLIST_THEME_EXACT_NO = 3;
    public static final int BLACKLIST_TITLE_NO = 4;
    public static final int BLACKLIST_THEME_TITLE_NO = 5;
    public static final int BLACKLIST_COUNT_HITS_NO = 6;

    public static final String TAG = "BlackData";
    public static final String[] XML_NAMES = {
            "black-nr",
            "black-sender",
            "black-thema",
            "black-thema-exakt",
            "black-titel",
            "black-thema-titel",
            "black-count-hits"};

    public String[] arr;

    private int no = 0;
    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final BooleanProperty themeExact = new SimpleBooleanProperty(true);
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty themeTitle = new SimpleStringProperty("");
    private int countHits = 0;

    public BlackDataProps() {
        makeArray();
    }

    private void makeArray() {
        arr = new String[XML_NAMES.length];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
//        list.add(new ConfigIntExtra("no", BLACKLIST_NO, no));
        list.add(new ConfigStringPropExtra("channel", BLACKLIST_SENDER, channel));
        list.add(new ConfigStringPropExtra("theme", BLACKLIST_THEME, theme));
        list.add(new ConfigBoolPropExtra("themeExact", BLACKLIST_THEME_EXACT, themeExact));
        list.add(new ConfigStringPropExtra("title", BLACKLIST_TITLE, title));
        list.add(new ConfigStringPropExtra("bithemeTitletrate", BLACKLIST_THEME_TITLE, themeTitle));
//        list.add(new ConfigInntExtra("countHits", BLACKLIST_COUNT_HITS, countHits));

        return list.toArray(new Config[]{});
    }

    @Override
    public String getTag() {
        return TAG;
    }

    public synchronized void incCountHits() {
        ++this.countHits;
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

    public void setPropsFromXml() {
        setChannel(arr[BLACKLIST_SENDER_NO]);
        setTheme(arr[BLACKLIST_THEME_NO]);
        setThemeExact(arr[BLACKLIST_THEME_EXACT_NO].isEmpty() ? true : Boolean.parseBoolean(arr[BLACKLIST_THEME_EXACT_NO]));
        setTitle(arr[BLACKLIST_TITLE_NO]);
        setThemeTitle(arr[BLACKLIST_THEME_TITLE_NO]);
        try {
            setCountHits(Integer.parseInt(arr[BLACKLIST_COUNT_HITS_NO]));
        } catch (Exception ex) {
            setCountHits(0);
        }
    }
}
