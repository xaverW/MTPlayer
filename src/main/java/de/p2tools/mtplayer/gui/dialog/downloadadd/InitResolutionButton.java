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
    private final AddDto addDto;
    private EventHandler<ActionEvent> onAction;

    public InitResolutionButton(AddDto addDto) {
        this.addDto = addDto;
        init();
    }

    private void init() {
        onAction = (a) -> {
            setRes();
            addDto.updateAct();
        };

        // und jetzt für den aktuellen Film das GUI setzen
        makeAct();

        final ToggleGroup toggleGroupSize = new ToggleGroup();
        addDto.rbHd.setToggleGroup(toggleGroupSize);
        addDto.rbHigh.setToggleGroup(toggleGroupSize);
        addDto.rbSmall.setToggleGroup(toggleGroupSize);

        addDto.rbHd.setOnAction(onAction);
        addDto.rbHigh.setOnAction(onAction);
        addDto.rbSmall.setOnAction(onAction);
        addDto.chkResolutionAll.setOnAction(a -> {
            if (addDto.chkResolutionAll.isSelected()) {
                setRes();
                addDto.updateAct();
            }
        });
    }

    private void setRes() {
        if (addDto.rbHd.isSelected()) {
            setResolution(FilmDataMTP.RESOLUTION_HD);
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmDataMTP.RESOLUTION_HD);

        } else if (addDto.rbHigh.isSelected()) {
            setResolution(FilmDataMTP.RESOLUTION_NORMAL);
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmDataMTP.RESOLUTION_NORMAL);

        } else {
            setResolution(FilmDataMTP.RESOLUTION_SMALL);
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmDataMTP.RESOLUTION_SMALL);
        }
    }

    private void setResolution(String resolution) {
        if (addDto.chkResolutionAll.isSelected()) {
            Arrays.stream(addDto.downloadAddData).forEach(downloadAddData -> {
                if (resolution.equals(FilmDataMTP.RESOLUTION_HD) && downloadAddData.download.getFilm().isHd()) {
                    downloadAddData.resolution = FilmDataMTP.RESOLUTION_HD;

                } else if (resolution.equals(FilmDataMTP.RESOLUTION_SMALL) && addDto.getAct().download.getFilm().isSmall()) {
                    downloadAddData.resolution = FilmDataMTP.RESOLUTION_SMALL;

                } else {
                    downloadAddData.resolution = FilmDataMTP.RESOLUTION_NORMAL;
                }
                downloadAddData.download.setUrl(downloadAddData.download.getFilm().getUrlForResolution(downloadAddData.resolution));
                InitProgramCall.setProgrammCall(addDto, downloadAddData);
            });

        } else {
            addDto.getAct().resolution = resolution;
            addDto.getAct().download.setUrl(addDto.getAct().download.getFilm().getUrlForResolution(resolution));
            InitProgramCall.setProgrammCall(addDto, addDto.getAct());
        }
    }

    public void makeAct() {
        System.out.println("REA-ACT");
        addDto.rbHigh.setOnAction(null);
        addDto.rbHd.setOnAction(null);
        addDto.rbSmall.setOnAction(null);

        addDto.rbHigh.setDisable(addDto.getAct().downloadIsRunning());
        addDto.rbHd.setDisable(addDto.getAct().downloadIsRunning());
        addDto.rbSmall.setDisable(addDto.getAct().downloadIsRunning());

        addDto.rbHd.setDisable(!addDto.getAct().download.getFilm().isHd());
        addDto.rbSmall.setDisable(!addDto.getAct().download.getFilm().isSmall());

        switch (addDto.getAct().resolution) {
            case FilmDataMTP.RESOLUTION_HD -> addDto.rbHd.setSelected(true);
            case FilmDataMTP.RESOLUTION_SMALL -> addDto.rbSmall.setSelected(true);
            default -> addDto.rbHigh.setSelected(true);
        }

        String textHd = "HD";
        if (!addDto.rbHd.isDisable() && !addDto.getAct().fileSize_HD.isEmpty()) {
            addDto.rbHd.setText(textHd + P2LibConst.LINE_SEPARATOR +
                    "[ " + addDto.getAct().fileSize_HD + " MB ]");
            addDto.rbHd.setTextAlignment(TextAlignment.CENTER);
            addDto.rbHd.setMinHeight(Region.USE_PREF_SIZE);
        } else {
            addDto.rbHd.setText(textHd);
        }

        String textHeight = "hohe Auflösung";
        if (!addDto.getAct().fileSize_high.isEmpty()) {
            addDto.rbHigh.setText(textHeight + P2LibConst.LINE_SEPARATOR +
                    "[ " + addDto.getAct().fileSize_high + " MB ]");
            addDto.rbHigh.setTextAlignment(TextAlignment.CENTER);
            addDto.rbHigh.setMinHeight(Region.USE_PREF_SIZE);
        } else {
            addDto.rbHigh.setText(textHeight);
        }

        String textLow = "niedrige Auflösung";
        if (!addDto.rbSmall.isDisable() && !addDto.getAct().fileSize_small.isEmpty()) {
            addDto.rbSmall.setText(textLow + P2LibConst.LINE_SEPARATOR +
                    "[ " + addDto.getAct().fileSize_small + " MB ]");
            addDto.rbSmall.setTextAlignment(TextAlignment.CENTER);
            addDto.rbSmall.setMinHeight(Region.USE_PREF_SIZE);
        } else {
            addDto.rbSmall.setText(textLow);
        }

        addDto.rbHigh.setOnAction(onAction);
        addDto.rbHd.setOnAction(onAction);
        addDto.rbSmall.setOnAction(onAction);
    }
}
