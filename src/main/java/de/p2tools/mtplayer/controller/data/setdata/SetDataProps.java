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

package de.p2tools.mtplayer.controller.data.setdata;

import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.gui.configdialog.panesetdata.AboSubDir;
import de.p2tools.p2lib.configfile.config.*;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class SetDataProps extends SetDataXml {

    public static final String TAG = "SetData";
    final ProgramList programList = new ProgramList();
    public static final Color RESET_COLOR = Color.BLACK;

    private StringProperty id = new SimpleStringProperty("");
    private StringProperty visibleName = new SimpleStringProperty("");
    private StringProperty prefix = new SimpleStringProperty("");
    private StringProperty suffix = new SimpleStringProperty("");

    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(this, "color", RESET_COLOR);

    private StringProperty destPath = new SimpleStringProperty("");
    private StringProperty destName = new SimpleStringProperty("");
    private BooleanProperty play = new SimpleBooleanProperty(false);
    private BooleanProperty save = new SimpleBooleanProperty(false);
    private BooleanProperty button = new SimpleBooleanProperty(false);
    private BooleanProperty abo = new SimpleBooleanProperty(false);

    private IntegerProperty maxSize = new SimpleIntegerProperty(0);
    private IntegerProperty maxField = new SimpleIntegerProperty(0);
    private StringProperty resolution = new SimpleStringProperty(FilmDataMTP.RESOLUTION_NORMAL);
    private StringProperty description = new SimpleStringProperty("");
    private BooleanProperty infoFile = new SimpleBooleanProperty(false);
    private BooleanProperty subtitle = new SimpleBooleanProperty(false);

    private BooleanProperty genAboSubDir = new SimpleBooleanProperty(true);
    private final IntegerProperty aboSubDir_ENSubDirNo = new SimpleIntegerProperty(AboSubDir.ENSubDir.THEME.getNo());

    public final Property[] properties = {id, visibleName, prefix, suffix, color, destPath, destName,
            play, save, button, abo, maxSize, maxField, resolution,/* adOn, */description,
            /* infoUrl,*/ infoFile, subtitle, genAboSubDir, aboSubDir_ENSubDirNo};

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "SetData";
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new Config_stringProp("id", id));
        list.add(new Config_stringProp("visibleName", visibleName));
        list.add(new Config_stringProp("prefix", prefix));
        list.add(new Config_stringProp("suffix", suffix));
        list.add(new Config_colorProp("color", color));
        list.add(new Config_stringProp("destPath", destPath));
        list.add(new Config_stringProp("destName", destName));
        list.add(new Config_boolProp("play", play));
        list.add(new Config_boolProp("save", save));
        list.add(new Config_boolProp("button", button));
        list.add(new Config_boolProp("abo", abo));
        list.add(new Config_intProp("maxSize", maxSize));
        list.add(new Config_intProp("maxField", maxField));
        list.add(new Config_stringProp("resolution", resolution));
        list.add(new Config_stringProp("description", description));
        list.add(new Config_boolProp("infoFile", infoFile));
        list.add(new Config_boolProp("subtitle", subtitle));
        list.add(new Config_boolProp("genAboSubDir", genAboSubDir));
        list.add(new Config_intProp("aboSubDir_ENSubDirNo", aboSubDir_ENSubDirNo));
        list.add(new Config_pDataList(programList));

        return list.toArray(new Config[]{});
    }

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

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
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

    public boolean isGenAboSubDir() {
        return genAboSubDir.get();
    }

    public BooleanProperty genAboSubDirProperty() {
        return genAboSubDir;
    }

    public void setGenAboSubDir(boolean genAboSubDir) {
        this.genAboSubDir.set(genAboSubDir);
    }

    public int getAboSubDir_ENSubDirNo() {
        return aboSubDir_ENSubDirNo.get();
    }

    public IntegerProperty aboSubDir_ENSubDirNoProperty() {
        return aboSubDir_ENSubDirNo;
    }

    public void setAboSubDir_ENSubDirNo(int aboSubDir_ENSubDirNo) {
        this.aboSubDir_ENSubDirNo.set(aboSubDir_ENSubDirNo);
    }

    public AboSubDir.ENSubDir getAboSubDir_ENSubDir() {
        return AboSubDir.getENSubDir(getAboSubDir_ENSubDirNo());
    }

    public String getAboSubDir_ENSubDir_Name() {
        return getAboSubDir_ENSubDir().getName();
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
