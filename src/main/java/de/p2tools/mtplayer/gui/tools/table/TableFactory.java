package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.compdata.CompData;
import de.p2tools.mtplayer.controller.data.compdata.CompDataProps;
import de.p2tools.mtplayer.controller.data.compdata.CompList;

import java.text.Collator;
import java.util.Comparator;
import java.util.regex.Pattern;

public class TableFactory {
    private TableFactory() {
    }

    // #:.*\(S\d\d\/E.*
    public static Comparator<String> getComparator(boolean audio) {
        CompList compList = audio ? ProgData.getInstance().compListAudio : ProgData.getInstance().compListFilm;

        if (compList.isEmpty()) {
            return (s, t1) -> Collator.getInstance().compare(s, t1);
        }

        if (audio ? !ProgConfig.SYSTEM_SORT_AUDIO_TABLE.get() : !ProgConfig.SYSTEM_SORT_FILM_TABLE.get()) {
            return (s, t1) -> Collator.getInstance().compare(s, t1);
        }

        compList.forEach(CompDataProps::init);
        Comparator<String> comparator = (s, t1) -> {
            // t nach s => t>s => 1
            s = s.toLowerCase();
            t1 = t1.toLowerCase();
            final int c = s.compareTo(t1);

            for (CompData compData : compList) {
                if (compData.isRegEx()) {
                    // regEx
                    Pattern pattern = compData.getPattern();
                    if (ProgConfig.SYSTEM_FILTER_REG_EX_ONLY_CONTAIN.get()) {
                        if (pattern.matcher(s).find() && pattern.matcher(t1).find()) {
                            return c;
                        } else if (pattern.matcher(s).find() && !pattern.matcher(t1).find()) {
                            return -1;
                        } else if (!pattern.matcher(s).find() && pattern.matcher(t1).find()) {
                            return 1;
                        }

                    } else {
                        if (pattern.matcher(s).matches() && pattern.matcher(t1).matches()) {
                            return c;
                        } else if (pattern.matcher(s).matches() && !pattern.matcher(t1).matches()) {
                            return -1;
                        } else if (!pattern.matcher(s).matches() && pattern.matcher(t1).matches()) {
                            return 1;
                        }
                    }

                } else {
                    // Text
                    final String text = compData.getTextLow();
                    if (s.contains(text) && t1.contains(text)) {
                        return c;
                    } else if (s.contains(text) && !t1.contains(text)) {
                        return -1;
                    } else if (!s.contains(text) && t1.contains(text)) {
                        return 1;
                    }
                }
            }

            return c;
        };
        return comparator;
    }
}
