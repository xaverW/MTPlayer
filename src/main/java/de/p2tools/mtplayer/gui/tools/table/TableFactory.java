package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.compdata.CompData;
import de.p2tools.mtplayer.controller.data.compdata.CompDataProps;
import de.p2tools.mtplayer.controller.data.compdata.CompList;
import javafx.beans.property.BooleanProperty;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Pattern;

public class TableFactory {
    private TableFactory() {
    }


    // #:.*\(S\d{1,2}\/E\d{1,2}\).*
    // #:.*\(\d{1,2}\).*
    // #:.*\(\d{1,2}\/\d{1,2}\).*

    public static Comparator<String> getComparator(CompList compList, BooleanProperty sort) {
        if (compList.isEmpty()) {
            return (s, t1) -> Collator.getInstance().compare(s, t1);
        }

        if (!sort.get()) {
            return (s, t1) -> Collator.getInstance().compare(s, t1);
        }

        // collator: 8.3s
        // compare: 6s
        // "e" and "f" are considered primary differences,
        // while "e" and "ě" are secondary differences,
        // "e" and "E" are tertiary differences and
        // "e" and "e" are identical
        Collator collator = Collator.getInstance(Locale.GERMAN);
        collator.setStrength(Collator.PRIMARY);

        compList.forEach(CompDataProps::init);

        return (s, t1) -> {
            // t nach s => t>s => 1
            s = s.toLowerCase();
            t1 = t1.toLowerCase();

            // final int c = s.compareTo(t1);
            final int c = collator.compare(s, t1);

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
    }
}
