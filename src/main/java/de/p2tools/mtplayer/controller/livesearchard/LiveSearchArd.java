package de.p2tools.mtplayer.controller.livesearchard;

import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.mtplayer.controller.livesearchzdf.ZDFFactory;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;

public class LiveSearchArd {

    public LiveSearchArd() {
    }

    public void loadLive(JsonInfoDtoArd jsonInfoDtoArd) {
        new ArdSearchWorker().loadLive(jsonInfoDtoArd);
        Platform.runLater(() -> LiveFactory.progressPropertyZDF.setValue(LiveFactory.PROGRESS_NULL));
        PLog.sysLog("Filme gefunden: " + jsonInfoDtoArd.getList().size());
    }

    public void loadUrl(JsonInfoDtoArd jsonInfoDtoArd) {
        try {
            String url = jsonInfoDtoArd.getSearchString();
            PLog.sysLog("Filme suchen: " + url);
            LiveFactory.setProgress(LiveFactory.CHANNEL.ARD, -0.5, 1);
            new ArdFilmDeserializer().addFilmUrl(jsonInfoDtoArd);
        } catch (final Exception ex) {
            PLog.errorLog(898945124, ex, "Url: " + ZDFFactory.URL_BASE);
        }

        Platform.runLater(() -> LiveFactory.progressPropertyARD.setValue(LiveFactory.PROGRESS_NULL));
        PLog.sysLog("Filme gefunden: " + jsonInfoDtoArd.getList().size());
    }
}
