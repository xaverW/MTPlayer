package de.p2tools.mtplayer.controller.worker;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.tools.P2ColorFactory;
import javafx.scene.paint.Color;

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
        if (ProgData.getInstance().mtPlayerController == null) {
            // beim Laden der Config!!
            return;
        }

        String gui;
        String background;
        String titleBar;
        String titleBarSel;
        boolean transparent;
        boolean transparentTitleBar;
        boolean transparentTitleBarSel;
        if (ProgConfig.SYSTEM_DARK_THEME.get()) {
            // DARK
            if (ProgConfig.SYSTEM_GUI_THEME_1.get()) {
                gui = ProgConfig.SYSTEM_GUI_THEME_DARK_1.get();
                background = ProgConfig.SYSTEM_GUI_BACKGROUND_DARK_1.get();
                titleBar = ProgConfig.SYSTEM_GUI_TITLE_BAR_DARK_1.get();
                titleBarSel = ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_DARK_1.get();
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_DARK_1.get());
                transparent = ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_1.get();
                transparentTitleBar = ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_DARK_1.get();
                transparentTitleBarSel = ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_1.get();
            } else {
                gui = ProgConfig.SYSTEM_GUI_THEME_DARK_2.get();
                background = ProgConfig.SYSTEM_GUI_BACKGROUND_DARK_2.get();
                titleBar = ProgConfig.SYSTEM_GUI_TITLE_BAR_DARK_2.get();
                titleBarSel = ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_DARK_2.get();
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_DARK_2.get());
                transparent = ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_DARK_2.get();
                transparentTitleBar = ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_DARK_2.get();
                transparentTitleBarSel = ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_2.get();
            }

        } else {
            // LIGHT
            if (ProgConfig.SYSTEM_GUI_THEME_1.get()) {
                gui = ProgConfig.SYSTEM_GUI_THEME_LIGHT_1.get();
                background = ProgConfig.SYSTEM_GUI_BACKGROUND_LIGHT_1.get();
                titleBar = ProgConfig.SYSTEM_GUI_TITLE_BAR_LIGHT_1.get();
                titleBarSel = ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_LIGHT_1.get();
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_LIGHT_1.get());
                transparent = ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_1.get();
                transparentTitleBar = ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_LIGHT_1.get();
                transparentTitleBarSel = ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_1.get();
            } else {
                gui = ProgConfig.SYSTEM_GUI_THEME_LIGHT_2.get();
                background = ProgConfig.SYSTEM_GUI_BACKGROUND_LIGHT_2.get();
                titleBar = ProgConfig.SYSTEM_GUI_TITLE_BAR_LIGHT_2.get();
                titleBarSel = ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_LIGHT_2.get();
                ProgConfig.SYSTEM_ICON_COLOR.set(ProgConfig.SYSTEM_ICON_THEME_LIGHT_2.get());
                transparent = ProgConfig.SYSTEM_GUI_BACKGROUND_TRANSPARENT_LIGHT_2.get();
                transparentTitleBar = ProgConfig.SYSTEM_GUI_TITLE_BAR_TRANSPARENT_LIGHT_2.get();
                transparentTitleBarSel = ProgConfig.SYSTEM_GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_2.get();
            }
        }
        PIconFactory.setColor();
        ProgConfig.SYSTEM_GUI_COLOR.set(gui); // damit wird dann das CSS neu geladen
        ProgConfig.SYSTEM_BACKGROUND_COLOR.set(background); // damit wird dann das CSS neu geladen
        ProgConfig.SYSTEM_TITLE_BAR_COLOR.set(titleBar); // damit wird dann das CSS neu geladen
        ProgConfig.SYSTEM_TITLE_BAR_SEL_COLOR.set(titleBarSel); // damit wird dann das CSS neu geladen
        setCss(transparent, transparentTitleBar, transparentTitleBarSel);
    }

    private static void setCss(boolean transparent, boolean titleBarTransparent, boolean titleBarSelTransparent) {
        // ================
        // Gui-TitleBar
        String guiTitleBar = P2ColorFactory.getColor(ProgConfig.SYSTEM_TITLE_BAR_COLOR.getValueSafe());
        if (!guiTitleBar.isEmpty() && !titleBarTransparent) {
            guiTitleBar = "-pTitleBarColor: " + guiTitleBar + "; ";
        } else {
            guiTitleBar = "-pTitleBarColor: transparent; ";
        }

        String guiTitleBarBorder;
        if (ProgConfig.SYSTEM_DARK_THEME.get()) {
            guiTitleBarBorder = "-pTitleBarBorderColor: #c8c8c8; ";
        } else {
            guiTitleBarBorder = "-pTitleBarBorderColor: #505050; ";
        }

        // Gui-TitleBar-Text
        String guiTitleBarText = P2ColorFactory.getMaxColor(ProgConfig.SYSTEM_TITLE_BAR_COLOR.getValueSafe());
        if (titleBarTransparent && ProgConfig.SYSTEM_DARK_THEME.get()) {
            guiTitleBarText = "-pTitleBarText: " + P2ColorFactory.getColor(Color.WHITE) + "; ";

        } else if (titleBarTransparent) {
            guiTitleBarText = "-pTitleBarText: " + P2ColorFactory.getColor(Color.BLACK) + "; ";

        } else {
            if (!guiTitleBarText.isEmpty()) {
                guiTitleBarText = "-pTitleBarText: " + guiTitleBarText + "; ";
            }
        }

        // Gui-TitleBar-Sel
        String guiTitleBarSel = P2ColorFactory.getColor(ProgConfig.SYSTEM_TITLE_BAR_SEL_COLOR.getValueSafe());
        if (!guiTitleBarSel.isEmpty() && !titleBarSelTransparent) {
            guiTitleBarSel = "-pTitleBarSelColor: " + guiTitleBarSel + "; ";
        } else {
            guiTitleBarSel = "-pTitleBarSelColor: transparent; ";
        }
        String guiTitleBarBorderSel = P2ColorFactory.getHalfMaxColor(ProgConfig.SYSTEM_TITLE_BAR_SEL_COLOR.getValueSafe(), 0.2);
        if (!guiTitleBarBorderSel.isEmpty()) {
            guiTitleBarBorderSel = "-pTitleBarSelBorderColor: " + guiTitleBarBorderSel + "; ";
//            guiTitleBarBorerSel = "-pTitleBarSelBorderColor: green; ";
        }

        // Gui-TitleBar-Sel-Text
        String guiTitleBarSelText = P2ColorFactory.getMaxColor(ProgConfig.SYSTEM_TITLE_BAR_SEL_COLOR.getValueSafe());
        if (titleBarSelTransparent && ProgConfig.SYSTEM_DARK_THEME.get()) {
            guiTitleBarSelText = "-pTitleBarSelText: " + P2ColorFactory.getColor(Color.WHITE) + "; ";

        } else if (titleBarSelTransparent) {
            guiTitleBarSelText = "-pTitleBarSelText: " + P2ColorFactory.getColor(Color.BLACK) + "; ";

        } else {
            if (!guiTitleBarSelText.isEmpty()) {
                guiTitleBarSelText = "-pTitleBarSelText: " + guiTitleBarSelText + "; ";
            }
        }


        // ================
        // Gui-Color
        String guiColor = P2ColorFactory.getColor(ProgConfig.SYSTEM_GUI_COLOR.getValueSafe());
        if (!guiColor.isEmpty()) {
            guiColor = "-pGuiColor: " + guiColor + "; ";
        }
        String guiColorDark = P2ColorFactory.changeColor(ProgConfig.SYSTEM_GUI_COLOR.getValueSafe(), 0.5);
        if (!guiColorDark.isEmpty()) {
            guiColorDark = "-pGuiColorDark: " + guiColorDark + "; ";
        }
        String guiColorLight = P2ColorFactory.changeColor(ProgConfig.SYSTEM_GUI_COLOR.getValueSafe(), 1.5);
        if (!guiColorLight.isEmpty()) {
            guiColorLight = "-pGuiColorLight: " + guiColorLight + "; ";
        }

        // ================
        // Text GuiColor
        String guiTextColor = P2ColorFactory.getMaxColor(ProgConfig.SYSTEM_GUI_COLOR.getValueSafe());
        String guiTextColorLight = P2ColorFactory.getMaxColor(P2ColorFactory.changeColor(ProgConfig.SYSTEM_GUI_COLOR.getValueSafe(), 1.3));
        String guiTextColorDark = P2ColorFactory.getMaxColor(P2ColorFactory.changeColor(ProgConfig.SYSTEM_GUI_COLOR.getValueSafe(), 0.7));
        if (!guiTextColor.isEmpty()) {
            guiTextColor = "-pGuiTextColor: " + guiTextColor + "; ";
        }

        if (!guiTextColorLight.isEmpty()) {
            guiTextColorLight = "-pGuiTextColorLight: " + guiTextColorLight + "; ";
        }
        if (!guiTextColorDark.isEmpty()) {
            guiTextColorDark = " -pGuiTextColorDark: " + guiTextColorDark + "; ";
        }


        // ================
        // Background
        String guiBackground = P2ColorFactory.getColor(ProgConfig.SYSTEM_BACKGROUND_COLOR.getValueSafe());
        if (!guiBackground.isEmpty() && !transparent) {
            guiBackground = "-pBackgroundColor: " + guiBackground + "; ";
        } else {
            guiBackground = "-pBackgroundColor: transparent; ";
        }

        String guiBackupSel = P2ColorFactory.changeColor(ProgConfig.SYSTEM_BACKGROUND_COLOR.getValueSafe(), 0.8);
        if (!guiBackupSel.isEmpty() && !transparent) {
            guiBackupSel = "-pBackgroundColorSel: " + guiBackupSel + ";";
        } else {
            guiBackupSel = "-pBackgroundColorSel: transparent; ";
        }

        // ================
        // Text Background
        String backgroundTextColor = P2ColorFactory.getMaxColor(ProgConfig.SYSTEM_BACKGROUND_COLOR.getValueSafe());
        String backgroundSelTextColor = P2ColorFactory.getMaxColor(P2ColorFactory.changeColor(ProgConfig.SYSTEM_BACKGROUND_COLOR.getValueSafe(), 0.8));
        if (transparent && ProgConfig.SYSTEM_DARK_THEME.get()) {
            backgroundTextColor = "-pBackgroundTextColor: " + P2ColorFactory.getColor(Color.WHITE) + "; ";
            backgroundSelTextColor = "-pBackgroundSelTextColor: " + P2ColorFactory.getColor(Color.WHITE) + "; ";

        } else if (transparent) {
            backgroundTextColor = "-pBackgroundTextColor: " + P2ColorFactory.getColor(Color.BLACK) + "; ";
            backgroundSelTextColor = "-pBackgroundSelTextColor: " + P2ColorFactory.getColor(Color.BLACK) + "; ";

        } else {
            if (!backgroundTextColor.isEmpty()) {
                backgroundTextColor = "-pBackgroundTextColor: " + backgroundTextColor + "; ";
            }
            if (!backgroundSelTextColor.isEmpty()) {
                backgroundSelTextColor = "-pBackgroundSelTextColor: " + backgroundSelTextColor + "; ";
            }
        }

        // ================
        // GREY
        String textColorMax = "";
        String backgroundColorGray = "";
        String backgroundColorGraySel = "";
        if (ProgConfig.SYSTEM_DARK_THEME.get()) {
            // DARK
            textColorMax = "-pTextColorMax: " + P2ColorFactory.getColor(Color.WHITE) + "; ";
            backgroundColorGray = "-pBackgroundColorGray: #646464; ";
            backgroundColorGraySel = "-pBackgroundColorGraySel: #434343; ";

        } else {
            // LIGHT
            textColorMax = "-pTextColorMax: " + P2ColorFactory.getColor(Color.BLACK) + "; ";
            backgroundColorGray = "-pBackgroundColorGray: #d4d4d4; ";
            backgroundColorGraySel = "-pBackgroundColorGraySel: #b1b1b1; ";
        }

        ProgConfig.SYSTEM_CSS_ADDER.set(guiColor + guiColorDark + guiColorLight +
                guiTextColor + guiTextColorLight + guiTextColorDark +
                guiTitleBar + guiTitleBarText + guiTitleBarSel + guiTitleBarSelText +
                guiTitleBarBorder + guiTitleBarBorderSel + backgroundTextColor + backgroundSelTextColor +
                guiBackground + guiBackupSel + textColorMax + backgroundColorGray + backgroundColorGraySel);
    }
}
