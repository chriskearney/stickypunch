package com.philcollins.stickypunch.http;

import com.philcollins.stickypunch.api.model.PackageCreator;
import com.philcollins.stickypunch.api.model.WebPushStore;
import com.philcollins.stickypunch.api.model.WebPushStoreAuth;
import com.philcollins.stickypunch.apnspush.ApnsPushManager;
import com.philcollins.stickypunch.http.inject.ManifestInjectionProvider;
import com.philcollins.stickypunch.http.model.jackson.HttpObjectMapper;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Server;

public class HttpServiceFactory {

    private HttpServiceFactory() {
    }

    public static Server create(WebPushStore webPushStore,
                                 WebPushStoreAuth webPushStoreAuth,
                                 PackageCreator packageCreator,
                                 ApnsPushManager apnsPushManager,
                                 HttpConfiguration config) throws Exception {

        ObjectMapper objectMapper = HttpObjectMapper.getInstance();

        ManifestInjectionProvider.addToManifest(ObjectMapper.class, objectMapper);
        ManifestInjectionProvider.addToManifest(WebPushStore.class, webPushStore);
        ManifestInjectionProvider.addToManifest(WebPushStoreAuth.class, webPushStoreAuth);
        ManifestInjectionProvider.addToManifest(PackageCreator.class, packageCreator);
        ManifestInjectionProvider.addToManifest(ApnsPushManager.class, apnsPushManager);

        return JettyService.create(config);
    }

}

