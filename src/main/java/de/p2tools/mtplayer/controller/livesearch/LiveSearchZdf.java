package de.p2tools.mtplayer.controller.livesearch;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.mtplayer.controller.livesearchzdf.ZdfFilmDetailDeserializer;
import de.p2tools.p2lib.tools.log.P2Log;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LiveSearchZdf {
    private static final String ATTRIBUTE_HREF = "href";
    private static final String URL_BASE = "https://www.zdf.de";
    private static final String URL_API_BASE = "https://api.zdf.de";

    public LiveSearchZdf() {
    }

    public void loadLive(JsonInfoDto jsonInfoDto, boolean next) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ZDF);

        if (!next) {
            jsonInfoDto.init();
            jsonInfoDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_ZDF.getValue());
        } else {
            jsonInfoDto.getList().clear();
        }

        try {
            if (!next) {
                final Optional<Document> document = LiveFactory.loadPage(URL_BASE);
                if (document.isPresent()) {
                    final Optional<String> searchBearer = ZdfBearerFactory.parseIndexPage(document.get());
                    searchBearer.ifPresent(jsonInfoDto::setApi);
                    loadUrls(jsonInfoDto, false);
                }
            } else {
                loadUrls(jsonInfoDto, true);
            }
        } catch (final Exception ex) {
            P2Log.errorLog(898945124, ex, "Url: " + URL_BASE);
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

    private void loadUrls(JsonInfoDto jsonInfoDto, boolean next) {
        int max;
        List<String> urls = getFilms(jsonInfoDto, next);
        int count = 0;
        max = urls.size();
        P2Log.sysLog("Filme suchen: " + max);
        if (!urls.isEmpty()) {
            LiveFactory.setProgress(LiveFactory.CHANNEL.ZDF, count, max);
            for (String url : urls) {
                workFilm(jsonInfoDto, url);
                LiveFactory.setProgress(LiveFactory.CHANNEL.ZDF, ++count, max);
            }
        }
    }

    private List<String> getFilms(JsonInfoDto jsonInfoDto, boolean next) {
        final String searchUrl;
        if (next) {
            searchUrl = jsonInfoDto.getNextUrl();
        } else {
            searchUrl = "https://www.zdf.de/suche?q=" + jsonInfoDto.getSearchString() +
                    "&abName=ab-2024-03-25&abGroup=gruppe-b";
        }

        List<String> urlList = new ArrayList<>();
        if (searchUrl.isEmpty()) {
            return urlList;
        }

        final Optional<Document> document = LiveFactory.loadPage(searchUrl);
        if (document.isPresent()) {
            final Elements elements = document.get().select("div.box, div.m-tags");
            for (final Element element : elements) {
                Elements urlElement = element.select("a");
                String url = addUrlBase(urlElement.attr(ATTRIBUTE_HREF));
                urlList.add(url);
            }
            isMore(jsonInfoDto, document.get());
        }

        return urlList;
    }

    private void isMore(JsonInfoDto jsonInfoDto, Document document) {
        final Element element = document.selectFirst("div.load-more-container");
        if (element != null) {
            Elements urlElement = element.select("a");
            jsonInfoDto.setNextUrl(addUrlBase(urlElement.attr(ATTRIBUTE_HREF)));

        } else {
            jsonInfoDto.setNextUrl("");
        }
    }

    private void workFilm(JsonInfoDto jsonInfoDto, String url) {
        // jetzt jeden Film abarbeiten
        final Optional<Document> document = LiveFactory.loadPage(url);
        if (document.isEmpty()) {
            System.out.println("document.isEmpty(): error");
            return;
        }

        final Elements elements = document.get().select("div.b-playerbox.b-ratiobox.js-rb-live");
        if (elements.isEmpty()) {
            System.out.println("elements.isEmpty(): error");
            return;
        }

        Element element = elements.first();
        if (element == null) {
            System.out.println("elements.first()==null: error");
            return;
        }

        String urlElement = element.attr("data-zdfplayer-id");
        if (urlElement.isEmpty()) {
            System.out.println("urlElement.isEmpty(): error");
            return;
        }

        String filmUrl = addApiUrlBase("content/documents/" + urlElement + ".json");
        new ZdfFilmDetailDeserializer().deserialize(jsonInfoDto, filmUrl);
    }

    private String addUrlBase(String url) {
        if (!url.startsWith("/")) {
            return URL_BASE + "/" + url;
        } else {
            return URL_BASE + url;
        }
    }

    private String addApiUrlBase(String url) {
        if (!url.startsWith("/")) {
            return URL_API_BASE + "/" + url;
        } else {
            return URL_API_BASE + url;
        }
    }
}
