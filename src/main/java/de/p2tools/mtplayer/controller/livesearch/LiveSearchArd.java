package de.p2tools.mtplayer.controller.livesearch;

import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.livesearch.ard.ArdDto;
import de.p2tools.mtplayer.controller.livesearch.ard.ArdFilmDeserializer;
import de.p2tools.mtplayer.controller.livesearch.ard.ArdSearchFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.JsonFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.p2lib.tools.log.P2Log;

import java.util.Iterator;
import java.util.Optional;

public class LiveSearchArd {

    public LiveSearchArd() {
    }

    public void loadLive(ArdDto ardDto, boolean next) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ARD);

        if (!next) {
            ardDto.init();
            ardDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_ARD.getValue());

        } else {
            // dann nur die Liste löschen
            ardDto.getFilmList().clear();
        }

        load(ardDto, next);

        LiveFactory.addToList(ardDto.getFilmList());
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ARD);
        P2Log.sysLog("Filme gefunden: " + ardDto.getFilmList().size());
    }

    public void loadUrl(ArdDto ardDto) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ARD);

        ardDto.init();
        ardDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARD.getValue());
        try {
            String url = ardDto.getSearchString();
            P2Log.sysLog("Filme suchen: " + url);
            addFilmWithUrl(ardDto);
        } catch (final Exception ex) {
            P2Log.errorLog(898945124, ex, "Url: " + ardDto.getSearchString());
        }

        LiveFactory.addToList(ardDto.getFilmList());
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ARD);
        P2Log.sysLog("Filme gefunden: " + ardDto.getFilmList().size());
    }

    private void load(ArdDto ardDto, boolean next) {
        final String url;
        if (next) {
            url = ardDto.getNextUrl();
        } else {
            url = ardDto.getSearchUrl();
        }

        try {
            Optional<JsonNode> rootNode = JsonFactory.getRootNode(url);
            if (rootNode.isEmpty()) {
                return;
            }
            JsonNode jsonNode = rootNode.get();

            if (!next) {
                // beim ersten Mal die Gesamtgröße suchen
                Optional<JsonNode> optionalJsonNode = JsonFactory.getOptElement(jsonNode, "pagination", "totalElements");
                optionalJsonNode.ifPresent(node -> ardDto.setTotalElements(node.asLong()));
            }
            hasMore(ardDto);

            if (jsonNode.get("teasers") != null) {
                int max = jsonNode.get("teasers").size();
                int no = 0;

                Iterator<JsonNode> children = jsonNode.get("teasers").elements();
                LiveFactory.setProgress(LiveFactory.CHANNEL.ARD, no, max);
                while (children.hasNext()) {
                    ++no;
                    String id = JsonFactory.getString(children.next(), "id");
                    addFilmWithId(ardDto, id);
                    LiveFactory.setProgress(LiveFactory.CHANNEL.ARD, no, max);
                }
            }
        } catch (final Exception ex) {
            P2Log.errorLog(979858978, ex, "Url: " + url);
        }
        P2Log.sysLog("Filme gefunden: " + ardDto.getFilmList().size());
    }

    private void hasMore(ArdDto ardDto) {
        long res = ardDto.getTotalElements() - (long) ardDto.getActPage() * ArdDto.PAGE_SIZE - ArdDto.PAGE_SIZE;
        if (res > 0) {
            ardDto.addNextUrl();
        }
    }

    private void addFilmWithId(ArdDto ardDto, String id) {
        ardDto.setPageId(id);
        final Optional<JsonNode> rootNode = JsonFactory.getRootNode(ArdSearchFactory.getPageUrl(id));
        rootNode.ifPresent(jsonElement -> new ArdFilmDeserializer().deserialize(ardDto, jsonElement));
    }

    private void addFilmWithUrl(ArdDto ardDto) {
        String url = ardDto.getSearchString().trim();
        // https://www.ardmediathek.de/video/Y3JpZDovL2JyLmRlL2Jyb2FkY2FzdC9GMjAyNVdPMDIyMjgwQTA
        String id = url.substring(url.lastIndexOf("/") + 1);
        addFilmWithId(ardDto, id);
    }
}
