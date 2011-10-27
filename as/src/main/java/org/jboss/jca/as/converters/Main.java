/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.jca.as.converters;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Converter main class
 * 
 * @author Jeff Zhang
 */
public class Main
{
   /** New line character */
   private static final String NEW_LINE = System.getProperty("line.separator");

   /** Exit codes */
   private static final int SUCCESS = 0;
   private static final int ERROR = 1;
   private static final int OTHER = 2;
   
   /**
    * Main
    * @param args args 
    * @throws Exception exception
    */
   public static void main(String[] args)
   {
      FileInputStream in = null;
      FileOutputStream out = null;

      try
      {
         if (args.length < 3)
         {
            usage();
            System.exit(OTHER);
         }

         String option = args[0];
         String oldDsFilename = args[1];
         String newFilename = args[2];
         
         if (!(option.equals("-ra") || option.equals("-ds")) ||
             !oldDsFilename.endsWith("-ds.xml") || !newFilename.endsWith(".xml"))
         {
            usage();
            System.exit(OTHER);
         }

         String outxml = "";

         in = new FileInputStream(oldDsFilename);

         if (option.equals("-ds"))
         {
            LegacyDsParser parser = new LegacyDsParser();
            DataSources ds = parser.parse(in);
            outxml = ds.toString();
         }
         else if (option.equals("-ra"))
         {
            LegacyCfParser parser = new LegacyCfParser();
            ConnectionFactories cfs = parser.parse(in);
            outxml = cfs.toString();
         }
         
         out = new FileOutputStream(newFilename);
         out.write(outxml.getBytes(Charset.forName("UTF-8")));
         out.flush();

         System.out.println(NEW_LINE);
         System.out.println("Done.");
         System.exit(SUCCESS);
      }
      catch (Throwable t)
      {
         System.err.println("Error: " + t.getMessage());
         t.printStackTrace(System.err);
         System.exit(ERROR);
      }
      finally
      {
         if (in != null)
         {
            try
            {
               in.close();
            }
            catch (IOException ioe)
            {
               // Ignore
            }
         }

         if (out != null)
         {
            try
            {
               out.close();
            }
            catch (IOException ioe)
            {
               // Ignore
            }
         }
      }
   }

   /**
    * Tool usage
    */
   private static void usage()
   {
      System.out.println("Usage: ./converter.sh -{ds|ra} old-ds.xml mydeployment-{ds|ra}.xml");
   }
}