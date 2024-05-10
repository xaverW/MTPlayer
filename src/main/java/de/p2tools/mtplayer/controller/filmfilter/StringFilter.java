package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.configlist.ConfigStringList;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class StringFilter extends P2DataSample<StringFilter> implements Comparable<StringFilter> {

    public static String TAG = "StringFilter";

    private final ObservableList<String> filterListTheme = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListThemeTitle = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListTitel = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListSomewhere = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListUrl = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListFastFilter = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListAboName = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListAboDescription = FXCollections.observableArrayList("");

    private final ObservableList<String> filterListArdLive = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListArdUrl = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListZdfLive = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListZdfUrl = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListLiveThema = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListLiveTitel = FXCollections.observableArrayList("");


    public StringFilter() {
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new ConfigStringList("filterListTheme", filterListTheme));
        list.add(new ConfigStringList("filterListThemeTitle", filterListThemeTitle));
        list.add(new ConfigStringList("filterListTitel", filterListTitel));
        list.add(new ConfigStringList("filterListSomewhere", filterListSomewhere));
        list.add(new ConfigStringList("filterListUrl", filterListUrl));
        list.add(new ConfigStringList("filterListFastFilter", filterListFastFilter));

        list.add(new ConfigStringList("filterListAboName", filterListAboName));
        list.add(new ConfigStringList("filterListAboDescription", filterListAboDescription));

        list.add(new ConfigStringList("filterListArdLive", filterListArdLive));
        list.add(new ConfigStringList("filterListArdUrl", filterListArdUrl));
        list.add(new ConfigStringList("filterListZdfLive", filterListZdfLive));
        list.add(new ConfigStringList("filterListZdfUrl", filterListZdfUrl));
        list.add(new ConfigStringList("filterListLiveThema", filterListLiveThema));
        list.add(new ConfigStringList("filterListLiveTitel", filterListLiveTitel));
        return list.toArray(new Config[]{});
    }

    public ObservableList<String> getFilterListTheme() {
        return filterListTheme;
    }

    public ObservableList<String> getFilterListThemeTitle() {
        return filterListThemeTitle;
    }

    public ObservableList<String> getFilterListTitel() {
        return filterListTitel;
    }

    public ObservableList<String> getFilterListSomewhere() {
        return filterListSomewhere;
    }

    public ObservableList<String> getFilterListUrl() {
        return filterListUrl;
    }

    public ObservableList<String> getFilterListFastFilter() {
        return filterListFastFilter;
    }

    public ObservableList<String> getFilterListAboName() {
        return filterListAboName;
    }

    public ObservableList<String> getFilterListAboDescription() {
        return filterListAboDescription;
    }

    public ObservableList<String> getFilterListArdLive() {
        return filterListArdLive;
    }

    public ObservableList<String> getFilterListArdUrl() {
        return filterListArdUrl;
    }

    public ObservableList<String> getFilterListZdfLive() {
        return filterListZdfLive;
    }

    public ObservableList<String> getFilterListZdfUrl() {
        return filterListZdfUrl;
    }

    public ObservableList<String> getFilterListLiveThema() {
        return filterListLiveThema;
    }

    public ObservableList<String> getFilterListLiveTitel() {
        return filterListLiveTitel;
    }
}
