/*
 * P2tools Copyright (C) 2021 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;


public class TableRowDownload<T> extends TableRow<T> {

    private final BooleanProperty geoMelden;

    public TableRowDownload() {
        geoMelden = ProgConfig.SYSTEM_MARK_GEO;
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setStyle("");
            setTooltip(null);

        } else {
            DownloadData download = (DownloadData) item;
            if (ProgConfig.DOWNLOAD_GUI_SHOW_TABLE_TOOL_TIP.getValue()) {
                setTooltip(new Tooltip(download.getTheme() + "\n" + download.getTitle()));
            }

            if (geoMelden.get() && download.getGeoBlocked()) {
                // geoGeblockt
                for (int i = 0; i < getChildren().size(); i++) {
                    getChildren().get(i).setStyle(ProgColorList.FILM_GEOBLOCK.getCssFontBold());
                }

            } else if (download.isStateError()) {
                Tooltip tooltip = new Tooltip();
                tooltip.setText(download.getErrorMessage());
                setTooltip(tooltip);

            } else {
                for (int i = 0; i < getChildren().size(); i++) {
                    getChildren().get(i).setStyle("");
                }
            }

            switch (download.getState()) {
                case DownloadConstants.STATE_INIT:
                case DownloadConstants.STATE_STOPPED:
                    setStyle("");
                    break;
                case DownloadConstants.STATE_STARTED_WAITING:
                    setStyle(ProgColorList.DOWNLOAD_WAIT.getCssBackground());
                    break;
                case DownloadConstants.STATE_STARTED_RUN:
                    setStyle(ProgColorList.DOWNLOAD_RUN.getCssBackground());
                    break;
                case DownloadConstants.STATE_FINISHED:
                    setStyle(ProgColorList.DOWNLOAD_FINISHED.getCssBackground());
                    break;
                case DownloadConstants.STATE_ERROR:
                    setStyle(ProgColorList.DOWNLOAD_ERROR.getCssBackground());
                    break;
            }
        }
    }
}
