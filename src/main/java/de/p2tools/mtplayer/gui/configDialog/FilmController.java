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

package de.p2tools.mtplayer.gui.configDialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2Lib.dialogs.accordion.PAccordionPane;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class FilmController extends PAccordionPane {

    private FilmLoad filmLoad;
    private BlackListPane blackListPane;
    private FilmSender filmSender;
    private final BooleanProperty diacriticChanged;

    private final ProgData progData;
    private final Stage stage;

    public FilmController(Stage stage, BooleanProperty diacriticChanged) {
        super(stage, ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_FILM);
        this.stage = stage;
        this.diacriticChanged = diacriticChanged;
        progData = ProgData.getInstance();

        init();
    }

    @Override
    public void close() {
        filmLoad.close();
        blackListPane.close();
        filmSender.close();
        super.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        filmLoad = new FilmLoad(stage, progData, diacriticChanged);
        filmLoad.make(result);

        blackListPane = new BlackListPane(stage, progData, false, new SimpleBooleanProperty());
        blackListPane.make(result);

        filmSender = new FilmSender(stage, false);
        filmSender.make(result);

        return result;
    }
}