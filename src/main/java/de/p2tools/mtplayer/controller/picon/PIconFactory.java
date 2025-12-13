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
        BTN_PLUS("mdi-plus-circle-outline", 18),
        BTN_MINUS("mdi-minus-circle-outline", 18),
        BTN_NEXT("mdi-chevron-double-right", 18),
        BTN_PREV("mdi-chevron-double-left", 18),
        BTN_QUIT("gmi-power-settings-new", 18),
        BTN_CLEAR("gmi-clear", 18),
        BTN_RESET_1("gmi-radio-button-on", 25),
        BTN_RESET_2("gmi-rotate-right", 30),
        BTN_RESET("mdi-rotate-3d", 18),
        BTN_SEARCH("gmi-search", 20),
        BTN_EDIT("mdomz-settings", 20),


        ICON_BOOLEAN_ON("mdral-done_outline", 20),

        TOOL_BTN_PLAY("mdmz-play_arrow", 25),
        TOOL_BTN_PLAY_ALL("gmi-fast-forward", 25),
        TOOL_BTN_RECORDE("gmi-fiber-manual-record", 25),

        TOOL_BTN_ADD_BOOKMARK("mdi-bookmark-plus-outline", 25),
        TOOL_BTN_DEL_BOOKMARK("mdi2b-bookmark-minus-outline", 25),
        TOOL_BTN_DEL_ALL_BOOKMARK("mdi2b-bookmark-off-outline", 25),
        TOOL_BTN_SHOW_BOOKMARK("mdi2b-bookmark-outline", 25),
        TOOL_BTN_SHOW_BOOKMARK_DIALOG("mdi2c-comment-bookmark-outline", 25),

        TABLE_FILE_DEL("gmi-clear", 15),
        TABLE_DIR_OPEN("mdi2f-folder-open-outline", 16),
        TABLE_START("mdomz-play_arrow", 20),

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
