/*
angepasste Version aus:
https://github.com/mediathekview/MLib
*/

package de.p2tools.mtplayer.controller.livesearchard;

public class ArdFilmUrlInfoDto extends FilmUrlInfoDto {

    private final String quality;

    public ArdFilmUrlInfoDto(final String aUrl, final String aQuality) {
        super(aUrl);
        quality = aQuality;
    }

    public String getQuality() {
        return quality;
    }
}
