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

    private final AddDownloadDto addDownloadDto;

    public InitSubTitle(AddDownloadDto addDownloadDto) {
        this.addDownloadDto = addDownloadDto;
        init();
    }

    private void init() {
        addDownloadDto.chkInfo.setOnAction(a -> setInfoSubTitle());
        addDownloadDto.chkSubtitle.setOnAction(a -> setInfoSubTitle());
    }

    public void makeAct() {
        addDownloadDto.chkInfo.setDisable(addDownloadDto.getAct().downloadIsRunning());
        addDownloadDto.chkSubtitle.setDisable(addDownloadDto.getAct().downloadIsRunning());

        addDownloadDto.chkInfo.setSelected(addDownloadDto.getAct().download.getInfoFile());
        addDownloadDto.chkSubtitle.setDisable(addDownloadDto.getAct().download.getUrlSubtitle().isEmpty());
        addDownloadDto.chkSubtitle.setSelected(addDownloadDto.getAct().download.isSubtitle());
    }

    public void setInfoSubTitle() {
        // Info
        if (addDownloadDto.chkInfoAll.isSelected()) {
            Arrays.stream(addDownloadDto.addDownloadData).forEach(downloadAddData ->
                    downloadAddData.download.setInfoFile(addDownloadDto.chkInfo.isSelected()));
        } else {
            addDownloadDto.getAct().download.setInfoFile(addDownloadDto.chkInfo.isSelected());
        }

        // SubTitle
        if (addDownloadDto.chkSubTitleAll.isSelected()) {
            Arrays.stream(addDownloadDto.addDownloadData).forEach(downloadAddData -> {
                if (downloadAddData.download.getUrlSubtitle().isEmpty()) {
                    // dann immer false, gibts nicht
                    downloadAddData.download.setSubtitle(false);
                } else {
                    downloadAddData.download.setSubtitle(addDownloadDto.chkSubtitle.isSelected());
                }
            });

        } else {
            if (addDownloadDto.getAct().download.getUrlSubtitle().isEmpty()) {
                // dann immer false, gibts nicht
                addDownloadDto.getAct().download.setSubtitle(false);
            } else {
                addDownloadDto.getAct().download.setSubtitle(addDownloadDto.chkSubtitle.isSelected());
            }
        }
    }
}
