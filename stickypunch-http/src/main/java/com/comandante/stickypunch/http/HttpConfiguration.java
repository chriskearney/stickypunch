package com.comandante.stickypunch.http;

import org.apache.commons.configuration.Configuration;

import java.net.InetSocketAddress;

public class HttpConfiguration {

    public static final String LISTEN_ADDRESS_PROP = "stickypunch.http.listen.addr";
    public static final String LISTEN_ADDRESS_DEFAULT = "0.0.0.0";
    public static final String LISTEN_PORT_PROP = "stickypunch.http.listen.port";
    public static final int LISTEN_PORT_DEFAULT = 8204;
    public final InetSocketAddress listenAddress;

    public static final String NUM_HTTP_WORKERS_PROP = "stickypunch.http.numWorkers";
    public static final int NUM_HTTP_WORKERS_DEFAULT = 10;
    public final int numHttpWorkers;

    public static final String ENABLE_STRICT_AUTH_PROP = "stickypunch.http.strictAuth";
    public static final boolean ENABLE_STRICT_AUTH_DEFAULT = true;
    public final boolean isStrictAuth;

    public static final String HTTP_REQUEST_LOG_NAME = "stickypunch.http.request.log.filename";
    public static final String HTTP_REQUEST_LOG_NAME_DEFAULT = "stickypunch";
    public final String httpLogFileName;

    public HttpConfiguration(Configuration config) {
        listenAddress = new InetSocketAddress(config.getString(LISTEN_ADDRESS_PROP, LISTEN_ADDRESS_DEFAULT),
                config.getInt(LISTEN_PORT_PROP, LISTEN_PORT_DEFAULT));
        numHttpWorkers = config.getInt(NUM_HTTP_WORKERS_PROP, NUM_HTTP_WORKERS_DEFAULT);
        isStrictAuth = config.getBoolean(ENABLE_STRICT_AUTH_PROP, ENABLE_STRICT_AUTH_DEFAULT);
        httpLogFileName = config.getString(HTTP_REQUEST_LOG_NAME, HTTP_REQUEST_LOG_NAME_DEFAULT);
    }

}

