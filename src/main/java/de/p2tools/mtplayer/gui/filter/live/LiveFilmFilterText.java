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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.filter.helper.PCboString;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.pcbo.P2CboCheckBoxListString;
import javafx.geometry.Insets;
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

        Button btnClear = P2ButtonClearFilterFactory.getPButtonClear();
        btnClear.setOnAction(a -> progData.liveFilmFilterWorker.clearFilter());
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(10, 0, 0, 0));
        hBox.getChildren().add(btnClear);
        this.getChildren().add(hBox);
    }

    private void addTxt(String txt, Node control, VBox vBoxComplete) {
        VBox vBox = new VBox(2);
        Label label = new Label(txt);
        vBox.getChildren().addAll(label, control);
        vBoxComplete.getChildren().add(vBox);
    }
}
