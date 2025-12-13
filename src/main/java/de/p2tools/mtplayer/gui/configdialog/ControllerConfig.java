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
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.configdialog.configpanes.*;
import de.p2tools.p2lib.dialogs.accordion.P2AccordionPane;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class ControllerConfig extends P2AccordionPane {

    private PaneConfig paneConfig;
    private PaneAbo paneAbo;
    private PaneFilter paneFilter;
    private PaneIcon paneIcon;
    private PaneColorIcon paneColorIcon;
    private PaneColorTable paneColorTable;
    private PaneStatusBar paneStatusBar;
    private PaneShortcut paneShortcut;
    private PaneGeo paneGeo;
    private PaneProxy paneProxy;
    private PaneKeySize paneKeySize;
    private PaneUpdate paneUpdate;
    private PaneProgs paneProgs;
    private PaneLogFile paneLogFile;

    private final Stage stage;
    private final ProgData progData;

    public ControllerConfig(Stage stage) {
        super(ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_CONFIG);
        this.stage = stage;
        this.progData = ProgData.getInstance();
        init();
    }

    @Override
    public void close() {
        paneConfig.close();
        paneAbo.close();
        paneFilter.close();
        paneIcon.close();
        paneColorIcon.close();
        paneColorTable.close();
        paneStatusBar.close();
        paneShortcut.close();
        paneGeo.close();
        paneProxy.close();
        paneKeySize.close();
        paneProgs.close();
        paneLogFile.close();
        paneUpdate.close();
        super.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<>();

        paneConfig = new PaneConfig(stage);
        paneConfig.make(result);

        paneAbo = new PaneAbo(stage);
        paneAbo.make(result);

        paneFilter = new PaneFilter(stage);
        paneFilter.make(result);

        paneIcon = new PaneIcon(stage);
        paneIcon.make(result);

        paneLogFile = new PaneLogFile(stage);
        paneLogFile.make(result);

        paneColorIcon = new PaneColorIcon(stage);
        paneColorIcon.make(result);

        paneColorTable = new PaneColorTable(stage);
        paneColorTable.make(result);

        paneStatusBar = new PaneStatusBar(stage);
        paneStatusBar.make(result);

        paneShortcut = new PaneShortcut(stage);
        paneShortcut.make(result);

        paneGeo = new PaneGeo(stage);
        paneGeo.make(result);

        paneProxy = new PaneProxy(stage);
        paneProxy.make(result);

        paneKeySize = new PaneKeySize(stage, progData);
        paneKeySize.make(result);

        paneProgs = new PaneProgs(stage);
        paneProgs.make(result);

        paneUpdate = new PaneUpdate(stage);
        paneUpdate.make(result);
        return result;
    }
}
