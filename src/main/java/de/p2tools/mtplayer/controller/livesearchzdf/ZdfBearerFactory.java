package de.p2tools.mtplayer.controller.livesearchzdf;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;

public class ZdfBearerFactory {
    private ZdfBearerFactory() {
    }

    private static final String JSON_API_TOKEN = "apiToken";
    private static final String QUERY_SEARCH_BEARER = "head > script";

    private static String parseBearer(final String json) {
        String bearer = "";
        final int indexToken = json.indexOf(JSON_API_TOKEN);

        if (indexToken > 0) {
            final int indexStart = json.indexOf("'", indexToken + JSON_API_TOKEN.length() + 1) + 1;
            final int indexEnd = json.indexOf("'", indexStart);

            if (indexStart > 0) {
                bearer = json.substring(indexStart, indexEnd);
            }
        }

        return bearer;
    }

    public static Optional<String> parseIndexPage(final Document document) {
        final Elements scriptElements = document.select(QUERY_SEARCH_BEARER);

        for (final Element scriptElement : scriptElements) {
            final String script = scriptElement.html();
            final String value = parseBearer(script);
            if (!value.isEmpty()) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }
}
