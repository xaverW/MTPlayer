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

        addAboDto.chkAboSubDir.setOnAction(event -> {
            if (addAboDto.chkAboSubDir.isFocused()) {
                // dann durch den User
                setPathToAbo();
            }

            addAboDto.lblSetSubDir.setVisible(!addAboDto.chkAboSubDir.isSelected()); // aus dem Set anzeigen
            addAboDto.cboAboSubDir.setVisible(addAboDto.chkAboSubDir.isSelected()); // eignen Sub anzeigen
        });

        ArrayList<String> path = addAboDto.progData.aboList.getAboSubDirList();
        addAboDto.cboAboSubDir.setItems(FXCollections.observableArrayList(path));

        addAboDto.cboAboSubDir.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!addAboDto.cboAboSubDir.isFocused()) {
                return;
            }
            setPathToAbo();
        });
        addAboDto.cboAboSubDir.selectionModelProperty().addListener((u, o, n) -> {
            if (!addAboDto.cboAboSubDir.isFocused()) {
                return;
            }
            setPathToAbo();
        });
    }

    private void initOwnPath() {
        ArrayList<String> path = addAboDto.progData.aboList.getAboDirList();
        addAboDto.cboAboDir.setItems(FXCollections.observableArrayList(path));

        addAboDto.cboAboDir.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!addAboDto.cboAboDir.isFocused()) {
                return;
            }
            setPathToAbo();
        });
        addAboDto.cboAboDir.selectionModelProperty().addListener((u, o, n) -> {
            if (!addAboDto.cboAboDir.isFocused()) {
                return;
            }
            setPathToAbo();
        });
    }

    private void initFileName() {
        ArrayList<String> fileName = addAboDto.progData.aboList.getAboFileNameList();
        addAboDto.cboAboFileName.setItems(FXCollections.observableArrayList(fileName));

        addAboDto.cboAboFileName.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!addAboDto.cboAboFileName.isFocused()) {
                return;
            }
            setFileNameToAbo();
        });
        addAboDto.cboAboFileName.selectionModelProperty().addListener((u, o, n) -> {
            if (!addAboDto.cboAboFileName.isFocused()) {
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
            if (addAboDto.chkAboSubDir.isSelected()) {
                addAboData.abo.setAboSubDir(addAboDto.cboAboSubDir.getEditor().getText());
            } else {
                addAboData.abo.setAboSubDir("");
            }
            addAboData.abo.setAboDir("");
        } else {
            addAboData.abo.setAboDir(addAboDto.cboAboDir.getEditor().getText());
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
            addAboData.abo.setAboFileName(addAboDto.cboAboFileName.getEditor().getText());
        }
    }

    private void addMissingPath() {
        final String sSet = addAboDto.cboAboSubDir.getEditor().getText();
        if (!addAboDto.cboAboSubDir.getItems().contains(sSet)) {
            addAboDto.cboAboSubDir.getItems().add(sSet);
        }
        final String sOwnPath = addAboDto.cboAboDir.getEditor().getText();
        if (!addAboDto.cboAboDir.getItems().contains(sOwnPath)) {
            addAboDto.cboAboDir.getItems().add(sOwnPath);
        }
        final String sOwnFileName = addAboDto.cboAboFileName.getEditor().getText();
        if (!addAboDto.cboAboFileName.getItems().contains(sOwnFileName)) {
            addAboDto.cboAboFileName.getItems().add(sOwnFileName);
        }
    }

    public void makeAct() {
        addAboDto.cboAboSubDir.getEditor().setText(addAboDto.getAct().abo.getAboSubDir());
        addAboDto.cboAboDir.getEditor().setText(addAboDto.getAct().abo.getAboDir());
        addAboDto.cboAboFileName.getEditor().setText(addAboDto.getAct().abo.getAboFileName());

        addAboDto.rbSetPath.setSelected(addAboDto.getAct().abo.getAboDir().isEmpty());
        addAboDto.rbOwnPath.setSelected(!addAboDto.rbSetPath.isSelected());

        addAboDto.rbSetFileName.setSelected(addAboDto.getAct().abo.getAboFileName().isEmpty());
        addAboDto.rbOwnFileName.setSelected(!addAboDto.rbSetFileName.isSelected());

        addAboDto.chkAboSubDir.setSelected(!addAboDto.getAct().abo.getAboSubDir().isEmpty());
        if (addAboDto.chkAboSubDir.isSelected()) {
            // dann einen eigenen Pfad eingeben
            addAboDto.lblSetSubDir.setVisible(false);
            addAboDto.cboAboSubDir.setVisible(true);
        } else {
            // Standard des Sets verwenden
            addAboDto.lblSetSubDir.setVisible(true);
            addAboDto.cboAboSubDir.setVisible(false);
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
        DownloadData downloadData = new DownloadData(DownloadConstants.SRC_DOWNLOAD, setData, filmData, abo,
                "", "", "", false);
        addAboDto.lblResPath.setText(downloadData.getDestPath());
        addAboDto.lblResFileName.setText(downloadData.getDestFileName());
    }
}
