package com.philcollins.stickypunch.http;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.philcollins.pushpackage.PackageZipBuilder;
import com.philcollins.pushpackage.PackageZipConfiguration;
import com.philcollins.pushpackage.PackageZipCreator;
import com.philcollins.pushpackage.jackson.WebsiteObjectMapper;
import com.philcollins.pushpackage.jackson.model.Website;
import com.philcollins.stickypunch.SQLiteWebPushStore;
import com.philcollins.stickypunch.SQLiteWebPushStoreFactory;
import com.philcollins.stickypunch.WebPushStoreConfiguration;
import com.philcollins.stickypunch.api.model.PackageSigner;
import com.philcollins.stickypunch.apnspush.ApnsPushManager;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.eclipse.jetty.server.Server;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@Ignore
public class ModuleFunctionalTest {
    private static final String HOST = "localhost";
    private static final int PORT = TestUtils.findFreePort();
    private static final String BASE_URI = "http://" + HOST + ":" + PORT;

    private static CloseableHttpClient client;

    @BeforeClass
    public static void setUp() throws Exception {
        org.apache.log4j.BasicConfigurator.configure();
        LogManager.getRootLogger().setLevel(Level.INFO);
        HttpConfiguration httpConfiguration = new HttpConfiguration(new MapConfiguration(ImmutableMap.<String, Object>of(
                HttpConfiguration.LISTEN_ADDRESS_PROP, HOST,
                HttpConfiguration.LISTEN_PORT_PROP, Integer.toString(PORT)
        )));
        SQLiteWebPushStore sqLiteWebPushStore = SQLiteWebPushStoreFactory.create(new WebPushStoreConfiguration(new BaseConfiguration()));
        sqLiteWebPushStore.createWebPushUserTable();
        WebPushUserIdAuth webPushUserIdAuth = new WebPushUserIdAuth(sqLiteWebPushStore,httpConfiguration);
        PackageSigner packageSigner = new PackageSigner() {
            @Override
            public byte[] sign(byte[] data) throws Exception {
                return new byte[0];
            }
        };
        HashMap<String, String> hashProps = Maps.newHashMap();
        hashProps.put(PackageZipConfiguration.PUSH_PACKAGE_FILES, "pushpackage.raw");
        PackageZipBuilder packageZipBuilder = new PackageZipBuilder(new PackageZipConfiguration(new MapConfiguration(hashProps)), packageSigner);
        PackageZipCreator packageZipCreator = new PackageZipCreator(packageZipBuilder);
        Server server = HttpServiceFactory.create(sqLiteWebPushStore, webPushUserIdAuth, packageZipCreator, mock(ApnsPushManager.class), httpConfiguration);
        server.start();
        client = HttpClients.createDefault();
    }

    @Test
    public void testLog() throws Exception {
        HttpPost post = new HttpPost(BASE_URI + "/push/v1/log");
        StringEntity entity = new StringEntity(TestUtils.getRandomLogJson(), HTTP.UTF_8);
        entity.setContentType("application/json");
        post.setEntity(entity);
        post.addHeader("Content-Type", "application/json");
        HttpResponse response = client.execute(post);
        assertEquals(200, response.getStatusLine().getStatusCode());
        EntityUtils.consumeQuietly(response.getEntity());
    }

    @Test
    public void testRegisterAndAuthenticate() throws Exception {
        HttpPost post = new HttpPost(BASE_URI + "/push/v1/pushPackages/websiteId");
        HttpResponse httpResponse = client.execute(post);
        String authenticationToken = extractAuthenticationToken(httpResponse);
        String someDeviceToken = "1E7259FE6498002E9F9A4B02C5CC746DD8D78908D9EB6F0399F08E0CEAFE77E4";
        post = new HttpPost(BASE_URI + "/push/v1/devices/" + someDeviceToken + "/registrations/web.com.purplestickypunch.demo");
        post.addHeader("Authorization", "ApplePushNotifications " + authenticationToken);
        HttpResponse response = client.execute(post);
        assertEquals(200, response.getStatusLine().getStatusCode());
        EntityUtils.consumeQuietly(response.getEntity());
    }

    private String extractAuthenticationToken(HttpResponse httpResponse) throws IOException {
        InputStream inputStream = httpResponse.getEntity().getContent();
        OutputStream outputStream = new ByteArrayOutputStream();
        ZipInputStream zis = new ZipInputStream(inputStream);
        ZipEntry ze;
        byte[] buff = new byte[1024];
        while ((ze = zis.getNextEntry()) != null) {
            // get file name
            if (ze.getName().equals("website.json")) {
                int l = 0;
                // write buffer to file
                while ((l = zis.read(buff)) > 0) {
                    outputStream.write(buff, 0, l);
                }
            }
        }
        String json = outputStream.toString();
        outputStream.close();
        zis.close();
        Website website = WebsiteObjectMapper.getInstance().readValue(json, Website.class);
        return website.getAuthenticationToken();
    }
}
