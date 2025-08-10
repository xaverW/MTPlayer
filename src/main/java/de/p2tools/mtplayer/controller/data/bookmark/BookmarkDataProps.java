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

package de.p2tools.mtplayer.controller.data.bookmark;

import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.config.Config_boolProp;
import de.p2tools.p2lib.configfile.config.Config_pDateProp;
import de.p2tools.p2lib.configfile.config.Config_stringProp;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import de.p2tools.p2lib.tools.date.P2Date;
import de.p2tools.p2lib.tools.date.P2DateProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class BookmarkDataProps extends P2DataSample<BookmarkData> {

    private final BooleanProperty audio = new SimpleBooleanProperty(false); // dann ist es ein Bookmark aus AUDIO
    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty url = new SimpleStringProperty("");
    private final StringProperty info = new SimpleStringProperty("");
    private final P2DateProperty date = new P2DateProperty(new P2Date(0));
    private String buttonDummy = ""; // Tabellenspalte mit den Buttons

    public static final String TAG = "BookmarkData";

    BookmarkDataProps() {
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "DownloadData";
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new Config_boolProp("audio", audio));
        list.add(new Config_stringProp("channel", channel));
        list.add(new Config_stringProp("theme", theme));
        list.add(new Config_stringProp("title", title));
        list.add(new Config_stringProp("url", url));
        list.add(new Config_stringProp("info", info));
        list.add(new Config_pDateProp("date", date));
        return list.toArray(new Config[]{});
    }

    public boolean isAudio() {
        return audio.get();
    }

    public void setAudio(boolean audio) {
        this.audio.set(audio);
    }

    public BooleanProperty audioProperty() {
        return audio;
    }

    public String getChannel() {
        return channel.get();
    }

    public void setChannel(String channel) {
        this.channel.set(channel);
    }

    public StringProperty channelProperty() {
        return channel;
    }

    public String getTheme() {
        return theme.get();
    }

    public void setTheme(String theme) {
        this.theme.set(theme);
    }

    public StringProperty themeProperty() {
        return theme;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public StringProperty urlProperty() {
        return url;
    }

    public String getInfo() {
        return info.get();
    }

    public void setInfo(String info) {
        this.info.set(info);
    }

    public StringProperty infoProperty() {
        return info;
    }

    public P2Date getDate() {
        return date.get();
    }

    public void setDate(P2Date date) {
        this.date.set(date);
    }

    public P2DateProperty dateProperty() {
        return date;
    }

    public String getButtonDummy() {
        return buttonDummy;
    }

    public void setButtonDummy(String buttonDummy) {
        this.buttonDummy = buttonDummy;
    }

    @Override
    public int compareTo(BookmarkData arg0) {
        return sorter.compare(getUrl(), arg0.getUrl());
    }
}
