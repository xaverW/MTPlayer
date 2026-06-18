package de.p2tools.mtplayer.controller.livesearch.ard;

public class ArdSearchFactory {
    private ArdSearchFactory() {
    }

    public static String getSearchUrl(String search) {
        return ArdDto.SEARCH_URL.replace(ArdDto.SEARCH, search);
    }

    public static String getNextSearchUrl(String search, int page) {
        return ArdDto.SEARCH_URL.replace(ArdDto.SEARCH, search).replace(ArdDto.PAGE_NO, page + "");
    }

    public static String getPageUrl(String id) {
        return ArdDto.PAGE_URL.replace(ArdDto.ID, id);
    }
}
