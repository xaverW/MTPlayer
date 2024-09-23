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

package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.filter.DownloadFilterController;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneFactory;
import de.p2tools.p2lib.guitools.pclosepane.P2InfoController;
import de.p2tools.p2lib.guitools.pclosepane.P2InfoDto;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.ArrayList;

public class DownloadGui {

    ProgData progData;
    private final SplitPane splitPane = new SplitPane();
    private final HBox hBox = new HBox();
    private final DownloadFilterController downloadFilterController;
    private final DownloadGuiController downloadGuiController;
    private final BooleanProperty boundFilter = new SimpleBooleanProperty(false);
    private final P2InfoController infoControllerFilter;

    public DownloadGui() {
        progData = ProgData.getInstance();
        downloadFilterController = new DownloadFilterController();
        downloadGuiController = new DownloadGuiController();
        ArrayList<P2InfoDto> list = new ArrayList<>();
        P2InfoDto infoDto = new P2InfoDto(downloadFilterController,
                ProgConfig.DOWNLOAD__FILTER_IS_RIP,
                ProgConfig.DOWNLOAD__FILTER_DIALOG_SIZE, ProgData.DOWNLOAD_TAB_ON,
                "Filter", "Filter", true);
        list.add(infoDto);
        infoControllerFilter = new P2InfoController(list, ProgConfig.DOWNLOAD__FILTER_IS_SHOWING);
    }

    public SplitPane pack() {
        // Menü
        final MenuController menuController = new MenuController(MenuController.StartupMode.DOWNLOAD);

        // Gui
        progData.downloadGuiController = downloadGuiController;

        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        SplitPane.setResizableWithParent(downloadFilterController, Boolean.FALSE);

        hBox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        hBox.getChildren().addAll(splitPane, menuController);

        ProgConfig.DOWNLOAD__FILTER_IS_SHOWING.addListener((observable, oldValue, newValue) -> setSplit());
        ProgConfig.DOWNLOAD__FILTER_IS_RIP.addListener((observable, oldValue, newValue) -> setSplit());
        setSplit();
        return new SplitPane(hBox);
    }

    private void setSplit() {
        P2ClosePaneFactory.setSplit(boundFilter, splitPane,
                infoControllerFilter, true, downloadGuiController,
                ProgConfig.DOWNLOAD__FILTER_DIVIDER, ProgConfig.DOWNLOAD__FILTER_IS_SHOWING);
    }
}
