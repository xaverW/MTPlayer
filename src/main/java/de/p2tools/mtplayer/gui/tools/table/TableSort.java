package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class TableSort {
    private final TableView<FilmDataMTP> tableView;
    private final List<TableColumn<FilmDataMTP, ?>> sortOrder = FXCollections.observableArrayList();

    public TableSort(TableView<FilmDataMTP> tableView) {
        // Start beim sortierten Titel: 64s und damit: 36s
        this.tableView = tableView;
        tableView.getSortOrder().forEach(s -> sortOrder.add(s));
        tableView.getSortOrder().clear();
    }

    public void setSort() {
        P2Log.debugLog("====================");
        P2Log.debugLog("Table setSort");
        tableView.getSortOrder().setAll(sortOrder);
    }
}
