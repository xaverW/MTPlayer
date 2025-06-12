/*
angepasste Version aus:
https://github.com/mediathekview/MLib
*/

package de.p2tools.mtplayer.controller.livesearchzdf;

import de.p2tools.mtplayer.controller.livesearch.tools.LiveConst;

import java.time.Duration;
import java.util.*;

/**
 * A data transfer object containing the information for downloading a video.
 */
public class DownloadDto {

    private Optional<GeoLocations> geoLocation;
    private final Map<String, String> subTitleUrls;
    private final Map<String, Map<LiveConst.Qualities, String>> downloadUrls;
    private Optional<Duration> duration;

    public DownloadDto() {
        downloadUrls = new HashMap<>();
        geoLocation = Optional.empty();
        subTitleUrls = new HashMap<>();
    }

    public void addUrl(final String language, final LiveConst.Qualities quality, final String url) {
        if (!downloadUrls.containsKey(language)) {
            downloadUrls.put(language, new EnumMap<>(LiveConst.Qualities.class));
        }

        Map<LiveConst.Qualities, String> urlMap = downloadUrls.get(language);
        urlMap.put(quality, url);
    }

    public Map<LiveConst.Qualities, String> getDownloadUrls(final String language) {
        if (downloadUrls.containsKey(language)) {
            return downloadUrls.get(language);
        }

        return new EnumMap<>(LiveConst.Qualities.class);
    }

    public Optional<Duration> getDuration() {
        return duration;
    }

    public Set<String> getLanguages() {
        return downloadUrls.keySet();
    }

    public Optional<GeoLocations> getGeoLocation() {
        return geoLocation;
    }

    public Optional<String> getSubTitleUrl(String language) {
        if (subTitleUrls.containsKey(language)) {
            return Optional.of(subTitleUrls.get(language));
        }
        return Optional.empty();
    }

    public Optional<String> getUrl(final String language, final LiveConst.Qualities resolution) {
        if (downloadUrls.containsKey(language)) {
            Map<LiveConst.Qualities, String> urlMap = downloadUrls.get(language);
            if (urlMap.containsKey(resolution)) {
                return Optional.of(urlMap.get(resolution));
            }
        }
        return Optional.empty();
    }

    public void setDuration(final Duration duration) {
        this.duration = Optional.of(duration);
    }

    public void setGeoLocation(final GeoLocations aGeoLocation) {
        geoLocation = Optional.of(aGeoLocation);
    }

    public void addSubTitleUrl(final String language, final String aUrl) {
        subTitleUrls.put(language, aUrl);
    }
}
