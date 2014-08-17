/*******************************************************************************
 * Copyright (C) 2014 Andreas Tennert. This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *******************************************************************************/

package org.atennert.connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;

import org.atennert.connector.distribution.IPacketListener;
import org.atennert.connector.distribution.PacketListenerObservable;
import org.atennert.connector.distribution.PacketDistributor;
import org.atennert.connector.distribution.PacketListenerModel;
import org.atennert.connector.packets.IPacketFactory;
import org.atennert.connector.packets.Packet;
import org.atennert.connector.packets.PacketFactory;
import org.atennert.connector.pluginloader.IInstanceFilter;
import org.atennert.connector.pluginloader.PluginLoader;
import org.atennert.connector.reader.ComConnector;
import org.atennert.connector.reader.PacketDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the start of the framework application.
 * 
 * @author Andreas Tennert
 */
public class Main
{
    private static final Logger log = LoggerFactory.getLogger( Main.class );

    public static void main( String[] args )
    {
        log.info( "Loading application." );

        // load properties
        log.debug( "Loading configuration files." );
        Map< String, Properties > propertyMap = loadPluginProperties();

        // unread program configuration (unused right now)
        propertyMap.remove( "EnOceanConnector" );

        BlockingQueue< Integer > messageByteQueue = new LinkedBlockingQueue< Integer >();
        BlockingQueue< Packet > sendPacketQueue = new LinkedBlockingQueue< Packet >();

        PacketListenerObservable modelListener = new PacketListenerObservable();
        PacketDistributor distributor = new PacketDistributor( modelListener,
                sendPacketQueue );
        PacketListenerModel model = new PacketListenerModel( modelListener );

        PacketFactory factory = new PacketFactory();

        // load plug-ins
        log.debug( "Loading plugins." );
        PluginLoader pLoader = new PluginLoader( "plugin_name",
                "plugin_libraries", new IInstanceFilter[]
                { new IInstanceFilter()
                {
                    @Override
                    public boolean isCorrectType( Object o )
                    {
                        return o instanceof IPacketListener;
                    }
                }, new IInstanceFilter()
                {
                    @Override
                    public boolean isCorrectType( Object o )
                    {
                        return o instanceof IPacketFactory;
                    }
                } } );
        loadPlugins( propertyMap, model, factory, pLoader );

        ComConnector comConnector = new ComConnector( messageByteQueue,
                sendPacketQueue );
        PacketDecoder consumer = new PacketDecoder( messageByteQueue,
                distributor, factory );

        // start GUI
        GUI gMain = new GUI( model, distributor, comConnector, consumer );
        gMain.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        gMain.pack();

        gMain.setVisible( true );

        log.info( "Application started." );
    }

    /**
     * Load all available plugins. Factory are given to the main factory and
     * packet readers are added to the listener model together with their
     * properties.
     * 
     * @param propertyMap the map of plugin properties
     * @param model the packet listener model to which all packet listeners are
     *            given
     * @param factory the main factory which gets all sub factories
     * @param pLoader loader class for plugins.
     */
    private static void loadPlugins( Map< String, Properties > propertyMap,
            PacketListenerModel model, PacketFactory factory,
            PluginLoader pLoader )
    {
        File[] entries = new File( "Plugin" ).listFiles( new FilenameFilter()
        {
            @Override
            public boolean accept( File dir, String name )
            {
                return new File( dir, name ).isFile()
                        && name.toLowerCase().endsWith( ".jar" );
            }
        } );

        if( entries != null )
        {
            String fileName;
            Object extension;

            for( File jarFile : entries )
            {
                fileName = jarFile.getName();
                final String pluginName = fileName.substring( 0,
                        fileName.lastIndexOf( ".jar" ) );
                extension = pLoader.loadFromJar( jarFile, pluginName + ".conf" );

                if( extension != null )
                {
                    if( extension instanceof IPacketListener )
                    {
                        model.addListener( pluginName,
                                (IPacketListener) extension,
                                propertyMap.get( pluginName ) );
                    }
                    else if( extension instanceof IPacketFactory )
                    {
                        factory.addFactory( (IPacketFactory) extension );
                    }

                    log.debug( "Loaded " + fileName + "." );
                }
                else
                {
                    log.warn( "Unable to load " + fileName + "!" );
                }
            }
        }
    }

    /**
     * Load the plugin properties that can be found in the configuration
     * directory.
     * 
     * @return a map of plugin names (keys) with their corresponding properties
     *         (values)
     */
    private static Map< String, Properties > loadPluginProperties()
    {
        File[] configs = new File( "Config" ).listFiles( new FilenameFilter()
        {
            public boolean accept( File dir, String name )
            {
                return new File( dir, name ).isFile()
                        && name.toLowerCase().endsWith( ".conf" );
            }
        } );

        Map< String, Properties > propertyMap = new HashMap< String, Properties >();
        if( configs != null )
        {
            for( File confFile : configs )
            {
                String fileName = confFile.getName();
                Properties properties = new Properties();
                try
                {
                    InputStream is = new FileInputStream( confFile );
                    properties.load( is );
                    propertyMap.put(
                            fileName.substring( 0,
                                    fileName.lastIndexOf( ".conf" ) ),
                            properties );
                    is.close();
                }
                catch( FileNotFoundException e )
                {
                    log.error( "[Main.loadPluginProperties] Excpected file not found: "
                            + e.getMessage() );
                }
                catch( IOException e )
                {
                    log.error( "[Main.loadPluginProperties] unable to read file: "
                            + e.getMessage() );
                }
            }
        }
        return propertyMap;
    }
}
