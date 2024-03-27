package de.p2tools.mtplayer.controller.livesearch;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class MVHttpClient {
    private static final MVHttpClient ourInstance = new MVHttpClient();
    private final OkHttpClient httpClient;
    private final OkHttpClient copyClient;

    private MVHttpClient() {
        this.httpClient = (new OkHttpClient.Builder()).connectTimeout(30L, TimeUnit.SECONDS).writeTimeout(30L, TimeUnit.SECONDS).readTimeout(30L, TimeUnit.SECONDS).connectionPool(new ConnectionPool(100, 1L, TimeUnit.SECONDS)).build();
        this.httpClient.dispatcher().setMaxRequests(100);
        this.copyClient = this.httpClient.newBuilder().connectTimeout(5L, TimeUnit.SECONDS).readTimeout(5L, TimeUnit.SECONDS).writeTimeout(2L, TimeUnit.SECONDS).build();
    }

    public static MVHttpClient getInstance() {
        return ourInstance;
    }

    public OkHttpClient getHttpClient() {
        return this.httpClient;
    }

    public OkHttpClient getReducedTimeOutClient() {
        return this.copyClient;
    }
}
