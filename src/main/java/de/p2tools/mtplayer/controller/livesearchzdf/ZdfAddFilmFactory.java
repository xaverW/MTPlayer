package de.p2tools.mtplayer.controller.livesearchzdf;

import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.livesearch.*;
import de.p2tools.mtplayer.controller.livesearchard.LiveSearchFactory;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.tools.log.PLog;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public class ZdfAddFilmFactory {
    private static final transient ZdfVideoUrlOptimizer optimizer = new ZdfVideoUrlOptimizer();
    private static final DateTimeFormatter DATE_FORMAT
            = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT
            = DateTimeFormatter.ofPattern("HH:mm:ss");

    private ZdfAddFilmFactory() {
    }


    public static void processRestTarget(final JsonInfoDtoZdf aDto, DownloadDto downloadDto) {
        try {
            appendSignLanguage(downloadDto, aDto.getZdfFilmDto().getUrlSignLanguage());

            final ZdfFilmDto result = aDto.getZdfFilmDto();
            addFilm(aDto, downloadDto, result);
        } catch (Exception e) {
            PLog.errorLog(959562654, e, aDto.getSearchString());
        }
    }

    private static void appendSignLanguage(DownloadDto downloadDto, Optional<String> urlSignLanguage) {
        if (urlSignLanguage.isPresent()) {
            downloadDto
                    .getDownloadUrls(ZdfConstants.LANGUAGE_GERMAN)
                    .forEach((resolution, url) ->
                            downloadDto.addUrl(ZdfConstants.LANGUAGE_GERMAN_DGS, resolution, url));
        }
    }

    private static void addFilm(final JsonInfoDtoZdf jsonInfoDtoZdf, DownloadDto downloadDto, final ZdfFilmDto zdfFilmDto) {
        for (final String language : downloadDto.getLanguages()) {

            if (downloadDto.getUrl(language, Qualities.NORMAL).isPresent()) {
                DownloadDtoFilmConverter.getOptimizedUrls(
                        downloadDto.getDownloadUrls(language), Optional.of(optimizer));

                final FilmDataMTP filmWithLanguage = createFilm(downloadDto, zdfFilmDto, jsonInfoDtoZdf, language);
                LiveSearchFactory.setFilmSize(filmWithLanguage);
                filmWithLanguage.init();
                jsonInfoDtoZdf.getList().add(filmWithLanguage);
            }
        }
    }

    private static FilmDataMTP createFilm(DownloadDto downloadDto, final ZdfFilmDto zdfFilmDto, final JsonInfoDtoZdf jsonInfoDtoZdf, final String aLanguage) {
        final String title = updateTitle(aLanguage, zdfFilmDto.getTitle());
        LocalDateTime time = zdfFilmDto.getTime().orElse(LocalDateTime.now());
        String dateValue = time.format(DATE_FORMAT);
        String timeValue = time.format(TIME_FORMAT);

        Map<Qualities, String> downloadUrls = downloadDto.getDownloadUrls(aLanguage);
        String urlNormal = downloadUrls.get(Qualities.NORMAL);
        Duration duration = zdfFilmDto.getDuration().orElse(downloadDto.getDuration().orElse(Duration.ZERO));
        String fileSize = "";

        FilmDataMTP film = new ZdfDatenFilm(LiveConst.ZDF,
                zdfFilmDto.getTopic().orElse(title),
                zdfFilmDto.getWebsite().orElse(""),
                title, urlNormal,
                dateValue, timeValue, duration.getSeconds(), zdfFilmDto.getDescription().orElse(""), fileSize);

        if (downloadUrls.containsKey(Qualities.SMALL)) {
            LiveFactory.addUrlKlein(film, downloadUrls.get(Qualities.SMALL));
        }
        if (downloadUrls.containsKey(Qualities.HD)) {
            LiveFactory.addUrlHd(film, downloadUrls.get(Qualities.HD));
        }
        final Optional<String> subTitleUrl = downloadDto.getSubTitleUrl(aLanguage);
        if (subTitleUrl.isPresent()) {
            LiveFactory.addUrlSubtitle(film, subTitleUrl.get());
        }

        final Optional<GeoLocations> geoLocation = downloadDto.getGeoLocation();
        geoLocation.ifPresent(geoLocations -> film.arr[FilmDataXml.FILM_GEO] = geoLocations.getDescription());

        return film;
    }

    private static String updateTitle(final String aLanguage, final String aTitle) {
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
