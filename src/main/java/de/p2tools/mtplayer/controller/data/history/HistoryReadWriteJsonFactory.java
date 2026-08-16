package de.p2tools.mtplayer.controller.data.history;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.tools.FileFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class HistoryReadWriteJsonFactory {
    private HistoryReadWriteJsonFactory() {
    }

    public static void read() {
        try {
            final Path urlPath = FileFactory.getUrlFilePath(ProgInfos.getSettingsDirectory_String(), ProgConst.FILE_HISTORY_JSON);
            if (!urlPath.toFile().exists()) {
                return;
            }

            JsonParser jsonParser = new JsonFactory().createParser(urlPath.toFile());
            while (jsonParser.nextToken() != null) {
                String name = jsonParser.currentName();

                if (HistoryData.TAG.equals(name)) {
                    jsonParser.nextToken();
                    readJson(jsonParser);
                }
            }
            jsonParser.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public static void write() {
        try {
            final Path urlPath = FileFactory.getUrlFilePath(ProgInfos.getSettingsDirectory_String(), ProgConst.FILE_HISTORY_JSON);
            final JsonGenerator jsonGenerator = new JsonFactory()
                    .createGenerator(new FileOutputStream(urlPath.toFile()));
            jsonGenerator.useDefaultPrettyPrinter();
            jsonGenerator.writeStartObject(); // start root object

            // Memo
            for (HistoryData historyData : ProgData.getInstance().historyListJson) {
                writeJson(historyData, jsonGenerator);
            }
            jsonGenerator.writeEndObject(); //end address object
            jsonGenerator.flush();
            jsonGenerator.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private static void writeJson(HistoryData historyData, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeObjectFieldStart(HistoryData.TAG);
        //start address object
        jsonGenerator.writeNumberField("source", historyData.getSource());
        jsonGenerator.writeStringField("date", historyData.getDate().toString());
        jsonGenerator.writeStringField("channel", historyData.getChannel());
        jsonGenerator.writeStringField("theme", historyData.getTheme());
        jsonGenerator.writeStringField("title", historyData.getTitle());
        jsonGenerator.writeStringField("url", historyData.getUrl());
        jsonGenerator.writeEndObject(); //end address object
    }

    private static void readJson(JsonParser jsonParser) throws IOException {
        int source = HistoryData.SOURCE_SHOWN_DOWNLOAD;
        String date = "";
        String channel = "";
        String theme = "";
        String title = "";
        String url = "";

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.currentName();
            jsonParser.nextToken();
            if ("date".equals(key)) {
                date = jsonParser.getText();
            } else if ("channel".equals(key)) {
                channel = jsonParser.getText();
            } else if ("theme".equals(key)) {
                theme = jsonParser.getText();
            } else if ("title".equals(key)) {
                title = jsonParser.getText();
            } else if ("url".equals(key)) {
                url = jsonParser.getText();
            } else if ("source".equals(key)) {
                source = jsonParser.getIntValue();
            }
        }

        HistoryData historyData = new HistoryData(source, date, channel, theme, title, url);
        ProgData.getInstance().historyListJson.add(historyData);
    }
}
