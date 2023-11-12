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
import de.p2tools.p2lib.tools.PSystemUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InitPathName {
    private final ObservableList<String> pathList;
    private final AddDownloadDto addDownloadDto;

    public InitPathName(AddDownloadDto addDownloadDto) {
        this.addDownloadDto = addDownloadDto;
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
            if (addDownloadDto.getAct().download.getSetData().getDestPath().isEmpty()) {
                path = System.getProperty("user.home");
            } else {
                path = addDownloadDto.getAct().download.getSetData().getDestPath();
            }
            pathList.add(path);
        }

        Arrays.stream(addDownloadDto.addDownloadData).toList().forEach(downloadAddData -> {
            if (downloadAddData.download.getDestPath().isEmpty()) {
                setPath(pathList.get(0), false);
            }
            if (!pathList.contains(downloadAddData.download.getDestPath())) {
                pathList.add(downloadAddData.download.getDestPath());
            }
        });

        addDownloadDto.cboPath.setEditable(true);
        addDownloadDto.cboPath.setItems(pathList);
        addDownloadDto.chkPathAll.setOnAction(a -> {
            if (addDownloadDto.chkPathAll.isSelected()) {
                pathChanged();
            }
        });

        // Dateiname
        Arrays.stream(addDownloadDto.addDownloadData).toList().forEach(downloadAddData -> {
            if (downloadAddData.download.getDestFileName().isEmpty()) {
                if (!downloadAddData.download.getSetData().getDestName().isEmpty()) {
                    addDownloadDto.txtName.setText(downloadAddData.download.getSetData().getDestName());
                } else {
                    addDownloadDto.txtName.setText(downloadAddData.download.getTitle());
                }
            }
        });

        makeAct();
        addDownloadDto.cboPath.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!addDownloadDto.cboPath.isFocused()) {
                return;
            }
            if (oldValue != null && newValue != null && !oldValue.equals(newValue)) {
                pathChanged();
            }
        });
        addDownloadDto.txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!addDownloadDto.txtName.isFocused()) {
                return;
            }
            if (oldValue != null && newValue != null && !oldValue.equals(newValue)) {
                nameChanged();
            }
        });
    }

    private void nameChanged() {
        addDownloadDto.getAct().download.setFile(addDownloadDto.cboPath.getEditor().getText(), addDownloadDto.txtName.getText());
        if (!addDownloadDto.txtName.getText().equals(FileNameUtils.checkFileName(addDownloadDto.txtName.getText(), false /* pfad */))) {
            addDownloadDto.txtName.setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
        } else {
            addDownloadDto.txtName.setStyle("");
        }
        InitProgramCall.setProgrammCall(addDownloadDto, addDownloadDto.getAct());
    }

    private void pathChanged() {
        // beim Ändern der cbo oder manuellem Eintragen
        final String s = addDownloadDto.cboPath.getEditor().getText();
        if (!addDownloadDto.cboPath.getItems().contains(s)) {
            addDownloadDto.cboPath.getItems().add(s);
        }
        setPath(s, addDownloadDto.chkPathAll.isSelected());
        DownloadAddDialogFactory.calculateAndCheckDiskSpace(s, addDownloadDto.lblFree, addDownloadDto.getAct());
    }

    private void setPath(String path, boolean all) {
        if (all) {
            Arrays.stream(addDownloadDto.addDownloadData).forEach(downloadAddData -> {
                if (!downloadAddData.download.getDestPath().equals(path)) {
                    downloadAddData.download.setFile(path, addDownloadDto.txtName.getText());
                    InitProgramCall.setProgrammCall(addDownloadDto, downloadAddData);
                }
            });
        } else {
            if (!addDownloadDto.getAct().download.getDestPath().equals(path)) {
                addDownloadDto.getAct().download.setFile(path, addDownloadDto.txtName.getText());
                InitProgramCall.setProgrammCall(addDownloadDto, addDownloadDto.getAct());
            }
        }
    }

    public void makeAct() {
        // nach dem actFilm setzen, z.B. beim Wechsel
        addDownloadDto.cboPath.setDisable(addDownloadDto.getAct().downloadIsRunning());
        addDownloadDto.txtName.setDisable(addDownloadDto.getAct().downloadIsRunning());

        if (!addDownloadDto.cboPath.getItems().contains(addDownloadDto.getAct().download.getDestPath())) {
            addDownloadDto.cboPath.getItems().add(addDownloadDto.getAct().download.getDestPath());
        }
        addDownloadDto.cboPath.getSelectionModel().select(addDownloadDto.getAct().download.getDestPath());
        addDownloadDto.txtName.setText(addDownloadDto.getAct().download.getDestFileName());

        DownloadAddDialogFactory.calculateAndCheckDiskSpace(
                addDownloadDto.getAct().download.getDestPath(), addDownloadDto.lblFree,
                addDownloadDto.getAct());
    }

    public void clearPath() {
        pathList.clear();
        ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.clear();
        pathChanged();
    }

    public void proposeDestination() {
        String actPath = addDownloadDto.cboPath.getEditor().getText();
        if (actPath == null) {
            actPath = "";
        }

        String stdPath;
        if (addDownloadDto.getAct().download.getSetData().getDestPath().isEmpty()) {
            stdPath = PSystemUtils.getStandardDownloadPath();
        } else {
            stdPath = addDownloadDto.getAct().download.getSetData().getDestPath();
        }

        actPath = DownloadAddDialogFactory.getNextName(stdPath, actPath, addDownloadDto.getAct().download.getTheme());
        if (!addDownloadDto.cboPath.getItems().contains(actPath)) {
            addDownloadDto.cboPath.getItems().add(actPath);
        }
        addDownloadDto.cboPath.getSelectionModel().select(actPath);
        pathChanged();
    }

    public void setUsedPaths() {
        // Dialog-Ende: Die verwendeten Pfade einfügen
        List<AddDownloadData> list = new ArrayList<>(Arrays.stream(addDownloadDto.addDownloadData).toList());
        Collections.reverse(list);
        list.forEach(downloadAddData -> ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.add(0, downloadAddData.download.getDestPath()));

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
