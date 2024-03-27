package de.p2tools.mtplayer.controller.livesearchzdf;

import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.livesearch.LiveFactory;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Optional;

public class LiveSearchZdf {

    public LiveSearchZdf() {
    }

    public List<FilmDataMTP> loadLive(JsonInfoDtoZdf jsonInfoDtoZdf) {
        int max;
        try {
            final Optional<Document> document = LiveFactory.loadPage(ZDFFactory.URL_BASE);
            if (document.isPresent()) {
                final Optional<String> searchBearer = ZdfBearerFactory.parseIndexPage(document.get());
                searchBearer.ifPresent(jsonInfoDtoZdf::setApi);

                List<String> urls = ZDFFactory.getFilms(jsonInfoDtoZdf);
                int count = 0;
                max = urls.size();
                PLog.sysLog("Filme suchen: " + max);
                if (!urls.isEmpty()) {
                    LiveFactory.setProgress(LiveFactory.CHANNEL.ZDF, 0, max);
                    for (String url : urls) {
                        ZDFFactory.workFilm(jsonInfoDtoZdf, url);
                        LiveFactory.setProgress(LiveFactory.CHANNEL.ZDF, ++count, max);
                    }
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(898945124, ex, "Url: " + ZDFFactory.URL_BASE);
        }

        Platform.runLater(() -> LiveFactory.progressPropertyZDF.setValue(LiveFactory.PROGRESS_NULL));
        PLog.sysLog("Filme gefunden: " + jsonInfoDtoZdf.getList().size());
        return jsonInfoDtoZdf.getList();
    }

    public List<FilmDataMTP> loadUrl(JsonInfoDtoZdf jsonInfoDtoZdf) {
        try {
            final Optional<Document> document = LiveFactory.loadPage(ZDFFactory.URL_BASE);
            if (document.isPresent()) {
                final Optional<String> searchBearer = ZdfBearerFactory.parseIndexPage(document.get());
                searchBearer.ifPresent(jsonInfoDtoZdf::setApi);

                String url = jsonInfoDtoZdf.getSearchString();
                PLog.sysLog("Filme suchen: " + url);
                LiveFactory.setProgress(LiveFactory.CHANNEL.ZDF, -0.5, 1);
                ZDFFactory.workFilm(jsonInfoDtoZdf, url);
            }
        } catch (final Exception ex) {
            PLog.errorLog(898945124, ex, "Url: " + ZDFFactory.URL_BASE);
        }

        Platform.runLater(() -> LiveFactory.progressPropertyZDF.setValue(LiveFactory.PROGRESS_NULL));
        PLog.sysLog("Filme gefunden: " + jsonInfoDtoZdf.getList().size());
        return jsonInfoDtoZdf.getList();
    }
}
