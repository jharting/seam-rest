package org.jboss.seam.resteasy.crud.temporary;

/**
 * EntityHome interface (since we do not have real EntityHome so far) 
 */
public interface EntityHome<ENTITY_TYPE, ENTITY_IDENTIFIER_TYPE>
{
   ENTITY_IDENTIFIER_TYPE getId();
   void setId(ENTITY_IDENTIFIER_TYPE id);
   ENTITY_TYPE getInstance();
   void setInstance(ENTITY_TYPE instance);
   ENTITY_TYPE find();
   void persist();
   void remove();
   void merge(ENTITY_TYPE object);
}
