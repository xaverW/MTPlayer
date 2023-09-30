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
package de.p2tools.mtplayer;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Hyperlink;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MTStartErrorWindow extends Application {
    // toDo für weitere Startfehlermeldungen vorbereiten -> nach P2Tools

    private Stage primaryStage;
    private final String url = "https://www.p2tools.de/mtplayer/manual/start.html";

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initRootLayout();
    }

    private void initRootLayout() {
        try {
            StackPane root = new StackPane();
            Scene scene = new Scene(root, 600, 300);
            primaryStage.setTitle("Fehler");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(e -> {
                quitt();
            });

            VBox vBox = new VBox();
            vBox.setPadding(new Insets(20));
            vBox.setSpacing(20);

            Label txtHeader = new Label("Speicherwarnung");
            TextArea txtText = new TextArea("Das Programm hat nicht genügend Arbeitsspeicher zugewiesen bekommen.");
            txtText.setWrapText(true);
            txtText.setEditable(false);

            HBox hBoxUrl = new HBox(10);
            hBoxUrl.setAlignment(Pos.CENTER_LEFT);
            P2Hyperlink hyperlink = new P2Hyperlink(url,
                    ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
            hBoxUrl.getChildren().addAll(new Label("Infos:"), hyperlink);

            Button btnOk = new Button("_Ok");
            btnOk.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
            btnOk.setOnAction(a -> {
                quitt();
            });

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.BOTTOM_RIGHT);

            hBox.getChildren().add(btnOk);
            vBox.getChildren().addAll(txtHeader, txtText, hBoxUrl, hBox);
            root.getChildren().add(vBox);

            primaryStage.show();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void quitt() {
        Platform.exit();
        System.exit(3);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
