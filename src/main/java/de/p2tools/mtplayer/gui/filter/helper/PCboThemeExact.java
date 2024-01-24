package de.p2tools.mtplayer.gui.filter.helper;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import javafx.beans.property.StringProperty;
import org.controlsfx.control.SearchableComboBox;

public class PCboThemeExact extends SearchableComboBox<String> {

    public PCboThemeExact(ProgData progData, StringProperty stringPropertyThemeExact) {
        super();
        setItems(ThemeListFactory.themeForChannelList);
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (!progData.filmFilterWorker.getActFilterSettings().isThemeExact()) {
                        // dann betrifft es das nicht
                        return;
                    }
                    if (newValue != null && !stringPropertyThemeExact.getValueSafe().equals(newValue)) {
                        stringPropertyThemeExact.setValue(newValue);
                    }
                }
        );
    }
}
