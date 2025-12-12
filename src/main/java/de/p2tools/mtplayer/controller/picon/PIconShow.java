package de.p2tools.mtplayer.controller.picon;

import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.ikonli.P2IconFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

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

        int row = 0;
        int col = 0;
        gridPane.add(new Label("P2ICON"), 0, row++);
        for (P2IconFactory.P2ICON p2Icon : P2IconFactory.P2ICON.values()) {
            gridPane.add(new Label(p2Icon.getLiteral()), col++, row);
            gridPane.add(p2Icon.getFontIcon(SIZE), col++, row);
            if (col > 2) {
                col = 0;
                row++;
            }
        }
        gridPane.add(new Label("PICON"), 0, ++row);

        ++row;
        col = 0;
        for (PIconFactory.PICON pIcon : PIconFactory.PICON.values()) {
            gridPane.add(new Label(pIcon.getLiteral()), col++, row);
            gridPane.add(pIcon.getFontIcon(SIZE), col++, row);
            if (col > 2) {
                col = 0;
                row++;
            }
        }

        getVBoxCont().getChildren().add(scrollPane);
    }
}