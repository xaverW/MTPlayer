package de.p2tools.mtplayer.controller.filter;

public class FilterFactory {
    private FilterFactory() {
    }

    public static void saveStoredFilter(FilterWorker filterWorker, FilmFilter sf) {
        // Filter mit den aktuellen Einstellungen überschreiben
        if (sf == null) {
            return;
        }

        final String name = sf.getName();
        filterWorker.getActFilterSettings().copyTo(sf);
        sf.setName(name);
    }
}
