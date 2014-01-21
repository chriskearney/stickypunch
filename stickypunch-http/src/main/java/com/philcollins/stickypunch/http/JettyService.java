package com.philcollins.stickypunch.http;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.yammer.metrics.jetty.InstrumentedHandler;
import com.yammer.metrics.jetty.InstrumentedQueuedThreadPool;
import com.yammer.metrics.jetty.InstrumentedSelectChannelConnector;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class JettyService {

    private static final String STICKYPUNCH_RESOURCE = "com.philcollins.stickypunch.http.resource";
    private static final String INJECT_JERSEY_PACKAGE = "com.philcollins.stickypunch.http.inject";

    public static Server create(HttpConfiguration configuration) throws Exception {
        Server server = new Server();

        SelectChannelConnector connector = new InstrumentedSelectChannelConnector(configuration.listenAddress.getPort());
        connector.setHost(configuration.listenAddress.getHostName());
        QueuedThreadPool queuedThreadPool = new InstrumentedQueuedThreadPool();
        queuedThreadPool.setMaxThreads(configuration.numHttpWorkers);
        connector.setThreadPool(queuedThreadPool);
        queuedThreadPool.start();

        server.setConnectors(new Connector[]{connector});
        server.setSendServerVersion(true);
        server.setSendDateHeader(true);
        server.setStopAtShutdown(true);
        server.setGracefulShutdown((int) TimeUnit.SECONDS.toMillis(5));

        ServletContextHandler root = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        root.setContextPath("/");

        server.setHandler(new InstrumentedHandler(root));

        // Jersey
        root.addServlet(buildJerseyServlet(), "/*");

        NCSARequestLog requestLog = new NCSARequestLog(configuration.httpLogFileName);
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(true);
        requestLog.setLogTimeZone("GMT");
        requestLog.setLogLatency(true);
        requestLog.setLogDispatch(true);

        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(requestLog);
        root.setHandler(requestLogHandler);

        connector.setStatsOn(true);
        StatisticsHandler statisticsHandler = new StatisticsHandler();
        statisticsHandler.setServer(server);

        requestLogHandler.setHandler(statisticsHandler);

        // Setup JMX
        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        server.getContainer().addEventListener(mbContainer);
        mbContainer.addBean(statisticsHandler);

        return server;
    }

    private static ServletHolder buildJerseyServlet() {
        Set<String> jerseyPackages = Sets.newHashSet();
        jerseyPackages.add(STICKYPUNCH_RESOURCE);
        jerseyPackages.add(INJECT_JERSEY_PACKAGE);
        ImmutableMap.Builder<String, Object> propsBuilder = ImmutableMap.builder();
        propsBuilder.put(PackagesResourceConfig.PROPERTY_PACKAGES, jerseyPackages.toArray(new String[jerseyPackages.size()]));
        PackagesResourceConfig jerseyResourceConfig = new PackagesResourceConfig(propsBuilder.build());
        return new ServletHolder(new ServletContainer(jerseyResourceConfig));
    }
}
