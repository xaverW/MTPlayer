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

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.gui.tools.Listener;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.tools.log.Duration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateMediaDb implements Runnable {

    private String error = "";
    private boolean more = false;

    private final ProgData progData;
    private final MediaDataList mediaDataList;
    private final String path;
    private final String collection;
    private String[] suffix;
    private List mediaDataArrayList = new ArrayList<MediaData>();

    final boolean withoutSuffix = Boolean.parseBoolean(ProgConfig.MEDIA_DB_WITH_OUT_SUFFIX.get());

    /**
     * duchsucht die vom User angelegten Pfade für die interne Mediensammlung
     * -> bei jedem Start
     *
     * @param mediaDataList
     */
    public CreateMediaDb(MediaDataList mediaDataList) {
        progData = ProgData.getInstance();
        this.mediaDataList = mediaDataList;
        this.path = "";
        this.collection = "";
        getSuffix();
    }

    /**
     * durchsucht einen EXTERNEN Pfad
     * -> wird nur manuell vom User gestartet und löscht nicht die MediaDB
     *
     * @param path
     * @param mediaDataList
     */
    public CreateMediaDb(MediaDataList mediaDataList, String path, String collection) {
        progData = ProgData.getInstance();
        this.mediaDataList = mediaDataList;
        this.path = path;
        this.collection = collection;
        getSuffix();
    }

    @Override
    public synchronized void run() {

        Duration.counterStart("Mediensammlung erstellen");
        mediaDataList.setSearching(true);
        Listener.notify(Listener.EREIGNIS_MEDIA_DB_START, MediaDataList.class.getSimpleName());

        //todo mediaPathDataList erst mal putzen: gleiche, leere, ..
        try {

            if (path.isEmpty()) {
                // ===================================
                // die gesamte MediaDB laden: gespeichert und lokale Filme
                mediaDataArrayList.addAll(mediaDataList.loadSavedExternalMediaData());

                // und die Pfade "putzen"
                progData.mediaPathDataList.cleanUpMediaPathData();

                for (final MediaPathData mediaPathData : progData.mediaPathDataList.getInternalMediaPathDataList()) {
                    final File f = new File(mediaPathData.getPath());
                    if (!f.canRead()) {
                        if (!error.isEmpty()) {
                            error = error + PConst.LINE_SEPARATOR;
                            more = true;
                        }
                        error = error + f.getPath();
                    }
                }
                if (!error.isEmpty()) {
                    // Verzeichnisse können nicht durchsucht werden
                    errorMsg();
                }
                progData.mediaPathDataList.getInternalMediaPathDataList().stream().forEach((mediaPathData) ->
                        searchFile(new File(mediaPathData.getPath()), false));

                mediaDataList.setAll(mediaDataArrayList);
                mediaDataList.checkExternalMediaData();

            } else {
                // ===================================
                // dann nur einen Pfad hinzufügen
                final File f = new File(path);
                if (!f.canRead()) {
                    if (!error.isEmpty()) {
                        error = error + PConst.LINE_SEPARATOR;
                    }
                    error = error + f.getPath();
                }
                if (!error.isEmpty()) {
                    // Verzeichnisse können nicht durchsucht werden
                    errorMsg();
                }
                searchFile(new File(path), true);

                progData.mediaPathDataList.addExternalMediaPathData(collection, path);
                mediaDataList.addAll(mediaDataArrayList);
                mediaDataList.checkExternalMediaData();
                mediaDataList.writeExternalMediaData();
            }

        } catch (final Exception ex) {
            PLog.errorLog(120321254, ex);
        }

        mediaDataList.setSearching(false);
        Listener.notify(Listener.EREIGNIS_MEDIA_DB_STOP, MediaDataList.class.getSimpleName());
        Duration.counterStop("Mediensammlung erstellen");
    }


    private void errorMsg() {
        Platform.runLater(() -> PAlert.showErrorAlert("Fehler beim Erstellen der Mediensammlung",
                (more ? "Die Pfade der Mediensammlung können nicht alle gelesen werden:" + PConst.LINE_SEPARATOR
                        : "Der Pfad der Mediensammlung kann nicht gelesen werden:" + PConst.LINE_SEPARATOR) + error));
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
                    mediaDataArrayList.add(new MediaData(file.getName(), file.getParent().intern(),
                            file.length(), collection.intern(), external));
                }
            }
        }
    }

    private void getSuffix() {
        suffix = ProgConfig.MEDIA_DB_SUFFIX.get().split(",");
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
