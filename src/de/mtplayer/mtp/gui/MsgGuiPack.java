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

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.p2tools.p2Lib.tools.SysMsg;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;

public class MsgGuiPack {

    Daten daten;
    DoubleProperty doublePropertyMessage;
    DoubleProperty doublePropertyLogs;

    public MsgGuiPack() {
        daten = Daten.getInstance();
        this.doublePropertyLogs = Config.MSG_PANEL_LOGS_DIVIDER.getDoubleProperty();
        this.doublePropertyMessage = Config.MSG_PANEL_DIVIDER.getDoubleProperty();
    }

    public SplitPane pack() {

        final MsgLogController logControllerLogs = new MsgLogController(SysMsg.LOG_SYSTEM);
        final MsgLogController logControllerProgs = new MsgLogController(SysMsg.LOG_PLAYER);
        SplitPane splitPane = new SplitPane();
        SplitPane splitPaneLog = new SplitPane();

        splitPaneLog.setOrientation(Orientation.HORIZONTAL);
        splitPaneLog.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        splitPaneLog.getItems().addAll(logControllerLogs, logControllerProgs);
        splitPaneLog.getDividers().get(0).positionProperty().bindBidirectional(doublePropertyLogs);

        if (Daten.debug) {
            final MsgMemController memController = new MsgMemController();
            splitPane.setOrientation(Orientation.VERTICAL);
            splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            splitPane.getItems().addAll(splitPaneLog, memController);
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(doublePropertyMessage);
            return splitPane;
        } else {
            return splitPaneLog;
        }
    }

}
