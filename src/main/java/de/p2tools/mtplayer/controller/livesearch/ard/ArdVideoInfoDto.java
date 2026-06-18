/*
angepasste Version aus:
https://github.com/mediathekview/MLib
*/

package de.p2tools.mtplayer.controller.livesearch.ard;

import de.p2tools.mtplayer.controller.livesearch.tools.LiveConst;

import java.util.EnumMap;
import java.util.Map;

/**
 * Video information from
 * {@literal http://www.ardmediathek.de/play/media/[documentId]?devicetype=pc&features=flash}.
 */
public class ArdVideoInfoDto {

    private final Map<LiveConst.Qualities, String> videoUrls;
    private String subtitleUrl;

    public ArdVideoInfoDto() {
        videoUrls = new EnumMap<>(LiveConst.Qualities.class);
    }

    public String put(final LiveConst.Qualities key, final String value) {
        return videoUrls.put(key, value);
    }

    public void setSubtitleUrl(final String subtitleUrl) {
        this.subtitleUrl = subtitleUrl;
    }

}
