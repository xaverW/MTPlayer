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

    private final AddDownloadDto addDownloadDto;

    public InitStartTime(AddDownloadDto addDownloadDto) {
        this.addDownloadDto = addDownloadDto;
        init();
    }

    private void init() {
        addDownloadDto.chkStartTimeAll.setSelected(true); // soll stand. für alle gelten

        final ToggleGroup toggleGroupStart = new ToggleGroup();
        addDownloadDto.rbStartNow.setToggleGroup(toggleGroupStart);
        addDownloadDto.rbStartNotYet.setToggleGroup(toggleGroupStart);
        addDownloadDto.rbStartAtTime.setToggleGroup(toggleGroupStart);

        addDownloadDto.rbStartNow.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOW);
        addDownloadDto.rbStartNotYet.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOT);
        addDownloadDto.rbStartAtTime.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_TIME);

        addDownloadDto.p2TimePicker.getSelectionModel().selectFirst();
        addDownloadDto.p2TimePicker.setDisable(!addDownloadDto.rbStartAtTime.isSelected());

        if (addDownloadDto.addNewDownloads) {
            makeAct();
            setStartTime();
        } else {
            // wenn schon eine Startzeit, dann jetzt setzen
            if (!addDownloadDto.getAct().download.getStartTime().isEmpty()) {
                addDownloadDto.rbStartAtTime.setSelected(true);
                addDownloadDto.p2TimePicker.setTime(addDownloadDto.getAct().download.getStartTime());
            } else {
                addDownloadDto.rbStartNotYet.setSelected(true);
            }
        }

        addDownloadDto.chkStartTimeAll.setOnAction(a -> {
            if (addDownloadDto.chkStartTimeAll.isSelected()) {
                setStartTime();
            }
        });
        addDownloadDto.rbStartNow.setOnAction(a -> {
            setStartTime();
        });
        addDownloadDto.rbStartNotYet.setOnAction(a -> {
            setStartTime();
        });
        addDownloadDto.rbStartAtTime.selectedProperty().addListener((u, o, n) ->
                addDownloadDto.p2TimePicker.setDisable(!addDownloadDto.rbStartAtTime.isSelected()));
        addDownloadDto.rbStartAtTime.setOnAction(a -> {
            setStartTime();
        });

        addDownloadDto.p2TimePicker.setOnAction(a -> {
            setStartTime();
        });
    }

    public void makeAct() {
        addDownloadDto.p2TimePicker.setDisable(addDownloadDto.getAct().downloadIsRunning());
        addDownloadDto.rbStartNow.setDisable(addDownloadDto.getAct().downloadIsRunning());
        addDownloadDto.rbStartNotYet.setDisable(addDownloadDto.getAct().downloadIsRunning());
        addDownloadDto.p2TimePicker.setDisable(!addDownloadDto.rbStartAtTime.isSelected());

        if (!addDownloadDto.getAct().download.getStartTime().isEmpty()) {
            // dann Startzeit
            addDownloadDto.p2TimePicker.setTime(addDownloadDto.getAct().download.getStartTime());
            addDownloadDto.rbStartAtTime.setSelected(true);
        } else if (addDownloadDto.getAct().startNow) {
            // dann starten
            addDownloadDto.rbStartNow.setSelected(true);
        } else {
            // nix
            addDownloadDto.rbStartNotYet.setSelected(true);
        }
    }

    private void setStartTime() {
        if (addDownloadDto.chkStartTimeAll.isSelected()) {
            Arrays.stream(addDownloadDto.addDownloadData).forEach(this::setTime);
        } else {
            setTime(addDownloadDto.getAct());
        }
    }

    private void setTime(AddDownloadData addDownloadData) {
        if (addDownloadDto.rbStartAtTime.isSelected()) {
            addDownloadData.download.setStartTime(addDownloadDto.p2TimePicker.getTime());
        } else if (addDownloadDto.rbStartNow.isSelected()) {
            addDownloadData.download.setStartTime("");
            addDownloadData.startNow = true;
        } else {
            addDownloadData.download.setStartTime("");
            addDownloadData.startNow = false;
        }
    }
}
