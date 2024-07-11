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

import de.p2tools.mtplayer.MTPlayerController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.icons.P2Icon;
import de.p2tools.p2lib.tools.log.P2Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProgIcons {
    public static String ICON_PATH = "res/program/";
    public static String ICON_PATH_LONG = "de/p2tools/mtplayer/res/program/";

    private static final List<PIcon> iconList = new ArrayList<>();

    public static PIcon ICON_DIALOG_ON = new PIcon(ICON_PATH_LONG, ICON_PATH, "dialog-ein.png", "dialog-ein-sw.png", 16, 16);
    public static PIcon IMAGE_ACHTUNG_64 = new PIcon(ICON_PATH_LONG, ICON_PATH, "achtung_64.png", 64, 64);

    public static PIcon ICON_BUTTON_SEARCH_16 = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-search_16.png", 16, 16);
    public static PIcon ICON_BUTTON_CHANGE = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-change.png", 16, 16);
    public static PIcon ICON_BUTTON_CLEAN = new PIcon(ICON_PATH_LONG, ICON_PATH, "clean_16.png", 16, 16);
    public static PIcon ICON_BUTTON_RESET = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-reset.png", 16, 16);
    public static PIcon ICON_BUTTON_UPDATE = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-update.png", 16, 16);
    public static PIcon ICON_BUTTON_PROPOSE = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-propose.png", 16, 16);
    public static PIcon ICON_BUTTON_EDIT = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-edit.png", 16, 16);
    public static PIcon ICON_BUTTON_EDIT_FILTER = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-edit-filter.png", 16, 16);
    public static PIcon ICON_BUTTON_BACKWARD = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-backward.png", 16, 16);
    public static PIcon ICON_BUTTON_FORWARD = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-forward.png", 16, 16);
    public static PIcon ICON_BUTTON_EDIT_ABO_PATH = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-edit-filter.png", 16, 16);
    public static PIcon ICON_BUTTON_MENU = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-menu.png", 18, 15);
    public static PIcon ICON_BUTTON_QUIT = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-quit.png", 16, 16);
    public static PIcon ICON_BUTTON_FILE_OPEN = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-file-open.png", 16, 16);
    public static PIcon ICON_BUTTON_PLAY = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-play.png", 16, 16);
    public static PIcon ICON_BUTTON_CLEAR = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-clear.png", 16, 16);
    public static PIcon ICON_BUTTON_SEARCH = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-search.png", 16, 16);
    public static PIcon ICON_BUTTON_UP_DOWN = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-up-down.png", 8, 26);
    public static PIcon ICON_BUTTON_UP_DOWN_H = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-up-down-h.png", 26, 6);
    public static PIcon ICON_DIALOG_QUIT = new PIcon(ICON_PATH_LONG, ICON_PATH, "dialog-quit.png", 64, 64);
    public static PIcon IMAGE_TABLE_ABO_ON = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-abo-on.png", 14, 14);
    public static PIcon IMAGE_TABLE_ABO_OFF = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-abo-off.png", 14, 14);
    public static PIcon IMAGE_TABLE_ABO_DEL = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-abo-del.png", 14, 14);
    public static PIcon IMAGE_TABLE_FILM_PLAY = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-film-play.png", 14, 14);
    public static PIcon IMAGE_TABLE_FILM_SAVE = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-film-save.png", 14, 14);
    public static PIcon IMAGE_TABLE_FILM_BOOKMARK = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-film-bookmark.png", 14, 14);
    public static PIcon IMAGE_TABLE_FILM_DEL_BOOKMARK = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-film-del-bookmark.png", 14, 14);
    public static PIcon IMAGE_TABLE_DOWNLOAD_START = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-download-start.png", 14, 14);
    public static PIcon IMAGE_TABLE_DOWNLOAD_DEL = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-download-del.png", 14, 14);
    public static PIcon IMAGE_TABLE_DOWNLOAD_STOP = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-download-stop.png", 14, 14);
    public static PIcon IMAGE_TABLE_DOWNLOAD_OPEN_DIR = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-download-open-dir.png", 14, 14);

    public static PIcon ICON_BUTTON_WORKER_STOP = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-worker-stop.png", 12, 12);
    public static PIcon ICON_BUTTON_STOP = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-stop.png", 16, 16);
    public static PIcon ICON_BUTTON_NEXT = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-next.png", 16, 16);
    public static PIcon ICON_BUTTON_PREV = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-prev.png", 16, 16);
    public static PIcon ICON_BUTTON_REMOVE = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-remove.png", 16, 16);
    public static PIcon ICON_BUTTON_SEPARATOR = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-separator.png", 22, 16);
    public static PIcon ICON_BUTTON_ADD = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-add.png", 16, 16);
    public static PIcon ICON_BUTTON_MOVE_DOWN = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-move-down.png", 16, 16);
    public static PIcon ICON_BUTTON_MOVE_UP = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-move-up.png", 16, 16);
    public static PIcon ICON_BUTTON_MOVE_TOP = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-move-top.png", 16, 16);
    public static PIcon ICON_BUTTON_MOVE_BOTTOM = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-move-bottom.png", 16, 16);
    public static PIcon ICON_BUTTON_FILMFILTER_DEL = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-filmfilter-del.png", 16, 16);
    public static PIcon ICON_BUTTON_DEL_SW = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-del-sw.png", 12, 12);

    public static PIcon ICON_TOOLBAR_BUTTON_SEARCH = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-button-search.png", 24, 24);
    public static PIcon ICON_TOOLBAR_MENU = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-menu.png", 18, 15);
    public static PIcon ICON_TOOLBAR_MENU_TOP = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-menu-top.png", 32, 18);

    public static PIcon ICON_TOOLBAR_DOWNLOAD_CLEAN = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-download-clean.png", 26, 26);
    public static PIcon ICON_TOOLBAR_DOWNLOAD_START = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-download-starten.png", 26, 26);
    public static PIcon ICON_TOOLBAR_DOWNLOAD_DEL = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-download-del.png", 26, 26);
    public static PIcon ICON_TOOLBAR_DOWNLOAD_UNDO = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-download-undo.png", 26, 26);
    public static PIcon ICON_TOOLBAR_DOWNLOAD_FILM_START = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-download-film-start.png", 26, 26);
    public static PIcon ICON_TOOLBAR_DOWNLOAD_REFRESH = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-download-refresh.png", 26, 26);
    public static PIcon ICON_TOOLBAR_DOWNLOAD_START_ALL = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-download-start-all.png", 26, 26);
    public static PIcon ICON_TOOLBAR_DOWNLOAD_START_ALL_TIME = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-download-start-time.png", 26, 26);

    public static PIcon ICON_TOOLBAR_FILM_START = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-filme-start.png", 26, 26);
    public static PIcon ICON_TOOLBAR_FILM_ALL_START = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-filme-all-start.png", 26, 26);
    public static PIcon ICON_TOOLBAR_FILM_REC = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-filme-rec.png", 26, 26);
    public static PIcon ICON_TOOLBAR_FILM_BOOKMARK = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-filme-bookmark.png", 26, 26);
    public static PIcon ICON_TOOLBAR_FILM_BOOKMARK_FILTER = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-filme-bookmark-filter.png", 26, 26);
    public static PIcon ICON_TOOLBAR_FILM_DEL_BOOKMARK = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-filme-del-bookmark.png", 26, 26);
    public static PIcon ICON_TOOLBAR_FILM_DEL_ALL_BOOKMARK = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-filme-del-all-bookmark.png", 26, 26);
    public static PIcon ICON_TOOLBAR_PROPOSE = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-propose.png", 26, 26);
    public static PIcon ICON_TOOLBAR_LIVE = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-live.png", 26, 26);

    public static PIcon ICON_TOOLBAR_ABO_NEW = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-abo-new.png", 26, 26);
    public static PIcon ICON_TOOLBAR_ABO_ON = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-abo-on.png", 26, 26);
    public static PIcon ICON_TOOLBAR_ABO_OFF = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-abo-off.png", 26, 26);
    public static PIcon ICON_TOOLBAR_ABO_DEL = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-abo-del.png", 26, 26);
    public static PIcon ICON_TOOLBAR_ABO_CONFIG = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-abo-config.png", 26, 26);

    public static PIcon ICON_FILTER_FILM_LOAD = new PIcon(ICON_PATH_LONG, ICON_PATH, "filter-film-load.png", 22, 22);
    public static PIcon ICON_FILTER_FILM_SAVE = new PIcon(ICON_PATH_LONG, ICON_PATH, "filter-film-save.png", 22, 22);
    public static PIcon ICON_FILTER_FILM_NEW = new PIcon(ICON_PATH_LONG, ICON_PATH, "filter-film-new.png", 22, 22);

    public static void initIcons() {
        iconList.forEach(p -> {
            String url = p.genUrl(PIcon.class, MTPlayerController.class, ProgConst.class, ProgIcons.class, P2LibConst.class);
            if (url.isEmpty()) {
                // dann wurde keine gefunden
                P2Log.errorLog(915245458, "ProgIconsInfo: keine URL, icon: " + p.getPathFileNameDark() + " - " + p.getFileName());
            }
        });
    }

    public static class PIcon extends P2Icon {
        public PIcon(String longPath, String path, String fileName, int w, int h) {
            super(longPath, path, fileName, w, h);
            iconList.add(this);
        }

        public PIcon(String longPath, String path, String fileName, String fileNameDark, int w, int h) {
            super(longPath, path, fileName, fileNameDark, w, h);
            iconList.add(this);
        }

        public boolean searchUrl(String p, Class<?>... clazzAr) {
            URL url;
            url = MTPlayerController.class.getResource(p);
            if (set(url, p, "MTPlayerController.class.getResource")) return true;
            url = ProgConst.class.getResource(p);
            if (set(url, p, "ProgConst.class.getResource")) return true;
            url = ProgIcons.class.getResource(p);
            if (set(url, p, "ProgIcons.class.getResource")) return true;
            url = this.getClass().getResource(p);
            if (set(url, p, "this.getClass().getResource")) return true;

            url = ClassLoader.getSystemResource(p);
            if (set(url, p, "ClassLoader.getSystemResource")) return true;
            url = P2LibConst.class.getClassLoader().getResource(p);
            if (set(url, p, "P2LibConst.class.getClassLoader().getResource")) return true;
            url = ProgConst.class.getClassLoader().getResource(p);
            if (set(url, p, "ProgConst.class.getClassLoader().getResource")) return true;
            url = this.getClass().getClassLoader().getResource(p);
            if (set(url, p, "this.getClass().getClassLoader().getResource")) return true;

            return false;
        }
    }
}