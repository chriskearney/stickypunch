package com.comandante.stickypunch.apnspush;

import com.google.common.base.Optional;
import com.notnoop.apns.ApnsService;
import com.comandante.stickypunch.api.model.WebPushStore;
import com.comandante.stickypunch.api.model.WebPushUser;
import com.comandante.stickypunch.api.model.WebPushUserBuilder;
import org.apache.commons.configuration.BaseConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApnsFeedbackServiceTest {
    static ApnsService apnsService = mock(ApnsService.class);
    static WebPushStore webPushStore = mock(WebPushStore.class);
    static ApnsFeedbackService apnsFeedbackService;

    @BeforeClass
    public static void setUp() {
        apnsFeedbackService = new ApnsFeedbackService(apnsService, new ApnsConfiguration(new BaseConfiguration()), webPushStore);

    }
    @Test
    public void testRunOneIteration() throws Exception {
        Map<String, Date> inActiveDevices = new HashMap<String, Date>();
        inActiveDevices.put("devicetoken1", new Date());
        inActiveDevices.put("devicetoken2", new Date());
        inActiveDevices.put("devicetoken3", new Date());
        inActiveDevices.put("devicetoken4", new Date());

        when(apnsService.getInactiveDevices()).thenReturn(inActiveDevices);

        WebPushUser webPushUser = new WebPushUserBuilder()
                .setUserId("USERID")
                .setActive(true)
                .setDeviceToken("DEVICETOKEN1")
                .setIsActiveTimestamp(System.currentTimeMillis() + 86400000)
                .setWebsitePushId("websitepushId")
                .build();

        when(webPushStore.getWebPushUserMostRecentlyUpdatedByDeviceToken(anyString())).thenReturn(Optional.of(webPushUser));

       // when(webPushStore.updateWebPushUser(webPushUser)).

        apnsFeedbackService.runOneIteration();

    }
}
