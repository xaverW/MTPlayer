/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.gui.dialog.downloadadd;

import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.ProgramData;

import java.util.Arrays;

public class InitProgramCall {

    private final AddDownloadDto addDownloadDto;

    public InitProgramCall(AddDownloadDto addDownloadDto) {
        this.addDownloadDto = addDownloadDto;
        init();
    }

    private void init() {
        Arrays.stream(addDownloadDto.addDownloadData).toList().forEach(downloadAddData -> {
            downloadAddData.orgProgArray = downloadAddData.download.getProgramCallArray();
        });

        makeAct();
        addDownloadDto.btnProgramCallReset.setOnAction(e -> addDownloadDto.textAreaCallArray.setText(addDownloadDto.getAct().orgProgArray));
        addDownloadDto.textAreaCallArray.textProperty().addListener((u, o, n) -> {
            addDownloadDto.getAct().download.setProgramCallArray(n.trim());
            addDownloadDto.getAct().download.setProgramCall(ProgramData.makeProgAufrufArray(addDownloadDto.getAct().download.getProgramCallArray()));
            addDownloadDto.textAreaProg.setText(addDownloadDto.getAct().download.getProgramCall());
        });

        addDownloadDto.textAreaProg.textProperty().addListener((observable, oldValue, newValue) -> {
            addDownloadDto.getAct().download.setProgramCall(newValue.trim());
        });
    }

    public void makeAct() {
        addDownloadDto.textAreaCallArray.setDisable(addDownloadDto.getAct().downloadIsRunning());

        addDownloadDto.lblProgramIsDownload.setVisible(!addDownloadDto.getAct().download.getType().equals(DownloadConstants.TYPE_PROGRAM));
        addDownloadDto.lblProgramIsDownload.setManaged(!addDownloadDto.getAct().download.getType().equals(DownloadConstants.TYPE_PROGRAM));

        addDownloadDto.vBoxProgramCall.setVisible(addDownloadDto.getAct().download.getType().equals(DownloadConstants.TYPE_PROGRAM));
        addDownloadDto.vBoxProgramCall.setManaged(addDownloadDto.getAct().download.getType().equals(DownloadConstants.TYPE_PROGRAM));

        addDownloadDto.textAreaProg.setText(addDownloadDto.getAct().download.getProgramCall());
        addDownloadDto.textAreaCallArray.setText(addDownloadDto.getAct().download.getProgramCallArray());
    }

    public static void setProgrammCall(AddDownloadDto addDownloadDto, AddDownloadData addDownloadData) {
        DownloadData download = addDownloadData.download;
        // muss noch der Programmaufruf neu gebaut werden
        download.setPathName(addDownloadData.path, addDownloadData.name);
        download.makeProgParameter();

        addDownloadDto.textAreaProg.setText(addDownloadDto.getAct().download.getProgramCall());
        addDownloadDto.textAreaCallArray.setText(addDownloadDto.getAct().download.getProgramCallArray());
    }
}
