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
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class InfoPaneDialog extends PDialogExtra {
    private final Pane pane;
    private final BooleanProperty onProperty;
    private final BooleanProperty infoOnProperty;
    private final BooleanProperty tabIsOnProperty;
    ChangeListener<? super Boolean> l = (ChangeListener<Boolean>) (observable, oldValue, newValue) ->
            setVis(newValue);

    public InfoPaneDialog(Pane pane, String title,
                          StringProperty sizeProperty, BooleanProperty onProperty, BooleanProperty infoOnProperty,
                          BooleanProperty tabIsOnProperty) {
        super(ProgData.getInstance().primaryStage, sizeProperty, title,
                false, false, DECO.NO_BORDER);

        this.pane = pane;
        this.tabIsOnProperty = tabIsOnProperty;
        this.onProperty = onProperty;// zeigt an, ob Dialog zu sehen
        this.infoOnProperty = infoOnProperty;// infoPane anzeigen

        init(this.tabIsOnProperty.getValue());
        this.onProperty.setValue(true);
        this.tabIsOnProperty.addListener(l);
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
        tabIsOnProperty.removeListener(l);
        onProperty.setValue(false);
        infoOnProperty.setValue(true);
    }

    private void setVis(boolean value) {
        if (value) {
            showDialog();
        } else {
            getStage().hide();
        }
    }
}
