/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package de.mtplayer.mtp.controller.data.abo;

import de.mtplayer.mLib.tools.Data;
import de.mtplayer.mLib.tools.MDate;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import javafx.beans.property.*;

public class AboProps extends AboXml {
    private final IntegerProperty nr = new SimpleIntegerProperty(0);
    private final BooleanProperty active = new SimpleBooleanProperty(true);
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty resolution = new SimpleStringProperty(FilmXml.AUFLOESUNG_NORMAL);
    private final StringProperty sender = new SimpleStringProperty("");
    private final BooleanProperty senderExact = new SimpleBooleanProperty(true);
    private final StringProperty theme = new SimpleStringProperty("");
    private final BooleanProperty themeExact = new SimpleBooleanProperty(true);
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty themeTitle = new SimpleStringProperty("");
    private final StringProperty somewhere = new SimpleStringProperty("");
    private final IntegerProperty min = new SimpleIntegerProperty(0); // Minuten
    private final IntegerProperty max = new SimpleIntegerProperty(SelectedFilter.FILTER_DURATIION_MAX_MIN); //Minuten
    private final StringProperty dest = new SimpleStringProperty("");
    private final ObjectProperty<MDate> date = new SimpleObjectProperty<>(new MDate(0));
    private final StringProperty pset = new SimpleStringProperty("");


    public final Property[] properties = {nr, active, name, resolution,
            sender, senderExact, theme, themeExact, title, themeTitle, somewhere,
            min, max, dest, date, pset};

    public String getStringOf(int i) {
        return String.valueOf(properties[i].getValue());
    }

    public AboProps() {
        super();
    }

    public int getNr() {
        return nr.get();
    }

    public IntegerProperty nrProperty() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr.set(nr);
    }

    public boolean getActive() {
        return active.get();
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }


    public String getResolution() {
        return resolution.get();
    }

    public StringProperty resolutionProperty() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution.set(resolution);
    }

    public String getSender() {
        return sender.get();
    }

    public StringProperty senderProperty() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender.set(sender);
    }

    public boolean isSenderExact() {
        return senderExact.get();
    }

    public BooleanProperty senderExactProperty() {
        return senderExact;
    }

    public void setSenderExact(boolean senderExact) {
        this.senderExact.set(senderExact);
    }

    public String getTheme() {
        return theme.get();
    }

    public StringProperty themeProperty() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme.set(theme);
    }

    public boolean isThemeExact() {
        return themeExact.get();
    }

    public BooleanProperty themeExactProperty() {
        return themeExact;
    }

    public void setThemeExact(boolean themaExact) {
        this.themeExact.set(themaExact);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getThemeTitle() {
        return themeTitle.get();
    }

    public StringProperty themeTitleProperty() {
        return themeTitle;
    }

    public void setThemeTitle(String themeTitle) {
        this.themeTitle.set(themeTitle);
    }

    public String getSomewhere() {
        return somewhere.get();
    }

    public StringProperty somewhereProperty() {
        return somewhere;
    }

    public void setSomewhere(String somewhere) {
        this.somewhere.set(somewhere);
    }

    public int getMin() {
        return min.get();
    }

    public int getMinSec() {
        return min.get() * 60;
    }

    public IntegerProperty minProperty() {
        return min;
    }

    public void setMin(int min) {
        this.min.set(min);
    }

    public int getMax() {
        return max.get();
    }

    public int getMaxSec() {
        return max.get() * 60;
    }

    public IntegerProperty maxProperty() {
        return max;
    }

    public void setMax(int max) {
        this.max.set(max);
    }

    public String getDest() {
        return dest.get();
    }

    public StringProperty destProperty() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest.set(dest);
    }

    public MDate getDate() {
        return date.get();
    }

    public ObjectProperty dateProperty() {
        return date;
    }

    public void setDate(MDate date) {
        this.date.set(date);
    }

    public void setDatum(String date, String time) {
        MDate d = new MDate();
        d.setDatum(date, time);
        this.date.setValue(d);
    }

    public String getPset() {
        return pset.get();
    }

    public StringProperty psetProperty() {
        return pset;
    }

    public void setPset(String pset) {
        this.pset.set(pset);
    }

    public void setPropsFromXml() {
        setActive(Boolean.parseBoolean(arr[ABO_ON]));
        setResolution(arr[ABO_AUFLOESUNG]);
        setName(arr[ABO_NAME]);
        setSender(arr[ABO_SENDER]);
        setSenderExact(arr[ABO_SENDER_EXAKT].isEmpty() ? true : Boolean.parseBoolean(arr[ABO_SENDER_EXAKT]));
        setTheme(arr[ABO_THEMA]);
        setThemeExact(arr[ABO_THEMA_EXAKT].isEmpty() ? true : Boolean.parseBoolean(arr[ABO_THEMA_EXAKT]));
        setTitle(arr[ABO_TITEL]);
        setThemeTitle(arr[ABO_THEMA_TITEL]);
        setSomewhere(arr[ABO_IRGENDWO]);

        setDauerFromXml();

        setDest(arr[ABO_ZIELPFAD]);
        setDatum(arr[ABO_DOWN_DATUM], "");
        setPset(arr[ABO_PSET]);
    }

    public void setXmlFromProps() {
        arr[ABO_NR] = getNr() + "";
        arr[ABO_ON] = String.valueOf(getActive());
        arr[ABO_AUFLOESUNG] = getResolution();
        arr[ABO_NAME] = getName();
        arr[ABO_SENDER] = getSender();
        arr[ABO_SENDER_EXAKT] = String.valueOf(isSenderExact());
        arr[ABO_THEMA] = getTheme();
        arr[ABO_THEMA_EXAKT] = String.valueOf(isThemeExact());
        arr[ABO_TITEL] = getTitle();
        arr[ABO_THEMA_TITEL] = getThemeTitle();
        arr[ABO_IRGENDWO] = getSomewhere();

        arr[ABO_MINDESTDAUER] = String.valueOf(getMin());
        arr[ABO_MAXDESTDAUER] = String.valueOf(getMax());

        arr[ABO_ZIELPFAD] = getDest();
        arr[ABO_DOWN_DATUM] = getDate().toString();
        arr[ABO_PSET] = getPset();
    }

    private void setDauerFromXml() {
        int min;
        int max;
        try {
            min = Integer.parseInt(arr[ABO_MINDESTDAUER]);
            max = Integer.parseInt(arr[ABO_MAXDESTDAUER]);
        } catch (final Exception ex) {
            min = 0;
            max = SelectedFilter.FILTER_DURATIION_MAX_MIN;
        }
        setMin(min);
        setMax(max);
    }


    public int compareTo(AboProps arg0) {
        return Data.sorter.compare(getName(), arg0.getName());
    }

}
