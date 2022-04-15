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

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.tools.filmFilter.CheckFilmFilter;

public class AboDataXml extends AboDataProps {

    public String[] arr;

    public AboDataXml() {
        arr = makeArr(AboFieldNames.MAX_ELEM);
    }

    private String[] makeArr(int max) {
        final String[] a = new String[max];
        for (int i = 0; i < max; ++i) {
            a[i] = "";
        }
        return a;
    }

    public void setPropsFromXml() {
        setActive(Boolean.parseBoolean(arr[AboFieldNames.ABO_ACTIVE_NO]));
        setResolution(arr[AboFieldNames.ABO_RESOLUTION_NO]);
        setName(arr[AboFieldNames.ABO_NAME_NO]);
        setDescription(arr[AboFieldNames.ABO_DESCRIPTION_NO]);
        setChannel(arr[AboFieldNames.ABO_CHANNEL_NO]);
        setTheme(arr[AboFieldNames.ABO_THEME_NO]);
        setThemeExact(arr[AboFieldNames.ABO_THEME_EXACT_NO].isEmpty() ? true : Boolean.parseBoolean(arr[AboFieldNames.ABO_THEME_EXACT_NO]));
        setThemeTitle(arr[AboFieldNames.ABO_THEME_TITLE_NO]);
        setTitle(arr[AboFieldNames.ABO_TITLE_NO]);
        setSomewhere(arr[AboFieldNames.ABO_SOMEWHERE_NO]);

        setTimeRangeFromXml();
        setDurationMinFromXml();
        setDurationMaxFromXml();
        setStartTime(arr[AboFieldNames.ABO_START_TIME_NO]);

        setAboSubDir(arr[AboFieldNames.ABO_DEST_PATH_NO]);
        setDate(arr[AboFieldNames.ABO_DATE_LAST_ABO_NO], "");
        setSetDataId(arr[AboFieldNames.ABO_SET_DATA_ID_NO]);
    }

    private void setTimeRangeFromXml() {
        int max;

        if (arr[AboFieldNames.ABO_TIME_RANGE_NO].equals(ProgConst.FILTER_ALL)) {
            max = CheckFilmFilter.FILTER_TIME_RANGE_ALL_VALUE;
            setTimeRange(max);
            return;
        }

        try {
            max = Integer.parseInt(arr[AboFieldNames.ABO_TIME_RANGE_NO]);
        } catch (final Exception ex) {
            max = CheckFilmFilter.FILTER_TIME_RANGE_ALL_VALUE;
        }

        setTimeRange(max);
    }

    private void setDurationMinFromXml() {
        int min;
        if (arr[AboFieldNames.ABO_MIN_DURATION_NO].equals(ProgConst.FILTER_ALL)) {
            min = CheckFilmFilter.FILTER_DURATION_MIN_MINUTE;
            setMinDurationMinute(min);
            return;
        }

        try {
            min = Integer.parseInt(arr[AboFieldNames.ABO_MIN_DURATION_NO]);
        } catch (final Exception ex) {
            min = CheckFilmFilter.FILTER_DURATION_MIN_MINUTE;
        }
        setMinDurationMinute(min);
    }

    private void setDurationMaxFromXml() {
        int max;
        if (arr[AboFieldNames.ABO_MAX_DURATION_NO].equals(ProgConst.FILTER_ALL)) {
            max = CheckFilmFilter.FILTER_DURATION_MAX_MINUTE;
            setMaxDurationMinute(max);
            return;
        }

        try {
            max = Integer.parseInt(arr[AboFieldNames.ABO_MAX_DURATION_NO]);
        } catch (final Exception ex) {
            max = CheckFilmFilter.FILTER_DURATION_MAX_MINUTE;
        }
        setMaxDurationMinute(max);
    }
}
