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

package de.mtplayer.mtp.tools.file;

import de.mtplayer.mLib.tools.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author emil
 */
public class GetFile {

    public static final String PFAD_PSET_LINUX = "/de/mtplayer/mtp/tools/file/pset_linux.xml";
    public static final String PFAD_PSET_WINDOWS = "/de/mtplayer/mtp/tools/file/pset_windows.xml";
    public static final String PFAD_HILFETEXT_PRGRAMME = "/de/mtplayer/mtp/tools/file/hilfetext_pset.txt";
    public static final String PFAD_HILFETEXT_EDIT_DOWNLOAD_PROG = "hilfetext_editDownloadProg.txt";
    public static final String PFAD_HILFETEXT_RESET = "hilfetext_reset.txt";

    public String getHilfeSuchen(String pfad) {
        String ret = "";
        try (InputStreamReader in = new InputStreamReader(getClass().getResource(pfad).openStream(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(in)) {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                ret = ret + '\n' + strLine;
            }
        } catch (final IOException ex) {
            Log.errorLog(885692213, ex);
        }
        return ret;
    }

    public InputStreamReader getPsetVorlageLinux() {
        try {
            return new InputStreamReader(getClass().getResource(PFAD_PSET_LINUX).openStream(), StandardCharsets.UTF_8);
        } catch (final IOException ex) {
            Log.errorLog(469691002, ex);
        }
        return null;
    }

    public InputStreamReader getPsetVorlageWindows() {
        try {
            return new InputStreamReader(getClass().getResource(PFAD_PSET_WINDOWS).openStream(), StandardCharsets.UTF_8);
        } catch (final IOException ex) {
            Log.errorLog(842306087, ex);
        }
        return null;
    }
}
