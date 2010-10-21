package org.jboss.seam.resteasy.crud;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

//import org.jboss.resteasy.plugins.providers.atom.Link;

@XmlRootElement
public class ResourceQueryResult<ENTITY_TYPE>
{
   private List<ENTITY_TYPE> entities;
//   private Link next;
//   private Link previous;
   private String namespacePrefix;
   private String namespaceUri;

   public ResourceQueryResult()
   {
   }

   public ResourceQueryResult(List<ENTITY_TYPE> entities)
   {
      this.entities = entities;
//      this.next = new Link("next", "");
//      this.previous = new Link("previous", "");
   }

   @XmlAnyElement
   public List<ENTITY_TYPE> getEntities()
   {
      return entities;
   }

   public void setEntities(List<ENTITY_TYPE> entities)
   {
      this.entities = entities;
   }

//   @XmlElement(name = "link")
//   public Link getNext()
//   {
//      return next;
//   }

//   public void setNext(Link next)
//   {
//      this.next = next;
//   }
   
//   public void setNext(URI uri)
//   {
//      this.next = new Link("next", uri);
//   }

//   @XmlElement(name = "link")
//   public Link getPrevious()
//   {
//      return previous;
//   }

//   public void setPrevious(Link previous)
//   {
//      this.previous = previous;
//   }
   
//   public void setPrevious(URI uri)
//   {
//      this.previous = new Link("previous", uri);
//   }

   @XmlTransient
   public String getNamespacePrefix()
   {
      return namespacePrefix;
   }

   public void setNamespacePrefix(String namespacePrefix)
   {
      this.namespacePrefix = namespacePrefix;
   }

   @XmlTransient
   public String getNamespaceUri()
   {
      return namespaceUri;
   }

   public void setNamespaceUri(String namespaceUri)
   {
      this.namespaceUri = namespaceUri;
   }
}
