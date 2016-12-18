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

package org.atennert.connector.packets.enocean;

import java.util.Date;

import org.atennert.connector.packets.IPacketConstants;
import org.atennert.connector.packets.Packet;

/**
 * This class implements the EnOcean defined common command packet (type:
 * {@link IPacketConstants#TYPE_COMMON_COMMAND}). Common command packets always
 * have a command code and may include command data and/or optional information.
 * All common command codes are available as constants in this class. They are:
 * <ul>
 * <li>{@link #CO_WR_SLEEP}</li>
 * <li>{@link #CO_WR_RESET}</li>
 * <li>{@link #CO_RD_VERSION}</li>
 * <li>{@link #CO_RD_SYS_LOG}</li>
 * <li>{@link #CO_WR_SYS_LOG}</li>
 * <li>{@link #CO_WR_BIST}</li>
 * <li>{@link #CO_WR_IDBASE}</li>
 * <li>{@link #CO_RD_IDBASE}</li>
 * <li>{@link #CO_WR_REPEATER}</li>
 * <li>{@link #CO_RD_REPEATER}</li>
 * <li>{@link #CO_WR_FILTER_ADD}</li>
 * <li>{@link #CO_WR_FILTER_DEL}</li>
 * <li>{@link #CO_WR_FILTER_DEL_ALL}</li>
 * <li>{@link #CO_WR_FILTER_ENABLE}</li>
 * <li>{@link #CO_RD_FILTER}</li>
 * <li>{@link #CO_WR_WAIT_MATURITY}</li>
 * <li>{@link #CO_WR_SUBTEL}</li>
 * <li>{@link #CO_WR_MEM}</li>
 * <li>{@link #CO_RD_MEM}</li>
 * <li>{@link #CO_RD_MEM_ADDRESS}</li>
 * <li>{@link #CO_RD_SECURITY}</li>
 * <li>{@link #CO_WR_SECURITY}</li>
 * <li>{@link #CO_WR_LEARNMODE}</li>
 * <li>{@link #CO_RD_LEARNMODE}</li>
 * <li>{@link #CO_WR_SECUREDEVICE_ADD}</li>
 * <li>{@link #CO_WR_SECUREDEVICE_DEL}</li>
 * <li>{@link #CO_RD_SECUREDEVICES}</li>
 * </ul>
 * 
 * @author Andreas Tennert
 */
public class CommonCommandPacket extends Packet {
    /** Order to enter in energy saving mode */
    public final static int CO_WR_SLEEP = 1;
    /** Order to reset the device */
    public final static int CO_WR_RESET = 2;
    /** Read the device (SW) version / (HW) version, chip ID etc. */
    public final static int CO_RD_VERSION = 3;
    /** Read system log from device databank */
    public final static int CO_RD_SYS_LOG = 4;
    /** Reset System log from device databank */
    public final static int CO_WR_SYS_LOG = 5;
    /** Perform Flash BIST operation */
    public final static int CO_WR_BIST = 6;
    /** Write ID range base number */
    public final static int CO_WR_IDBASE = 7;
    /** Read ID range base number */
    public final static int CO_RD_IDBASE = 8;
    /** Write Repeater Level off,1,2 */
    public final static int CO_WR_REPEATER = 9;
    /** Read Repeater Level off,1,2 */
    public final static int CO_RD_REPEATER = 10;
    /** Add filter to filter list */
    public final static int CO_WR_FILTER_ADD = 11;
    /** Delete filter from filter list */
    public final static int CO_WR_FILTER_DEL = 12;
    /** Delete all filter */
    public final static int CO_WR_FILTER_DEL_ALL = 13;
    /** Enable/Disable supplied filters */
    public final static int CO_WR_FILTER_ENABLE = 14;
    /** Read supplied filters */
    public final static int CO_RD_FILTER = 15;
    /**
     * Waiting till end of maturity time before received radio telegrams will
     * transmitted
     */
    public final static int CO_WR_WAIT_MATURITY = 16;
    /** Enable/Disable transmitting additional subtelegram info */
    public final static int CO_WR_SUBTEL = 17;
    /** Write x bytes of the Flash, XRAM, RAM0 ... . */
    public final static int CO_WR_MEM = 18;
    /** Read x bytes of the Flash, XRAM, RAM0 ... . */
    public final static int CO_RD_MEM = 19;
    /**
     * Feedback about the used address and length of the config area and the
     * Smart Ack Table
     */
    public final static int CO_RD_MEM_ADDRESS = 20;
    /** Read own security information (level, key) */
    public final static int CO_RD_SECURITY = 21;
    /** Write own security information (level, key) */
    public final static int CO_WR_SECURITY = 22;
    /** Enable/disable learn mode */
    public final static int CO_WR_LEARNMODE = 23;
    /** Read learn mode */
    public final static int CO_RD_LEARNMODE = 24;
    /** Add a secure device */
    public final static int CO_WR_SECUREDEVICE_ADD = 25;
    /** Delete a secure device */
    public final static int CO_WR_SECUREDEVICE_DEL = 26;
    /** Read all secure devices (SLF, ID, channel) */
    public final static int CO_RD_SECUREDEVICES = 27;
    /** Sets the gateway transceiver mode */
    public final static int CO_WR_MODE = 28;

    /**
     * General constructor.
     * 
     * @param data the required data part, contains the command code and the
     *            common command data
     * @param optional optional data
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public CommonCommandPacket( int[] data, int[] optional, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_COMMON_COMMAND, data, optional, timestamp, isValid );
    }

    /**
     * Constructor with separated command code and command data and optional
     * data.
     * 
     * @param commonCommandCode the common command code, those codes are
     *            available as constants, for instance {@link #CO_WR_SLEEP}
     * @param data common command data
     * @param optional optional data
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public CommonCommandPacket( int commonCommandCode, int[] data, int[] optional, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_EVENT, new int[data.length + 1], optional, timestamp, isValid );

        this.data[0] = commonCommandCode;
        System.arraycopy( data, 0, this.data, 1, data.length );
    }

    /**
     * Constructor with separated command code and command data, without
     * optional data.
     * 
     * @param commonCommandCode the common command code, those codes are
     *            available as constants, for instance {@link #CO_WR_SLEEP}
     * @param data common command data
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public CommonCommandPacket( int commonCommandCode, int[] data, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_EVENT, new int[data.length + 1], new int[0], timestamp, isValid );

        this.data[0] = commonCommandCode;
        System.arraycopy( data, 0, this.data, 1, data.length );
    }

    /**
     * Constructor with command code without any further data.
     * 
     * @param commonCommandCode the common command code, those codes are
     *            available as constants, for instance {@link #CO_WR_SLEEP}
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public CommonCommandPacket( int commonCommandCode, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_EVENT, new int[] { commonCommandCode }, new int[0], timestamp, isValid );
    }

    /**
     * @return the common command code, for instance {@link #CO_WR_SLEEP}
     */
    public int getCommonCommandCode() {
        return data[0];
    }
}
