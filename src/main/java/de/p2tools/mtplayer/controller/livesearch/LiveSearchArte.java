package de.p2tools.mtplayer.controller.livesearch;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.livesearch.arte.ArteDto;
import de.p2tools.mtplayer.controller.livesearch.arte.ArteSearchSitesFactory;
import de.p2tools.mtplayer.controller.livesearch.arte.ArteSiteDto;
import de.p2tools.mtplayer.controller.livesearch.arte.ArteSiteFactory;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
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
        for (int i = 0; i < arteDto.getSiteDtos().size(); ++i) {
            addFilmWithId(arteDto, arteDto.getSiteDtos().get(i));
            LiveFactory.setProgress(LiveFactory.CHANNEL.ARTE, i + 1, arteDto.getSiteDtos().size());
        }
        addFilm(arteDto);
    }

    public void loadUrl(ArteDto arteDto) {
        // https://www.arte.tv/de/videos/  125691-000-A  /outgrow-the-system-wirtschaft-neu-denken/
        String url = ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARTE.get().trim();
        P2Log.sysLog("Filme suchen: " + url);
        LiveFactory.setProgressWait(LiveFactory.CHANNEL.ARTE);
        arteDto.init();

        url = url.replace("https://www.arte.tv/de/videos/", "");
        String id = url.substring(0, url.indexOf("/"));
        ArteSiteDto arteSiteDto = new ArteSiteDto();
        arteSiteDto.setId(id);
        addFilmWithId(arteDto, arteSiteDto);

        addFilm(arteDto);
    }

    private void addFilmWithId(ArteDto arteDto, ArteSiteDto arteSiteDto) {
        arteDto.setSiteDto(arteSiteDto);
        ArteSiteFactory.addSite(arteDto);
    }

    private void addFilm(ArteDto arteDto) {
        arteDto.getFilmList().forEach(FilmData::init);
        LiveFactory.addToList(arteDto.getFilmList());
        LiveFactory.setProgressNull(LiveFactory.CHANNEL.ARTE);
        P2Log.sysLog("Filme gefunden: " + arteDto.getFilmList().size());
    }
}
