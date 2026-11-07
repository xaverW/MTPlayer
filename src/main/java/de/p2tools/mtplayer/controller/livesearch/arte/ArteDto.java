package de.p2tools.mtplayer.controller.livesearch.arte;

import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class ArteDto {
    private String searchString = "";
    private final ArrayList<FilmDataMTP> filmList = new ArrayList<>();
    private final ArrayList<ArteSiteDto> siteDtos = new ArrayList<>();
    private final StringProperty nextUrl = new SimpleStringProperty("");
    private ArteSiteDto arteSiteDto = new ArteSiteDto();

    public ArteDto() {
    }

    public void init() {
        searchString = "";
        nextUrl.set("");
        arteSiteDto = new ArteSiteDto();
        filmList.clear();
        siteDtos.clear();
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public ArrayList<FilmDataMTP> getFilmList() {
        return filmList;
    }

    public ArrayList<ArteSiteDto> getSiteDtos() {
        return siteDtos;
    }

    public ArteSiteDto getSiteDto() {
        return arteSiteDto;
    }

    public void setSiteDto(ArteSiteDto pageId) {
        this.arteSiteDto = pageId;
    }
}
