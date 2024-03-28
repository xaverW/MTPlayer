package de.p2tools.mtplayer.controller.livesearchardapi;

import de.p2tools.mtplayer.controller.film.FilmDataMTP;

import java.util.HashSet;
import java.util.Set;

public class ArdFilmDto {

    private final FilmDataMTP film;
    private final Set<ArdFilmInfoDto> relatedFilms;

    public ArdFilmDto(final FilmDataMTP film) {
        this.film = film;
        this.relatedFilms = new HashSet<>();
    }

    public FilmDataMTP getFilm() {
        return film;
    }

    public Set<ArdFilmInfoDto> getRelatedFilms() {
        return relatedFilms;
    }

    public void addRelatedFilm(final ArdFilmInfoDto filmInfoDto) {
        relatedFilms.add(filmInfoDto);
    }
}
