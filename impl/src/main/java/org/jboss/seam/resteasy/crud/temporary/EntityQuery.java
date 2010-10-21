package org.jboss.seam.resteasy.crud.temporary;

import java.util.List;

public interface EntityQuery<T>
{
   void setFirstResult(int start);
   void setMaxResults(int max);
   List<T> getResultList();
}
