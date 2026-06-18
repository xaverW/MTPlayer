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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboConstants;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.filter.helper.PCboString;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.pcbo.P2CboCheckBoxListString;
import de.p2tools.p2lib.mediathek.filter.FilterCheckRegEx;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AboFilterController extends FilterController {

    private P2CboCheckBoxListString mbChannel;
    private final ComboBox<String> cboState = new ComboBox<>(); // Abo ein-/ausgeschaltet
    private PCboString cboName;
    private PCboString cboSearchText;
    private PCboString cboDescription;
    private final RadioButton rbAll = new RadioButton("Alle");
    private final RadioButton rbFilm = new RadioButton("Filme");
    private final RadioButton rbAudio = new RadioButton("Audios");
    private final ProgData progData;

    public AboFilterController() {
        VBox vBoxFilter = getVBoxFilter();
        progData = ProgData.getInstance();
        progData.aboFilterController = this;

        ToggleGroup tg = new ToggleGroup();
        rbAll.setToggleGroup(tg);
        rbFilm.setToggleGroup(tg);
        rbAudio.setToggleGroup(tg);

        initFilter();

        addCont("Abos für Sender", mbChannel, vBoxFilter);
        addCont("Status", cboState, vBoxFilter);
        addCont("Name", cboName, vBoxFilter);
        addCont("Filtertext", cboSearchText, vBoxFilter);
        addCont("Beschreibung", cboDescription, vBoxFilter);

        VBox vBoxRadio = new VBox(2);
        Label lblRadio = new Label("Abos für Liste");
        HBox hBoxRadio = new HBox(P2LibConst.SPACING_HBOX);
        hBoxRadio.getChildren().addAll(rbAll, rbFilm, rbAudio);
        vBoxRadio.getChildren().addAll(lblRadio, hBoxRadio);
        vBoxFilter.getChildren().addAll(vBoxRadio);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(10, 0, 0, 0));
        Button btnClear = P2ButtonClearFilterFactory.getPButtonClearFilter();
        hBox.getChildren().add(btnClear);

        final Button btnHelpFilter = PIconFactory.getHelpButton("Filter", HelpText.GUI_ABO_FILTER);
        HBox hBoxHelp = new HBox();
        hBoxHelp.setAlignment(Pos.CENTER_RIGHT);
        hBoxHelp.getChildren().add(btnHelpFilter);

        vBoxFilter.getChildren().addAll(hBox, P2GuiTools.getVBoxGrower(), hBoxHelp);
        btnClear.setOnAction(a -> clearFilter());
    }

    private void initFilter() {
        rbAll.selectedProperty().addListener((u, o, n) -> {
            if (rbAll.isSelected())
                ProgConfig.FILTER_ABO_LIST.set(ProgConst.LIST_FILM_AUDIO);
        });
        rbFilm.selectedProperty().addListener((u, o, n) -> {
            if (rbFilm.isSelected())
                ProgConfig.FILTER_ABO_LIST.set(ProgConst.LIST_FILM);
        });
        rbAudio.selectedProperty().addListener((u, o, n) -> {
            if (rbAudio.isSelected())
                ProgConfig.FILTER_ABO_LIST.set(ProgConst.LIST_AUDIO);
        });
        ProgConfig.FILTER_ABO_LIST.addListener((u, o, n) -> {
            rbAll.setSelected(ProgConfig.FILTER_ABO_LIST.get() == ProgConst.LIST_FILM_AUDIO);
            rbFilm.setSelected(ProgConfig.FILTER_ABO_LIST.get() == ProgConst.LIST_FILM);
            rbAudio.setSelected(ProgConfig.FILTER_ABO_LIST.get() == ProgConst.LIST_AUDIO);
        });

        rbAll.setSelected(ProgConfig.FILTER_ABO_LIST.get() == ProgConst.LIST_FILM_AUDIO);
        rbFilm.setSelected(ProgConfig.FILTER_ABO_LIST.get() == ProgConst.LIST_FILM);
        rbAudio.setSelected(ProgConfig.FILTER_ABO_LIST.get() == ProgConst.LIST_AUDIO);

        mbChannel = new P2CboCheckBoxListString(ProgConfig.FILTER_ABO_CHANNEL,
                ThemeListFactory.channelsForAbosList);

        cboState.getItems().addAll(AboConstants.ALL, AboConstants.ABO_ON, AboConstants.ABO_OFF);
        cboState.valueProperty().bindBidirectional(ProgConfig.FILTER_ABO_TYPE);

        cboName = new PCboString(progData.stringFilterLists.getFilterListAboName(),
                ProgConfig.FILTER_ABO_NAME);
        new FilterCheckRegEx(cboName.getEditor());

        cboSearchText = new PCboString(progData.stringFilterLists.getFilterListAboSearchText(),
                ProgConfig.FILTER_ABO_SEARCH_TEXT);
        new FilterCheckRegEx(cboSearchText.getEditor());

        cboDescription = new PCboString(progData.stringFilterLists.getFilterListAboDescription(),
                ProgConfig.FILTER_ABO_DESCRIPTION);
        new FilterCheckRegEx(cboDescription.getEditor());
    }

    private void clearFilter() {
        ProgConfig.FILTER_ABO_NAME.set("");
        ProgConfig.FILTER_ABO_SEARCH_TEXT.set("");
        ProgConfig.FILTER_ABO_DESCRIPTION.set("");
        ProgConfig.FILTER_ABO_CHANNEL.setValue("");
        ProgConfig.FILTER_ABO_TYPE.set("");
        ProgConfig.FILTER_ABO_LIST.set(ProgConst.LIST_FILM_AUDIO);
    }
}
