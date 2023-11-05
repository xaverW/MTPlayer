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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.download.DownloadFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.PDialogFileChooser;

import java.util.regex.Matcher;

public class SetReplacePatternFactory {
    public static final String PATTERN_PATH_DEST = "ZIELPFAD";
    public static final String PATTERN_PATH_VLC = "PFAD_VLC";
    public static final String PATTERN_PATH_FFMPEG = "PFAD_FFMPEG";

    public static boolean progReplacePattern(SetDataList list) {
        boolean ret = true;
        for (final SetData pSet : list) {
            if (!progReplacePattern(pSet)) {
                ret = false;
            }
        }
        return ret;
    }

    private static boolean progReplacePattern(SetData pSet) {
        //todo da muss vorher der Downloadpfad abgefragt werden -> beim Update suchen??
        pSet.setDestPath(pSet.getDestPath().replace(PATTERN_PATH_DEST, DownloadFactory.getDownloadPath()));
        String vlc = "";
        String ffmpeg = "";
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
            // ffmpeg
            prog.setProgPath(prog.getProgPath().replaceAll(PATTERN_PATH_FFMPEG, Matcher.quoteReplacement(ffmpeg)));
            prog.setProgSwitch(prog.getProgSwitch().replaceAll(PATTERN_PATH_FFMPEG, Matcher.quoteReplacement(ffmpeg)));
        }
        return true;
    }

    private static String getPathVlc() {
        // liefert den Pfad wenn vorhanden, wenn nicht wird er in einem Dialog abgefragt
        if (ProgConfig.SYSTEM_PATH_VLC.get().isEmpty()) {
            ProgConfig.SYSTEM_PATH_VLC.setValue(PDialogFileChooser.showFileChooser(ProgData.getInstance().primaryStage, "VLC",
                    "VLC wird nicht gefunden.", "Bitte den Pfad zum" + P2LibConst.LINE_SEPARATOR +
                            "VLC-Player angeben.", false, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView()));
        }
        return ProgConfig.SYSTEM_PATH_VLC.get();
    }

    private static String getPathFFmpeg() {
        // liefert den Pfad wenn vorhanden, wenn nicht wird er in einem Dialog abgefragt
        if (ProgConfig.SYSTEM_PATH_FFMPEG.get().isEmpty()) {
            ProgConfig.SYSTEM_PATH_FFMPEG.setValue(PDialogFileChooser.showFileChooser(ProgData.getInstance().primaryStage, "ffmpeg",
                    "ffmpeg wird nicht gefunden.", "Bitte den Pfad zu" + P2LibConst.LINE_SEPARATOR +
                            "ffmpeg angeben.", false, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView()));
        }
        return ProgConfig.SYSTEM_PATH_FFMPEG.get();
    }
}
