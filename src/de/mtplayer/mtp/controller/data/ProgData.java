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

package de.mtplayer.mtp.controller.data;

import de.mtplayer.mtp.gui.tools.SetsPrograms;

import static de.mtplayer.mtp.controller.starter.RuntimeExec.TRENNER_PROG_ARRAY;

public class ProgData extends ProgProps {

    public ProgData() {
        setRestart(false);
        setDownManager(false);
    }

    public ProgData(String name, String programmpfad, String schalter, String restart, String downloadmanager) {
        setName(name);
        setProgPath(programmpfad);
        setProgSwitch(schalter);
        setRestart(restart.equals("") ? false : Boolean.parseBoolean(restart));
        setDownManager(downloadmanager.equals("") ? false : Boolean.parseBoolean(downloadmanager));
    }

    public ProgData copy() {
        setXmlFromProps();

        final ProgData ret = new ProgData();
        System.arraycopy(arr, 0, ret.arr, 0, arr.length);
        ret.setPropsFromXml();

        return ret;
    }

    public boolean urlTesten(String url) {
        //prüfen ob das Programm zur Url passt
        boolean ret = false;
        if (url != null) {
            //Felder sind entweder leer oder passen
            if (SetsPrograms.praefixTesten(getPraefix(), url, true)
                    && SetsPrograms.praefixTesten(getSuffix(), url, false)) {
                ret = true;
            }
        }
        return ret;
    }

    public String getProgrammAufruf() {
        return getProgPath() + " " + getProgSwitch();
    }

    public String getProgrammAufrufArray() {
        String ret;
        ret = getProgPath();
        final String[] ar = getProgSwitch().split(" ");
        for (final String s : ar) {
            ret = ret + TRENNER_PROG_ARRAY + s;
        }
        return ret;
    }

    public static String makeProgAufrufArray(String pArray) {
        final String[] progArray = pArray.split(TRENNER_PROG_ARRAY);
        String execStr = "";
        for (final String s : progArray) {
            execStr = execStr + s + " ";
        }
        execStr = execStr.trim(); // letztes Leerzeichen wieder entfernen
        return execStr;
    }
}
