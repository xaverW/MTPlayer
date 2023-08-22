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


package de.p2tools.mtplayer.gui.filter.helper;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ComboBox;

public class P2CboStringSearchExact extends ComboBox<String> {
    private final StringProperty strSearchProperty;
    private final ProgData progData;

    public P2CboStringSearchExact(ProgData progData, StringProperty strSearchProperty) {
        this.progData = progData;
        this.strSearchProperty = strSearchProperty;
        setEditable(false);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setVisibleRowCount(P2CboStringSearch.MAX_FILTER_HISTORY);
        init();
    }

    private void init() {
        progData.filmFilterWorker.getActFilterSettings().themeExactProperty()
                .addListener((observable, oldValue, newValue) -> addCboThemeItem());
        addCboThemeItem();
        ThemeListFactory.themeForChannelList.addListener((ListChangeListener<String>) c -> {
            if (!progData.filmFilterWorker.getActFilterSettings().isThemeExact()) {
                // dann betrifft es das nicht
                return;
            }

            progData.filmFilterWorker.getActFilterSettings().switchFilterOff(true);
            String oldValue = getSelectionModel().getSelectedItem(); // kann NULL sein, wenn nicht ausgewählt
            getItems().setAll(ThemeListFactory.themeForChannelList);
            progData.filmFilterWorker.getActFilterSettings().switchFilterOff(false);

            // und jetzt sel, wenn nicht mehr in der Liste ändert sich der sel!!!
            if (oldValue == null || oldValue.isEmpty()) {
                // dann war nichts ausgewählt, hat sich nichts geändert
                return;
            }
            if (ThemeListFactory.themeForChannelList.contains(oldValue)) {
                getSelectionModel().select(oldValue);
            } else {
                getSelectionModel().select("");
                progData.filmFilterWorker.postFilterChange();
            }
        });

        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (!progData.filmFilterWorker.getActFilterSettings().isThemeExact()) {
                        // dann betrifft es das nicht
                        return;
                    }
                    if (this.isShowing() ||
                            newValue == null && !strSearchProperty.getValueSafe().isEmpty() ||
                            newValue != null && !strSearchProperty.getValueSafe().equals(newValue)) {
                        strSearchProperty.setValue(newValue);
                    }
                }
        );
        strSearchProperty.addListener((u, o, n) -> {
            getSelectionModel().select(strSearchProperty.getValue());
        });
        getSelectionModel().select(strSearchProperty.getValue());
    }

    private void addCboThemeItem() {
        if (progData.filmFilterWorker.getActFilterSettings().isThemeExact()) {
            // dann die channels eintragen
            getItems().addAll(ThemeListFactory.themeForChannelList);
//        } else {
            // dann freie Suche
//            getItems().clear();
        }
    }
}
