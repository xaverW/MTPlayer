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


package de.p2tools.mtplayer.gui.dialog.abodialog;

import de.p2tools.mtplayer.controller.data.setdata.SetData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.Arrays;

public class InitSetDataAbo {
    private final AddAboDto addAboDto;
    private EventHandler<ActionEvent> onAction;

    public InitSetDataAbo(AddAboDto addAboDto) {
        this.addAboDto = addAboDto;
        init();
    }

    private void init() {
        onAction = (a) -> {
            makeSetDataChange();
        };
        addAboDto.cboSetData.setOnAction(onAction);

        if (addAboDto.progData.setDataList.getSetDataListSave().size() > 1) {
            // nur dann machts Sinn
            addAboDto.cboSetData.getItems().addAll(addAboDto.progData.setDataList.getSetDataListSave());
            addAboDto.cboSetData.getSelectionModel().select(addAboDto.getAct().abo.getSetData());
        }
    }

    public void makeAct() {
        addAboDto.cboSetData.setOnAction(null);
        addAboDto.cboSetData.getSelectionModel().select(addAboDto.getAct().abo.getSetData());
        addAboDto.cboSetData.setOnAction(onAction);

        // und jetzt den StandardPfad setzen
        if (addAboDto.getAct().abo.getSetData().isGenAboSubDir()) {
            addAboDto.lblSetSubDir.setText(addAboDto.getAct().abo.getSetData().getAboSubDir_ENSubDir_Name());
        } else {
            addAboDto.lblSetSubDir.setText("");
        }
    }


    public void makeSetDataChange() {
        if (addAboDto.chkSetAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(this::makeSetDataChange);
        } else {
            makeSetDataChange(addAboDto.getAct());
        }
        addAboDto.updateAct();
    }

    private void makeSetDataChange(AddAboData addAboData) {
        SetData psetData = this.addAboDto.cboSetData.getSelectionModel().getSelectedItem();
        if (addAboData.abo.getSetData() == psetData) {
            // dann passts eh
            return;
        }

        addAboData.abo.setSetData(psetData);
    }
}
