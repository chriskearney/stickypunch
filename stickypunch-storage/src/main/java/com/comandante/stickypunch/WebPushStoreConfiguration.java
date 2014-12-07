package com.comandante.stickypunch;

import org.apache.commons.configuration.Configuration;

public class WebPushStoreConfiguration {

    public static final String SQL_LITE_DATABASE_NAME_PROP = "stickypunch.sqlite.database.filename";
    public static final String SQL_LITE_DATABASE_NAME_DEFAULT = "stickypunch.db";
    public final String sqLiteDatabaseName;

    public static final String SQL_LITE_DATABASE_TIMEOUT_SECONDS_PROP = "stickypunch.sqlite.timeout";
    public static final int SQL_LITE_DATABASE_TIMEOUT_SECONDS_DEFAULT = 30;
    public final int sqLiteTimeout;

    public final String sqLiteDatabaseConnectUrl;

    public WebPushStoreConfiguration(Configuration configuration) {
        sqLiteDatabaseName = configuration.getString(SQL_LITE_DATABASE_NAME_PROP, SQL_LITE_DATABASE_NAME_DEFAULT);
        sqLiteTimeout = configuration.getInt(SQL_LITE_DATABASE_TIMEOUT_SECONDS_PROP, SQL_LITE_DATABASE_TIMEOUT_SECONDS_DEFAULT);
        sqLiteDatabaseConnectUrl = "jdbc:sqlite" + ":" + configuration.getString(SQL_LITE_DATABASE_NAME_PROP, SQL_LITE_DATABASE_NAME_DEFAULT);
    }
}
