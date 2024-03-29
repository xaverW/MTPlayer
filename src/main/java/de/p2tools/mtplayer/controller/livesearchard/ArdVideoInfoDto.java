package de.p2tools.mtplayer.controller.livesearchard;

import de.p2tools.mtplayer.controller.livesearch.tools.LiveConst;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

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

    public LiveConst.Qualities getDefaultQuality() {
        if (videoUrls.containsKey(LiveConst.Qualities.NORMAL)) {
            return LiveConst.Qualities.NORMAL;
        }
        return videoUrls.keySet().iterator().next();
    }

    public String getDefaultVideoUrl() {
        return videoUrls.get(getDefaultQuality());
    }

    public String getSubtitleUrl() {
        return subtitleUrl;
    }

    public Optional<String> getSubtitleUrlOptional() {
        if (StringUtils.isNotBlank(subtitleUrl)) {
            return Optional.of(subtitleUrl);
        }

        return Optional.empty();
    }

    public Map<LiveConst.Qualities, String> getVideoUrls() {
        return videoUrls;
    }

    public boolean containsQualities(final LiveConst.Qualities key) {
        return videoUrls.containsKey(key);
    }

    public String put(final LiveConst.Qualities key, final String value) {
        return videoUrls.put(key, value);
    }

    public void setSubtitleUrl(final String subtitleUrl) {
        this.subtitleUrl = subtitleUrl;
    }

}
