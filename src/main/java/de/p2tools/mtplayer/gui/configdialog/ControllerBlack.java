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
import de.p2tools.mtplayer.gui.configpanes.PaneBlack;
import de.p2tools.mtplayer.gui.configpanes.PaneBlackList;
import de.p2tools.p2lib.dialogs.accordion.PAccordionPane;
import de.p2tools.p2lib.dialogs.dialog.PDialog;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TitledPane;

import java.util.ArrayList;
import java.util.Collection;

public class ControllerBlack extends PAccordionPane {

    private final ProgData progData;

    private final BooleanProperty blackChanged;
    private PaneBlack paneBlack;
    private PaneBlackList paneBlackList;
    private final PDialog pDialog;

    public ControllerBlack(PDialog pDialog, BooleanProperty blackChanged) {
        super(ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_BLACKLIST);
        this.pDialog = pDialog;
        this.blackChanged = blackChanged;
        progData = ProgData.getInstance();

        init();
    }

    @Override
    public void close() {
        super.close();
        paneBlack.close();
        paneBlackList.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        paneBlack = new PaneBlack(pDialog, blackChanged);
        paneBlack.makeBlack(result);
        paneBlackList = new PaneBlackList(pDialog, progData, true, blackChanged);
        paneBlackList.make(result);
        return result;
    }
}