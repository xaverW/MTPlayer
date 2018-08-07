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

package de.mtplayer.mtp.gui.startDialog;

import de.p2tools.p2Lib.PConst;
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
        Text text = new Text("1 -> Hier kann die Filmliste" + PConst.LINE_SEPARATOR +
                "aktualisiert werden." +
                PConst.LINE_SEPARATORx2 +
                "2 -> Hier kann man die" + PConst.LINE_SEPARATOR +
                "Ansicht zwischen Filmen, Downloads" + PConst.LINE_SEPARATOR +
                "und angelegten Abos umschalten" +
                PConst.LINE_SEPARATORx2 +
                "3 -> Hier befinden sich" + PConst.LINE_SEPARATOR +
                "die Programmeinstellungen." +
                PConst.LINE_SEPARATORx2 +
                "4 -> Damit kann man Filme ansehen" + PConst.LINE_SEPARATOR +
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
        Text text = new Text("1 -> Damit kann ein (darunter)" + PConst.LINE_SEPARATOR +
                "ausgewähltes Filterprofil wieder" + PConst.LINE_SEPARATOR +
                "eingestellt werden." +
                PConst.LINE_SEPARATORx2 +
                "2 -> Damit werden die oben eingestellten" + PConst.LINE_SEPARATOR +
                "Filter in dem darunter ausgewählten" + PConst.LINE_SEPARATOR +
                "Filterprofil gespeichert." +
                PConst.LINE_SEPARATORx2 +
                "3 -> Damit können die eingestellten" + PConst.LINE_SEPARATOR +
                "Filter als neues Profil gespeichert werden." +
                PConst.LINE_SEPARATORx2 +
                "4 -> Hier können die angezeigten" + PConst.LINE_SEPARATOR +
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
