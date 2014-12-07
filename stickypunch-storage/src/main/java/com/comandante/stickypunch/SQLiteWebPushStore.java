package com.comandante.stickypunch;

import com.google.common.base.Optional;
import com.comandante.stickypunch.api.model.WebPushStore;
import com.comandante.stickypunch.api.model.WebPushUser;
import com.comandante.stickypunch.api.model.WebPushUserBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteWebPushStore implements WebPushStore {

    private static final Logger log = LogManager.getLogger(SQLiteWebPushStore.class);

    private final WebPushStoreConfiguration webPushStoreConfiguration;
    private final Connection conn;

    public SQLiteWebPushStore(WebPushStoreConfiguration webPushStoreConfiguration) throws SQLException {
        this.webPushStoreConfiguration = webPushStoreConfiguration;
        conn = DriverManager.getConnection(webPushStoreConfiguration.sqLiteDatabaseConnectUrl);
    }

    public void createWebPushUserTable() throws SQLException {
        Statement statement = conn.createStatement();
        final String createSql = "CREATE TABLE if not exists webPushUser (" +
                "userId char(36) NOT NULL, " +
                "websitePushId char(255), " +
                "deviceToken char(64), " +
                "isActive INTEGER NOT NULL," +
                "isActiveTimestamp INTEGER NOT NULL," +
                "PRIMARY KEY (userId), " +
                "UNIQUE (deviceToken))";
        statement.executeUpdate(createSql);
    }

    public void dropWebPushUserTable() throws SQLException {
        Statement statement = conn.createStatement();
        final String dropSql = "DROP TABLE if exists webPushUser";
        statement.executeUpdate(dropSql);
    }

    public Optional<WebPushUser> get(String userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT userId, websitePushId, deviceToken, isActive, isActiveTimestamp FROM webPushUser WHERE userId=?");
        stmt.setString(1, userId);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            return Optional.absent();
        }
        String deviceToken = rs.getString("deviceToken");
        String websitePushId = rs.getString("websitePushId");
        Long isActiveTimestamp = rs.getLong("isActiveTimestamp");
        boolean isActive = rs.getInt("isActive") > 0;
        return Optional.of(new WebPushUserBuilder()
                .setUserId(userId)
                .setDeviceToken(deviceToken)
                .setWebsitePushId(websitePushId)
                .setActive(isActive)
                .setIsActiveTimestamp(isActiveTimestamp)
                .build());
    }

    public Optional<WebPushUser> getDeviceTokenAndUserId(String userId, String deviceToken) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT userId, websitePushId, deviceToken, isActive, isActiveTimestamp FROM webPushUser WHERE userId=? AND deviceToken=?");
        stmt.setString(1, userId);
        stmt.setString(2, deviceToken);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            return Optional.absent();
        }
        String token = rs.getString("deviceToken");
        String websitePushId = rs.getString("websitePushId");
        Long isActiveTimestamp = rs.getLong("isActiveTimestamp");
        boolean isActive = rs.getInt("isActive") > 0;
        return Optional.of(new WebPushUserBuilder()
                .setUserId(userId)
                .setDeviceToken(token)
                .setWebsitePushId(websitePushId)
                .setActive(isActive)
                .setIsActiveTimestamp(isActiveTimestamp)
                .build());
    }

    public Optional<WebPushUser> getLastUpdatedDeviceToken(String deviceToken) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT userId, websitePushId, deviceToken, isActive, isActiveTimestamp FROM webPushUser WHERE deviceToken=? AND isActiveTimestamp=(SELECT MAX(isActiveTimestamp) where deviceToken=?)");
        stmt.setString(1, deviceToken);
        stmt.setString(2, deviceToken);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            return Optional.absent();
        }
        String userId = rs.getString("userId");
        String token = rs.getString("deviceToken");
        String websitePushId = rs.getString("websitePushId");
        Long isActiveTimestamp = rs.getLong("isActiveTimestamp");
        boolean isActive = rs.getInt("isActive") > 0;
        return Optional.of(new WebPushUserBuilder()
                .setUserId(userId)
                .setDeviceToken(token)
                .setWebsitePushId(websitePushId)
                .setActive(isActive)
                .setIsActiveTimestamp(isActiveTimestamp)
                .build());
    }

    public List<WebPushUser> getAll() throws SQLException {
        Statement statement = conn.createStatement();
        final String getSql = "SELECT userId, websitePushId, deviceToken, isActive, isActiveTimestamp FROM webPushUser WHERE deviceToken IS NOT NULL";
        List<WebPushUser> retList = new ArrayList<WebPushUser>();
        ResultSet rs = statement.executeQuery(getSql);
        while (rs.next()) {
            String userId = rs.getString("userId");
            String token = rs.getString("deviceToken");
            String websitePushId = rs.getString("websitePushId");
            Long isActiveTimestamp = rs.getLong("isActiveTimestamp");
            boolean isActive = rs.getInt("isActive") > 0;
            retList.add(new WebPushUserBuilder()
                    .setUserId(userId)
                    .setDeviceToken(token)
                    .setWebsitePushId(websitePushId)
                    .setActive(isActive)
                    .setIsActiveTimestamp(isActiveTimestamp)
                    .build());
        }
        return retList;
    }

    public void save(WebPushUser webPushUser) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "REPLACE INTO webPushUser(userId, websitePushId, deviceToken, isActive, isActiveTimestamp) VALUES(?,?,?,?,?)");

        stmt.setString(1, webPushUser.getUserId().get());
        if (webPushUser.getWebsitePushId().isPresent()) {
            stmt.setString(2, webPushUser.getWebsitePushId().get());
        }
        if (webPushUser.getDeviceToken().isPresent()) {
            stmt.setString(3, webPushUser.getDeviceToken().get());
        }
        if (webPushUser.getActive().isPresent()) {
            stmt.setInt(4, webPushUser.getActive().get() ? 1 : 0);
        }
        if (webPushUser.getActiveTimestamp().isPresent()) {
            stmt.setLong(5, webPushUser.getActiveTimestamp().get());
        }
        stmt.executeUpdate();
    }

    @Override
    public void updateWebPushUser(WebPushUser webPushUser) throws Exception {
        this.save(webPushUser);
    }

    @Override
    public Optional<WebPushUser> getWebPushUserMostRecentlyUpdatedByDeviceToken(String deviceToken) {
        Optional<WebPushUser> webPushUserOptional = Optional.absent();
        try {
            webPushUserOptional = this.getLastUpdatedDeviceToken(deviceToken);
        } catch (SQLException e) {
            log.error("Unable to retrieve webPushUser.", e);
        }
        return webPushUserOptional;
    }

    @Override
    public Optional<WebPushUser> getWebPushUsersByUserId(String userId) {
        Optional<WebPushUser> webPushUserOptional = Optional.absent();
        try {
            webPushUserOptional = this.get(userId);
        } catch (SQLException e) {
            log.error("Unable to retrieve webPushUser.", e);
        }
        return webPushUserOptional;
    }

    @Override
    public Optional<WebPushUser> getWebPushUsersByUserIdAndDeviceToken(String userId, String deviceToken) {
        Optional<WebPushUser> webPushUserOptional = Optional.absent();
        try {
            webPushUserOptional = this.getDeviceTokenAndUserId(userId, deviceToken);
        } catch (SQLException e) {
            log.error("Unable to retrieve webPushUser.", e);
        }
        return webPushUserOptional;
    }

    @Override
    public Optional<List<WebPushUser>> getWebPushUsers() {
        List<WebPushUser> webPushUsers = null;
        try {
            webPushUsers = this.getAll();
        } catch (SQLException e) {
            log.error("Unable to retrieve webPushUsers.", e);
        }
        if (webPushUsers.size() <= 0) {
            return Optional.absent();
        }
        return Optional.of(webPushUsers);
    }
}
