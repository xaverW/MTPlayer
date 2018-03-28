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

package de.mtplayer.mtp.controller.data;

import de.mtplayer.mLib.tools.MLC;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class SetProps extends SetXml {

    public static final Color RESET_COLOR = Color.BLACK;

    private StringProperty name = new SimpleStringProperty("");
    private StringProperty praefix = new SimpleStringProperty("");
    private StringProperty suffix = new SimpleStringProperty("");

    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(this, "color", RESET_COLOR);

    private StringProperty destPath = new SimpleStringProperty("");
    private StringProperty destName = new SimpleStringProperty("");
    private BooleanProperty genThema = new SimpleBooleanProperty(true);
    private BooleanProperty play = new SimpleBooleanProperty(false);
    private BooleanProperty save = new SimpleBooleanProperty(false);
    private BooleanProperty button = new SimpleBooleanProperty(false);
    private BooleanProperty abo = new SimpleBooleanProperty(false);

    private IntegerProperty maxSize = new SimpleIntegerProperty(0);
    private IntegerProperty maxField = new SimpleIntegerProperty(0);
    private StringProperty resolution = new SimpleStringProperty(FilmXml.AUFLOESUNG_NORMAL);
    private StringProperty adOn = new SimpleStringProperty("");
    private StringProperty descripton = new SimpleStringProperty("");
    private StringProperty infoUrl = new SimpleStringProperty("");
    private BooleanProperty infoFile = new SimpleBooleanProperty(false);
    private BooleanProperty subtitle = new SimpleBooleanProperty(false);


    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getPraefix() {
        return praefix.get();
    }

    public StringProperty praefixProperty() {
        return praefix;
    }

    public void setPraefix(String praefix) {
        this.praefix.set(praefix);
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

    public boolean isGenThema() {
        return genThema.get();
    }

    public BooleanProperty genThemaProperty() {
        return genThema;
    }

    public void setGenThema(boolean genThema) {
        this.genThema.set(genThema);
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

    public String getDescripton() {
        return descripton.get();
    }

    public StringProperty descriptonProperty() {
        return descripton;
    }

    public void setDescripton(String descripton) {
        this.descripton.set(descripton);
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
        setName(arr[PROGRAMMSET_NAME]);
        setPraefix(arr[PROGRAMMSET_PRAEFIX_DIREKT]);
        setSuffix(arr[PROGRAMMSET_SUFFIX_DIREKT]);

        setColorFromHex(arr[PROGRAMMSET_FARBE]);

        setDestPath(arr[PROGRAMMSET_ZIEL_PFAD]);
        setDestName(arr[PROGRAMMSET_ZIEL_DATEINAME]);
        setGenThema(Boolean.parseBoolean(arr[PROGRAMMSET_THEMA_ANLEGEN]));
        setPlay(Boolean.parseBoolean(arr[PROGRAMMSET_IST_ABSPIELEN]));
        setSave(Boolean.parseBoolean(arr[PROGRAMMSET_IST_SPEICHERN]));
        setButton(Boolean.parseBoolean(arr[PROGRAMMSET_IST_BUTTON]));
        setAbo(Boolean.parseBoolean(arr[PROGRAMMSET_IST_ABO]));

        setMaxFromXml();

        setResolution(arr[PROGRAMMSET_AUFLOESUNG]);
        setAdOn(arr[PROGRAMMSET_ADD_ON]);
        setDescripton(arr[PROGRAMMSET_BESCHREIBUNG]);
        setInfoUrl(arr[PROGRAMMSET_INFO_URL]);

        setInfoFile(Boolean.parseBoolean(arr[PROGRAMMSET_INFODATEI]));
        setSubtitle(Boolean.parseBoolean(arr[PROGRAMMSET_SUBTITLE]));
    }

    private void setMaxFromXml() {
        int maxSize;
        int maxField;
        try {
            maxSize = Integer.parseInt(arr[PROGRAMMSET_MAX_LAENGE]);
            maxField = Integer.parseInt(arr[PROGRAMMSET_MAX_LAENGE_FIELD]);
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
        arr[PROGRAMMSET_NAME] = getName();
        arr[PROGRAMMSET_PRAEFIX_DIREKT] = getPraefix();
        arr[PROGRAMMSET_SUFFIX_DIREKT] = getSuffix();

        arr[PROGRAMMSET_FARBE] = MLC.getColorToHex(color.getValue());
        arr[PROGRAMMSET_ZIEL_PFAD] = getDestPath();
        arr[PROGRAMMSET_ZIEL_DATEINAME] = getDestName();

        arr[PROGRAMMSET_THEMA_ANLEGEN] = String.valueOf(isGenThema());
        arr[PROGRAMMSET_IST_ABSPIELEN] = String.valueOf(isPlay());
        arr[PROGRAMMSET_IST_SPEICHERN] = String.valueOf(isSave());
        arr[PROGRAMMSET_IST_BUTTON] = String.valueOf(isButton());
        arr[PROGRAMMSET_IST_ABO] = String.valueOf(isAbo());

        arr[PROGRAMMSET_MAX_LAENGE] = String.valueOf(getMaxSize());
        arr[PROGRAMMSET_MAX_LAENGE_FIELD] = String.valueOf(getMaxField());

        arr[PROGRAMMSET_AUFLOESUNG] = getResolution();
        arr[PROGRAMMSET_ADD_ON] = getAdOn();
        arr[PROGRAMMSET_BESCHREIBUNG] = getDescripton();
        arr[PROGRAMMSET_INFO_URL] = getInfoUrl();

        arr[PROGRAMMSET_INFODATEI] = String.valueOf(isInfoFile());
        arr[PROGRAMMSET_SUBTITLE] = String.valueOf(isSubtitle());
    }

}
