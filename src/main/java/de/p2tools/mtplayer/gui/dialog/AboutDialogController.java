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

package de.p2tools.mtplayer.gui.dialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.update.SearchProgramUpdate;
import de.p2tools.p2Lib.dialogs.AboutDialog;

public class AboutDialogController extends AboutDialog {
    private final ProgData progData;

    public AboutDialogController(ProgData progData) {
        super(progData.primaryStage, ProgConst.PROGRAM_NAME, ProgConst.URL_WEBSITE, ProgConst.URL_WEBSITE_HELP,
                ProgConst.FILE_PROG_ICON, ProgConfig.SYSTEM_PROG_OPEN_URL,
                ProgConfig.SYSTEM_DARK_THEME.getValue(),
                new String[]{"Filmliste:", "Einstellungen:"},
                new String[]{ProgInfos.getLocalFilmListFile(), ProgInfos.getSettingsFile().toAbsolutePath().toString()},
                true);

        this.progData = progData;
        setMaskerPane();
        progData.maskerPane.visibleProperty().addListener((u, o, n) -> {
            setMaskerPane();
        });
    }

    @Override
    public void make() {
        super.make();
    }

    @Override
    public void runCheckButton() {
        new SearchProgramUpdate(ProgData.getInstance(), this.getStage()).searchNewProgramVersion(true);
    }

    @Override
    public void close() {
        super.close();
    }

    private void setMaskerPane() {
        if (progData.maskerPane.isVisible()) {
            this.setMaskerVisible(true);
        } else {
            this.setMaskerVisible(false);
        }
    }
}
