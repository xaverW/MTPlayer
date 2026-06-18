package de.p2tools.mtplayer.controller.livesearch.ard;

import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class ArdDto {
    public static final String SEARCH = "SEARCH";
    public static final String ID = "ID";
    public static int PAGE_SIZE = 20;
    public static final String PAGE_NO = "0";
    public static final String SEARCH_URL = "https://api.ardmediathek.de/search-system/search/vods/ard?"
            + "query=" +
            SEARCH
            + "&pageNumber=" +
            PAGE_NO
            + "&pageSize=24&platform=MEDIA_THEK&sortingCriteria=SCORE_DESC";

    public static final String PAGE_URL = "https://api.ardmediathek.de/page-gateway/pages/ard/item/" +
            ID +
            "?embedded=false&mcV6=true";

    private String searchString = "";
    private ArrayList<FilmDataMTP> filmList = new ArrayList<>();
    private final StringProperty nextUrl = new SimpleStringProperty("");
    private long totalElements = 0;
    private int actPage = 0;
    private String pageId = "";

    public ArdDto() {
    }

    public void init() {
    }

    public String getSearchUrl() {
        return ArdSearchFactory.getSearchUrl(searchString);
    }

    public String getSearchUrl(String searchString) {
        setSearchString(searchString);
        return ArdSearchFactory.getSearchUrl(searchString);
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

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
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
