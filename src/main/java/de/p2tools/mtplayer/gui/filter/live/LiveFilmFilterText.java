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

package de.p2tools.mtplayer.gui.filter.live;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.filter.FilterControllerSort;
import de.p2tools.mtplayer.gui.filter.helper.PCboString;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.pcbo.P2CboCheckBoxListString;
import de.p2tools.p2lib.ikonli.P2IconFactory;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.BooleanSupplier;

public class LiveFilmFilterText extends VBox {

    private final P2CboCheckBoxListString mbChannel;
    private final PCboString cboThema;
    private final PCboString cboTitle;

    private final ProgData progData;

    public LiveFilmFilterText() {
        super();
        progData = ProgData.getInstance();

        final BooleanSupplier booleanSupplier = () -> {
            progData.liveFilmFilterWorker.getActFilterSettings().reportFilterReturn();
            return true;
        };

        mbChannel = new P2CboCheckBoxListString(progData.liveFilmFilterWorker.getActFilterSettings().channelProperty(),
                ThemeListFactory.allChannelListFilm);
        cboThema = new PCboString(progData.stringFilterLists.getFilterListLiveThema(),
                progData.liveFilmFilterWorker.getActFilterSettings().themeProperty(), booleanSupplier);
        cboTitle = new PCboString(progData.stringFilterLists.getFilterListLiveTitel(),
                progData.liveFilmFilterWorker.getActFilterSettings().titleProperty(), booleanSupplier);

        addFilter();
    }

    private void addFilter() {
        this.setSpacing(5);

        addTxt("Sender", mbChannel, this);
        addTxt("Thema", cboThema, this);
        addTxt("Titel", cboTitle, this);

        // Sortierung
        FilterControllerSort filterControllerSort = new FilterControllerSort(FilterControllerSort.SORTER.LIVE);
        this.getChildren().addAll(filterControllerSort);
        filterControllerSort.visibleProperty().bind(ProgConfig.SYSTEM_SORT_LIVE_TABLE);
        filterControllerSort.managedProperty().bind(filterControllerSort.visibleProperty());

        final Button btnHelp = P2IconFactory.getHelpButton(progData.primaryStage, "Live-Suche",
                HelpText.LIVE_SEARCH);
        Button btnClear = P2ButtonClearFilterFactory.getPButtonClear();

        this.getChildren().add(P2GuiTools.getDistanceV(20));
        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(btnHelp, P2GuiTools.getHBoxGrower(), btnClear);

        this.getChildren().addAll(P2GuiTools.getVBoxGrower(), hBox, P2GuiTools.getDistanceV(10));
    }

    private void addTxt(String txt, Node control, VBox vBoxComplete) {
        VBox vBox = new VBox(2);
        Label label = new Label(txt);
        vBox.getChildren().addAll(label, control);
        vBoxComplete.getChildren().add(vBox);
    }
}
