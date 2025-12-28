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

package de.p2tools.mtplayer.gui.startdialog;

import de.p2tools.p2lib.P2LibConst;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StartPane extends VBox {

    final int picSize = 450;

    public StartPane() {
    }

    public void close() {
    }

    public void makeStart1() {
        HBox hBox = new HBox();
        hBox.setSpacing(25);
        hBox.setPadding(new Insets(20, 10, 0, 10));

        ImageView iv = new ImageView();
        Image im = getHelpScreen1();
        iv.setSmooth(true);
        iv.setImage(im);

        Label text = new Label("1) Hier wird die Filmliste" + P2LibConst.LINE_SEPARATOR +
                "aktualisiert." +

                P2LibConst.LINE_SEPARATORx2 +
                "2) Die Ansicht der Filme, Audios, Live-Suche," + P2LibConst.LINE_SEPARATOR +
                "Downloads oder Abos wird hier umgeschaltet." +

                P2LibConst.LINE_SEPARATORx2 +
                "3) Hier befinden sich" + P2LibConst.LINE_SEPARATOR +
                "die Programmeinstellungen." +

                P2LibConst.LINE_SEPARATORx2 +
                "4) Damit kann man Filme ansehen" + P2LibConst.LINE_SEPARATOR +
                "und speichern." +

                P2LibConst.LINE_SEPARATORx2 +
                "5) Hier lassen sich einzelne Filme vormerken" + P2LibConst.LINE_SEPARATOR +
                "und die Vormerkungen wieder löschen." + P2LibConst.LINE_SEPARATOR +
                "Und zuletzt öffnet sich hier ein Dialog" + P2LibConst.LINE_SEPARATOR +
                "mit den markierten Filmen.");

        hBox.getChildren().addAll(iv, text);
        getChildren().addAll(StartFactory.getTitle("Infos zur Programmoberfläche"), hBox);
    }


    public void makeStart2() {
        HBox hBox = new HBox();
        hBox.setSpacing(25);
        hBox.setPadding(new Insets(20, 10, 0, 10));

        ImageView iv = new ImageView();
        Image im = getHelpScreen2();
        iv.setSmooth(true);
        iv.setImage(im);

        Label text = new Label(
                "1) In dem Bereich sind die" + P2LibConst.LINE_SEPARATOR +
                        "Filter angeordnet." +
                        P2LibConst.LINE_SEPARATORx2 +

                        "2) Hier können die oben angezeigten" + P2LibConst.LINE_SEPARATOR +
                        "Filter ein- und ausgeblendet werden." +
                        P2LibConst.LINE_SEPARATORx2 +

                        "3) Damit können die oben eingestellten" + P2LibConst.LINE_SEPARATOR +
                        "Filter in einem Filterprofil gespeichert " + P2LibConst.LINE_SEPARATOR +
                        "und auch wieder abgerufen werden." +

                        P2LibConst.LINE_SEPARATORx2 +
                        "5) Hier kann eine vereinfachte" + P2LibConst.LINE_SEPARATOR +
                        "Suche ausgewählt werden.");

        hBox.getChildren().addAll(iv, text);
        getChildren().addAll(StartFactory.getTitle("Infos zum Filmfilter"), hBox);
    }

    private javafx.scene.image.Image getHelpScreen1() {
        final String path = "/de/p2tools/mtplayer/res/startdialog/mtplayer-startpage-1.png";
        return new javafx.scene.image.Image(path, picSize,
                picSize,
                true, true);
    }

    private javafx.scene.image.Image getHelpScreen2() {
        final String path = "/de/p2tools/mtplayer/res/startdialog/mtplayer-startpage-2.png";
        return new javafx.scene.image.Image(path, picSize,
                picSize,
                true, true);
    }
}
