/*******************************************************************************
 * Copyright (C) 2014 Andreas Tennert. 
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *******************************************************************************/

package org.atennert.connector.pluginloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A loader for plugins. The plugins have to contain a class that will be
 * instantiated as well as a configuration file that contains at least the name
 * of the class file to instantiate. It can also contain an entry for which
 * lists required libraries.
 * 
 * @author Andreas Tennert
 */
public class PluginLoader
{
    /** The plugin name entry. */
    private final String pluginNameEntry;

    /** The library entry. */
    private final String libraryEntry;

    /** plugin instance type filters */
    private final IInstanceFilter[] filter;

    private static final Logger log = LoggerFactory
            .getLogger( PluginLoader.class );

    /**
     * Instantiates a new plugin loader.
     * 
     * @param pluginNameEntry
     *            the plugin name entry
     * @param libraryEntry
     *            the library entry
     * @param filter
     *            array of filters to check the plugin type
     */
    public PluginLoader( String pluginNameEntry, String libraryEntry,
            IInstanceFilter[] filter )
    {
        this.pluginNameEntry = pluginNameEntry;
        this.libraryEntry = libraryEntry;
        this.filter = filter;
    }

    /**
     * Load a plugin from a jar file.
     * 
     * @param file
     *            the jar file that contains the plugin
     * @param pluginConfigFile
     *            the plugin configuration file
     * @return the plugin or null if an error occurred
     */
    public Object loadFromJar( File file, String pluginConfigFile )
    {
        Object plugin;

        URL[] urls;
        URLClassLoader classLoader = null;

        try
        {
            JarFile jarFile = new JarFile( file, true, JarFile.OPEN_READ );

            try
            {
                ZipEntry entry = jarFile.getEntry( pluginConfigFile );

                if( entry != null )
                {
                    Properties properties = new Properties();
                    InputStream is = jarFile.getInputStream( entry );

                    properties.load( is );
                    String pluginName = properties
                            .getProperty( pluginNameEntry );
                    String libraries = properties.getProperty( libraryEntry );

                    if( pluginName == null )
                    {
                        is.close();
                        log.warn( "[PluginLoader.loadFromJar] no plugin name found!" );
                        return null;
                    }

                    try
                    {
                        try
                        {
                            urls = getLibraries( file, libraries );
                            classLoader = new URLClassLoader( urls,
                                    ClassLoader.getSystemClassLoader() );
                        }
                        catch( MalformedURLException e )
                        {
                            log.warn( "[PluginLoader.loadFromJar] couldn't find requested plugins for "
                                    + pluginName );
                        }

                        Class< ? > pluginClass = classLoader
                                .loadClass( pluginName );
                        if( pluginClass == null ) { return null; }

                        plugin = pluginClass.newInstance();

                        classLoader.close();

                        for( IInstanceFilter f : filter )
                        {
                            if( f.isCorrectType( plugin ) ) { return plugin; }
                        }

                        log.warn( "[PluginLoader.loadFromJar] found a plugin but the instance type did not match!" );
                    }
                    catch( Exception ex )
                    {
                        log.warn( "[PluginLoader.loadFromJar] unable to load Plugin because of exception:\n"
                                + ex );
                    }
                    finally
                    {
                        is.close();
                    }
                }

                log.warn( "[PluginLoader.loadFromJar] unable to find config file: "
                        + pluginConfigFile );
            }
            catch( IOException e )
            {
                log.warn( "[PluginLoader.loadFromJar] no access to configuration file." );
            }
            finally
            {
                jarFile.close();
            }
        }
        catch( IOException e )
        {
            log.warn( "[PluginLoader.loadFromJar] no access to jar file." );
        }
        finally
        {
            classLoader = null;
            urls = null;
        }
        return null;
    }

    /**
     * Get the libraries for a plugin.
     * 
     * @param jar
     *            the jar
     * @param libraries
     *            the libraries
     * @return the libraries
     * @throws MalformedURLException
     *             the malformed url exception
     */
    private URL[] getLibraries( File jar, String libraries )
            throws MalformedURLException
    {
        ArrayList< URL > urls = new ArrayList< URL >();

        urls.add( jar.toURI().toURL() );
        if( libraries != null && !libraries.trim().equals( "" ) )
        {
            String[] libs = libraries.split( ";" );
            for( String lib : libs )
            {
                urls.add( new File( jar.getParent() + "/Libs/" + lib ).toURI()
                        .toURL() );
            }
        }

        return urls.toArray( new URL[urls.size()] );
    }
}
