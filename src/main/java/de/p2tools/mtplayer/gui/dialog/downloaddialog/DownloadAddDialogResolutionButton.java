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


package de.p2tools.mtplayer.gui.dialog.downloaddialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class DownloadAddDialogResolutionButton {
    private final RadioButton rbHd;
    private final RadioButton rbHigh;
    private final RadioButton rbSmall;
    private final CheckBox chkResolutionAll;
    private final String textHd = "HD";
    private final String textHeight = "hohe Auflösung";
    private final String textLow = "niedrige Auflösung";
    private DownloadAddData[] downloadAddInfosArr;

    public DownloadAddDialogResolutionButton(RadioButton rbHd, RadioButton rbHigh, RadioButton rbSmall,
                                             CheckBox chkResolutionAll,
                                             DownloadAddData[] downloadAddInfosArr) {
        this.rbHd = rbHd;
        this.rbHigh = rbHigh;
        this.rbSmall = rbSmall;
        this.chkResolutionAll = chkResolutionAll;
        this.downloadAddInfosArr = downloadAddInfosArr;
    }

    public void initResolutionButton(int actFilmIsShown) {
        final ToggleGroup toggleGroupSize = new ToggleGroup();
        rbHd.setToggleGroup(toggleGroupSize);
        rbHigh.setToggleGroup(toggleGroupSize);
        rbSmall.setToggleGroup(toggleGroupSize);

        // und jetzt für den aktuellen Film das GUI setzen
        makeActResolutionButtons(actFilmIsShown);
    }

    public void setRes(int actFilmIsShown) {
        if (rbHd.isSelected()) {
            downloadAddInfosArr[actFilmIsShown].setResolution(FilmDataMTP.RESOLUTION_HD, chkResolutionAll.isSelected());
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmDataMTP.RESOLUTION_HD);

        } else if (rbHigh.isSelected()) {
            downloadAddInfosArr[actFilmIsShown].setResolution(FilmDataMTP.RESOLUTION_NORMAL, chkResolutionAll.isSelected());
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmDataMTP.RESOLUTION_NORMAL);

        } else {
            downloadAddInfosArr[actFilmIsShown].setResolution(FilmDataMTP.RESOLUTION_SMALL, chkResolutionAll.isSelected());
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmDataMTP.RESOLUTION_SMALL);
        }
    }

    public void makeActResolutionButtons(int actFilmIsShown) {
        rbHd.setDisable(!downloadAddInfosArr[actFilmIsShown].film.isHd());
        rbSmall.setDisable(!downloadAddInfosArr[actFilmIsShown].film.isSmall());

        switch (downloadAddInfosArr[actFilmIsShown].resolution) {
            case FilmDataMTP.RESOLUTION_HD -> rbHd.setSelected(true);
            case FilmDataMTP.RESOLUTION_SMALL -> rbSmall.setSelected(true);
            default -> rbHigh.setSelected(true);
        }

        if (!rbHd.isDisable() && !downloadAddInfosArr[actFilmIsShown].fileSize_HD.isEmpty()) {
            rbHd.setText(textHd + "   [ " + downloadAddInfosArr[actFilmIsShown].fileSize_HD + " MB ]");
        } else {
            rbHd.setText(textHd);
        }

        if (!downloadAddInfosArr[actFilmIsShown].fileSize_high.isEmpty()) {
            rbHigh.setText(textHeight + "   [ " + downloadAddInfosArr[actFilmIsShown].fileSize_high + " MB ]");
        } else {
            rbHigh.setText(textHeight);
        }

        if (!rbSmall.isDisable() && !downloadAddInfosArr[actFilmIsShown].fileSize_small.isEmpty()) {
            rbSmall.setText(textLow + "   [ " + downloadAddInfosArr[actFilmIsShown].fileSize_small + " MB ]");
        } else {
            rbSmall.setText(textLow);
        }
    }
}
