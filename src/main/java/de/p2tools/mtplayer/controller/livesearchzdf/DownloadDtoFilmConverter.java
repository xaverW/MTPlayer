package de.p2tools.mtplayer.controller.livesearchzdf;

import de.p2tools.mtplayer.controller.livesearch.tools.LiveConst;

import java.util.Map;
import java.util.Optional;

public class DownloadDtoFilmConverter {

    private DownloadDtoFilmConverter() {
    }

    public static void getOptimizedUrls(
            final Map<LiveConst.Qualities, String> downloadUrls,
            final Optional<ZdfVideoUrlOptimizer> aUrlOptimizer) {

        for (final Map.Entry<LiveConst.Qualities, String> qualitiesEntry : downloadUrls.entrySet()) {
            String url = qualitiesEntry.getValue();

            if (qualitiesEntry.getKey() == LiveConst.Qualities.NORMAL && aUrlOptimizer.isPresent()) {
                url = aUrlOptimizer.get().getOptimizedUrlNormal(url);
                qualitiesEntry.setValue(url);
            }
            if (qualitiesEntry.getKey() == LiveConst.Qualities.HD && aUrlOptimizer.isPresent()) {
                url = aUrlOptimizer.get().getOptimizedUrlHd(url);
                qualitiesEntry.setValue(url);
            }
        }

        if (!downloadUrls.containsKey(LiveConst.Qualities.HD) && aUrlOptimizer.isPresent()) {
            final Optional<String> hdUrl
                    = aUrlOptimizer.get().determineUrlHd(downloadUrls.get(LiveConst.Qualities.NORMAL));
            hdUrl.ifPresent(url -> downloadUrls.put(LiveConst.Qualities.HD, url));
        }
    }
}
