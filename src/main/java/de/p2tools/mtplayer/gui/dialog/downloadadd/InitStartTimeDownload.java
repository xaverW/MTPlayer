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

public class InitStartTimeDownload {

    private final AddDownloadDto addDownloadDto;

    public InitStartTimeDownload(AddDownloadDto addDownloadDto) {
        this.addDownloadDto = addDownloadDto;
        init();
    }

    private void init() {
        final ToggleGroup toggleGroupStart = new ToggleGroup();
        addDownloadDto.rbStartNow.setToggleGroup(toggleGroupStart);
        addDownloadDto.rbStartNotYet.setToggleGroup(toggleGroupStart);
        addDownloadDto.rbStartAtTime.setToggleGroup(toggleGroupStart);

        if (addDownloadDto.addNewDownloads) {
            // Vorgabe nur für neue Downloads
            addDownloadDto.rbStartNow.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOW);
            addDownloadDto.rbStartNotYet.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOT);
            addDownloadDto.rbStartAtTime.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_TIME);
        }

        addDownloadDto.p2TimePicker.getSelectionModel().selectFirst();
        addDownloadDto.p2TimePicker.disableProperty().bind(addDownloadDto.rbStartAtTime.selectedProperty().not()
                .or(addDownloadDto.rbStartAtTime.disableProperty()));

        if (addDownloadDto.addNewDownloads) {
            setStartTime();
        } else {
            // wenn schon eine Startzeit, dann jetzt setzen
            if (!addDownloadDto.getAct().download.getStartTime().isEmpty()) {
                addDownloadDto.rbStartAtTime.setSelected(true);
                addDownloadDto.p2TimePicker.setTime(addDownloadDto.getAct().download.getStartTimeOnly());
            } else {
                addDownloadDto.rbStartNotYet.setSelected(true);
            }
        }

        addDownloadDto.rbStartNow.setOnAction(a -> {
            setStartTime();
        });
        addDownloadDto.rbStartNotYet.setOnAction(a -> {
            setStartTime();
        });
        addDownloadDto.rbStartAtTime.setOnAction(a -> {
            setStartTime();
        });

        addDownloadDto.p2TimePicker.setOnAction(a -> {
            setStartTime();
        });
    }

    public void makeAct() {
        addDownloadDto.rbStartNow.setDisable(addDownloadDto.getAct().downloadIsRunning());
        addDownloadDto.rbStartNotYet.setDisable(addDownloadDto.getAct().downloadIsRunning());
        addDownloadDto.rbStartAtTime.setDisable(addDownloadDto.getAct().downloadIsRunning());

        if (!addDownloadDto.getAct().download.getStartTime().isEmpty()) {
            // dann Startzeit
            addDownloadDto.p2TimePicker.setTime(addDownloadDto.getAct().download.getStartTimeOnly());
            addDownloadDto.rbStartAtTime.setSelected(true);

        } else if (addDownloadDto.getAct().startNow) {
            // dann starten
            addDownloadDto.rbStartNow.setSelected(true);

        } else {
            // nix
            addDownloadDto.rbStartNotYet.setSelected(true);
        }
    }

    public void setStartTime() {
        if (addDownloadDto.chkStartTimeAll.isSelected()) {
            Arrays.stream(addDownloadDto.addDownloadData).forEach(this::setTime);
        } else {
            setTime(addDownloadDto.getAct());
        }
    }

    private void setTime(AddDownloadData addDownloadData) {
        if (addDownloadDto.rbStartAtTime.isSelected()) {
            addDownloadData.download.setStartTimeAlsoTomorrow(addDownloadDto.p2TimePicker.getTime());
            addDownloadData.startNow = false;
        } else if (addDownloadDto.rbStartNow.isSelected()) {
            addDownloadData.download.setStartTime("");
            addDownloadData.startNow = true;
        } else {
            addDownloadData.download.setStartTime("");
            addDownloadData.startNow = false;
        }
    }
}
