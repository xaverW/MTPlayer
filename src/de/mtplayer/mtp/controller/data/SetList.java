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

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.download.DownloadTools;
import de.mtplayer.mtp.gui.tools.SetsPrograms;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.dialog.PAlertFileChosser;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class SetList extends SimpleListProperty<SetData> {
    // Liste aller Programmsets
    public static final String PATTERN_PATH_DEST = "ZIELPFAD";
    public static final String PATTERN_PATH_VLC = "PFAD_VLC";
    public static final String PATTERN_PATH_FLV = "PFAD_FLVSTREAMER";
    public static final String PATTERN_PATH_FFMPEG = "PFAD_FFMPEG";
    public static final String PATTERN_PATH_SCRIPT = "PFAD_SCRIPT";
    public String version = "";
    private BooleanProperty listChanged = new SimpleBooleanProperty(true);

    public SetList() {
        super(FXCollections.observableArrayList());
    }

    public static boolean progReplacePattern(SetList list) {
        boolean ret = true;
        for (final SetData pSet : list) {
            if (!progReplacePattern(pSet)) {
                ret = false;
            }
        }
        return ret;
    }

    public boolean isListChanged() {
        return listChanged.get();
    }

    public BooleanProperty listChangedProperty() {
        return listChanged;
    }

    public void setListChanged() {
        this.listChanged.set(!listChanged.get());
    }

    public boolean removePset(Object obj) {
        // remove and notify
        boolean ret = super.remove(obj);
        setListChanged();
        return ret;
    }

    public boolean addPset(SetData psetData) {
        // add and notify
        boolean play = false;
        for (final SetData psetData1 : this) {
            if (psetData1.isPlay()) {
                play = true;
                break;
            }
        }
        if (play) {
            psetData.setPlay(false);
        }
        final boolean ret = super.add(psetData);
        setListChanged();
        return ret;
    }

    public boolean addPset(SetList list) {
        // add and notify
        boolean ret = true;
        for (final SetData entry : list) {
            if (!addPset(entry)) {
                ret = false;
            }
        }
        setListChanged();
        return ret;
    }


    private static boolean progReplacePattern(SetData pSet) {
        //todo da muss vorher der Downloadpfad abgefragt werden -> beim Update suchen??
        pSet.setDestPath(pSet.getDestPath().replace(PATTERN_PATH_DEST, DownloadTools.getDownloadPath()));
        String vlc = "";
        String flvstreamer = "";
        String ffmpeg = "";
        final String script = SetsPrograms.getPathScript();
        // damit nur die Variablen abgefragt werden, die auch verwendet werden
        for (int p = 0; p < pSet.getProgramList().size(); ++p) {
            final ProgramData prog = pSet.getProg(p);
            if (prog.getProgPath().contains(PATTERN_PATH_VLC) || prog.getProgSwitch().contains(PATTERN_PATH_VLC)) {
                vlc = getPathVlc();
                break;
            }
        }
        for (int p = 0; p < pSet.getProgramList().size(); ++p) {
            final ProgramData prog = pSet.getProg(p);
            if (prog.getProgPath().contains(PATTERN_PATH_FLV) || prog.getProgSwitch().contains(PATTERN_PATH_FLV)) {
                flvstreamer = getPathFlv();
                break;
            }
        }
        for (int p = 0; p < pSet.getProgramList().size(); ++p) {
            final ProgramData prog = pSet.getProg(p);
            if (prog.getProgPath().contains(PATTERN_PATH_FFMPEG) || prog.getProgSwitch().contains(PATTERN_PATH_FFMPEG)) {
                ffmpeg = getPathFFmpeg();
                break;
            }
        }
        for (int p = 0; p < pSet.getProgramList().size(); ++p) {
            final ProgramData prog = pSet.getProg(p);
            // VLC
            prog.setProgPath(prog.getProgPath().replaceAll(PATTERN_PATH_VLC, Matcher.quoteReplacement(vlc)));
            prog.setProgSwitch(prog.getProgSwitch().replaceAll(PATTERN_PATH_VLC, Matcher.quoteReplacement(vlc)));
            // flvstreamer
            prog.setProgPath(prog.getProgPath().replaceAll(PATTERN_PATH_FLV, Matcher.quoteReplacement(flvstreamer)));
            prog.setProgSwitch(prog.getProgSwitch().replaceAll(PATTERN_PATH_FLV, Matcher.quoteReplacement(flvstreamer)));
            // ffmpeg
            prog.setProgPath(prog.getProgPath().replaceAll(PATTERN_PATH_FFMPEG, Matcher.quoteReplacement(ffmpeg)));
            prog.setProgSwitch(prog.getProgSwitch().replaceAll(PATTERN_PATH_FFMPEG, Matcher.quoteReplacement(ffmpeg)));
            // script
            prog.setProgPath(prog.getProgPath().replaceAll(PATTERN_PATH_SCRIPT, Matcher.quoteReplacement(script)));
            prog.setProgSwitch(prog.getProgSwitch().replaceAll(PATTERN_PATH_SCRIPT, Matcher.quoteReplacement(script)));
        }
        return true;
    }

    private static String getPathVlc() {
        // liefert den Pfad wenn vorhanden, wenn nicht wird er in einem Dialog abgefragt
        if (ProgConfig.SYSTEM_PATH_VLC.get().isEmpty()) {
            ProgConfig.SYSTEM_PATH_VLC.setValue(PAlertFileChosser.showAlertFileChooser(ProgData.getInstance().primaryStage, "VLC",
                    "VLC wird nicht gefunden.", "Bitte den Pfad zum" + PConst.LINE_SEPARATOR +
                            "VLC-Player angeben.", false, new ProgIcons().ICON_BUTTON_FILE_OPEN));
        }
        return ProgConfig.SYSTEM_PATH_VLC.get();
    }

    private static String getPathFlv() {
        // liefert den Pfad wenn vorhanden, wenn nicht wird er in einem Dialog abgefragt
        if (ProgConfig.SYSTEM_PATH_FLVSTREAMER.get().isEmpty()) {
            ProgConfig.SYSTEM_PATH_FLVSTREAMER.setValue(PAlertFileChosser.showAlertFileChooser(ProgData.getInstance().primaryStage, "flvstreamer",
                    "flvstreamer wird nicht gefunden.", "Bitte den Pfad zum" + PConst.LINE_SEPARATOR +
                            "flvstreamer angeben.", false, new ProgIcons().ICON_BUTTON_FILE_OPEN));
        }
        return ProgConfig.SYSTEM_PATH_FLVSTREAMER.get();
    }

    private static String getPathFFmpeg() {
        // liefert den Pfad wenn vorhanden, wenn nicht wird er in einem Dialog abgefragt
        if (ProgConfig.SYSTEM_PATH_FFMPEG.get().isEmpty()) {
            ProgConfig.SYSTEM_PATH_FFMPEG.setValue(PAlertFileChosser.showAlertFileChooser(ProgData.getInstance().primaryStage, "ffmpeg",
                    "ffmpeg wird nicht gefunden.", "Bitte den Pfad zu" + PConst.LINE_SEPARATOR +
                            "ffmpeg angeben.", false, new ProgIcons().ICON_BUTTON_FILE_OPEN));
        }
        return ProgConfig.SYSTEM_PATH_FFMPEG.get();
    }

    public SetData getPsetPlay() {
        //liefert die Programmgruppe zum Abspielen
        for (final SetData psetData : this) {
            if (psetData.isPlay()) {
                return psetData;
            }
        }
        return null;
    }

    public SetData getPsetAbo(String name) {
        // liefert mit dem Namen eines Abos das passende Set zurück
        // wird nichts gefunden, wird das erste Set (der Abos) genommen
        SetData ret = null;
        if (isEmpty()) {
            ret = null;
        } else if (size() == 1) {
            ret = this.get(0);
        } else {
            for (final SetData pset : this) {
                if (pset.isAbo()) {
                    if (pset.getName().equals(name)) {
                        ret = pset;
                    }
                }
            }
            if (ret == null) {
                // die erste Pset der Abos
                final SetList ps = getListAbo();
                if (ps.size() > 0) {
                    ret = ps.get(0);
                    if (ret == null) {
                        // dann die erste Prgruppe
                        ret = get(0);
                    }
                }
            }
        }
        return ret;
    }

    public SetList getListSave() {
        // liefert eine Liste Programmsets, die zum Speichern angelegt sind (ist meist nur eins)
        return stream().filter(datenPset -> datenPset.isSave())
                .collect(Collectors.toCollection(SetList::new));
    }

    public SetList getListButton() {
        // liefert eine Liste Programmsets, die als Button angelegt sind
        // "leere" Button  werden nicht mehr angezeigt
        // sind nur die 2 Standardsets in der Liste wird nichts geliefert

        if (this.size() <= 2) {
            return new SetList();
        }

        return stream()
                .filter(datenPset -> datenPset.isButton())
                .filter(datenPset -> !datenPset.getProgramList().isEmpty())
                .filter(datenPset -> !datenPset.getName().isEmpty())
                .collect(Collectors.toCollection(SetList::new));
    }

    public SetList getListAbo() {
        // liefert eine Liste Programmsets, die für Abos angelegt sind (ist meist nur eins)
        return stream().filter(data -> data.isAbo())
                .collect(Collectors.toCollection(SetList::new));
    }

    public ObservableList<String> getPsetNameList() {
        //liefert eine Liste aller Psetnamen
        ObservableList<String> list = FXCollections.observableArrayList();

        for (final SetData psetData : this) {
            list.add(psetData.getName());
        }

        return list;
    }

    public void setPlay(SetData setData) {
        for (final SetData psetData : this) {
            psetData.setPlay(false);
        }
        setData.setPlay(true);
        setListChanged();
    }

    public SetData getPsetName(String name) {
        //liefert das PSet mit dem Namen oder null
        for (final SetData psetData : this) {
            if (psetData.getName().equals(name)) {
                return psetData;
            }
        }

        return null;
    }

    public int auf(int idx, boolean auf) {
        final SetData prog = this.remove(idx);
        int neu = idx;
        if (auf) {
            if (neu > 0) {
                --neu;
            }
        } else if (neu < size()) {
            ++neu;
        }
        this.add(neu, prog);
        setListChanged();
        return neu;
    }

    public ArrayList<String> getListProg() {
        return stream().map(SetData::toString).collect(Collectors.toCollection(ArrayList::new));
    }
}
