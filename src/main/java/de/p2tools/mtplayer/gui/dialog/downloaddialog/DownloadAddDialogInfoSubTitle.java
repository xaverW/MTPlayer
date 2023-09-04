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

import javafx.scene.control.CheckBox;

public class DownloadAddDialogInfoSubTitle {
    private final CheckBox chkInfo;
    private final CheckBox chkSubtitle;
    private final CheckBox chkSubTitleAll;
    private final CheckBox chkInfoAll;
    private final DownloadAddData[] downloadAddInfosArr;

    public DownloadAddDialogInfoSubTitle(CheckBox chkInfo, CheckBox chkSubtitle,
                                         CheckBox chkSubTitleAll, CheckBox chkInfoAll,
                                         DownloadAddData[] downloadAddInfosArr) {

        this.chkInfo = chkInfo;
        this.chkSubtitle = chkSubtitle;
        this.chkInfoAll = chkInfoAll;
        this.chkSubTitleAll = chkSubTitleAll;
        this.downloadAddInfosArr = downloadAddInfosArr;
    }

    public void initInfoSubTitle(int actFilmIsShown) {
        makeInfoSubTitle(actFilmIsShown);
    }

    public void makeInfoSubTitle(int actFilmIsShown) {
        chkInfo.setSelected(downloadAddInfosArr[actFilmIsShown].makeInfo);
        chkSubtitle.setDisable(downloadAddInfosArr[actFilmIsShown].subIsDisabled);
        chkSubtitle.setSelected(downloadAddInfosArr[actFilmIsShown].makeSubTitle);
    }

    public void setInfoSubTitle(int actFilmIsShown) {
        downloadAddInfosArr[actFilmIsShown].setInfo(chkInfo.isSelected(), chkInfoAll.isSelected());
        downloadAddInfosArr[actFilmIsShown].setSubtitle(chkSubtitle.isSelected(), chkSubTitleAll.isSelected());
    }
}
