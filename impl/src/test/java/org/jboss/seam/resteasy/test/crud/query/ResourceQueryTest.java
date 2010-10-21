//package org.jboss.seam.resteasy.test.crud.query;
//
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.jboss.arquillian.api.Deployment;
//import org.jboss.seam.resteasy.crud.ResourceQuery;
//import org.jboss.seam.resteasy.crud.ResourceQueryResult;
//import org.jboss.seam.resteasy.crud.ResourceQueryResultWriter;
//import org.jboss.seam.resteasy.crud.temporary.EntityQuery;
//import org.jboss.seam.resteasy.test.SeamResteasyTest;
//import org.jboss.shrinkwrap.api.Archives;
//import org.jboss.shrinkwrap.api.spec.WebArchive;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//import static org.testng.Assert.assertEquals;
//import static org.testng.Assert.assertTrue;
//
//public class ResourceQueryTest extends SeamResteasyTest
//{
//   private HttpClient client = new HttpClient();
//   
//   @Deployment
//   public static WebArchive createDeployment()
//   {
//      WebArchive war = Archives.create("test.war", WebArchive.class);
//      war.addClasses(MockSheepQuery.class, SheepResourceQuery.class, SheepResourceQueryWithLinkHeaders.class,
//            ResourceQueryResult.class, ResourceQuery.class, Sheep.class, EntityQuery.class,
//            SheepResourceQueryWithAtomLinks.class, ResourceQueryResultWriter.class,
//            SheepResourceQueryInCustomNamespace.class);
//      war.addLibraries(libraries);
//      war.setWebXML("org/jboss/seam/resteasy/framework/test/home/web.xml");
//      return war;
//   }
//   
//   @DataProvider
//   public Object[][] testData()
//   {
//      return new Object[][] {
//            new Object[] {
//                  "application/xml", "",
//                  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><result xmlns:ns2=\"http://www.w3.org/2005/Atom\"><sheep><name>alpha</name></sheep><sheep><name>bravo</name></sheep><sheep><name>charlie</name></sheep><sheep><name>delta</name></sheep></result>",
//            },
//            new Object[] {
//                  "application/xml", "?start=1&show=2",
//                  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><result xmlns:ns2=\"http://www.w3.org/2005/Atom\"><sheep><name>bravo</name></sheep><sheep><name>charlie</name></sheep></result>"
//            },
//            new Object[] {
//                  "application/xml", "?start=3&show=3",
//                  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><result xmlns:ns2=\"http://www.w3.org/2005/Atom\"><sheep><name>delta</name></sheep></result>"
//            },
//            new Object[] {
//                  "application/json", "",
//                  "{\"result\":{\"sheep\":[{\"name\":\"alpha\"},{\"name\":\"bravo\"},{\"name\":\"charlie\"},{\"name\":\"delta\"}]}}"
//            },
//            new Object[] {
//                  "application/json", "?start=1&show=2",
//                  "{\"result\":{\"sheep\":[{\"name\":\"bravo\"},{\"name\":\"charlie\"}]}}"
//            },
//            new Object[] {
//                  "application/json", "?start=3&show=3",
//                  "{\"result\":{\"sheep\":{\"name\":\"delta\"}}}"
//            }
//      };
//   }
//   
//   @Test(dataProvider="testData")
//   public void testGet(String mimeType, String params, String expectedResponse) throws Exception
//   {
//      GetMethod get = new GetMethod("http://localhost:8080/test/sheep" + params);
//      get.setRequestHeader("Accept", mimeType);
//      assertEquals(client.executeMethod(get), 200);
//      assertEquals(get.getResponseBodyAsString(), expectedResponse);
//   }
//   
//   @Test
//   public void testLinkHeaders() throws Exception
//   {
//      GetMethod get = new GetMethod("http://localhost:8080/test/sheepWithLinkHeaders?start=2&show=2");
//      get.setRequestHeader("Accept", "application/xml");
//      assertEquals(client.executeMethod(get), 200);
//      String expectedHeader = "Link: <http://localhost:8080/test/sheepWithLinkHeaders?start=4&show=2>; rel=\"next\"" +
//      		", <http://localhost:8080/test/sheepWithLinkHeaders?start=0&show=2>; rel=\"previous\"";
//      assertEquals(expectedHeader, get.getResponseHeader("Link").toString().trim());
//   }
//   
//   @Test
//   public void testAtomLinks() throws Exception
//   {
//      GetMethod get = new GetMethod("http://localhost:8080/test/sheepWithAtomLinks?start=1&show=3");
//      get.setRequestHeader("Accept", "application/xml");
//      assertEquals(client.executeMethod(get), 200);
//      String expectedPreviousLink = "href=\"http://localhost:8080/test/sheepWithAtomLinks?start=0&amp;show=3\"";
//      String expectedNextLink = "href=\"http://localhost:8080/test/sheepWithAtomLinks?start=4&amp;show=3\"";
//      String response = get.getResponseBodyAsString();
//      assertTrue(response.contains(expectedPreviousLink));
//      assertTrue(response.contains(expectedNextLink));
//   }
//   
//   @Test
//   public void testAtomLinksWithCustomNamespace() throws Exception
//   {
//      GetMethod get = new GetMethod("http://localhost:8080/test/sheepInCustomNamespace?start=2&show=1");
//      get.setRequestHeader("Accept", "application/farm+xml");
//      assertEquals(client.executeMethod(get), 200);
//      String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
//      		"<farm:result xmlns:ns2=\"http://www.w3.org/2005/Atom\" xmlns:farm=\"http://example.com/farm\">" +
//      		"<sheep><name>charlie</name></sheep>" +
//      		"</farm:result>";
//      assertEquals(get.getResponseBodyAsString(), expectedResponse);
//   }
//}
