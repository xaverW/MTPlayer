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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InitDestination {
    private final AddAboDto addAboDto;

    public InitDestination(AddAboDto addAboDto) {
        this.addAboDto = addAboDto;
        init();
    }

    private void init() {
        addAboDto.chkDestAboSubDir.setOnAction(event -> {
            if (addAboDto.chkDestAboSubDir.isFocused()) {
                // dann durch den User
                if (addAboDto.chkDestAboSubDir.isSelected()) {
                    // dann gespeicherten SubDir wieder setzen
                    if (addAboDto.chkDestSetSubDirAll.isSelected()) {
                        Arrays.stream(addAboDto.addAboData).forEach(addAboData -> {
                            addAboData.abo.setAboSubDir(addAboDto.getAct().aboSubDir); // da wird immer der "erste alte" gesetzt
                        });
                    } else {
                        addAboDto.getAct().abo.setAboSubDir(addAboDto.getAct().aboSubDir);
                    }

                } else {
                    // dann SubDir speichern
                    if (addAboDto.chkDestSetSubDirAll.isSelected()) {
                        Arrays.stream(addAboDto.addAboData).forEach(addAboData -> {
                            addAboData.aboSubDir = addAboData.abo.getAboSubDir();
                            addAboData.abo.setAboSubDir("");
                        });
                    } else {
                        addAboDto.getAct().aboSubDir = addAboDto.getAct().abo.getAboSubDir();
                        addAboDto.getAct().abo.setAboSubDir("");
                    }
                }
            }
            if (addAboDto.chkDestAboSubDir.isSelected()) {
                // dann einen eigenen Pfad eingeben
                addAboDto.lblSetSubDir.setVisible(false);
                addAboDto.cboDestSetSubDir.setVisible(true);
                addAboDto.cboDestSetSubDir.getEditor().setText(addAboDto.getAct().abo.getAboSubDir());

            } else {
                // Standard des Sets verwenden
                addAboDto.lblSetSubDir.setVisible(true);
                addAboDto.cboDestSetSubDir.setVisible(false);
                addAboDto.cboDestSetSubDir.getEditor().setText("");
            }
            setResPath();
        });

        ArrayList<String> path = addAboDto.progData.aboList.getAboDestinationPathList();
        addAboDto.cboDestSetSubDir.setItems(FXCollections.observableArrayList(path));

        addAboDto.cboDestSetSubDir.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!addAboDto.cboDestSetSubDir.isFocused()) {
                return;
            }
            setPath();
        });
        addAboDto.cboDestSetSubDir.selectionModelProperty().addListener((u, o, n) -> {
            if (!addAboDto.cboDestSetSubDir.isFocused()) {
                return;
            }
            setPath();
        });
    }

    public void setPath() {
        addMissingPath();
        if (addAboDto.chkDestSetSubDirAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(addAboData -> {
                addAboData.abo.setAboSubDir(addAboDto.cboDestSetSubDir.getEditor().getText());
                if (addAboDto.chkDestAboSubDir.isSelected()) {
                    addAboData.aboSubDir = addAboData.abo.getAboSubDir();
                }
            });
        } else {
            addAboDto.getAct().abo.setAboSubDir(addAboDto.cboDestSetSubDir.getEditor().getText());
            if (addAboDto.chkDestAboSubDir.isSelected()) {
                addAboDto.getAct().aboSubDir = addAboDto.getAct().abo.getAboSubDir();
            }
        }
        setResPath();
    }

    private void addMissingPath() {
        final String s = addAboDto.cboDestSetSubDir.getEditor().getText();
        if (!addAboDto.cboDestSetSubDir.getItems().contains(s)) {
            addAboDto.cboDestSetSubDir.getItems().add(s);
        }
    }

    public void makeAct() {
        addAboDto.cboDestSetSubDir.getEditor().setText(addAboDto.getAct().abo.getAboSubDir());
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

    public void setResPath() {
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

        if (addAboDto.cboSetData.getSelectionModel().getSelectedItem() != null) {
            SetData setData = addAboDto.cboSetData.getSelectionModel().getSelectedItem();

            DownloadData downloadData = new DownloadData(DownloadConstants.SRC_ABO, setData, filmData, addAboDto.getAct().abo,
                    "", "", "", false);
            addAboDto.lblResPath.setText(downloadData.getDestPath());
            addAboDto.lblResFileName.setText(downloadData.getDestFileName());

        } else {
            List<SetData> setData = ProgData.getInstance().setDataList.getSetDataListAbo();
            if (!setData.isEmpty()) {
                ArrayList<FilmDataMTP> list = new ArrayList<>();
                list.add(filmData);
                DownloadData downloadData = new DownloadData(list, setData.get(0));

                addAboDto.lblResPath.setText(downloadData.getDestPath());
                addAboDto.lblResFileName.setText(downloadData.getDestFileName());
            }
        }
    }
}
