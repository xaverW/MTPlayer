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
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PHyperlink;
import javafx.geometry.Insets;
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
    Button btnOk = new Button("Ok");
    private final Color GRAY = Color.DARKSLATEGRAY;


    public AboutDialogController(ProgData progData) {
        super(null, "Über das Programm", true);
        this.progData = progData;

        addOkButtons(btnOk);
        init(getVBoxCompleteDialog(), true);
    }


    @Override
    public void make() {
        btnOk.setOnAction(a -> close());
        HBox hBox = new HBox();

        getVboxCont().getChildren().add(hBox);

        ImageView iv = new ImageView();
        Image im = getImage();
        iv.setSmooth(true);
        iv.setCache(true);
        iv.setImage(im);
        hBox.getChildren().add(iv);

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        int row = 0;

        // top
        Text text = new Text(ProgConst.PROGRAMNAME);
        text.setFont(Font.font(null, FontWeight.BOLD, 40));
        gridPane.add(text, 0, row, 2, 1);


        text = new Text(PConst.LINE_SEPARATOR + "Version: " + Functions.getProgVersion());
        text.setFont(new Font(18));
        gridPane.add(text, 0, ++row, 2, 1);

        text = new Text("[ Build: " + Functions.getBuild() + " vom " + Functions.getCompileDate() + " ]");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, 0, ++row, 2, 1);


        text = new Text(PConst.LINE_SEPARATORx2 + "Autor");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, 0, ++row, 2, 1);

        text = new Text("Xaver W. (xaverW)");
        text.setFont(new Font(15));
        gridPane.add(text, 0, ++row, 2, 1);


        // Pfade
        text = new Text(PConst.LINE_SEPARATORx2 + "Programm Informationen");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, 0, ++row, 2, 1);


        PHyperlink hyperlinkWeb = new PHyperlink(ProgConst.ADRESSE_WEBSITE,
                ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);

        PHyperlink hyperlinkHelp = new PHyperlink(ProgConst.ADRESSE_WEBSITE_HELP,
                ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);

        text = new Text("Website:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, 0, ++row);
        gridPane.add(hyperlinkWeb, 1, row);

        text = new Text("Anleitung:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, 0, ++row);
        gridPane.add(hyperlinkHelp, 1, row);

        text = new Text("Filmliste:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, 0, ++row);

        text = new Text(ProgInfos.getFilmListFile());
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, 1, row);

        text = new Text("Einstellungen:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, 0, ++row);

        final Path xmlFilePath = new ProgInfos().getSettingsFile();
        text = new Text(xmlFilePath.toAbsolutePath().toString());
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, 1, row);


        // Java
        text = new Text(PConst.LINE_SEPARATORx2 + "Java Informationen");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, 0, ++row, 2, 1);

        text = new Text("Version:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, 0, ++row);

        text = new Text(System.getProperty("java.version"));
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, 1, row);

        text = new Text("Type:");
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, 0, ++row);

        String strVmType = System.getProperty("java.vm.name");
        strVmType += " (" + System.getProperty("java.vendor") + ")";
        text = new Text(strVmType);
        text.setFont(new Font(15));
        text.setFill(GRAY);
        gridPane.add(text, 1, row);

        text = new Text(PConst.LINE_SEPARATORx2 + "Ein Dankeschön an alle," + PConst.LINE_SEPARATOR +
                "die mit Vorschlägen oder Quelltext" + PConst.LINE_SEPARATOR +
                "zu diesem Programm beigetragen haben.");
        text.setFont(Font.font(null, FontWeight.BOLD, 15));
        gridPane.add(text, 0, ++row, 2, 1);


        hBox.getChildren().add(gridPane);

    }

    private javafx.scene.image.Image getImage() {
        final String path = "/de/mtplayer/mtp/res/P2.png";
        return new Image(path, 128, 128, false, true);
    }

}
