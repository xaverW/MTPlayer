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

package de.p2tools.mtplayer.gui.configdialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.gui.configpanes.PaneMediaConfig;
import de.p2tools.mtplayer.gui.configpanes.PaneMediaDataPath;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
import de.p2tools.mtplayer.gui.mediadialog.PaneDialogMedia;
import de.p2tools.p2lib.dialogs.accordion.P2AccordionPane;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class ControllerMedia extends P2AccordionPane {

    private PaneMediaConfig paneMediaConfig;
    private PaneMediaDataPath panePathIntern;
    private PaneMediaDataPath panePathExtern;
    private PaneDialogMedia paneDialogMedia;

    private final Stage stage;

    public ControllerMedia(Stage stage) {
        super(ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_MEDIA);
        this.stage = stage;
        init();
    }

    @Override
    public void close() {
        paneMediaConfig.close();
        panePathIntern.close();
        panePathExtern.close();
        paneDialogMedia.close();
        super.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();

        paneMediaConfig = new PaneMediaConfig(stage);
        paneMediaConfig.make(result);

        panePathIntern = new PaneMediaDataPath(stage, false);
        panePathIntern.make(result);

        panePathExtern = new PaneMediaDataPath(stage, true);
        panePathExtern.make(result);

        MediaDataDto mediaDataDtoMedia = new MediaDataDto();
        mediaDataDtoMedia.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_MEDIA;
        mediaDataDtoMedia.buildSearchFrom = ProgConfig.DIALOG_BUILD_SEARCH_FROM_FOR_MEDIA;
        mediaDataDtoMedia.searchInWhat = ProgConfig.DIALOG_SEARCH_IN_WHAT_FOR_MEDIA;
        paneDialogMedia = new PaneDialogMedia(stage, mediaDataDtoMedia);
        paneDialogMedia.make(result);

        return result;
    }
}

