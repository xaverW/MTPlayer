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

package de.p2tools.mtplayer.controller.mediadb;

import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateMediaDb {

    private String error = "";
    private boolean more = false;

    private final ProgData progData;
    private final MediaDataList mediaDataList;
    private String[] suffix;
    private final ArrayList<String> logs = new ArrayList<>();

    final boolean withoutSuffix = ProgConfig.MEDIA_DB_WITH_OUT_SUFFIX.getValue();
    final boolean noHiddenFiles = ProgConfig.MEDIA_DB_NO_HIDDEN_FILES.getValue();
    final int fileSize = ProgConfig.MEDIA_DB_FILE_SIZE_MBYTE.getValue() * 1000 * 1000; //sind BYTE
    final boolean checkFileSize = fileSize > ProgConst.MEDIA_COLLECTION_FILESIZE_ALL_FILES;

    public CreateMediaDb() {
        progData = ProgData.getInstance();
        this.mediaDataList = progData.mediaDataList;
        getSuffix();
    }

    public void createDB(MediaCollectionData mediaCollectionData) {
        if (mediaCollectionData == null) {
            // durchsucht die vom User angelegten Pfade für die interne Mediensammlung
            // und fügt die gespeicherten externen Medien hinzu
            create();
        } else {
            // durchsucht einen Pfad und fügt ihn an die DB an
            // wird nur manuell vom User gestartet und löscht nicht die MediaDB
            create(mediaCollectionData);
        }
    }

    private void create() {
        // durchsucht die vom User angelegten Pfade für die interne Mediensammlung
        // und fügt die gespeicherten externen Medien hinzu,
        // bei jedem Programmstart oder manuell vom User gestartet
        List<MediaData> tmpMediaDataList = new ArrayList<>();
        start();
        try {
            // ===================================
            // die gesamte MediaDB laden: gespeicherte Liste abarbeiten
            // und lokale Filme anfügen
            logs.add("Gesamte Mediensammlung aufbauen");

            // jetzt erst mal die gespeicherten EXTERNEN laden
            tmpMediaDataList.addAll(new ReadMediaDb().loadSavedExternalMediaData());

            // und die Pfade "putzen" und dann auf lesbarkeit prüfen
            progData.mediaCollectionDataList.cleanUpInternalMediaCollectionData();
            for (final MediaCollectionData mediaCollectionData : progData.mediaCollectionDataList.getMediaCollectionDataList(false)) {
                final File f = new File(mediaCollectionData.getPath());
                if (!f.canRead()) {
                    if (!error.isEmpty()) {
                        error = error + P2LibConst.LINE_SEPARATOR;
                        more = true;
                    }
                    error = error + f.getPath();
                }
            }
            if (!error.isEmpty()) {
                // Verzeichnisse können nicht durchsucht werden
                errorMsg();
            }

            // und jetzt abarbeiten
            progData.mediaCollectionDataList.getMediaCollectionDataList(false).forEach((mediaCollectionData) -> {
                if (mediaCollectionData.getCollectionName().isEmpty()) {
                    final String name = progData.mediaCollectionDataList.getNextMediaCollectionName(false);
                    mediaCollectionData.setCollectionName(name);
                }
                // und alle Medien dieses Pfades suchen
                searchFile(new File(mediaCollectionData.getPath()), mediaCollectionData.getIdInt(), tmpMediaDataList);
            });

            logs.add(" -> gefundene Medien: " + tmpMediaDataList.size());
            mediaDataList.setAll(tmpMediaDataList);
            mediaDataList.checkDuplicateMediaData();
            mediaDataList.countMediaData(progData);
        } catch (final Exception ex) {
            PLog.errorLog(945120375, ex);
        }
        stop();
    }

    private void create(MediaCollectionData mediaCollectionData) {
        // durchsucht einen Pfad und fügt ihn an die DB an
        // wird nur manuell vom User gestartet und löscht nicht die MediaDB
        if (mediaCollectionData == null) {
            return;
        }
        try {
            final List<MediaData> tmpMediaDataList = new ArrayList<>();
            start();
            // ===================================
            // dann nur einen Pfad hinzufügen
            final File f = new File(mediaCollectionData.getPath());
            logs.add("externen Pfad absuchen: " + f.getAbsolutePath());
            if (!f.canRead()) {
                if (!error.isEmpty()) {
                    error = error + P2LibConst.LINE_SEPARATOR;
                }
                error = error + f.getPath();
            }
            if (!error.isEmpty()) {
                // Verzeichnisse können nicht durchsucht werden
                errorMsg();
            }

            // und jetzt alle Medien dieses Pfades suchen
            searchFile(new File(mediaCollectionData.getPath()), mediaCollectionData.getIdInt(), tmpMediaDataList);
            logs.add(" -> im Pfad gefundene Medien: " + tmpMediaDataList.size());
            mediaDataList.addAll(tmpMediaDataList);
            mediaDataList.checkDuplicateMediaData();
            mediaDataList.countMediaData(progData);
            if (mediaCollectionData.isExternal()) {
                // nur externe müssen geschrieben werden
                new WriteMediaDb(progData).writeExternalMediaData(logs);
            }
        } catch (final Exception ex) {
            PLog.errorLog(120321254, ex);
        }
        stop();
    }

    private void start() {
        PDuration.counterStart("createDB");
        mediaDataList.setStopSearching(false);
        mediaDataList.setSearching(true);
        PListener.notify(PListener.EVENT_MEDIA_DB_START, CreateMediaDb.class.getSimpleName());
    }

    private void stop() {
        mediaDataList.setSearching(false);
        PLog.sysLog(logs);
        PListener.notify(PListener.EVENT_MEDIA_DB_STOP, CreateMediaDb.class.getSimpleName());
        PDuration.counterStop("createDB");
    }

    private void errorMsg() {
        Platform.runLater(() -> PAlert.showErrorAlert("Fehler beim Erstellen der Mediensammlung",
                (more ? "Die Pfade der Mediensammlung können nicht alle gelesen werden:" + P2LibConst.LINE_SEPARATOR
                        : "Der Pfad der Mediensammlung kann nicht gelesen werden:" + P2LibConst.LINE_SEPARATOR) + error));
    }

    private void searchFile(File dir, int collectionIdLong, List<MediaData> tmpMediaDataList) {
        if (mediaDataList.isStopSearching()) {
            // dann wurde es vom User abgebrochen
            return;
        }
        if (dir == null) {
            return;
        }

        final File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory()) {
                    searchFile(file, collectionIdLong, tmpMediaDataList);
                } else {
                    if (checkFileSize && file.length() < fileSize) {
                        continue;
                    }
                    if (noHiddenFiles && file.isHidden()) {
                        continue;
                    }
                    if (!checkSuffix(suffix, file.getName())) {
                        continue;
                    }
                    tmpMediaDataList.add(new MediaData(file.getName(), file.getParent().intern(),
                            file.length(), collectionIdLong));
                }
            }
        }
    }

    private void getSuffix() {
        String[] arr = ProgConfig.MEDIA_DB_SUFFIX.get().split(",");
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < arr.length; ++i) {
            String str = arr[i].trim().toLowerCase();
            if (str.isEmpty()) {
                continue;
            }

            if (!str.startsWith(".")) {
                str = "." + str;
            }

            list.add(str);
        }
        suffix = list.toArray(new String[]{});
    }

    private boolean checkSuffix(String[] str, String url) {
        // liefert TRUE wenn die Datei in die Mediensammlung kommt
        // prüfen ob url mit einem Argument in str endet
        // wenn str leer dann true
        if (str.length == 0) {
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
