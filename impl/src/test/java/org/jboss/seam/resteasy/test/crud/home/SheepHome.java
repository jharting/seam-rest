package org.jboss.seam.resteasy.test.crud.home;

import java.util.HashMap;

public class SheepHome extends MockEntityHome<Sheep, Integer>
{

   private static SheepHome instance;
   private Integer uniqueId = 5;
   
   @SuppressWarnings("serial")
   private SheepHome()
   {
      super(new HashMap<Integer, Sheep>()
      {
         {
            put(1, new Sheep("alpha")); // testGet(), testConditinalGet()
            put(2, new Sheep("bravo")); // testConditinalPut()
            put(3, new Sheep("charlie")); // testPut()
            put(4, new Sheep("delta")); // testDelete()
         }
      });
   }

   public static SheepHome getHomeInstance()
   {
      if (instance == null)
      {
         instance = new SheepHome();
      }
      return instance;
   }

   @Override
   public void persist()
   {
      if (getId() == null)
      {
         setId(uniqueId++);
      }
      super.persist();
   }
   
   
}
