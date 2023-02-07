/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer;

import de.p2tools.mtplayer.controller.ProgStartAfterGui;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2Lib.tools.log.LogMessage;
import de.p2tools.p2Lib.tools.log.PLog;
import org.apache.commons.cli.*;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class AppParameter {
    public static final String TEXT_LINE = "===========================================";
    private static final String ARGUMENT_PREFIX = "-";

    void processArgs(final String... arguments) {
        if (arguments == null) {
            return;
        }

        printArguments(arguments);
        readPathFromArguments(arguments);

        try {
            final Options allowed = new Options();
            for (ProgParameter parameter : ProgParameter.values()) {
                allowed.addOption(parameter.shortname, parameter.name, parameter.hasArgs, parameter.helpText);
            }

            final CommandLineParser parser = new DefaultParser();
            final CommandLine line = parser.parse(allowed, arguments);

            if (hasOption(line, ProgParameter.HELP)) {
                printHelp(allowed);
                System.exit(0);
            }

            if (hasOption(line, ProgParameter.VERSION)) {
                EventQueue.invokeLater(() -> {
                    ProgStartAfterGui.shortStartMsg();
                    LogMessage.endMsg();
                    System.exit(0);
                });
            }

            if (hasOption(line, ProgParameter.DEBUG)) {
                ProgData.debug = true;
            }

            if (hasOption(line, ProgParameter.AUTOMODE)) {
                ProgData.automode = true;
            }

            if (hasOption(line, ProgParameter.DURATION)) {
                ProgData.duration = true;
            }

            if (hasOption(line, ProgParameter.PATH)) {
                String path = line.getOptionValue(ProgParameter.PATH.name);
                setConfigDir(path);
            }

        } catch (Exception ex) {
            PLog.errorLog(941237890, ex);
        }
    }

    private void readPathFromArguments(final String[] arguments) {
        if (arguments == null ||
                arguments.length == 0 ||
                arguments[0].startsWith(ARGUMENT_PREFIX)) {
            return;
        }

        setConfigDir(arguments[0]);
    }

    private void setConfigDir(String path) {
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        ProgData.configDir = path;
    }

    private void printArguments(final String[] aArguments) {
        if (aArguments.length == 0) {
            return;
        }

        ArrayList<String> list = new ArrayList<>();
        list.add(TEXT_LINE);
        for (final String argument : aArguments) {
            list.add(String.format("Startparameter: %s", argument));
        }
        list.add(TEXT_LINE);

        PLog.emptyLine();
        PLog.sysLog(list);
        PLog.emptyLine();
    }

    enum ProgParameter {
        HELP("h", "help", false, "show help"),
        VERSION("v", "version", false, "show version"),
        PATH("p", "path", true, "path of configuration file"),
        DEBUG("d", "debug", false, "show debug info"),
        AUTOMODE("a", "auto", false, "use automode: start, load, quit"),
        DURATION("t", "time", false, "show timekeeping info");

        final String shortname;
        final String name;
        final boolean hasArgs;
        final String helpText;

        ProgParameter(final String shortname, final String name,
                      final boolean hasArgs, final String helpText) {
            this.shortname = shortname;
            this.name = name;
            this.hasArgs = hasArgs;
            this.helpText = helpText;
        }

    }


    private static boolean hasOption(final CommandLine line, final ProgParameter parameter) {
        return line.hasOption(parameter.name);
    }

    private static int extractInt(final CommandLine line, final ProgParameter parameter) {
        return Integer.parseInt(line.getOptionValue(parameter.name));
    }

    private static void printHelp(final Options allowed) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(ProgConst.PROGRAM_NAME, allowed);

    }


}
