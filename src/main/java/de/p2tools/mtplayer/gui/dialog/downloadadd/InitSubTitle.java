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

import java.util.Arrays;

public class InitSubTitle {

    private final AddDto addDto;

    public InitSubTitle(AddDto addDto) {
        this.addDto = addDto;
        init();
    }

    private void init() {
        makeAct();
        addDto.chkInfo.setOnAction(a -> setInfoSubTitle());
        addDto.chkInfoAll.setOnAction(a -> {
            if (addDto.chkInfoAll.isSelected()) {
                setInfoSubTitle();
            }
        });
        addDto.chkSubtitle.setOnAction(a -> setInfoSubTitle());
        addDto.chkSubTitleAll.setOnAction(a -> {
            if (addDto.chkSubTitleAll.isSelected()) {
                setInfoSubTitle();
            }
        });
    }

    public void makeAct() {
        addDto.chkInfo.setDisable(addDto.getAct().downloadIsRunning());
        addDto.chkSubtitle.setDisable(addDto.getAct().downloadIsRunning());

        addDto.chkInfo.setSelected(addDto.getAct().download.getInfoFile());
        addDto.chkSubtitle.setDisable(addDto.getAct().subIsDisabled);
        addDto.chkSubtitle.setSelected(addDto.getAct().download.isSubtitle());
    }

    private void setInfoSubTitle() {
        // Info
        if (addDto.chkInfoAll.isSelected()) {
            Arrays.stream(addDto.downloadAddData).forEach(downloadAddData ->
                    downloadAddData.download.setInfoFile(addDto.chkInfo.isSelected()));
        } else {
            addDto.getAct().download.setInfoFile(addDto.chkInfo.isSelected());
        }

        // SubTitle
        if (addDto.chkSubTitleAll.isSelected()) {
            Arrays.stream(addDto.downloadAddData).forEach(downloadAddData -> {
                if (downloadAddData.subIsDisabled) {
                    // dann immer false, gibts nicht
                    downloadAddData.download.setSubtitle(false);
                } else {
                    downloadAddData.download.setSubtitle(addDto.chkSubtitle.isSelected());
                }
            });

        } else {
            if (addDto.getAct().subIsDisabled) {
                // dann immer false, gibts nicht
                addDto.getAct().download.setSubtitle(false);
            } else {
                addDto.getAct().download.setSubtitle(addDto.chkSubtitle.isSelected());
            }
        }
    }
}
