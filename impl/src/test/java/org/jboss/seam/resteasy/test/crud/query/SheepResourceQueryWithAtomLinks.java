package org.jboss.seam.resteasy.test.crud.query;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.seam.resteasy.crud.ResourceQuery;

@Path("/sheepWithAtomLinks")
@Produces("application/xml")
public class SheepResourceQueryWithAtomLinks extends ResourceQuery<Sheep>
{
   public SheepResourceQueryWithAtomLinks()
   {
      super(MockSheepQuery.getInstance(), true, true);
   }
}
