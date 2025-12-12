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
        if (ProgConfig.SYSTEM_DARK_THEME.get()) {
            if (ProgConfig.SYSTEM_BLACK_WHITE_ICON.get()) {
                ProgConfig.SYSTEM_ICON_COLOR.set(Color.LIGHTGREY.toString());
            } else {
                ProgConfig.SYSTEM_ICON_COLOR.set(Color.BLUE.toString());
            }
        } else {
            if (ProgConfig.SYSTEM_BLACK_WHITE_ICON.get()) {
                ProgConfig.SYSTEM_ICON_COLOR.set(Color.BLACK.toString());
            } else {
                ProgConfig.SYSTEM_ICON_COLOR.set(Color.BLUE.toString());
            }
        }
        PIconFactory.setColor();
    }
}
