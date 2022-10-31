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

package de.p2tools.mtplayer.controller.tools;

import de.p2tools.p2Lib.tools.log.PLog;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author emil
 */
public class GetFile {

    public static final String PATH_PSET_LINUX = "/de/p2tools/mtplayer/res/file/pset_linux.xml";
    public static final String PATH_PSET_WINDOWS = "/de/p2tools/mtplayer/res/file/pset_windows.xml";

//    public String getHelpSearch(String path) {
//        String ret = "";
//        try (InputStreamReader in = new InputStreamReader(getClass().getResource(path).openStream(), StandardCharsets.UTF_8);
//             BufferedReader br = new BufferedReader(in)) {
//            String strLine;
//            while ((strLine = br.readLine()) != null) {
//                ret = ret + P2LibConst.LINE_SEPARATOR + strLine;
//            }
//        } catch (final IOException ex) {
//            PLog.errorLog(804154789, ex);
//        }
//        return ret;
//    }

    public InputStreamReader getPsetTamplateLinux() {
        try {
            return new InputStreamReader(getClass().getResource(PATH_PSET_LINUX).openStream(), StandardCharsets.UTF_8);
        } catch (final IOException ex) {
            PLog.errorLog(469691002, ex);
        }
        return null;
    }

    public InputStreamReader getPsetTemplateWindows() {
        try {
            return new InputStreamReader(getClass().getResource(PATH_PSET_WINDOWS).openStream(), StandardCharsets.UTF_8);
        } catch (final IOException ex) {
            PLog.errorLog(842306087, ex);
        }
        return null;
    }
}
