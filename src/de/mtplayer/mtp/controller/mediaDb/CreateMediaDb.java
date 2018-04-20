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

package de.mtplayer.mtp.controller.mediaDb;

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

public class CreateMediaDb implements Runnable {

    private String error = "";
    private boolean more = false;

    private final Daten daten;
    private final MediaList mediaList;
    private final String path;
    private final String collection;
    private String[] suffix;
    private List search = new ArrayList<MediaData>();

    final boolean withoutSuffix = Boolean.parseBoolean(Config.MEDIA_DB_WITH_OUT_SUFFIX.get());

    /**
     * duchsucht die vom User angelegten Pfade für die Mediensammlung
     * -> bei jedem Start
     *
     * @param mediaList
     */
    public CreateMediaDb(MediaList mediaList) {
        daten = Daten.getInstance();
        this.mediaList = mediaList;
        this.path = "";
        this.collection = "";
        getSuffix();
    }

    /**
     * durchsucht einen EXTERNEN Pfad
     * -> wird nur manuell vom User gestartet und löscht nicht die MediaDB
     *
     * @param path
     * @param mediaList
     */
    public CreateMediaDb(MediaList mediaList, String path, String collection) {
        daten = Daten.getInstance();
        this.mediaList = mediaList;
        this.path = path;
        this.collection = collection;
        getSuffix();
    }

    @Override
    public synchronized void run() {

        Duration.counterStart("Mediensammlung erstellen");
        mediaList.setPropSearch(true);
        Listener.notify(Listener.EREIGNIS_MEDIA_DB_START, MediaList.class.getSimpleName());

        try {

            if (path.isEmpty()) {
                // ===================================
                // die gesamte MediaDB laden: gespeichert und lokale Filme
                search.addAll(MediaDb.loadSavedList());

                for (final MediaPathData mediaPathData : daten.mediaPathList.getInternalList()) {
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
                daten.mediaPathList.getInternalList().stream().forEach((mp) ->
                        searchFile(new File(mp.getPath()), false));

                mediaList.setAll(search);
                mediaList.checkExternalMediaData();

            } else {
                // ===================================
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

                daten.mediaPathList.addExternal(collection, path);
                mediaList.addAll(search);
                mediaList.checkExternalMediaData();
                MediaDb.writeList(mediaList);
            }

        } catch (final Exception ex) {
            PLog.errorLog(120321254, ex);
        }

        mediaList.setPropSearch(false);
        Listener.notify(Listener.EREIGNIS_MEDIA_DB_STOP, MediaList.class.getSimpleName());
        Duration.counterStop("Mediensammlung erstellen");
    }


    private void errorMsg() {
        Platform.runLater(() -> new MTAlert().showErrorAlert("Fehler beim Erstellen der Mediensammlung",
                (more ? "Die Pfade der Mediensammlung können nicht alle gelesen werden:\n"
                        : "Der Pfad der Mediensammlung kann nicht gelesen werden:\n") + error));
    }

    private void searchFile(File dir, boolean external) {
        if (dir == null) {
            return;
        }
        final File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory()) {
                    searchFile(file, external);
                } else if (checkSuffix(suffix, file.getName())) {
                    search.add(new MediaData(file.getName(), file.getParent().intern(),
                            file.length(), collection.intern(), external));
                }
            }
        }
    }

    private void getSuffix() {
        suffix = Config.MEDIA_DB_SUFFIX.get().split(",");
        for (int i = 0; i < suffix.length; ++i) {
            suffix[i] = suffix[i].toLowerCase();
            if (!suffix[i].isEmpty() && !suffix[i].startsWith(".")) {
                suffix[i] = '.' + suffix[i];
            }
        }
    }

    private boolean checkSuffix(String[] str, String url) {
        // liefert TRUE wenn die Datei in die Mediensammlung kommt
        // prüfen ob url mit einem Argument in str endet
        // wenn str leer dann true
        if (str.length == 1 && str[0].isEmpty()) {
            return true;
        }

        boolean ret = true;
        final String urlLowerCase = url.toLowerCase();
        for (final String s : str) {
            //Suffix prüfen
            if (withoutSuffix) {
                if (urlLowerCase.endsWith(s)) {
                    ret = false;
                    break;
                }
            } else {
                ret = false;
                if (urlLowerCase.endsWith(s)) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }
}
