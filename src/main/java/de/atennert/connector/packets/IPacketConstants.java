package de.atennert.connector.packets;

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
    int TYPE_ANY = -1;
    
    /** Radio telegram */
    int TYPE_RADIO = 0x01;
    /** Response to any packet */
    int TYPE_RESPONSE = 0x02;
    /** Radio subtelegram */
    int TYPE_RADIO_SUB_TEL = 0x03;
    /** Event message */
    int TYPE_EVENT = 0x04;
    /** Common command */
    int TYPE_COMMON_COMMAND = 0x05;
    /** Smart Ack command */
    int TYPE_SMART_ACK_COMMAND = 0x06;
    /** Remote management command */
    int TYPE_REMOTE_MAN_COMMAND = 0x07;
    /** Advanced radio protocol raw data */
    int TYPE_RADIO_ADVANCED = 0x0A;
}
