package de.p2tools.mtplayer.controller.livesearchard;

import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.livesearch.tools.JsonFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;

import java.util.Iterator;
import java.util.Optional;

public class ArdSearchWorker {
    public ArdSearchWorker() {
    }

    public void loadLive(JsonInfoDtoArd jsonInfoDtoArd) {
        jsonInfoDtoArd.setStartUrl("https://api.ardmediathek.de/search-system/mediathek/ard/search/vods?query=" +
                jsonInfoDtoArd.getSearchString() +
                "&pageNumber=" + jsonInfoDtoArd.getPageNo() +
                "&pageSize=" + JsonInfoDtoArd.PAGE_SIZE +
                "&audioDes=false&signLang=false&subtitle=false&childCont=false&sortingCriteria=SCORE_DESC&platform=MEDIA_THEK");

        int max = 0;
        try {
            Optional<JsonNode> rootNode = JsonFactory.getRootNode(jsonInfoDtoArd.getStartUrl());
            if (rootNode.isEmpty()) {
                return;
            }
            JsonNode jsonNode = rootNode.get();

            if (jsonNode.get("pagination") != null) {
                if (jsonNode.get("pagination").get("totalElements") != null) {
                    long soa = jsonNode.get("pagination").get("totalElements").asLong();
                    jsonInfoDtoArd.setSizeOverAll(soa);
                }
            }

            if (jsonNode.get("teasers") != null) {
                max = jsonNode.get("teasers").size();
                int no = -1;

                Iterator<JsonNode> children = jsonNode.get("teasers").elements();
                while (children.hasNext()) {
                    jsonInfoDtoArd.setHitNo(++no);
                    String id = JsonFactory.getString(children.next(), "id");
                    new ArdFilmDeserializer().addFilmId(jsonInfoDtoArd, id);
                    LiveFactory.setProgress(LiveFactory.CHANNEL.ARD, no, max);
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(979858978, ex, "Url: " + jsonInfoDtoArd.getStartUrl());
        }
        Platform.runLater(() -> LiveFactory.progressPropertyARD.setValue(LiveFactory.PROGRESS_NULL));
        PLog.sysLog("Filme gefunden: " + jsonInfoDtoArd.getList().size());
    }
}
