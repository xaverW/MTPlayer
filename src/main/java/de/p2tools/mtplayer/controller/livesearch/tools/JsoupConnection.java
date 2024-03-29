package de.p2tools.mtplayer.controller.livesearch.tools;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;

/**
 * Helper Class to get rid of static method call for better testability
 */
public class JsoupConnection {

    public Connection getConnection(String url) {
        return Jsoup.connect(url);
    }

    public Document getDocument(String url) throws IOException {
        return getConnection(url).get();
    }

    public Document getDocumentTimeoutAfter(String url, int timeoutInMilliseconds) throws IOException {
        return getConnection(url).timeout(timeoutInMilliseconds).get();
    }

    public Document getDocumentTimeoutAfterAlternativeDocumentType(String url, int timeoutInMilliseconds, Parser parser) throws IOException {
        return getConnection(url).timeout(timeoutInMilliseconds).parser(parser).get();
    }

}
