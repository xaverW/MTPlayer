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


package de.p2tools.mtplayer.gui.dialog.downloaddialog;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import javafx.scene.control.ComboBox;

import java.util.Arrays;

public class DownloadAddDialogSetData {
    private final ProgData progData;
    private final ComboBox<SetData> cboSetData;
    private final DownloadAddData[] downloadAddInfosArr;

    public DownloadAddDialogSetData(ProgData progData, ComboBox<SetData> cboSetData,
                                    DownloadAddData[] downloadAddInfosArr) {

        this.progData = progData;
        this.cboSetData = cboSetData;
        this.downloadAddInfosArr = downloadAddInfosArr;
    }

    public void initCboSetData(SetData setData) {
        if (progData.setDataList.getSetDataListSave().size() > 1) {
            // nur dann machts Sinn
            cboSetData.getItems().addAll(progData.setDataList.getSetDataListSave());
            cboSetData.getSelectionModel().select(setData);
        }
    }

    public void makeActSetData(int actFilmIsShown) {
        cboSetData.getSelectionModel().select(downloadAddInfosArr[actFilmIsShown].setData);
    }

    public void makeSetDataChange(boolean all, int actFilmIsShown) {
        if (all) {
            Arrays.stream(downloadAddInfosArr).forEach(this::makeSetDataChange);
        } else {
            makeSetDataChange(downloadAddInfosArr[actFilmIsShown]);
        }
    }

    public void makeSetDataChange(DownloadAddData downloadAddData) {
        SetData psetData = cboSetData.getSelectionModel().getSelectedItem();

        downloadAddData.setData = psetData;
        downloadAddData.download = new DownloadData(DownloadConstants.SRC_DOWNLOAD, psetData,
                downloadAddData.film, null, downloadAddData.name, downloadAddData.path,
                FilmDataMTP.RESOLUTION_NORMAL);

        downloadAddData.path = downloadAddData.download.getDestPath();
        downloadAddData.name = downloadAddData.download.getDestFileName();
        downloadAddData.makeInfo = downloadAddData.setData.isInfoFile();

        if (downloadAddData.film.getUrlSubtitle().isEmpty()) {
            // dann gibts keinen Subtitle
            downloadAddData.subIsDisabled = true;
            downloadAddData.makeSubTitle = false;
        } else {
            downloadAddData.subIsDisabled = false;
            downloadAddData.makeSubTitle = downloadAddData.setData.isSubtitle();
        }

        // die Werte passend zum Film setzen
        if (downloadAddData.setData.getResolution().equals(FilmDataMTP.RESOLUTION_HD)
                && downloadAddData.film.isHd()) {
            downloadAddData.resolution = FilmDataMTP.RESOLUTION_HD;

        } else if (downloadAddData.setData.getResolution().equals(FilmDataMTP.RESOLUTION_SMALL)
                && downloadAddData.film.isSmall()) {
            downloadAddData.resolution = FilmDataMTP.RESOLUTION_SMALL;

        } else {
            downloadAddData.resolution = FilmDataMTP.RESOLUTION_NORMAL;
        }
    }
}
