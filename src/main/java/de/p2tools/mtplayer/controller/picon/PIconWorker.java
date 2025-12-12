package de.p2tools.mtplayer.controller.picon;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import javafx.scene.paint.Color;

public class PIconWorker {
    private final ProgData progData;

    public PIconWorker(ProgData progData) {
        this.progData = progData;

        ProgConfig.SYSTEM_DARK_THEME.addListener((u, o, n) -> {
            setColor();
        });
        ProgConfig.SYSTEM_BLACK_WHITE_ICON.addListener((u, o, n) -> {
            setColor();
        });
    }

    private void setColor() {
        final String BLUE_LIGHT = "#376cb5";
        final String BLUE_DARK = "#4b92f5";
        final String DARK = Color.LIGHTGREY.toString();
        final String LIGHT = "#444444";
        if (ProgConfig.SYSTEM_DARK_THEME.get()) {
            if (ProgConfig.SYSTEM_BLACK_WHITE_ICON.get()) {
                // dark - BW
                ProgConfig.SYSTEM_ICON_COLOR.set(DARK);

            } else {
                // dark - blue
                ProgConfig.SYSTEM_ICON_COLOR.set(BLUE_DARK);
            }
        } else {
            if (ProgConfig.SYSTEM_BLACK_WHITE_ICON.get()) {
                // light - BW
                ProgConfig.SYSTEM_ICON_COLOR.set(LIGHT);

            } else {
                // light - blue
                ProgConfig.SYSTEM_ICON_COLOR.set(BLUE_LIGHT);
            }
        }
        PIconFactory.setColor();
    }
}
