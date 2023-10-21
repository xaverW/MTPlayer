/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.gui.mediaSearch;

import de.p2tools.mtplayer.controller.config.ProgConst;
import javafx.beans.property.*;

public class MediaDataDto {

    public enum SHOW_WHAT {
        SHOW_MEDIA, SHOW_ABO, SHOW_HISTORY
    }

    public SHOW_WHAT whatToShow;
    public String searchTheme = "";
    public String searchTitle = "";
    public StringProperty searchStringProp = new SimpleStringProperty();

    //aus was der Suchbegriff gebaut wird: T/Th/TT
    public IntegerProperty buildSearchFrom = new SimpleIntegerProperty(ProgConst.MEDIA_SEARCH_TITEL_OR_NAME);
    //wo gesucht wird: T/Th/TT
    public IntegerProperty searchInWhat = new SimpleIntegerProperty(ProgConst.MEDIA_SEARCH_TITEL_OR_NAME);

    // Suchbegriff EXAKT verwenden
    public BooleanProperty cleaningExact = new SimpleBooleanProperty(Boolean.FALSE);
    // Putzen
    public BooleanProperty cleaning = new SimpleBooleanProperty(Boolean.TRUE);
    // Cleaning Liste anwenden
    public BooleanProperty cleaningList = new SimpleBooleanProperty(Boolean.TRUE);
    // Verknüpfen mit UND sonst OR
    public BooleanProperty cleaningAndOr = new SimpleBooleanProperty(Boolean.FALSE); // Verknüpfen mit UND sonst OR


    public MediaDataDto() {
    }
}
