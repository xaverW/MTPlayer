package de.p2tools.mtplayer.controller.filter;

import de.p2tools.mtplayer.controller.config.ProgData;

public class FilterDto {
    public FilterDto(boolean audio) {
        this.filterWorker = audio ? ProgData.getInstance().filterWorkerAudio : ProgData.getInstance().filterWorkerFilm;
        this.audio = audio;
    }

    public FilterWorker filterWorker;
    public boolean audio;
}
