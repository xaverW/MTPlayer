package de.p2tools.mtplayer.controller.picon;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.tools.P2ColorFactory;
import de.p2tools.p2lib.tools.log.P2Log;

public class PIconWorker {
    private final ProgData progData;

    public PIconWorker(ProgData progData) {
        this.progData = progData;

        ProgConfig.SYSTEM_DARK_THEME.addListener((u, o, n) -> {
            setColor();
        });
        ProgConfig.SYSTEM_GUI_THEME_1.addListener((u, o, n) -> {
            setColor();
        });
    }

    public void setColor() {
        String gui;
        if (ProgConfig.SYSTEM_DARK_THEME.get()) {
            // DARK
            if (ProgConfig.SYSTEM_GUI_THEME_1.get()) {
                gui = ProgConfig.SYSTEM_GUI_THEME_DARK_1.get();
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_DARK_1.get());

            } else {
                gui = ProgConfig.SYSTEM_GUI_THEME_DARK_2.get();
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_DARK_2.get());
            }

        } else {
            // LIGHT
            if (ProgConfig.SYSTEM_GUI_THEME_1.get()) {
                gui = ProgConfig.SYSTEM_GUI_THEME_LIGHT_1.get();
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_LIGHT_1.get());

            } else {
                gui = ProgConfig.SYSTEM_GUI_THEME_LIGHT_2.get();
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_LIGHT_2.get());
            }
        }
        gui = P2ColorFactory.getColor(gui);
        gui = ProgConst.GUI_COLOR_ROOT_1 + gui + ProgConst.GUI_COLOR_ROOT_2;

        PIconFactory.setColor();
        P2Log.sysLog("GUI-Color setzen: " + gui);
        ProgConfig.SYSTEM_GUI_COLOR.set(gui); // damit wird dann das CSS neu geladen
    }
}
