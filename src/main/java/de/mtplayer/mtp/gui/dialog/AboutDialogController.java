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

import de.mtplayer.mLib.tools.Functions;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PHyperlink;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.nio.file.Path;

public class AboutDialogController extends PDialogExtra {

    private final ProgData progData;
    Button btnOk = new Button("_Ok");
    private final Color GRAY;


    public AboutDialogController(ProgData progData) {
        super(progData.primaryStage, null, "Über das Programm", true, false, DECO.SMALL);
        this.progData = progData;
        if (ProgConfig.SYSTEM_DARK_THEME.getBool()) {
            this.GRAY = Color.LAVENDER;
        } else {
            this.GRAY = Color.DARKBLUE;
        }
        addOkButton(btnOk);
        init(true);
    }


    public void make() {
//        make1();
//        make2();
        make3(true);
//        make3(false);
    }

    public void make1() {
        btnOk.setOnAction(a -> close());

        GridPane gridPane = new GridPane();
//        gridPane.setGridLinesVisible(true);
        gridPane.setHgap(50);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(0, 10, 0, 10));
        getvBoxCont().getChildren().add(gridPane);

        int row = 0;

        ImageView iv = new ImageView();
        Image im = getImage();
        iv.setSmooth(true);
        iv.setCache(true);
        iv.setImage(im);
        gridPane.add(iv, 0, row, 1, 3);

        // top
        Text text1 = new Text(ProgConst.PROGRAMNAME);
        text1.setFont(Font.font(null, FontWeight.BOLD, 40));
        gridPane.add(text1, 1, row, 2, 1);
        GridPane.setValignment(text1, VPos.TOP);

        Text text2 = new Text(P2LibConst.LINE_SEPARATOR + "Version: " + Functions.getProgVersion());
        text2.setFont(new Font(18));
        gridPane.add(text2, 1, ++row, 2, 1);

        Text text3 = new Text("[ Build: " + Functions.getBuild() + " vom " + Functions.getCompileDate() + " ]");
        text3.setFont(new Font(15));
        text3.setFill(GRAY);
        gridPane.add(text3, 1, ++row, 2, 1);
        GridPane.setValignment(text3, VPos.BOTTOM);


        //=======================
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        getvBoxCont().getChildren().add(gridPane);

        row = 0;
        int c = 0;

        Text text = new Text(P2LibConst.LINE_SEPARATORx2 + "Autor");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, c, ++row, 2, 1);

        text = new Text("Xaver W. (xaverW)");
        text.setFont(new Font(15));
        gridPane.add(text, c, ++row, 2, 1);


        // Pfade
        text = new Text(P2LibConst.LINE_SEPARATORx2 + "Programm Informationen");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, c, ++row, 2, 1);


        PHyperlink hyperlinkWeb = new PHyperlink(ProgConst.ADRESSE_WEBSITE,
                ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);

        PHyperlink hyperlinkHelp = new PHyperlink(ProgConst.ADRESSE_WEBSITE_HELP,
                ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);

        text = new Text("Website:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);
        gridPane.add(hyperlinkWeb, c + 1, row);

        text = new Text("Anleitung:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);
        gridPane.add(hyperlinkHelp, c + 1, row);

        text = new Text("Filmliste:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);

        text = new Text(ProgInfos.getFilmListFile());
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c + 1, row);

        text = new Text("Einstellungen:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);

        final Path xmlFilePath = new ProgInfos().getSettingsFile();
        text = new Text(xmlFilePath.toAbsolutePath().toString());
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c + 1, row);


        // Java
        text = new Text(P2LibConst.LINE_SEPARATORx2 + "Java Informationen");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, c, ++row, 2, 1);

        text = new Text("Version:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);

        text = new Text(System.getProperty("java.version"));
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c + 1, row);

        text = new Text("Type:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);

        String strVmType = System.getProperty("java.vm.name");
        strVmType += " (" + System.getProperty("java.vendor") + ")";
        text = new Text(strVmType);
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c + 1, row);

        text = new Text(P2LibConst.LINE_SEPARATORx2 + "Ein Dankeschön an alle," + P2LibConst.LINE_SEPARATOR +
                "die mit Vorschlägen oder Quelltext" + P2LibConst.LINE_SEPARATOR +
                "zu diesem Programm beigetragen haben.");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, c, ++row, 2, 1);
    }

    public void make2() {
        btnOk.setOnAction(a -> close());

        ImageView iv = new ImageView();
        Image im = getImage();
        iv.setSmooth(true);
        iv.setCache(true);
        iv.setImage(im);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(25);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(0, 10, 0, 10));
        getvBoxCont().getChildren().add(gridPane);

        int row = 0;

        gridPane.add(iv, 0, row, 1, 3);

        // top
        Text text1 = new Text(ProgConst.PROGRAMNAME);
        text1.setFont(Font.font(null, FontWeight.BOLD, 40));
        gridPane.add(text1, 1, row, 2, 1);
        GridPane.setValignment(text1, VPos.TOP);

        Text text2 = new Text(P2LibConst.LINE_SEPARATOR + "Version: " + Functions.getProgVersion());
        text2.setFont(new Font(18));
        gridPane.add(text2, 1, ++row, 2, 1);

        Text text3 = new Text("[ Build: " + Functions.getBuild() + " vom " + Functions.getCompileDate() + " ]");
        text3.setFont(new Font(15));
        text3.setFill(GRAY);
        gridPane.add(text3, 1, ++row, 2, 1);
        GridPane.setValignment(text3, VPos.BOTTOM);


        int c = 1;

        Text text = new Text(P2LibConst.LINE_SEPARATORx2 + "Autor");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, c, ++row, 2, 1);

        text = new Text("Xaver W. (xaverW)");
        text.setFont(new Font(15));
        gridPane.add(text, c, ++row, 2, 1);


        // Pfade
        text = new Text(P2LibConst.LINE_SEPARATORx2 + "Programm Informationen");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, c, ++row, 2, 1);


        PHyperlink hyperlinkWeb = new PHyperlink(ProgConst.ADRESSE_WEBSITE,
                ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);

        PHyperlink hyperlinkHelp = new PHyperlink(ProgConst.ADRESSE_WEBSITE_HELP,
                ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);

        text = new Text("Website:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);
        gridPane.add(hyperlinkWeb, c + 1, row);

        text = new Text("Anleitung:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);
        gridPane.add(hyperlinkHelp, c + 1, row);

        text = new Text("Filmliste:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);

        text = new Text(ProgInfos.getFilmListFile());
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c + 1, row);

        text = new Text("Einstellungen:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);

        final Path xmlFilePath = new ProgInfos().getSettingsFile();
        text = new Text(xmlFilePath.toAbsolutePath().toString());
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c + 1, row);


        // Java
        text = new Text(P2LibConst.LINE_SEPARATORx2 + "Java Informationen");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, c, ++row, 2, 1);

        text = new Text("Version:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);

        text = new Text(System.getProperty("java.version"));
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c + 1, row);

        text = new Text("Type:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);

        String strVmType = System.getProperty("java.vm.name");
        strVmType += " (" + System.getProperty("java.vendor") + ")";
        text = new Text(strVmType);
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c + 1, row);

        text = new Text(P2LibConst.LINE_SEPARATORx2 + "Ein Dankeschön an alle," + P2LibConst.LINE_SEPARATOR +
                "die mit Vorschlägen oder Quelltext" + P2LibConst.LINE_SEPARATOR +
                "zu diesem Programm beigetragen haben.");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, c, ++row, 2, 1);
    }

    public void make3(boolean a1) {
        btnOk.setOnAction(a -> close());

        GridPane gridPane = new GridPane();
//        gridPane.setGridLinesVisible(true);
        gridPane.setHgap(50);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(0, 10, 0, 10));
        HBox hBox = new HBox();
        hBox.getChildren().add(gridPane);
        hBox.setAlignment(Pos.CENTER);

        if (a1) {
            gridPane.getStyleClass().add("dialog-about");
            getvBoxCont().getChildren().add(hBox);
        } else {
            hBox.getStyleClass().add("dialog-about");
//        HBox.setHgrow(gridPane, Priority.ALWAYS);
            getvBoxCont().getChildren().add(hBox);
        }

        int row = 0;

        ImageView iv = new ImageView();
        Image im = getImage();
        iv.setSmooth(true);
        iv.setCache(true);
        iv.setImage(im);
        gridPane.add(iv, 0, row, 1, 3);

        // top
        Text text1 = new Text(ProgConst.PROGRAMNAME);
        text1.setFont(Font.font(null, FontWeight.BOLD, 40));
        gridPane.add(text1, 1, row, 2, 1);
        GridPane.setValignment(text1, VPos.TOP);

        Text text2 = new Text(P2LibConst.LINE_SEPARATOR + "Version: " + Functions.getProgVersion());
        text2.setFont(new Font(18));
        gridPane.add(text2, 1, ++row, 2, 1);

        Text text3 = new Text("[ Build: " + Functions.getBuild() + " vom " + Functions.getCompileDate() + " ]");
        text3.setFont(new Font(15));
        text3.setFill(GRAY);
        gridPane.add(text3, 1, ++row, 2, 1);
        GridPane.setValignment(text3, VPos.BOTTOM);


        //=======================
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        getvBoxCont().getChildren().add(gridPane);

        row = 0;
        int c = 0;

        Text text = new Text(P2LibConst.LINE_SEPARATORx2 + "Autor");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, c, ++row, 2, 1);

        text = new Text("Xaver W. (xaverW)");
        text.setFont(new Font(15));
        gridPane.add(text, c, ++row, 2, 1);


        // Pfade
        text = new Text(P2LibConst.LINE_SEPARATORx2 + "Programm Informationen");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, c, ++row, 2, 1);


        PHyperlink hyperlinkWeb = new PHyperlink(ProgConst.ADRESSE_WEBSITE,
                ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);

        PHyperlink hyperlinkHelp = new PHyperlink(ProgConst.ADRESSE_WEBSITE_HELP,
                ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);

        text = new Text("Website:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);
        gridPane.add(hyperlinkWeb, c + 1, row);

        text = new Text("Anleitung:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);
        gridPane.add(hyperlinkHelp, c + 1, row);

        text = new Text("Filmliste:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);

        text = new Text(ProgInfos.getFilmListFile());
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c + 1, row);

        text = new Text("Einstellungen:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);

        final Path xmlFilePath = new ProgInfos().getSettingsFile();
        text = new Text(xmlFilePath.toAbsolutePath().toString());
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c + 1, row);


        // Java
        text = new Text(P2LibConst.LINE_SEPARATORx2 + "Java Informationen");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, c, ++row, 2, 1);

        text = new Text("Version:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);

        text = new Text(System.getProperty("java.version"));
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c + 1, row);

        text = new Text("Type:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c, ++row);

        String strVmType = System.getProperty("java.vm.name");
        strVmType += " (" + System.getProperty("java.vendor") + ")";
        text = new Text(strVmType);
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, c + 1, row);

        text = new Text(P2LibConst.LINE_SEPARATORx2 + "Ein Dankeschön an alle," + P2LibConst.LINE_SEPARATOR +
                "die mit Vorschlägen oder Quelltext" + P2LibConst.LINE_SEPARATOR +
                "zu diesem Programm beigetragen haben.");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, c, ++row, 2, 1);
    }

    private javafx.scene.image.Image getImage() {
        final String path = "/de/mtplayer/mtp/res/P2.png";
        return new Image(path, 128, 128, false, true);
    }

}
