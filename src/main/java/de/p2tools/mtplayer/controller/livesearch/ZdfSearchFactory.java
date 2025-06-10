package de.p2tools.mtplayer.controller.livesearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.p2tools.p2lib.mediathek.download.MLHttpClient;
import de.p2tools.p2lib.tools.log.P2Log;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ZdfSearchFactory {
    private ZdfSearchFactory() {
    }


    public static List<String> getFilmList(JsonInfoDto jsonInfoDto, boolean next) {
        // curl 'https://api.zdf.de/graphql?operationName=getSearchResults&variables=%7B"query":"traumschiff","first":24,
        // "after":null%7D&extensions=%7B"persistedQuery":%7B"version":1,
        // "sha256Hash":"7617f9fb0a7dd8ea236318372b9991fbda70a0068fc3dbb7e75d7605f7b91340"%7D%7D'

        // https://api.zdf.de/graphql?operationName=getSearchResults&variables=%7B"query":"traumschiff","first":24,"after":null%7D&extensions=%7B"persistedQuery":%7B"version":1,"sha256Hash":"7617f9fb0a7dd8ea236318372b9991fbda70a0068fc3dbb7e75d7605f7b91340"%7D%7D
        // -H 'Referer: https://www.zdf.de/'
        // -H 'content-type: application/json'
        // -H 'zdf-app-id: ffw-mt-web-05d9aa4f'
        // -H 'api-auth: Bearer aa3noh4ohz9eeboo8shiesheec9ciequ9Quah7el'
        // -H 'Origin: https://www.zdf.de'

        // jsonInfoDto.setApi("aa3noh4ohz9eeboo8shiesheec9ciequ9Quah7el");

        List<String> urlList = new ArrayList<>();
        String search = jsonInfoDto.getSearchString();
        String url1 = "https://api.zdf.de/graphql?operationName=getSearchResults&variables=%7B%22query%22%3A%22";
        String url2 = "%22%2C%22mode%22%3A%22ALL_RESULTS_EXCLUDING_TOP_RESULTS%22%2C%22filters%22%3A%7B%22contentOwner%22%3A%5B%5D%2C%22fsk%22%3A%5B%5D%2C%22language%22%3A%5B%5D%7D%2C%22first%22%3A24%2C%22after%22%3A";
        String url3 = "%7D&extensions=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22sha256Hash%22%3A%227617f9fb0a7dd8ea236318372b9991fbda70a0068fc3dbb7e75d7605f7b91340%22%7D%7D";

        String url;
        if (jsonInfoDto.getZdfNextCursor().isEmpty()) {
            url = url1 + search + url2 + "null" + url3;
        } else {
            url = url1 + search + url2 + "null" + url3;
            url = url1 + search + url2 + "%22" + jsonInfoDto.getZdfNextCursor() + "%22" + url3;
        }

        try {
            final Request.Builder builder = new Request.Builder().url(url);
            builder.addHeader("Referer", "https://www.zdf.de/");
            builder.addHeader("content-type", "application/json");
            builder.addHeader("zdf-app-id", "ffw-mt-web-05d9aa4f");
            // builder.addHeader("api-auth", "Bearer aa3noh4ohz9eeboo8shiesheec9ciequ9Quah7el");
            builder.addHeader("api-auth", "Bearer " + jsonInfoDto.getApi());
            builder.addHeader("Origin", "https://www.zdf.de");

            Response response = MLHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
            ResponseBody body = response.body();

            if (body != null && response.isSuccessful()) {
                InputStream input = body.byteStream();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(input);


                if (rootNode.has("data")) {
                    if (rootNode.get("data").has("searchDocuments")) {

                        if (rootNode.get("data").get("searchDocuments").has("pageInfo")) {
                            if (rootNode.get("data").get("searchDocuments").get("pageInfo").has("hasNextPage") &&
                                    rootNode.get("data").get("searchDocuments").get("pageInfo").has("endCursor")) {

                                JsonNode hasNext = rootNode.get("data").get("searchDocuments").get("pageInfo").get("hasNextPage");
                                boolean has = hasNext.asBoolean(); // dann gibts keine weiteren

                                JsonNode endCursor = rootNode.get("data").get("searchDocuments").get("pageInfo").get("endCursor");
                                String end = endCursor.asText();

                                if (has && !end.isEmpty()) {
                                    // ""startCursor":"MQ==","endCursor":"MjQ="
                                    jsonInfoDto.setZdfNextCursor(end);
                                } else {
                                    jsonInfoDto.setZdfNextCursor("");
                                }
                            }
                        }

                        if (rootNode.get("data").get("searchDocuments").has("results")) {
                            JsonNode results = rootNode.get("data").get("searchDocuments").get("results");
                            Iterator<JsonNode> itRes = results.iterator();
                            while (itRes.hasNext()) {
                                JsonNode i = itRes.next();
                                getRes(i, urlList);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            P2Log.errorLog(102589874, ex, "URL: " + url);
        }
        return urlList;
    }

    private static void getRes(JsonNode jsonNode, List<String> urlList) {
        if (jsonNode.has("item")) {
            if (jsonNode.get("item").has("canonical")) {
                JsonNode results = jsonNode.get("item").get("canonical");
                String url = results.asText();
                urlList.add(url);
            }
        }
    }
}
