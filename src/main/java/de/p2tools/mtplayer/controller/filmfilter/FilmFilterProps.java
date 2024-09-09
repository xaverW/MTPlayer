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

package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.config.Config_boolProp;
import de.p2tools.p2lib.configfile.config.Config_intProp;
import de.p2tools.p2lib.configfile.config.Config_stringProp;
import de.p2tools.p2lib.configfile.pdata.P2Data;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import javafx.beans.property.*;

import java.util.ArrayList;

public class FilmFilterProps extends P2DataSample<FilmFilter> implements Comparable<FilmFilter> {

    public static String TAG = "SelectedFilter";

    private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty channelVis = new SimpleBooleanProperty(true);
    private final StringProperty channel = new SimpleStringProperty();

    private final BooleanProperty themeVis = new SimpleBooleanProperty(false);
    private final BooleanProperty themeIsExact = new SimpleBooleanProperty(false);
    private final StringProperty theme = new SimpleStringProperty();
    private final StringProperty exactTheme = new SimpleStringProperty();

    private final BooleanProperty themeTitleVis = new SimpleBooleanProperty(true);
    private final StringProperty themeTitle = new SimpleStringProperty();
    private final BooleanProperty titleVis = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty();
    private final BooleanProperty somewhereVis = new SimpleBooleanProperty(false);
    private final StringProperty somewhere = new SimpleStringProperty();
    private final BooleanProperty urlVis = new SimpleBooleanProperty(false);
    private final StringProperty url = new SimpleStringProperty();

    private final BooleanProperty timeRangeVis = new SimpleBooleanProperty(false); // max Tage
    private final IntegerProperty timeRange = new SimpleIntegerProperty(15);

    private final BooleanProperty minMaxDurVis = new SimpleBooleanProperty(true); // Filmlänge
    private final IntegerProperty minDur = new SimpleIntegerProperty(0);
    private final IntegerProperty maxDur = new SimpleIntegerProperty(FilterCheck.FILTER_DURATION_MAX_MINUTE);

    private final BooleanProperty minMaxTimeVis = new SimpleBooleanProperty(true); // Tageszeit
    private final BooleanProperty minMaxTimeInvert = new SimpleBooleanProperty(false);
    private final IntegerProperty minTime = new SimpleIntegerProperty(0); // Tageszeit in Sekunden
    private final IntegerProperty maxTime = new SimpleIntegerProperty(FilterCheck.FILTER_TIME_MAX_SEC); // Tageszeit in Sekunden

    private final BooleanProperty showDateVis = new SimpleBooleanProperty(false); //Sendedatum
    private final StringProperty showDate = new SimpleStringProperty(FilterCheck.FILTER_SHOW_DATE_ALL);

    private final BooleanProperty onlyVis = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyBookmark = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyHd = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyNew = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyUt = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyLive = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyActHistory = new SimpleBooleanProperty(false);

    private final BooleanProperty notVis = new SimpleBooleanProperty(false);
    private final BooleanProperty notAbo = new SimpleBooleanProperty(false);
    private final BooleanProperty notHistory = new SimpleBooleanProperty(false);
    private final BooleanProperty notDouble = new SimpleBooleanProperty(false);
    private final BooleanProperty notGeo = new SimpleBooleanProperty(false);
    private final BooleanProperty notFuture = new SimpleBooleanProperty(false);

    private final IntegerProperty blacklistOnOff = new SimpleIntegerProperty(BlacklistFilterFactory.BLACKLILST_FILTER_OFF);

    public BooleanProperty[] sfBooleanPropArr = {channelVis, themeVis, themeIsExact, themeTitleVis,
            titleVis, somewhereVis, urlVis, timeRangeVis, minMaxDurVis,
            minMaxTimeVis, minMaxTimeInvert, showDateVis,
            onlyVis, onlyBookmark, onlyHd, onlyNew, onlyUt, onlyLive, onlyActHistory, notVis,
            notAbo, notHistory, notDouble, notGeo, notFuture};

    public StringProperty[] sfStringPropArr = {name, channel, theme, exactTheme, themeTitle, title, somewhere, url, showDate};
    public IntegerProperty[] sfIntegerPropArr = {timeRange, minDur, maxDur, minTime, maxTime, blacklistOnOff};

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new Config_stringProp("name", name));
        list.add(new Config_boolProp("channelVis", channelVis));
        list.add(new Config_stringProp("channel", channel));

        list.add(new Config_boolProp("themeVis", themeVis));
        list.add(new Config_boolProp("themeIsExact" + P2Data.TAGGER + "themeExact", themeIsExact));
        list.add(new Config_stringProp("theme", theme));
        list.add(new Config_stringProp("exactTheme", exactTheme));

        list.add(new Config_boolProp("themeTitleVis", themeTitleVis));
        list.add(new Config_stringProp("themeTitle", themeTitle));
        list.add(new Config_boolProp("titleVis", titleVis));
        list.add(new Config_stringProp("title", title));
        list.add(new Config_boolProp("somewhereVis", somewhereVis));
        list.add(new Config_stringProp("somewhere", somewhere));
        list.add(new Config_boolProp("urlVis", urlVis));
        list.add(new Config_stringProp("url", url));

        list.add(new Config_boolProp("timeRangeVis", timeRangeVis));
        list.add(new Config_intProp("timeRange", timeRange));

        list.add(new Config_boolProp("minMaxDurVis", minMaxDurVis));
        list.add(new Config_intProp("minDur", minDur));
        list.add(new Config_intProp("maxDur", maxDur));

        list.add(new Config_boolProp("minMaxTimeVis", minMaxTimeVis));
        list.add(new Config_boolProp("minMaxTimeInvert", minMaxTimeInvert));
        list.add(new Config_intProp("minTime", minTime));
        list.add(new Config_intProp("maxTime", maxTime));

        list.add(new Config_boolProp("showDateVis", showDateVis));
        list.add(new Config_stringProp("showDate", showDate));

        list.add(new Config_boolProp("onlyVis", onlyVis));
        list.add(new Config_boolProp("onlyBookmark", onlyBookmark));
        list.add(new Config_boolProp("onlyHd", onlyHd));
        list.add(new Config_boolProp("onlyNew", onlyNew));
        list.add(new Config_boolProp("onlyUt", onlyUt));
        list.add(new Config_boolProp("onlyLive", onlyLive));
        list.add(new Config_boolProp("onlyAktHistory", onlyActHistory));

        list.add(new Config_boolProp("notVis", notVis));
        list.add(new Config_boolProp("notAbo", notAbo));
        list.add(new Config_boolProp("notHistory", notHistory));
        list.add(new Config_boolProp("notDouble", notDouble));
        list.add(new Config_boolProp("notGeo", notGeo));
        list.add(new Config_boolProp("notFuture", notFuture));

        list.add(new Config_intProp("blacklistOnOff", blacklistOnOff));

        return list.toArray(new Config[]{});
    }

    public boolean isSame(FilmFilter sf) {
        if (sf == null) {
            return false;
        }

        for (int i = 0; i < sfBooleanPropArr.length; ++i) {
            if (!this.sfBooleanPropArr[i].getValue().equals(sf.sfBooleanPropArr[i].getValue())) {
                return false;
            }
        }

        //wenn der Name mit verglichen werden soll, dann Start bei 0, sonst 1
        int ii = 1;
        for (int i = ii; i < sfStringPropArr.length; ++i) {
            if (!this.sfStringPropArr[i].getValue().equals(sf.sfStringPropArr[i].getValue())) {
                return false;
            }
        }

        for (int i = 0; i < sfIntegerPropArr.length; ++i) {
            if (!this.sfIntegerPropArr[i].getValue().equals(sf.sfIntegerPropArr[i].getValue())) {
                return false;
            }
        }
        return true;
    }

    public FilmFilter getCopy() {
        FilmFilter sf = new FilmFilter();
        this.copyTo(sf);
        return sf;
    }

    public void copyTo(FilmFilter sf) {
        for (int i = 0; i < sfBooleanPropArr.length; ++i) {
            sf.sfBooleanPropArr[i].setValue(this.sfBooleanPropArr[i].getValue());
        }
        for (int i = 0; i < sfStringPropArr.length; ++i) {
            sf.sfStringPropArr[i].setValue(this.sfStringPropArr[i].getValue());
        }
        for (int i = 0; i < sfIntegerPropArr.length; ++i) {
            sf.sfIntegerPropArr[i].setValue(this.sfIntegerPropArr[i].getValue());
        }
    }

    @Override
    public String getTag() {
        return TAG;
    }


    public String getName() {
        return name.getValueSafe();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public boolean isChannelVis() {
        return channelVis.get();
    }

    public BooleanProperty channelVisProperty() {
        return channelVis;
    }

    public void setChannelVis(boolean channelVis) {
        this.channelVis.set(channelVis);
    }

    public String getChannel() {
        return channel.getValueSafe();
    }

    public StringProperty channelProperty() {
        return channel;
    }

    public void setChannel(String sender) {
        this.channel.set(sender);
    }

    public boolean isThemeVis() {
        return themeVis.get();
    }

    public BooleanProperty themeVisProperty() {
        return themeVis;
    }

    public void setThemeVis(boolean themeVis) {
        this.themeVis.set(themeVis);
    }

    public boolean isThemeIsExact() {
        return themeIsExact.get();
    }

    public BooleanProperty themeIsExactProperty() {
        return themeIsExact;
    }

    public void setThemeIsExact(boolean themeIsExact) {
        this.themeIsExact.set(themeIsExact);
    }

    public String getResTheme() {
        if (themeIsExact.getValue()) {
            return exactTheme.getValueSafe();
        } else {
            return theme.getValueSafe();
        }
    }

    public String getTheme() {
        return theme.getValueSafe();
    }

    public StringProperty themeProperty() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme.set(theme);
    }

    public String getExactTheme() {
        return exactTheme.getValueSafe();
    }

    public StringProperty exactThemeProperty() {
        return exactTheme;
    }

    public void setExactTheme(String exactTheme) {
        this.exactTheme.set(exactTheme);
    }

    public boolean isThemeTitleVis() {
        return themeTitleVis.get();
    }

    public BooleanProperty themeTitleVisProperty() {
        return themeTitleVis;
    }

    public void setThemeTitleVis(boolean themeTitleVis) {
        this.themeTitleVis.set(themeTitleVis);
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

    public boolean isTitleVis() {
        return titleVis.get();
    }

    public BooleanProperty titleVisProperty() {
        return titleVis;
    }

    public void setTitleVis(boolean titleVis) {
        this.titleVis.set(titleVis);
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

    public boolean isSomewhereVis() {
        return somewhereVis.get();
    }

    public BooleanProperty somewhereVisProperty() {
        return somewhereVis;
    }

    public void setSomewhereVis(boolean somewhereVis) {
        this.somewhereVis.set(somewhereVis);
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

    public boolean isUrlVis() {
        return urlVis.get();
    }

    public BooleanProperty urlVisProperty() {
        return urlVis;
    }

    public void setUrlVis(boolean urlVis) {
        this.urlVis.set(urlVis);
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }


    public boolean isTimeRangeVis() {
        return timeRangeVis.get();
    }

    public BooleanProperty timeRangeVisProperty() {
        return timeRangeVis;
    }

    public void setTimeRangeVis(boolean timeRangeVis) {
        this.timeRangeVis.set(timeRangeVis);
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

    public boolean isMinMaxDurVis() {
        return minMaxDurVis.get();
    }

    public BooleanProperty minMaxDurVisProperty() {
        return minMaxDurVis;
    }

    public void setMinMaxDurVis(boolean minMaxDurVis) {
        this.minMaxDurVis.set(minMaxDurVis);
    }

    public int getMinDur() {
        return minDur.get();
    }

    public IntegerProperty minDurProperty() {
        return minDur;
    }

    public void setMinDur(int minDur) {
        this.minDur.set(minDur);
    }

    public int getMaxDur() {
        return maxDur.get();
    }

    public IntegerProperty maxDurProperty() {
        return maxDur;
    }

    public void setMaxDur(int maxDur) {
        this.maxDur.set(maxDur);
    }


    public boolean isMinMaxTimeVis() {
        return minMaxTimeVis.get();
    }

    public BooleanProperty minMaxTimeVisProperty() {
        return minMaxTimeVis;
    }

    public void setMinMaxTimeVis(boolean minMaxTimeVis) {
        this.minMaxTimeVis.set(minMaxTimeVis);
    }

    public boolean isMinMaxTimeInvert() {
        return minMaxTimeInvert.get();
    }

    public BooleanProperty minMaxTimeInvertProperty() {
        return minMaxTimeInvert;
    }

    public void setMinMaxTimeInvert(boolean minMaxTimeInvert) {
        this.minMaxTimeInvert.set(minMaxTimeInvert);
    }

    public int getMinTime() {
        return minTime.get();
    }

    public IntegerProperty minTimeProperty() {
        return minTime;
    }

    public void setMinTime(int minTime) {
        this.minTime.set(minTime);
    }

    public int getMaxTime() {
        return maxTime.get();
    }

    public IntegerProperty maxTimeProperty() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime.set(maxTime);
    }

    public boolean isShowDateVis() {
        return showDateVis.get();
    }

    public BooleanProperty showDateVisProperty() {
        return showDateVis;
    }

    public void setShowDateVis(boolean showDateVis) {
        this.showDateVis.set(showDateVis);
    }


    public String getShowDate() {
        return showDate.get();
    }

    public StringProperty showDateProperty() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate.set(showDate);
    }

    public boolean isOnlyVis() {
        return onlyVis.get();
    }

    public BooleanProperty onlyVisProperty() {
        return onlyVis;
    }

    public void setOnlyVis(boolean onlyVis) {
        this.onlyVis.set(onlyVis);
    }

    public boolean isOnlyBookmark() {
        return onlyBookmark.get();
    }

    public BooleanProperty onlyBookmarkProperty() {
        return onlyBookmark;
    }

    public void setOnlyBookmark(boolean onlyBookmark) {
        this.onlyBookmark.set(onlyBookmark);
    }

    public boolean isOnlyHd() {
        return onlyHd.get();
    }

    public BooleanProperty onlyHdProperty() {
        return onlyHd;
    }

    public void setOnlyHd(boolean onlyHd) {
        this.onlyHd.set(onlyHd);
    }

    public boolean isOnlyNew() {
        return onlyNew.get();
    }

    public BooleanProperty onlyNewProperty() {
        return onlyNew;
    }

    public void setOnlyNew(boolean onlyNew) {
        this.onlyNew.set(onlyNew);
    }

    public boolean isOnlyUt() {
        return onlyUt.get();
    }

    public BooleanProperty onlyUtProperty() {
        return onlyUt;
    }

    public void setOnlyUt(boolean onlyUt) {
        this.onlyUt.set(onlyUt);
    }

    public boolean isOnlyLive() {
        return onlyLive.get();
    }

    public BooleanProperty onlyLiveProperty() {
        return onlyLive;
    }

    public void setOnlyLive(boolean onlyLive) {
        this.onlyLive.set(onlyLive);
    }

    public boolean getOnlyActHistory() {
        return onlyActHistory.get();
    }

    public BooleanProperty onlyActHistoryProperty() {
        return onlyActHistory;
    }

    public void setOnlyActHistory(boolean onlyActHistory) {
        this.onlyActHistory.set(onlyActHistory);
    }

    public boolean isNotVis() {
        return notVis.get();
    }

    public BooleanProperty notVisProperty() {
        return notVis;
    }

    public void setNotVis(boolean notVis) {
        this.notVis.set(notVis);
    }

    public boolean isNotAbo() {
        return notAbo.get();
    }

    public BooleanProperty notAboProperty() {
        return notAbo;
    }

    public void setNotAbo(boolean notAbo) {
        this.notAbo.set(notAbo);
    }

    public boolean isNotHistory() {
        return notHistory.get();
    }

    public BooleanProperty notHistoryProperty() {
        return notHistory;
    }

    public void setNotHistory(boolean notHistory) {
        this.notHistory.set(notHistory);
    }

    public boolean isNotDouble() {
        return notDouble.get();
    }

    public BooleanProperty notDoubleProperty() {
        return notDouble;
    }

    public void setNotDouble(boolean notDouble) {
        this.notDouble.set(notDouble);
    }

    public boolean isNotGeo() {
        return notGeo.get();
    }

    public BooleanProperty notGeoProperty() {
        return notGeo;
    }

    public void setNotGeo(boolean notGeo) {
        this.notGeo.set(notGeo);
    }

    public boolean isNotFuture() {
        return notFuture.get();
    }

    public BooleanProperty notFutureProperty() {
        return notFuture;
    }

    public void setNotFuture(boolean notFuture) {
        this.notFuture.set(notFuture);
    }

    public int getBlacklistOnOff() {
        return blacklistOnOff.get();
    }

    public IntegerProperty blacklistOnOffProperty() {
        return blacklistOnOff;
    }

    public void setBlacklistOnOff(int blacklistOnOff) {
        this.blacklistOnOff.set(blacklistOnOff);
    }

    @Override
    public String toString() {
        return name.getValue();
    }

    @Override
    public int compareTo(FilmFilter o) {
        return name.getValue().compareTo(o.getName());
    }
}
