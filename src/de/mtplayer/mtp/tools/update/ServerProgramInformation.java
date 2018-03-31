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

package de.mtplayer.mtp.tools.update;

import de.mtplayer.mLib.tools.Log;

/**
 * Encapsulates the retrieved update information.
 */
class ServerProgramInformation {
    private int version;
    private String releaseNotes;
    private String updateUrl;

    public int getVersion() {
        return version;
    }

    public void setVersion(String version) {
        try {
            this.version = Integer.parseInt(version);
        } catch (NumberFormatException ex) {
            Log.errorLog(12344564, ex, "Fehler beim Parsen der Version '" + version + "'.");
            this.version = -1;
        }
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String release) {
        releaseNotes = release;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    /**
     * Tag definition for server response file
     */
    class ParserTags {
        final static String VERSION = "Program_Version";
        final static String RELEASE_NOTES = "Program_Release_Info";
        final static String UPDATE_URL = "Download_Programm";
        final static String INFO = "Info";
        final static String INFO_NO = "number";
    }
}
