package de.p2tools.mtplayer.controller.picon;

import de.p2tools.p2lib.ikonli.P2IconFactory;
import org.kordamp.ikonli.javafx.FontIcon;

public class PIconFactory {

    public enum PICON implements P2IconFactory.P2Icon {
        BTN_UP_DOWN_H("mdi2p-pan-horizontal", 20),
        BTN_SEPARATOR("mdi2t-tilde", 20),
        BTN_SEPARATOR_WIDTH("mdi-ray-start-end", 20),
        ICON_BOOLEAN_ON("mdral-done_outline", 20),
        TOOLBAR_BTN_BOOKMARK_SHOW_DIALOG("mdi2c-comment-bookmark-outline", 25),
        TOOLBAR_BTN_DOWNLOAD_START_TIME("mdmz-more_time", 25);


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

        public FontIcon getFontIcon() {
            return P2IconFactory.getIcon(literal, size);
        }

        public FontIcon getFontIcon(int size) {
            this.size = size;
            return P2IconFactory.getIcon(literal, size);
        }
    }

    private PIconFactory() {
    }
}
