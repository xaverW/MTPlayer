package de.p2tools.mtplayer.controller.livesearch;

public enum Qualities {
    HD("HD"), NORMAL("Normal"), SMALL("Klein"), UHD("UHD");

    private final String description;

    Qualities(String aDescription) {
        description = aDescription;
    }

    public String getDescription() {
        return description;
    }
}
