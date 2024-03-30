package de.p2tools.mtplayer.controller.livesearch.tools;

import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

public final class UrlUtils {

    private UrlUtils() {
        super();
    }

    /**
     * adds the domain if missing.
     *
     * @param aUrl    the url to check
     * @param aDomain the domain to add
     * @return the url including the domain
     */
    public static String addDomainIfMissing(final String aUrl, final String aDomain) {
        if (aUrl != null && aUrl.startsWith("/")) {
            return aDomain + aUrl;
        }

        return aUrl;
    }

    /**
     * adds the protocol if missing.
     *
     * @param aUrl      the url to check
     * @param aProtocol the protocol to add
     * @return the url including the protocol
     */
    public static String addProtocolIfMissing(final String aUrl, final String aProtocol) {
        if (aUrl == null || aUrl.isEmpty()) {
            return aUrl;
        }

        if (aUrl.startsWith("//")) {
            return aProtocol + aUrl;
        }
        if (!aUrl.contains("://") && !aUrl.startsWith("/")) {
            return aProtocol + "//" + aUrl;
        }

        return aUrl;
    }

    /**
     * checks whether an url exists. uses head request to check.
     *
     * @param aUrl the url to check
     * @return true if url exists else false.
     */
    public static boolean existsUrl(@NotNull final String aUrl) {
        boolean result = false;

        Request request = new Request.Builder().head().url(aUrl).build();
        try (Response response = MVHttpClient.getInstance().getReducedTimeOutClient().newCall(request).execute()) {
            if (response.isSuccessful()) {
                result = true;
            }
        } catch (IOException ignored) {
        }

        return result;
    }

    /**
     * returns the file name of the url.
     *
     * @param aUrl the url
     * @return the name of the file
     */
    public static Optional<String> getFileName(final String aUrl) {
        if (aUrl != null) {
            int index = aUrl.lastIndexOf('/');
            if (index > 0) {
                final String file = aUrl.substring(index + 1);
                if (file.contains(".")) {
                    return Optional.of(file);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * returns the file type of the url.
     *
     * @param aUrl the url
     * @return the type of the file
     */
    public static Optional<String> getFileType(final String aUrl) {
        if (aUrl != null) {
            int index = aUrl.lastIndexOf('.');
            if (index > 0) {
                int indexQuestionMark = aUrl.indexOf('?', index);
                if (indexQuestionMark < 0) {
                    indexQuestionMark = aUrl.length();
                }
                return Optional.of(aUrl.substring(index + 1, indexQuestionMark));
            }
        }

        return Optional.empty();
    }

    /**
     * returns the protocol of the url.
     *
     * @param aUrl the url
     * @return the protocol of the url (e.g. "http:")
     */
    public static Optional<String> getProtocol(final String aUrl) {
        if (aUrl != null) {
            int index = aUrl.indexOf("//");
            if (index > 0) {
                String protocol = aUrl.substring(0, index);
                return Optional.of(protocol);
            }
        }

        return Optional.empty();
    }

    /**
     * removes the query parameters of the url
     *
     * @param aUrl the url
     * @return the url without query parameters
     */
    public static String removeParameters(String aUrl) {
        if (aUrl == null) {
            return null;
        }

        final int indexParameterStart = aUrl.indexOf('?');
        if (indexParameterStart > 0) {
            return aUrl.substring(0, indexParameterStart);
        }
        return aUrl;
    }
}
