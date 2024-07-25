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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class BlackListFilter {

    private final StringProperty channel = new SimpleStringProperty();
    private final StringProperty theme = new SimpleStringProperty("");
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty themeTitle = new SimpleStringProperty("");
    private final StringProperty somewhere = new SimpleStringProperty("");

    private final BooleanProperty themeExactIndeterminate = new SimpleBooleanProperty(true);
    private final BooleanProperty themeExact = new SimpleBooleanProperty(true);

    private final BooleanProperty filterActiveIndeterminate = new SimpleBooleanProperty(true);
    private final BooleanProperty filterActive = new SimpleBooleanProperty(true);


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

    public String getThemeTitle() {
        return themeTitle.get();
    }

    public void setThemeTitle(String themeTitle) {
        this.themeTitle.set(themeTitle);
    }

    public StringProperty themeTitleProperty() {
        return themeTitle;
    }

    public boolean isFilterActiveIndeterminate() {
        return filterActiveIndeterminate.get();
    }

    public void setFilterActiveIndeterminate(boolean set) {
        this.filterActiveIndeterminate.set(set);
    }

    public BooleanProperty filterActiveIndeterminateProperty() {
        return filterActiveIndeterminate;
    }

    public boolean isFilterActive() {
        return filterActive.get();
    }

    public void setFilterActive(boolean set) {
        filterActive.set(set);
    }

    public BooleanProperty filterActiveProperty() {
        return filterActive;
    }

    public boolean isThemeExactIndeterminate() {
        return themeExactIndeterminate.get();
    }

    public void setThemeExactIndeterminate(boolean set) {
        themeExactIndeterminate.set(set);
    }

    public BooleanProperty themeExactIndeterminateProperty() {
        return themeExactIndeterminate;
    }

    public boolean isThemeExact() {
        return themeExact.get();
    }

    public void setThemeExact(boolean set) {
        themeExact.set(set);
    }

    public BooleanProperty themeExactProperty() {
        return themeExact;
    }

    public String getSomewhere() {
        return somewhere.get();
    }

    public void setSomewhere(String somewhere) {
        this.somewhere.set(somewhere);
    }

    public StringProperty somewhereProperty() {
        return somewhere;
    }
}
