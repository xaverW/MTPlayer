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

package de.p2tools.mtplayer.controller.data.blackdata;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.p2lib.configfile.config.*;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import de.p2tools.p2lib.tools.date.P2LDateFactory;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class BlackDataProps extends P2DataSample<BlackDataProps> {

    public static final String TAG = "BlackData";

    private int no = 0;
    private final IntegerProperty list = new SimpleIntegerProperty(ProgConst.LIST_FILM_AUDIO); // wo gesucht werden soll
    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final BooleanProperty themeExact = new SimpleBooleanProperty(true);
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty themeTitle = new SimpleStringProperty("");
    private final BooleanProperty active = new SimpleBooleanProperty(true);
    private LocalDate genDate = LocalDate.now();
    private int countHits = 0;

    public BlackDataProps() {
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> configList = new ArrayList<>();
        configList.add(new Config_intProp("list", list));
        configList.add(new Config_stringProp("channel", channel));
        configList.add(new Config_stringProp("theme", theme));
        configList.add(new Config_boolProp("themeExact", themeExact));
        configList.add(new Config_stringProp("title", title));
        configList.add(new Config_stringProp("themeTitle", themeTitle));
        configList.add(new Config_boolProp("active", active));
        configList.add(new Config_lDate("genDate", P2LDateFactory.toString(genDate)) {
            @Override
            public void setUsedValue(LocalDate act) {
                genDate = act;
            }
        });
        return configList.toArray(new Config[]{});
    }

    public BlackData getCopy() {
        BlackData bl = new BlackData();
        bl.setList(list.get());
        bl.setChannel(channel.getValueSafe());
        bl.setTheme(theme.getValueSafe());
        bl.setThemeExact(themeExact.getValue());
        bl.setTitle(title.getValueSafe());
        bl.setThemeTitle(themeTitle.getValueSafe());
        bl.setActive(active.getValue());
        bl.setGenDate(genDate);
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

    public int getList() {
        return list.get();
    }

    public void setList(int list) {
        this.list.set(list);
    }

    public IntegerProperty listProperty() {
        return list;
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

    public boolean isActive() {
        return active.get();
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public LocalDate getGenDate() {
        return genDate;
    }

    public void setGenDate(LocalDate genDate) {
        this.genDate = genDate;
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
