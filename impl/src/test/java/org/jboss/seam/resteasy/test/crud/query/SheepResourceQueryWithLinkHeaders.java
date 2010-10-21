package org.jboss.seam.resteasy.test.crud.query;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.seam.resteasy.crud.ResourceQuery;

@Path("/sheepWithLinkHeaders")
@Produces("application/xml")
public class SheepResourceQueryWithLinkHeaders extends ResourceQuery<Sheep>
{
   public SheepResourceQueryWithLinkHeaders()
   {
      super(MockSheepQuery.getInstance(), true, false);
   }
}
