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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextFlow;

public class FilmGuiInfoController {
    final private TextFlow textFlow;
    final private AnchorPane anchorPane;

    public FilmGuiInfoController(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
        this.textFlow = new TextFlow();

        anchorPane.getChildren().add(textFlow);

        AnchorPane.setLeftAnchor(textFlow, 10.0);
        AnchorPane.setBottomAnchor(textFlow, 10.0);
        AnchorPane.setRightAnchor(textFlow, 10.0);
        AnchorPane.setTopAnchor(textFlow, 10.0);

    }

    public void setFilm(Film film) {
        FilmTools.getInfoText(film, textFlow.getChildren());
    }


}

