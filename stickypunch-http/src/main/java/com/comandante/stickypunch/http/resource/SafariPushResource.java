package com.comandante.stickypunch.http.resource;

import com.google.common.base.Optional;
import com.comandante.stickypunch.api.model.PackageCreator;
import com.comandante.stickypunch.api.model.PackageZipEntry;
import com.comandante.stickypunch.api.model.WebPushStore;
import com.comandante.stickypunch.api.model.WebPushStoreAuth;
import com.comandante.stickypunch.api.model.WebPushUser;
import com.comandante.stickypunch.api.model.WebPushUserBuilder;
import com.comandante.stickypunch.http.inject.ManifestInjectionContext;
import com.comandante.stickypunch.http.model.jackson.WebPushLog;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.resource.Singleton;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Path("/push/v1/")
@Singleton
public class SafariPushResource {

    private static final Logger log = LogManager.getLogger(SafariPushResource.class);
    private final ObjectMapper mapper;
    private final WebPushStore webPushStore;
    private final WebPushStoreAuth webPushStoreAuth;
    private final PackageCreator packageCreator;

    public SafariPushResource(@ManifestInjectionContext ObjectMapper objectMapper,
                              @ManifestInjectionContext WebPushStore webPushStore,
                              @ManifestInjectionContext WebPushStoreAuth webPushStoreAuth,
                              @ManifestInjectionContext PackageCreator packageCreator) {
        this.mapper = objectMapper;
        this.webPushStore = webPushStore;
        this.webPushStoreAuth = webPushStoreAuth;
        this.packageCreator = packageCreator;
    }

    @POST
    @Path("/pushPackages/{websitePushId}")
    @Produces("application/zip")
    public InputStream getPushPackage(@Context HttpContext context) throws Exception {
        PackageZipEntry packageZipEntry = packageCreator.getPackage();
        webPushStore.updateWebPushUser(new WebPushUserBuilder()
                .setUserId(packageZipEntry.getId())
                .setActive(false)
                .setIsActiveTimestamp(System.currentTimeMillis())
                .build());
        return packageZipEntry.getInputStream();
    }

    @POST
    @Path("/log")
    @Produces(MediaType.APPLICATION_JSON)
    public Response log(String logJson) throws IOException {
        WebPushLog webPushLog = mapper.readValue(logJson, WebPushLog.class);
        webPushLog.flush();
        return Response.ok().build();
    }

    @POST
    @Path("/devices/{deviceToken}/registrations/{websitePushId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRegistration(@Context HttpHeaders headers,
                                       @PathParam("deviceToken") String deviceToken,
                                       @PathParam("websitePushId") String websitePushId) throws Exception {
        Optional<String> userIdOptional = extractandUserId(headers);
        if (!auth(userIdOptional, Optional.<String>absent())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        updateUserId(userIdOptional, true, websitePushId, deviceToken);
        return Response.ok().build();
    }

    @DELETE
    @Path("/devices/{deviceToken}/registrations/{websitePushId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response disableRegistration(@Context HttpHeaders headers,
                                        @PathParam("deviceToken") String deviceToken,
                                        @PathParam("websitePushId") String websitePushId) throws Exception {
        Optional<String> userIdOptional = extractandUserId(headers);
        if (!auth(userIdOptional, Optional.of(deviceToken))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        updateUserId(userIdOptional, false, websitePushId, deviceToken);
        return Response.ok().build();
    }

    private void updateUserId(Optional<String> userId, boolean isActive, String websitePushId, String deviceToken) throws Exception {
        if (!userId.isPresent()) {
            throw new Exception("Missing AuthenticationToken in Auth header:");
        }
        String userIdStr = userId.get();
        WebPushUser webPushUser = new WebPushUserBuilder()
                .setUserId(userIdStr)
                .setActive(isActive)
                .setDeviceToken(deviceToken)
                .setWebsitePushId(websitePushId)
                .setIsActiveTimestamp(System.currentTimeMillis())
                .build();
        webPushStore.updateWebPushUser(webPushUser);
        log.info(String.format("Received UPDATE isActive=" + isActive + " registration for websitePushId: %s deviceToken: %s", websitePushId, deviceToken));
    }

    private Optional<String> extractandUserId(HttpHeaders headers) {
        Optional<String> userIdOpt = Optional.absent();
        MultivaluedMap<String, String> requestHeaders = headers.getRequestHeaders();
        log.debug("HEADERS:" + requestHeaders.toString());
        List<String> authHeaders = requestHeaders.get("Authorization");
        for (String auth : authHeaders) {
            if (auth.startsWith("ApplePushNotifications ")) {
                userIdOpt = Optional.of(auth.split(" ")[1]);
            }
        }
        return userIdOpt;
    }

    private boolean auth(Optional<String> userIdOptional, Optional<String> deviceToken) {
        if (userIdOptional.isPresent()) {
            if (!webPushStoreAuth.authUserId(userIdOptional.get(), deviceToken)) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

}
