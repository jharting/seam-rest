package org.jboss.seam.resteasy.crud;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.plugins.providers.jaxb.AbstractJAXBProvider;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextFinder;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBMarshalException;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.Types;

@Provider
@Produces("*/*")
public class ResourceQueryResultWriter implements MessageBodyWriter<ResourceQueryResult<?>>
{

   @Context
   protected Providers providers;
   
   public long getSize(ResourceQueryResult<?> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return ResourceQueryResult.class.isAssignableFrom(type);
   }

   @SuppressWarnings("unchecked")
   public void writeTo(ResourceQueryResult<?> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      JAXBContextFinder finder = getFinder(mediaType);
      Class<?> resultType = Types.getCollectionBaseType(type, genericType);
      
      try
      {
         JAXBContext ctx = finder.findCacheContext(mediaType, annotations, ResourceQueryResult.class, resultType);

         String elementName = "result";
         String namespacePrefix;
         String namespaceURI;

         // you can place @Wrapped on overridden getResourceList method in ResourceQuery subclass
         Wrapped wrapped = FindAnnotation.findAnnotation(annotations, Wrapped.class);
         if (wrapped != null)
         {
            elementName = wrapped.element();
            namespaceURI = wrapped.namespace();
            namespacePrefix = wrapped.prefix();
         }
         // or you can specify this information via ResourceQuery constructor
         // this is the preferred way
         else
         {
            namespacePrefix = t.getNamespacePrefix();
            namespaceURI = t.getNamespaceUri();
         }

         JAXBElement<ResourceQueryResult> resultList = new JAXBElement<ResourceQueryResult>(new QName(namespaceURI, elementName, namespacePrefix), ResourceQueryResult.class, t);
         Marshaller marshaller = ctx.createMarshaller();
         AbstractJAXBProvider.decorateMarshaller(resultType, annotations, mediaType, marshaller);
         marshaller.marshal(resultList, entityStream);
      }
      catch (JAXBException e)
      {
         throw new JAXBMarshalException(e);
      }
   }
   
   protected JAXBContextFinder getFinder(MediaType type)
   {
      ContextResolver<JAXBContextFinder> resolver = providers.getContextResolver(JAXBContextFinder.class, type);
      if (resolver == null)
      {
         throw new JAXBMarshalException("Unable to find JAXBContext for media type: " + type);
      }
      return resolver.getContext(null);
   }
}
