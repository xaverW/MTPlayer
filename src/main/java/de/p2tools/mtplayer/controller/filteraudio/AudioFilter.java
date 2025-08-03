package de.p2tools.mtplayer.controller.filteraudio;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filterfilm.Filter;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class AudioFilter extends AudioFilterProps implements Filter {
    private final PauseTransition pause = new PauseTransition(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue())); // nach Ablauf wird Änderung gemeldet - oder nach Return

    public AudioFilter() {
        initFilter();
    }

    public void reportFilterReturn() {
        // sind die ComboBoxen, wenn return gedrückt wird
        P2Log.debugLog("reportFilterReturn");
        pause.stop();
//        PListener.notify(PListener.EVENT_LIVE_FILTER_CHANGED, FilmFilter.class.getSimpleName());
        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_LIVE_FILTER_CHANGED);
    }

    public void clearFilter() {
        pause.setDuration(Duration.millis(0));
        setChannel("");
        setTheme("");
        setTitle("");
        pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
    }

    private void initFilter() {
        pause.setOnFinished(event -> reportFilterChange());
        pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        ProgConfig.SYSTEM_FILTER_WAIT_TIME.addListener((observable, oldValue, newValue) -> {
            P2Log.debugLog("SYSTEM_FILTER_WAIT_TIME: " + ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue());
            pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        });

        clearFilter();

        channelProperty().addListener(l -> setFilterChange(true));
        themeProperty().addListener(l -> setFilterChange(false));
        titleProperty().addListener(l -> setFilterChange(false));
    }

    private void reportFilterChange() {
        // sind die anderen Filter (ändern, ein-ausschalten), wenn Pause abgelaufen ist / gestoppt ist
//        PListener.notify(PListener.EVENT_LIVE_FILTER_CHANGED, FilmFilter.class.getSimpleName());
        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_LIVE_FILTER_CHANGED);
    }

    private void setFilterChange(boolean startNow) {
        // wird ausgelöst, wenn ein Filter ein/ausgeschaltet wird oder was eingetragen wird
        if (!startNow && ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
            //dann wird erst nach "RETURN" gestartet
            pause.stop();

        } else {
            // dann wird sofort gestartet (nach Pause)
            pause.playFromStart();
        }
    }
}
