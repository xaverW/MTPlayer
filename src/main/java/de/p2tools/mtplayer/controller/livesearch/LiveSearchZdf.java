package de.p2tools.mtplayer.controller.livesearch;

import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.mtplayer.controller.livesearchzdf.ZdfFilmDetailDeserializer;
import de.p2tools.p2lib.tools.log.PLog;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LiveSearchZdf {
    private static final String ATTRIBUTE_HREF = "href";
    public static final String URL_BASE = "https://www.zdf.de";
    public static final String URL_API_BASE = "https://api.zdf.de";

    public LiveSearchZdf() {
    }

    public List<FilmDataMTP> loadLive(JsonInfoDto jsonInfoDto) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ZDF);
        int max;
        try {
            final Optional<Document> document = LiveFactory.loadPage(URL_BASE);
            if (document.isPresent()) {
                final Optional<String> searchBearer = ZdfBearerFactory.parseIndexPage(document.get());
                searchBearer.ifPresent(jsonInfoDto::setApi);

                List<String> urls = getFilms(jsonInfoDto);
                int count = 0;
                max = urls.size();
                PLog.sysLog("Filme suchen: " + max);
                if (!urls.isEmpty()) {
                    LiveFactory.setProgress(LiveFactory.CHANNEL.ZDF, count, max);
                    for (String url : urls) {
                        workFilm(jsonInfoDto, url);
                        LiveFactory.setProgress(LiveFactory.CHANNEL.ZDF, ++count, max);
                    }
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(898945124, ex, "Url: " + URL_BASE);
        }

        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ZDF);
        PLog.sysLog("Filme gefunden: " + jsonInfoDto.getList().size());
        return jsonInfoDto.getList();
    }

    public List<FilmDataMTP> loadUrl(JsonInfoDto jsonInfoDto) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ZDF);
        try {
            final Optional<Document> document = LiveFactory.loadPage(URL_BASE);
            if (document.isPresent()) {
                final Optional<String> searchBearer = ZdfBearerFactory.parseIndexPage(document.get());
                searchBearer.ifPresent(jsonInfoDto::setApi);

                String url = jsonInfoDto.getStartUrl();
                PLog.sysLog("Filme suchen: " + url);
                workFilm(jsonInfoDto, url);
            }
        } catch (final Exception ex) {
            PLog.errorLog(898945124, ex, "Url: " + URL_BASE);
        }

        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ZDF);
        PLog.sysLog("Filme gefunden: " + jsonInfoDto.getList().size());
        return jsonInfoDto.getList();
    }

    private List<String> getFilms(JsonInfoDto jsonInfoDto) {
        String searchUrl = "https://www.zdf.de/suche?q=" + jsonInfoDto.getSearchString() +
                "&abName=ab-2024-03-25&abGroup=gruppe-b";

        List<String> urlList = new ArrayList<>();
        final Optional<Document> document = LiveFactory.loadPage(searchUrl);
        if (document.isPresent()) {
            final Elements elements = document.get().select("div.box, div.m-tags");
            for (final Element element : elements) {
                Elements urlElement = element.select("a");
                String url = addUrlBase(urlElement.attr(ATTRIBUTE_HREF));
                urlList.add(url);
            }
        }

        return urlList;
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
