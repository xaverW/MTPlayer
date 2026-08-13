package de.p2tools.mtplayer.controller.data.history;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.alert.P2Alert;
import javafx.stage.Stage;

import java.util.*;

public class HistoryFactory {
    private HistoryFactory() {
    }

    public static void delSelection(Stage stage, List<HistoryData> list) {
        if (list.isEmpty()) {
            P2Alert.showInfoNoSelection(stage);
            return;
        }

        // alle die gelöscht werden sollen
        HashMap<String, HistoryData> map = new HashMap<>();
        list.forEach(h -> map.put(h.getUrl(), h));

        if (list.size() <= 1) {
            ProgData.getInstance().historyListJson.removeHistory(list);
            return;
        }


        List<HistoryData> rest = new ArrayList<>();
        ProgData.getInstance().historyListJson.forEach(h -> {
            if (map.get(h.getUrl()) == null) {
                // dann solls nicht gelöscht werden
                rest.add(h);
            }
        });

        if (P2Alert.showAlertOkCancel(stage, "Löschen", "History löschen",
                "Soll die gesamte Auswahl ("
                        + list.size() + " Einträge) gelöscht werden?")) {
            ProgData.getInstance().historyListJson.replaceList(rest);
            resetFilm(map);
        }
    }

    private static void resetFilm(HashMap<String, HistoryData> map) {
        ProgData.getInstance().filmList.forEach(film -> {
            if (map.get(film.getUrlHistory()) != null) {
                // dann wird er gelöscht
                film.setShown(false);
                film.setActHist(false);
            }
        });
        ProgData.getInstance().audioList.forEach(film -> {
            if (map.get(film.getUrlHistory()) != null) {
                // dann wird er gelöscht
                film.setShown(false);
                film.setActHist(false);
            }
        });
    }

    public static void delNotInList(Stage stage) {
        List<HistoryData> retList = new ArrayList<>();
        HashMap<String, HistoryData> historyMap = new HashMap<>();
        ProgData.getInstance().historyListJson.forEach(h -> historyMap.put(h.getUrl(), h));

        ProgData.getInstance().filmList.forEach(f -> {
            HistoryData h = historyMap.remove(f.getUrlHistory());
            // dann ists noch in der Filmliste
            if (h != null) {
                retList.add(h);
            }
        });
        ProgData.getInstance().audioList.forEach(f -> {
            HistoryData h = historyMap.remove(f.getUrlHistory());
            // dann ists noch in der Audioliste
            if (h != null) {
                retList.add(h);
            }
        });

        int size = historyMap.size();
        if (size <= 0) {
            P2Alert.showInfoAlert(stage, "History",
                    "Einträge aus der History löschen",
                    "Es sind keine alten Einträge in der History.");
        } else {
            if (P2Alert.BUTTON.YES.equals(P2Alert.showAlert_yes_no(stage, "History",
                    "Einträge aus der History löschen",
                    "Sollen:\n\n" +
                            size + " Einträge\n\n" +
                            "gelöscht werden?"))) {
                // dann löschen
                ProgData.getInstance().historyListJson.replaceList(retList);
            }
        }
    }

    public static synchronized void delOld(Stage stage, int year) {
        // HistoryData mit Alter und nicht in der Filmliste
        if (year <= 0) {
            return;
        }

        final Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date());

        List<HistoryData> retList = new ArrayList<>();
        HashMap<String, HistoryData> historyMap = new HashMap<>();
        ProgData.getInstance().historyListJson.forEach(h -> historyMap.put(h.getUrl(), h));

        ProgData.getInstance().filmList.forEach(f -> {
            HistoryData h = historyMap.remove(f.getUrlHistory());
            // dann ists noch in der Filmliste
            if (h != null) {
                retList.add(h);
            }
        });
        ProgData.getInstance().audioList.forEach(f -> {
            HistoryData h = historyMap.remove(f.getUrlHistory());
            // dann ists noch in der Audioliste
            if (h != null) {
                retList.add(h);
            }
        });

        // der Rest zum löschen
        List<HistoryData> mapList = historyMap.values().stream().toList();
        List<HistoryData> delList = new ArrayList<>();

        mapList.forEach(h -> {
            Calendar c2 = Calendar.getInstance();
            c2.setTime(h.getDate());
            int yearDiff = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
            if (yearDiff < year) {
                retList.add(h);
            } else {
                delList.add(h);
            }
        });

        int size = delList.size();
        if (size <= 0) {
            P2Alert.showInfoAlert(stage, "History",
                    "Einträge aus der History löschen",
                    "Es sind keine alten Einträge in der History.");
        } else {
            if (P2Alert.BUTTON.YES.equals(P2Alert.showAlert_yes_no(stage, "History",
                    "Einträge aus der History löschen",
                    "Sollen:\n\n" +
                            size + " Einträge\n\n" +
                            "gelöscht werden?"))) {
                // dann löschen
                ProgData.getInstance().historyListJson.replaceList(retList);
            }
        }
    }
}
