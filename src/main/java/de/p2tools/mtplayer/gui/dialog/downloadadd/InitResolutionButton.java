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
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.p2lib.P2LibConst;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

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
            storeRes(); // wird nur beim Ändern der Radios gemacht
        };

        // und jetzt für den aktuellen Film das GUI setzen
        addDownloadDto.rbHd.setOnAction(onAction);
        addDownloadDto.rbHigh.setOnAction(onAction);
        addDownloadDto.rbSmall.setOnAction(onAction);
    }

    public void setRes() {
        if (addDownloadDto.rbHd.isSelected()) {
            setResolution(FilmDataMTP.RESOLUTION_HD);
        } else if (addDownloadDto.rbHigh.isSelected()) {
            setResolution(FilmDataMTP.RESOLUTION_NORMAL);
        } else {
            setResolution(FilmDataMTP.RESOLUTION_SMALL);
        }
        addDownloadDto.updateAct();
    }

    private void storeRes() {
        if (addDownloadDto.rbHd.isSelected()) {
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmDataMTP.RESOLUTION_HD);
        } else if (addDownloadDto.rbHigh.isSelected()) {
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmDataMTP.RESOLUTION_NORMAL);
        } else {
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmDataMTP.RESOLUTION_SMALL);
        }
    }

    private void setResolution(String resolution) {
        if (addDownloadDto.chkResolutionAll.isSelected()) {
            Arrays.stream(addDownloadDto.addDownloadData).forEach(addDownloadData -> {
                if (resolution.equals(FilmDataMTP.RESOLUTION_HD) && addDownloadData.download.isHd()) {
                    addDownloadData.download.setResolution(FilmDataMTP.RESOLUTION_HD);

                } else if (resolution.equals(FilmDataMTP.RESOLUTION_SMALL) && addDownloadDto.getAct().download.isSmall()) {
                    addDownloadData.download.setResolution(FilmDataMTP.RESOLUTION_SMALL);

                } else {
                    addDownloadData.download.setResolution(FilmDataMTP.RESOLUTION_NORMAL);
                }
                InitProgramCall.setProgrammCall(addDownloadDto, addDownloadData);
            });

        } else {
            addDownloadDto.getAct().download.setResolution(resolution);
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

        addDownloadDto.rbHd.setDisable(!addDownloadDto.getAct().download.isHd());
        addDownloadDto.rbSmall.setDisable(!addDownloadDto.getAct().download.isSmall());

        switch (addDownloadDto.getAct().download.getResolution()) {
            case FilmDataMTP.RESOLUTION_HD -> addDownloadDto.rbHd.setSelected(true);
            case FilmDataMTP.RESOLUTION_SMALL -> addDownloadDto.rbSmall.setSelected(true);
            default -> addDownloadDto.rbHigh.setSelected(true);
        }

        String textHd = "HD";
        if (!addDownloadDto.rbHd.isDisable() && !addDownloadDto.getAct().download.getFilmSizeHd().isEmpty()) {
            addDownloadDto.rbHd.setText(textHd + P2LibConst.LINE_SEPARATOR +
                    "[ " + addDownloadDto.getAct().download.getFilmSizeHd() + " MB ]");
        } else {
            addDownloadDto.rbHd.setText(textHd);
        }

        String textHeight = "hohe Auflösung";
        if (!addDownloadDto.getAct().download.getFilmSizeNormal().isEmpty()) {
            addDownloadDto.rbHigh.setText(textHeight + P2LibConst.LINE_SEPARATOR +
                    "[ " + addDownloadDto.getAct().download.getFilmSizeNormal() + " MB ]");
        } else {
            addDownloadDto.rbHigh.setText(textHeight);
        }

        String textLow = "niedrige Auflösung";
        if (!addDownloadDto.rbSmall.isDisable() && !addDownloadDto.getAct().download.getFilmSizeSmall().isEmpty()) {
            addDownloadDto.rbSmall.setText(textLow + P2LibConst.LINE_SEPARATOR +
                    "[ " + addDownloadDto.getAct().download.getFilmSizeSmall() + " MB ]");
        } else {
            addDownloadDto.rbSmall.setText(textLow);
        }

        addDownloadDto.rbHigh.setOnAction(onAction);
        addDownloadDto.rbHd.setOnAction(onAction);
        addDownloadDto.rbSmall.setOnAction(onAction);
    }
}
