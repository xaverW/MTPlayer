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


package de.p2tools.mtplayer.controller.data;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.res.GetIcon;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProgIcons {

    public final Image ICON_DIALOG_ON = GetIcon.getImage(ProgConfig.SYSTEM_DARK_THEME.getBool() ? "dialog-ein.png" : "dialog-ein-sw.png");
    public final Image IMAGE_ACHTUNG_32 = GetIcon.getImage("achtung_32.png");
    public final Image IMAGE_ACHTUNG_64 = GetIcon.getImage("achtung_64.png");

    public final ImageView ICON_BUTTON_RESET = GetIcon.getImageView("button-reset.png", 16, 16);
    public final ImageView ICON_BUTTON_UPDATE = GetIcon.getImageView("button-update.png", 16, 16);
    public final ImageView ICON_BUTTON_PROPOSE = GetIcon.getImageView("button-propose.png", 16, 16);
    public final ImageView ICON_BUTTON_EDIT_FILTER = GetIcon.getImageView("button-edit-filter.png", 16, 16);
    public final ImageView ICON_BUTTON_BACKWARD = GetIcon.getImageView("button-backward.png", 16, 16);
    public final ImageView ICON_BUTTON_FORWARD = GetIcon.getImageView("button-forward.png", 16, 16);
    public final ImageView ICON_BUTTON_EDIT_ABO_PATH = GetIcon.getImageView("button-edit-filter.png", 16, 16);
    public final ImageView ICON_BUTTON_MENU = GetIcon.getImageView("button-menu.png", 18, 15);
    public final ImageView ICON_BUTTON_QUIT = GetIcon.getImageView("button-quit.png", 16, 16);
    public final ImageView ICON_BUTTON_FILE_OPEN = GetIcon.getImageView("button-file-open.png", 16, 16);
    public final ImageView ICON_BUTTON_EXTERN_PROGRAM = GetIcon.getImageView("button-forward.png", 16, 16);
    public final ImageView ICON_BUTTON_PLAY = GetIcon.getImageView("button-play.png", 16, 16);
    public final ImageView ICON_BUTTON_CLEAR = GetIcon.getImageView("button-clear.png", 16, 16);

    public final ImageView ICON_DIALOG_QUIT = GetIcon.getImageView("dialog-quit.png", 64, 64);
    public final ImageView ICON_CLEAN_16 = GetIcon.getImageView("clean_16.png", 16, 16);

    // table
    public static final Image IMAGE_TABLE_FILM_PLAY = GetIcon.getImage("table-film-play.png", 14, 14);
    public static final Image IMAGE_TABLE_FILM_SAVE = GetIcon.getImage("table-film-save.png", 14, 14);
    public static final Image IMAGE_TABLE_DOWNLOAD_START = GetIcon.getImage("table-download-start.png", 14, 14);
    public static final Image IMAGE_TABLE_DOWNLOAD_DEL = GetIcon.getImage("table-download-del.png", 14, 14);
    public static final Image IMAGE_TABLE_DOWNLOAD_STOP = GetIcon.getImage("table-download-stop.png", 14, 14);
    public static final Image IMAGE_TABLE_DOWNLOAD_OPEN_DIR = GetIcon.getImage("table-download-open-dir.png", 14, 14);

    public final ImageView DOWNLOAD_OK = GetIcon.getImageView("download-ok.png", 16, 16);
    public final ImageView DOWNLOAD_ERROR = GetIcon.getImageView("download-error.png", 16, 16);

    public final ImageView ICON_BUTTON_STOP = GetIcon.getImageView("button-stop.png", 16, 16);
    public final ImageView ICON_BUTTON_NEXT = GetIcon.getImageView("button-next.png", 16, 16);
    public final ImageView ICON_BUTTON_PREV = GetIcon.getImageView("button-prev.png", 16, 16);
    public final ImageView ICON_BUTTON_REMOVE = GetIcon.getImageView("button-remove.png", 16, 16);
    public final ImageView ICON_BUTTON_ADD = GetIcon.getImageView("button-add.png", 16, 16);
    public final ImageView ICON_BUTTON_MOVE_DOWN = GetIcon.getImageView("button-move-down.png", 16, 16);
    public final ImageView ICON_BUTTON_MOVE_UP = GetIcon.getImageView("button-move-up.png", 16, 16);
    public final ImageView ICON_BUTTON_MOVE_TOP = GetIcon.getImageView("button-move-top.png", 16, 16);
    public final ImageView ICON_BUTTON_MOVE_BOTTOM = GetIcon.getImageView("button-move-bottom.png", 16, 16);

    public final ImageView FX_ICON_TOOLBAR_MENU = GetIcon.getImageView("toolbar-menu.png", 18, 15);
    public final ImageView FX_ICON_TOOLBAR_MENU_TOP = GetIcon.getImageView("toolbar-menu-top.png", 32, 18);

    public final ImageView FX_ICON_TOOLBAR_DOWNLOAD_CLEAN = GetIcon.getImageView("toolbar-download-clean.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_DOWNLOAD_START = GetIcon.getImageView("toolbar-download-starten.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_DOWNLOAD_DEL = GetIcon.getImageView("toolbar-download-del.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_DOWNLOAD_UNDO = GetIcon.getImageView("toolbar-download-undo.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_DOWNLOAD_FILM_START = GetIcon.getImageView("toolbar-download-film-start.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_DOWNLOAD_REFRESH = GetIcon.getImageView("toolbar-download-refresh.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_DOWNLOAD_START_ALL = GetIcon.getImageView("toolbar-download-start-all.png", 32, 32);

    public final ImageView FX_ICON_TOOLBAR_FILM_START = GetIcon.getImageView("toolbar-filme-start.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_FILM_REC = GetIcon.getImageView("toolbar-filme-rec.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_FILM_BOOKMARK = GetIcon.getImageView("toolbar-filme-bookmark.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_FILM_BOOKMARK_FILTER = GetIcon.getImageView("toolbar-filme-bookmark-filter.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_FILM_DEL_BOOKMARK = GetIcon.getImageView("toolbar-filme-del-bookmark.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_FILM_DEL_ALL_BOOKMARK = GetIcon.getImageView("toolbar-filme-del-all-bookmark.png", 32, 32);

    public final ImageView FX_ICON_TOOLBAR_ABO_NEW = GetIcon.getImageView("toolbar-abo-new.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_ABO_ON = GetIcon.getImageView("toolbar-abo-on.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_ABO_OFF = GetIcon.getImageView("toolbar-abo-off.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_ABO_DEL = GetIcon.getImageView("toolbar-abo-del.png", 32, 32);
    public final ImageView FX_ICON_TOOLBAR_ABO_CONFIG = GetIcon.getImageView("toolbar-abo-config.png", 32, 32);

    public final ImageView FX_ICON_FILTER_FILM_LOAD = GetIcon.getImageView("filter-film-load.png", 22, 22);
    public final ImageView FX_ICON_FILTER_FILM_SAVE = GetIcon.getImageView("filter-film-save.png", 22, 22);
    public final ImageView FX_ICON_FILTER_FILM_NEW = GetIcon.getImageView("filter-film-new.png", 22, 22);
}
