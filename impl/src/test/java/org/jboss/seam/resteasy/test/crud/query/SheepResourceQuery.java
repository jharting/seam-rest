package org.jboss.seam.resteasy.test.crud.query;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.seam.resteasy.crud.ResourceQuery;

@Path("/sheep")
@Produces( { "application/xml", "application/json" })
public class SheepResourceQuery extends ResourceQuery<Sheep>
{

   public SheepResourceQuery()
   {
      super(MockSheepQuery.getInstance());
   }
}
