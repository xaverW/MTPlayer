package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.tools.log.P2Log;

public class LiveFilter extends TextFilter implements Filter {
    public static String TAG = "LiveFilter";

    @Override
    public String getTag() {
        return TAG;
    }

    public void reportFilterReturn() {
        // sind die ComboBoxen wenn return gedrückt wird
        P2Log.debugLog("reportFilterReturn");
        ProgData.getInstance().liveFilmFilterWorker.setFilter();
    }
}
