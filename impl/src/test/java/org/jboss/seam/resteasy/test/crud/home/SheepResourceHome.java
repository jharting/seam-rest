package org.jboss.seam.resteasy.test.crud.home;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.seam.resteasy.crud.ResourceHome;

@Path("/sheep")
@Produces("text/plain")
@Consumes("text/plain")
public class SheepResourceHome extends ResourceHome<Sheep, Integer>
{

   public SheepResourceHome()
   {
      super(SheepHome.getHomeInstance());
      setConditionalGetEnabled(true);
      setConditionalPutEnabled(true);
   }
}
