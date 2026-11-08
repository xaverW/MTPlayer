package de.p2tools.mtplayer.controller.livesearch.arte;

import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.livesearch.tools.JsonFactory;
import de.p2tools.p2lib.mediathek.filmdata.FilmDataXml;
import de.p2tools.p2lib.tools.log.P2Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.Optional;

public class ArteSiteFactory {
    private static final String GERMAN_TIME_ZONE = "Europe/Berlin";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATUM_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private ArteSiteFactory() {
    }

    public static void addSite(ArteDto arteDto) {
        // website  https://www.arte.tv/de/videos/118267-017-A/re-unsere-trinkhalle-darf-nicht-sterben/
        // config	https://api.arte.tv/api/player/v2/config/de/118267-017-A
        // playlist	https://api.arte.tv/api/player/v2/playlist/de/125691-000-A

        String urlConfig = "https://api.arte.tv/api/player/v2/config/de/" + arteDto.getSiteDto().getId();
        FilmDataMTP filmDataMTP = new FilmDataMTP();
        getConfig(arteDto, urlConfig, filmDataMTP);
        if (!filmDataMTP.getUrl().isEmpty()) {
            arteDto.getFilmList().add(filmDataMTP);
        }
    }

    private static void getConfig(ArteDto arteDto, String url, FilmDataMTP filmDataMTP) {
        try {
            Optional<JsonNode> root = JsonFactory.getRootNode(url);
            if (root.isEmpty()) {
                return;
            }
            JsonNode nodeRoot = root.get();

            filmDataMTP.arr[FilmDataMTP.FILM_SIZE] = "";
            filmDataMTP.arr[FilmDataMTP.FILM_URL_SUBTITLE] = "";

            filmDataMTP.arr[FilmDataMTP.FILM_URL] = "";
            filmDataMTP.arr[FilmDataMTP.FILM_URL_SMALL] = "";
            filmDataMTP.arr[FilmDataMTP.FILM_URL_HD] = "";

            filmDataMTP.arr[FilmDataMTP.FILM_CHANNEL] = "ARTE";

            Optional<JsonNode> node = JsonFactory.getOptElement(nodeRoot, new String[]{"data", "attributes", "metadata", "link", "url"});
            node.ifPresent(n -> filmDataMTP.arr[FilmDataMTP.FILM_WEBSITE] = n.asText());

            node = JsonFactory.getOptElement(nodeRoot, new String[]{"data", "attributes", "metadata", "description"});
            node.ifPresent(n -> filmDataMTP.arr[FilmDataMTP.FILM_DESCRIPTION] = n.asText());

            node = JsonFactory.getOptElement(nodeRoot, new String[]{"data", "attributes", "rights", "begin"});
            node.ifPresent(n -> parseDate(filmDataMTP, n.asText()));

            node = JsonFactory.getOptElement(nodeRoot, new String[]{"data", "attributes", "stat", "push", "genre"});
            node.ifPresent(n ->
                    filmDataMTP.arr[FilmDataMTP.FILM_THEME] = arteDto.getSiteDto().getGenre().isEmpty() ?
                            "ARTE" : arteDto.getSiteDto().getGenre());

            node = JsonFactory.getOptElement(nodeRoot, new String[]{"data", "attributes", "metadata", "title"});
            node.ifPresent(n -> filmDataMTP.arr[FilmDataMTP.FILM_TITLE] = n.asText());
            if (filmDataMTP.arr[FilmDataMTP.FILM_TITLE].isEmpty()) {
                node = JsonFactory.getOptElement(nodeRoot, new String[]{"data", "attributes", "metadata", "subtitle"});
                node.ifPresent(n -> filmDataMTP.arr[FilmDataMTP.FILM_TITLE] = n.asText());
            }

            node = JsonFactory.getOptElement(nodeRoot, new String[]{"data", "attributes", "metadata", "duration", "seconds"});
            node.ifPresent(n -> checkFilmDauer(filmDataMTP, n.asLong()));

            Iterator<JsonNode> it = JsonFactory.getIterator(nodeRoot, new String[]{"data", "attributes", "streams"});
            while (it.hasNext()) {
                node = JsonFactory.getOptElement(it.next(), new String[]{"url"});
                node.ifPresent(n -> filmDataMTP.arr[FilmDataMTP.FILM_URL] = n.asText());
            }
        } catch (final Exception ex) {
            P2Log.errorLog(546212147, ex, "Url: " + url);
        }
    }

    private static void parseDate(FilmDataMTP filmDataMTP, String date) {
        try {
            final ZonedDateTime inputDateTime = ZonedDateTime.parse(date);
            final LocalDateTime localDateTime = inputDateTime.withZoneSameInstant(ZoneId.of(GERMAN_TIME_ZONE)).toLocalDateTime();
            LocalDateTime time = localDateTime == null ? LocalDateTime.now() : localDateTime;
            String dateValue = time.format(DATE_FORMAT);
            String timeValue = time.format(TIME_FORMAT);
            checkDatum(filmDataMTP, dateValue);
            checkTime(filmDataMTP, dateValue, timeValue);
        } catch (final DateTimeParseException ex) {
            P2Log.errorLog(852147852, ex, "Error parsing date time value " + date);
        }
    }

    private static void checkDatum(FilmDataMTP filmDataMTP, String date) {
        date = date.trim();
        if (date.contains(".") && date.length() == 10) {
            try {
                LocalDate filmDate = LocalDate.parse(date, DATUM_FORMATTER);
                if (filmDate.getYear() < 1900) {
                    P2Log.errorLog(852145877, "Unsinniger Wert: [" + date + "]");
                } else {
                    filmDataMTP.arr[FilmDataXml.FILM_DATE] = date;
                }
            } catch (Exception var4) {
                P2Log.errorLog(745458745, var4 + "[" + date + "]");
            }
        }
    }

    private static void checkTime(FilmDataMTP filmDataMTP, String date, String time) {
        time = time.trim();
        if (!date.isEmpty() && !time.isEmpty()) {
            if (time.contains(":") && time.length() == 8) {
                filmDataMTP.arr[FilmDataXml.FILM_TIME] = time;
            } else {
                P2Log.errorLog(702102159, "[" + time + "] ");
            }
        }
    }

    private static void checkFilmDauer(FilmDataMTP filmDataMTP, long dauerSekunden) {
        if (dauerSekunden > 0L && dauerSekunden <= 356400L) {
            String hours = String.valueOf(dauerSekunden / 3600L);
            dauerSekunden %= 3600L;
            String min = String.valueOf(dauerSekunden / 60L);
            String seconds = String.valueOf(dauerSekunden % 60L);
            filmDataMTP.arr[FilmDataXml.FILM_DURATION] = fuellen(hours) + ':' + fuellen(min) + ':' + fuellen(seconds);
        } else {
            filmDataMTP.arr[FilmDataXml.FILM_DURATION] = "";
        }
    }

    private static String fuellen(String s) {
        StringBuilder sBuilder = new StringBuilder(s);
        while (sBuilder.length() < 2) {
            sBuilder.insert(0, '0');
        }
        s = sBuilder.toString();
        return s;
    }
}
