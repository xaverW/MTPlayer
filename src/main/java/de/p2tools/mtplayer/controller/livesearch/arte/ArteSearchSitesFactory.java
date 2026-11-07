package de.p2tools.mtplayer.controller.livesearch.arte;

import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.livesearch.tools.JsonFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.p2lib.tools.log.P2Log;

import java.util.Iterator;
import java.util.Optional;

public class ArteSearchSitesFactory {
    private ArteSearchSitesFactory() {
    }

    public static void search1(ArteDto arteDto) {
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
    }

    public static void search2(ArteDto arteDto) {
        String url = "https://api.arte.tv/api/emac/v4/de/web/zones/f75e7b54-a37b-4f8f-b01b-dbefa877c041/content?page=1&query=" +
                arteDto.getSearchString();
//        String url = "https://api.arte.tv/api/emac/v4/de/web/zones/89658033-61df-430e-a825-3eec69311209/content?page=1&query=" +
//                arteDto.getSearchString();

        try {
            do {
                Optional<JsonNode> rootNode = JsonFactory.getRootNode(url);
                if (rootNode.isEmpty()) {
                    return;
                }
                JsonNode jsonNode = rootNode.get();

                searchSites(arteDto, jsonNode);

                // dann die nächste suchen
                Optional<JsonNode> jsonNext = JsonFactory.getOptElement(jsonNode, new String[]{"pagination", "links", "next"});
                if (jsonNext.isPresent()) {
                    url = jsonNext.get().asText();
                }
            } while (!url.isEmpty() && !url.equals("null"));


            LiveFactory.setProgress(LiveFactory.CHANNEL.ARTE, 0, arteDto.getIdList().size());
        } catch (final Exception ex) {
            P2Log.errorLog(546212147, ex, "Url: " + url);
        }
    }

    public static void searchSites(ArteDto arteDto, JsonNode jsonNode) {
        try {
            Iterator<JsonNode> children = JsonFactory.getIterator(jsonNode, "data");
            while (children.hasNext()) {
                JsonNode ch = children.next();

                // FilmUrls suchen
                Optional<JsonNode> siteId = JsonFactory.getOptElement(ch, "id");
                siteId.ifPresent(n -> {
                    String id = n.asText();
                    if (id.contains("_")) {
                        id = id.substring(0, id.indexOf("_"));
                    }
                    arteDto.getIdList().add(id);
                });
            }
        } catch (final Exception ex) {
            P2Log.errorLog(632652147, ex);
        }
    }
}
