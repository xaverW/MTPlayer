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

package de.mtplayer.mtp.gui.mediaDialog;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.dialog.PDialog;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class MediaConfigController extends PDialog {

    private TabPane tabPane = new TabPane();
    private Button btnOk = new Button("Ok");
    Button btnHlp = new Button("");
    Button btnCreateMediaDB = new Button("Mediensammlung neu aufbauen");
    ProgressBar progress = new ProgressBar();

    private final ProgData progData;

    public MediaConfigController() {
        super(ProgConfig.MEDIA_CONFIG_DIALOG_SIZE.getStringProperty(), "Mediensammlung", true);

        this.progData = ProgData.getInstance();

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);

        vBox.getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        HBox hBoxHlp = new HBox();
        hBoxHlp.setSpacing(10);
        hBoxHlp.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(hBoxHlp, Priority.ALWAYS);
        hBoxHlp.getChildren().addAll(btnHlp, btnCreateMediaDB, progress);

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5));
        hBox.getChildren().addAll(hBoxHlp, btnOk);
        vBox.getChildren().add(hBox);

        init(vBox, true);
    }

    @Override
    public void make() {
        btnOk.setOnAction(a -> close());
        progress.visibleProperty().bind(progData.mediaList.propSearchProperty());
        btnCreateMediaDB.disableProperty().bind(progData.mediaList.propSearchProperty());
        btnCreateMediaDB.setOnAction(event -> progData.mediaList.createMediaDb());

        btnHlp.setText("");
        btnHlp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHlp.setOnAction(a -> new MTAlert().showHelpAlert("Medien", HelpText.MEDIA_DIALOG));

        initPanel();
    }

    private void initPanel() {
        try {

            AnchorPane mediaConfigPaneController = new MediaConfigPaneMediaController();
            Tab tab = new Tab("Einstellungen Mediensammlung");
            tab.setClosable(false);
            tab.setContent(mediaConfigPaneController);
            tabPane.getTabs().add(tab);


            AnchorPane mediaListPaneController = new MediaConfigPaneMediaListController();
            tab = new Tab("Mediensammlung");
            tab.setClosable(false);
            tab.setContent(mediaListPaneController);
            tabPane.getTabs().add(tab);

            AnchorPane historyListPaneController = new MediaConfigPaneHistoryController(true);
            tab = new Tab("gesehene Filme");
            tab.setClosable(false);
            tab.setContent(historyListPaneController);
            tabPane.getTabs().add(tab);

            AnchorPane aboListPaneController = new MediaConfigPaneHistoryController(false);
            tab = new Tab("erledigte Abos");
            tab.setClosable(false);
            tab.setContent(aboListPaneController);
            tabPane.getTabs().add(tab);

        } catch (final Exception ex) {
            PLog.errorLog(962104652, ex);
        }
    }

}
