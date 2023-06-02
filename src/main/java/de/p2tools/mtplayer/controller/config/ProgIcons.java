/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */


package de.p2tools.mtplayer.controller.config;

import de.p2tools.p2lib.icons.GetIcon;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProgIcons {
    public static String ICON_PATH = "/de/p2tools/mtplayer/res/program/";


    public enum Icons {
        ICON_DIALOG_ON("dialog-ein.png", "dialog-ein-sw.png"),
        IMAGE_ACHTUNG_64("achtung_64.png"),

        ICON_BUTTON_CHANGE("button-change.png", 16, 16),
        ICON_BUTTON_CLEAN("clean_16.png", 16, 16),
        ICON_BUTTON_RESET("button-reset.png", 16, 16),
        ICON_BUTTON_UPDATE("button-update.png", 16, 16),
        ICON_BUTTON_PROPOSE("button-propose.png", 16, 16),
        ICON_BUTTON_EDIT("button-edit.png", 16, 16),
        ICON_BUTTON_EDIT_FILTER("button-edit-filter.png", 16, 16),
        ICON_BUTTON_BACKWARD("button-backward.png", 16, 16),
        ICON_BUTTON_FORWARD("button-forward.png", 16, 16),
        ICON_BUTTON_EDIT_ABO_PATH("button-edit-filter.png", 16, 16),
        ICON_BUTTON_MENU("button-menu.png", 18, 15),
        ICON_BUTTON_QUIT("button-quit.png", 16, 16),
        ICON_BUTTON_FILE_OPEN("button-file-open.png", 16, 16),
        ICON_BUTTON_PLAY("button-play.png", 16, 16),
        ICON_BUTTON_CLEAR("button-clear.png", 16, 16),
        ICON_DIALOG_QUIT("dialog-quit.png", 64, 64),
        IMAGE_TABLE_FILM_PLAY("table-film-play.png", 14, 14),
        IMAGE_TABLE_FILM_SAVE("table-film-save.png", 14, 14),
        IMAGE_TABLE_DOWNLOAD_START("table-download-start.png", 14, 14),
        IMAGE_TABLE_DOWNLOAD_DEL("table-download-del.png", 14, 14),
        IMAGE_TABLE_DOWNLOAD_STOP("table-download-stop.png", 14, 14),
        IMAGE_TABLE_DOWNLOAD_OPEN_DIR("table-download-open-dir.png", 14, 14),

        ICON_BUTTON_STOP("button-stop.png", 16, 16),
        ICON_BUTTON_NEXT("button-next.png", 16, 16),
        ICON_BUTTON_PREV("button-prev.png", 16, 16),
        ICON_BUTTON_REMOVE("button-remove.png", 16, 16),
        ICON_BUTTON_SEPARATOR("button-separator.png", 22, 16),
        ICON_BUTTON_ADD("button-add.png", 16, 16),
        ICON_BUTTON_MOVE_DOWN("button-move-down.png", 16, 16),
        ICON_BUTTON_MOVE_UP("button-move-up.png", 16, 16),
        ICON_BUTTON_MOVE_TOP("button-move-top.png", 16, 16),
        ICON_BUTTON_MOVE_BOTTOM("button-move-bottom.png", 16, 16),

        ICON_TOOLBAR_MENU("toolbar-menu.png", 18, 15),
        ICON_TOOLBAR_MENU_TOP("toolbar-menu-top.png", 32, 18),
        ICON_TOOLBAR_DOWNLOAD_CLEAN("toolbar-download-clean.png", 32, 32),
        ICON_TOOLBAR_DOWNLOAD_START("toolbar-download-starten.png", 32, 32),
        ICON_TOOLBAR_DOWNLOAD_DEL("toolbar-download-del.png", 32, 32),
        ICON_TOOLBAR_DOWNLOAD_UNDO("toolbar-download-undo.png", 32, 32),
        ICON_TOOLBAR_DOWNLOAD_FILM_START("toolbar-download-film-start.png", 32, 32),
        ICON_TOOLBAR_DOWNLOAD_REFRESH("toolbar-download-refresh.png", 32, 32),
        ICON_TOOLBAR_DOWNLOAD_START_ALL("toolbar-download-start-all.png", 32, 32),
        ICON_TOOLBAR_DOWNLOAD_START_ALL_TIME("toolbar-download-start-time.png", 32, 32),

        ICON_TOOLBAR_FILM_START("toolbar-filme-start.png", 32, 32),
        ICON_TOOLBAR_FILM_REC("toolbar-filme-rec.png", 32, 32),
        ICON_TOOLBAR_FILM_BOOKMARK("toolbar-filme-bookmark.png", 32, 32),
        ICON_TOOLBAR_FILM_BOOKMARK_FILTER("toolbar-filme-bookmark-filter.png", 32, 32),
        ICON_TOOLBAR_FILM_DEL_BOOKMARK("toolbar-filme-del-bookmark.png", 32, 32),
        ICON_TOOLBAR_FILM_DEL_ALL_BOOKMARK("toolbar-filme-del-all-bookmark.png", 32, 32),

        ICON_TOOLBAR_ABO_NEW("toolbar-abo-new.png", 32, 32),
        ICON_TOOLBAR_ABO_ON("toolbar-abo-on.png", 32, 32),
        ICON_TOOLBAR_ABO_OFF("toolbar-abo-off.png", 32, 32),
        ICON_TOOLBAR_ABO_DEL("toolbar-abo-del.png", 32, 32),
        ICON_TOOLBAR_ABO_CONFIG("toolbar-abo-config.png", 32, 32),

        ICON_FILTER_FILM_LOAD("filter-film-load.png", 22, 22),
        ICON_FILTER_FILM_SAVE("filter-film-save.png", 22, 22),
        ICON_FILTER_FILM_NEW("filter-film-new.png", 22, 22);

        private String fileName;
        private String fileNameDark = "";
        private int w = 0;
        private int h = 0;

        Icons(String fileName, int w, int h) {
            this.fileName = fileName;
            this.w = w;
            this.h = h;
        }

        Icons(String fileName, String fileNameDark, int w, int h) {
            this.fileName = fileName;
            this.fileNameDark = fileNameDark;
            this.w = w;
            this.h = h;
        }

        Icons(String fileName) {
            this.fileName = fileName;
        }

        Icons(String fileName, String fileNameDark) {
            this.fileName = fileName;
            this.fileNameDark = fileNameDark;
        }

        public ImageView getImageView() {
            if (ProgConfig.SYSTEM_DARK_THEME.get() && !fileNameDark.isEmpty()) {
                return GetIcon.getImageView(fileNameDark, ICON_PATH, w, h);
            }
            return GetIcon.getImageView(fileName, ICON_PATH, w, h);
        }

        public Image getImage() {
            if (ProgConfig.SYSTEM_DARK_THEME.get() && !fileNameDark.isEmpty()) {
                return GetIcon.getImage(fileNameDark, ICON_PATH, w, h);
            }
            return GetIcon.getImage(fileName, ICON_PATH, w, h);
        }
    }
}
