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
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.dialogs.PDialogFileChosser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class SetDataList extends SetDataListWorker {

    public static boolean progReplacePattern(SetDataList list) {
        boolean ret = true;
        for (final SetData pSet : list) {
            if (!progReplacePattern(pSet)) {
                ret = false;
            }
        }
        return ret;
    }

    public boolean removeSetData(Object obj) {
        // remove and notify
        boolean ret = super.remove(obj);
        setListChanged();
        return ret;
    }

    public boolean addSetData(SetData psetData) {
        // add and notify
        boolean play = false;
        for (final SetData sd : this) {
            if (sd.isPlay()) {
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

    public boolean addSetData(SetDataList list) {
        // add and notify
        boolean ret = true;
        for (final SetData entry : list) {
            if (!addSetData(entry)) {
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
            ProgConfig.SYSTEM_PATH_VLC.setValue(PDialogFileChosser.showFileChooser(ProgData.getInstance().primaryStage, "VLC",
                    "VLC wird nicht gefunden.", "Bitte den Pfad zum" + P2LibConst.LINE_SEPARATOR +
                            "VLC-Player angeben.", false, new ProgIcons().ICON_BUTTON_FILE_OPEN));
        }
        return ProgConfig.SYSTEM_PATH_VLC.get();
    }

    private static String getPathFlv() {
        // liefert den Pfad wenn vorhanden, wenn nicht wird er in einem Dialog abgefragt
        if (ProgConfig.SYSTEM_PATH_FLVSTREAMER.get().isEmpty()) {
            ProgConfig.SYSTEM_PATH_FLVSTREAMER.setValue(PDialogFileChosser.showFileChooser(ProgData.getInstance().primaryStage, "flvstreamer",
                    "flvstreamer wird nicht gefunden.", "Bitte den Pfad zum" + P2LibConst.LINE_SEPARATOR +
                            "flvstreamer angeben.", false, new ProgIcons().ICON_BUTTON_FILE_OPEN));
        }
        return ProgConfig.SYSTEM_PATH_FLVSTREAMER.get();
    }

    private static String getPathFFmpeg() {
        // liefert den Pfad wenn vorhanden, wenn nicht wird er in einem Dialog abgefragt
        if (ProgConfig.SYSTEM_PATH_FFMPEG.get().isEmpty()) {
            ProgConfig.SYSTEM_PATH_FFMPEG.setValue(PDialogFileChosser.showFileChooser(ProgData.getInstance().primaryStage, "ffmpeg",
                    "ffmpeg wird nicht gefunden.", "Bitte den Pfad zu" + P2LibConst.LINE_SEPARATOR +
                            "ffmpeg angeben.", false, new ProgIcons().ICON_BUTTON_FILE_OPEN));
        }
        return ProgConfig.SYSTEM_PATH_FFMPEG.get();
    }

    public SetData getSetDataPlay() {
        //liefert die Programmgruppe zum Abspielen
        for (final SetData psetData : this) {
            if (psetData.isPlay()) {
                return psetData;
            }
        }
        return null;
    }

    public SetData getSetDataForAbo() {
        return getSetDataForAbo("");
    }

    public SetData getSetDataForAbo(String id) {
        // liefert mit dem SetNamen das passende Set zurück
        // wird nichts gefunden, wird das erste Set (der Abos) genommen

        if (isEmpty()) {
            return null;
        }

        if (size() == 1) {
            // gibt nur eins
            return this.get(0);
        }

        // das Set mit dem Namen
        for (final SetData pset : this) {
            if (pset.isAbo() && pset.getId().equals(id)) {
                return pset;
            }
        }

        // das erste Set der Abos
        for (final SetData pset : this) {
            if (pset.isAbo()) {
                return pset;
            }
        }

        // das erste Set der Downloads
        for (final SetData pset : this) {
            if (pset.isSave()) {
                // wenns keins gibt, wird das "ABO"
                pset.setAbo(true);
                return pset;
            }
        }

        // dann eben das erste Set
        return get(0);
    }

    public SetData getSetDataForDownloads(String id) {
        // liefert mit dem SetNamen das passende Set zurück
        // wird nichts gefunden, wird das erste Set (der Abos/Downloads) genommen

        if (isEmpty()) {
            return null;
        }

        if (size() == 1) {
            // gibt nur eins
            return this.get(0);
        }

        // das Set mit dem Namen
        for (final SetData pset : this) {
            if (pset.isSave() && pset.getId().equals(id)) {
                return pset;
            }
        }


        // das erste Set der Downloads
        for (final SetData pset : this) {
            if (pset.isSave()) {
                return pset;
            }
        }

        // das erste Set der Abos
        for (final SetData pset : this) {
            if (pset.isAbo()) {
                // wenns keins gibt, wird das "SAVE"
                pset.setSave(true);
                return pset;
            }
        }

        // dann eben das erste Set
        return get(0);
    }

    public SetDataList getSetDataListSave() {
        // liefert eine Liste Programmsets, die zum Speichern angelegt sind (ist meist nur eins)
        return stream().filter(setData -> setData.isSave())
                .collect(Collectors.toCollection(SetDataList::new));
    }

    public SetDataList getSetDataListButton() {
        // liefert eine Liste Programmsets, die als Button angelegt sind
        // "leere" Button  werden nicht mehr angezeigt
        // sind nur die 2 Standardsets in der Liste wird nichts geliefert

        if (this.size() <= 2) {
            return new SetDataList();
        }

        return stream()
                .filter(setData -> setData.isButton())
                .filter(setData -> !setData.getProgramList().isEmpty())
                .filter(setData -> !setData.getVisibleName().isEmpty())
                .collect(Collectors.toCollection(SetDataList::new));
    }

    public SetDataList getSetDataListAbo() {
        // liefert eine Liste Programmsets, die für Abos angelegt sind (ist meist nur eins)
        return stream().filter(data -> data.isAbo())
                .collect(Collectors.toCollection(SetDataList::new));
    }

//    public ObservableList<String> getSetDataVisibleNameList() {
//        //liefert eine Liste aller SetDataNamen
//        ObservableList<String> list = FXCollections.observableArrayList();
//
//        for (final SetData psetData : this) {
//            list.add(psetData.getVisibleName());
//        }
//
//        return list;
//    }

    public void setPlay(SetData setData) {
        for (final SetData sData : this) {
            sData.setPlay(false);
        }
        setData.setPlay(true);
        setListChanged();
    }

    public SetData getSetDataWithId(String id) {
        //liefert das PSet mit ID oder null
        for (final SetData setData : this) {
            if (setData.getId().equals(id)) {
                return setData;
            }
        }

        return null;
    }

    public int up(int idx, boolean up) {
        final SetData prog = this.remove(idx);
        int newIdx = idx;
        if (up) {
            if (newIdx > 0) {
                --newIdx;
            }
        } else if (newIdx < size()) {
            ++newIdx;
        }
        this.add(newIdx, prog);
        setListChanged();
        return newIdx;
    }

    public ArrayList<String> getStringListSetData() {
        return stream().map(SetData::setDataToString).collect(Collectors.toCollection(ArrayList::new));
    }
}
