package de.p2tools.mtplayer.controller.tips;

import java.util.List;

public class TipsFactory {
    public enum TIPPS {
        INFOS("Infos", TipListInfos.getTips()),
        GUI("Gui", TipListGui.getTips()),
        FILME("Filme", TipListFilm.getTips()),
        DOWNLOAD("Downloads", TipListDownload.getTips()),
        ABO("Abos", TipListAbo.getTips()),
        FILTER("Filter", TipListFilter.getTips()),
        SET("Sets", TipListSet.getTips());

        private final String name;
        private final List<TipData> tipsList;

        TIPPS(String name, List<TipData> tipsList) {
            this.name = name;
            this.tipsList = tipsList;
        }

        public String getName() {
            return name;
        }

        public List<TipData> getTipsList() {
            return tipsList;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private TipsFactory() {
    }
}
