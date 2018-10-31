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

package de.mtplayer.mtp.controller.data.abo;

import de.mtplayer.mLib.tools.Data;
import de.mtplayer.mLib.tools.MDate;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import javafx.beans.property.*;

public class AboProps extends AboXml {
    private final IntegerProperty nr = new SimpleIntegerProperty(0);
    private final BooleanProperty active = new SimpleBooleanProperty(true);
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final StringProperty resolution = new SimpleStringProperty(Film.RESOLUTION_NORMAL);
    private final StringProperty channel = new SimpleStringProperty("");
    private final BooleanProperty channelExact = new SimpleBooleanProperty(true);
    private final StringProperty theme = new SimpleStringProperty("");
    private final BooleanProperty themeExact = new SimpleBooleanProperty(true);
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty themeTitle = new SimpleStringProperty("");
    private final StringProperty somewhere = new SimpleStringProperty("");
    private final IntegerProperty minDuration = new SimpleIntegerProperty(0); // Minuten
    private final IntegerProperty maxDuration = new SimpleIntegerProperty(SelectedFilter.FILTER_DURATION_MAX_MIN); //Minuten
    private final StringProperty destination = new SimpleStringProperty("");
    private final ObjectProperty<MDate> date = new SimpleObjectProperty<>(new MDate(0));
    private final StringProperty psetName = new SimpleStringProperty("");

    private final IntegerProperty hit = new SimpleIntegerProperty(0);
    private int countHit = 0;

    public final Property[] properties = {nr, active, name, description, resolution,
            channel, channelExact, theme, themeExact, title, themeTitle, somewhere,
            minDuration, maxDuration, destination, date, psetName};

    public String getStringOf(int i) {
        return String.valueOf(properties[i].getValue());
    }

    public AboProps() {
        super();
    }

    public int getNr() {
        return nr.get();
    }

    public IntegerProperty nrProperty() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr.set(nr);
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

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getResolution() {
        return resolution.get();
    }

    public StringProperty resolutionProperty() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution.set(resolution);
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

    public String getSomewhere() {
        return somewhere.get();
    }

    public StringProperty somewhereProperty() {
        return somewhere;
    }

    public void setSomewhere(String somewhere) {
        this.somewhere.set(somewhere);
    }

    public int getMinDuration() {
        return minDuration.get();
    }

    public int getMinSec() {
        return minDuration.get() * 60;
    }

    public IntegerProperty minDurationProperty() {
        return minDuration;
    }

    public void setMinDuration(int minDuration) {
        this.minDuration.set(minDuration);
    }

    public int getMaxDuration() {
        return maxDuration.get();
    }

    public int getMaxSec() {
        return maxDuration.get() * 60;
    }

    public IntegerProperty maxDurationProperty() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration.set(maxDuration);
    }

    public String getDestination() {
        return destination.get();
    }

    public StringProperty destinationProperty() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination.set(destination);
    }

    public MDate getDate() {
        return date.get();
    }

    public ObjectProperty dateProperty() {
        return date;
    }

    public void setDate(MDate date) {
        this.date.set(date);
    }

    public void setDatum(String date, String time) {
        MDate d = new MDate();
        d.setDatum(date, time);
        this.date.setValue(d);
    }

    public String getPsetName() {
        return psetName.get();
    }

    public StringProperty psetNameProperty() {
        return psetName;
    }

    public void setPsetName(String psetName) {
        this.psetName.set(psetName);
    }

    public int getHit() {
        return hit.get();
    }

    public IntegerProperty hitProperty() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit.set(hit);
    }

    public synchronized void clearCountHit() {
        countHit = 0;
    }

    public synchronized void incrementCountHit() {
        this.countHit = ++countHit;
    }

    public void setCountedHits() {
        this.setHit(countHit);
    }

    public void setPropsFromXml() {
        setActive(Boolean.parseBoolean(arr[ABO_ON]));
        setResolution(arr[ABO_RESOLUTION]);
        setName(arr[ABO_NAME]);
        setDescription(arr[ABO_DESCRIPTION]);
        setChannel(arr[ABO_CHANNEL]);
        setChannelExact(arr[ABO_CHANNEL_EXACT].isEmpty() ? true : Boolean.parseBoolean(arr[ABO_CHANNEL_EXACT]));
        setTheme(arr[ABO_THEME]);
        setThemeExact(arr[ABO_THEME_EXACT].isEmpty() ? true : Boolean.parseBoolean(arr[ABO_THEME_EXACT]));
        setTitle(arr[ABO_TITLE]);
        setThemeTitle(arr[ABO_THEME_TITLE]);
        setSomewhere(arr[ABO_SOMEWHERE]);

        setDurationFromXml();

        setDestination(arr[ABO_DEST_PATH]);
        setDatum(arr[ABO_DOWN_DATE], "");
        setPsetName(arr[ABO_PSET_NAME]);
    }

    public void setXmlFromProps() {
        arr[ABO_NR] = getNr() + "";
        arr[ABO_ON] = String.valueOf(isActive());
        arr[ABO_RESOLUTION] = getResolution();
        arr[ABO_NAME] = getName();
        arr[ABO_DESCRIPTION] = getDescription();
        arr[ABO_CHANNEL] = getChannel();
        arr[ABO_CHANNEL_EXACT] = String.valueOf(getChannelExact());
        arr[ABO_THEME] = getTheme();
        arr[ABO_THEME_EXACT] = String.valueOf(isThemeExact());
        arr[ABO_TITLE] = getTitle();
        arr[ABO_THEME_TITLE] = getThemeTitle();
        arr[ABO_SOMEWHERE] = getSomewhere();

        arr[ABO_MIN_DURATION] = String.valueOf(getMinDuration());
        arr[ABO_MAX_DURATION] = String.valueOf(getMaxDuration());

        arr[ABO_DEST_PATH] = getDestination();
        arr[ABO_DOWN_DATE] = getDate().toString();
        arr[ABO_PSET_NAME] = getPsetName();
    }

    private void setDurationFromXml() {
        int min;
        int max;
        try {
            min = Integer.parseInt(arr[ABO_MIN_DURATION]);
            max = Integer.parseInt(arr[ABO_MAX_DURATION]);
        } catch (final Exception ex) {
            min = 0;
            max = SelectedFilter.FILTER_DURATION_MAX_MIN;
        }
        setMinDuration(min);
        setMaxDuration(max);
    }


    public int compareTo(AboProps arg0) {
        return Data.sorter.compare(getName(), arg0.getName());
    }

}
