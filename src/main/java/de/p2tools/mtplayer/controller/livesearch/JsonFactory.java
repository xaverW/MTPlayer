package de.p2tools.mtplayer.controller.livesearch;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.Optional;

public class JsonFactory {
    private JsonFactory() {
    }

    public static Optional<String> getOptStringElement(JsonNode jsonNode, String element) {
        if (jsonNode.has(element)) {
            return Optional.of(jsonNode.get(element).asText());
        }
        return Optional.empty();
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
