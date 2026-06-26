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

package de.p2tools.mtplayer.controller.data.history;

import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.mediathek.film.FilmDate;
import de.p2tools.p2lib.tools.GermanStringSorter;

public class HistoryData implements Comparable<HistoryData> {

    public static int SOURCE_SHOWN_DOWNLOAD = 0;
    public static int SOURCE_SHOWN = 1;
    public static int SOURCE_DOWNLOAD = 2;

    private final static GermanStringSorter sorter = GermanStringSorter.getInstance();
    public static final String TAG = "HistoryData";

    private int source;
    private FilmDate date;
    private final String channel;
    private final String theme;
    private final String title;
    private final String url;

    public HistoryData(DownloadData download) {
        this.source = download.isAbo() ? SOURCE_DOWNLOAD : SOURCE_SHOWN;
        try {
            this.date = new FilmDate();
        } catch (final Exception ignore) {
            this.date = new FilmDate(0);
        }
        this.channel = download.getChannel();
        this.theme = download.getTheme();
        this.title = download.getTitle();
        this.url = download.getHistoryUrl();
    }

    public HistoryData(DownloadData download, int source) {
        this.source = source;
        this.date = new FilmDate();
        this.channel = download.getChannel();
        this.theme = download.getTheme();
        this.title = download.getTitle();
        this.url = download.getHistoryUrl();
    }

    public HistoryData(int source, String channel, String theme, String title, String url) {
        this.source = source;
        this.channel = channel;
        this.theme = theme;
        this.title = title;
        this.url = url;
        this.date = new FilmDate();
    }

    public HistoryData(int source, String date, String channel, String theme, String title, String url) {
        this.source = source;
        this.channel = channel;
        this.theme = theme;
        this.title = title;
        this.url = url;
        try {
            FilmDate filmDate = new FilmDate();
            filmDate.setPDate(date);
            this.date = filmDate;
        } catch (final Exception ignore) {
            this.date = new FilmDate(0);
        }
    }

    public void addSourceDownload() {
        if (source == HistoryData.SOURCE_SHOWN) {
            source = HistoryData.SOURCE_SHOWN_DOWNLOAD;
        }
    }

    public void addSourceShown() {
        if (source == HistoryData.SOURCE_DOWNLOAD) {
            source = HistoryData.SOURCE_SHOWN_DOWNLOAD;
        }
    }

    public void addSourceShownDownload() {
        source = HistoryData.SOURCE_SHOWN_DOWNLOAD;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public FilmDate getDate() {
        return date;
    }

    public String getChannel() {
        return channel;
    }

    public String getTheme() {
        return theme;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return url;
    }

    @Override
    public int compareTo(HistoryData arg0) {
        return sorter.compare(getTitle(), arg0.getTitle());
    }
}
