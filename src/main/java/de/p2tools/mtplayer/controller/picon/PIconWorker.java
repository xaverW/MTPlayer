package de.p2tools.mtplayer.controller.picon;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;

public class PIconWorker {
    private final ProgData progData;

    public PIconWorker(ProgData progData) {
        this.progData = progData;

        ProgConfig.SYSTEM_THEME_DARK.addListener((u, o, n) -> {
            setColor();
        });
        ProgConfig.SYSTEM_ICON_THEME_1.addListener((u, o, n) -> {
            setColor();
        });
    }

    public void setColor() {
        if (ProgConfig.SYSTEM_THEME_DARK.get()) {
            // DARK
            if (ProgConfig.SYSTEM_ICON_THEME_1.get()) {
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_DARK_1.get());

            } else {
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_DARK_2.get());
            }

        } else {
            // LIGHT
            if (ProgConfig.SYSTEM_ICON_THEME_1.get()) {
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_LIGHT_1.get());

            } else {
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_LIGHT_2.get());
            }
        }

        PIconFactory.setColor();
    }
}
