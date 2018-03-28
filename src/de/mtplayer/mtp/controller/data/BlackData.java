/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

import de.mtplayer.mtp.tools.storedFilter.Filter;

public class BlackData extends BlackProps {


    public Filter fSender = new Filter();
    public Filter fThema = new Filter();
    public Filter fThemaTitel = new Filter();
    public Filter fTitel = new Filter();
    public Filter fSomewhere = new Filter();


    public BlackData() {
        super();
        initFilter();
    }

    public BlackData(String sender, String thema, String titel, String themaTitel) {
        super();
        initFilter();

        setSender(sender);
        setThema(thema);
        setTitel(titel);
        setThemaTitel(themaTitel);
    }

    public void createFilter() {
        fSender.filter = getSender();
        fSender.exakt = isSenderExact();
        fSender.setArray();

        fThema.filter = getThema();
        fThema.exakt = isThemaExact();
        fThema.setArray();

        fThemaTitel.filter = getThemaTitel();
        fThemaTitel.setArray();

        fTitel.filter = getTitel();
        fTitel.setArray();
    }

    private void initFilter() {
        senderProperty().addListener(l -> createFilter());
        senderExactProperty().addListener(l -> createFilter());

        themaProperty().addListener(l -> createFilter());
        themaExactProperty().addListener(l -> createFilter());

        themaTitelProperty().addListener(l -> createFilter());
        titelProperty().addListener(l -> createFilter());
    }


}
