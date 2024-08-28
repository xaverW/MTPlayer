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
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2MenuButton;
import de.p2tools.p2lib.mtfilter.FilterCheckRegEx;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AboFilterController extends FilterController {

    private P2MenuButton mbChannel;
    private ComboBox<String> cboArt = new ComboBox<>(); // Abo ein-/ausgeschaltet
    private PCboString cboName;
    private PCboString cboDescription;
    private Button btnClear = P2ButtonClearFilterFactory.getPButtonClearFilter();

    private final VBox vBoxFilter;
    private final ProgData progData;

    public AboFilterController() {
        super(ProgConfig.ABO_GUI_FILTER_DIVIDER_ON);
        vBoxFilter = getVBoxFilter(true);
        progData = ProgData.getInstance();
        progData.aboFilterController = this;

        initFilter();
        addCont("Abos für Sender", mbChannel, vBoxFilter);
        addCont("Status", cboArt, vBoxFilter);
        addCont("Name", cboName, vBoxFilter);
        addCont("Beschreibung", cboDescription, vBoxFilter);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(10, 0, 0, 0));
        hBox.getChildren().add(btnClear);
        vBoxFilter.getChildren().add(hBox);
        btnClear.setOnAction(a -> clearFilter());
    }

    private void initFilter() {
        mbChannel = new P2MenuButton(ProgConfig.FILTER_ABO_CHANNEL,
                ThemeListFactory.channelsForAbosList);

        cboArt.getItems().addAll(AboConstants.ALL, AboConstants.ABO_ON, AboConstants.ABO_OFF);
        cboArt.valueProperty().bindBidirectional(ProgConfig.FILTER_ABO_TYPE);

        cboName = new PCboString(progData.stringFilterLists.getFilterListAboName(),
                ProgConfig.FILTER_ABO_NAME);
        FilterCheckRegEx fN = new FilterCheckRegEx(cboName.getEditor());
        cboName.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fN.checkPattern());

        cboDescription = new PCboString(progData.stringFilterLists.getFilterListAboDescription(),
                ProgConfig.FILTER_ABO_DESCRIPTION);
        FilterCheckRegEx fD = new FilterCheckRegEx(cboDescription.getEditor());
        cboDescription.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fD.checkPattern());
    }

    private void clearFilter() {
        ProgConfig.FILTER_ABO_NAME.set("");
        ProgConfig.FILTER_ABO_DESCRIPTION.set("");
        ProgConfig.FILTER_ABO_CHANNEL.setValue("");
        ProgConfig.FILTER_ABO_TYPE.set("");
    }
}
