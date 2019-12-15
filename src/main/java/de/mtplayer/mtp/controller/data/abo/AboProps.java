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
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.tools.filmListFilter.FilmFilter;
import javafx.beans.property.*;

public class AboProps extends AboXml {
    private final IntegerProperty nr = new SimpleIntegerProperty(0);
    private final BooleanProperty active = new SimpleBooleanProperty(true);
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final StringProperty resolution = new SimpleStringProperty(Film.RESOLUTION_NORMAL);
    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final BooleanProperty themeExact = new SimpleBooleanProperty(true);
    private final StringProperty themeTitle = new SimpleStringProperty("");
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty somewhere = new SimpleStringProperty("");
    private final IntegerProperty timeRange = new SimpleIntegerProperty(FilmFilter.FILTER_TIME_RANGE_ALL_VALUE);
    private final IntegerProperty minDurationMinute = new SimpleIntegerProperty(FilmFilter.FILTER_DURATION_MIN_MINUTE); // Minuten
    private final IntegerProperty maxDurationMinute = new SimpleIntegerProperty(FilmFilter.FILTER_DURATION_MAX_MINUTE); //Minuten
    private final StringProperty aboSubDir = new SimpleStringProperty("");
    private final ObjectProperty<MDate> date = new SimpleObjectProperty<>(new MDate(0));
    private final StringProperty setDataId = new SimpleStringProperty("");

    private final ObjectProperty<SetData> setData = new SimpleObjectProperty<>();

    private final IntegerProperty hit = new SimpleIntegerProperty(0);
    private int countHit = 0;

    public final Property[] properties = {nr, active, name, description, resolution,
            channel, theme, themeExact, themeTitle, title, somewhere,
            timeRange, minDurationMinute, maxDurationMinute, aboSubDir, date, setDataId};

    public String getStringOf(int i) {
        return String.valueOf(properties[i].getValue());
    }

    public AboProps() {
        super();
    }

    public SetData getSetData(ProgData progData) {
        // wenn das Set noch nicht vorhanden ist, wird es vorher gesetzt
        if (setData.getValue() == null) {
            setSetData(progData.setDataList.getSetDataForAbo());
        }
        return setData.get();
    }

    public SetData getSetData() {
        return setData.get();
    }

    public ObjectProperty<SetData> setDataProperty() {
        return setData;
    }

    public void setSetData(SetData setData) {
        this.setData.set(setData);
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

    public String getThemeTitle() {
        return themeTitle.get();
    }

    public StringProperty themeTitleProperty() {
        return themeTitle;
    }

    public void setThemeTitle(String themeTitle) {
        this.themeTitle.set(themeTitle);
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

    public String getSomewhere() {
        return somewhere.get();
    }

    public StringProperty somewhereProperty() {
        return somewhere;
    }

    public void setSomewhere(String somewhere) {
        this.somewhere.set(somewhere);
    }

    public int getTimeRange() {
        return timeRange.get();
    }

    public IntegerProperty timeRangeProperty() {
        return timeRange;
    }

    public void setTimeRange(int timeRange) {
        this.timeRange.set(timeRange);
    }

    public int getMinDurationMinute() {
        return minDurationMinute.get();
    }

    public IntegerProperty minDurationMinuteProperty() {
        return minDurationMinute;
    }

    public void setMinDurationMinute(int minDurationMinute) {
        this.minDurationMinute.set(minDurationMinute);
    }

    public int getMaxDurationMinute() {
        return maxDurationMinute.get();
    }

    public IntegerProperty maxDurationMinuteProperty() {
        return maxDurationMinute;
    }

    public void setMaxDurationMinute(int maxDurationMinute) {
        this.maxDurationMinute.set(maxDurationMinute);
    }

    public String getAboSubDir() {
        return aboSubDir.get();
    }

    public StringProperty aboSubDirProperty() {
        return aboSubDir;
    }

    public void setAboSubDir(String aboSubDir) {
        this.aboSubDir.set(aboSubDir);
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

    public String getSetDataId() {
        return setDataId.get();
    }

    public StringProperty setDataIdProperty() {
        return setDataId;
    }

    public void setSetDataId(String setDataId) {
        this.setDataId.set(setDataId);
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
        setTheme(arr[ABO_THEME]);
        setThemeExact(arr[ABO_THEME_EXACT].isEmpty() ? true : Boolean.parseBoolean(arr[ABO_THEME_EXACT]));
        setThemeTitle(arr[ABO_THEME_TITLE]);
        setTitle(arr[ABO_TITLE]);
        setSomewhere(arr[ABO_SOMEWHERE]);

        setTimeRangeFromXml();
        setDurationMinFromXml();
        setDurationMaxFromXml();

        setAboSubDir(arr[ABO_DEST_PATH]);
        setDatum(arr[ABO_DOWN_DATE], "");
        setSetDataId(arr[ABO_SET_DATA_ID]);
    }

    public void setXmlFromProps() {
        arr[ABO_NR] = getNr() + "";
        arr[ABO_ON] = String.valueOf(isActive());
        arr[ABO_RESOLUTION] = getResolution();
        arr[ABO_NAME] = getName();
        arr[ABO_DESCRIPTION] = getDescription();
        arr[ABO_CHANNEL] = getChannel();
        arr[ABO_THEME] = getTheme();
        arr[ABO_THEME_EXACT] = String.valueOf(isThemeExact());
        arr[ABO_THEME_TITLE] = getThemeTitle();
        arr[ABO_TITLE] = getTitle();
        arr[ABO_SOMEWHERE] = getSomewhere();

        if (getTimeRange() == FilmFilter.FILTER_TIME_RANGE_ALL_VALUE) {
            arr[ABO_TIME_RANGE] = ProgConst.FILTER_ALL;
        } else {
            arr[ABO_TIME_RANGE] = String.valueOf(getTimeRange());
        }

        if (getMinDurationMinute() == FilmFilter.FILTER_DURATION_MIN_MINUTE) {
            arr[ABO_MIN_DURATION] = ProgConst.FILTER_ALL;
        } else {
            arr[ABO_MIN_DURATION] = String.valueOf(getMinDurationMinute());
        }

        if (getMaxDurationMinute() == FilmFilter.FILTER_DURATION_MAX_MINUTE) {
            arr[ABO_MAX_DURATION] = ProgConst.FILTER_ALL;
        } else {
            arr[ABO_MAX_DURATION] = String.valueOf(getMaxDurationMinute());
        }

        arr[ABO_DEST_PATH] = getAboSubDir();
        arr[ABO_DOWN_DATE] = getDate().toString();
        arr[ABO_SET_DATA_ID] = getSetData() == null ? "" : getSetData().getId();
    }

    private void setTimeRangeFromXml() {
        int max;

        if (arr[ABO_TIME_RANGE].equals(ProgConst.FILTER_ALL)) {
            max = FilmFilter.FILTER_TIME_RANGE_ALL_VALUE;
            setTimeRange(max);
            return;
        }

        try {
            max = Integer.parseInt(arr[ABO_TIME_RANGE]);
        } catch (final Exception ex) {
            max = FilmFilter.FILTER_TIME_RANGE_ALL_VALUE;
        }

        setTimeRange(max);
    }

    private void setDurationMinFromXml() {
        int min;
        if (arr[ABO_MIN_DURATION].equals(ProgConst.FILTER_ALL)) {
            min = FilmFilter.FILTER_DURATION_MIN_MINUTE;
            setMinDurationMinute(min);
            return;
        }

        try {
            min = Integer.parseInt(arr[ABO_MIN_DURATION]);
        } catch (final Exception ex) {
            min = FilmFilter.FILTER_DURATION_MIN_MINUTE;
        }
        setMinDurationMinute(min);
    }

    private void setDurationMaxFromXml() {
        int max;
        if (arr[ABO_MAX_DURATION].equals(ProgConst.FILTER_ALL)) {
            max = FilmFilter.FILTER_DURATION_MAX_MINUTE;
            setMaxDurationMinute(max);
            return;
        }

        try {
            max = Integer.parseInt(arr[ABO_MAX_DURATION]);
        } catch (final Exception ex) {
            max = FilmFilter.FILTER_DURATION_MAX_MINUTE;
        }
        setMaxDurationMinute(max);
    }


    public int compareTo(AboProps arg0) {
        return Data.sorter.compare(getName(), arg0.getName());
    }

}
