package de.p2tools.mtplayer.controller.filmfilter;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TextFilter {
    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final StringProperty themeTitle = new SimpleStringProperty("");
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty somewhere = new SimpleStringProperty("");


    public TextFilter(FilmFilter filmFilter) {
        if (filmFilter.isChannelVis()) {
            channel.set(filmFilter.getChannel());
        }
        if (filmFilter.isThemeVis()) {
            theme.set(filmFilter.isThemeIsExact() ? filmFilter.getExactTheme() : filmFilter.getTheme());
        }
        if (filmFilter.isThemeTitleVis()) {
            themeTitle.set(filmFilter.getThemeTitle());
        }
        if (filmFilter.isTitleVis()) {
            title.set(filmFilter.getTitle());
        }
        if (filmFilter.isSomewhereVis()) {
            somewhere.set(filmFilter.getSomewhere());
        }
    }

    public boolean filterIsEmpty() {
        if (!getChannel().isEmpty()) {
            return false;
        }
        if (!getTheme().isEmpty()) {
            return false;
        }
        if (!getThemeTitle().isEmpty()) {
            return false;
        }
        if (!getTitle().isEmpty()) {
            return false;
        }
        if (!getSomewhere().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean filterIsSame(TextFilter tf) {
        if (!getChannel().equals(tf.getChannel())) {
            return false;
        }
        if (!getTheme().equals(tf.getTheme())) {
            return false;
        }
        if (!getThemeTitle().equals(tf.getThemeTitle())) {
            return false;
        }
        if (!getTitle().equals(tf.getTitle())) {
            return false;
        }
        if (!getSomewhere().equals(tf.getSomewhere())) {
            return false;
        }
        return true;
    }

    public String getChannel() {
        return channel.getValueSafe();
    }

    public void setChannel(String channel) {
        this.channel.set(channel);
    }

    public String getTheme() {
        return theme.getValueSafe();
    }

    public void setTheme(String theme) {
        this.theme.set(theme);
    }

    public String getThemeTitle() {
        return themeTitle.getValueSafe();
    }

    public void setThemeTitle(String themeTitle) {
        this.themeTitle.set(themeTitle);
    }

    public String getTitle() {
        return title.getValueSafe();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getSomewhere() {
        return somewhere.getValueSafe();
    }

    public void setSomewhere(String somewhere) {
        this.somewhere.set(somewhere);
    }
}
