package de.p2tools.mtplayer.controller.livesearchzdf;

import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.mediathek.filmdata.FilmDataXml;
import de.p2tools.p2lib.tools.log.P2Log;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ZdfDatenFilm extends FilmDataMTP {
    private static final String[] LEGAL_NOTICES = new String[]{"+++ Aus rechtlichen Gründen ist der Film nur innerhalb von Deutschland abrufbar. +++", "+++ Aus rechtlichen Gründen ist diese Sendung nur innerhalb von Deutschland abrufbar. +++", "+++ Aus rechtlichen Gründen ist dieses Video nur innerhalb von Deutschland abrufbar. +++", "+++ Aus rechtlichen Gründen ist dieses Video nur innerhalb von Deutschland verfügbar. +++", "+++ Aus rechtlichen Gründen kann das Video nur innerhalb von Deutschland abgerufen werden. +++ Due to legal reasons the video is only available in Germany.+++", "+++ Aus rechtlichen Gründen kann das Video nur innerhalb von Deutschland abgerufen werden. +++", "+++ Due to legal reasons the video is only available in Germany.+++", "+++ Aus rechtlichen Gründen kann das Video nur in Deutschland abgerufen werden. +++", "[Aus rechtlichen Günden können wir die Partie nicht als Einzelclip anbieten.]", "+++ Aus rechtlichen Gründen ist das Video nur innerhalb von Deutschland abrufbar. +++", "+++Aus rechtlichen Gründen kann die Sendung nur innerhalb von Deutschland abgerufen werden. +++", "+++ Aus rechtlichen Gründen dürfen wir dieses Video nur innerhalb von Deutschland anbieten. +++", "+++Aus rechtlichen Gründen kann dieses Video nur innerhalb von Deutschland abgerufen werden.+++"};
    private static final DateTimeFormatter DATUM_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ZdfDatenFilm(String ssender, String tthema, String filmWebsite, String ttitel, String uurl,
                        String datum, String zeit, long dauerSekunden, String description) {
        arr[FilmDataXml.FILM_CHANNEL] = ssender;
        arr[FilmDataXml.FILM_THEME] = tthema.isEmpty() ? ssender : normalize(cleanWhitespaces(tthema.trim()));
        arr[FilmDataXml.FILM_WEBSITE] = filmWebsite;
        arr[FilmDataXml.FILM_TITLE] = ttitel.isEmpty() ? tthema : ttitel.trim();
        arr[FilmDataXml.FILM_URL] = uurl;
        checkDatum(datum,
                this.arr[FilmDataXml.FILM_CHANNEL] + ' ' +
                        this.arr[FilmDataXml.FILM_THEME] + ' ' +
                        this.arr[FilmDataXml.FILM_TITLE]);
        this.checkZeit(this.arr[FilmDataXml.FILM_DATE], zeit,
                this.arr[FilmDataXml.FILM_CHANNEL] + ' ' +
                        this.arr[FilmDataXml.FILM_THEME] + ' ' +
                        this.arr[FilmDataXml.FILM_TITLE]);
        this.arr[FilmDataXml.FILM_DESCRIPTION] = normalize(cleanDescription(description));
        this.checkFilmDauer(dauerSekunden);
    }

    private void checkFilmDauer(long dauerSekunden) {
        if (dauerSekunden > 0L && dauerSekunden <= 356400L) {
            String hours = String.valueOf(dauerSekunden / 3600L);
            dauerSekunden %= 3600L;
            String min = String.valueOf(dauerSekunden / 60L);
            String seconds = String.valueOf(dauerSekunden % 60L);
            this.arr[FilmDataXml.FILM_DURATION] = this.fuellen(2, hours) + ':' + this.fuellen(2, min) + ':' + this.fuellen(2, seconds);
        } else {
            this.arr[FilmDataXml.FILM_DURATION] = "";
        }
    }

    private String fuellen(int anz, String s) {
        while (s.length() < anz) {
            s = '0' + s;
        }
        return s;
    }

    private void checkZeit(String datum, String zeit, String fehlermeldung) {
        zeit = zeit.trim();
        if (!datum.isEmpty() && !zeit.isEmpty()) {
            if (zeit.contains(":") && zeit.length() == 8) {
                this.arr[FilmDataXml.FILM_TIME] = zeit;
            } else {
                P2Log.errorLog(159623647, '[' + zeit + "] " + fehlermeldung);
            }
        }

    }

    private void checkDatum(String datum, String fehlermeldung) {
        datum = datum.trim();
        if (datum.contains(".") && datum.length() == 10) {
            try {
                LocalDate filmDate = LocalDate.parse(datum, DATUM_FORMATTER);
                if (filmDate.getYear() < 1900) {
                    P2Log.errorLog(923012125, "Unsinniger Wert: [" + datum + "] " + fehlermeldung);
                } else {
                    this.arr[FilmDataXml.FILM_DATE] = datum;
                }
            } catch (Exception var4) {
                P2Log.errorLog(794630593, var4);
                P2Log.errorLog(946301596, '[' + datum + "] " + fehlermeldung);
            }
        }

    }

    private static String normalize(String s) {
        return s != null ? Normalizer.normalize(s, Normalizer.Form.NFC) : null;
    }

    public static String cleanWhitespaces(String text) {
        return text.replaceAll("[\\t\\n\\x0B\\f\\r]", "").replace(" ", " ");
    }

    private static String cleanDescription(String description) {
        description = removeHtml(description);
        String[] var1 = LEGAL_NOTICES;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            String legalNotice = var1[var3];
            if (description.contains(legalNotice)) {
                description = description.replace(legalNotice, "");
            }
        }

        if (description.startsWith("|")) {
            description = description.substring(1).trim();
        }

        if (description.startsWith("Video-Clip")) {
            description = description.substring("Video-Clip".length()).trim();
        }

        if (description.startsWith(":") || description.startsWith(",") || description.startsWith("\n")) {
            description = description.substring(1).trim();
        }

        if (description.contains("\\\"")) {
            description = description.replace("\\\"", "\"");
        }

        return description.length() > 400 ? description.substring(0, 400) + "\n....." : description;
    }

    public static String removeHtml(String in) {
        return in.replaceAll("\\<.*?>", "");
    }

    @Override
    public String getIndex() {
        // zdf uses different hosts for load balancing
        // https://rodl..., https://nrodl...
        // ignore the hosts in index to avoid duplicate entries        
        String url = getUrl();

        url = url.replaceFirst("https://nrodl", "https://rodl")
                .replaceFirst("http://nrodl", "http://rodl");

        return arr[FilmDataXml.FILM_CHANNEL].toLowerCase() + arr[FilmDataXml.FILM_THEME].toLowerCase() + url;
    }
}
