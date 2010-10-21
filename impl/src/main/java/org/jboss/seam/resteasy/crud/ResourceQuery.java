package org.jboss.seam.resteasy.crud;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.spi.LinkHeader;
import org.jboss.seam.resteasy.crud.temporary.EntityQuery;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;

public class ResourceQuery<ENTITY_TYPE>
{
   @Context
   private UriInfo uriInfo;
   private EntityQuery<ENTITY_TYPE> query;
   private boolean atomLinksEnabled = false;
   private boolean linkHeadersEnabled = false;
   private Type entityType;
   private ResourceQueryResult<ENTITY_TYPE> result;
   private UriBuilder uriBuilder = null;

   public ResourceQuery(EntityQuery<ENTITY_TYPE> query)
   {
      this.query = query;
      this.result = new ResourceQueryResult<ENTITY_TYPE>();
   }

   public ResourceQuery(EntityQuery<ENTITY_TYPE> query, boolean linkHeadersEnabled)
   {
      this(query);
      this.linkHeadersEnabled = linkHeadersEnabled;
   }

   public ResourceQuery(EntityQuery<ENTITY_TYPE> query, boolean linkHeadersEnabled, boolean atomLinksEnabled)
   {
      this(query, linkHeadersEnabled);
      this.atomLinksEnabled = atomLinksEnabled;
   }

   public ResourceQuery(EntityQuery<ENTITY_TYPE> query, boolean linkHeadersEnabled, boolean atomLinksEnabled, String namespacePrefix, String namespaceUri)
   {
      this(query, linkHeadersEnabled, atomLinksEnabled);
      result.setNamespacePrefix(namespacePrefix);
      result.setNamespaceUri(namespaceUri);
   }

   @GET
   public Response getResourceList(@QueryParam("start") @DefaultValue("0") int start, @QueryParam("show") @DefaultValue("25") int show)
   {
      if (start < 0 || show < 0)
      {
         return Response.status(BAD_REQUEST).build();
      }

      executeQuery(start, show, result);

      // TODO cache ResponseTypes (in a static map?)
      Type type = new ResponseType(ResourceQueryResult.class, getEntityType());

      ResponseBuilder response;
      response = Response.status(OK).entity(new GenericEntity<Object>(result, type));

      // Atom links and Link headers
      if (linkHeadersEnabled || atomLinksEnabled)
      {
         URI next = null;
         URI previous = null;

         if (uriBuilder == null)
         {
            uriBuilder = uriInfo.getAbsolutePathBuilder().queryParam("start", "{start}").queryParam("show", "{stop}");
         }

         // TODO some more sophisticated logic based ResourceQuery.nextExists or
         // something
         next = uriBuilder.build(start + show, show);
         previous = uriBuilder.build(Math.max(start - show, 0), show);

         if (atomLinksEnabled)
         {
//            result.setNext(next);
//            result.setPrevious(previous);
         }
         if (linkHeadersEnabled)
         {
            setLinkHeaders(response, next, previous);
         }
      }
      return response.build();
   }

   protected Type getEntityType()
   {
      if (entityType == null)
      {
         Type superclass = this.getClass().getGenericSuperclass();
         if (superclass instanceof ParameterizedType)
         {
            ParameterizedType parameterizedSuperclass = (ParameterizedType) superclass;
            return parameterizedSuperclass.getActualTypeArguments()[0];
         }
         else
         {
            throw new RuntimeException("Unable to determine entity type.");
         }
      }
      return entityType;
   }

   private void setLinkHeaders(ResponseBuilder response, URI next, URI previous)
   {
      LinkHeader linkHeader = new LinkHeader();
      Link previousLink = new Link();
      Link nextLink = new Link();
      previousLink.setRelationship("previous");
      previousLink.setHref(previous.toASCIIString());
      nextLink.setRelationship("next");
      nextLink.setHref(next.toASCIIString());
      linkHeader.addLink(nextLink).addLink(previousLink);
      response.header("Link", linkHeader);
   }

   protected void executeQuery(int start, int show, ResourceQueryResult<ENTITY_TYPE> result)
   {
      query.setFirstResult(start);
      query.setMaxResults(show);
      result.setEntities(query.getResultList());
   }

   public class ResponseType implements ParameterizedType
   {

      private Type rawType;
      private Type[] typeParameter;

      public ResponseType(Type rawType, Type typeParameter)
      {
         this.rawType = rawType;
         this.typeParameter = new Type[] { typeParameter };
      }

      public Type[] getActualTypeArguments()
      {
         return typeParameter;
      }

      public Type getOwnerType()
      {
         return null;
      }

      public Type getRawType()
      {
         return rawType;
      }
   }
}
