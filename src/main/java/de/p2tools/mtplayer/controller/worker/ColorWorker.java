package de.p2tools.mtplayer.controller.worker;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.dialogs.dialog.P2Dialog;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.tools.P2ColorFactory;

public class ColorWorker {
    private final ProgData progData;

    public ColorWorker(ProgData progData) {
        this.progData = progData;

        ProgConfig.SYSTEM_DARK_THEME.addListener((u, o, n) -> {
            ProgColorList.setColorTheme();
            setColor();
        });
        ProgConfig.SYSTEM_GUI_THEME_1.addListener((u, o, n) -> {
            setColor();
        });
    }

    public void setColor() {
        String gui;
        String background;
        if (ProgConfig.SYSTEM_DARK_THEME.get()) {
            // DARK
            if (ProgConfig.SYSTEM_GUI_THEME_1.get()) {
                gui = ProgConfig.SYSTEM_GUI_THEME_DARK_1.get();
                background = ProgConfig.SYSTEM_GUI_BACKGROUND_DARK_1.get();
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_DARK_1.get());

            } else {
                gui = ProgConfig.SYSTEM_GUI_THEME_DARK_2.get();
                background = ProgConfig.SYSTEM_GUI_BACKGROUND_DARK_2.get();
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_DARK_2.get());
            }

        } else {
            // LIGHT
            if (ProgConfig.SYSTEM_GUI_THEME_1.get()) {
                gui = ProgConfig.SYSTEM_GUI_THEME_LIGHT_1.get();
                background = ProgConfig.SYSTEM_GUI_BACKGROUND_LIGHT_1.get();
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_LIGHT_1.get());

            } else {
                gui = ProgConfig.SYSTEM_GUI_THEME_LIGHT_2.get();
                background = ProgConfig.SYSTEM_GUI_BACKGROUND_LIGHT_2.get();
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_LIGHT_2.get());
            }
        }
        PIconFactory.setColor();
        ProgConfig.SYSTEM_GUI_COLOR.set(gui); // damit wird dann das CSS neu geladen
        ProgConfig.SYSTEM_BACKGROUND_COLOR.set(background); // damit wird dann das CSS neu geladen
        setCss();
    }

    private static void setCss() {
        if (ProgData.getInstance().mtPlayerController == null) {
            // beim Laden der Config!!
            return;
        }

        // Gui-Color
        String guiColor = P2ColorFactory.getColor(ProgConfig.SYSTEM_GUI_COLOR.getValueSafe());
        if (!guiColor.isEmpty()) {
            guiColor = "-pGuiColor: " + guiColor + "; ";
        }

        String guiBackup = P2ColorFactory.getColor(ProgConfig.SYSTEM_BACKGROUND_COLOR.getValueSafe());
        if (!guiBackup.isEmpty()) {
            guiBackup = "-pBackgroundColor: " + guiBackup + "; ";
        }

        String guiBackupSel = P2ColorFactory.changeColor(ProgConfig.SYSTEM_BACKGROUND_COLOR.getValueSafe(), 0.8);
        if (!guiColor.isEmpty()) {
            guiBackupSel = "-pBackgroundColorSel: " + guiBackupSel + ";";
        }

        ProgData.getInstance().mtPlayerController.setStyle(guiColor + guiBackup + guiBackupSel);
        for (P2Dialog d : P2DialogExtra.getDialogList()) {
            d.getStage().getScene().getRoot().setStyle(guiBackupSel);
        }
    }
}
