package de.p2tools.mtplayer.controller.data.replace;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.mediathek.tools.P2FileNameUtils;
import org.apache.commons.lang3.SystemUtils;

public class ReplaceFactory {
    private ReplaceFactory() {
    }


    /**
     * Entferne verbotene Zeichen aus Dateiname.
     *
     * @param name   Dateiname
     * @param isPath
     * @return Bereinigte Fassung
     */
    public static String replaceFileNameWithReplaceList(String name, boolean isPath) {
        // AboListFactory.addNewAbo
        // AboListFactory.addNewAboFromFilterButton
        // DownloadFactoryMakeParameter.buildFileNamePath
        //                              buildFileNamePath
        // DownloadFactoryMakeParameter.getField
        // DownloadAddDialogFactory.getNextName
        String ret = name;
        boolean isWindowsPath = false;

        if (SystemUtils.IS_OS_WINDOWS && isPath && ret.length() > 1 && ret.charAt(1) == ':') {
            // damit auch "d:" und nicht nur "d:\" als Pfad geht
            isWindowsPath = true;
            ret = ret.replaceFirst(":", ""); // muss zum Schluss wieder rein, kann aber so nicht ersetzt werden
        }

        // zuerst die Ersetzungstabelle mit den Wünschen des Users
        if (ProgConfig.SYSTEM_USE_REPLACETABLE.getValue()) {
            ret = ProgData.getInstance().replaceList.replace(ret, isPath);
        }

        // und wenn gewünscht: "NUR Ascii-Zeichen"
        if (ProgConfig.SYSTEM_ONLY_ASCII.getValue()) {
            ret = P2FileNameUtils.convertToASCIIEncoding(ret, isPath);
        } else {
            ret = P2FileNameUtils.convertToNativeEncoding(ret, isPath);
        }

        if (isWindowsPath) {
            // c: wieder herstellen
            if (ret.length() == 1) {
                ret = ret + ":";
            } else if (ret.length() > 1) {
                ret = ret.charAt(0) + ":" + ret.substring(1);
            }
        }
        return ret;
    }
}
