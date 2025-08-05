package de.p2tools.mtplayer.gui.filter.helper;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import javafx.beans.property.StringProperty;
import org.controlsfx.control.SearchableComboBox;

public class PCboThemeExact extends SearchableComboBox<String> {

    public PCboThemeExact(boolean audio, ProgData progData, StringProperty stringPropertyThemeExact) {
        if (audio) {
            setItems(ThemeListFactory.themeForChannelListAudio);
            ThemeListFactory.themeForChannelChangedAudio.addListener((u, o, n) -> {
                setItems(ThemeListFactory.themeForChannelListAudio);
            });
            getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        if (!progData.filterWorkerAudio.getActFilterSettings().isThemeIsExact()) {
                            // dann betrifft es das nicht
                            return;
                        }

                        final String str = newValue == null ? "" : newValue;
                        stringPropertyThemeExact.setValue(str);
                    }
            );

        } else {
            setItems(ThemeListFactory.themeForChannelListFilm);
            ThemeListFactory.themeForChannelChangedFilm.addListener((u, o, n) -> {
                setItems(ThemeListFactory.themeForChannelListFilm);
            });
            getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        if (!progData.filterWorkerFilm.getActFilterSettings().isThemeIsExact()) {
                            // dann betrifft es das nicht
                            return;
                        }

                        final String str = newValue == null ? "" : newValue;
                        stringPropertyThemeExact.setValue(str);
                    }
            );
        }

        stringPropertyThemeExact.addListener((u, o, n) -> {
            getSelectionModel().select(stringPropertyThemeExact.getValue());
        });

        getSelectionModel().select(stringPropertyThemeExact.getValue());
    }
}
