package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.compdata.CompData;
import de.p2tools.mtplayer.controller.data.compdata.CompList;

import java.util.Comparator;
import java.util.regex.Pattern;

public class TableFactory {
    private TableFactory() {
    }

    final static String R1 = "Tatort";
    final static String R2 = "Sport";

//        titleColumn.setComparator((s, t1) -> s.length() > t1.length() ? 0 : 1);

//        titleColumn.setComparator((s, t1) -> s.contains("(S02/E06)") && !t1.contains(" (S02/E06)") ? 0 : 1);

//        titleColumn.setComparator((s, t1) -> s.matches(".*[(S]\\d\\d[\\/E].*") &&
//                !t1.matches(".*[(S]\\d\\d[\\/E].*") ? 0 : 1);

//        final String REG = ".*[(S]\\d\\d[\\/E].*";
//        titleColumn.setComparator((s, t1) -> {
//            int c = t1.toLowerCase().compareTo(s.toLowerCase());
//
//            if (s.matches(REG) && t1.matches(REG)) {
//                return c;
//            } else if (s.matches(REG) && !t1.matches(REG)) {
//                return 1;
//            } else if (!s.matches(REG) && t1.matches(REG)) {
//                return -1;
//            } else {
//                return c;
//            }
//        });

//    int c = t1.toLowerCase().compareTo(s.toLowerCase());
//
//                if (s.matches(R1) && t1.matches(R1)) {
//        return c;
//    } else if (s.contains(R1) && !t1.contains(R1)) {
//        return 1;
//    } else if (!s.contains(R1) && t1.contains(R1)) {
//        return -1;
//
//    } else if (s.contains(R2) && t1.contains(R2)) {
//        return c;
//    } else if (s.contains(R2) && !t1.contains(R2)) {
//        return 1;
//    } else if (!s.contains(R2) && t1.contains(R2)) {
//        return -1;
//
//    } else {
//        return c;
//    }

// #:.*[(][S]\d\d[\/][E].*

    public static Comparator<String> getComparator(boolean audio) {
        CompList compList = audio ? ProgData.getInstance().compListAudio : ProgData.getInstance().compListFilm;

        Comparator<String> comparator = new Comparator<>() {
            @Override
            public int compare(String s, String t1) {
                // t nach s => t>s => 1
                s = s.toLowerCase();
                t1 = t1.toLowerCase();
                final int c = s.compareTo(t1);

                for (CompData compData : compList) {
                    if (compData.isRegEx()) {
                        // regEx
                        Pattern pattern = compData.getPattern();
                        if (s.contains("(23/27)")) {
                            System.out.println("");
                        }
                        if (ProgConfig.SYSTEM_FILTER_REG_EX_ONLY_CONTAIN.get()) {
                            boolean tt = pattern.matcher(s).matches();
                            if (tt) {
                                System.out.println("");
                            }
                            boolean tt1 = pattern.matcher(s).find();
                            if (tt1) {
                                System.out.println("");
                            }

                            if (pattern.matcher(s).find() && pattern.matcher(t1).find()) {
                                return c;
                            } else if (pattern.matcher(s).find() && !pattern.matcher(t1).find()) {
                                return -1;
                            } else if (!pattern.matcher(s).find() && pattern.matcher(t1).find()) {
                                return 1;
                            }

                        } else {
                            boolean tt = pattern.matcher(s).matches();
                            if (tt) {
                                System.out.println("");
                            }
                            boolean tt1 = pattern.matcher(s).find();
                            if (tt1) {
                                System.out.println("");
                            }
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
            }
        };
        return comparator;
    }
}
