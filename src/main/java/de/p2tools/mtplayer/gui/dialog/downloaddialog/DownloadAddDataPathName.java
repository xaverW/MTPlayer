/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.gui.dialog.downloaddialog;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.p2lib.mtfilm.tools.FileNameUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DownloadAddDataPathName {
    private final ComboBox<String> cboPath;
    private final TextField txtName;
    private final CheckBox chkPathAll;
    private final Label lblFree;
    private final DownloadAddData[] downloadAddInfosArr;
    private final ObservableList<String> pathList;

    public DownloadAddDataPathName(ComboBox<String> cboPath, TextField txtName,
                                   CheckBox chkPathAll, Label lblFree, DownloadAddData[] downloadAddInfosArr) {

        this.cboPath = cboPath;
        this.txtName = txtName;
        this.chkPathAll = chkPathAll;
        this.lblFree = lblFree;
        this.downloadAddInfosArr = downloadAddInfosArr;
        this.pathList = FXCollections.observableArrayList();
        this.pathList.addAll(ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH);
    }

    public void initPathAndName(int actFilmIsShown) {
        // cboPath einrichten
        if (pathList.isEmpty() ||
                pathList.size() == 1 && pathList.get(0).isEmpty()) {
            //leer oder und nur ein leerer Eintrag
            pathList.clear();
            String path;
            if (downloadAddInfosArr[actFilmIsShown].setData.getDestPath().isEmpty()) {
                path = System.getProperty("user.home");
            } else {
                path = downloadAddInfosArr[actFilmIsShown].setData.getDestPath();
            }
            pathList.add(path);
        }

        Arrays.stream(downloadAddInfosArr).toList().forEach(downloadAddData -> {
            if (downloadAddData.path.isEmpty()) {
                downloadAddData.setPath(pathList.get(0), false);
            }
            if (!pathList.contains(downloadAddData.path)) {
                pathList.add(downloadAddData.path);
            }
        });

        cboPath.setEditable(true);
        cboPath.setItems(pathList);

        // Dateiname
        Arrays.stream(downloadAddInfosArr).toList().forEach(f -> {
            if (f.name.isEmpty()) {
                if (!f.setData.getDestName().isEmpty()) {
                    txtName.setText(f.setData.getDestName());
                } else {
                    txtName.setText(f.film.getTitle());
                }
            }
        });

        makeActPathName(actFilmIsShown);
    }

    public void nameChanged(int actFilmIsShown) {
        downloadAddInfosArr[actFilmIsShown].setName(txtName.getText());
        if (!txtName.getText().equals(FileNameUtils.checkFileName(txtName.getText(), false /* pfad */))) {
            txtName.setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
        } else {
            txtName.setStyle("");
        }
    }

    public void pathChanged(int actFilmIsShown, String actValue) {
        // beim Ändern der cbo oder manuellem Eintragen
        final String s = cboPath.getValue();
        if (!cboPath.getItems().contains(s)) {
            cboPath.getItems().add(s);
        }
        downloadAddInfosArr[actFilmIsShown].setPath(s, chkPathAll.isSelected());
        DownloadAddDialogFactory.calculateAndCheckDiskSpace(s, lblFree, downloadAddInfosArr[actFilmIsShown]);
    }

    public void makeActPathName(int actFilmIsShown) {
        // nach dem actFilm setzen, z.B. beim Wechsel
        if (!cboPath.getItems().contains(downloadAddInfosArr[actFilmIsShown].path)) {
            cboPath.getItems().add(downloadAddInfosArr[actFilmIsShown].path);
        }
        cboPath.getSelectionModel().select(downloadAddInfosArr[actFilmIsShown].path);
        txtName.setText(downloadAddInfosArr[actFilmIsShown].name);

        DownloadAddDialogFactory.calculateAndCheckDiskSpace(downloadAddInfosArr[actFilmIsShown].path, lblFree, downloadAddInfosArr[actFilmIsShown]);
    }

    public void clearPath() {
        pathList.clear();
        ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.clear();
    }

    public void setUsedPaths() {
        // Dialog-Ende: Die verwendeten Pfade oben einfügen
        List<DownloadAddData> list = new ArrayList<>(Arrays.stream(downloadAddInfosArr).toList());
        Collections.reverse(list);
        list.forEach(downloadAddData -> ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.add(0, downloadAddData.path));

        // doppelte aussortieren
        final ArrayList<String> tmpPathList = new ArrayList<>();
        for (String s : ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH) {
            if (s.endsWith(File.separator)) {
                s = s.substring(0, s.length() - 1);
            }
            if (!tmpPathList.contains(s)) {
                tmpPathList.add(s);
            }

            // und die Anzahl der Einträge begrenzen
            if (tmpPathList.size() >= ProgConst.MAX_DEST_PATH_IN_DIALOG_DOWNLOAD) {
                break;
            }
        }

        // und jetzt wieder eintragen
        ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.clear();
        ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.addAll(tmpPathList);
    }
}
