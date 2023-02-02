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


package de.p2tools.mtplayer.gui.dialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.gui.configDialog.BlackListPane;
import de.p2tools.mtplayer.gui.configDialog.BlackPane;
import de.p2tools.mtplayer.gui.configDialog.ConfigDialogController;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collection;

public class BlackDialog extends PDialogExtra {

    private final ProgData progData;
    private final BooleanProperty blackPaneChanged = new SimpleBooleanProperty(false);
    private final BooleanProperty blackListPaneChanged = new SimpleBooleanProperty(false);
    private BlackPane blackPane;
    private BlackListPane blackListPane;
    private final Button btnOk = new Button("_Ok");
    private final Button btnApply = new Button("_Anwenden");

    public BlackDialog(ProgData progData) {
        super(progData.primaryStage, ProgConfig.BLACK_DIALOG_SIZE,
                "Blacklist bearbeiten", false, false, DECO.NONE, true);
        this.progData = progData;

        initDialog();
        init(false);
        blackPane.setStage(getStage());
        blackListPane.setStage(getStage());
        super.showDialog();
    }

    @Override
    public void close() {
        blackPane.close();
        blackListPane.close();
        super.close();
    }

    private void initDialog() {
        Accordion accordion = new Accordion();
        Collection<TitledPane> titledPanes = new ArrayList<>();

        blackPane = new BlackPane(getStage(), blackPaneChanged);
        blackPane.makeBlack(titledPanes);
        blackListPane = new BlackListPane(getStage(), progData, true, blackListPaneChanged);
        blackListPane.make(titledPanes);
        accordion.getPanes().addAll(titledPanes);
        accordion.setExpandedPane(accordion.getPanes().get(0));
        VBox.setVgrow(accordion, Priority.ALWAYS);
        VBox vBox = getVBoxCont();
        vBox.setPadding(new Insets(0));
        vBox.getChildren().add(accordion);

        addOkCancelApplyButtons(btnOk, null, btnApply);
        btnOk.setOnAction(a -> {
            if (blackPaneChanged.getValue() || blackListPaneChanged.getValue()) {
                apply();
            }
            close();
        });
        btnApply.setOnAction(a -> {
            if (blackPaneChanged.getValue() || blackListPaneChanged.getValue()) {
                apply();
            }
        });
    }

    private void apply() {
        if ((blackPaneChanged.get() || blackListPaneChanged.getValue())
                && !LoadFilmFactory.loadFilmlist.getPropLoadFilmlist()) {
            // sonst hat sich nichts geändert oder wird dann eh gemacht
            progData.filmlist.filterListWithBlacklist(true);
            Listener.notify(Listener.EVENT_BLACKLIST_CHANGED, ConfigDialogController.class.getSimpleName());
        }
    }
}
