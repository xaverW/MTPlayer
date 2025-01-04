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
import de.p2tools.mtplayer.gui.configdialog.configpanes.PaneFilmDouble;
import de.p2tools.mtplayer.gui.configdialog.configpanes.PaneFilmLoad;
import de.p2tools.mtplayer.gui.configdialog.configpanes.PaneFilmSender;
import de.p2tools.mtplayer.gui.configdialog.configpanes.PaneFilmUt;
import de.p2tools.mtplayer.gui.configdialog.paneblacklist.PaneBlackList;
import de.p2tools.p2lib.dialogs.accordion.P2AccordionPane;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class ControllerLoadFilmList extends P2AccordionPane {

    private PaneFilmLoad paneFilmLoad;
    private PaneFilmDouble paneFilmDouble;
    private PaneFilmUt paneFilmUt;
    private PaneFilmUt paneFilmMark;
    private PaneBlackList paneBlackList;
    private PaneFilmSender paneFilmSender;
    private final BooleanProperty diacriticChanged;

    private final ProgData progData;
    private final Stage stage;

    public ControllerLoadFilmList(Stage stage, BooleanProperty diacriticChanged) {
        super(ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_FILM);
        this.stage = stage;
        this.diacriticChanged = diacriticChanged;
        progData = ProgData.getInstance();

        init();
    }

    @Override
    public void close() {
        paneFilmLoad.close();
        paneFilmDouble.close();
        paneFilmUt.close();
        paneFilmMark.close();
        paneBlackList.close();
        paneFilmSender.close();
        super.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        paneFilmLoad = new PaneFilmLoad(stage, progData, diacriticChanged);
        paneFilmLoad.make(result);

        paneFilmDouble = new PaneFilmDouble(stage, progData);
        paneFilmDouble.make(result);

        paneFilmUt = new PaneFilmUt(stage, true);
        paneFilmUt.make(result);

        paneFilmMark = new PaneFilmUt(stage, false);
        paneFilmMark.make(result);

        paneBlackList = new PaneBlackList(stage, progData, false, new SimpleBooleanProperty());
        paneBlackList.make(result);

        paneFilmSender = new PaneFilmSender(stage);
        paneFilmSender.make(result);

        return result;
    }
}