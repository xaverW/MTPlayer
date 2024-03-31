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
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.filter.helper.PCboStringSearch;
import de.p2tools.p2lib.guitools.P2MenuButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LiveFilmFilterText extends VBox {

    private final P2MenuButton mbChannel;
    private final PCboStringSearch cboThema;
    private final PCboStringSearch cboTitle;

    private final ProgData progData;

    public LiveFilmFilterText() {
        super();
        progData = ProgData.getInstance();

        mbChannel = new P2MenuButton(progData.liveFilmFilterWorker.getActFilterSettings().channelProperty(), ThemeListFactory.allChannelList);
        cboThema = new PCboStringSearch(progData, progData.liveFilmFilterWorker.getActFilterSettings().themeProperty());
        cboTitle = new PCboStringSearch(progData, progData.liveFilmFilterWorker.getActFilterSettings().titleProperty());

        addFilter();
    }

    private void addFilter() {
        this.setSpacing(5);

        addTxt("Sender", mbChannel, this);
        addTxt("Thema", cboThema, this);
        addTxt("Titel", cboTitle, this);

        Button btnClear = new Button();
        btnClear.setTooltip(new Tooltip("Filter löschen"));
        btnClear.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
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
