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

public class InitName {
    private final AddAboDto addAboDto;

    public InitName(AddAboDto addAboDto) {
        this.addAboDto = addAboDto;
        init();
    }

    private void init() {
        addAboDto.txtName.textProperty().addListener((u, o, n) -> {
            if (!addAboDto.txtName.isFocused()) {
                return;
            }
            addAboDto.getAct().abo.setName(addAboDto.txtName.getText());
        });
    }

    public void makeAct() {
        // nach dem actFilm setzen, z.B. beim Wechsel
        addAboDto.txtName.setText(addAboDto.getAct().abo.getName());
    }
}
