package com.philcollins.stickypunch.http.resource;

import com.google.common.base.Optional;
import com.philcollins.stickypunch.api.model.PackageCreator;
import com.philcollins.stickypunch.api.model.PackageZipEntry;
import com.philcollins.stickypunch.api.model.WebPushStore;
import com.philcollins.stickypunch.api.model.WebPushStoreAuth;
import com.philcollins.stickypunch.api.model.WebPushUser;
import com.philcollins.stickypunch.api.model.WebPushUserBuilder;
import com.philcollins.stickypunch.apnspush.ApnsPushManager;
import com.philcollins.stickypunch.http.inject.ManifestInjectionContext;
import com.philcollins.stickypunch.http.model.jackson.WebPushLog;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.resource.Singleton;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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
public class Resource {

    private static final Logger log = LogManager.getLogger(Resource.class);
    private final ObjectMapper mapper;
    private final WebPushStore webPushStore;
    private final WebPushStoreAuth webPushStoreAuth;
    private final PackageCreator packageCreator;
    private final ApnsPushManager apnsPushManager;

    public Resource(@ManifestInjectionContext ObjectMapper objectMapper,
                    @ManifestInjectionContext WebPushStore webPushStore,
                    @ManifestInjectionContext WebPushStoreAuth webPushStoreAuth,
                    @ManifestInjectionContext PackageCreator packageCreator,
                    @ManifestInjectionContext ApnsPushManager apnsPushManager) {
        this.mapper = objectMapper;
        this.webPushStore = webPushStore;
        this.webPushStoreAuth = webPushStoreAuth;
        this.packageCreator = packageCreator;
        this.apnsPushManager = apnsPushManager;
    }

    @GET
    @Path("/devices")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllActiveDevices(@Context HttpContext context) throws Exception {
        Optional<List<WebPushUser>> webPushUsers = webPushStore.getWebPushUsers();
        if (!webPushUsers.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(webPushUsers.get());
        return Response.ok(json).build();
    }
    @GET
    @Path("/send/{deviceToken}/{msg}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendSimplePush(@Context HttpHeaders headers,
                                   @PathParam("deviceToken") String deviceToken,
                                   @PathParam("msg") String msg) throws Exception {
        if (apnsPushManager.sendPush(deviceToken, "Sticky Punch Push!", msg)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
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
