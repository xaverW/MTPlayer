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

package de.mtplayer.mtp.tools.storedFilter;

import de.mtplayer.mtp.tools.filmListFilter.FilmFilter;
import javafx.beans.property.*;

public class SelectedFilterProps {

    private final StringProperty name = new SimpleStringProperty();

    private final BooleanProperty channelVis = new SimpleBooleanProperty(true);
    private final BooleanProperty channelExact = new SimpleBooleanProperty(true);
    private final StringProperty channel = new SimpleStringProperty();
    private final BooleanProperty themeVis = new SimpleBooleanProperty(false);
    private final BooleanProperty themeExact = new SimpleBooleanProperty(false);
    private final StringProperty theme = new SimpleStringProperty();
    private final BooleanProperty themeTitleVis = new SimpleBooleanProperty(true);
    private final StringProperty themeTitle = new SimpleStringProperty();
    private final BooleanProperty titleVis = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty();
    private final BooleanProperty somewhereVis = new SimpleBooleanProperty(false);
    private final StringProperty somewhere = new SimpleStringProperty();
    private final BooleanProperty urlVis = new SimpleBooleanProperty(false);
    private final StringProperty url = new SimpleStringProperty();

    private final BooleanProperty timeRangeVis = new SimpleBooleanProperty(false);
    private final IntegerProperty timeRange = new SimpleIntegerProperty(15);

    private final BooleanProperty minMaxDurVis = new SimpleBooleanProperty(true);
    private final IntegerProperty minDur = new SimpleIntegerProperty(0);
    private final IntegerProperty maxDur = new SimpleIntegerProperty(FilmFilter.FILTER_DURATION_MAX_MINUTE);

    private final BooleanProperty minMaxTimeVis = new SimpleBooleanProperty(true);
    private final BooleanProperty minMaxTimeInvert = new SimpleBooleanProperty(false);
    private final IntegerProperty minTime = new SimpleIntegerProperty(0); // Tageszeit in Sekunden
    private final IntegerProperty maxTime = new SimpleIntegerProperty(FilmFilter.FILTER_FILMTIME_MAX_SEC); // Tageszeit in Sekunden

    private final BooleanProperty onlyVis = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyBookmark = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyHd = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyNew = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyUt = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyLive = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyAktHistory = new SimpleBooleanProperty(false);

    private final BooleanProperty notVis = new SimpleBooleanProperty(false);
    private final BooleanProperty notAbo = new SimpleBooleanProperty(false);
    private final BooleanProperty notHistory = new SimpleBooleanProperty(false);
    private final BooleanProperty notDouble = new SimpleBooleanProperty(false);
    private final BooleanProperty notGeo = new SimpleBooleanProperty(false);
    private final BooleanProperty notFuture = new SimpleBooleanProperty(false);

    private final BooleanProperty blacklistOn = new SimpleBooleanProperty(false);

    public BooleanProperty[] sfBooleanPropArr = {channelVis, channelExact, themeVis, themeExact, themeTitleVis,
            titleVis, somewhereVis, urlVis, timeRangeVis, minMaxDurVis, minMaxTimeVis, minMaxTimeInvert,
            onlyVis, onlyBookmark, onlyHd, onlyNew, onlyUt, onlyLive, onlyAktHistory, notVis,
            notAbo, notHistory, notDouble, notGeo, notFuture, blacklistOn};
    public StringProperty[] sfStringPropArr = {name, channel, theme, themeTitle, title, somewhere, url};
    public IntegerProperty[] sfIntegerPropArr = {timeRange, minDur, maxDur, minTime, maxTime};


    public String getName() {
        return name.get();
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

    public boolean isChannelExact() {
        return channelExact.get();
    }

    public BooleanProperty channelExactProperty() {
        return channelExact;
    }

    public void setChannelExact(boolean channelExact) {
        this.channelExact.set(channelExact);
    }

    public String getChannel() {
        return channel.get() == null ? "" : channel.get();
    }

    public StringProperty channelProperty() {
        return channel;
    }

    public void setChannel(String sender) {
        this.channel.set(sender);
        this.channelVis.set(true);
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

    public boolean isThemeExact() {
        return themeExact.get();
    }

    public BooleanProperty themeExactProperty() {
        return themeExact;
    }

    public void setThemeExact(boolean themeExact) {
        this.themeExact.set(themeExact);
    }

    public String getTheme() {
        return theme.get() == null ? "" : theme.get();
    }

    public StringProperty themeProperty() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme.set(theme);
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

    public boolean isOnlyAktHistory() {
        return onlyAktHistory.get();
    }

    public BooleanProperty onlyAktHistoryProperty() {
        return onlyAktHistory;
    }

    public void setOnlyAktHistory(boolean onlyAktHistory) {
        this.onlyAktHistory.set(onlyAktHistory);
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

    public boolean isBlacklistOn() {
        return blacklistOn.get();
    }

    public BooleanProperty blacklistOnProperty() {
        return blacklistOn;
    }

    public void setBlacklistOn(boolean blacklistOn) {
        this.blacklistOn.set(blacklistOn);
    }

    @Override
    public String toString() {
        return name.getValue();
    }

}
