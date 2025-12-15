package de.p2tools.mtplayer.controller.picon;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.tools.P2ColorFactory;
import de.p2tools.p2lib.tools.P2ToolsFactory;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class IconRunner {
    static final int pause = 200;
    static String[] colorList;
    static List<FontIcon> fontList;

    private IconRunner() {
    }

    public static void run() {
        fontList = getAllNodes(ProgData.getInstance().mtPlayerController);
        colorList = new String[]{P2ColorFactory.getColor(
                javafx.scene.paint.Color.ALICEBLUE),
                P2ColorFactory.getColor(Color.RED),
                P2ColorFactory.getColor(Color.BLUE),
                P2ColorFactory.getColor(Color.YELLOW),
                P2ColorFactory.getColor(Color.CORNFLOWERBLUE),
                P2ColorFactory.getColor(Color.ANTIQUEWHITE),
                P2ColorFactory.getColor(Color.BLANCHEDALMOND),
                P2ColorFactory.getColor(Color.GOLD),
                P2ColorFactory.getColor(Color.GOLDENROD),
                P2ColorFactory.getColor(Color.MEDIUMBLUE),
                P2ColorFactory.getColor(Color.TURQUOISE),
                P2ColorFactory.getColor(Color.SKYBLUE),
                P2ColorFactory.getColor(Color.DARKBLUE),
                P2ColorFactory.getColor(Color.DARKCYAN),
                P2ColorFactory.getColor(Color.DARKGREEN),
                P2ColorFactory.getColor(Color.DARKRED),
                P2ColorFactory.getColor(Color.DARKKHAKI),
                P2ColorFactory.getColor(Color.DARKRED),
                P2ColorFactory.getColor(Color.BLANCHEDALMOND),
                P2ColorFactory.getColor(Color.DARKSLATEGRAY),
                P2ColorFactory.getColor(Color.PURPLE),
                P2ColorFactory.getColor(Color.LIGHTGREY),
                P2ColorFactory.getColor(Color.KHAKI),
                P2ColorFactory.getColor(Color.LINEN),
                P2ColorFactory.getColor(Color.DARKSLATEGRAY),
                P2ColorFactory.getColor(Color.VIOLET),
                P2ColorFactory.getColor(Color.MEDIUMVIOLETRED),
                P2ColorFactory.getColor(Color.LIGHTGOLDENRODYELLOW),
                P2ColorFactory.getColor(Color.ORANGE),
                P2ColorFactory.getColor(Color.CYAN),
                P2ColorFactory.getColor(Color.GREENYELLOW),
                P2ColorFactory.getColor(Color.MEDIUMSLATEBLUE),
                P2ColorFactory.getColor(Color.LIGHTBLUE),
                P2ColorFactory.getColor(Color.MEDIUMSEAGREEN),
                P2ColorFactory.getColor(Color.OLIVEDRAB)};

        runThread();
    }

    private static void runThread() {
        new Thread(() -> {
            Random random = ThreadLocalRandom.current();

            for (int i = 0; i < 100; ++i) {
                Platform.runLater(() -> {
                    fontList.forEach(f -> {
                        int number = random.nextInt(colorList.length);
                        String c = colorList[number];
                        f.setIconColor(Paint.valueOf(c));
                    });
                });

                P2ToolsFactory.pause(pause);
            }
        }).start();
    }

    public static List<FontIcon> getAllNodes(Parent root) {
        Paint paint;
        List<FontIcon> ret = new ArrayList<>();
        try {
            // Paint.valueOf(ProgConfig.SYSTEM_ICON_COLOR.getValueSafe())
            paint = Color.web(P2LibConst.iconColor.getValueSafe());
        } catch (Exception e) {
            P2Log.errorLog(656564541, e.getMessage());
            return ret;
        }
        ArrayList<Node> nodes = new ArrayList<>();
        add(root, nodes);
        int i = 0;
        for (Node node : nodes) {
            if (node.getClass().equals(FontIcon.class)) {
                ((FontIcon) node).setIconColor(paint);
                ret.add((FontIcon) node);
                ++i;
            }
        }
        P2Log.sysLog("FontIcon von --> " + i + " <-- Nodes geändert");
        return ret;
    }

    private static void add(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                add((Parent) node, nodes);
        }
    }


}
