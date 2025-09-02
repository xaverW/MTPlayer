package de.p2tools.mtplayer.gui.filter.helper;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filter.FilterDto;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import org.controlsfx.control.SearchableComboBox;

public class PCboThemeExact extends SearchableComboBox<String> {


    public PCboThemeExact(ProgData progData, FilterDto filterDto) {
        if (filterDto.audio) {
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
                        filterDto.filterWorker.getActFilterSettings().exactThemeProperty().setValue(str);
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
                        filterDto.filterWorker.getActFilterSettings().exactThemeProperty().setValue(str);
                    }
            );
        }

        filterDto.filterWorker.getActFilterSettings().exactThemeProperty().addListener((u, o, n) -> {
            getSelectionModel().select(filterDto.filterWorker.getActFilterSettings().exactThemeProperty().getValue());
        });

        getSelectionModel().select(filterDto.filterWorker.getActFilterSettings().exactThemeProperty().getValue());
    }
}
