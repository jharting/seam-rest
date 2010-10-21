/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.resteasy.test.crud.home;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.resteasy.crud.temporary.EntityHome;


public class MockEntityHome<T, U> implements EntityHome<T, U>
{

   private U id;
   private T instance;
   private Map<U, T> entities = new HashMap<U, T>();
   
   public MockEntityHome()
   {
   }

   public MockEntityHome(Map<U, T> instances)
   {
      this.entities = instances;
   }

   public U getId()
   {
      return id;
   }

   public void setId(U id)
   {
      this.id = id;
   }

   public T getInstance()
   {
      return instance;
   }

   public void setInstance(T instance)
   {
      this.instance = instance;
   }

   public T find()
   {
      instance = entities.get(id);
      if (instance == null)
      {
         throw new IllegalStateException("Entity not found " + id);
      }
      return instance;
   }

   public void persist()
   {
      entities.put(id, instance);
   }

   public void remove()
   {
      entities.remove(id);
   }

   public void merge(T object)
   {
      entities.put(id, object);
   }
}
