package de.p2tools.mtplayer.controller.filter;

import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.configlist.ConfigStringList;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class StringFilter extends P2DataSample<StringFilter> implements Comparable<StringFilter> {
    // sind die gespeicherten String in den CBO's der TextFilter

    public static String TAG = "StringFilter";

    // Film
    private final ObservableList<String> filterListFilmTheme = FXCollections.observableArrayList();
    private final ObservableList<String> filterListFilmThemeTitle = FXCollections.observableArrayList();
    private final ObservableList<String> filterListFilmTitel = FXCollections.observableArrayList();
    private final ObservableList<String> filterListFilmSomewhere = FXCollections.observableArrayList();
    private final ObservableList<String> filterListFilmUrl = FXCollections.observableArrayList();

    // FastFilter
    private final ObservableList<String> filterListFastFilterFilm = FXCollections.observableArrayList();
    private final ObservableList<String> filterListFastFilterAudio = FXCollections.observableArrayList();

    // Audio
    private final ObservableList<String> filterListAudioTheme = FXCollections.observableArrayList();
    private final ObservableList<String> filterListAudioThemeTitle = FXCollections.observableArrayList();
    private final ObservableList<String> filterListAudioTitel = FXCollections.observableArrayList();
    private final ObservableList<String> filterListAudioSomewhere = FXCollections.observableArrayList();
    private final ObservableList<String> filterListAudioUrl = FXCollections.observableArrayList();

    // Abo
    private final ObservableList<String> filterListAboName = FXCollections.observableArrayList();
    private final ObservableList<String> filterListAboSearchText = FXCollections.observableArrayList();
    private final ObservableList<String> filterListAboDescription = FXCollections.observableArrayList();

    // Live-Filter
    private final ObservableList<String> filterListLiveArd = FXCollections.observableArrayList();
    private final ObservableList<String> filterListLiveArdUrl = FXCollections.observableArrayList();
    private final ObservableList<String> filterListLiveZdf = FXCollections.observableArrayList();
    private final ObservableList<String> filterListLiveZdfUrl = FXCollections.observableArrayList();
    private final ObservableList<String> filterListLiveThema = FXCollections.observableArrayList();
    private final ObservableList<String> filterListLiveTitel = FXCollections.observableArrayList();

    public StringFilter() {
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new ConfigStringList("filterListTheme", filterListFilmTheme));
        list.add(new ConfigStringList("filterListThemeTitle", filterListFilmThemeTitle));
        list.add(new ConfigStringList("filterListTitel", filterListFilmTitel));
        list.add(new ConfigStringList("filterListSomewhere", filterListFilmSomewhere));
        list.add(new ConfigStringList("filterListUrl", filterListFilmUrl));

        list.add(new ConfigStringList("filterListFastFilterFilm", filterListFastFilterFilm));
        list.add(new ConfigStringList("filterListFastFilterAudio", filterListFastFilterAudio));

        list.add(new ConfigStringList("filterListAudioTheme", filterListAudioTheme));
        list.add(new ConfigStringList("filterListAudioThemeTitle", filterListAudioThemeTitle));
        list.add(new ConfigStringList("filterListAudioTitel", filterListAudioTitel));
        list.add(new ConfigStringList("filterListAudioSomewhere", filterListAudioSomewhere));
        list.add(new ConfigStringList("filterListAudioUrl", filterListAudioUrl));

        list.add(new ConfigStringList("filterListAboName", filterListAboName));
        list.add(new ConfigStringList("filterListAboSearchText", filterListAboSearchText));
        list.add(new ConfigStringList("filterListAboDescription", filterListAboDescription));

        list.add(new ConfigStringList("filterListArdLive", filterListLiveArd));
        list.add(new ConfigStringList("filterListArdUrl", filterListLiveArdUrl));
        list.add(new ConfigStringList("filterListZdfLive", filterListLiveZdf));
        list.add(new ConfigStringList("filterListZdfUrl", filterListLiveZdfUrl));
        list.add(new ConfigStringList("filterListLiveThema", filterListLiveThema));
        list.add(new ConfigStringList("filterListLiveTitel", filterListLiveTitel));
        return list.toArray(new Config[]{});
    }

    // Film
    public ObservableList<String> getFilterListFilmTheme() {
        return filterListFilmTheme;
    }

    public ObservableList<String> getFilterListFilmThemeTitle() {
        return filterListFilmThemeTitle;
    }

    public ObservableList<String> getFilterListFilmTitel() {
        return filterListFilmTitel;
    }

    public ObservableList<String> getFilterListFilmSomewhere() {
        return filterListFilmSomewhere;
    }

    public ObservableList<String> getFilterListFilmUrl() {
        return filterListFilmUrl;
    }

    // FastFilter
    public ObservableList<String> getFilterListFastFilterFilm() {
        return filterListFastFilterFilm;
    }

    public ObservableList<String> getFilterListFastFilterAudio() {
        return filterListFastFilterAudio;
    }

    // Audio
    public ObservableList<String> getFilterListAudioTheme() {
        return filterListAudioTheme;
    }

    public ObservableList<String> getFilterListAudioThemeTitle() {
        return filterListAudioThemeTitle;
    }

    public ObservableList<String> getFilterListAudioTitel() {
        return filterListAudioTitel;
    }

    public ObservableList<String> getFilterListAudioSomewhere() {
        return filterListAudioSomewhere;
    }

    public ObservableList<String> getFilterListAudioUrl() {
        return filterListAudioUrl;
    }

    // Abo
    public ObservableList<String> getFilterListAboName() {
        return filterListAboName;
    }

    public ObservableList<String> getFilterListAboSearchText() {
        return filterListAboSearchText;
    }

    public ObservableList<String> getFilterListAboDescription() {
        return filterListAboDescription;
    }

    // Live
    public ObservableList<String> getFilterListLiveArd() {
        return filterListLiveArd;
    }

    public ObservableList<String> getFilterListLiveArdUrl() {
        return filterListLiveArdUrl;
    }

    public ObservableList<String> getFilterListLiveZdf() {
        return filterListLiveZdf;
    }

    public ObservableList<String> getFilterListLiveZdfUrl() {
        return filterListLiveZdfUrl;
    }

    public ObservableList<String> getFilterListLiveThema() {
        return filterListLiveThema;
    }

    public ObservableList<String> getFilterListLiveTitel() {
        return filterListLiveTitel;
    }
}
