package de.p2tools.mtplayer.controller.livesearch.arte;

public class ArteSearchFactory {
    private ArteSearchFactory() {
    }

    public static String getSearchUrl(String search) {
        return ArteDto.SEARCH_URL.replace(ArteDto.SEARCH, search);
    }

    public static String getPageUrl(String id) {
        return ArteDto.PAGE_URL.replace(ArteDto.ID, id);
    }
}
