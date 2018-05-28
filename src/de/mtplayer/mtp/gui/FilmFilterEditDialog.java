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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.gui.dialog.MTDialog;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;


public class FilmFilterEditDialog extends MTDialog {

    final ProgData progData;
    final VBox vbox;

    public FilmFilterEditDialog(ProgData progData) {
        super("", null,
                "Filter ein- und ausschalten", true);

        this.progData = progData;
        vbox = new VBox();
        vbox.setSpacing(20);

        init(vbox, true);
    }

    @Override
    public void make() {
        vbox.setPadding(new Insets(10));

        VBox vBoxCont = new VBox();
        vBoxCont.setSpacing(15);
        VBox.setVgrow(vBoxCont, Priority.ALWAYS);
        vBoxCont.getStyleClass().add("dialog-only-border");

        init(vBoxCont);

        final Button btnHelpAbo = new PButton().helpButton("Filter ein- und ausschalten",
                HelpText.GUI_FILMS_EDIT_FILTER);

        HBox hBox = new HBox();
        hBox.setSpacing(10);

        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        Button btnOk = new Button("Ok");
        btnOk.setOnAction(event -> close());
        hBox.getChildren().addAll(btnHelpAbo, btnOk);

        vbox.getChildren().addAll(vBoxCont, hBox);


    }

    public void init(VBox vbox) {
        VBox v = new VBox();
        ToggleSwitch tglChannel = new ToggleSwitch("Sender");
        tglChannel.setMaxWidth(Double.MAX_VALUE);
        tglChannel.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().channelVisProperty());
        v.getChildren().add(tglChannel);

        ToggleSwitch tglChannelExact = new ToggleSwitch("  -> exakt");
        tglChannelExact.setMaxWidth(Double.MAX_VALUE);
        tglChannelExact.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().channelExactProperty());
        v.getChildren().add(tglChannelExact);
        vbox.getChildren().add(v);

        v = new VBox();
        ToggleSwitch tglTheme = new ToggleSwitch("Thema");
        tglTheme.setMaxWidth(Double.MAX_VALUE);
        tglTheme.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().themeVisProperty());
        v.getChildren().add(tglTheme);

        ToggleSwitch tglThemeExact = new ToggleSwitch("  -> exakt");
        tglThemeExact.setMaxWidth(Double.MAX_VALUE);
        tglThemeExact.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().themeExactProperty());
        v.getChildren().add(tglThemeExact);
        vbox.getChildren().add(v);

        ToggleSwitch tglThemeTitle = new ToggleSwitch("Thema oder Titel");
        tglThemeTitle.setMaxWidth(Double.MAX_VALUE);
        tglThemeTitle.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().themeTitleVisProperty());
        vbox.getChildren().add(tglThemeTitle);

        ToggleSwitch tglTitle = new ToggleSwitch("Titel");
        tglTitle.setMaxWidth(Double.MAX_VALUE);
        tglTitle.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().titleVisProperty());
        vbox.getChildren().add(tglTitle);

        ToggleSwitch tglSomewhere = new ToggleSwitch("Irgendwo");
        tglSomewhere.setMaxWidth(Double.MAX_VALUE);
        tglSomewhere.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().somewhereVisProperty());
        vbox.getChildren().add(tglSomewhere);

        ToggleSwitch tglUrl = new ToggleSwitch("Url");
        tglUrl.setMaxWidth(Double.MAX_VALUE);
        tglUrl.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().urlVisProperty());
        vbox.getChildren().add(tglUrl);

        ToggleSwitch tglDays = new ToggleSwitch("Zeitraum [Tage]");
        tglDays.setMaxWidth(Double.MAX_VALUE);
        tglDays.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().daysVisProperty());
        vbox.getChildren().add(tglDays);

        ToggleSwitch tglMinMax = new ToggleSwitch("Filmlänge Min/Max [Minuten]");
        tglMinMax.setMaxWidth(Double.MAX_VALUE);
        tglMinMax.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().minMaxDurVisProperty());
        vbox.getChildren().add(tglMinMax);

        ToggleSwitch tglMinMaxTime = new ToggleSwitch("Uhrzeit des Films");
        tglMinMaxTime.setMaxWidth(Double.MAX_VALUE);
        tglMinMaxTime.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().minMaxTimeVisProperty());
        vbox.getChildren().add(tglMinMaxTime);

        ToggleSwitch tglOnly = new ToggleSwitch("\"nur anzeigen\"");
        tglOnly.setMaxWidth(Double.MAX_VALUE);
        tglOnly.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().onlyVisProperty());
        vbox.getChildren().add(tglOnly);

        ToggleSwitch tglNot = new ToggleSwitch("\"nicht anzeigen\"");
        tglNot.setMaxWidth(Double.MAX_VALUE);
        tglNot.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().notVisProperty());
        vbox.getChildren().add(tglNot);
    }
}
