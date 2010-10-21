package org.jboss.seam.resteasy.test.crud.query;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.resteasy.crud.temporary.EntityQuery;

public class MockSheepQuery implements EntityQuery<Sheep>
{

   private List<Sheep> sheep;
   private int start = 0;
   private int max = 2;

   private static MockSheepQuery instance;

   private MockSheepQuery()
   {
      sheep = new ArrayList<Sheep>();
      sheep.add(new Sheep("alpha"));
      sheep.add(new Sheep("bravo"));
      sheep.add(new Sheep("charlie"));
      sheep.add(new Sheep("delta"));
   }

   public List<Sheep> getResultList()
   {
      return sheep.subList(start, Math.min(start + max, sheep.size()));
   }

   public void setFirstResult(int start)
   {
      this.start = start;
   }

   public void setMaxResults(int max)
   {
      this.max = max;
   }

   public static MockSheepQuery getInstance()
   {
      if (instance == null)
      {
         instance = new MockSheepQuery();
      }
      return instance;
   }

}
