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

package de.p2tools.mtplayer.controller.data.abo;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.controller.data.film.FilmData;
import de.p2tools.mtplayer.tools.Data;
import de.p2tools.mtplayer.tools.filmFilter.CheckFilmFilter;
import de.p2tools.p2Lib.configFile.config.*;
import de.p2tools.p2Lib.configFile.pData.PDataSample;
import de.p2tools.p2Lib.tools.date.PDate;
import de.p2tools.p2Lib.tools.date.PDateFactory;
import de.p2tools.p2Lib.tools.date.PDateProperty;
import javafx.beans.property.*;

import java.util.ArrayList;

public class AboDataProps extends PDataSample<AboData> implements Comparable<AboData> {

    private final IntegerProperty no = new SimpleIntegerProperty(0);
    private final BooleanProperty active = new SimpleBooleanProperty(true);
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final StringProperty resolution = new SimpleStringProperty(FilmData.RESOLUTION_NORMAL);
    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final BooleanProperty themeExact = new SimpleBooleanProperty(true);
    private final StringProperty themeTitle = new SimpleStringProperty("");
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty somewhere = new SimpleStringProperty("");
    private final IntegerProperty timeRange = new SimpleIntegerProperty(CheckFilmFilter.FILTER_TIME_RANGE_ALL_VALUE);
    private final IntegerProperty minDurationMinute = new SimpleIntegerProperty(CheckFilmFilter.FILTER_DURATION_MIN_MINUTE); // Minuten
    private final IntegerProperty maxDurationMinute = new SimpleIntegerProperty(CheckFilmFilter.FILTER_DURATION_MAX_MINUTE); //Minuten
    private final StringProperty startTime = new SimpleStringProperty("");
    private final StringProperty aboSubDir = new SimpleStringProperty("");
    private final PDateProperty date = new PDateProperty(new PDate(0));//Datum des letzten gefundenen Downloads
    private final StringProperty setDataId = new SimpleStringProperty("");//nur zum Speichern/Laden
    private final PDateProperty genDate = new PDateProperty(new PDate());//Erstelldatum

    private final ObjectProperty<SetData> setData = new SimpleObjectProperty<>();
    private final IntegerProperty hit = new SimpleIntegerProperty(0);
    private int countHit = 0;

    public final Property[] properties = {no, active, name, description, resolution,
            channel, theme, themeExact, themeTitle, title, somewhere,
            timeRange, minDurationMinute, maxDurationMinute, startTime, aboSubDir, date, setDataId, genDate};

    public static final String TAG = "AboData"; //ab jetzt wird "Abo" verwendet, alt war: "Abonnement"

    @Override
    public Config[] getConfigsArr() {
        //das muss noch gesetzt werden!!
        setSetDataId(setData.getValue() == null ? "" : setData.getValue().getId());

        ArrayList<Config> list = new ArrayList<>();
        list.add(new ConfigIntPropExtra("no", AboFieldNames.ABO_NO, no));
        list.add(new ConfigBoolPropExtra("active", AboFieldNames.ABO_ACTIVE, active));
        list.add(new ConfigStringPropExtra("name", AboFieldNames.ABO_NAME, name));
        list.add(new ConfigStringPropExtra("description", AboFieldNames.ABO_DESCRIPTION, description));
        list.add(new ConfigStringPropExtra("resolution", AboFieldNames.ABO__RESOLUTION, resolution));
        list.add(new ConfigStringPropExtra("channel", AboFieldNames.ABO_CHANNEL, channel));
        list.add(new ConfigStringPropExtra("theme", AboFieldNames.ABO_THEME, theme));
        list.add(new ConfigBoolPropExtra("themeExact", AboFieldNames.ABO_THEME_EXACT, themeExact));
        list.add(new ConfigStringPropExtra("themeTitle", AboFieldNames.ABO_THEME_TITLE, themeTitle));
        list.add(new ConfigStringPropExtra("title", AboFieldNames.ABO_TITLE, title));
        list.add(new ConfigStringPropExtra("somewhere", AboFieldNames.ABO_SOMEWHERE, somewhere));
        list.add(new ConfigIntPropExtra("timeRange", AboFieldNames.ABO_TIME_RANGE, timeRange));
        list.add(new ConfigIntPropExtra("minDurationMinute", AboFieldNames.ABO_MIN_DURATION, minDurationMinute));
        list.add(new ConfigIntPropExtra("maxDurationMinute", AboFieldNames.ABO_MAX_DURATION, maxDurationMinute));
        list.add(new ConfigStringPropExtra("startTime", AboFieldNames.ABO_START_TIME, startTime));
        list.add(new ConfigStringPropExtra("aboSubDir", AboFieldNames.ABO_DEST_DIR, aboSubDir));
        list.add(new ConfigPDateProp("date", AboFieldNames.ABO_DATE_LAST_ABO, date));
        list.add(new ConfigStringPropExtra("setDataId", AboFieldNames.ABO_SET_DATA_ID, setDataId));
        list.add(new ConfigPDateProp("genDate", AboFieldNames.ABO_GEN_DATE, genDate));
        return list.toArray(new Config[]{});
    }

    @Override
    public String getTag() {
        return TAG;
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

        if (this.setData.getValue() == null) {
            setSetDataId("");
        } else {
            setSetDataId(this.setData.getValue().getId());
        }
    }

    public int getNo() {
        return no.get();
    }

    public IntegerProperty noProperty() {
        return no;
    }

    public void setNo(int no) {
        this.no.set(no);
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

    public String getStartTime() {
        return startTime.get();
    }

    public StringProperty startTimeProperty() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime.set(startTime);
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

    public PDate getDate() {
        return date.get();
    }

    public PDateProperty dateProperty() {
        return date;
    }

    public void setDate(PDate date) {
        this.date.set(date);
    }

    public void setDate(String date, String time) {
        PDate d = new PDate();
        d.setPDate(date, time, PDateFactory.F_FORMAT_dd_MM_yyyy, PDateFactory.F_FORMAT_dd_MM_yyyyHH_mm_ss);
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

    public PDate getGenDate() {
        return genDate.get();
    }

    public PDateProperty genDateProperty() {
        return genDate;
    }

    public void setGenDate(PDate genDate) {
        this.genDate.set(genDate);
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

    public int compareTo(AboDataProps arg0) {
        return Data.sorter.compare(getName(), arg0.getName());
    }
}
