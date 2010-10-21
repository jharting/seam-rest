package org.jboss.seam.resteasy.crud;

import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.seam.resteasy.crud.temporary.EntityHome;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class ResourceHome<ENTITY_TYPE, ENTITY_IDENTIFIER_TYPE>
{
   private EntityHome<ENTITY_TYPE, ENTITY_IDENTIFIER_TYPE> entityHome;
   private boolean conditionalGetEnabled = false;
   private boolean conditionalPutEnabled = false;
   
   public ResourceHome(EntityHome<ENTITY_TYPE, ENTITY_IDENTIFIER_TYPE> entityHome)
   {
      this.entityHome = entityHome;
   }

   /**
    * TODO describe that this should be overriden to alter etag creation
    * @param entity
    * @return
    */
   protected EntityTag getEtag(ENTITY_TYPE entity)
   {
      return new EntityTag(String.valueOf(entity.hashCode()));
   }
   
   protected CacheControl getCacheControl()
   {
      return new CacheControl();
   }

   @GET
   @Path("/{id}")
   public Response get(@PathParam("id") ENTITY_IDENTIFIER_TYPE identifier, @Context Request request)
   {

      entityHome.setId(identifier);
      ENTITY_TYPE entity;
      
      
      try
      {
         entity = entityHome.find();
      }
      catch (IllegalStateException e) // TODO separate method protected entityNotFound(id, e)
      {
         return Response.status(NOT_FOUND).build();
      }
      

      if (conditionalGetEnabled)
      {
         EntityTag etag = getEtag(entity);
         ResponseBuilder suggestedResponse = request.evaluatePreconditions(etag); // will create 304 response if a request passes revalidation
         if (suggestedResponse != null)
         {
            return suggestedResponse.cacheControl(getCacheControl()).build(); // what if someone wants to add custom CacheControl? (max-age?)
         }
         
         return Response.ok(entity).cacheControl(getCacheControl()).tag(etag).build();
      }
      
      return Response.ok(entity).cacheControl(getCacheControl()).build();
   }
   
   @POST
   public Response post(ENTITY_TYPE incommingEntity, @Context UriInfo uriInfo) {
      
      entityHome.setInstance(incommingEntity);
      entityHome.persist();
      ENTITY_IDENTIFIER_TYPE id = entityHome.getId();
      
      if (id == null)
      {
         throw new NullPointerException();
      }
      
      // TODO it would be great to cache this somehow - it cannot be a static field on this level, it might be on subclass though
      URI uri = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get").build(id);
      
      return Response.created(uri).build();
   }
   
   @PUT
   @Path("/{id}")
   public Response put(@PathParam("id") ENTITY_IDENTIFIER_TYPE identifier, ENTITY_TYPE incommingEntity, @Context Request request)
   {
      if (conditionalPutEnabled)
      {
         entityHome.setId(identifier);
         ENTITY_TYPE entity = entityHome.find();
         EntityTag etag = getEtag(entity);
         
         ResponseBuilder suggestedResponse = request.evaluatePreconditions(etag);
         if (suggestedResponse != null)
         {
            return suggestedResponse.tag(etag).build();
         }
      }
      
      entityHome.setId(identifier);
      entityHome.merge(incommingEntity);
      
      if (conditionalPutEnabled) // TODO this is also conditionalget so maybe merge these two things?
      {
         EntityTag etag = getEtag(incommingEntity);
         return Response.noContent().tag(etag).build();
      }
      
      return Response.noContent().build();
   }
   
   @DELETE
   @Path("/{id}")
   public void delete(@PathParam("id") ENTITY_IDENTIFIER_TYPE id)
   {
      // TODO support conditional DELETE? (change void to Response in that case)
      entityHome.setId(id);
      entityHome.find();
      entityHome.remove();
   }

   public boolean isConditionalGetEnabled()
   {
      return conditionalGetEnabled;
   }

   public void setConditionalGetEnabled(boolean conditionalGetEnabled)
   {
      this.conditionalGetEnabled = conditionalGetEnabled;
   }

   public boolean isConditionalPutEnabled()
   {
      return conditionalPutEnabled;
   }

   public void setConditionalPutEnabled(boolean conditionalPutEnabled)
   {
      this.conditionalPutEnabled = conditionalPutEnabled;
   }
}
