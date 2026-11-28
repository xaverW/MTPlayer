package de.p2tools.mtplayer.controller.update;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.p2lib.dialogs.WhatsNewInfo;
import de.p2tools.p2lib.tools.date.P2LDateFactory;

import java.time.LocalDate;
import java.util.ArrayList;

public class WhatsNewList extends ArrayList<WhatsNewInfo> {
    private LocalDate maxDate = LocalDate.MIN;

    public WhatsNewList() {
        addWhatsNew();
        this.forEach(whatsNewInfo -> {
            if (whatsNewInfo.getDate().isAfter(maxDate)) {
                maxDate = whatsNewInfo.getDate();
            }
        });
    }

    public void setLastShownToMax() {
        ProgConfig.SYSTEM_WHATS_NEW_DATE_LAST_SHOWN.setValue(P2LDateFactory.toStringR(maxDate));
    }

    public ArrayList<WhatsNewInfo> getOnlyNews() {
        final ArrayList<WhatsNewInfo> list = new ArrayList<>();
        final LocalDate lastDate = P2LDateFactory.fromStringR(ProgConfig.SYSTEM_WHATS_NEW_DATE_LAST_SHOWN.getValueSafe());
        this.forEach(whatsNewInfo -> {
            if (lastDate.isBefore(whatsNewInfo.getDate())) {
                list.add(whatsNewInfo);
            }
        });
        return list;
    }

    private void addWhatsNew() {
        WhatsNewInfo whatsNewInfo = new WhatsNewInfo(LocalDate.of(2026, 11, 14),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_program_infos.png",
                "Programm-Infos",
                """
                        Im Programmmenü: "Tools->Programminfos" werden in einem
                        neuen Dialog Infos zum Speicherverbrauch und über das
                        Programm angezeigt.""", 60);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2026, 11, 14),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_search_arte.png",
                "Live-Suche bei ARTE",
                """
                        In der Live-Suche ist jetzt auch der Sender ARTE enthalten.
                        Damit kann dann nach Suchbegriffen und mit der URL einer
                        Sendung gesucht werden.""", 70);
        add(whatsNewInfo);
        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2026, 11, 26),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_sortTable.png",
                "Spalte Titel blockweise sortieren",
                """
                        Wird in den Tabellen die Spalte "Titel" sortiert, ist das jetzt
                        auch blockweise möglich, zB. zuerst alle Filme mit "Tatort"
                        im Namen, dann alle Filme mit "Sport" im Namen und dann der Rest.
                        In den Blöcken werden die Filme auch alphabetisch sortiert.
                        So können zB. lange Downloadlisten Thematisch sortiert werden.
                        
                        In einem Dialog (im Programmmenü ->Tools ->Titel-Sortierung)
                        kann sie eingestellt und für jede Tabelle eingeschaltet werden.
                        
                        Es gibt dann im Filterpanel links neben der Tabelle ein neues
                        Auswahlfeld mit den erstellten Sortier-Einstellungen.
                        """, 220);
        add(whatsNewInfo);
    }
}
