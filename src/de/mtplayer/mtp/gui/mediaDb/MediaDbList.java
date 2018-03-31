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

import de.mtplayer.mLib.tools.Duration;
import de.mtplayer.mLib.tools.Functions;
import de.mtplayer.mLib.tools.Log;
import de.mtplayer.mLib.tools.SysMsg;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.Listener;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class MediaDbList extends SimpleListProperty<MediaDbData> {

    public final static String TRENNER = "  |###|  ";
    public final String FILE_SEPERATOR_MEDIA_PATH = "<>";
    private boolean makeIndex = false;
    private String[] suffix = {""};

    private final Daten daten;
    private FilteredList<MediaDbData> filteredList = null;
    private SortedList<MediaDbData> sortedList = null;

    private BooleanProperty propSearch = new SimpleBooleanProperty(false);


    public MediaDbList(Daten aDaten) {
        super(FXCollections.observableArrayList());
        daten = aDaten;
    }

    public boolean isPropSearch() {
        return propSearch.get();
    }

    public BooleanProperty propSearchProperty() {
        return propSearch;
    }

    public void setPropSearch(boolean propSearch) {
        this.propSearch.set(propSearch);
    }

    public SortedList<MediaDbData> getSortedList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<MediaDbData>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<MediaDbData> getFilteredList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<MediaDbData>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return filteredList;
    }

    public synchronized void filterdListSetPred(Predicate<MediaDbData> predicate) {
        filteredList.setPredicate(predicate);
    }

    public synchronized void filterdListClearPred() {
        filteredList.setPredicate(p -> false);
    }

    public synchronized void filterdListClearPred(boolean pred) {
        filteredList.setPredicate(p -> pred);
    }

    public synchronized boolean setAll(Collection elements) {
        return super.setAll(elements);
    }

    public synchronized boolean add(MediaDbData mediaDbData) {
        return super.add(mediaDbData);
    }

    private void del(boolean ohneSave) {
        if (ohneSave) {
            List l = this.parallelStream().filter(datenMediaDB -> !datenMediaDB.isExtern()).collect(Collectors.toList());
            this.setAll(l);
        } else {
            clear();
            exportListe("");
        }
    }

    public synchronized void createMediaDB() {
        if (isPropSearch()) {
            // dann mach mers gerade schon :)
            return;
        }

        Duration.counterStart("Mediensammlung erstellen");
        Listener.notify(Listener.EREIGNIS_MEDIA_DB_START, MediaDbList.class.getSimpleName());
        setPropSearch(true);

        suffix = Config.MEDIA_DB_SUFFIX.get().split(",");
        for (int i = 0; i < suffix.length; ++i) {
            suffix[i] = suffix[i].toLowerCase();
            if (!suffix[i].isEmpty() && !suffix[i].startsWith(".")) {
                suffix[i] = '.' + suffix[i];
            }
        }

        del(true /*ohneSave*/);
        new Thread(new MakeIndex(suffix)).start();
    }

    public synchronized void loadSavedList() {
        final Path urlPath = getFilePath();
        //use Automatic Resource Management
        try (LineNumberReader in = new LineNumberReader(Files.newBufferedReader(urlPath))) {
            String zeile;
            while ((zeile = in.readLine()) != null) {
                final MediaDbData mdb = getUrlAusZeile(zeile);
                if (mdb != null) {
                    add(mdb);
                }
            }
        } catch (final Exception ex) {
            Log.errorLog(461203787, ex);
        }
    }

    public synchronized void exportListe(String datei) {
        Path logFilePath = null;
        boolean export = false;
        SysMsg.sysMsg("MediaDB schreiben (" + daten.mediaDbList.size() + " Dateien) :");
        if (!datei.isEmpty()) {
            export = true;
            try {
                final File file = new File(datei);
                final File dir = new File(file.getParent());
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        Log.errorLog(945120365, "Kann den Pfad nicht anlegen: " + dir.toString());
                    }
                }
                SysMsg.sysMsg("   --> Start Schreiben nach: " + datei);
                logFilePath = file.toPath();
            } catch (final Exception ex) {
                Log.errorLog(102035478, ex, "nach: " + datei);
            }
        } else {
            SysMsg.sysMsg("   --> Start Schreiben nach: " + getFilePath().toString());
            logFilePath = getFilePath();
        }

        try (BufferedWriter bw = Files.newBufferedWriter(logFilePath)) {
            bw.newLine();
            bw.newLine();
            for (final MediaDbData entry : this) {
                if (!datei.isEmpty()) {
                    //dann alles schreiben
                    bw.write(getLine(entry, export));
                    bw.newLine();
                } else if (entry.isExtern()) {
                    //in der Konfig nur die externen
                    bw.write(getLine(entry, export));
                    bw.newLine();
                }
            }
            bw.newLine();
            //
            bw.flush();
        } catch (final Exception ex) {
            Platform.runLater(() -> new MTAlert().showErrorAlert("Fehler beim Schreiben",
                    "Datei konnte nicht geschrieben werden!"));
        }
        SysMsg.sysMsg("   --> geschrieben!");
    }

    private String getLine(MediaDbData med, boolean export) {
        if (export) {
            return med.arr[MediaDbData.MEDIA_DB_NAME];
        }
        String ret = "";
        ret += Functions.minTextLaenge(60, med.arr[MediaDbData.MEDIA_DB_NAME]) + TRENNER;
        ret += Functions.minTextLaenge(60, med.arr[MediaDbData.MEDIA_DB_PATH]) + TRENNER;
        ret += med.mVMediaDBFileSize.sizeL + "";
        return ret;
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

    private MediaDbData getUrlAusZeile(String zeile) {
        //02-202.mp3     |###|  /tmp/John Grisham/Das Komplott 1    |###|  3     
        if (zeile.isEmpty()) {
            return null;
        }
        String name = "", pfad = "", s = "";
        long size = 0;
        try {
            if (zeile.contains(TRENNER)) {
                name = zeile.substring(0, zeile.indexOf(TRENNER)).trim();
                pfad = zeile.substring(zeile.indexOf(TRENNER) + TRENNER.length(), zeile.lastIndexOf(TRENNER)).trim();
                s = zeile.substring(zeile.lastIndexOf(TRENNER) + TRENNER.length()).trim();
            }
            if (!s.isEmpty()) {
                try {
                    size = Integer.parseInt(s);
                } catch (final Exception ignore) {
                    size = 0;
                }
            }
            return new MediaDbData(name, pfad, size, true /*extern*/);
        } catch (final Exception ex) {
            Log.errorLog(912035647, ex);
        }
        return null;
    }
}
