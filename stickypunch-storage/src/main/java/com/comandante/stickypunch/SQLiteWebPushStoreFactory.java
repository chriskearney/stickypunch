package com.comandante.stickypunch;

import java.sql.SQLException;

public class SQLiteWebPushStoreFactory {

    public static SQLiteWebPushStore create(WebPushStoreConfiguration webPushStoreConfiguration) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        SQLiteWebPushStore sqLiteWebPushStore = new SQLiteWebPushStore(webPushStoreConfiguration);
        sqLiteWebPushStore.createWebPushUserTable();
        return sqLiteWebPushStore;
    }
}
