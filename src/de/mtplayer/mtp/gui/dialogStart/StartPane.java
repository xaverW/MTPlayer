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

package de.mtplayer.mtp.gui.dialogStart;

import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class StartPane {


    public TitledPane makeStart1() {

        HBox hBox = new HBox();
        hBox.setSpacing(25);
        hBox.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Erster Programmstart", hBox);

        ImageView iv = new ImageView();
        Image im = getHelpScreen1();

        iv.setSmooth(true);
        iv.setCache(true);

        iv.setImage(im);

        hBox.getChildren().addAll(iv);
        Text text = new Text("1 -> Hier kann die Filmliste\n" +
                "aktualisiert werden." +
                "\n\n" +
                "2 -> Hier kann man die\n" +
                "Ansicht zwischen Filmen, Downloads\n" +
                "und angelegten Abos umschalten" +
                "\n\n" +
                "3 -> Hier befinden sich\n" +
                "die Programmeinstellungen." +
                "\n\n" +
                "4 -> Damit kann man Filme ansehen\n" +
                "und speichern.");
        hBox.getChildren().add(text);

        return tpConfig;
    }

    public TitledPane makeStart2() {

        HBox hBox = new HBox();
        hBox.setSpacing(25);
        hBox.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Erster Programmstart", hBox);

        ImageView iv = new ImageView();
        Image im = getHelpScreen2();

        iv.setSmooth(true);
        iv.setCache(true);

        iv.setImage(im);

        hBox.getChildren().addAll(iv);
        Text text = new Text("1 -> Damit kann ein (darunter)\n" +
                "ausgewähltes Filterprofil wieder\n" +
                "eingestellt werden." +
                "\n\n" +
                "2 -> Damit werden die oben eingestellten\n" +
                "Filter in dem darunter ausgewählten\n" +
                "Filterprofil gespeichert." +
                "\n\n" +
                "3 -> Damit können die eingestellten\n" +
                "Filter als neues Profil gespeichert werden." +
                "\n\n" +
                "4 -> Hier können die angezeigten\n" +
                "Filter ein- und ausgeblendet werden.");
        hBox.getChildren().add(text);

        return tpConfig;
    }

    private javafx.scene.image.Image getHelpScreen1() {
        final String path = "/de/mtplayer/mtp/res/prog-help-1.png";
        return new javafx.scene.image.Image(path, 600,
                600,
                true, true);
    }

    private javafx.scene.image.Image getHelpScreen2() {
        final String path = "/de/mtplayer/mtp/res/prog-help-2.png";
        return new javafx.scene.image.Image(path, 600,
                600,
                true, true);
    }

}
