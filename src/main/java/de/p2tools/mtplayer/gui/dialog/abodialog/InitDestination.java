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

import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Arrays;

public class InitDestination {
    private final AddAboDto addAboDto;

    public InitDestination(AddAboDto addAboDto) {
        this.addAboDto = addAboDto;
        init();
    }

    private void init() {
        addAboDto.chkDestination.setOnAction(event -> {
            if (addAboDto.chkDestination.isFocused()) {
                // dann durch den User
                if (addAboDto.chkDestination.isSelected()) {
                    // dann gespeicherten SubDir wieder setzen
                    if (addAboDto.chkDestinationAll.isSelected()) {
                        Arrays.stream(addAboDto.addAboData).forEach(addAboData -> {
                            addAboData.abo.setAboSubDir(addAboDto.getAct().aboSubDir); // da wird immer der "erste alte" gesetzt
                        });
                    } else {
                        addAboDto.getAct().abo.setAboSubDir(addAboDto.getAct().aboSubDir);
                    }

                } else {
                    // dann SubDir speichern
                    if (addAboDto.chkDestinationAll.isSelected()) {
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
            if (addAboDto.chkDestination.isSelected()) {
                // dann einen eigenen Pfad eingeben
                addAboDto.lblDestination.setVisible(false);
                addAboDto.cboDestination.setVisible(true);
                addAboDto.cboDestination.getEditor().setText(addAboDto.getAct().abo.getAboSubDir());

            } else {
                // Standard des Sets verwenden
                addAboDto.lblDestination.setVisible(true);
                addAboDto.cboDestination.setVisible(false);
                addAboDto.cboDestination.getEditor().setText("");
            }
        });

        ArrayList<String> path = addAboDto.progData.aboList.getAboDestinationPathList();
        addAboDto.cboDestination.setItems(FXCollections.observableArrayList(path));

        addAboDto.cboDestination.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!addAboDto.cboDestination.isFocused()) {
                return;
            }
            setPath();
        });
        addAboDto.cboDestination.selectionModelProperty().addListener((u, o, n) -> {
            if (!addAboDto.cboDestination.isFocused()) {
                return;
            }
            setPath();
        });
    }

    public void setPath() {
        addMissingPath();
        if (addAboDto.chkDestinationAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(addAboData -> {
                addAboData.abo.setAboSubDir(addAboDto.cboDestination.getEditor().getText());
                if (addAboDto.chkDestination.isSelected()) {
                    addAboData.aboSubDir = addAboData.abo.getAboSubDir();
                }
            });
        } else {
            addAboDto.getAct().abo.setAboSubDir(addAboDto.cboDestination.getEditor().getText());
            if (addAboDto.chkDestination.isSelected()) {
                addAboDto.getAct().aboSubDir = addAboDto.getAct().abo.getAboSubDir();
            }
        }
    }

    private void addMissingPath() {
        final String s = addAboDto.cboDestination.getEditor().getText();
        if (!addAboDto.cboDestination.getItems().contains(s)) {
            addAboDto.cboDestination.getItems().add(s);
        }
    }

    public void makeAct() {
        addAboDto.cboDestination.getEditor().setText(addAboDto.getAct().abo.getAboSubDir());
        addAboDto.chkDestination.setSelected(!addAboDto.getAct().abo.getAboSubDir().isEmpty());
        if (addAboDto.chkDestination.isSelected()) {
            // dann einen eigenen Pfad eingeben
            addAboDto.lblDestination.setVisible(false);
            addAboDto.cboDestination.setVisible(true);
        } else {
            // Standard des Sets verwenden
            addAboDto.lblDestination.setVisible(true);
            addAboDto.cboDestination.setVisible(false);
        }

        addMissingPath();
    }
}
