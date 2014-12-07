package com.comandante.stickypunch;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.comandante.pushpackage.PackageZipBuilder;
import com.comandante.pushpackage.PackageZipConfiguration;
import com.comandante.pushpackage.PackageZipPool;
import com.comandante.pushpackage.PackageZipPoolPopulate;
import com.comandante.pushpackage.PackageZipSigner;
import com.comandante.stickypunch.apnspush.ApnsConfiguration;
import com.comandante.stickypunch.apnspush.ApnsFeedbackService;
import com.comandante.stickypunch.apnspush.ApnsPushManager;
import com.comandante.stickypunch.http.HttpConfiguration;
import com.comandante.stickypunch.http.HttpServiceFactory;
import com.comandante.stickypunch.http.WebPushUserIdAuth;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.eclipse.jetty.server.Server;

import java.io.File;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws Exception {
        Configuration config = buildConfiguration(args);
        //
        // Database
        //
        WebPushStoreConfiguration pushStoreConfiguration = new WebPushStoreConfiguration(config);
        SQLiteWebPushStore sqLiteWebPushStore = SQLiteWebPushStoreFactory.create(pushStoreConfiguration);
        //
        // PackageZip
        //
        PackageZipConfiguration packageZipConfiguration = new PackageZipConfiguration(config);
        PackageZipSigner packageSigner = new PackageZipSigner(packageZipConfiguration);
        PackageZipBuilder packageZipBuilder = new PackageZipBuilder(packageZipConfiguration, packageSigner);
        PackageZipPool packageZipPool = new PackageZipPool(packageZipConfiguration);
        PackageZipPoolPopulate packageZipPoolPopulate = new PackageZipPoolPopulate(packageZipPool, packageZipBuilder);
        //
        // Apns Push Service
        //
        ApnsConfiguration apnsConfiguration = new ApnsConfiguration(config);
        ApnsService apnsService = APNS.newService().withCert(apnsConfiguration.apnsCertLocation, apnsConfiguration.apnsCertPassword).withProductionDestination().build();
        ApnsPushManager apnsPushManager = new ApnsPushManager(apnsService, apnsConfiguration, sqLiteWebPushStore);
        ApnsFeedbackService apnsFeedbackService = new ApnsFeedbackService(apnsService, apnsConfiguration, sqLiteWebPushStore);
        //
        // Jetty
        //
        HttpConfiguration httpConfiguration = new HttpConfiguration(config);
        WebPushUserIdAuth webPushUserIdAuth = new WebPushUserIdAuth(sqLiteWebPushStore, httpConfiguration);
        Server jettyServer = HttpServiceFactory.create(sqLiteWebPushStore, webPushUserIdAuth, packageZipPool, apnsPushManager, httpConfiguration);
        // START
        apnsService.start();
        apnsFeedbackService.startAndWait();
        Executors.newSingleThreadExecutor().submit(packageZipPoolPopulate);
        jettyServer.start();
    }

    private static Configuration buildConfiguration(String[] args) throws ConfigurationException {
        CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
        File propFile = new File(args[0]);
        if (propFile.exists()) {
            compositeConfiguration.addConfiguration(new PropertiesConfiguration(propFile));
        }
        compositeConfiguration.addConfiguration(new SystemConfiguration());

        return compositeConfiguration;
    }
}
