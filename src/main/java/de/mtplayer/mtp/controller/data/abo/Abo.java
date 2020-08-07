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

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.tools.storedFilter.Filter;

public class Abo extends AboProps {
    public int nr;

    public Filter fChannel = new Filter();
    public Filter fTheme = new Filter();
    public Filter fThemeTitle = new Filter();
    public Filter fTitle = new Filter();
    public Filter fSomewhere = new Filter();


    public Abo() {
        // neue Abos sind immer ein
        setActive(true);
        setResolution(Film.RESOLUTION_NORMAL);
        initFilter();
    }

    public Abo(ProgData progData,
               String name,
               String channel,
               String theme,
               String themeTitle,
               String title,
               String somewhere,
               int timeRange,
               int minDurationMinute,
               int maxDurationMinute,
               String destination) {

        // neue Abos sind immer ein
        setActive(true);
        setResolution(Film.RESOLUTION_NORMAL);
        initFilter();

        setName(name);

        setChannel(channel);
        setTheme(theme);
        setThemeTitle(themeTitle);
        setTitle(title);
        setSomewhere(somewhere);

        setTimeRange(timeRange);
        setMinDurationMinute(minDurationMinute);
        setMaxDurationMinute(maxDurationMinute);

        setAboSubDir(destination);
        setSetData(progData.setDataList.getSetDataForAbo());
    }

    void initAbo(ProgData progData) {
        // init beim Programmstart,
        // kann ein anderes Set notwendig sein (wenn es gelöscht wurde)
        SetData setData = progData.setDataList.getSetDataForAbo(getSetDataId());
        setSetData(setData);
        setSetDataId(setData == null ? "" : setData.getId());
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


    public void copyToMe(Abo abo) {
        for (int i = 0; i < AboXml.MAX_ELEM; ++i) {
            this.properties[i].setValue(abo.properties[i].getValue());
        }
        this.setSetData(abo.getSetData());
        this.setXmlFromProps();
    }

    public Abo getCopy() {
        final Abo ret = new Abo();
        for (int i = 0; i < AboXml.MAX_ELEM; ++i) {
            ret.properties[i].setValue(this.properties[i].getValue());
        }
        ret.setSetData(getSetData());
        ret.setXmlFromProps();

        return ret;
    }

}
