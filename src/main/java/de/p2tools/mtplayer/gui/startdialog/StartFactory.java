package de.p2tools.mtplayer.gui.startdialog;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class StartFactory {
    private StartFactory() {
    }

    public static HBox getTitle(String title) {
        HBox hBox = new HBox(10);
        hBox.getStyleClass().add("startInfo_1");

        Label t = new Label(title);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(t);
        return hBox;
    }
}
