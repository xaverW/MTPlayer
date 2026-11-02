package de.p2tools.mtplayer.controller.livesearch;

import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.livesearch.ard.ArdDto;
import de.p2tools.mtplayer.controller.livesearch.arte.ArteDto;
import de.p2tools.mtplayer.controller.livesearch.arte.ArteSiteFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.JsonFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.p2lib.tools.log.P2Log;

import java.util.Iterator;
import java.util.Optional;

public class LiveSearchArte {

    public LiveSearchArte() {
    }

    public void loadLive(ArteDto arteDto) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ARTE);
        arteDto.init();
        arteDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_ARTE.getValue());

        load(arteDto);

        LiveFactory.addToList(arteDto.getFilmList());
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ARTE);
        P2Log.sysLog("Filme gefunden: " + arteDto.getFilmList().size());
    }

    public void loadUrl(ArteDto arteDto) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ARTE);

        arteDto.init();
        arteDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARTE.getValue());
        try {
            String url = arteDto.getSearchString();
            P2Log.sysLog("Filme suchen: " + url);
            addFilmWithUrl(arteDto);
        } catch (final Exception ex) {
            P2Log.errorLog(898945124, ex, "Url: " + arteDto.getSearchString());
        }

        LiveFactory.addToList(arteDto.getFilmList());
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ARTE);
        P2Log.sysLog("Filme gefunden: " + arteDto.getFilmList().size());
    }

    private void load(ArteDto arteDto) {
        final String url;
        url = arteDto.getSearchUrl();

        try {
            Optional<JsonNode> rootNode = JsonFactory.getRootNode(url);
            if (rootNode.isEmpty()) {
                return;
            }
            JsonNode jsonNode = rootNode.get();

            if (jsonNode.get("zones") != null) {
                Iterator<JsonNode> children = jsonNode.get("zones").elements();
                while (children.hasNext()) {
                    JsonNode ch = children.next();

                    // FilmUrls suchen
                    Iterator<JsonNode> u = JsonFactory.getIterator(ch, new String[]{"content", "data"});
                    while (u.hasNext()) {
                        JsonNode filmUrl = u.next();
                        Optional<JsonNode> siteId = JsonFactory.getOptElement(filmUrl, "id");
                        siteId.ifPresent(n -> {
                            String id = n.asText();
                            if (id.contains("_")) {
                                id = id.substring(0, id.indexOf("_"));
                            }
                            arteDto.getIdList().add(id);
                        });
                    }
                }
                LiveFactory.setProgress(LiveFactory.CHANNEL.ARTE, 0, arteDto.getIdList().size());
            }
        } catch (final Exception ex) {
            P2Log.errorLog(546212147, ex, "Url: " + url);
        }

        // und jetzt die Seiten laden
        for (int i = 0; i < arteDto.getIdList().size(); ++i) {
            addFilmWithId(arteDto, arteDto.getIdList().get(i));
            LiveFactory.setProgress(LiveFactory.CHANNEL.ARTE, i + 1, arteDto.getIdList().size());
        }
    }

    private void hasMore(ArteDto arteDto) {
        long res = arteDto.getTotalElements() - (long) arteDto.getActPage() * ArdDto.PAGE_SIZE - ArdDto.PAGE_SIZE;
        if (res > 0) {
            arteDto.addNextUrl();
        }
    }

    private void addFilmWithId(ArteDto ardDto, String id) {
        ardDto.setPageId(id);
        ArteSiteFactory.addSite(ardDto);
    }

    private void addFilmWithUrl(ArteDto ardDto) {
        // https://www.arte.tv/de/videos/  125691-000-A  /outgrow-the-system-wirtschaft-neu-denken/
        String url = ardDto.getSearchUrl().trim();
        url = url.replace("https://www.arte.tv/de/videos/", "");
        String id = url.substring(0, url.indexOf("/"));
        addFilmWithId(ardDto, id);
    }
}
