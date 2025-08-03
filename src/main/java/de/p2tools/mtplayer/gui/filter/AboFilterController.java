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

package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboConstants;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.filter.helper.PCboString;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2MenuButton;
import de.p2tools.p2lib.mediathek.filter.FilterCheckRegEx;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AboFilterController extends FilterController {

    private P2MenuButton mbChannel;
    private final ComboBox<String> cboState = new ComboBox<>(); // Abo ein-/ausgeschaltet
    private PCboString cboName;
    private PCboString cboSearchText;
    private PCboString cboDescription;
    private final ProgData progData;

    public AboFilterController() {
        VBox vBoxFilter = getVBoxFilter();
        progData = ProgData.getInstance();
        progData.aboFilterController = this;

        initFilter();
        addCont("Abos für Sender", mbChannel, vBoxFilter);
        addCont("Status", cboState, vBoxFilter);
        addCont("Name", cboName, vBoxFilter);
        addCont("Filtertext", cboSearchText, vBoxFilter);
        addCont("Beschreibung", cboDescription, vBoxFilter);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(10, 0, 0, 0));
        Button btnClear = P2ButtonClearFilterFactory.getPButtonClearFilter();
        hBox.getChildren().add(btnClear);

        final Button btnHelpFilter = P2Button.helpButton("Filter", HelpText.GUI_ABO_FILTER);
        HBox hBoxHelp = new HBox();
        hBoxHelp.setAlignment(Pos.CENTER_RIGHT);
        hBoxHelp.getChildren().add(btnHelpFilter);

        vBoxFilter.getChildren().addAll(hBox, P2GuiTools.getVBoxGrower(), hBoxHelp);
        btnClear.setOnAction(a -> clearFilter());
    }

    private void initFilter() {
        mbChannel = new P2MenuButton(ProgConfig.FILTER_ABO_CHANNEL,
                ThemeListFactory.channelsForAbosList);

        cboState.getItems().addAll(AboConstants.ALL, AboConstants.ABO_ON, AboConstants.ABO_OFF);
        cboState.valueProperty().bindBidirectional(ProgConfig.FILTER_ABO_TYPE);

        cboName = new PCboString(progData.filmFilterStringLists.getFilterListAboName(),
                ProgConfig.FILTER_ABO_NAME);
        FilterCheckRegEx fN = new FilterCheckRegEx(cboName.getEditor());
        cboName.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fN.checkPattern());

        cboSearchText = new PCboString(progData.filmFilterStringLists.getFilterListAboSearchText(),
                ProgConfig.FILTER_ABO_SEARCH_TEXT);
        FilterCheckRegEx fS = new FilterCheckRegEx(cboSearchText.getEditor());
        cboSearchText.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fS.checkPattern());

        cboDescription = new PCboString(progData.filmFilterStringLists.getFilterListAboDescription(),
                ProgConfig.FILTER_ABO_DESCRIPTION);
        FilterCheckRegEx fD = new FilterCheckRegEx(cboDescription.getEditor());
        cboDescription.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fD.checkPattern());
    }

    private void clearFilter() {
        ProgConfig.FILTER_ABO_NAME.set("");
        ProgConfig.FILTER_ABO_SEARCH_TEXT.set("");
        ProgConfig.FILTER_ABO_DESCRIPTION.set("");
        ProgConfig.FILTER_ABO_CHANNEL.setValue("");
        ProgConfig.FILTER_ABO_TYPE.set("");
    }
}
