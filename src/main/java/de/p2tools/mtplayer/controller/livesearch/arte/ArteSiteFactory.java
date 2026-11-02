package de.p2tools.mtplayer.controller.livesearch.arte;

import com.fasterxml.jackson.databind.JsonNode;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.livesearch.tools.JsonFactory;
import de.p2tools.p2lib.tools.log.P2Log;

import java.util.Iterator;
import java.util.Optional;

public class ArteSiteFactory {
    private ArteSiteFactory() {
    }

    public static void addSite(ArteDto arteDto) {
        // config	"https://api.arte.tv/api/player/v2/config/de/125691-000-A"
        // playlist	"https://api.arte.tv/api/player/v2/playlist/de/125691-000-A"

        String urlConfig = "https://api.arte.tv/api/player/v2/config/de/" + arteDto.getPageId();
        String urlPlaylist = "https://api.arte.tv/api/player/v2/playlist/de/" + arteDto.getPageId();
        FilmDataMTP filmDataMTP = new FilmDataMTP();
        getConfig(arteDto, urlConfig, filmDataMTP);
        getPlaylist(arteDto, urlPlaylist, filmDataMTP);
//        if (!filmDataMTP.getUrl().isEmpty()) {
        arteDto.getFilmList().add(filmDataMTP);
//        }
    }

    private static void getConfig(ArteDto arteDto, String url, FilmDataMTP filmDataMTP) {
        try {
            Optional<JsonNode> root = JsonFactory.getRootNode(url);
            if (root.isEmpty()) {
                return;
            }
            JsonNode nodeRoot = root.get();

            Optional<JsonNode> node = JsonFactory.getOptElement(nodeRoot, new String[]{"data", "attributes", "metadata", "link", "url"});
            node.ifPresent(n -> filmDataMTP.arr[FilmDataMTP.FILM_WEBSITE] = n.asText());

            node = JsonFactory.getOptElement(nodeRoot, new String[]{"data", "attributes", "metadata", "description"});
            node.ifPresent(n -> filmDataMTP.arr[FilmDataMTP.FILM_DESCRIPTION] = n.asText());

            node = JsonFactory.getOptElement(nodeRoot, new String[]{"data", "attributes", "metadata", "title"});
            node.ifPresent(n -> filmDataMTP.arr[FilmDataMTP.FILM_TITLE] = n.asText());

            Iterator<JsonNode> it = JsonFactory.getIterator(nodeRoot, new String[]{"data", "attributes", "streams"});
            while (it.hasNext()) {
                node = JsonFactory.getOptElement(it.next(), new String[]{"url"});
                node.ifPresent(n -> filmDataMTP.arr[FilmDataMTP.FILM_URL] = n.asText());
            }
        } catch (final Exception ex) {
            P2Log.errorLog(546212147, ex, "Url: " + url);
        }
    }

    private static void getPlaylist(ArteDto arteDto, String url, FilmDataMTP filmDataMTP) {

    }
}
