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

package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.filmfilter.BlacklistFilterFactory;
import de.p2tools.mtplayer.gui.configdialog.ConfigDialogController;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class FilmFilterControllerBlacklist extends HBox {

    private final ProgData progData;

    private final PToggleSwitch tglBlacklist = new PToggleSwitch("Blacklist:");

    public FilmFilterControllerBlacklist() {
        progData = ProgData.getInstance();

        Button btnBlack = new Button("");
        btnBlack.getStyleClass().add("buttonSmall");
        btnBlack.setGraphic(ProgIcons.Icons.ICON_BUTTON_EDIT.getImageView());
        btnBlack.setOnAction(a -> new ConfigDialogController(ProgData.getInstance(), true).showDialog());

        Label lblRight = new Label();
        tglBlacklist.setAllowIndeterminate(true);
        tglBlacklist.setLabelLeft("Blacklist [ein]:", "Blacklist [aus]:", "Blacklist [invers]:");
        tglBlacklist.setTooltip(new Tooltip("Blacklist aus: Alle Filme werden angezeigt.\n" +
                "Blacklist ein: Von der Blacklist erfasste Filme werden nicht angezeigt.\n" +
                "Blacklist invers: Nur von der Blacklist erfasste Filme werden angezeigt."));

        progData.actFilmFilterWorker.getActFilterSettings().blacklistOnOffProperty().addListener((u, o, n) -> {
            switch (progData.actFilmFilterWorker.getActFilterSettings().blacklistOnOffProperty().getValue()) {
                case BlacklistFilterFactory.BLACKLILST_FILTER_OFF:
                    tglBlacklist.setIndeterminate(false);
                    tglBlacklist.setSelected(false);
                    break;
                case BlacklistFilterFactory.BLACKLILST_FILTER_ON:
                    tglBlacklist.setIndeterminate(false);
                    tglBlacklist.setSelected(true);
                    break;
                case BlacklistFilterFactory.BLACKLILST_FILTER_INVERS:
                    tglBlacklist.setIndeterminate(true);
                    tglBlacklist.setSelected(false);
                    break;
            }
        });

        tglBlacklist.getCheckBox().setOnAction((mouseEvent) -> {
            if (tglBlacklist.isIndeterminate()) {
                progData.actFilmFilterWorker.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_INVERS);
            } else if (tglBlacklist.isSelected()) {
                progData.actFilmFilterWorker.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_ON);
            } else {
                progData.actFilmFilterWorker.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_OFF);
            }
        });

        setSpacing(5);
        setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(tglBlacklist, Priority.ALWAYS);
        getChildren().addAll(tglBlacklist, lblRight, btnBlack);
    }
}
