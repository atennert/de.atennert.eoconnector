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

package org.atennert.connector.packets;

/**
 * This contains type constants of EnOcean messages.
 * The constant TYPE_ANY is supposed to only be used for
 * registering new IPacketListener instances and is not
 * an actual EnOcean data packet type.
 * 
 * @author Andreas Tennert
 */
public interface IPacketConstants {
    
    /** Any telegram */
    public static final int TYPE_ANY = -1;
    
    /** Radio telegram */
    public static final int TYPE_RADIO = 0x01;
    /** Response to any packet */
    public static final int TYPE_RESPONSE = 0x02;
    /** Radio subtelegram */
    public static final int TYPE_RADIO_SUB_TEL = 0x03;
    /** Event message */
    public static final int TYPE_EVENT = 0x04;
    /** Common command */
    public static final int TYPE_COMMON_COMMAND = 0x05;
    /** Smart Ack command */
    public static final int TYPE_SMART_ACK_COMMAND = 0x06;
    /** Remote management command */
    public static final int TYPE_REMOTE_MAN_COMMAND = 0x07;
    /** Advanced radio protocol raw data */
    public static final int TYPE_RADIO_ADVANCED = 0x0A;
}
