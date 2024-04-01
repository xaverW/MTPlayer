package de.p2tools.mtplayer.controller.livesearch;

import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.livesearch.tools.JsonFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.mtplayer.controller.livesearchard.ArdFilmDeserializer;
import de.p2tools.p2lib.tools.log.P2Log;

import java.util.Iterator;
import java.util.Optional;

public class LiveSearchArd {

    private static final String START_URL = "https://api.ardmediathek.de/page-gateway/pages/ard/item/";

    public LiveSearchArd() {
    }

    public void loadLive(JsonInfoDto jsonInfoDto, boolean next) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ARD);

        if (!next) {
            jsonInfoDto.init();
            jsonInfoDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_ARD.getValue());

        } else {
            // dann nur die Liste löschen
            jsonInfoDto.getList().clear();
        }

        load(jsonInfoDto, next);

        LiveFactory.addToList(jsonInfoDto);
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ARD);
        P2Log.sysLog("Filme gefunden: " + jsonInfoDto.getList().size());
    }

    public void loadUrl(JsonInfoDto jsonInfoDto) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ARD);

        jsonInfoDto.init();
        jsonInfoDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARD.getValue());
        try {
            String url = jsonInfoDto.getSearchString();
            P2Log.sysLog("Filme suchen: " + url);
            addFilmWithUrl(jsonInfoDto);
        } catch (final Exception ex) {
            P2Log.errorLog(898945124, ex, "Url: " + jsonInfoDto.getSearchString());
        }

        LiveFactory.addToList(jsonInfoDto);
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ARD);
        P2Log.sysLog("Filme gefunden: " + jsonInfoDto.getList().size());
    }

    private void load(JsonInfoDto jsonInfoDto, boolean next) {
        final String url;
        if (next) {
            url = jsonInfoDto.getNextUrl();
        } else {
            jsonInfoDto.setStartUrl("https://api.ardmediathek.de/search-system/mediathek/ard/search/vods?query=" +
                    jsonInfoDto.getSearchString() +
                    "&pageNumber=0" +
                    "&pageSize=" + JsonInfoDto.PAGE_SIZE +
                    "&audioDes=false&signLang=false&subtitle=false&childCont=false&sortingCriteria=SCORE_DESC&platform=MEDIA_THEK");
            url = jsonInfoDto.getStartUrl();
        }

        try {
            Optional<JsonNode> rootNode = JsonFactory.getRootNode(url);
            if (rootNode.isEmpty()) {
                return;
            }
            JsonNode jsonNode = rootNode.get();

            if (!next) {
                // beim ersten mal die Gesamtgröße suchen
                Optional<JsonNode> optionalJsonNode = JsonFactory.getOptElement(jsonNode, "pagination", "totalElements");
                optionalJsonNode.ifPresent(node -> jsonInfoDto.setSizeOverAll(node.asLong()));
            }
            hasMore(jsonInfoDto);

            if (jsonNode.get("teasers") != null) {
                int max = jsonNode.get("teasers").size();
                int no = 0;

                Iterator<JsonNode> children = jsonNode.get("teasers").elements();
                LiveFactory.setProgress(LiveFactory.CHANNEL.ARD, no, max);
                while (children.hasNext()) {
                    ++no;
                    String id = JsonFactory.getString(children.next(), "id");
                    addFilmWithId(jsonInfoDto, id);
                    LiveFactory.setProgress(LiveFactory.CHANNEL.ARD, no, max);
                }
            }
        } catch (final Exception ex) {
            P2Log.errorLog(979858978, ex, "Url: " + url);
        }
        P2Log.sysLog("Filme gefunden: " + jsonInfoDto.getList().size());
    }

    private void hasMore(JsonInfoDto jsonInfoDto) {
        long res = jsonInfoDto.getSizeOverAll() - (long) jsonInfoDto.getPageNo() * JsonInfoDto.PAGE_SIZE - JsonInfoDto.PAGE_SIZE;
        if (res > 0) {
            jsonInfoDto.setPageNo(jsonInfoDto.getPageNo() + 1);
            String url = "https://api.ardmediathek.de/search-system/mediathek/ard/search/vods?query=" +
                    jsonInfoDto.getSearchString() +
                    "&pageNumber=" + jsonInfoDto.getPageNo() +
                    "&pageSize=" + JsonInfoDto.PAGE_SIZE +
                    "&audioDes=false&signLang=false&subtitle=false&childCont=false&sortingCriteria=SCORE_DESC&platform=MEDIA_THEK";
            jsonInfoDto.setNextUrl(url);
        } else {
            jsonInfoDto.setNextUrl("");
        }
    }

    private void addFilmWithId(JsonInfoDto jsonInfoDto, String id) {
        final Optional<JsonNode> rootNode = JsonFactory.getRootNode(START_URL + id);
        jsonInfoDto.setArdFilmId(id);
        rootNode.ifPresent(jsonElement -> new ArdFilmDeserializer().deserialize(jsonInfoDto, jsonElement));
    }

    private void addFilmWithUrl(JsonInfoDto jsonInfoDto) {
        String url = jsonInfoDto.getSearchString().trim();
        String id = url.substring(url.lastIndexOf("/") + 1);
        addFilmWithId(jsonInfoDto, id);
    }
}
