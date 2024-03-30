package de.p2tools.mtplayer.controller.livesearch;

import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.livesearch.tools.JsonFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.mtplayer.controller.livesearchard.ArdFilmDeserializer;
import de.p2tools.p2lib.tools.log.PLog;

import java.util.Iterator;
import java.util.Optional;

public class LiveSearchArd {

    private static final String START_URL = "https://api.ardmediathek.de/page-gateway/pages/ard/item/";

    public LiveSearchArd() {
    }

    public void loadLive(JsonInfoDto jsonInfoDto) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ARD);
        load(jsonInfoDto);
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ARD);
        PLog.sysLog("Filme gefunden: " + jsonInfoDto.getList().size());
    }

    public void loadUrl(JsonInfoDto jsonInfoDto) {
        try {
            LiveFactory.setProgressWait(LiveFactory.CHANNEL.ARD);
            String url = jsonInfoDto.getSearchString();
            PLog.sysLog("Filme suchen: " + url);
            addFilmUrl(jsonInfoDto);
        } catch (final Exception ex) {
            PLog.errorLog(898945124, ex, "Url: " + jsonInfoDto.getSearchString());
        }

        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ARD);
        PLog.sysLog("Filme gefunden: " + jsonInfoDto.getList().size());
    }

    private void load(JsonInfoDto jsonInfoDto) {
        jsonInfoDto.setStartUrl("https://api.ardmediathek.de/search-system/mediathek/ard/search/vods?query=" +
                jsonInfoDto.getSearchString() +
                "&pageNumber=" + jsonInfoDto.getPageNo() +
                "&pageSize=" + JsonInfoDto.PAGE_SIZE +
                "&audioDes=false&signLang=false&subtitle=false&childCont=false&sortingCriteria=SCORE_DESC&platform=MEDIA_THEK");

        int max = 0;
        try {
            Optional<JsonNode> rootNode = JsonFactory.getRootNode(jsonInfoDto.getStartUrl());
            if (rootNode.isEmpty()) {
                return;
            }
            JsonNode jsonNode = rootNode.get();

            Optional<JsonNode> optionalJsonNode = JsonFactory.getOptElement(jsonNode, "pagination", "totalElements");
            optionalJsonNode.ifPresent(node -> jsonInfoDto.setSizeOverAll(node.asLong()));

            if (jsonNode.get("teasers") != null) {
                max = jsonNode.get("teasers").size();
                int no = 0;

                Iterator<JsonNode> children = jsonNode.get("teasers").elements();
                LiveFactory.setProgress(LiveFactory.CHANNEL.ARD, no, max);
                while (children.hasNext()) {
                    ++no;
                    String id = JsonFactory.getString(children.next(), "id");
                    addFilmId(jsonInfoDto, id);
                    LiveFactory.setProgress(LiveFactory.CHANNEL.ARD, no, max);
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(979858978, ex, "Url: " + jsonInfoDto.getStartUrl());
        }
        PLog.sysLog("Filme gefunden: " + jsonInfoDto.getList().size());
    }

    private void addFilmId(JsonInfoDto jsonInfoDto, String id) {
        final Optional<JsonNode> rootNode = JsonFactory.getRootNode(START_URL + id);
        rootNode.ifPresent(jsonElement -> new ArdFilmDeserializer().deserialize(jsonInfoDto, jsonElement));
    }

    private void addFilmUrl(JsonInfoDto jsonInfoDto) {
        String url = jsonInfoDto.getSearchString().trim();
        String id = url.substring(url.lastIndexOf("/") + 1);
        addFilmId(jsonInfoDto, id);
    }
}
