package org.jboss.seam.rest.exceptions;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * A request-scoped resource for customizing an REST error response from within a Seam Catch exception handler.
 * 
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 */
@RequestScoped
public class ResponseBuilderProducer {
    private ResponseBuilder responseBuilder;

    @Produces
    @RequestScoped
    @RestResource
    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }

    @Produces
    @RestResource
    public Response buildCatchResponse() {
        return responseBuilder.build();
    }

    @PostConstruct
    public void initialize() {
        responseBuilder = Response.serverError();
    }
}
