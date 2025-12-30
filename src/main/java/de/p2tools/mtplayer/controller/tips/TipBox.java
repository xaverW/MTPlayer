/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.tips;

import de.p2tools.p2lib.guitools.P2Hyperlink;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TipBox extends VBox {

    private TipData tip = null;
    private final ImageView imageView = new ImageView();
    private final TextArea taText = new TextArea();
    private final HBox hBoxHyper = new HBox();
    private int imageSize = 450;
    private VBox vBoxButton = new VBox();

    public TipBox() {
        initTop();
    }

    public void setTips(TipData tip) {
        this.tip = tip;
        setTipOfDay();
    }

    private void initTop() {
        imageView.setSmooth(true);
        taText.setWrapText(true);
        taText.setEditable(false);

        VBox vBoxL = new VBox(0);
        vBoxL.setPadding(new Insets(20, 0, 0, 5));
        vBoxL.getChildren().add(imageView);

        VBox vBoxR = new VBox(0);
        vBoxR.setPadding(new Insets(20, 5, 0, 0));
        VBox.setVgrow(taText, Priority.ALWAYS);
        hBoxHyper.setPadding(new Insets(0));
        vBoxR.getChildren().addAll(taText, hBoxHyper);

        HBox hBox = new HBox(20);
        HBox.setHgrow(vBoxR, Priority.ALWAYS);
        hBox.getChildren().addAll(vBoxL, vBoxR);

        VBox.setVgrow(hBox, Priority.ALWAYS);
        getChildren().add(hBox);
    }

    private void setTipOfDay() {
        Image im;
        if (tip.getImage() != null) {
            if (imageSize > 0) {
                im = new Image(tip.getImage(), imageSize, imageSize, true, true);
            } else {
                im = new Image(tip.getImage(), 400, 400, true, true);
            }
            imageView.setImage(im);
        } else {
            imageView.setImage(null);
        }
        taText.setText(tip.getText());

        hBoxHyper.getChildren().clear();
        if (tip.getHyperlinkWeb() != null) {
            P2Hyperlink hyperlinkWeb;
            if (tip.openUrlWithProgProperty() != null) {
                hyperlinkWeb = new P2Hyperlink(tip.getHyperlinkWeb(),
                        tip.openUrlWithProgProperty());
            } else {
                hyperlinkWeb = new P2Hyperlink(tip.getHyperlinkWeb());
            }

            hBoxHyper.getChildren().add(hyperlinkWeb);
        }
    }
}
