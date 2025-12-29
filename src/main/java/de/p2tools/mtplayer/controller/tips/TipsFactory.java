package de.p2tools.mtplayer.controller.tips;

import java.util.List;

public class TipsFactory {
    public enum TIPPS {
        INFOS("Infos", TipListInfos.getTips()),
        GUI("Gui", TipListGui.getTips()),
        FILTER("Filter", TipListFilter.getTips()),
        ABO("Abo", TipListAbo.getTips()),
        ALL("Alles", TipListAll.getTips());

        private final String name;
        private final List<PTipOfDay> tipsList;

        TIPPS(String name, List<PTipOfDay> tipsList) {
            this.name = name;
            this.tipsList = tipsList;
        }

        public String getName() {
            return name;
        }

        public List<PTipOfDay> getTipsList() {
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
