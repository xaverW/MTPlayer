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
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.dialog.PDialog;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class FilmFilterEditDialog extends PDialog {

    final ProgData progData;
    final VBox vbox;

    public FilmFilterEditDialog(ProgData progData) {
        super(null, "Filter ein- und ausschalten", true);

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

        final Button btnHelpAbo = PButton.helpButton(getStage(), "Filter ein- und ausschalten",
                HelpText.GUI_FILMS_EDIT_FILTER);

        HBox hBox = new HBox();
        hBox.setSpacing(10);

        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        Button btnOk = new Button("Ok");
        btnOk.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
        btnOk.setOnAction(event -> close());
        hBox.getChildren().addAll(btnHelpAbo, btnOk);

        vbox.getChildren().addAll(vBoxCont, hBox);


    }

    public void init(VBox vbox) {
        PToggleSwitch tglChannel = new PToggleSwitch("Sender");
        tglChannel.setMaxWidth(Double.MAX_VALUE);
        tglChannel.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().channelVisProperty());
        vbox.getChildren().add(tglChannel);

//        PToggleSwitch tglChannelExact = new PToggleSwitch("  -> exakt");
//        tglChannelExact.setMaxWidth(Double.MAX_VALUE);
//        tglChannelExact.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().channelExactProperty());
//        v.getChildren().add(tglChannelExact);
//        vbox.getChildren().add(v);

        VBox v = new VBox();
        PToggleSwitch tglTheme = new PToggleSwitch("Thema");
        tglTheme.setMaxWidth(Double.MAX_VALUE);
        tglTheme.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().themeVisProperty());
        v.getChildren().add(tglTheme);

        PToggleSwitch tglThemeExact = new PToggleSwitch("  -> exakt");
        tglThemeExact.setMaxWidth(Double.MAX_VALUE);
        tglThemeExact.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().themeExactProperty());
        v.getChildren().add(tglThemeExact);
        vbox.getChildren().add(v);

        PToggleSwitch tglThemeTitle = new PToggleSwitch("Thema oder Titel");
        tglThemeTitle.setMaxWidth(Double.MAX_VALUE);
        tglThemeTitle.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().themeTitleVisProperty());
        vbox.getChildren().add(tglThemeTitle);

        PToggleSwitch tglTitle = new PToggleSwitch("Titel");
        tglTitle.setMaxWidth(Double.MAX_VALUE);
        tglTitle.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().titleVisProperty());
        vbox.getChildren().add(tglTitle);

        PToggleSwitch tglSomewhere = new PToggleSwitch("Irgendwo");
        tglSomewhere.setMaxWidth(Double.MAX_VALUE);
        tglSomewhere.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().somewhereVisProperty());
        vbox.getChildren().add(tglSomewhere);

        PToggleSwitch tglUrl = new PToggleSwitch("Url");
        tglUrl.setMaxWidth(Double.MAX_VALUE);
        tglUrl.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().urlVisProperty());
        vbox.getChildren().add(tglUrl);

        PToggleSwitch tglTimeRange = new PToggleSwitch("Zeitraum [Tage]");
        tglTimeRange.setMaxWidth(Double.MAX_VALUE);
        tglTimeRange.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().timeRangeVisProperty());
        vbox.getChildren().add(tglTimeRange);

        PToggleSwitch tglMinMax = new PToggleSwitch("Filmlänge Min/Max [Minuten]");
        tglMinMax.setMaxWidth(Double.MAX_VALUE);
        tglMinMax.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minMaxDurVisProperty());
        vbox.getChildren().add(tglMinMax);

        PToggleSwitch tglMinMaxTime = new PToggleSwitch("Uhrzeit des Films");
        tglMinMaxTime.setMaxWidth(Double.MAX_VALUE);
        tglMinMaxTime.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minMaxTimeVisProperty());
        vbox.getChildren().add(tglMinMaxTime);

        PToggleSwitch tglOnly = new PToggleSwitch("\"nur anzeigen\"");
        tglOnly.setMaxWidth(Double.MAX_VALUE);
        tglOnly.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().onlyVisProperty());
        vbox.getChildren().add(tglOnly);

        PToggleSwitch tglNot = new PToggleSwitch("\"nicht anzeigen\"");
        tglNot.setMaxWidth(Double.MAX_VALUE);
        tglNot.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().notVisProperty());
        vbox.getChildren().add(tglNot);
    }
}
