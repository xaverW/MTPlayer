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

    private final AddDto addDto;

    public InitProgramCall(AddDto addDto) {
        this.addDto = addDto;
        init();
    }

    private void init() {
        Arrays.stream(addDto.downloadAddData).toList().forEach(downloadAddData -> {
            downloadAddData.orgProgArray = downloadAddData.download.getProgramCallArray();
        });

        makeAct();
        addDto.btnProgramCallReset.setOnAction(e -> addDto.textAreaCallArray.setText(addDto.getAct().orgProgArray));
        addDto.textAreaCallArray.textProperty().addListener((u, o, n) -> {
            addDto.getAct().download.setProgramCallArray(n.trim());
            addDto.getAct().download.setProgramCall(ProgramData.makeProgAufrufArray(addDto.getAct().download.getProgramCallArray()));
            addDto.textAreaProg.setText(addDto.getAct().download.getProgramCall());
        });

        addDto.textAreaProg.textProperty().addListener((observable, oldValue, newValue) -> {
            addDto.getAct().download.setProgramCall(newValue.trim());
        });
    }

    public void makeAct() {
        System.out.println("PROG_CALL_ACT");
        addDto.textAreaCallArray.setDisable(addDto.getAct().downloadIsRunning());

        addDto.lblProgramIsDownload.setVisible(!addDto.getAct().download.getType().equals(DownloadConstants.TYPE_PROGRAM));
        addDto.lblProgramIsDownload.setManaged(!addDto.getAct().download.getType().equals(DownloadConstants.TYPE_PROGRAM));

        addDto.vBoxProgramCall.setVisible(addDto.getAct().download.getType().equals(DownloadConstants.TYPE_PROGRAM));
        addDto.vBoxProgramCall.setManaged(addDto.getAct().download.getType().equals(DownloadConstants.TYPE_PROGRAM));

        addDto.textAreaProg.setText(addDto.getAct().download.getProgramCall());
        addDto.textAreaCallArray.setText(addDto.getAct().download.getProgramCallArray());
    }

    public static void setProgrammCall(AddDto addDto, DownloadAddData downloadAddData) {
        DownloadData download = downloadAddData.download;
        if (download.getType().equals(DownloadConstants.TYPE_PROGRAM) && download.getSetData() != null) {
            // muss noch der Programmaufruf neu gebaut werden
            download.setPathName(downloadAddData.path, downloadAddData.name);
            download.makeProgParameter();
        }

        addDto.textAreaProg.setText(addDto.getAct().download.getProgramCall());
        addDto.textAreaCallArray.setText(addDto.getAct().download.getProgramCallArray());
    }
}
