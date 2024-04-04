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


package de.p2tools.mtplayer.gui.dialog.abodialog;

import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Arrays;

public class InitDestination {
    private final AddAboDto addAboDto;

    public InitDestination(AddAboDto addAboDto) {
        this.addAboDto = addAboDto;
        initSetPath();
        initOwnPath();
        initFileName();
    }

    private void initSetPath() {
        addAboDto.rbSetPath.setOnAction(a -> setPathToAbo());
        addAboDto.rbOwnPath.setOnAction(a -> setPathToAbo());
        addAboDto.rbSetFileName.setOnAction(a -> setFileNameToAbo());
        addAboDto.rbOwnFileName.setOnAction(a -> setFileNameToAbo());

        addAboDto.chkDestAboSubDir.setOnAction(event -> {
            if (addAboDto.chkDestAboSubDir.isFocused()) {
                // dann durch den User
                setPathToAbo();
            }
            if (addAboDto.chkDestAboSubDir.isSelected()) {
                // dann einen eigenen Pfad eingeben
                addAboDto.lblSetSubDir.setVisible(false);
                addAboDto.cboDestSetSubDir.setVisible(true);

            } else {
                // Standard des Sets verwenden
                addAboDto.lblSetSubDir.setVisible(true);
                addAboDto.cboDestSetSubDir.setVisible(false);
            }
        });

        ArrayList<String> path = addAboDto.progData.aboList.getAboSubDirList();
        addAboDto.cboDestSetSubDir.setItems(FXCollections.observableArrayList(path));

        addAboDto.cboDestSetSubDir.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!addAboDto.cboDestSetSubDir.isFocused()) {
                return;
            }
            setPathToAbo();
        });
        addAboDto.cboDestSetSubDir.selectionModelProperty().addListener((u, o, n) -> {
            if (!addAboDto.cboDestSetSubDir.isFocused()) {
                return;
            }
            setPathToAbo();
        });
    }

    private void initOwnPath() {
        ArrayList<String> path = addAboDto.progData.aboList.getAboDirList();
        addAboDto.cboDestAboDir.setItems(FXCollections.observableArrayList(path));

        addAboDto.cboDestAboDir.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!addAboDto.cboDestAboDir.isFocused()) {
                return;
            }
            setPathToAbo();
        });
        addAboDto.cboDestAboDir.selectionModelProperty().addListener((u, o, n) -> {
            if (!addAboDto.cboDestAboDir.isFocused()) {
                return;
            }
            setPathToAbo();
        });
    }

    private void initFileName() {
        ArrayList<String> fileName = addAboDto.progData.aboList.getAboFileNameList();
        addAboDto.cboDestAboFileName.setItems(FXCollections.observableArrayList(fileName));

        addAboDto.cboDestAboFileName.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!addAboDto.cboDestAboFileName.isFocused()) {
                return;
            }
            setFileNameToAbo();
        });
        addAboDto.cboDestAboFileName.selectionModelProperty().addListener((u, o, n) -> {
            if (!addAboDto.cboDestAboFileName.isFocused()) {
                return;
            }
            setFileNameToAbo();
        });
    }

    public void setPathToAbo() {
        addMissingPath();
        if (addAboDto.chkDestAboDirAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(this::setAboPath);
        } else {
            setAboPath(addAboDto.getAct());
        }
        setResPath();
    }

    private void setAboPath(AddAboData addAboData) {
        if (addAboDto.rbSetPath.isSelected()) {
            // Pfad aus dem Set
            if (addAboDto.chkDestAboSubDir.isSelected()) {
                addAboData.abo.setAboSubDir(addAboDto.cboDestSetSubDir.getEditor().getText());
            } else {
                addAboData.abo.setAboSubDir("");
            }
            addAboData.abo.setAboDir("");
        } else {
            addAboData.abo.setAboDir(addAboDto.cboDestAboDir.getEditor().getText());
        }
    }

    public void setFileNameToAbo() {
        addMissingPath();
        if (addAboDto.chkDestAboFileNameAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(this::setAboFileName);
        } else {
            setAboFileName(addAboDto.getAct());
        }
        setResPath();
    }

    private void setAboFileName(AddAboData addAboData) {
        if (addAboDto.rbSetFileName.isSelected()) {
            addAboData.abo.setAboFileName("");
        } else {
            addAboData.abo.setAboFileName(addAboDto.cboDestAboFileName.getEditor().getText());
        }
    }

    private void addMissingPath() {
        final String sSet = addAboDto.cboDestSetSubDir.getEditor().getText();
        if (!addAboDto.cboDestSetSubDir.getItems().contains(sSet)) {
            addAboDto.cboDestSetSubDir.getItems().add(sSet);
        }
        final String sOwnPath = addAboDto.cboDestAboDir.getEditor().getText();
        if (!addAboDto.cboDestAboDir.getItems().contains(sOwnPath)) {
            addAboDto.cboDestAboDir.getItems().add(sOwnPath);
        }
        final String sOwnFileName = addAboDto.cboDestAboFileName.getEditor().getText();
        if (!addAboDto.cboDestAboFileName.getItems().contains(sOwnFileName)) {
            addAboDto.cboDestAboFileName.getItems().add(sOwnFileName);
        }
    }

    public void makeAct() {
        addAboDto.cboDestSetSubDir.getEditor().setText(addAboDto.getAct().abo.getAboSubDir());
        addAboDto.cboDestAboDir.getEditor().setText(addAboDto.getAct().abo.getAboDir());
        addAboDto.cboDestAboFileName.getEditor().setText(addAboDto.getAct().abo.getAboFileName());

        addAboDto.rbSetPath.setSelected(addAboDto.getAct().abo.getAboDir().isEmpty());
        addAboDto.rbOwnPath.setSelected(!addAboDto.rbSetPath.isSelected());

        addAboDto.rbSetFileName.setSelected(addAboDto.getAct().abo.getAboFileName().isEmpty());
        addAboDto.rbOwnFileName.setSelected(!addAboDto.rbSetFileName.isSelected());

        addAboDto.chkDestAboSubDir.setSelected(!addAboDto.getAct().abo.getAboSubDir().isEmpty());
        if (addAboDto.chkDestAboSubDir.isSelected()) {
            // dann einen eigenen Pfad eingeben
            addAboDto.lblSetSubDir.setVisible(false);
            addAboDto.cboDestSetSubDir.setVisible(true);
        } else {
            // Standard des Sets verwenden
            addAboDto.lblSetSubDir.setVisible(true);
            addAboDto.cboDestSetSubDir.setVisible(false);
        }

        addMissingPath();
        setResPath();
    }

    private void setResPath() {
        FilmDataMTP filmData = new FilmDataMTP();
        filmData.arr[FilmDataXml.FILM_CHANNEL] = "SENDER";
        filmData.arr[FilmDataXml.FILM_THEME] = "THEMA";
        filmData.arr[FilmDataXml.FILM_TITLE] = "TITEL";
        filmData.arr[FilmDataXml.FILM_DURATION] = "100";
        filmData.arr[FilmDataXml.FILM_DATE] = "24.12.2023";
        filmData.arr[FilmDataXml.FILM_TIME] = "12:30:00";
        filmData.arr[FilmDataXml.FILM_URL] = "https://URL.MP4";
        filmData.arr[FilmDataXml.FILM_WEBSITE] = "WEBSITE";
        filmData.arr[FilmDataXml.FILM_DESCRIPTION] = "BESCHREIBUNG";
        filmData.init();

        final SetData setData = addAboDto.getAct().abo.getSetData();
        final AboData abo = addAboDto.getAct().abo;
        DownloadData downloadData = new DownloadData(DownloadConstants.SRC_ABO, setData, filmData, abo,
                "", "", "", false);
        addAboDto.lblResPath.setText(downloadData.getDestPath());
        addAboDto.lblResFileName.setText(downloadData.getDestFileName());
    }
}
