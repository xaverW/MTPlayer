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

import de.p2tools.mtplayer.controller.film.FilmDataMTP;

import java.util.Arrays;

public class InitResolution {
    private final AddAboDto addAboDto;

    public InitResolution(AddAboDto addAboDto) {
        this.addAboDto = addAboDto;
        init();
    }

    private void init() {
        addAboDto.rbHd.setOnAction(a -> setRes());
        addAboDto.rbHigh.setOnAction(a -> setRes());
        addAboDto.rbLow.setOnAction(a -> setRes());
    }

    public void makeAct() {
        // nach dem actFilm setzen, z.B. beim Wechsel
        switch (addAboDto.getAct().abo.getResolution()) {
            case FilmDataMTP.RESOLUTION_HD -> addAboDto.rbHd.setSelected(true);
            case FilmDataMTP.RESOLUTION_SMALL -> addAboDto.rbLow.setSelected(true);
            default -> addAboDto.rbHigh.setSelected(true);
        }
    }

    public void setRes() {
        if (addAboDto.rbHd.isSelected()) {
            setResolution(FilmDataMTP.RESOLUTION_HD);

        } else if (addAboDto.rbHigh.isSelected()) {
            setResolution(FilmDataMTP.RESOLUTION_NORMAL);

        } else {
            setResolution(FilmDataMTP.RESOLUTION_SMALL);
        }
    }

    private void setResolution(String resolution) {
        if (addAboDto.chkResolutionAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(downloadAddData -> {
                downloadAddData.abo.setResolution(resolution);
            });

        } else {
            addAboDto.getAct().abo.setResolution(resolution);
        }
    }
}
