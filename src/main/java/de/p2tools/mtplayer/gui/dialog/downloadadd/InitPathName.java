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


package de.p2tools.mtplayer.gui.dialog.downloadadd;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.p2lib.mtfilm.tools.FileNameUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InitPathName {
    private final ObservableList<String> pathList;
    private final AddDto addDto;

    public InitPathName(AddDto addDto) {
        this.addDto = addDto;
        this.pathList = FXCollections.observableArrayList();
        this.pathList.addAll(ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH);
        init();
    }

    private void init() {
        // PathList aufbauen
        if (pathList.isEmpty() ||
                pathList.size() == 1 && pathList.get(0).isEmpty()) {
            //leer oder und nur ein leerer Eintrag
            pathList.clear();
            String path;
            if (addDto.getAct().setData.getDestPath().isEmpty()) {
                path = System.getProperty("user.home");
            } else {
                path = addDto.getAct().setData.getDestPath();
            }
            pathList.add(path);
        }

        Arrays.stream(addDto.downloadAddData).toList().forEach(downloadAddData -> {
            if (downloadAddData.path.isEmpty()) {
                setPath(pathList.get(0), false);
            }
            if (!pathList.contains(downloadAddData.path)) {
                pathList.add(downloadAddData.path);
            }
        });

        addDto.cboPath.setEditable(true);
        addDto.cboPath.setItems(pathList);
        addDto.chkPathAll.setOnAction(a -> {
            if (addDto.chkPathAll.isSelected()) {
                pathChanged();
            }
        });

        // Dateiname
        Arrays.stream(addDto.downloadAddData).toList().forEach(downloadAddData -> {
            if (downloadAddData.name.isEmpty()) {
                if (!downloadAddData.setData.getDestName().isEmpty()) {
                    addDto.txtName.setText(downloadAddData.setData.getDestName());
                } else {
                    addDto.txtName.setText(downloadAddData.download.getFilm().getTitle());
                }
            }
        });

        makeAct();
        addDto.cboPath.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!addDto.cboPath.isFocused()) {
                System.out.println("no focus");
                return;
            }
            if (oldValue != null && newValue != null && !oldValue.equals(newValue)) {
                pathChanged();
            }
        });
        addDto.txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!addDto.txtName.isFocused()) {
                System.out.println("no focus");
                return;
            }
            if (oldValue != null && newValue != null && !oldValue.equals(newValue)) {
                nameChanged();
            }
        });
    }

    private void nameChanged() {
        addDto.getAct().name = addDto.txtName.getText();
        if (!addDto.txtName.getText().equals(FileNameUtils.checkFileName(addDto.txtName.getText(), false /* pfad */))) {
            addDto.txtName.setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
        } else {
            addDto.txtName.setStyle("");
        }
        InitProgramCall.setProgrammCall(addDto, addDto.getAct());
    }

    private void pathChanged() {
        // beim Ändern der cbo oder manuellem Eintragen
        final String s = addDto.cboPath.getEditor().getText();
        System.out.println("s: " + s);
        if (!addDto.cboPath.getItems().contains(s)) {
            addDto.cboPath.getItems().add(s);
        }
        setPath(s, addDto.chkPathAll.isSelected());
        DownloadAddDialogFactory.calculateAndCheckDiskSpace(s, addDto.lblFree, addDto.getAct());
    }

    private void setPath(String path, boolean all) {
        if (all) {
            Arrays.stream(addDto.downloadAddData).forEach(downloadAddData -> {
                downloadAddData.path = path;
                InitProgramCall.setProgrammCall(addDto, downloadAddData);
            });
        } else {
            addDto.getAct().path = path;
            InitProgramCall.setProgrammCall(addDto, addDto.getAct());
        }
    }

    public void makeAct() {
        System.out.println("PATH_ACT");
        // nach dem actFilm setzen, z.B. beim Wechsel
        addDto.cboPath.setDisable(addDto.getAct().downloadIsRunning());
        addDto.txtName.setDisable(addDto.getAct().downloadIsRunning());

        if (!addDto.cboPath.getItems().contains(addDto.getAct().path)) {
            addDto.cboPath.getItems().add(addDto.getAct().path);
        }
        addDto.cboPath.getSelectionModel().select(addDto.getAct().path);
        addDto.txtName.setText(addDto.getAct().name);

        DownloadAddDialogFactory.calculateAndCheckDiskSpace(
                addDto.getAct().path, addDto.lblFree,
                addDto.getAct());
        System.out.println("makeAct-2");
    }

    public void clearPath() {
        pathList.clear();
        ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.clear();
    }

    public void setUsedPaths() {
        // Dialog-Ende: Die verwendeten Pfade einfügen
        List<DownloadAddData> list = new ArrayList<>(Arrays.stream(addDto.downloadAddData).toList());
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
