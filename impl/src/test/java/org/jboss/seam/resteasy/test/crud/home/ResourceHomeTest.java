package org.jboss.seam.resteasy.test.crud.home;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.Run;
import org.jboss.arquillian.api.RunModeType;
import org.jboss.seam.resteasy.crud.ResourceHome;
import org.jboss.seam.resteasy.crud.temporary.EntityHome;
import org.jboss.seam.resteasy.test.SeamResteasyClientTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.Test;

@Run(RunModeType.AS_CLIENT)
public class ResourceHomeTest extends SeamResteasyClientTest
{
   
   @Deployment
   public static WebArchive createDeployment()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
      war.addClasses(MockEntityHome.class, ResourceHome.class, Sheep.class, SheepHome.class, SheepResourceHome.class, EntityHome.class);
      return war;
   }
   
   @Test
   public void testGet() throws Exception
   {
      GetMethod get = new GetMethod("http://localhost:8080/test/sheep/1");
      get.setRequestHeader("Accept", "text/plain");
      assertEquals(client.executeMethod(get), 200);
      assertEquals(get.getResponseBodyAsString(), "alpha");
   }
   
   @Test
   public void testPost() throws Exception
   {
      PostMethod put = new PostMethod("http://localhost:8080/test/sheep");
      put.setRequestEntity(new StringRequestEntity("zulu", "text/plain", "UTF-8"));
      assertEquals(client.executeMethod(put), 201);
      String location = put.getResponseHeader("Location").getValue();
      assertNotNull(location);
      
      GetMethod get = new GetMethod(location);
      get.setRequestHeader("Accept", "text/plain");
      assertEquals(client.executeMethod(get), 200);
      assertEquals(get.getResponseBodyAsString(), "zulu");
   }
   
   @Test
   public void testPut() throws Exception
   {
      PutMethod put = new PutMethod("http://localhost:8080/test/sheep/3");
      put.setRequestEntity(new StringRequestEntity("ch", "text/plain", "UTF-8"));
      assertEquals(client.executeMethod(put), 204);
      
      GetMethod get = new GetMethod("http://localhost:8080/test/sheep/3");
      get.setRequestHeader("Accept", "text/plain");
      assertEquals(client.executeMethod(get), 200);
      assertEquals(get.getResponseBodyAsString(), "ch");
   }
   
   @Test
   public void testDelete() throws Exception
   {
      DeleteMethod delete = new DeleteMethod("http://localhost:8080/test/sheep/4");
      assertEquals(client.executeMethod(delete), 204);
      
      GetMethod get = new GetMethod("http://localhost:8080/test/sheep/4");
      assertEquals(client.executeMethod(get), 404);
   }
   
   @Test
   public void testConditinalGet() throws Exception
   {
      // Do the first request
      GetMethod get1 = new GetMethod("http://localhost:8080/test/sheep/1");
      get1.setRequestHeader("Accept", "text/plain");
      int status1 = client.executeMethod(get1);
      String etag1 = get1.getResponseHeader("ETag").getValue();
      assertEquals(status1, 200);
      assertEquals(get1.getResponseBodyAsString(), "alpha");
      assertEquals(etag1, "92909949");
      
      // Do the second request - conditional GET this time using ETag from the previous response
      GetMethod get2 = new GetMethod("http://localhost:8080/test/sheep/1");
      get2.setRequestHeader("Accept", "text/plain");
      get2.setRequestHeader("If-None-Match", etag1);
      int status2 = client.executeMethod(get2);
      String etag2 = get2.getResponseHeader("ETag").getValue();
      assertEquals(status2, 304);
      assertEquals(etag2, "92909949"); // make sure that also conditional get provides us with ETag
   }
   
   @Test
   public void testConditinalPut() throws Exception
   {
      // Fetch the entity
      GetMethod get1 = new GetMethod("http://localhost:8080/test/sheep/2");
      get1.setRequestHeader("Accept", "text/plain");
      int status1 = client.executeMethod(get1);
      String etag1 = get1.getResponseHeader("ETag").getValue();
      String entity = get1.getResponseBodyAsString();
      assertEquals(status1, 200);
      assertEquals(etag1, "93998249");
      
      // Do an update
      PutMethod put1 = new PutMethod("http://localhost:8080/test/sheep/2");
      put1.setRequestHeader("If-Match", etag1);
      put1.setRequestEntity(new StringRequestEntity("br", "text/plain", "UTF-8"));
      int status2 = client.executeMethod(put1);
      String etag2 = put1.getResponseHeader("ETag").getValue();
      assertEquals(status2, 204);
      assertEquals(etag2, "3183");
      
      // Do another update - this one should fail since etag1 is used and the entity has been modified meanwhile
      PutMethod put2 = new PutMethod("http://localhost:8080/test/sheep/2");
      put2.setRequestHeader("Content-Type", "text/plain");
      put2.setRequestHeader("If-Match", etag1);
      put2.setRequestEntity(new StringRequestEntity(entity + "tender", "text/plain", "UTF-8"));
      int status3 = client.executeMethod(put2);
      assertEquals(status3, 412);
   }   
}
