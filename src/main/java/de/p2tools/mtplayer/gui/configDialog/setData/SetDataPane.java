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

package de.p2tools.mtplayer.gui.configDialog.setData;

import de.p2tools.mtplayer.controller.data.SetData;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.util.Collection;

public class SetDataPane {

    private final SetPane setPane;
    private final DestinationPane destinationPane;
    private final DownloadPane downloadPane;

    private SetData setData = null;
    private Collection<TitledPane> result;
    private ProgramPane programPane;
    private final Stage stage;

    public SetDataPane(Stage stage) {
        this.stage = stage;
        setPane = new SetPane(stage);
        destinationPane = new DestinationPane(stage);
        downloadPane = new DownloadPane(stage);
        programPane = new ProgramPane(stage);
    }

    public void close() {
        setPane.close();
        destinationPane.close();
        downloadPane.close();
        programPane.close();
    }

    public void makeSetPane(Collection<TitledPane> result) {
        this.result = result;

        setPane.makePane(result);
        destinationPane.makePane(result);
        downloadPane.makePane(result);
        programPane.makeProgs(result);

        setDisable();
    }

    public void bindProgData(SetData setData) {
        setPane.unBindProgData();
        destinationPane.unBindProgData();
        downloadPane.unBindProgData();

        this.setData = setData;
        if (setData != null) {
            setPane.bindProgData(setData);
            destinationPane.bindProgData(setData);
            downloadPane.bindProgData(setData);
            programPane.setSetDate(setData);
        }
        setDisable();
    }

    public void setDisable() {
        for (TitledPane tp : result) {
            tp.setDisable(setData == null);
        }
    }
}
