package de.p2tools.mtplayer.gui.filter.helper;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.p2lib.tools.log.PLog;
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

                    stringPropertyThemeExact.setValue(newValue);
                }
        );
        stringPropertyThemeExact.addListener((u, o, n) -> {
            PLog.debugLogCount("PCboThemeExact: " + stringPropertyThemeExact.getValue());
            getSelectionModel().select(stringPropertyThemeExact.getValue());
        });
        getSelectionModel().select(stringPropertyThemeExact.getValue());
    }
}
