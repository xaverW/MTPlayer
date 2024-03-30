package de.p2tools.mtplayer.controller.livesearchzdf;

import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.livesearch.JsonInfoDto;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveConst;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.tools.log.PLog;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public class ZdfFilmDetailTask {
    private static final transient ZdfVideoUrlOptimizer optimizer = new ZdfVideoUrlOptimizer();
    private static final DateTimeFormatter DATE_FORMAT
            = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT
            = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ZdfFilmDetailTask() {
    }

    public void processRestTarget(final JsonInfoDto jsonInfoDto, ZdfFilmDto zdfFilmDto, DownloadDto downloadDto) {
        try {
            appendSignLanguage(downloadDto, zdfFilmDto.getUrlSignLanguage());

            final ZdfFilmDto result = zdfFilmDto;
            addFilm(jsonInfoDto, downloadDto, result);
        } catch (Exception e) {
            PLog.errorLog(959562654, e, jsonInfoDto.getSearchString());
        }
    }

    private void appendSignLanguage(DownloadDto downloadDto, Optional<String> urlSignLanguage) {
        if (urlSignLanguage.isPresent()) {
            downloadDto
                    .getDownloadUrls(ZdfConstants.LANGUAGE_GERMAN)
                    .forEach((resolution, url) ->
                            downloadDto.addUrl(ZdfConstants.LANGUAGE_GERMAN_DGS, resolution, url));
        }
    }

    private void addFilm(final JsonInfoDto jsonInfoDto, DownloadDto downloadDto, final ZdfFilmDto zdfFilmDto) {
        for (final String language : downloadDto.getLanguages()) {

            if (downloadDto.getUrl(language, LiveConst.Qualities.NORMAL).isPresent()) {
                DownloadDtoFilmConverter.getOptimizedUrls(
                        downloadDto.getDownloadUrls(language), Optional.of(optimizer));

                final FilmDataMTP filmWithLanguage = createFilm(downloadDto, zdfFilmDto, language);
                LiveFactory.setFilmSize(filmWithLanguage);
                filmWithLanguage.init();
                jsonInfoDto.getList().add(filmWithLanguage);
            }
        }
    }

    private FilmDataMTP createFilm(DownloadDto downloadDto, final ZdfFilmDto zdfFilmDto, final String aLanguage) {
        final String title = updateTitle(aLanguage, zdfFilmDto.getTitle());
        LocalDateTime time = zdfFilmDto.getTime().orElse(LocalDateTime.now());
        String dateValue = time.format(DATE_FORMAT);
        String timeValue = time.format(TIME_FORMAT);

        Map<LiveConst.Qualities, String> downloadUrls = downloadDto.getDownloadUrls(aLanguage);
        String urlNormal = downloadUrls.get(LiveConst.Qualities.NORMAL);
        Duration duration = zdfFilmDto.getDuration().orElse(downloadDto.getDuration().orElse(Duration.ZERO));

        FilmDataMTP film = new ZdfDatenFilm(LiveConst.ZDF,
                zdfFilmDto.getTopic().orElse(title),
                zdfFilmDto.getWebsite().orElse(""),
                title, urlNormal,
                dateValue, timeValue, duration.getSeconds(), zdfFilmDto.getDescription().orElse(""));

        if (downloadUrls.containsKey(LiveConst.Qualities.SMALL)) {
            LiveFactory.addUrlKlein(film, downloadUrls.get(LiveConst.Qualities.SMALL));
        }
        if (downloadUrls.containsKey(LiveConst.Qualities.HD)) {
            LiveFactory.addUrlHd(film, downloadUrls.get(LiveConst.Qualities.HD));
        }
        final Optional<String> subTitleUrl = downloadDto.getSubTitleUrl(aLanguage);
        if (subTitleUrl.isPresent()) {
            LiveFactory.addUrlSubtitle(film, subTitleUrl.get());
        }

        final Optional<GeoLocations> geoLocation = downloadDto.getGeoLocation();
        geoLocation.ifPresent(geoLocations -> film.arr[FilmDataXml.FILM_GEO] = geoLocations.getDescription());

        return film;
    }

    private String updateTitle(final String aLanguage, final String aTitle) {
        String title = aTitle;
        switch (aLanguage) {
            case ZdfConstants.LANGUAGE_GERMAN:
                return title;
            case ZdfConstants.LANGUAGE_GERMAN_AD:
                title += " (Audiodeskription)";
                break;
            case ZdfConstants.LANGUAGE_GERMAN_DGS:
                title += " (Gebärdensprache)";
                break;
            case ZdfConstants.LANGUAGE_ENGLISH:
                title += " (Englisch)";
                break;
            case ZdfConstants.LANGUAGE_FRENCH:
                title += " (Französisch)";
                break;
            default:
                title += "(" + aLanguage + ")";
        }

        return title;
    }
}
