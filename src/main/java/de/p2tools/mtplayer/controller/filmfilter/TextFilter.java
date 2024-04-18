package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.config.Config_stringProp;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class TextFilter extends P2DataSample<TextFilter> implements Comparable<TextFilter> {
    public static String TAG = "TextFilter";

    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final StringProperty themeTitle = new SimpleStringProperty("");
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty somewhere = new SimpleStringProperty("");

    public TextFilter() {
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new Config_stringProp("channel", channel));
        list.add(new Config_stringProp("theme", theme));
        list.add(new Config_stringProp("themeTitle", themeTitle));
        list.add(new Config_stringProp("title", title));
        list.add(new Config_stringProp("somewhere", somewhere));
        return list.toArray(new Config[]{});
    }

    @Override
    public String getTag() {
        return TAG;
    }

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

    public void clearFilter() {
        channel.set("");
        theme.set("");
        themeTitle.set("");
        title.set("");
        somewhere.set("");
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

    public StringProperty channelProperty() {
        return channel;
    }

    public String getTheme() {
        return theme.getValueSafe();
    }

    public void setTheme(String theme) {
        this.theme.set(theme);
    }

    public StringProperty themeProperty() {
        return theme;
    }

    public String getThemeTitle() {
        return themeTitle.getValueSafe();
    }

    public void setThemeTitle(String themeTitle) {
        this.themeTitle.set(themeTitle);
    }

    public StringProperty themeTitleProperty() {
        return themeTitle;
    }

    public String getTitle() {
        return title.getValueSafe();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getSomewhere() {
        return somewhere.getValueSafe();
    }

    public void setSomewhere(String somewhere) {
        this.somewhere.set(somewhere);
    }

    public StringProperty somewhereProperty() {
        return somewhere;
    }
}
