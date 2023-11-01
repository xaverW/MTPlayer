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


package de.p2tools.mtplayer.gui.dialog.abodialog;

import de.p2tools.mtplayer.controller.data.abo.AboData;

import java.util.List;

public class InitAddAboArray {

    private InitAddAboArray() {
    }

    public static AddAboData[] initDownloadInfoArrayFilm(AboData abo, AddAboDto addDto) {
        // AboArr anlegen
        AddAboData[] addAboData = new AddAboData[1];
        addAboData[0] = new AddAboData();
        addAboData[0].abo = abo.getCopy();
        addAboData[0].aboOrg = abo;
        addAboData[0].aboSubDir = abo.getAboSubDir();

        return addAboData;
    }

    public static AddAboData[] initDownloadInfoArrayFilm(List<AboData> abos, AddAboDto addDto) {
        // AboArr anlegen
        AddAboData[] addAboData = new AddAboData[abos.size()];
        for (int i = 0; i < abos.size(); ++i) {
            addAboData[i] = new AddAboData();
            addAboData[i].abo = abos.get(i).getCopy();
            addAboData[i].aboOrg = abos.get(i);
            addAboData[i].aboSubDir = abos.get(i).getAboSubDir();
        }
        return addAboData;
    }
}