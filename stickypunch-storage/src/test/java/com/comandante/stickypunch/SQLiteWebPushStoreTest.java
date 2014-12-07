package com.comandante.stickypunch;

import com.google.common.base.Optional;
import com.comandante.stickypunch.api.model.WebPushUser;
import com.comandante.stickypunch.api.model.WebPushUserBuilder;
import org.apache.commons.configuration.BaseConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SQLiteWebPushStoreTest {

    private static WebPushStoreConfiguration webPushStoreConfiguration;
    private static SQLiteWebPushStore sqLiteWebPushStore;

    @BeforeClass
    public static void setUp() throws Exception {
        webPushStoreConfiguration = new WebPushStoreConfiguration(new BaseConfiguration());
        sqLiteWebPushStore = SQLiteWebPushStoreFactory.create(webPushStoreConfiguration);
        sqLiteWebPushStore.dropWebPushUserTable();
        sqLiteWebPushStore.createWebPushUserTable();
    }

    @Test
    public void testSaveAndGet() throws Exception {
        String randomUserId = UUID.randomUUID().toString();
        String randomWebsiteId = UUID.randomUUID().toString();
        String randomDeviceToken = UUID.randomUUID().toString();
        Long timestamp = System.currentTimeMillis();
        boolean isActive = true;
        WebPushUser webPushUser = new WebPushUserBuilder()
                .setUserId(randomUserId)
                .setWebsitePushId(randomWebsiteId)
                .setDeviceToken(randomDeviceToken)
                .setActive(isActive)
                .setIsActiveTimestamp(timestamp).build();
        sqLiteWebPushStore.save(webPushUser);
        Optional<WebPushUser> webPushUser1Optional = sqLiteWebPushStore.get(randomUserId);

        assertTrue(webPushUser1Optional.isPresent());
        WebPushUser webPushUser1 = webPushUser1Optional.get();

        assertEquals(webPushUser.getActive(), webPushUser1.getActive());
        assertEquals(webPushUser.getDeviceToken(), webPushUser1.getDeviceToken());
        assertEquals(webPushUser.getUserId(), webPushUser1.getUserId());
        assertEquals(webPushUser.getWebsitePushId(), webPushUser1.getWebsitePushId());
        assertEquals(webPushUser.getActiveTimestamp(), webPushUser1.getActiveTimestamp());
    }

    @Test
    public void testGetByDeviceToken() throws Exception {
        final String deviceToken = "DEVICETOKEN";
        WebPushUser webPushUser = new WebPushUserBuilder()
                .setActive(true)
                .setDeviceToken(deviceToken)
                .setUserId("")
                .setWebsitePushId("")
                .setIsActiveTimestamp(System.currentTimeMillis()).build();

        sqLiteWebPushStore.updateWebPushUser(webPushUser);

        Optional<WebPushUser> webPushUser1 = sqLiteWebPushStore.getLastUpdatedDeviceToken(deviceToken);

        assertTrue(webPushUser1.isPresent());
    }

    @Test
    public void testGetByDeviceTokenAndUserId() throws Exception {
        final String deviceToken = "DEVICETOKEN";
        final String userId = "USERID";
        WebPushUser webPushUser = new WebPushUserBuilder()
                .setActive(true)
                .setDeviceToken(deviceToken)
                .setUserId(userId)
                .setWebsitePushId("")
                .setIsActiveTimestamp(System.currentTimeMillis()).build();

        sqLiteWebPushStore.updateWebPushUser(webPushUser);

        Optional<WebPushUser> webPushUser1 = sqLiteWebPushStore.getWebPushUsersByUserIdAndDeviceToken(userId, deviceToken);

        assertTrue(webPushUser1.isPresent());
    }

    @Test
    public void testGetAllDevices() throws Exception {

        String randomUUID = UUID.randomUUID().toString();

        WebPushUser webPushUser = new WebPushUserBuilder()
                .setActive(true)
                .setDeviceToken(UUID.randomUUID().toString())
                .setUserId(UUID.randomUUID().toString())
                .setWebsitePushId("")
                .setIsActiveTimestamp(System.currentTimeMillis()).build();

        WebPushUser webPushUser1 = new WebPushUserBuilder()
                .setActive(true)
                .setDeviceToken(UUID.randomUUID().toString())
                .setUserId(randomUUID)
                .setWebsitePushId("")
                .setIsActiveTimestamp(System.currentTimeMillis()).build();

        sqLiteWebPushStore.save(webPushUser);
        sqLiteWebPushStore.save(webPushUser1);

        List<WebPushUser> all = sqLiteWebPushStore.getAll();
        boolean match = false;
        for (int i = 0; i < all.size(); i++) {
            WebPushUser pushUser = all.get(i);
            if (pushUser.getUserId().get().equals(randomUUID)) {
                match = true;
            }
        }
        assertTrue(match);
    }
}
