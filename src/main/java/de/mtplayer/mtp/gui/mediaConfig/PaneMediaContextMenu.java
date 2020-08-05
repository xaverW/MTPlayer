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

package de.mtplayer.mtp.gui.mediaConfig;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.mediaDb.MediaData;
import de.p2tools.p2Lib.guiTools.POpen;
import de.p2tools.p2Lib.tools.file.PFileUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class PaneMediaContextMenu {

    private final ProgData progData;
    private final MediaData mediaData;
    private final Stage stage;

    public PaneMediaContextMenu(Stage stage, MediaData mediaData) {
        this.stage = stage;
        this.mediaData = mediaData;
        this.progData = ProgData.getInstance();
    }

    public ContextMenu getContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu);
        return contextMenu;
    }

    private void getMenu(ContextMenu contextMenu) {
        MenuItem miPlay = new MenuItem("gespeicherten Film abspielen");
        miPlay.setOnAction(a -> {
            String path = mediaData.getPath();
            String name = mediaData.getName();
            if (!path.isEmpty() && !name.isEmpty()) {
                POpen.playStoredFilm(PFileUtils.addsPath(path, name),
                        ProgConfig.SYSTEM_PROG_PLAY_FILME.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
            }
        });

        contextMenu.getItems().addAll(miPlay);
    }

}
