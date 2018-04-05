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

package de.mtplayer.mtp.gui.dialog;

import de.mtplayer.mLib.tools.MLConfigs;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.gui.tools.GuiSize;
import de.p2tools.p2Lib.tools.Log;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;


public class MTDialog {
    private Scene scene = null;
    private Stage stage = null;

    private final MLConfigs conf;
    private final String fxml;
    private final boolean modal;
    private final String title;

    private double stageWidth = 0;
    private double stageHeight = 0;

    public MTDialog(String fxml, MLConfigs conf, String title, boolean modal) {
        this.fxml = fxml;
        this.conf = conf;
        this.modal = modal;
        this.title = title;
    }

    public MTDialog(MLConfigs conf, String title, boolean modal) {
        this.fxml = null;
        this.conf = conf;
        this.modal = modal;
        this.title = title;
    }

    public MTDialog(String fxml, String title, boolean modal) {
        this.fxml = fxml;
        this.conf = null;
        this.modal = modal;
        this.title = title;
    }

    public MTDialog(String title, boolean modal) {
        this.fxml = null;
        this.conf = null;
        this.modal = modal;
        this.title = title;
    }

    public void init() {
        // die Dialoge werden beim Programmstart angelegt
        Platform.runLater(() -> {
            initDialog();
        });
    }

    public void init(Pane pane) {
        // die Dialoge werden beim Programmstart angelegt
        Platform.runLater(() -> {
            init(pane, false);
        });
    }

    public void init(boolean show) {
        initDialog();
        if (show) {
            showDialog();
        }
    }

    public void init(Pane pane, boolean show) {
        setSize(pane);
        String css = this.getClass().getResource(Const.CSS_FILE).toExternalForm();
        scene.getStylesheets().add(css);

        initDialog();
        if (show) {
            showDialog();
        }
    }

    private void initDialog() {
        try {
            if (scene == null) {
                final URL fxmlUrl = getClass().getResource(fxml);
                final FXMLLoader fXMLLoader = new FXMLLoader(fxmlUrl);
                fXMLLoader.setController(this);
                final Parent root = fXMLLoader.load();

                setSize(root);
            }

            stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);

            if (modal) {
                stage.initModality(Modality.APPLICATION_MODAL);
            }
            stage.setOnCloseRequest(e -> {
                e.consume();
                close();
            });
            scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    if (escEvent(keyEvent)) {
                        close();
                    }
                }
            });

            make();

            if (conf == null) {
                scene.getWindow().sizeToScene();
            }

        } catch (final Exception exc) {
            Log.errorLog(152030145, exc);
        }
    }

    private void setSize(Parent parent) {
        if (conf == null) {
            this.scene = new Scene(parent);
        } else {
            int w = GuiSize.getWidth(conf);
            int h = GuiSize.getHeight(conf);
            if (w > 0 && h > 0) {
                this.scene = new Scene(parent, GuiSize.getWidth(conf), GuiSize.getHeight(conf));
            } else {
                this.scene = new Scene(parent);
            }
        }
    }

    public boolean escEvent(KeyEvent keyEvent) {
        return true;
    }

    public void hide() {
        // close/hide are the same
        close();
    }

    public void close() {
        //bei wiederkehrenden Dialogen: die pos/size merken
        stageWidth = stage.getWidth();
        stageHeight = stage.getHeight();

        if (conf != null) {
            GuiSize.getSizeScene(conf, stage);
        }
        stage.close();
    }

    public void showDialog() {
        if (stageHeight > 0 && stageWidth > 0) {
            //bei wiederkehrenden Dialogen die pos/size setzen
            stage.setHeight(stageHeight);
            stage.setWidth(stageWidth);
        }

        if (conf != null) {
            GuiSize.setPos(conf, stage);
        } else {
            Stage parentStage = Daten.getInstance().primaryStage;
            ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
                double stageWidth = newValue.doubleValue();
                stage.setX(parentStage.getX() + parentStage.getWidth() / 2 - stageWidth / 2);
            };
            ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
                double stageHeight = newValue.doubleValue();
                stage.setY(parentStage.getY() + parentStage.getHeight() / 2 - stageHeight / 2);
            };

            stage.widthProperty().addListener(widthListener);
            stage.heightProperty().addListener(heightListener);

            stage.setOnShown(e -> {
                stage.widthProperty().removeListener(widthListener);
                stage.heightProperty().removeListener(heightListener);
            });

        }

        if (modal) {
            stage.showAndWait();
        } else {
            stage.show();
        }
    }

    Stage getStage() {
        return stage;
    }

    public boolean isShowing() {
        return stage.isShowing();
    }

    public void make() {
    }

}
