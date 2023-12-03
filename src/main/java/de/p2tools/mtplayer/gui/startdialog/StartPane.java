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
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class StartPane {

    public StartPane() {
    }

    public void close() {
    }

    public TitledPane makeStart1() {
        HBox hBox = new HBox();
        hBox.setSpacing(25);
        hBox.setPadding(new Insets(20));

        ImageView iv = new ImageView();
        Image im = getHelpScreen1();
        iv.setSmooth(true);
        iv.setImage(im);

        hBox.getChildren().addAll(iv);
        Label text = new Label("1) Hier wird die Filmliste" + P2LibConst.LINE_SEPARATOR +
                "aktualisiert." +

                P2LibConst.LINE_SEPARATORx2 +
                "2) Die Ansicht der Filme, Downloads" + P2LibConst.LINE_SEPARATOR +
                "oder Abos wird hier umgeschaltet." +

                P2LibConst.LINE_SEPARATORx2 +
                "3) Hier befinden sich" + P2LibConst.LINE_SEPARATOR +
                "die Programmeinstellungen." +

                P2LibConst.LINE_SEPARATORx2 +
                "4) Mit dem Pluszeichen können" + P2LibConst.LINE_SEPARATOR +
                "Spalten in der Tabelle" + P2LibConst.LINE_SEPARATOR +
                "ein- und ausgeblendet werden." +

                P2LibConst.LINE_SEPARATORx2 +
                "5) Damit kann man Filme ansehen" + P2LibConst.LINE_SEPARATOR +
                "und speichern." +

                P2LibConst.LINE_SEPARATORx2 +
                "6) Hier lassen sich einzelne Filme" + P2LibConst.LINE_SEPARATOR +
                "vormerken, die Vormerkungen" + P2LibConst.LINE_SEPARATOR +
                "wieder löschen und zuletzt werden" + P2LibConst.LINE_SEPARATOR +
                "alle vorgemerkte Filme angezeigt.");

        hBox.getChildren().add(text);

        TitledPane tpConfig = new TitledPane("Infos zur Programmoberfläche", hBox);
        return tpConfig;
    }

    public TitledPane makeStart2() {
        HBox hBox = new HBox();
        hBox.setSpacing(25);
        hBox.setPadding(new Insets(20));

        ImageView iv = new ImageView();
        Image im = getHelpScreen2();
        iv.setSmooth(true);
        iv.setImage(im);

        hBox.getChildren().addAll(iv);

        Label text = new Label(
                "1) In dem Bereich sind die" + P2LibConst.LINE_SEPARATOR +
                        "Filter angeordnet." +
                        P2LibConst.LINE_SEPARATORx2 +

                        "2) Hier können die oben angezeigten" + P2LibConst.LINE_SEPARATOR +
                        "Filter ein- und ausgeblendet werden." +
                        P2LibConst.LINE_SEPARATORx2 +

                        "3) Damit werden die oben eingestellten" + P2LibConst.LINE_SEPARATOR +
                        "Filter in dem darunter ausgewählten" + P2LibConst.LINE_SEPARATOR +
                        "Filterprofil gespeichert oder wieder" + P2LibConst.LINE_SEPARATOR +
                        "hergestellt (auch ein neues Profil" + P2LibConst.LINE_SEPARATOR +
                        "kann damit angelegt werden)." +

                        P2LibConst.LINE_SEPARATORx2 +
                        "4) Hier können die gespeicherten" + P2LibConst.LINE_SEPARATOR +
                        "Filterprofile verwaltet werden." +

                        P2LibConst.LINE_SEPARATORx2 +
                        "5) Damit kann eine vereinfachte" + P2LibConst.LINE_SEPARATOR +
                        "Suche ausgewählt werden.");

        hBox.getChildren().add(text);

        TitledPane tpConfig = new TitledPane("Infos zum Filmfilter", hBox);
        return tpConfig;
    }

    private javafx.scene.image.Image getHelpScreen1() {
        final String path = "/de/p2tools/mtplayer/res/mtplayer-startpage-1.png";
        return new javafx.scene.image.Image(path, 600,
                600,
                true, true);
    }

    private javafx.scene.image.Image getHelpScreen2() {
        final String path = "/de/p2tools/mtplayer/res/mtplayer-startpage-2.png";
        return new javafx.scene.image.Image(path, 600,
                600,
                true, true);
    }
}
