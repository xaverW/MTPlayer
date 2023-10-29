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
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.P2LibConst;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;

import java.util.Arrays;

public class InitResolutionButton {
    private final AddDownloadDto addDownloadDto;
    private EventHandler<ActionEvent> onAction;

    public InitResolutionButton(AddDownloadDto addDownloadDto) {
        this.addDownloadDto = addDownloadDto;
        init();
    }

    private void init() {
        onAction = (a) -> {
            setRes();
            addDownloadDto.updateAct();
        };

        // und jetzt für den aktuellen Film das GUI setzen
        makeAct();

        final ToggleGroup toggleGroupSize = new ToggleGroup();
        addDownloadDto.rbHd.setToggleGroup(toggleGroupSize);
        addDownloadDto.rbHigh.setToggleGroup(toggleGroupSize);
        addDownloadDto.rbSmall.setToggleGroup(toggleGroupSize);

        addDownloadDto.rbHd.setOnAction(onAction);
        addDownloadDto.rbHigh.setOnAction(onAction);
        addDownloadDto.rbSmall.setOnAction(onAction);
        addDownloadDto.chkResolutionAll.setOnAction(a -> {
            if (addDownloadDto.chkResolutionAll.isSelected()) {
                setRes();
                addDownloadDto.updateAct();
            }
        });
    }

    private void setRes() {
        if (addDownloadDto.rbHd.isSelected()) {
            setResolution(FilmDataMTP.RESOLUTION_HD);
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmDataMTP.RESOLUTION_HD);

        } else if (addDownloadDto.rbHigh.isSelected()) {
            setResolution(FilmDataMTP.RESOLUTION_NORMAL);
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmDataMTP.RESOLUTION_NORMAL);

        } else {
            setResolution(FilmDataMTP.RESOLUTION_SMALL);
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmDataMTP.RESOLUTION_SMALL);
        }
    }

    private void setResolution(String resolution) {
        if (addDownloadDto.chkResolutionAll.isSelected()) {
            Arrays.stream(addDownloadDto.addDownloadData).forEach(downloadAddData -> {
                if (resolution.equals(FilmDataMTP.RESOLUTION_HD) && downloadAddData.download.getFilm().isHd()) {
                    downloadAddData.resolution = FilmDataMTP.RESOLUTION_HD;

                } else if (resolution.equals(FilmDataMTP.RESOLUTION_SMALL) && addDownloadDto.getAct().download.getFilm().isSmall()) {
                    downloadAddData.resolution = FilmDataMTP.RESOLUTION_SMALL;

                } else {
                    downloadAddData.resolution = FilmDataMTP.RESOLUTION_NORMAL;
                }
                downloadAddData.download.setUrl(downloadAddData.download.getFilm().getUrlForResolution(downloadAddData.resolution));
                InitProgramCall.setProgrammCall(addDownloadDto, downloadAddData);
            });

        } else {
            addDownloadDto.getAct().resolution = resolution;
            addDownloadDto.getAct().download.setUrl(addDownloadDto.getAct().download.getFilm().getUrlForResolution(resolution));
            InitProgramCall.setProgrammCall(addDownloadDto, addDownloadDto.getAct());
        }
    }

    public void makeAct() {
        addDownloadDto.rbHigh.setOnAction(null);
        addDownloadDto.rbHd.setOnAction(null);
        addDownloadDto.rbSmall.setOnAction(null);

        addDownloadDto.rbHigh.setDisable(addDownloadDto.getAct().downloadIsRunning());
        addDownloadDto.rbHd.setDisable(addDownloadDto.getAct().downloadIsRunning());
        addDownloadDto.rbSmall.setDisable(addDownloadDto.getAct().downloadIsRunning());

        addDownloadDto.rbHd.setDisable(!addDownloadDto.getAct().download.getFilm().isHd());
        addDownloadDto.rbSmall.setDisable(!addDownloadDto.getAct().download.getFilm().isSmall());

        switch (addDownloadDto.getAct().resolution) {
            case FilmDataMTP.RESOLUTION_HD -> addDownloadDto.rbHd.setSelected(true);
            case FilmDataMTP.RESOLUTION_SMALL -> addDownloadDto.rbSmall.setSelected(true);
            default -> addDownloadDto.rbHigh.setSelected(true);
        }

        String textHd = "HD";
        if (!addDownloadDto.rbHd.isDisable() && !addDownloadDto.getAct().fileSize_HD.isEmpty()) {
            addDownloadDto.rbHd.setText(textHd + P2LibConst.LINE_SEPARATOR +
                    "[ " + addDownloadDto.getAct().fileSize_HD + " MB ]");
            addDownloadDto.rbHd.setTextAlignment(TextAlignment.CENTER);
            addDownloadDto.rbHd.setMinHeight(Region.USE_PREF_SIZE);
        } else {
            addDownloadDto.rbHd.setText(textHd);
        }

        String textHeight = "hohe Auflösung";
        if (!addDownloadDto.getAct().fileSize_high.isEmpty()) {
            addDownloadDto.rbHigh.setText(textHeight + P2LibConst.LINE_SEPARATOR +
                    "[ " + addDownloadDto.getAct().fileSize_high + " MB ]");
            addDownloadDto.rbHigh.setTextAlignment(TextAlignment.CENTER);
            addDownloadDto.rbHigh.setMinHeight(Region.USE_PREF_SIZE);
        } else {
            addDownloadDto.rbHigh.setText(textHeight);
        }

        String textLow = "niedrige Auflösung";
        if (!addDownloadDto.rbSmall.isDisable() && !addDownloadDto.getAct().fileSize_small.isEmpty()) {
            addDownloadDto.rbSmall.setText(textLow + P2LibConst.LINE_SEPARATOR +
                    "[ " + addDownloadDto.getAct().fileSize_small + " MB ]");
            addDownloadDto.rbSmall.setTextAlignment(TextAlignment.CENTER);
            addDownloadDto.rbSmall.setMinHeight(Region.USE_PREF_SIZE);
        } else {
            addDownloadDto.rbSmall.setText(textLow);
        }

        addDownloadDto.rbHigh.setOnAction(onAction);
        addDownloadDto.rbHd.setOnAction(onAction);
        addDownloadDto.rbSmall.setOnAction(onAction);
    }
}
