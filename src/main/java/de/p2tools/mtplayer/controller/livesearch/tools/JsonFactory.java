package de.p2tools.mtplayer.controller.livesearch.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.p2tools.p2lib.mediathek.download.MtHttpClient;
import de.p2tools.p2lib.tools.log.P2Log;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;

public class JsonFactory {
    private JsonFactory() {
    }

    public static Optional<JsonNode> getRootNode(String url) {
        return getRootNode(url, "");
    }

    public static Optional<JsonNode> getRootNode(String url, String api) {
        try {
            final Request.Builder builder = new Request.Builder().url(url);
            if (!api.isEmpty()) {
                builder.addHeader("Api-Auth", api);
            }
            Response response = MtHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
            ResponseBody body = response.body();

            if (body != null && response.isSuccessful()) {
                InputStream input = body.byteStream();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(input);
                return Optional.of(rootNode);
            }
        } catch (Exception ex) {
            P2Log.errorLog(102589874, ex, "URL: " + url);
        }
        return Optional.empty();
    }

    public static Optional<String> getOptStringElement(JsonNode jsonNode, String element) {
        if (jsonNode.has(element)) {
            return Optional.of(jsonNode.get(element).asText());
        }
        return Optional.empty();
    }

    public static Optional<String> getOptStringElement(JsonNode jsonNode, String element1, String element2) {
        if (jsonNode.has(element1)) {
            if (jsonNode.get(element1).has(element2)) {
                return Optional.of(jsonNode.get(element1).get(element2).asText());
            }
        }
        return Optional.empty();
    }

    public static Optional<String> getOptStringElement(JsonNode jsonNode, String element1, String element2, String element3) {
        if (jsonNode.has(element1)) {
            if (jsonNode.get(element1).has(element2)) {
                if (jsonNode.get(element1).get(element2).has(element3)) {
                    return Optional.of(jsonNode.get(element1).get(element2).get(element3).asText());
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<JsonNode> getOptElement(JsonNode jsonNode, String element) {
        if (jsonNode.has(element)) {
            return Optional.of(jsonNode.get(element));
        }
        return Optional.empty();
    }

    public static Optional<JsonNode> getOptElement(JsonNode jsonNode, String element1, String element2) {
        if (jsonNode.has(element1)) {
            if (jsonNode.get(element1).has(element2)) {
                return Optional.of(jsonNode.get(element1).get(element2));
            }
        }
        return Optional.empty();
    }

    public static Optional<JsonNode> getOptElement(JsonNode jsonNode, String element1, String element2, String element3) {
        if (jsonNode.has(element1)) {
            if (jsonNode.get(element1).has(element2)) {
                if (jsonNode.get(element1).get(element2).has(element3)) {
                    return Optional.of(jsonNode.get(element1).get(element2).get(element3));
                }
            }
        }
        return Optional.empty();
    }

    public static String getString(JsonNode jsonNode, String element) {
        if (jsonNode.has(element)) {
            return jsonNode.get(element).asText();
        }
        return "";
    }

    public static Iterator<JsonNode> getIterator(JsonNode jsonNode, String element) {
        if (!jsonNode.has(element)) {
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public JsonNode next() {
                    return null;
                }
            };
        }

        Iterator<JsonNode> it = jsonNode.get(element).elements();
        return it;
    }
}
