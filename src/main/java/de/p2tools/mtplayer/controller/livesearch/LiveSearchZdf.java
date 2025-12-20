package de.p2tools.mtplayer.controller.livesearch;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.mtplayer.controller.livesearchzdf.ZdfFilmDetailDeserializer;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.tools.log.P2Log;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Optional;

public class LiveSearchZdf {
    private static final String ATTRIBUTE_HREF = "href";
    private static final String URL_BASE = "https://www.zdf.de/suche";
    private static final String URL_API_BASE = "https://api.zdf.de";

    public LiveSearchZdf() {
    }

    public void loadLive(JsonInfoDto jsonInfoDto, boolean next) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ZDF);

        if (next) {
            jsonInfoDto.getList().clear();
        } else {
            jsonInfoDto.init();
            jsonInfoDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_ZDF.getValue());

            try {
                final Optional<Document> document = LiveFactory.loadPage(URL_BASE);
                if (document.isPresent()) {
                    final Optional<String> searchBearer = ZdfBearerFactory.parseIndexPage(document.get());
                    searchBearer.ifPresent(jsonInfoDto::setApi);
                } else {
                    P2Alert.showErrorAlert("Filme suchen", "Es konnten keine Filme " +
                            "gefunden werden.");
                    return;
                }
            } catch (final Exception ex) {
                P2Log.errorLog(874587458, ex, "Url: " + URL_BASE);
            }
        }

        List<String> list = ZdfSearchFactory.getFilmList(jsonInfoDto, next);
        for (String url : list) {
            url = addApiUrlBase("content/documents/" + url + ".json");
            new ZdfFilmDetailDeserializer(jsonInfoDto).deserialize(url);
        }

        LiveFactory.addToList(jsonInfoDto);
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ZDF);
        P2Log.sysLog("Filme gefunden: " + jsonInfoDto.getList().size());
    }

    public void loadUrl(JsonInfoDto jsonInfoDto) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ZDF);

        jsonInfoDto.init();
        jsonInfoDto.setStartUrl(ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ZDF.getValue());

        try {
            final Optional<Document> document = LiveFactory.loadPage(URL_BASE);
            if (document.isPresent()) {
                final Optional<String> searchBearer = ZdfBearerFactory.parseIndexPage(document.get());
                searchBearer.ifPresent(jsonInfoDto::setApi);

                String url = jsonInfoDto.getStartUrl();
                P2Log.sysLog("Filme suchen: " + url);
                workFilm(jsonInfoDto, url);
            }
        } catch (final Exception ex) {
            P2Log.errorLog(898945124, ex, "Url: " + URL_BASE);
        }

        LiveFactory.addToList(jsonInfoDto);
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ZDF);
        P2Log.sysLog("Filme gefunden: " + jsonInfoDto.getList().size());
    }

    private void workFilm(JsonInfoDto jsonInfoDto, String url) {
        // jetzt jeden Film abarbeiten
        // https://www.zdf.de/play/magazine/volle-kanne-104  /volle-kanne-vom-14-maerz-2025-mit-michael-nast-100
        // https://api.zdf.de/content/documents              /volle-kanne-vom-14-maerz-2025-mit-michael-nast-100.json

        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        url = url.substring(url.lastIndexOf("/") + 1);
        url = addApiUrlBase("content/documents/" + url + ".json");
        new ZdfFilmDetailDeserializer(jsonInfoDto).deserialize(url);
    }

    private String addApiUrlBase(String url) {
        if (!url.startsWith("/")) {
            return URL_API_BASE + "/" + url;
        } else {
            return URL_API_BASE + url;
        }
    }
}
