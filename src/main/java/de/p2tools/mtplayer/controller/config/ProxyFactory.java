package de.p2tools.mtplayer.controller.config;

import de.p2tools.p2lib.P2LibInit;
import de.p2tools.p2lib.tools.log.PLog;

import java.io.IOException;
import java.net.*;

public class ProxyFactory {
    private ProxyFactory() {
    }

    public static void initProxy() {
        if (ProgConfig.SYSTEM_USE_PROXY.getValue()) {
            try {
                String host = getHost();
                String port = getPort();

                if (!host.isEmpty() && !port.isEmpty()) {
                    // sonst machts keinen Sinn
                    setupAuthenticator();

                    // http://www.oracle.com/technetwork/java/javase/8u111-relnotes-3124969.html
                    // In some environments, certain authentication schemes may be
                    // undesirable when proxying HTTPS. Accordingly, the Basic
                    // authentication scheme has been deactivated, by default, in
                    // the Oracle Java Runtime .. Now, proxies requiring Basic
                    // authentication when setting up a tunnel for HTTPS will no
                    // longer succeed by default. If required, this authentication
                    // scheme can be reactivated by removing Basic from the
                    // jdk.http.auth.tunneling.disabledSchemes networking property,
                    // or by setting a system property of the same name to "" ( empty )
                    // on the command line.
                    System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "false");
                    System.setProperty("jdk.http.auth.proxying.disabledSchemes", "false");
                }
            } catch (Exception ex) {
                PLog.errorLog(205468971, "Fehler beim einrichten des Proxy");
            }
            P2LibInit.initProxy(ProgConfig.SYSTEM_USE_PROXY.getValue(),
                    getHost(), getPort(), getUser(), getPwd());
        }

    }

    private static void setupAuthenticator() {
        String user = getUser();
        String pwd = getPwd();
        if (!user.isEmpty() && !pwd.isEmpty()) {
            Authenticator.setDefault(new ProxyAuthenticator(user, pwd));
        }
    }

    static class ProxyAuthenticator extends Authenticator {
        private final String userName;
        private final String password;

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password.toCharArray());
        }

        public ProxyAuthenticator(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }
    }

    public static String getHost() {
        String host;
        if (System.getProperty("http.proxyHost") != null && !System.getProperty("http.proxyHost").isEmpty()) {
            host = System.getProperty("http.proxyHost");
        } else {
            host = ProgConfig.SYSTEM_PROXY_HOST.getValueSafe();
        }
        return host;
    }

    public static String getPort() {
        String port;
        if (System.getProperty("http.proxyPort") != null && !System.getProperty("http.proxyPort").isEmpty()) {
            port = System.getProperty("http.proxyPort");
        } else {
            port = ProgConfig.SYSTEM_PROXY_PORT.getValueSafe();
        }
        return port;
    }

    public static String getUser() {
        String usr;
        if (System.getProperty("http.proxyUser") != null && !System.getProperty("http.proxyUser").isEmpty()) {
            usr = System.getProperty("http.proxyUser");
        } else {
            usr = ProgConfig.SYSTEM_PROXY_USER.getValueSafe();
        }
        return usr;
    }

    public static String getPwd() {
        String pwd;
        if (System.getProperty("http.proxyPassword") != null && !System.getProperty("http.proxyPassword").isEmpty()) {
            pwd = System.getProperty("http.proxyPassword");
        } else {
            pwd = ProgConfig.SYSTEM_PROXY_PWD.getValueSafe();
        }
        return pwd;
    }

    public static HttpURLConnection getUrlConnection(String url) throws IOException {
        return getUrlConnection(new URL(url));
    }

    public static HttpURLConnection getUrlConnection(URL url) throws IOException {
        HttpURLConnection httpURLConn;

        String host = getHost();
        String port = getPort();

        if (!ProgConfig.SYSTEM_USE_PROXY.getValue() ||
                host.isEmpty() || port.isEmpty()) {
            // dann ohne Proxy
            httpURLConn = (HttpURLConnection) url.openConnection();

        } else {
            try {
                int iPort = Integer.parseInt(port);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, iPort));
                httpURLConn = (HttpURLConnection) url.openConnection(proxy);
            } catch (Exception ex) {
                // dann eben ohne proxy
                PLog.errorLog(701208613, "Kann die Proxy-Verbindung nicht aufbauen:\n" + host + " - " + port);
                httpURLConn = (HttpURLConnection) url.openConnection();
            }
        }

        httpURLConn.setConnectTimeout(1000 * ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getValue());
        httpURLConn.setReadTimeout(1000 * ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getValue());
        return httpURLConn;
    }
}
