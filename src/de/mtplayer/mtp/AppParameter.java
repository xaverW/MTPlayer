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


package de.mtplayer.mtp;

import de.mtplayer.mtp.controller.ProgStart;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
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
        ProgData.configDir = readPathFromArguments(arguments);

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
                    ProgStart.shortStartMsg();
                    LogMessage.endMsg();
                    System.exit(0);
                });
            }

            if (hasOption(line, ProgParameter.DEBUG)) {
                ProgData.debug = true;
            }

            if (hasOption(line, ProgParameter.PATH)) {
                String configDir = line.getOptionValue(ProgParameter.PATH.name);
                if (!configDir.endsWith(File.separator)) {
                    configDir += File.separator;
                }
                ProgData.configDir = configDir;
            }

        } catch (Exception ex) {
            PLog.errorLog(941237890, ex);
        }
    }

    private String readPathFromArguments(final String[] arguments) {
        String path = "";

        if (arguments == null || arguments.length == 0) {
            return path;
        }

        if (arguments[0].startsWith(ARGUMENT_PREFIX)) {
            return path;
        }

        path = arguments[0];
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }

        return path;
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
        DEBUG("d", "debug", false, "show debug info");

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
        formatter.printHelp(ProgConst.PROGRAMNAME, allowed);

    }


}
