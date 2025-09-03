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
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.p2lib.mediathek.filter.Filter;

public class AboData extends AboDataProps {
    public int nr;
    public Filter fChannel = new Filter();
    public Filter fTheme = new Filter();
    public Filter fThemeTitle = new Filter();
    public Filter fTitle = new Filter();
    public Filter fSomewhere = new Filter();


    public AboData() {
        // neue Abos sind immer ein
        setActive(true);
        setResolution(FilmDataMTP.RESOLUTION_NORMAL);
        initFilter();
    }

    public AboData(ProgData progData,
                   int audio,
                   String name,
                   String channel,
                   String theme,
                   boolean themeIsExact,
                   String themeTitle,
                   String title,
                   String somewhere,
                   int timeRange,
                   int minDurationMinute,
                   int maxDurationMinute,
                   String destination) {

        // neue Abos sind immer ein
        setActive(true);
        setResolution(FilmDataMTP.RESOLUTION_NORMAL);
        initFilter();

        setName(name);
        setList(audio);

        setChannel(channel);
        setTheme(theme);
        setThemeExact(themeIsExact);
        setThemeTitle(themeTitle);
        setTitle(title);
        setSomewhere(somewhere);

        setTimeRange(timeRange);
        setMinDurationMinute(minDurationMinute);
        setMaxDurationMinute(maxDurationMinute);

        setAboSubDir(destination);
        setSetData(progData.setDataList.getSetDataForAbo());
    }

    void initAbo(ProgData progData, int no) {
        // init beim Programmstart,
        // kann ein anderes Set notwendig sein (wenn es gelöscht wurde)
        SetData setData = progData.setDataList.getSetDataForAbo(getSetDataId());
        setSetData(setData);
        setSetDataId(setData == null ? "" : setData.getId());
        setNo(no);
    }

    private void initFilter() {
        channelProperty().addListener(l -> createFilter());
        themeProperty().addListener(l -> createFilter());
        themeExactProperty().addListener(l -> createFilter());

        themeTitleProperty().addListener(l -> createFilter());
        titleProperty().addListener(l -> createFilter());
        somewhereProperty().addListener(l -> createFilter());
    }

    private void createFilter() {
        fChannel = new Filter(getChannel(), true);
        fTheme = new Filter(getTheme(), isThemeExact(), true);
        fThemeTitle = new Filter(getThemeTitle(), true);
        fTitle = new Filter(getTitle(), true);
        fSomewhere = new Filter(getSomewhere(), true);
    }

    public boolean isEmpty() {
        // liefert TRUE wenn das Abo leer ist, also bei jedem Film ansprechen würde
        // ist dann offensichtlich falsch!!
        if (getChannel().isEmpty()
                && getTheme().isEmpty()
                && getThemeTitle().isEmpty()
                && getTitle().isEmpty()
                && getSomewhere().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


    public void copyToMe(AboData abo) {
        for (int i = 0; i < this.properties.length; ++i) {
            this.properties[i].setValue(abo.properties[i].getValue());
        }
        this.setSetData(abo.getSetData());
    }

    public AboData getCopy() {
        final AboData ret = new AboData();
        for (int i = 0; i < this.properties.length; ++i) {
            ret.properties[i].setValue(this.properties[i].getValue());
        }
        ret.setSetData(getSetData());
        return ret;
    }
}
