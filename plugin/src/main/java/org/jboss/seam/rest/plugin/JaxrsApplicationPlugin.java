/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.seam.rest.plugin;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.seam.forge.parser.JavaParser;
import org.jboss.seam.forge.parser.java.JavaClass;
import org.jboss.seam.forge.parser.java.JavaSource;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.Resource;
import org.jboss.seam.forge.project.constraints.RequiresProject;
import org.jboss.seam.forge.project.facets.JavaSourceFacet;
import org.jboss.seam.forge.project.facets.MetadataFacet;
import org.jboss.seam.forge.project.resources.builtin.DirectoryResource;
import org.jboss.seam.forge.project.resources.builtin.JavaResource;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.plugins.Command;
import org.jboss.seam.forge.shell.plugins.Option;
import org.jboss.seam.forge.shell.plugins.Plugin;

@Named("seam-rest")
// TODO: consider switching to facet and make adding JAX-RS
// application the default action
@RequiresProject
public class JaxrsApplicationPlugin implements Plugin
{
   public static final String JAXRS_APPLICATION = "javax.ws.rs.core.Application";

   @Inject
   private Shell shell;
   @Inject
   private Project project;
   private JavaSourceFacet java;
   private MetadataFacet metadata;

   @Inject
   public void init()
   {
      java = project.getFacet(JavaSourceFacet.class);
      metadata = project.getFacet(MetadataFacet.class);
   }

   @Command(value = "enable-jaxrs")
   public void installJaxrs(@Option(name = "application-name", shortName = "n", required = false) String nameParam, @Option(name = "base-uri", shortName = "u", required = false) String baseUriParam, @Option(name = "package", shortName = "p", required = false) String packageParam) throws FileNotFoundException
   {
      Set<JavaResource> foundApplications = new HashSet<JavaResource>();
      shell.printlnVerbose("Scanning for existing JAX-RS configuration");
      findJaxrsApplication(foundApplications, java.getBasePackageResource());
      if (!foundApplications.isEmpty())
      {
         shell.println("JAX-RS already installed: " + foundApplications.toString() + " Aborting.");
         return;
      }

      String baseUri = baseUriParam;
      if (baseUri == null)
      {
         baseUri = shell.prompt("Enter the mapping for JAX-RS services: [Press ENTER for default /*]", String.class, "/*");
      }

      String name = nameParam;
      if (nameParam == null)
      {
         name = convertToClassName(metadata.getProjectName() + "Application");
      }

      String packageName = packageParam;
      if (packageName == null)
      {
         packageName = java.getBasePackage();
      }

      JavaClass application = createJaxrsApplication(name, packageName, baseUri);
      java.saveJavaClass(application);

      shell.println("Created JAX-RS Application [" + application.getQualifiedName() + ", " + baseUri + "]");

   }

   protected void findJaxrsApplication(Set<JavaResource> foundApplications, DirectoryResource directory)
   {
      for (Resource<?> resource : directory.listResources())
      {
         if (resource instanceof JavaResource)
         {
            JavaResource javaResource = (JavaResource) resource;
            if (isApplication(javaResource))
            {
               foundApplications.add(javaResource);
            }
         }
         if (resource instanceof DirectoryResource)
         {
            findJaxrsApplication(foundApplications, (DirectoryResource) resource);
         }
      }
   }

   protected boolean isApplication(JavaResource resource)
   {
      JavaSource<?> source = null;
      try
      {
         source = resource.getJavaSource();
      }
      catch (FileNotFoundException e)
      {
         return false;
         // TODO: display warning
      }

      // TODO: besides the annotation, we should check that the class is a
      // subclass (even indirect) of the Application
      return source.hasAnnotation(ApplicationPath.class);
   }

   
   protected JavaClass createJaxrsApplication(String name, String packageName, String baseUri)
   {
      JavaClass application = JavaParser.create(JavaClass.class);
      application.setName(name);
      application.setSuperType(Application.class);
      application.addAnnotation(ApplicationPath.class).setStringValue(baseUri);
      application.setPackage(packageName);
      return application;
   }

   /**
    * Convert a String to a possible class name according to Java class name convention.
    * TODO: isolate into a util class 
    */
   protected String convertToClassName(String projectName)
   {
      Matcher matcher = Pattern.compile("^(\\p{Lower})|-(\\p{Lower})").matcher(projectName);
      StringBuffer buffer = new StringBuffer();
      while (matcher.find())
      {
         matcher.appendReplacement(buffer, matcher.group().toUpperCase());
      }
      matcher.appendTail(buffer);
      return buffer.toString().replaceAll("-", "");
   }
}
