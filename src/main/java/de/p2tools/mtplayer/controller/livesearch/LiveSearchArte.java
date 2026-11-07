package de.p2tools.mtplayer.controller.livesearch;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.livesearch.ard.ArdDto;
import de.p2tools.mtplayer.controller.livesearch.arte.ArteDto;
import de.p2tools.mtplayer.controller.livesearch.arte.ArteSearchSitesFactory;
import de.p2tools.mtplayer.controller.livesearch.arte.ArteSiteFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.p2lib.tools.log.P2Log;

public class LiveSearchArte {

    public LiveSearchArte() {
    }

    public void loadLive(ArteDto arteDto) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ARTE);
        arteDto.init();
        arteDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_ARTE.getValue());

        ArteSearchSitesFactory.search1(arteDto);
        ArteSearchSitesFactory.search2(arteDto);

        // und jetzt die Seiten laden
        for (int i = 0; i < arteDto.getIdList().size(); ++i) {
            addFilmWithId(arteDto, arteDto.getIdList().get(i));
            LiveFactory.setProgress(LiveFactory.CHANNEL.ARTE, i + 1, arteDto.getIdList().size());
        }

        LiveFactory.addToList(arteDto.getFilmList());
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ARTE);
        P2Log.sysLog("Filme gefunden: " + arteDto.getFilmList().size());
    }

    public void loadUrl(ArteDto arteDto) {
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ARTE);

        arteDto.init();
        arteDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARTE.getValue());
        try {
            String url = arteDto.getSearchString();
            P2Log.sysLog("Filme suchen: " + url);
            addFilmWithUrl(arteDto);
        } catch (final Exception ex) {
            P2Log.errorLog(898945124, ex, "Url: " + arteDto.getSearchString());
        }

        LiveFactory.addToList(arteDto.getFilmList());
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ARTE);
        P2Log.sysLog("Filme gefunden: " + arteDto.getFilmList().size());
    }


    private void hasMore(ArteDto arteDto) {
        long res = arteDto.getTotalElements() - (long) arteDto.getActPage() * ArdDto.PAGE_SIZE - ArdDto.PAGE_SIZE;
        if (res > 0) {
            arteDto.addNextUrl();
        }
    }

    private void addFilmWithId(ArteDto ardDto, String id) {
        ardDto.setPageId(id);
        ArteSiteFactory.addSite(ardDto);
    }

    private void addFilmWithUrl(ArteDto ardDto) {
        // https://www.arte.tv/de/videos/  125691-000-A  /outgrow-the-system-wirtschaft-neu-denken/
        String url = ardDto.getSearchUrl().trim();
        url = url.replace("https://www.arte.tv/de/videos/", "");
        String id = url.substring(0, url.indexOf("/"));
        addFilmWithId(ardDto, id);
    }
}
