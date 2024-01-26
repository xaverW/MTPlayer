package de.p2tools.mtplayer.gui.filter.helper;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import javafx.beans.property.StringProperty;
import org.controlsfx.control.SearchableComboBox;

public class PCboThemeExact extends SearchableComboBox<String> {
    public PCboThemeExact(ProgData progData, StringProperty stringPropertyThemeExact) {

        setItems(ThemeListFactory.themeForChannelList);
        ThemeListFactory.themeForChannelChanged.addListener((u, o, n) -> {
            // kommt sonst zu Laufzeitfehlern!
            setItems(ThemeListFactory.themeForChannelList);
        });

        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (!progData.filmFilterWorker.getActFilterSettings().isThemeIsExact()) {
                        // dann betrifft es das nicht
                        return;
                    }
                    stringPropertyThemeExact.setValue(newValue == null ? "" : newValue);
                }
        );
        stringPropertyThemeExact.addListener((u, o, n) -> {
            getSelectionModel().select(stringPropertyThemeExact.getValue());
        });
        getSelectionModel().select(stringPropertyThemeExact.getValue());
    }
}
