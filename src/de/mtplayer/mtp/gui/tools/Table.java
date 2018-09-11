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

package de.mtplayer.mtp.gui.tools;

import de.mtplayer.mLib.tools.Data;
import de.mtplayer.mLib.tools.MLConfigs;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class Table {
    public static enum TABLE {
        FILM, DOWNLOAD, ABO
    }

    private static final String SORT_ASCENDING = "ASCENDING";
    private static final String SORT_DESCENDING = "DESCENDING";
    private String width = "";
    private String sort = "";
    private String upDown = "";
    private String vis = "";
    private String order = "";

    private int maxSpalten;
    private double[] breite;
    private boolean[] visAr;
    private TableColumn[] tArray;

    private MLConfigs confWidth; //Spaltenbreite
    private MLConfigs confSort; //"Sortieren"  der Tabelle nach Spalte
    private MLConfigs confUpDown; //Sortierung UP oder Down
    private MLConfigs confVis; //Spalte ist sichtbar
    private MLConfigs confOrder; //"Reihenfolge" der Spalten

    public static void refresh_table(TableView table) {
        for (int i = 0; i < table.getColumns().size(); i++) {
            ((TableColumn) (table.getColumns().get(i))).setVisible(false);
            ((TableColumn) (table.getColumns().get(i))).setVisible(true);
        }
    }

    private void initConf(TABLE eTable) {
        switch (eTable) {

            case FILM:
                confWidth = ProgConfig.FILM_GUI_TABLE_WIDTH;
                confSort = ProgConfig.FILM_GUI_TABLE_SORT;
                confUpDown = ProgConfig.FILM_GUI_TABLE_UP_DOWN;
                confVis = ProgConfig.FILM_GUI_TABLE_VIS;
                confOrder = ProgConfig.FILM_GUI_TABLE_ORDER;
                break;

            case DOWNLOAD:
                confWidth = ProgConfig.DOWNLOAD_GUI_TABLE_WIDTH;
                confSort = ProgConfig.DOWNLOAD_GUI_TABLE_SORT;
                confUpDown = ProgConfig.DOWNLOAD_GUI_TABLE_UP_DOWN;
                confVis = ProgConfig.DOWNLOAD_GUI_TABLE_VIS;
                confOrder = ProgConfig.DOWNLOAD_GUI_TABLE_ORDER;
                break;

            case ABO:
                confWidth = ProgConfig.ABO_GUI_TABLE_WIDTH;
                confSort = ProgConfig.ABO_GUI_TABLE_SORT;
                confUpDown = ProgConfig.ABO_GUI_TABLE_UP_DOWN;
                confVis = ProgConfig.ABO_GUI_TABLE_VIS;
                confOrder = ProgConfig.ABO_GUI_TABLE_ORDER;
                break;
        }
    }

    private void initColumn(TABLE eTable, TableView<Data> table) {
//        table.setTooltip(new Tooltip("In der Tabelle können mit dem \"+\"-Button" + PConst.LINE_SEPARATOR +
//                "Spalten ein- und ausgeblendet werden." + PConst.LINE_SEPARATOR +
//                "Mit einem Klick auf den Titel einer Spalte" + PConst.LINE_SEPARATOR +
//                "wird die Tabelle nach der Spalte sortiert."));
        switch (eTable) {
            case FILM:
                tArray = new TableFilm(ProgData.getInstance()).initFilmColumn(table);
                break;

            case DOWNLOAD:
                tArray = new TableDownload().initDownloadColumn(table);
                break;

            case ABO:
                tArray = TableAbo.initAboColumn(table);
                break;
        }
        String order = confOrder.get();
        String[] arOrder = order.split(",");
        if (confOrder.get().isEmpty() || arOrder.length != tArray.length) {
            // dann gibts keine Einstellungen oder die Anzahl der Spalten hat sich geändert
            for (TableColumn tc : tArray) {
                table.getColumns().add(tc);
            }
        } else {
            for (int i = 0; i < arOrder.length; ++i) {
                String s = arOrder[i];
                for (TableColumn tc : tArray) {
                    if (s.equals(tc.getText())) {
                        if (!table.getColumns().contains(tc)) {
                            //aus Fehlern wird man klug :(
                            table.getColumns().add(tc);
                        }
                    }
                }
            }
        }
        table.getColumns().stream().forEach(c -> c.setSortable(true));
        table.getColumns().stream().forEach(c -> c.setVisible(true));

    }

    public void saveTable(TableView ta, TABLE eTable) {
        // Tabellendaten sichern

        TableView<Data> table = ta;

        initConf(eTable);
        maxSpalten = table.getColumns().size();

        table.getColumns().stream().forEach(c -> {
            width += c.getWidth() + ",";
            vis += String.valueOf(c.isVisible()) + ",";
        });

        table.getSortOrder().stream().forEach(so -> {
            sort += so.getText() + ",";
        });

        if (table.getSortOrder().size() > 0) {
            table.getSortOrder().stream().forEach(so -> {
                upDown += (so.getSortType().equals(TableColumn.SortType.ASCENDING) ? SORT_ASCENDING : SORT_DESCENDING) + ",";
            });
        }

        table.getColumns().stream().forEach(c -> {
            order += c.getText() + ",";
        });

        confWidth.setValue(width);
        confVis.setValue(vis);
        confSort.setValue(sort);
        confUpDown.setValue(upDown);
        confOrder.setValue(order);

    }


    public void resetTable(TableView ta, TABLE eTable) {
        reset(ta, eTable);
        setTable(ta, eTable);
    }

    private void reset(TableView ta, TABLE eTable) {
        initConf(eTable);
        maxSpalten = ta.getColumns().size();
        switch (eTable) {
            case FILM:
                resetFilm();
                break;

            case DOWNLOAD:
                resetDownload();
                break;

            case ABO:
                resetAbo();
                break;
        }
    }


    public void setTable(TableView ta, TABLE eTable) {
        // Tabelle setzen
        TableView<Data> table = ta;
        try {

            initConf(eTable);
            initColumn(eTable, table);


            maxSpalten = table.getColumns().size();

            breite = getDoubleArray(maxSpalten);
            visAr = getBoolArray(maxSpalten);

            if (!confWidth.get().isEmpty()) {
                width = confWidth.get();
                if (readArr(width, breite)) {
                    for (int i = 0; i < breite.length; ++i) {
                        table.getColumns().get(i).setPrefWidth(breite[i]);
                    }
                }
            }

            if (!confVis.get().isEmpty()) {
                vis = confVis.get();
                if (readArr(vis, visAr)) {
                    for (int i = 0; i < visAr.length; ++i) {
                        table.getColumns().get(i).setVisible(visAr[i]);
                    }
                }
            }

            if (!confSort.get().isEmpty()) {
                String sort = confSort.get();
                String[] arSort = sort.split(",");
                String sortUp = confUpDown.get();
                String[] arSortUp = sortUp.split(",");

                for (int i = 0; i < arSort.length; ++i) {
                    String s = arSort[i];
                    TableColumn co = table.getColumns().stream().filter(c -> c.getText().equals(s)).findFirst().get();
                    table.getSortOrder().add(co);

                    if (arSort.length == arSortUp.length) {
                        co.setSortType(arSortUp[i].equals(SORT_ASCENDING) ? TableColumn.SortType.ASCENDING : TableColumn.SortType.DESCENDING);
                    }
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(642103218, ex.getMessage());
            reset(ta, eTable);
        }

    }

    private boolean readArr(String s, double[] arr) {
        String sub;
        String[] sarr = s.split(",");
        if (maxSpalten != sarr.length) {
            // dann hat sich die Anzahl der Spalten der Tabelle geändert: Versionswechsel
            return false;
        } else {
            for (int i = 0; i < maxSpalten; i++) {
                try {
                    arr[i] = Double.parseDouble(sarr[i]);
                } catch (final Exception ex) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean readArr(String s, boolean[] arr) {
        String sub;
        String[] sarr = s.split(",");
        if (maxSpalten != sarr.length) {
            // dann hat sich die Anzahl der Spalten der Tabelle geändert: Versionswechsel
            return false;
        } else {
            for (int i = 0; i < maxSpalten; i++) {
                try {
                    arr[i] = Boolean.parseBoolean(sarr[i]);
                } catch (final Exception ex) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean readArr(String s, String[] arr) {
        arr = s.split(",");
        if (maxSpalten != arr.length) {
            // dann hat sich die Anzahl der Spalten der Tabelle geändert: Versionswechsel
            return false;
        }
        return true;
    }

    private int countString(String s) {
        int ret = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == ',') {
                ++ret;
            }
        }
        return ++ret;
    }

    private double[] getDoubleArray(int count) {
        final double[] arr = new double[count];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = -1;
        }
        return arr;
    }

    private boolean[] getBoolArray(int count) {
        final boolean[] arr = new boolean[count];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = true;
        }
        return arr;
    }

    private void resetFilm() {
        String[] visArray = new String[maxSpalten];
        String set = "";

        for (int i = 0; i < maxSpalten; ++i) {
            visArray[i] = Boolean.TRUE.toString();
        }

        for (int i = 0; i < maxSpalten; ++i) {
            set += visArray[i] + ",";
        }

        confWidth.setValue("");
        confVis.setValue(set);
        confSort.setValue("");
        confUpDown.setValue("");
        confOrder.setValue("");
    }

    private void resetDownload() {
        String[] visArray = new String[maxSpalten];
        String set = "";

        for (int i = 0; i < maxSpalten; ++i) {
            visArray[i] = Boolean.TRUE.toString();
        }

        for (int i = 0; i < maxSpalten; ++i) {
            set += visArray[i] + ",";
        }

        confWidth.setValue("");
        confVis.setValue(set);
        confSort.setValue("");
        confUpDown.setValue("");
        confOrder.setValue("");
    }

    private void resetAbo() {
        String[] visArray = new String[maxSpalten];
        String set = "";

        for (int i = 0; i < maxSpalten; ++i) {
            visArray[i] = Boolean.TRUE.toString();
        }
        visArray[Abo.ABO_PSET_NAME] = Boolean.FALSE.toString();

        for (int i = 0; i < maxSpalten; ++i) {
            set += visArray[i] + ",";
        }

        confWidth.setValue("");
        confVis.setValue(set);
        confSort.setValue("");
        confUpDown.setValue("");
        confOrder.setValue("");
    }
}
