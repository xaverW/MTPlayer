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

    public LocalDate getMaxDate() {
        return maxDate;
    }

    public void setLastShown() {
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
        WhatsNewInfo whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 7, 13),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_01.png",

                "Fortschrittsanzeige",
                "Es gab einige Optimierungen beim Suchen von Downloads. Das läuft jetzt im Hintergrund ab " +
                        "und blockiert so das Programm nicht mehr. " +
                        "Es wird jetzt auch der Fortschritt beim Suchen (Downloads, Filter der Blacklist) " +
                        "an verschiedenen Stellen im Programm " +
                        "angezeigt.", 110);
        add(whatsNewInfo);
        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 8, 27),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_02.png",

                "Anzeige Programm",
                "In den Einstellungen->Farben kann die Programmoberfläche " +
                        "auf \"Schwarz-Weiß\" umgestellt werden. (Das Programm " +
                        "muss zur Übernahme neu gestartet werden).", 110);
        add(whatsNewInfo);
    }
}
