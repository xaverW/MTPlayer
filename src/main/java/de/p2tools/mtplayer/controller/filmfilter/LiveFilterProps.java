package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.config.Config_stringProp;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class LiveFilterProps extends P2DataSample<LiveFilter> implements Comparable<LiveFilter> {
    public static String TAG = "LiveFilter";

    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final StringProperty title = new SimpleStringProperty("");

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new Config_stringProp("channel", channel));
        list.add(new Config_stringProp("theme", theme));
        list.add(new Config_stringProp("title", title));
        return list.toArray(new Config[]{});
    }

    @Override
    public String getTag() {
        return TAG;
    }

    public boolean isSame(LiveFilter tf) {
        return getChannel().equals(tf.getChannel()) &&
                getTheme().equals(tf.getTheme()) &&
                getTitle().equals(tf.getTitle());
    }

    public boolean isEmpty() {
        return getChannel().isEmpty() &&
                getTheme().isEmpty() &&
                getTitle().isEmpty();
    }

    public LiveFilter getCopy() {
        LiveFilter sf = new LiveFilter();
        this.copyTo(sf);
        return sf;
    }

    public void copyTo(LiveFilter sf) {
        setChannel(sf.getChannel());
        setTheme(sf.getTheme());
        setTitle(sf.getTitle());
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

    public String getTitle() {
        return title.getValueSafe();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    @Override
    public String toString() {
        return channel.getValueSafe() + theme.getValueSafe() + title.getValueSafe();
    }

    @Override
    public int compareTo(LiveFilter o) {
        if (o == null) {
            return -1;
        }

        return isSame(o) ? 0 : -1;
    }
}
