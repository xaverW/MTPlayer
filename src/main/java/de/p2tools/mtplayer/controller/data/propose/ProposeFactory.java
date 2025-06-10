/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.data.propose;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.cleaningdata.CleaningProposeFactory;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;

import java.util.Hashtable;

public class ProposeFactory {

    private ProposeFactory() {
    }

    public static void generateProposeList() {
        final Hashtable<String, Integer> hashtable = new Hashtable<>();
        ProgData.getInstance().proposeList.clear();

        ProgData.getInstance().historyList.forEach(historyData -> {
            CleaningProposeFactory.cleanSearchText(historyData, hashtable);
        });

        hashtable.forEach((s, i) -> {
            if (i > 250) {
                ProposeData proposeData = new ProposeData(s, i);
                ProgData.getInstance().proposeList.add(proposeData);
            }
        });
        hashtable.clear();
    }

    public static void generateFilmList(int minDur, int maxDur) {
        ProgData.getInstance().proposeList.getFilmDataList().clear();
        ProgData.getInstance().filmList
                .forEach(filmDataMTP ->
                        ProgData.getInstance().proposeList.forEach(p -> {
                            // dann dafür die Filme suchen
                            if (filmDataMTP.getTheme().contains(p.getName()) ||
                                    filmDataMTP.getTitle().contains(p.getName())) {
                                int prop = p.getCount() / (filmDataMTP.getTheme().length() + filmDataMTP.getTitle().length());
                                filmDataMTP.addPropose(prop);
                            }
                        })
                );

        ProgData.getInstance().filmList
                .filtered(p -> !p.isShown())
                .filtered(p -> p.getPropose() > 20)
                .filtered(p -> minDur == 0 ||
                        p.getDurationMinute() >= minDur)
                .filtered(p -> maxDur == FilterCheck.FILTER_DURATION_MAX_MINUTE ||
                        p.getDurationMinute() <= maxDur)
                .forEach(f -> ProgData.getInstance().proposeList.getFilmDataList().add(f));
    }
}
