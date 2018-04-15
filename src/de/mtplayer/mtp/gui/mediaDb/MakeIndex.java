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

package de.mtplayer.mtp.gui.mediaDb;

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.Listener;
import de.p2tools.p2Lib.tools.log.Duration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MakeIndex implements Runnable {

    private String error = "";
    private boolean more = false;

    private final Daten daten;
    private final String[] suffix;
    private final MediaDbList mediaDbList;
    private final String path;
    private List search = new ArrayList<MediaDbData>();

    final boolean ohneSuffix = Boolean.parseBoolean(Config.MEDIA_DB_WITH_OUT_SUFFIX.get());

    public MakeIndex(String[] suffix, MediaDbList mediaDbList) {
        daten = Daten.getInstance();
        this.suffix = suffix;
        this.path = "";
        this.mediaDbList = mediaDbList;
    }

    public MakeIndex(String[] suffix, String path, MediaDbList mediaDbList) {
        daten = Daten.getInstance();
        this.suffix = suffix;
        this.path = path;
        this.mediaDbList = mediaDbList;
    }


    @Override
    public synchronized void run() {
        Duration.counterStart("Mediensammlung erstellen");
        try {

            if (path.isEmpty()) {
                for (final MediaPathData mediaPathData : daten.mediaPathList) {
                    if (mediaPathData.isExtern()) {
                        continue;
                    }
                    final File f = new File(mediaPathData.getPath());
                    if (!f.canRead()) {
                        if (!error.isEmpty()) {
                            error = error + '\n';
                            more = true;
                        }
                        error = error + f.getPath();
                    }
                }
                if (!error.isEmpty()) {
                    // Verzeichnisse können nicht durchsucht werden
                    errorMsg();
                }
                daten.mediaPathList.stream().filter((mediaPathData) -> (!mediaPathData.isExtern())).forEach((mp) ->
                        searchFile(new File(mp.getPath()), false));

            } else if (!daten.mediaPathList.isEmpty()) {
                // dann nur einen Pfad hinzufügen
                final File f = new File(path);
                if (!f.canRead()) {
                    if (!error.isEmpty()) {
                        error = error + '\n';
                    }
                    error = error + f.getPath();
                }
                if (!error.isEmpty()) {
                    // Verzeichnisse können nicht durchsucht werden
                    errorMsg();
                }
                searchFile(new File(path), true);
            }

        } catch (final Exception ex) {
            PLog.errorLog(120321254, ex);
        }

        mediaDbList.setAll(search);
        mediaDbList.exportList("");

        Duration.counterStop("Mediensammlung erstellen");
        mediaDbList.setPropSearch(false);
        Listener.notify(Listener.EREIGNIS_MEDIA_DB_STOP, MediaDbList.class.getSimpleName());
    }

    private void errorMsg() {
        Platform.runLater(() -> new MTAlert().showErrorAlert("Fehler beim Erstellen der Mediensammlung",
                (more ? "Die Pfade der Mediensammlung können nicht alle gelesen werden:\n"
                        : "Der Pfad der Mediensammlung kann nicht gelesen werden:\n") + error));
    }

    private void searchFile(File dir, boolean save) {
        if (dir == null) {
            return;
        }
        final File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory()) {
                    searchFile(file, save);
                } else if (checkSuffix(suffix, file.getName())) {
                    search.add(new MediaDbData(file.getName(), file.getParent().intern(), file.length(), save));
                }
            }
        }
    }

    private boolean checkSuffix(String[] str, String uurl) {
        // liefert TRUE wenn die Datei in die Mediensammlung kommt
        // prüfen ob url mit einem Argument in str endet
        // wenn str leer dann true
        if (str.length == 1 && str[0].isEmpty()) {
            return true;
        }

        boolean ret = true;
        final String url = uurl.toLowerCase();
        for (final String s : str) {
            //Suffix prüfen
            if (ohneSuffix) {
                if (url.endsWith(s)) {
                    ret = false;
                    break;
                }
            } else {
                ret = false;
                if (url.endsWith(s)) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }
}
