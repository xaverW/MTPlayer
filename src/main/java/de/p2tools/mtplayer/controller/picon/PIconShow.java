package de.p2tools.mtplayer.controller.picon;

import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Text;
import de.p2tools.p2lib.ikonli.P2IconFactory;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class PIconShow extends P2DialogExtra {

    private static int SIZE = 25;

    public PIconShow() {
        super(P2LibConst.primaryStage, null, "Icons", false, false, false);
        init(true);
    }

    @Override
    public void make() {
        Button btnOk = new Button("OK");
        btnOk.setOnAction(a -> close());
        addOkButton(btnOk);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(10);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(gridPane);

        final int maxCol = 4;
        int row = 0;
        int col = 0;

        Label t1 = P2Text.getTextBoldUnderline("P2ICON");
        t1.setPadding(new Insets(5));
        gridPane.add(t1, 0, row++);
        for (P2IconFactory.P2ICON p2Icon : P2IconFactory.P2ICON.values()) {
            HBox hBox = new HBox(5);
            hBox.getChildren().addAll(p2Icon.getFontIcon(SIZE), new Label(p2Icon.getLiteral()));
            gridPane.add(hBox, col++, row);
            if (col > maxCol) {
                col = 0;
                row++;
            }
        }
        gridPane.add(new Label(""), 0, ++row);
        Label t2 = P2Text.getTextBoldUnderline("PICON");
        t2.setPadding(new Insets(5));
        gridPane.add(t2, 0, ++row);

        ++row;
        col = 0;
        for (PIconFactory.PICON pIcon : PIconFactory.PICON.values()) {
            HBox hBox = new HBox(5);
            hBox.getChildren().addAll(pIcon.getFontIcon(SIZE), new Label(pIcon.getLiteral()));
            gridPane.add(hBox, col++, row);
            if (col > maxCol) {
                col = 0;
                row++;
            }
        }

        getVBoxCont().getChildren().add(scrollPane);
    }
}