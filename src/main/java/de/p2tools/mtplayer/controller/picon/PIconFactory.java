package de.p2tools.mtplayer.controller.picon;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.ikonli.IkonlyFactory;
import de.p2tools.p2lib.ikonli.P2IconFactory;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

public class PIconFactory {

    public enum PICON implements P2IconFactory.P2Icon {
        BTN_HELP("mdi-help"),
        BTN_DIR_OPEN("mdi2f-folder-open-outline", 18),
        BTN_PLUS_OUTLINE("mdi-plus-circle-outline", 18),
        BTN_MINUS_OUTLINE("mdi-minus-circle-outline", 18),
        BTN_NEXT("mdi-chevron-double-right", 18),
        BTN_PREV("mdi-chevron-double-left", 18),
        BTN_QUIT("gmi-power-settings-new", 18),
        BTN_CLEAR("gmi-clear", 18),
        BTN_RESET_1("gmi-radio-button-on", 25),
        BTN_RESET_2("gmi-rotate-right", 30),
        BTN_RESET("mdi-rotate-3d", 18),
        BTN_SEARCH("gmi-search", 20),
        BTN_EDIT("mdomz-settings", 20),
        BTN_BACK("gmi-arrow-back-ios", 20),
        BTN_FORWARD("gmi-arrow-forward-ios", 20),
        BTN_QUIT_DIALOG("mdoal-forward", 20),
        BTN_PLAY("gmi-play-arrow", 20),
        BTN_UP_DOWN_H("mdi2p-pan-horizontal", 20),
        BTN_STOP("gmi-clear", 20),
        BTN_MINUS("mdmz-minus", 20),
        BTN_PLUS("mdal-add", 20),
        BTN_SEPARATOR("mdi2t-tilde", 20),
        BTN_SEPARATOR_WIDTH("mdi-ray-start-end", 20),
        BTN_TOP("gmi-vertical-align-top", 20),
        BTN_UP("mdoal-arrow_upward", 20),
        BTN_DOWN("mdoal-arrow_downward", 20),
        BTN_BOTTOM("gmi-vertical-align-bottom", 20),

        ICON_BOOLEAN_ON("mdral-done_outline", 20),

        TABLE_ABO_ON("gmi-play-arrow", 18),
        TABLE_ABO_OFF("gmi-pause", 18),
        TABLE_ABO_DEL("gmi-clear", 18),
        TABLE_ABO_ON_BIG("gmi-play-arrow", 22),
        TABLE_ABO_OFF_BIG("gmi-pause", 22),
        TABLE_ABO_DEL_BIG("gmi-clear", 22),

        TABLE_FILM_PLAY("gmi-play-arrow", 18),
        TABLE_FILM_SAVE("gmi-fiber-manual-record", 16),
        TABLE_BOOKMARK_ADD("mdi-bookmark-plus-outline", 18),
        TABLE_BOOKMARK_DEL("mdi2b-bookmark-off-outline", 18),
        TABLE_FILM_PLAY_BIG("gmi-play-arrow", 22),
        TABLE_FILM_SAVE_BIG("gmi-fiber-manual-record", 19),
        TABLE_BOOKMARK_ADD_BIG("mdi-bookmark-plus-outline", 23),
        TABLE_BOOKMARK_DEL_BIG("mdi2b-bookmark-off-outline", 23),

        TABLE_DOWNLOAD_START("mdi2t-triangle-down", 14),
        TABLE_DOWNLOAD_DEL("gmi-clear", 18),
        TABLE_DOWNLOAD_STOP("gmi-pause", 18),
        TABLE_DOWNLOAD_OPEN_DIR("mdi2f-folder-open-outline", 18),
        TABLE_DOWNLOAD_START_BIG("mdi2t-triangle-down", 18),
        TABLE_DOWNLOAD_DEL_BIG("gmi-clear", 22),
        TABLE_DOWNLOAD_STOP_BIG("gmi-pause", 22),
        TABLE_DOWNLOAD_OPEN_DIR_BIG("mdi2f-folder-open-outline", 22),

        TOOLBAR_BTN_PLAY("mdmz-play_arrow", 25),
        TOOLBAR_BTN_PLAY_ALL("gmi-fast-forward", 25),
        TOOLBAR_BTN_RECORDE("gmi-fiber-manual-record", 25),
        TOOLBAR_BTN_DOWNLOAD_CLEAN("mdal-cleaning_services", 25),
        TOOLBAR_BTN_DOWNLOAD_START("mdi2t-triangle-down", 20),
        TOOLBAR_BTN_DOWNLOAD_DEL("gmi-clear", 25),
        TOOLBAR_BTN_DOWNLOAD_UNDO("mdi2a-arrow-left-top-bold", 25),
        TOOLBAR_BTN_DOWNLOAD_REFRESH("mdi-rotate-3d", 25),
        TOOLBAR_BTN_DOWNLOAD_START_ALL("mdi2t-transfer-down", 25),
        TOOLBAR_BTN_DOWNLOAD_START_TIME("mdi2t-timer-sand", 25),
        TOOLBAR_BTN_ABO_CONFIG("mdomz-settings", 25),
        TOOLBAR_BTN_ABO_ADD("gmi-add", 25),
        TOOLBAR_BTN_ABO_ON("gmi-play-arrow", 25),
        TOOLBAR_BTN_ABO_OFF("gmi-pause", 25),
        TOOLBAR_BTN_ABO_DEL("gmi-clear", 25),
        TOOLBAR_BTN_BOOKMARK_ADD("mdi-bookmark-plus-outline", 25),
        TOOLBAR_BTN_BOOKMARK_DEL("mdi2b-bookmark-minus-outline", 25),
        TOOLBAR_BTN_BOOKMARK_DAL_ALL("mdi2b-bookmark-off-outline", 25),
        TOOLBAR_BTN_BOOKMARK_SHOW("mdoal-collections_bookmark", 25),
        TOOLBAR_BTN_BOOKMARK_SHOW_DIALOG("mdi2c-comment-bookmark-outline", 25),

        OWN_FILTER_LOAD("mdi2t-tray-arrow-up", 25),
        OWN_FILTER_SAVE("mdi2t-tray-arrow-down", 25),
        OWN_FILTER_NEW("gmi-add", 25),

        ATTENTION("mdomz-report", 80),
        PROG_MENU("gmi-menu", 25),
        TAB_MENU("gmi-menu", 20);

        private final String literal;
        private int size = 18;

        PICON(String literal) {
            this.literal = literal;
        }

        PICON(String literal, int size) {
            this.literal = literal;
            this.size = size;
        }

        public String getLiteral() {
            return literal;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public FontIcon getFontIcon() {
            return getIcon(literal, size);
        }

        public FontIcon getFontIcon(int size) {
            this.size = size;
            return getIcon(literal, size);
        }

        @Override
        public String toString() {
            return literal;
        }
    }

    private PIconFactory() {
    }

    public static void setColor() {
        IkonlyFactory.getAllNodes(ProgData.getInstance().mtPlayerController);
    }

    public static FontIcon getIcon(String literal, int size) {
        FontIcon fontIcon = new FontIcon();
        fontIcon.setIconSize(size);
        fontIcon.setIconColor(Paint.valueOf(ProgConfig.SYSTEM_ICON_COLOR.getValueSafe()));
        fontIcon.setIconLiteral(literal);
        return fontIcon;
    }

    public static Button getHelpButton(String header, String helpText) {
        return P2Button.helpButton(PIconFactory.PICON.BTN_HELP.getFontIcon(), header, helpText);
    }

    public static Button getHelpButton(Stage stage, String header, String helpText) {
        return P2Button.helpButton(stage, PIconFactory.PICON.BTN_HELP.getFontIcon(), header, helpText);
    }

    public static Button getHelpButton(ObjectProperty<Stage> stage, String header, String helpText) {
        return P2Button.helpButton(stage, PIconFactory.PICON.BTN_HELP.getFontIcon(), header, helpText);
    }
}
