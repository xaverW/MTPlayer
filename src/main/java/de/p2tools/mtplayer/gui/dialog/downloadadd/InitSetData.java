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

import de.p2tools.mtplayer.controller.data.setdata.SetData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.Arrays;

public class InitSetData {
    private final AddDto addDto;
    private EventHandler<ActionEvent> onAction;

    public InitSetData(AddDto addDto) {
        this.addDto = addDto;
        init(addDto.setDataStart);
    }

    private void init(SetData setData) {
        onAction = (a) -> {
            makeSetDataChange();
            addDto.updateAct();
        };
        addDto.cboSetData.setOnAction(onAction);

        addDto.chkSetAll.setOnAction(a -> {
            if (addDto.chkSetAll.isSelected()) {
                addDto.initSetData.makeSetDataChange();
                addDto.updateAct();
            }
        });

        if (addDto.progData.setDataList.getSetDataListSave().size() > 1) {
            // nur dann machts Sinn
            addDto.cboSetData.getItems().addAll(addDto.progData.setDataList.getSetDataListSave());
            addDto.cboSetData.getSelectionModel().select(setData);
        }
    }

    public void makeAct() {
        addDto.cboSetData.setDisable(addDto.getAct().downloadIsRunning());

        addDto.cboSetData.setOnAction(null);
        addDto.cboSetData.getSelectionModel().select(addDto.getAct().setData);
        addDto.cboSetData.setOnAction(onAction);
    }


    private void makeSetDataChange() {
        if (addDto.chkSetAll.isSelected()) {
            Arrays.stream(addDto.downloadAddData).forEach(this::makeSetDataChange);
        } else {
            makeSetDataChange(addDto.getAct());
        }
    }

    private void makeSetDataChange(DownloadAddData downloadAddData) {
        SetData psetData = addDto.cboSetData.getSelectionModel().getSelectedItem();
        if (downloadAddData.setData == psetData) {
            // dann passts eh
            return;
        }

        downloadAddData.setData = psetData;
        downloadAddData.download.setSetData(psetData, false);
        InitProgramCall.setProgrammCall(addDto, downloadAddData);
    }
}
