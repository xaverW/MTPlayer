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
import de.mtplayer.mtp.controller.data.download.DownloadTools;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.SetsPrograms;
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
    public static final String MUSTER_PFAD_ZIEL = "ZIELPFAD";
    public static final String MUSTER_PFAD_VLC = "PFAD_VLC";
    public static final String MUSTER_PFAD_FLV = "PFAD_FLVSTREAMER";
    public static final String MUSTER_PFAD_FFMPEG = "PFAD_FFMPEG";
    public static final String MUSTER_PFAD_SCRIPT = "PFAD_SCRIPT";
    public String version = "";
    private BooleanProperty listChanged = new SimpleBooleanProperty(true);


    public SetList() {
        super(FXCollections.observableArrayList());
    }

    public static boolean progMusterErsetzen(SetList liste) {
        boolean ret = true;
        for (final SetData pSet : liste) {
            if (!progMusterErsetzen(pSet)) {
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

    private static boolean progMusterErsetzen(SetData pSet) {
        //todo da muss vorher der Downloadpfad abgefragt werden -> beim Update suchen??
        pSet.setDestPath(pSet.getDestPath().replace(MUSTER_PFAD_ZIEL, DownloadTools.getDownloadPath()));
        String vlc = "";
        String flvstreamer = "";
        String ffmpeg = "";
        final String skript = SetsPrograms.getPfadScript();
        // damit nur die Variablen abgefragt werden, die auch verwendet werden
        for (int p = 0; p < pSet.getProgList().size(); ++p) {
            final ProgData prog = pSet.getProg(p);
            if (prog.getProgPath().contains(MUSTER_PFAD_VLC) || prog.getProgSwitch().contains(MUSTER_PFAD_VLC)) {
                vlc = getPfadVlc();
                break;
            }
        }
        for (int p = 0; p < pSet.getProgList().size(); ++p) {
            final ProgData prog = pSet.getProg(p);
            if (prog.getProgPath().contains(MUSTER_PFAD_FLV) || prog.getProgSwitch().contains(MUSTER_PFAD_FLV)) {
                flvstreamer = getPfadFlv();
                break;
            }
        }
        for (int p = 0; p < pSet.getProgList().size(); ++p) {
            final ProgData prog = pSet.getProg(p);
            if (prog.getProgPath().contains(MUSTER_PFAD_FFMPEG) || prog.getProgSwitch().contains(MUSTER_PFAD_FFMPEG)) {
                ffmpeg = getPfadFFmpeg();
                break;
            }
        }
        for (int p = 0; p < pSet.getProgList().size(); ++p) {
            final ProgData prog = pSet.getProg(p);
            // VLC
            prog.setProgPath(prog.getProgPath().replaceAll(MUSTER_PFAD_VLC, Matcher.quoteReplacement(vlc)));
            prog.setProgSwitch(prog.getProgSwitch().replaceAll(MUSTER_PFAD_VLC, Matcher.quoteReplacement(vlc)));
            // flvstreamer
            prog.setProgPath(prog.getProgPath().replaceAll(MUSTER_PFAD_FLV, Matcher.quoteReplacement(flvstreamer)));
            prog.setProgSwitch(prog.getProgSwitch().replaceAll(MUSTER_PFAD_FLV, Matcher.quoteReplacement(flvstreamer)));
            // ffmpeg
            prog.setProgPath(prog.getProgPath().replaceAll(MUSTER_PFAD_FFMPEG, Matcher.quoteReplacement(ffmpeg)));
            prog.setProgSwitch(prog.getProgSwitch().replaceAll(MUSTER_PFAD_FFMPEG, Matcher.quoteReplacement(ffmpeg)));
            // script
            prog.setProgPath(prog.getProgPath().replaceAll(MUSTER_PFAD_SCRIPT, Matcher.quoteReplacement(skript)));
            prog.setProgSwitch(prog.getProgSwitch().replaceAll(MUSTER_PFAD_SCRIPT, Matcher.quoteReplacement(skript)));
        }
        return true;
    }

    private static String getPfadVlc() {
        // liefert den Pfad wenn vorhanden, wenn nicht wird er in einem Dialog abgefragt
        if (ProgConfig.SYSTEM_PATH_VLC.get().isEmpty()) {
            ProgConfig.SYSTEM_PATH_VLC.setValue(new MTAlert().showAlertFileCooser("VLC", "VLC wird nicht gefunden.",
                    "Bitte den Pfad zum\n" +
                            "VLC-Player angeben.", false));
        }
        return ProgConfig.SYSTEM_PATH_VLC.get();
    }

    private static String getPfadFlv() {
        // liefert den Pfad wenn vorhanden, wenn nicht wird er in einem Dialog abgefragt
        if (ProgConfig.SYSTEM_PATH_FLVSTREAMER.get().isEmpty()) {
            ProgConfig.SYSTEM_PATH_FLVSTREAMER.setValue(new MTAlert().showAlertFileCooser("flvstreamer", "flvstreamer wird nicht gefunden.",
                    "Bitte den Pfad zum\n" +
                            "flvstreamer angeben.", false));
        }
        return ProgConfig.SYSTEM_PATH_FLVSTREAMER.get();
    }

    private static String getPfadFFmpeg() {
        // liefert den Pfad wenn vorhanden, wenn nicht wird er in einem Dialog abgefragt
        if (ProgConfig.SYSTEM_PATH_FFMPEG.get().isEmpty()) {
            ProgConfig.SYSTEM_PATH_FFMPEG.setValue(new MTAlert().showAlertFileCooser("ffmpeg", "ffmpeg wird nicht gefunden.",
                    "Bitte den Pfad zu\n" +
                            "ffmpeg angeben.", false));
        }
        return ProgConfig.SYSTEM_PATH_FFMPEG.get();
    }

    public SetData getPsetAbspielen() {
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
                final SetList ps = getListeAbo();
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

    public SetList getListeSpeichern() {
        // liefert eine Liste Programmsets, die zum Speichern angelegt sind (ist meist nur eins)
        return stream().filter(datenPset -> datenPset.isSave())
                .collect(Collectors.toCollection(SetList::new));
    }

    public SetList getListeButton() {
        // liefert eine Liste Programmsets, die als Button angelegt sind
        // "leere" Button  werden nicht mehr angezeigt
        // sind nur die 2 Standardsets in der Liste wird nichts geliefert

        if (this.size() <= 2) {
            return new SetList();
        }

        return stream().filter(datenPset ->
                datenPset.isButton() && !datenPset.getProgList().isEmpty() && !datenPset.getName().isEmpty())
                .collect(Collectors.toCollection(SetList::new));
    }

    public SetList getListeAbo() {
        // liefert eine Liste Programmsets, die für Abos angelegt sind (ist meist nur eins)
        return stream().filter(datenPset -> datenPset.isAbo())
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

    public void setAbspielen(SetData setData) {
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

    public boolean addPset(SetData psetData) {
        boolean abspielen = false;
        for (final SetData psetData1 : this) {
            if (psetData1.isPlay()) {
                abspielen = true;
                break;
            }
        }
        if (abspielen) {
            psetData.setPlay(false);
        }
        final boolean ret = add(psetData);
        setListChanged();
        return ret;
    }

    public boolean addPset(SetList liste) {
        boolean ret = true;
        for (final SetData entry : liste) {
            if (!addPset(entry)) {
                ret = false;
            }
        }
        setListChanged();
        return ret;
    }


    public ArrayList<String> getListProg() {
        return stream().map(SetData::toString).collect(Collectors.toCollection(ArrayList::new));
    }
}
