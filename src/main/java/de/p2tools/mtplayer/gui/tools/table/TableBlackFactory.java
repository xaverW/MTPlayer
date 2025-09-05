package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.data.blackdata.BlackData;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class TableBlackFactory {
    private TableBlackFactory() {

    }

    public static void columnFactoryList(TableColumn<BlackData, Integer> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                BlackData aboData = getTableView().getItems().get(getIndex());
                if (aboData.getList() == ProgConst.LIST_FILM) {
                    setText("Film");
                } else if (aboData.getList() == ProgConst.LIST_AUDIO) {
                    setText("Audio");
                } else {
                    setText("F & A");
                }
            }
        });
    }

//    public static void columnFactoryCount(TableColumn<BlackData, Integer> column) {
//        column.setCellFactory(c -> new TableCell<>() {
//            @Override
//            protected void updateItem(Integer item, boolean empty) {
//                super.updateItem(item, empty);
//
//                if (item == null || empty) {
//                    setText(null);
//                    setStyle("");
//                    return;
//                }
//
//                if (item == 0) {
//                    setText("");
//                } else {
//                    setText(item + "");
//                }
//            }
//        });
//    }
}
