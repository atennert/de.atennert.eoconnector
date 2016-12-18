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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.atennert.connector.distribution.PacketDistributor;
import org.atennert.connector.distribution.PacketListenerModel;
import org.atennert.connector.reader.ComConnector;
import org.atennert.connector.reader.PacketDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Graphical front-end for the EnOceanConnector application framework.
 * 
 * @author Andreas Tennert
 */
public class GUI extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = -3660921487845855371L;

    private static final Logger log = LoggerFactory.getLogger( GUI.class );

    private final JPanel pEverything = new JPanel();

    private final JLabel tCOMPorts = new JLabel( "COM Port" );
    private final JLabel tPlugins = new JLabel( "Plugins" );

    private final JButton btnStartStop = new JButton( "Start" );

    private final DefaultComboBoxModel< String > cbModel = new DefaultComboBoxModel< String >();
    private final JComboBox< String > cbCOMPorts = new JComboBox< String >( cbModel );

    private final List< JCheckBox > chkList = new ArrayList< JCheckBox >();

    private final PacketDistributor distributor;
    private final ComConnector connector;
    private final PacketDecoder consumer;
    private final PacketListenerModel model;

    public GUI( PacketListenerModel model, PacketDistributor distributor, ComConnector connector, PacketDecoder consumer ) {
        log.debug( "Setting up GUI." );

        this.distributor = distributor;
        this.connector = connector;
        this.consumer = consumer;
        this.model = model;

        this.setPreferredSize( new Dimension( 300, cbCOMPorts.getFont().getSize() * 15 ) );
        this.setResizable( false );

        this.setTitle( "EnOceanConnector" );
        this.setLocationRelativeTo( null );

        btnStartStop.addActionListener( this );
        this.addWindowListener( this );

        /* set layout */
        GridBagConstraints gbc;

        final GridBagLayout glbEverything = new GridBagLayout();
        pEverything.setLayout( glbEverything );

        /* serial ports */
        gbc = makegbc( 0, 0, 2, 1 );
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        glbEverything.setConstraints( tCOMPorts, gbc );
        pEverything.add( tCOMPorts );

        cbCOMPorts.setPreferredSize( new Dimension( 150, cbCOMPorts.getFont().getSize() + 10 ) );
        gbc = makegbc( 0, 1, 2, 1 );
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        glbEverything.setConstraints( cbCOMPorts, gbc );
        pEverything.add( cbCOMPorts );

        /*
         * plug-in listing
         */
        gbc = makegbc( 0, 2, 2, 1 );
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        glbEverything.setConstraints( tPlugins, gbc );
        pEverything.add( tPlugins );

        final GridBagLayout glbPlugins = new GridBagLayout();
        final JPanel pPlugins = new JPanel();
        pPlugins.setLayout( glbPlugins );

        int i = 0;
        for( final String evalName : model.getListenerNames() ) {
            final JCheckBox chkBox = new JCheckBox( evalName, false );

            gbc = makegbc( 0, 0 + i, 1, 1 );
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            glbPlugins.setConstraints( chkBox, gbc );
            pPlugins.add( chkBox );

            chkList.add( chkBox );
            i++;
        }

        final JScrollPane scrollPane = new JScrollPane( pPlugins );
        scrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
        scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scrollPane.setPreferredSize( new Dimension( 290, cbCOMPorts.getFont().getSize() * 7 ) );
        gbc = makegbc( 0, 3, 2, 2 );
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        glbEverything.setConstraints( scrollPane, gbc );
        pEverything.add( scrollPane );

        /*
         * Button (Start/Stop)
         */
        gbc = makegbc( 1, 5, 1, 1 );
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        glbEverything.setConstraints( btnStartStop, gbc );
        pEverything.add( btnStartStop );

        this.setContentPane( pEverything );

        connector.getPortUpdater().addListener( new PortListener() );

        log.debug( "GUI ready." );
    }

    private GridBagConstraints makegbc( int x, int y, int width, int height ) {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.insets = new Insets( 1, 1, 1, 1 );
        return gbc;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        final String sCmd = e.getActionCommand();

        if( sCmd.equals( btnStartStop.getText() ) ) {
            if( btnStartStop.getText().equals( "Start" ) ) {
                // start data acqusition
                if( !connector.setSerialPort( ( (String) cbModel.getSelectedItem() ) ) ) {
                    return;
                }

                for( final JCheckBox cb : chkList ) {
                    if( cb.isSelected() ) {
                        distributor.addListener( cb.getText(), model.getListener( cb.getText() ),
                                model.getProperties( cb.getText() ) );
                    }
                }

                distributor.activateListeners();
                new Thread( connector ).start();
                new Thread( consumer ).start();
                btnStartStop.setText( "Stop" );
                log.info( "Started receiving of data." );
            }
            else {
                // stop data acquisition
                connector.stopThread();
                consumer.stopThread();
                distributor.closeListeners();
                distributor.clear();
                btnStartStop.setText( "Start" );
                log.info( "Stopped receiving of data." );
            }
        }
    }

    @Override
    public void windowClosing( WindowEvent e ) {
        if( btnStartStop.getText().equals( "Start" ) ) {
            System.exit( 0 );
        }
    }

    @Override
    public void windowActivated( WindowEvent e ) {}

    @Override
    public void windowClosed( WindowEvent e ) {}

    @Override
    public void windowDeactivated( WindowEvent e ) {}

    @Override
    public void windowDeiconified( WindowEvent e ) {}

    @Override
    public void windowIconified( WindowEvent e ) {}

    @Override
    public void windowOpened( WindowEvent e ) {}

    /**
     * Listener for updates on available serial ports.
     */
    private class PortListener implements IEventListener< List< String >> {
        @Override
        public void onEvent( List< String > ports ) {
            final String selected = (String) cbModel.getSelectedItem();
            cbModel.removeAllElements();
            for( final String port : ports ) {
                cbModel.addElement( port );
            }
            cbModel.setSelectedItem( selected );
        }
    }
}
