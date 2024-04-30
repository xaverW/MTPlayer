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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.mtfilter.FilterCheckRegEx;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;

import java.util.function.BooleanSupplier;

public class PCboStringSearch extends ComboBox<PCboSearchLabel> {
    public static final int MAX_FILTER_HISTORY = 15;
    private final StringProperty strSearchProperty;
    private final ProgData progData;
    private final BooleanSupplier actFilter;

    public PCboStringSearch(ProgData progData, StringProperty strSearchProperty, BooleanSupplier actFilter) {
        this.progData = progData;
        this.strSearchProperty = strSearchProperty;
        this.actFilter = actFilter;
        setEditable(true);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setVisibleRowCount(MAX_FILTER_HISTORY);
        for (int i = 0; i < MAX_FILTER_HISTORY; ++i) {
            getItems().add(new PCboSearchLabel());
        }
        init();
    }

    private void init() {
        FilterCheckRegEx regEx = new FilterCheckRegEx(getEditor());
        getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            if (!this.isShowing()) {
                // dann ist eine Auswahl aus der Combo
                addLastFilter();
            }
            regEx.checkPattern();
            strSearchProperty.setValue(getEditor().getText());
        });

        getSelectionModel().selectedItemProperty().addListener(
                (ChangeListener<Object>) (observable, oldValue, newValue) -> {
                    // kann auch ein String!!!! sein
                    if (ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
                        // dann melden
                        if (this.isShowing() ||
                                newValue != null &&
                                        newValue.getClass().equals(PCboSearchLabel.class) &&
                                        !strSearchProperty.getValueSafe().equals(((PCboSearchLabel) newValue).getText())) {
                            actFilter.getAsBoolean();
                        }
                    }
                });

        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                actFilter.getAsBoolean();
            }
        });
        strSearchProperty.addListener((u, o, n) -> getEditor().setText(strSearchProperty.getValue()));
        getEditor().setText(strSearchProperty.getValue());
    }

    private synchronized void addLastFilter() {
        final String filterStr = getEditor().getText();
        if (filterStr == null || filterStr.isEmpty()) {
            return;
        }

        final PCboSearchLabel tmp = getItems().get(1);
        if (filterStr.contains(tmp.getText())) {
            // dann wird der erste damit ersetzt
            tmp.setText(filterStr);
            return;
        }

        // dann wird jetzt einfach weitergeschaltet
        for (int i = getItems().size() - 2; i >= 1; --i) {
            getItems().get(i + 1).setText(getItems().get(i).getText());
        }
        getItems().get(1).setText(filterStr);
    }
}
