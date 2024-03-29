package de.p2tools.mtplayer.controller.livesearchard;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Parses M3U8 files.
 */
public class M3U8Parser {

    public List<M3U8Dto> parse(String aM3U8Data) {
        List<M3U8Dto> result = new ArrayList<>();

        List<String[]> pairs = getLinePairs(aM3U8Data);
        for (String[] pair : pairs) {
            M3U8Dto dto = new M3U8Dto(prepareUrl(pair[1]));
            parseMeta(pair[0], dto);

            result.add(dto);
        }

        pairs.clear();
        result.sort(
                Comparator.comparing(
                        (M3U8Dto dto) -> dto.getNormalizedMeta(M3U8Constants.M3U8_RESOLUTION).orElse("")));

        return result;
    }

    private static void parseMeta(String aLine, M3U8Dto aDto) {
        int index = aLine.indexOf(':');
        if (index > 0) {
            String line = aLine.substring(index + 1);

            while ((index = line.indexOf(',')) > 0) {
                // Sicherstellen, dass Index für , nicht innerhalb eines mit " begrenzten Wertes liegt
                int indexQuote = line.indexOf('\"');
                while (indexQuote + 1 < index && indexQuote != -1) {
                    indexQuote = line.indexOf('\"', indexQuote + 1);
                    index = line.indexOf(',', indexQuote + 1);
                }

                if (index > 0) {

                    parseMetaParameter(line.substring(0, index), aDto);

                    line = line.substring(index + 1);
                } else {
                    // if no , found after the last quote
                    break;
                }
            }

            parseMetaParameter(line, aDto);
        }
    }

    private static void parseMetaParameter(String aParameter, M3U8Dto aDto) {
        String[] parameterParts = aParameter.split("=");
        aDto.addMeta(parameterParts[0], parameterParts[1]);
    }

    /**
     * Bereitet URL für MV auf, so dass Downloads über FFMPEG möglich it
     *
     * @param aUrl die URL aus der m3u8-Datei
     * @return die URL für den Download
     */
    private static String prepareUrl(String aUrl) {
        String url = aUrl;

        int indexSuffix = aUrl.lastIndexOf(".m3u8");
        if (indexSuffix > 0) {
            url = aUrl.substring(0, indexSuffix + 5);
        }

        return url;
    }

    /**
     * Ermittelt die Paare aus den zusammengehörenden Meta- und URL-Zeilen
     *
     * @param aM3U8Data M3U8-Inhalt
     * @return liste der Zeilenpaare. Im Array ist Index 0 die Metazeile, Index 1
     * die URL-Zeile
     */
    private static List<String[]> getLinePairs(String aM3U8Data) {
        List<String[]> pairs = new ArrayList<>();

        String currentMeta = null;
        String currentUrl = null;

        String[] lines = StringUtils.split(aM3U8Data, '\n');

        for (String line : lines) {
            if (line.startsWith("#EXT-X-STREAM-INF")) {
                currentMeta = line;
            } else if (line.startsWith("#")) {
                currentMeta = null;
                currentUrl = null;
            } else if (!line.isEmpty()) {
                currentUrl = line;
            }

            if (currentMeta != null && currentUrl != null) {
                pairs.add(new String[]{currentMeta, currentUrl});
                currentMeta = null;
                currentUrl = null;
            }
        }

        return pairs;
    }
}
