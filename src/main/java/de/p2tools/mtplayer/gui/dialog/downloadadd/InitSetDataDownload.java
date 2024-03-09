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

import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactoryMakeParameter;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.Arrays;

public class InitSetDataDownload {
    private final AddDownloadDto addDownloadDto;
    private EventHandler<ActionEvent> onAction;

    public InitSetDataDownload(AddDownloadDto addDownloadDto) {
        this.addDownloadDto = addDownloadDto;
        init(addDownloadDto.setDataStart);
    }

    private void init(SetData setData) {
        onAction = (a) -> {
            makeSetDataChange();
        };
        addDownloadDto.cboSetData.setOnAction(onAction);

        if (addDownloadDto.progData.setDataList.getSetDataListSave().size() > 1) {
            // nur dann machts Sinn
            addDownloadDto.cboSetData.getItems().addAll(addDownloadDto.progData.setDataList.getSetDataListSave());
            addDownloadDto.cboSetData.getSelectionModel().select(setData);
        }
    }

    public void makeAct() {
        addDownloadDto.cboSetData.setDisable(addDownloadDto.getAct().downloadIsRunning());

        addDownloadDto.cboSetData.setOnAction(null);
        addDownloadDto.cboSetData.getSelectionModel().select(addDownloadDto.getAct().download.getSetData());
        addDownloadDto.cboSetData.setOnAction(onAction);
    }


    public void makeSetDataChange() {
        if (addDownloadDto.chkSetAll.isSelected()) {
            Arrays.stream(addDownloadDto.addDownloadData).forEach(this::makeSetDataChange);
        } else {
            makeSetDataChange(addDownloadDto.getAct());
        }
        addDownloadDto.updateAct();
    }

    private void makeSetDataChange(AddDownloadData addDownloadData) {
        SetData psetData = addDownloadDto.cboSetData.getSelectionModel().getSelectedItem();
        if (addDownloadData.download.getSetData() == psetData) {
            // dann passts eh
            return;
        }

        addDownloadData.download.setSetData(psetData, false);

        // müssen Pfad/Name/Aufruf neu gebaut werden
        DownloadData download = addDownloadData.download;
        DownloadFactoryMakeParameter.makeProgParameter(download, download.getAbo(), "", "");
        addDownloadDto.textAreaProg.setText(addDownloadDto.getAct().download.getProgramCall());
        addDownloadDto.textAreaCallArray.setText(addDownloadDto.getAct().download.getProgramCallArray());
    }
}
