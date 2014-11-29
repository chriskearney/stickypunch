package com.philcollins.stickypunch.http.resource;

import com.sun.jersey.spi.resource.Singleton;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/stickypunch/v1/")
@Singleton
public class StickyPushResource {

    @POST
    @Path("/push/{deviceToken}/send")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRegistration(@Context HttpHeaders headers,
                                       @PathParam("deviceToken") String deviceToken) throws Exception {

        return Response.ok().build();
    }

    private class PushRequest {
        String deviceToken;
    }
}
