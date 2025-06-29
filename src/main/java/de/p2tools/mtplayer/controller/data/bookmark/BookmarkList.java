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

package de.p2tools.mtplayer.controller.data.bookmark;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.p2lib.configfile.pdata.P2DataList;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.ArrayList;
import java.util.HashSet;

public class BookmarkList extends SimpleListProperty<BookmarkData> implements P2DataList<BookmarkData> {

    public static final String TAG = "BookmarkList";
    private final HashSet<String> urlHash = new HashSet<>();
    private FilteredList<BookmarkData> filteredList = null;
    private SortedList<BookmarkData> sortedList = null;
    private boolean found = false;

    public BookmarkList() {
        super(FXCollections.observableArrayList());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller Bookmarks";
    }

    @Override
    public BookmarkData getNewItem() {
        return new BookmarkData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(BookmarkData.class)) {
            BookmarkData d = (BookmarkData) obj;
            addToThisList(d);
        }
    }

    public SortedList<BookmarkData> getSortedList() {
        filteredList = getFilteredList();
        if (sortedList == null) {
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<BookmarkData> getFilteredList() {
        if (filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
        }
        return filteredList;
    }

    public void removeUrlHash(HashSet<String> removeUrlHash) {
        final ArrayList<BookmarkData> newList = new ArrayList<>();
        found = false;
        P2Duration.counterStart("Bookmark: removeFromBookmark");
        P2Log.sysLog("Aus Bookmarks löschen: " + removeUrlHash.size() + ", löschen aus: " + ProgConst.FILE_BOOKMARKS_XML);

        this.forEach(bookmarkData -> {
            if (removeUrlHash.contains(bookmarkData.getUrl())) {
                // nur dann muss das Logfile auch geschrieben werden
                found = true;
            } else {
                // kommt wieder in die history
                newList.add(bookmarkData);
            }
        });

        if (found) {
            // und nur dann wurde was gelöscht und muss geschrieben werden
            clearList();
            this.addAll(newList);
            fillUrlHash();
            BookmarkLoadSaveFactory.saveBookmark();
        }

        P2Duration.counterStop("Bookmark: removeFromBookmark");
    }

    //===============
    //===============
    public boolean checkIfUrlAlreadyIn(String urlFilm) {
        // wenn url gefunden, dann true zurück
        return urlHash.contains(urlFilm);
    }

    public void clearList() {
        urlHash.clear();
        super.clear();
    }

    public void addToThisList(BookmarkData bookmarkData) {
        this.add(bookmarkData);
        urlHash.add(bookmarkData.getUrl());
    }

    public void fillUrlHash() {
        urlHash.clear();
        this.forEach(bookmarkData -> urlHash.add(bookmarkData.getUrl()));
    }
}
