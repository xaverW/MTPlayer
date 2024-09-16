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


package de.p2tools.mtplayer.gui.infoPane;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class InfoPaneDialog extends P2DialogExtra {
    private final Pane pane;
    private final BooleanProperty dialogIsRip; // wird beim Beenden ausgeschaltet
    private final BooleanProperty tabIsShowing; // Film, Abo, ..
    private final ChangeListener<? super Boolean> tabListener = (ChangeListener<Boolean>) (observable, oldValue, newValue) ->
            setVis();

    public InfoPaneDialog(Pane pane, String title,
                          StringProperty sizeProperty,
                          BooleanProperty dialogIsRip,
                          BooleanProperty tabIsShowing) {

        super(ProgData.getInstance().primaryStage, sizeProperty, title,
                false, false, DECO.NO_BORDER);

        this.pane = pane;
        this.dialogIsRip = dialogIsRip; // zeigt an, ob Dialog zu sehen
        this.tabIsShowing = tabIsShowing;

        init(this.tabIsShowing.get());
        this.tabIsShowing.addListener(tabListener);
        setVis();
    }

    @Override
    public void make() {
        VBox.setVgrow(pane, Priority.ALWAYS);
        getVBoxCont().setPadding(new Insets(0));
        getVBoxCont().getChildren().add(pane);
    }

    @Override
    public void close() {
        super.close();
        tabIsShowing.removeListener(tabListener);
        dialogIsRip.set(false);
    }

    private void setVis() {
        if (tabIsShowing.get()) {
            showDialog();
        } else {
            getStage().hide();
        }
    }
}
