package de.p2tools.mtplayer.controller.livesearch;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.mtplayer.controller.livesearch.zdf.ZdfBearerFactory;
import de.p2tools.mtplayer.controller.livesearch.zdf.ZdfDto;
import de.p2tools.mtplayer.controller.livesearch.zdf.ZdfFilmDetailDeserializer;
import de.p2tools.mtplayer.controller.livesearch.zdf.ZdfSearchFactory;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.tools.log.P2Log;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LiveSearchZdf {
    private static final String ATTRIBUTE_HREF = "href";
    private static final String URL_BASE = "https://www.zdf.de/suche";
    private static final String URL_API_BASE = "https://api.zdf.de";

    public LiveSearchZdf() {
    }

    public void loadLive(ZdfDto zdfDto) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ZDF);
        List<String> urlList = new ArrayList<>();

        zdfDto.init();
        zdfDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_ZDF.getValue());

        try {
            final Optional<Document> document = LiveFactory.loadPage(URL_BASE);
            if (document.isPresent()) {
                final Optional<String> searchBearer = ZdfBearerFactory.parseIndexPage(document.get());
                searchBearer.ifPresent(zdfDto::setApi);
            } else {
                P2Alert.showErrorAlert("Filme suchen", "Es konnten keine Filme " +
                        "gefunden werden.");
                return;
            }
        } catch (final Exception ex) {
            P2Log.errorLog(874587458, ex, "Url: " + URL_BASE);
        }

        int count = 0;
        do {
            urlList.addAll(ZdfSearchFactory.getFilmList(zdfDto));
            ++count;
        } while (!zdfDto.zdfNextCursorProperty().get().isEmpty() && count < 12);

        search(zdfDto, urlList);

        LiveFactory.addToList(zdfDto.getFilmDataList());
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ZDF);
        P2Log.sysLog("Filme gefunden: " + zdfDto.getFilmDataList().size());
    }

    private void search(ZdfDto zdfDto, List<String> list) {
        for (int i = 0; i < list.size(); ++i) {
            if (zdfDto.isStop()) {
                break;
            }

            String url = list.get(i);
            LiveFactory.setProgress(LiveFactory.CHANNEL.ZDF, i, list.size());
            url = addApiUrlBase("content/documents/" + url + ".json");
            new ZdfFilmDetailDeserializer(zdfDto).deserialize(url);
        }
    }

//    public void loadLive(ZdfDto zdfDto, boolean next) {
//        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ZDF);
//
//        if (next) {
//            zdfDto.getList().clear();
//        } else {
//            zdfDto.init();
//            zdfDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_ZDF.getValue());
//
//            try {
//                final Optional<Document> document = LiveFactory.loadPage(URL_BASE);
//                if (document.isPresent()) {
//                    final Optional<String> searchBearer = ZdfBearerFactory.parseIndexPage(document.get());
//                    searchBearer.ifPresent(zdfDto::setApi);
//                } else {
//                    P2Alert.showErrorAlert("Filme suchen", "Es konnten keine Filme " +
//                            "gefunden werden.");
//                    return;
//                }
//            } catch (final Exception ex) {
//                P2Log.errorLog(874587458, ex, "Url: " + URL_BASE);
//            }
//        }
//
//        List<String> list = ZdfSearchFactory.getFilmList(zdfDto, next);
//        for (String url : list) {
//            url = addApiUrlBase("content/documents/" + url + ".json");
//            new ZdfFilmDetailDeserializer(zdfDto).deserialize(url);
//        }
//
//        LiveFactory.addToList(zdfDto.getList());
//        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ZDF);
//        P2Log.sysLog("Filme gefunden: " + zdfDto.getList().size());
//    }

    public void loadUrl(ZdfDto zdfDto) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ZDF);

        zdfDto.init();
        zdfDto.setStartUrl(ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ZDF.getValue());

        try {
            final Optional<Document> document = LiveFactory.loadPage(URL_BASE);
            if (document.isPresent()) {
                final Optional<String> searchBearer = ZdfBearerFactory.parseIndexPage(document.get());
                searchBearer.ifPresent(zdfDto::setApi);

                String url = zdfDto.getStartUrl();
                P2Log.sysLog("Filme suchen: " + url);
                workFilm(zdfDto, url);
            }
        } catch (final Exception ex) {
            P2Log.errorLog(898945124, ex, "Url: " + URL_BASE);
        }

        LiveFactory.addToList(zdfDto.getFilmDataList());
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ZDF);
        P2Log.sysLog("Filme gefunden: " + zdfDto.getFilmDataList().size());
    }

    private void workFilm(ZdfDto zdfDto, String url) {
        // jetzt jeden Film abarbeiten
        // https://www.zdf.de/play/magazine/volle-kanne-104  /volle-kanne-vom-14-maerz-2025-mit-michael-nast-100
        // https://api.zdf.de/content/documents              /volle-kanne-vom-14-maerz-2025-mit-michael-nast-100.json

        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        url = url.substring(url.lastIndexOf("/") + 1);
        url = addApiUrlBase("content/documents/" + url + ".json");
        new ZdfFilmDetailDeserializer(zdfDto).deserialize(url);
    }

    private String addApiUrlBase(String url) {
        if (!url.startsWith("/")) {
            return URL_API_BASE + "/" + url;
        } else {
            return URL_API_BASE + url;
        }
    }
}
