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

package de.p2tools.mtplayer.controller.data.download;


public class DownloadXml extends DownloadProps {


    public String[] arr;

    public DownloadXml() {
        makeArray();
    }

    void makeArray() {
        arr = new String[DownloadFieldNames.MAX_ELEM];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
    }

    public void setPropsFromXml() {

        setAboName(arr[DownloadFieldNames.DOWNLOAD_ABO_NO]);
        setChannel(arr[DownloadFieldNames.DOWNLOAD_SENDER_NO]);
        setTheme(arr[DownloadFieldNames.DOWNLOAD_THEME_NO]);
        setTitle(arr[DownloadFieldNames.DOWNLOAD_TITLE_NO]);

        setFilmDate(arr[DownloadFieldNames.DOWNLOAD_DATE_NO], arr[DownloadFieldNames.DOWNLOAD_TIME_NO]);
        setTime(arr[DownloadFieldNames.DOWNLOAD_TIME_NO]);

        int dur;
        try {
            dur = Integer.parseInt(arr[DownloadFieldNames.DOWNLOAD_DURATION_NO]);
        } catch (final Exception ex) {
            dur = 0;
        }
        setDurationMinute(dur);

        setHd(Boolean.parseBoolean(arr[DownloadFieldNames.DOWNLOAD_HD_NO]));
        setUt(Boolean.parseBoolean(arr[DownloadFieldNames.DOWNLOAD_UT_NO]));
        setGeoBlocked(Boolean.parseBoolean(arr[DownloadFieldNames.DOWNLOAD_GEO_NO]));
        setFilmUrl(arr[DownloadFieldNames.DOWNLOAD_FILM_URL_NO]);
        setHistoryUrl(arr[DownloadFieldNames.DOWNLOAD_HISTORY_URL_NO]);
        setUrl(arr[DownloadFieldNames.DOWNLOAD_URL_NO]);
        setUrlRtmp(arr[DownloadFieldNames.DOWNLOAD_URL_RTMP_NO]);
        setSubtitle(Boolean.parseBoolean(arr[DownloadFieldNames.DOWNLOAD_URL_SUBTITLE_NO]));
        setSetDataId(arr[DownloadFieldNames.DOWNLOAD_SET_DATA_NO]);
        setProgram(arr[DownloadFieldNames.DOWNLOAD_PROGRAM_NO]);
        setProgramCall(arr[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_NO]);
        setProgramCallArray(arr[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_ARRAY_NO]);
        setDestFileName(arr[DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME_NO]);
        setDestPath(arr[DownloadFieldNames.DOWNLOAD_DEST_PATH_NO]);
        setDestPathFile(arr[DownloadFieldNames.DOWNLOAD_DEST_PATH_FILE_NAME_NO]);
        setStartTime(arr[DownloadFieldNames.DOWNLOAD_START_TIME_NO]);

        setType(arr[DownloadFieldNames.DOWNLOAD_TYPE_NO]);
        if (!arr[DownloadFieldNames.DOWNLOAD_SOURCE_NO].equals(DownloadConstants.SRC_ABO)) {
            // bei gelöschten Abos kanns dazu kommen
            arr[DownloadFieldNames.DOWNLOAD_SOURCE_NO] = DownloadConstants.SRC_DOWNLOAD;
        }
        setSource(arr[DownloadFieldNames.DOWNLOAD_SOURCE_NO]);
        setPlacedBack(Boolean.parseBoolean(arr[DownloadFieldNames.DOWNLOAD_PLACED_BACK_NO]));
        setInfoFile(Boolean.parseBoolean(arr[DownloadFieldNames.DOWNLOAD_INFO_FILE_NO]));
        setSubtitle(Boolean.parseBoolean(arr[DownloadFieldNames.DOWNLOAD_SUBTITLE_NO]));
        setProgramDownloadmanager(Boolean.parseBoolean(arr[DownloadFieldNames.DOWNLOAD_PROGRAM_DOWNLOADMANAGER_NO]));
    }

    public void setXmlFromProps() {
        arr[DownloadFieldNames.DOWNLOAD_ABO_NO] = getAboName();
        arr[DownloadFieldNames.DOWNLOAD_SENDER_NO] = getChannel();
        arr[DownloadFieldNames.DOWNLOAD_THEME_NO] = getTheme();
        arr[DownloadFieldNames.DOWNLOAD_TITLE_NO] = getTitle();
        arr[DownloadFieldNames.DOWNLOAD_DATE_NO] = getFilmDate().toString();
        arr[DownloadFieldNames.DOWNLOAD_TIME_NO] = getTime();
        arr[DownloadFieldNames.DOWNLOAD_DURATION_NO] = String.valueOf(getDurationMinute());
        arr[DownloadFieldNames.DOWNLOAD_HD_NO] = String.valueOf(isHd());
        arr[DownloadFieldNames.DOWNLOAD_UT_NO] = String.valueOf(isUt());
        arr[DownloadFieldNames.DOWNLOAD_GEO_NO] = String.valueOf(getGeoBlocked());
        arr[DownloadFieldNames.DOWNLOAD_FILM_URL_NO] = getFilmUrl();
        arr[DownloadFieldNames.DOWNLOAD_HISTORY_URL_NO] = getHistoryUrl();
        arr[DownloadFieldNames.DOWNLOAD_URL_NO] = getUrl();
        arr[DownloadFieldNames.DOWNLOAD_URL_RTMP_NO] = getUrlRtmp();
        arr[DownloadFieldNames.DOWNLOAD_URL_SUBTITLE_NO] = getUrlSubtitle();
        arr[DownloadFieldNames.DOWNLOAD_SET_DATA_NO] = getSetDataId();
        arr[DownloadFieldNames.DOWNLOAD_PROGRAM_NO] = getProgram();
        arr[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_NO] = getProgramCall();
        arr[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_ARRAY_NO] = getProgramCallArray();
        arr[DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME_NO] = getDestFileName();
        arr[DownloadFieldNames.DOWNLOAD_DEST_PATH_NO] = getDestPath();
        arr[DownloadFieldNames.DOWNLOAD_DEST_PATH_FILE_NAME_NO] = getDestPathFile();
        arr[DownloadFieldNames.DOWNLOAD_START_TIME_NO] = getStartTime();
        arr[DownloadFieldNames.DOWNLOAD_TYPE_NO] = getType();
        arr[DownloadFieldNames.DOWNLOAD_SOURCE_NO] = getSource();
        arr[DownloadFieldNames.DOWNLOAD_PLACED_BACK_NO] = String.valueOf(getPlacedBack());
        arr[DownloadFieldNames.DOWNLOAD_INFO_FILE_NO] = String.valueOf(getInfoFile());
        arr[DownloadFieldNames.DOWNLOAD_SUBTITLE_NO] = String.valueOf(isSubtitle());
        arr[DownloadFieldNames.DOWNLOAD_PROGRAM_DOWNLOADMANAGER_NO] = String.valueOf(getProgramDownloadmanager());
    }

    @Override
    public int compareTo(Download arg0) {
        int ret;
        if ((ret = sorter.compare(arr[DownloadFieldNames.DOWNLOAD_SENDER_NO], arg0.arr[DownloadFieldNames.DOWNLOAD_SENDER_NO])) == 0) {
            return sorter.compare(arr[DownloadFieldNames.DOWNLOAD_THEME_NO], arg0.arr[DownloadFieldNames.DOWNLOAD_THEME_NO]);
        }
        return ret;
    }
}
