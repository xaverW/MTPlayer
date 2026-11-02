package de.p2tools.mtplayer.controller.livesearch.arte;

import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.livesearch.ard.ArdSearchFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class ArteDto {
    public static final String SEARCH = "SEARCH";
    public static final String ID = "ID";
    public static int PAGE_SIZE = 20;
    public static final String PAGE_NO = "0";
    public static final String SEARCH_URL = "https://api.arte.tv/api/emac/v4/de/web/search/results?page=1&query=" + SEARCH;

    public static final String PAGE_URL = "https://api.ardmediathek.de/page-gateway/pages/ard/item/" +
            ID +
            "?embedded=false&mcV6=true";

    private String searchString = "";
    private ArrayList<FilmDataMTP> filmList = new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();
    private final StringProperty nextUrl = new SimpleStringProperty("");
    private int totalElements = 0;
    private int actPage = 0;
    private String pageId = "";

    public ArteDto() {
    }

    public void init() {
        searchString = "";
        nextUrl.set("");
        totalElements = 0;
        pageId = "";
        filmList.clear();
        idList.clear();
    }

    public String getSearchUrl() {
        return ArteSearchFactory.getSearchUrl(searchString);
    }

    public String getSearchUrl(String searchString) {
        setSearchString(searchString);
        return ArteSearchFactory.getSearchUrl(searchString);
    }

    // =========================

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public ArrayList<FilmDataMTP> getFilmList() {
        return filmList;
    }

    public ArrayList<String> getIdList() {
        return idList;
    }

    public String addNextUrl() {
        setActPage(getActPage() + 1);
        nextUrl.set(ArdSearchFactory.getNextSearchUrl(searchString, actPage));
        return nextUrl.get();
    }

    public String getNextUrl() {
        return nextUrl.get();
    }

    public StringProperty nextUrlProperty() {
        return nextUrl;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void addTotalElements(int totalElements) {
        this.totalElements += totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getActPage() {
        return actPage;
    }

    public void setActPage(int actPage) {
        this.actPage = actPage;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }
}
