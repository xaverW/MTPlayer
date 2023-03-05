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
import de.p2tools.mtplayer.gui.configpanes.*;
import de.p2tools.p2lib.dialogs.accordion.PAccordionPane;
import de.p2tools.p2lib.dialogs.dialog.PDialog;
import javafx.scene.control.TitledPane;

import java.util.ArrayList;
import java.util.Collection;

public class ControllerConfig extends PAccordionPane {

    private PaneConfig paneConfig;
    private PaneIcon paneIcon;
    private PaneColor paneColor;
    private PaneShortcut paneShortcut;
    private PaneGeo paneGeo;
    private PaneKeySize paneKeySize;
    private PaneUpdate paneUpdate;
    private PaneProgs paneProgs;
    private PaneLogfile paneLogfile;

    private final PDialog pDialog;
    private final ProgData progData;

    public ControllerConfig(PDialog pDialog) {
        super(ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_CONFIG);
        this.pDialog = pDialog;
        this.progData = ProgData.getInstance();
        init();
    }

    @Override
    public void close() {
        paneConfig.close();
        paneIcon.close();
        paneColor.close();
        paneShortcut.close();
        paneGeo.close();
        paneKeySize.close();
        paneProgs.close();
        paneLogfile.close();
        paneUpdate.close();
        super.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();

        paneConfig = new PaneConfig(pDialog);
        paneConfig.makeConfig(result);

        paneIcon = new PaneIcon(pDialog);
        paneIcon.makeIcon(result);

        paneLogfile = new PaneLogfile(pDialog);
        paneLogfile.makeLogfile(result);

        paneColor = new PaneColor(pDialog);
        paneColor.makeColor(result);

        paneShortcut = new PaneShortcut(pDialog);
        paneShortcut.makeShortcut(result);

        paneGeo = new PaneGeo(pDialog);
        paneGeo.makeGeo(result);

        paneKeySize = new PaneKeySize(pDialog, progData);
        paneKeySize.makeStyle(result);

        paneProgs = new PaneProgs(pDialog);
        paneProgs.makeProg(result);

        paneUpdate = new PaneUpdate(pDialog);
        paneUpdate.makeUpdate(result);
        return result;
    }
}
