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
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.Listener;
import de.p2tools.p2Lib.tools.log.Duration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CreateMediaDb implements Runnable {

    private String error = "";
    private boolean more = false;

    private final Daten daten;
    private final String[] suffix;
    private final MediaDbList mediaDbList;
    private final String path;
    private List search = new ArrayList<MediaDbData>();

    final boolean ohneSuffix = Boolean.parseBoolean(Config.MEDIA_DB_WITH_OUT_SUFFIX.get());

    /**
     * duchsucht die vom User angelegten Pfade für die Mediensammlung
     * -> bei jedem Start
     *
     * @param suffix
     * @param mediaDbList
     */
    public CreateMediaDb(String[] suffix, MediaDbList mediaDbList) {
        daten = Daten.getInstance();
        this.suffix = suffix;
        this.path = "";
        this.mediaDbList = mediaDbList;
    }

    /**
     * durchsucht einen EXTERNEN Pfad
     * -> wird nur manuel vom User gestartet und löscht nicht die MediaDB
     *
     * @param suffix
     * @param path
     * @param mediaDbList
     */
    public CreateMediaDb(String[] suffix, String path, MediaDbList mediaDbList) {
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
                // die gesamte MediaDB laden: gespeichert und lokale Filme
                search.addAll(loadSavedList());

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

                mediaDbList.setAll(search);

            } else {
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
                mediaDbList.addAll(search);
                writeList(search);
            }

        } catch (final Exception ex) {
            PLog.errorLog(120321254, ex);
        }

        Duration.counterStop("Mediensammlung erstellen");
        mediaDbList.setPropSearch(false);
        Listener.notify(Listener.EREIGNIS_MEDIA_DB_STOP, MediaDbList.class.getSimpleName());
    }

    private List<MediaDbData> loadSavedList() {
        final Path urlPath = getFilePath();
        return new ReadMediaDb(daten).read(urlPath);
    }

    private Path getFilePath() {
        Path urlPath = null;
        try {
            urlPath = Paths.get(ProgInfos.getSettingsDirectory_String()).resolve(Const.FILE_MEDIA_DB);
            if (Files.notExists(urlPath)) {
                urlPath = Files.createFile(urlPath);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        return urlPath;
    }

    private synchronized void writeList(List<MediaDbData> data) {
        final Path path = getFilePath();

        ArrayList<String> list = new ArrayList<>();
        list.add("MediaDB schreiben (" + daten.mediaDbList.size() + " Dateien) :");
        list.add("   --> Start Schreiben nach: " + path.toString());

        try {
            final File file = path.toFile();
            final File dir = new File(file.getParent());
            if (!dir.exists() && !dir.mkdirs()) {
                PLog.errorLog(932102478, "Kann den Pfad nicht anlegen: " + dir.toString());
                Platform.runLater(() -> new MTAlert().showErrorAlert("Fehler beim Schreiben",
                        "Der Pfad zum Schreiben der Mediensammlung kann nicht angelegt werden: \n" +
                                path.toString()));
                return;
            }

            new WriteMediaDb().write(path, data);
            list.add("   --> geschrieben!");

        } catch (final Exception ex) {
            list.add("   --> Fehler, nicht geschrieben!");
            PLog.errorLog(931201478, ex, "nach: " + path.toString());
            Platform.runLater(() -> new MTAlert().showErrorAlert("Fehler beim Schreiben",
                    "Die Mediensammlung konnte nicht geschrieben werden:\n" +
                            path.toString()));
        }

        PLog.userLog(list);
    }

    private void errorMsg() {
        Platform.runLater(() -> new MTAlert().showErrorAlert("Fehler beim Erstellen der Mediensammlung",
                (more ? "Die Pfade der Mediensammlung können nicht alle gelesen werden:\n"
                        : "Der Pfad der Mediensammlung kann nicht gelesen werden:\n") + error));
    }

    private void searchFile(File dir, boolean extern) {
        if (dir == null) {
            return;
        }
        final File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory()) {
                    searchFile(file, extern);
                } else if (checkSuffix(suffix, file.getName())) {
                    search.add(new MediaDbData(file.getName(), file.getParent().intern(), file.length(), extern));
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
