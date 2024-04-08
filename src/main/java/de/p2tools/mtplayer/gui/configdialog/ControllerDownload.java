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
import de.p2tools.mtplayer.gui.configpanes.PaneDownload;
import de.p2tools.mtplayer.gui.configpanes.PaneDownloadStop;
import de.p2tools.mtplayer.gui.configpanes.PaneReplace;
import de.p2tools.p2lib.dialogs.accordion.P2AccordionPane;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class ControllerDownload extends P2AccordionPane {

    private PaneDownload paneDownload;
    private PaneDownloadStop paneDownloadStop;
    private PaneReplace paneReplace;

    private final Stage stage;

    public ControllerDownload(Stage stage) {
        super(ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_DOWNLOAD);
        this.stage = stage;
        init();
    }

    @Override
    public void close() {
        paneDownload.close();
        paneDownloadStop.close();
        paneReplace.close();
        super.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        paneDownload = new PaneDownload(stage);
        paneDownload.makeDownload(result);

        paneDownloadStop = new PaneDownloadStop(stage);
        paneDownloadStop.makeDownload(result);

        paneReplace = new PaneReplace(stage);
        paneReplace.makeReplaceListTable(result);
        return result;
    }
}

