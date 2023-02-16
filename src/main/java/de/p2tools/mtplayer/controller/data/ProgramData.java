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

package de.p2tools.mtplayer.controller.data;

import de.p2tools.mtplayer.controller.starter.RuntimeExec;

public class ProgramData extends ProgramDataProps {

    public ProgramData() {
        setRestart(false);
        setDownManager(false);
    }

    public ProgramData(String name, String programmPath, String schalter, String restart, String downloadmanager) {
        setName(name);
        setProgPath(programmPath);
        setProgSwitch(schalter);
        setRestart(restart.equals("") ? false : Boolean.parseBoolean(restart));
        setDownManager(downloadmanager.equals("") ? false : Boolean.parseBoolean(downloadmanager));
    }

    public ProgramData getCopy() {
        final ProgramData ret = new ProgramData();
        for (int i = 0; i < properties.length; ++i) {
            ret.properties[i].setValue(this.properties[i].getValue());
        }
        return ret;
    }

    public boolean urlTesten(String url) {
        //prüfen ob das Programm zur Url passt
        boolean ret = false;
        if (url != null) {
            //Felder sind entweder leer oder passen
            if (SetFactory.testPrefix(getPrefix(), url, true)
                    && SetFactory.testPrefix(getSuffix(), url, false)) {
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
            ret = ret + RuntimeExec.TRENNER_PROG_ARRAY + s;
        }
        return ret;
    }

    public static String makeProgAufrufArray(String pArray) {
        final String[] progArray = pArray.split(RuntimeExec.TRENNER_PROG_ARRAY);
        String execStr = "";
        for (final String s : progArray) {
            execStr = execStr + s + " ";
        }
        execStr = execStr.trim(); // letztes Leerzeichen wieder entfernen
        return execStr;
    }
}
