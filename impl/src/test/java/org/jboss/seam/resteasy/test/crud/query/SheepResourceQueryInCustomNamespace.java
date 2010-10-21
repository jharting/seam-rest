package org.jboss.seam.resteasy.test.crud.query;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.seam.resteasy.crud.ResourceQuery;

@Path("/sheepInCustomNamespace")
@Produces("application/farm+xml")
public class SheepResourceQueryInCustomNamespace extends ResourceQuery<Sheep>
{
   public SheepResourceQueryInCustomNamespace()
   {
      super(MockSheepQuery.getInstance(), false, false, "farm", "http://example.com/farm");
   }
}
