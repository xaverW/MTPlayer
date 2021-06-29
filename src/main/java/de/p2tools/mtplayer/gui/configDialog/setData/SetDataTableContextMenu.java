/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
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

package de.p2tools.mtplayer.gui.configDialog.setData;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.SetData;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class SetDataTableContextMenu {

    ProgData progData;

    public SetDataTableContextMenu(ProgData progData) {
        this.progData = progData;
    }

    public ContextMenu getContextMenu(SetData setData) {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, setData);
        return contextMenu;
    }

    private void getMenu(ContextMenu contextMenu, SetData setData) {
        MenuItem miPlay = new MenuItem("\"Abspielen\" setzen");

        MenuItem miSave = new MenuItem("\"Speichern\" " + (setData.isSave() ? "löschen" : "setzen"));
        MenuItem miAbo = new MenuItem("\"Abo\" " + (setData.isAbo() ? "löschen" : "setzen"));
        MenuItem miButton = new MenuItem("\"Button\" " + (setData.isButton() ? "löschen" : "setzen"));

        miPlay.setOnAction(a -> progData.setDataList.setPlay(setData));
        miPlay.setDisable(setData.isPlay());

        miSave.setOnAction(a -> setData.setSave(!setData.isSave()));
        miAbo.setOnAction(a -> setData.setAbo(!setData.isAbo()));
        miButton.setOnAction(a -> setData.setButton(!setData.isButton()));

        contextMenu.getItems().addAll(miPlay, miSave, miAbo, miButton);
    }
}
