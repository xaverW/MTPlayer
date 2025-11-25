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
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.P2DialogFileChooser;
import de.p2tools.p2lib.mediathek.download.GetProgramStandardPath;
import de.p2tools.p2lib.tools.P2InfoFactory;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;

public class SetReplacePatternFactory {
    public static final String PATTERN_PATH_DEST = "ZIELPFAD";
    public static final String PATTERN_PATH_VLC = "PFAD_VLC";
    public static final String PATTERN_PATH_FFMPEG = "PFAD_FFMPEG";

    public static boolean progReplacePattern(Stage stage, SetDataList list) {
        // ImportStandardSet.getStandardSet
        boolean ret = true;
        for (final SetData pSet : list) {
            if (!progReplacePattern(stage, pSet)) {
                ret = false;
            }
        }
        return ret;
    }

    private static boolean progReplacePattern(Stage stage, SetData pSet) {
        pSet.setDestPath(pSet.getDestPath().replace(PATTERN_PATH_DEST,
                ProgConfig.DOWNLOAD_PATH.get().isEmpty() ?
                        P2InfoFactory.getStandardDownloadPath() : ProgConfig.DOWNLOAD_PATH.get()));

        String vlc = "";
        String ffmpeg = "";

        // damit nur die Variablen abgefragt werden, die auch verwendet werden
        for (int p = 0; p < pSet.getProgramList().size(); ++p) {
            final ProgramData prog = pSet.getProg(p);
            if (prog.getProgPath().contains(PATTERN_PATH_VLC) || prog.getProgSwitch().contains(PATTERN_PATH_VLC)) {
                vlc = getPathVlc(stage);
                break;
            }
        }
        for (int p = 0; p < pSet.getProgramList().size(); ++p) {
            final ProgramData prog = pSet.getProg(p);
            if (prog.getProgPath().contains(PATTERN_PATH_FFMPEG) || prog.getProgSwitch().contains(PATTERN_PATH_FFMPEG)) {
                ffmpeg = getPathFFmpeg(stage);
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

    private static String getPathVlc(Stage stage) {
        // liefert den Pfad, wenn vorhanden, wenn nicht, wird er in einem Dialog abgefragt
        String orgPath = GetProgramStandardPath.getTemplatePathVlc();
        if (ProgConfig.SYSTEM_PATH_VLC.get().isEmpty()) {
            ProgConfig.SYSTEM_PATH_VLC.set(orgPath);
        }

        Path path = Paths.get(ProgConfig.SYSTEM_PATH_VLC.get());
        if (!ProgConfig.SYSTEM_PATH_VLC.get().isEmpty() && Files.exists(path)) {
            return ProgConfig.SYSTEM_PATH_VLC.get();
        }

        // falls der Pfad geändert wurde
        path = Paths.get(orgPath);
        if (!orgPath.isEmpty() && Files.exists(path)) {
            ProgConfig.SYSTEM_PATH_VLC.set(orgPath);
            return ProgConfig.SYSTEM_PATH_VLC.get();
        }

        ProgConfig.SYSTEM_PATH_VLC.setValue(P2DialogFileChooser.showFileChooser(stage, "VLC",
                "VLC wird nicht gefunden.", "Bitte den Pfad zum" + P2LibConst.LINE_SEPARATOR +
                        "VLC-Player angeben.", false));
        return ProgConfig.SYSTEM_PATH_VLC.get();
    }

    private static String getPathFFmpeg(Stage stage) {
        // liefert den Pfad, wenn vorhanden, wenn nicht, wird er in einem Dialog abgefragt
        String orgPath = GetProgramStandardPath.getTemplatePathFFmpeg();
        if (ProgConfig.SYSTEM_PATH_FFMPEG.get().isEmpty()) {
            ProgConfig.SYSTEM_PATH_FFMPEG.set(orgPath);
        }

        Path path = Paths.get(ProgConfig.SYSTEM_PATH_FFMPEG.get());
        if (!ProgConfig.SYSTEM_PATH_FFMPEG.get().isEmpty() && Files.exists(path)) {
            return ProgConfig.SYSTEM_PATH_FFMPEG.get();
        }

        // falls der Pfad geändert wurde
        path = Paths.get(orgPath);
        if (!orgPath.isEmpty() && Files.exists(path)) {
            ProgConfig.SYSTEM_PATH_FFMPEG.set(orgPath);
            return ProgConfig.SYSTEM_PATH_FFMPEG.get();
        }

        ProgConfig.SYSTEM_PATH_FFMPEG.setValue(P2DialogFileChooser.showFileChooser(stage, "ffmpeg",
                "ffmpeg wird nicht gefunden.", "Bitte den Pfad zu" + P2LibConst.LINE_SEPARATOR +
                        "ffmpeg angeben.", false));
        return ProgConfig.SYSTEM_PATH_FFMPEG.get();
    }
}
