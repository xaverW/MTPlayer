/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
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

package de.mtplayer.mtp.controller.data;

import de.mtplayer.mLib.tools.MLC;
import de.mtplayer.mtp.controller.data.film.Film;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class SetDataProps extends SetDataXml {

    public static final Color RESET_COLOR = Color.BLACK;

    private StringProperty id = new SimpleStringProperty("");
    private StringProperty visibleName = new SimpleStringProperty("");
    private StringProperty prefix = new SimpleStringProperty("");
    private StringProperty suffix = new SimpleStringProperty("");

    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(this, "color", RESET_COLOR);

    private StringProperty destPath = new SimpleStringProperty("");
    private StringProperty destName = new SimpleStringProperty("");
    private BooleanProperty genTheme = new SimpleBooleanProperty(true);
    private BooleanProperty play = new SimpleBooleanProperty(false);
    private BooleanProperty save = new SimpleBooleanProperty(false);
    private BooleanProperty button = new SimpleBooleanProperty(false);
    private BooleanProperty abo = new SimpleBooleanProperty(false);

    private IntegerProperty maxSize = new SimpleIntegerProperty(0);
    private IntegerProperty maxField = new SimpleIntegerProperty(0);
    private StringProperty resolution = new SimpleStringProperty(Film.RESOLUTION_NORMAL);
    private StringProperty adOn = new SimpleStringProperty("");
    private StringProperty description = new SimpleStringProperty("");
    private StringProperty infoUrl = new SimpleStringProperty("");
    private BooleanProperty infoFile = new SimpleBooleanProperty(false);
    private BooleanProperty subtitle = new SimpleBooleanProperty(false);

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getVisibleName() {
        return visibleName.get();
    }

    public StringProperty visibleNameProperty() {
        return visibleName;
    }

    public void setVisibleName(String visibleName) {
        this.visibleName.set(visibleName);
    }

    public String getPrefix() {
        return prefix.get();
    }

    public StringProperty prefixProperty() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix.set(prefix);
    }

    public String getSuffix() {
        return suffix.get();
    }

    public StringProperty suffixProperty() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix.set(suffix);
    }

    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void setColor(Color newColor) {
        color.set(newColor);
    }

    public String getDestPath() {
        return destPath.get();
    }

    public StringProperty destPathProperty() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath.set(destPath);
    }

    public String getDestName() {
        return destName.get();
    }

    public StringProperty destNameProperty() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName.set(destName);
    }

    public boolean isGenTheme() {
        return genTheme.get();
    }

    public BooleanProperty genThemeProperty() {
        return genTheme;
    }

    public void setGenTheme(boolean genTheme) {
        this.genTheme.set(genTheme);
    }

    public boolean isPlay() {
        return play.get();
    }

    public BooleanProperty playProperty() {
        return play;
    }

    public void setPlay(boolean play) {
        this.play.set(play);
    }

    public boolean isSave() {
        return save.get();
    }

    public BooleanProperty saveProperty() {
        return save;
    }

    public void setSave(boolean save) {
        this.save.set(save);
    }

    public boolean isButton() {
        return button.get();
    }

    public BooleanProperty buttonProperty() {
        return button;
    }

    public void setButton(boolean button) {
        this.button.set(button);
    }

    public boolean isAbo() {
        return abo.get();
    }

    public BooleanProperty aboProperty() {
        return abo;
    }

    public void setAbo(boolean abo) {
        this.abo.set(abo);
    }

    public int getMaxSize() {
        return maxSize.get();
    }

    public IntegerProperty maxSizeProperty() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize.set(maxSize);
    }

    public int getMaxField() {
        return maxField.get();
    }

    public IntegerProperty maxFieldProperty() {
        return maxField;
    }

    public void setMaxField(int maxField) {
        this.maxField.set(maxField);
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

    public String getAdOn() {
        return adOn.get();
    }

    public StringProperty adOnProperty() {
        return adOn;
    }

    public void setAdOn(String adOn) {
        this.adOn.set(adOn);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getInfoUrl() {
        return infoUrl.get();
    }

    public StringProperty infoUrlProperty() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl.set(infoUrl);
    }

    public boolean isInfoFile() {
        return infoFile.get();
    }

    public BooleanProperty infoFileProperty() {
        return infoFile;
    }

    public void setInfoFile(boolean infoFile) {
        this.infoFile.set(infoFile);
    }

    public boolean isSubtitle() {
        return subtitle.get();
    }

    public BooleanProperty subtitleProperty() {
        return subtitle;
    }

    public void setSubtitle(boolean subtitle) {
        this.subtitle.set(subtitle);
    }


    public void setPropsFromXml() {
        setId(arr[PROGRAMSET_ID]);
        setVisibleName(arr[PROGRAMSET_VISIBLE_NAME]);
        setPrefix(arr[PROGRAMSET_PRAEFIX_DIRECT]);
        setSuffix(arr[PROGRAMSET_SUFFIX_DIRECT]);

        setColorFromHex(arr[PROGRAMSET_COLOR]);

        setDestPath(arr[PROGRAMSET_ZIEL_PFAD]);
        setDestName(arr[PROGRAMSET_ZIEL_DATEINAME]);
        setGenTheme(Boolean.parseBoolean(arr[PROGRAMSET_THEMA_ANLEGEN]));
        setPlay(Boolean.parseBoolean(arr[PROGRAMSET_IST_ABSPIELEN]));
        setSave(Boolean.parseBoolean(arr[PROGRAMSET_IST_SPEICHERN]));
        setButton(Boolean.parseBoolean(arr[PROGRAMSET_IST_BUTTON]));
        setAbo(Boolean.parseBoolean(arr[PROGRAMSET_IST_ABO]));

        setMaxFromXml();

        setResolution(arr[PROGRAMSET_AUFLOESUNG]);
        setAdOn(arr[PROGRAMSET_ADD_ON]);
        setDescription(arr[PROGRAMSET_BESCHREIBUNG]);
        setInfoUrl(arr[PROGRAMSET_INFO_URL]);

        setInfoFile(Boolean.parseBoolean(arr[PROGRAMSET_INFODATEI]));
        setSubtitle(Boolean.parseBoolean(arr[PROGRAMSET_SUBTITLE]));
    }

    private void setMaxFromXml() {
        int maxSize;
        int maxField;
        try {
            maxSize = Integer.parseInt(arr[PROGRAMSET_MAX_LAENGE]);
            maxField = Integer.parseInt(arr[PROGRAMSET_MAX_LAENGE_FIELD]);
        } catch (final Exception ex) {
            maxSize = 0;
            maxField = 0;
        }
        setMaxSize(maxSize);
        setMaxField(maxField);
    }

    private void setColorFromHex(String hex) {
        try {
            setColor(Color.web(hex));
        } catch (Exception ex) {

        }
    }

    public void setXmlFromProps() {
        arr[PROGRAMSET_ID] = getId();
        arr[PROGRAMSET_VISIBLE_NAME] = getVisibleName();
        arr[PROGRAMSET_PRAEFIX_DIRECT] = getPrefix();
        arr[PROGRAMSET_SUFFIX_DIRECT] = getSuffix();

        arr[PROGRAMSET_COLOR] = MLC.getColorToHex(color.getValue());
        arr[PROGRAMSET_ZIEL_PFAD] = getDestPath();
        arr[PROGRAMSET_ZIEL_DATEINAME] = getDestName();

        arr[PROGRAMSET_THEMA_ANLEGEN] = String.valueOf(isGenTheme());
        arr[PROGRAMSET_IST_ABSPIELEN] = String.valueOf(isPlay());
        arr[PROGRAMSET_IST_SPEICHERN] = String.valueOf(isSave());
        arr[PROGRAMSET_IST_BUTTON] = String.valueOf(isButton());
        arr[PROGRAMSET_IST_ABO] = String.valueOf(isAbo());

        arr[PROGRAMSET_MAX_LAENGE] = String.valueOf(getMaxSize());
        arr[PROGRAMSET_MAX_LAENGE_FIELD] = String.valueOf(getMaxField());

        arr[PROGRAMSET_AUFLOESUNG] = getResolution();
        arr[PROGRAMSET_ADD_ON] = getAdOn();
        arr[PROGRAMSET_BESCHREIBUNG] = getDescription();
        arr[PROGRAMSET_INFO_URL] = getInfoUrl();

        arr[PROGRAMSET_INFODATEI] = String.valueOf(isInfoFile());
        arr[PROGRAMSET_SUBTITLE] = String.valueOf(isSubtitle());
    }

    @Override
    public String toString() {
        return getVisibleName();
    }

    @Override
    public int compareTo(SetData setData) {
        return this.getVisibleName().compareTo(setData.getVisibleName());
    }
}
