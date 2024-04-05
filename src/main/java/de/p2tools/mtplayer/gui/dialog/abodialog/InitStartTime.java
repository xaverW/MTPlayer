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

import java.util.Arrays;

public class InitStartTime {

    private final AddAboDto addAboDto;

    public InitStartTime(AddAboDto addAboDto) {
        this.addAboDto = addAboDto;
        init();
    }

    private void init() {
        addAboDto.p2TimePicker.getSelectionModel().selectFirst();
        addAboDto.p2TimePicker.disableProperty().bind(addAboDto.chkStartTime.selectedProperty().not());

        addAboDto.chkStartTime.setOnAction(a -> setStartTimePick());
        addAboDto.p2TimePicker.setOnAction(a -> setStartTimePick());
    }

    public void makeAct() {
        addAboDto.chkStartTime.setSelected(!addAboDto.getAct().abo.getStartTime().isEmpty());
        if (addAboDto.chkStartTime.isSelected()) {
            addAboDto.p2TimePicker.setTime(addAboDto.getAct().abo.getStartTime());
        }
    }

    public void setStartTimePick() {
        if (addAboDto.chkStartTimeAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(this::setTime);
        } else {
            setTime(addAboDto.getAct());
        }
    }

    private void setTime(AddAboData addAboDto) {
        if (this.addAboDto.chkStartTime.isSelected()) {
            addAboDto.abo.setStartTime(this.addAboDto.p2TimePicker.getTime());
        } else {
            addAboDto.abo.setStartTime("");
        }
    }
}
