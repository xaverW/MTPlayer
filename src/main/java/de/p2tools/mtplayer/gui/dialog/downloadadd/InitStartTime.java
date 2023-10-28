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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import javafx.scene.control.ToggleGroup;

import java.util.Arrays;

public class InitStartTime {

    private final AddDto addDto;

    public InitStartTime(AddDto addDto) {
        this.addDto = addDto;
        init();
    }

    private void init() {
        addDto.chkStartTimeAll.setSelected(true); // soll stand. für alle gelten

        final ToggleGroup toggleGroupStart = new ToggleGroup();
        addDto.rbStartNow.setToggleGroup(toggleGroupStart);
        addDto.rbStartNotYet.setToggleGroup(toggleGroupStart);
        addDto.rbStartAtTime.setToggleGroup(toggleGroupStart);

        addDto.rbStartNow.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOW);
        addDto.rbStartNotYet.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOT);
        addDto.rbStartAtTime.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_TIME);

        addDto.p2TimePicker.getSelectionModel().selectFirst();
        addDto.p2TimePicker.setDisable(!addDto.rbStartAtTime.isSelected());

        if (addDto.addNewDownloads) {
            makeAct();
            setStartTime();
        } else {
            // wenn schon eine Startzeit, dann jetzt setzen
            if (!addDto.getAct().download.getStartTime().isEmpty()) {
                addDto.rbStartAtTime.setSelected(true);
                addDto.p2TimePicker.setTime(addDto.getAct().download.getStartTime());
            } else {
                addDto.rbStartNotYet.setSelected(true);
            }
        }

        addDto.chkStartTimeAll.setOnAction(a -> {
            if (addDto.chkStartTimeAll.isSelected()) {
                setStartTime();
            }
        });
        addDto.rbStartNow.setOnAction(a -> {
            setStartTime();
        });
        addDto.rbStartNotYet.setOnAction(a -> {
            setStartTime();
        });
        addDto.rbStartAtTime.selectedProperty().addListener((u, o, n) ->
                addDto.p2TimePicker.setDisable(!addDto.rbStartAtTime.isSelected()));
        addDto.rbStartAtTime.setOnAction(a -> {
            setStartTime();
        });

        addDto.p2TimePicker.setOnAction(a -> {
            setStartTime();
        });
    }

    public void makeAct() {
        System.out.println("TIME_ACT");
        addDto.p2TimePicker.setDisable(addDto.getAct().downloadIsRunning());
        addDto.rbStartNow.setDisable(addDto.getAct().downloadIsRunning());
        addDto.rbStartNotYet.setDisable(addDto.getAct().downloadIsRunning());
        addDto.p2TimePicker.setDisable(!addDto.rbStartAtTime.isSelected());

        if (!addDto.getAct().download.getStartTime().isEmpty()) {
            // dann Startzeit
            addDto.p2TimePicker.setTime(addDto.getAct().download.getStartTime());
            addDto.rbStartAtTime.setSelected(true);
        } else if (addDto.getAct().startNow) {
            // dann starten
            addDto.rbStartNow.setSelected(true);
        } else {
            // nix
            addDto.rbStartNotYet.setSelected(true);
        }
    }

    private void setStartTime() {
        if (addDto.chkStartTimeAll.isSelected()) {
            Arrays.stream(addDto.downloadAddData).forEach(this::setTime);
        } else {
            setTime(addDto.getAct());
        }
    }

    private void setTime(DownloadAddData downloadAddData) {
        if (addDto.rbStartAtTime.isSelected()) {
            downloadAddData.download.setStartTime(addDto.p2TimePicker.getTime());
        } else if (addDto.rbStartNow.isSelected()) {
            downloadAddData.download.setStartTime("");
            downloadAddData.startNow = true;
        } else {
            downloadAddData.download.setStartTime("");
            downloadAddData.startNow = false;
        }
    }
}
